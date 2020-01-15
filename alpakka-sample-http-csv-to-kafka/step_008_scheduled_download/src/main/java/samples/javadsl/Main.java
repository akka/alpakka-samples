/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.javadsl;

import akka.Done;
import akka.actor.Cancellable;
import akka.actor.CoordinatedShutdown;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.MediaRanges;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.headers.Accept;
import akka.japi.Pair;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.stream.alpakka.csv.javadsl.CsvParsing;
import akka.stream.alpakka.csv.javadsl.CsvToMap;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testcontainers.containers.KafkaContainer;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static akka.actor.typed.javadsl.Adapter.toClassic;

public class Main {

    public static void main(String[] args) throws Exception {
        Main me = new Main();
        me.run();
    }

    final HttpRequest httpRequest =
            HttpRequest.create(
                    "https://www.nasdaq.com/screening/companies-by-name.aspx?exchange=NASDAQ&render=download")
                    .withHeaders(Collections.singletonList(Accept.create(MediaRanges.ALL_TEXT)));

    private Source<ByteString, ?> extractEntityData(HttpResponse httpResponse) {
        if (httpResponse.status() == StatusCodes.OK) {
            return httpResponse.entity().getDataBytes();
        } else {
            return Source.failed(new RuntimeException("illegal response " + httpResponse));
        }
    }

    private Map<String, String> cleanseCsvData(Map<String, ByteString> map) {
        Map<String, String> out = new HashMap<>(map.size());
        map.forEach(
                (key, value) -> {
                    if (!key.isEmpty()) out.put(key, value.utf8String());
                });
        return out;
    }

    private final JsonFactory jsonFactory = new JsonFactory();

    private String toJson(Map<String, String> map) throws Exception {
        StringWriter sw = new StringWriter();
        JsonGenerator generator = jsonFactory.createGenerator(sw);
        generator.writeStartObject();
        map.forEach(
                (key, value) -> {
                    try {
                        generator.writeStringField(key, value);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        generator.writeEndObject();
        generator.close();
        return sw.toString();
    }

    private void run() throws Exception {
        KafkaContainer kafkaBroker = new KafkaContainer();
        kafkaBroker.start();
        final String bootstrapServers = kafkaBroker.getBootstrapServers();

        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "alpakka-samples");
        Http http = Http.get(toClassic(system));

        ProducerSettings<String, String> kafkaProducerSettings =
                ProducerSettings.create(toClassic(system), new StringSerializer(), new StringSerializer())
                        .withBootstrapServers(bootstrapServers);

        Pair<Cancellable, CompletionStage<Done>> stagePair =
                Source.tick(
                        Duration.ofSeconds(1),
                        Duration.ofSeconds(30),
                        httpRequest) // : HttpRequest
                        .mapAsync(1, http::singleRequest) // : HttpResponse
                        .flatMapConcat(this::extractEntityData) // : ByteString
                        .via(CsvParsing.lineScanner()) // : List<ByteString>
                        .via(CsvToMap.toMap()) // : Map<String, ByteString>
                        .map(this::cleanseCsvData) // : Map<String, String>
                        .map(this::toJson) // : String
                        .map(elem ->
                                new ProducerRecord<String, String>(
                                        "topic1", elem) // : Kafka ProducerRecord
                        )
                        .toMat(Producer.plainSink(kafkaProducerSettings), Keep.both())
                        .run(system);

        CoordinatedShutdown cs = CoordinatedShutdown.get(system);
        cs.addTask(CoordinatedShutdown.PhaseActorSystemTerminate(), "shut-down-client-http-pool", () ->
                http.shutdownAllConnectionPools().thenApply(r -> Done.done())
        );

        ConsumerSettings<String, String> kafkaConsumerSettings =
                ConsumerSettings.create(toClassic(system), new StringDeserializer(), new StringDeserializer())
                        .withBootstrapServers(bootstrapServers)
                        .withGroupId("topic1")
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer.DrainingControl<Done> control =
                Consumer.atMostOnceSource(kafkaConsumerSettings, Subscriptions.topics("topic1"))
                        .map(ConsumerRecord::value)
                        .toMat(Sink.foreach(System.out::println), Keep.both())
                        .mapMaterializedValue(Consumer::createDrainingControl)
                        .run(system);

        Cancellable tick = stagePair.first();
        CompletionStage<Done> streamCompletion = stagePair.second();

        TimeUnit.SECONDS.sleep(59);
        tick.cancel();

        streamCompletion
                .thenApplyAsync(done -> control.drainAndShutdown(system.executionContext()))
                .thenAccept(
                        done -> {
                            kafkaBroker.stop();
                            cs.run(CoordinatedShutdown.unknownReason());
                        });
    }
}
