/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.javadsl;

// #imports

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import akka.kafka.CommitterSettings;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.elasticsearch.ElasticsearchSourceSettings;
import akka.stream.alpakka.elasticsearch.ElasticsearchWriteSettings;
import akka.stream.alpakka.elasticsearch.WriteMessage;
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchFlow;
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchSource;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
// #imports

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private final Helper helper;
    private final String elasticsearchAddress;
    private final String kafkaBootstrapServers;

    private final String topic = "movies-to-elasticsearch";
    private final String groupId = "docs-group";

    // #es-setup
    private final String indexName = "movies";

    // #es-setup

    public Main(Helper helper) {
        this.elasticsearchAddress = helper.elasticsearchAddress;
        this.kafkaBootstrapServers = helper.kafkaBootstrapServers;
        this.helper = helper;
    }

    private ActorSystem actorSystem;
    private Materializer materializer;
    private RestClient elasticsearchClient;

    private Consumer.DrainingControl<Done> readFromKafkaToEleasticsearch() {
        // #kafka-setup
        // configure Kafka consumer (1)
        ConsumerSettings<Integer, String> kafkaConsumerSettings =
                ConsumerSettings.create(actorSystem, new IntegerDeserializer(), new StringDeserializer())
                        .withBootstrapServers(kafkaBootstrapServers)
                        .withGroupId(groupId)
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                        .withStopTimeout(Duration.ofSeconds(5));
        // #kafka-setup

        // #flow
        Consumer.DrainingControl<Done> control =
                Consumer.committableSource(kafkaConsumerSettings, Subscriptions.topics(topic)) // (5)
                        .asSourceWithContext(cm -> cm.committableOffset()) // (6)
                        .map(cm -> cm.record())
                        .map(
                                consumerRecord -> { // (7)
                                    Movie movie = JsonMappers.movieReader.readValue(consumerRecord.value());
                                    return WriteMessage.createUpsertMessage(String.valueOf(movie.id), movie);
                                })
                        .via(
                                ElasticsearchFlow.createWithContext(
                                        indexName,
                                        "_doc",
                                        ElasticsearchWriteSettings.create(),
                                        elasticsearchClient,
                                        JsonMappers.mapper)) // (8)
                        .map(
                                writeResult -> { // (9)
                                    writeResult
                                            .getError()
                                            .ifPresent(
                                                    errorJson -> {
                                                        throw new RuntimeException(
                                                                "Elasticsearch update failed "
                                                                        + writeResult.getErrorReason().orElse(errorJson));
                                                    });
                                    return NotUsed.notUsed();
                                })
                        .asSource() // (10)
                        .map(pair -> pair.second())
                        .toMat(Committer.sink(CommitterSettings.create(actorSystem)), Keep.both()) // (11)
                        .mapMaterializedValue(Consumer::createDrainingControl) // (12)
                        .run(materializer);
        // #flow
        return control;
    }

    private CompletionStage<Terminated> run() throws Exception {
        actorSystem = ActorSystem.create();
        materializer = ActorMaterializer.create(actorSystem);
        // #es-setup
        // Elasticsearch client setup (4)
        elasticsearchClient = RestClient.builder(HttpHost.create(elasticsearchAddress)).build();
        // #es-setup

        List<Movie> movies = Arrays.asList(new Movie(23, "Psycho"), new Movie(423, "Citizen Kane"));
        CompletionStage<Done> writing = helper.writeToKafka(topic, movies, actorSystem, materializer);
        writing.toCompletableFuture().get(10, TimeUnit.SECONDS);

        Consumer.DrainingControl<Done> control = readFromKafkaToEleasticsearch();
        TimeUnit.SECONDS.sleep(5);
        CompletionStage<Done> copyingFinished = control.drainAndShutdown(actorSystem.dispatcher());
        copyingFinished.toCompletableFuture().get(10, TimeUnit.SECONDS);
        CompletionStage<List<Movie>> reading = helper.readFromElasticsearch(elasticsearchClient, indexName, actorSystem, materializer);

        return reading.thenCompose(
                ms -> {
                    ms.forEach(m -> System.out.println("read " + m));
                    try {
                        elasticsearchClient.close();
                    } catch (IOException e) {
                        log.error(e.toString(), e);
                    }
                    actorSystem.terminate();
                    return actorSystem.getWhenTerminated();
                });
    }

    public static void main(String[] args) throws Exception {
        Helper helper = new Helper();
        helper.startContainers();
        Main main = new Main(helper);
        CompletionStage<Terminated> run = main.run();
        run.thenAccept(res -> {
            helper.stopContainers();
        });
    }
}
