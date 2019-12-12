/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.javadsl;

// #imports

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import static akka.actor.typed.javadsl.Adapter.*;
import akka.actor.typed.javadsl.Behaviors;
import akka.kafka.CommitterSettings;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Committer;
import akka.kafka.javadsl.Consumer;
import akka.stream.alpakka.elasticsearch.ElasticsearchWriteSettings;
import akka.stream.alpakka.elasticsearch.WriteMessage;
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchFlow;
import akka.stream.javadsl.Keep;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private ActorSystem<Void> actorSystem;
    private RestClient elasticsearchClient;

    private Consumer.DrainingControl<Done> readFromKafkaToEleasticsearch() {
        // #kafka-setup
        // configure Kafka consumer (1)
        ConsumerSettings<Integer, String> kafkaConsumerSettings =
                ConsumerSettings.create(toClassic(actorSystem), new IntegerDeserializer(), new StringDeserializer())
                        .withBootstrapServers(kafkaBootstrapServers)
                        .withGroupId(groupId)
                        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                        .withStopTimeout(Duration.ofSeconds(5));
        // #kafka-setup

        // #flow
        Consumer.DrainingControl<Done> control =
                Consumer.sourceWithOffsetContext(kafkaConsumerSettings, Subscriptions.topics(topic)) // (5)
                        .map(
                                consumerRecord -> { // (6)
                                    Movie movie = JsonMappers.movieReader.readValue(consumerRecord.value());
                                    return WriteMessage.createUpsertMessage(String.valueOf(movie.id), movie);
                                })
                        .via(
                                ElasticsearchFlow.createWithContext(
                                        indexName,
                                        "_doc",
                                        ElasticsearchWriteSettings.create(),
                                        elasticsearchClient,
                                        JsonMappers.mapper)) // (7)
                        .map(
                                writeResult -> { // (8)
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
                        .toMat(Committer.sinkWithOffsetContext(CommitterSettings.create(toClassic(actorSystem))), Keep.both()) // (9)
                        .mapMaterializedValue(Consumer::createDrainingControl) // (10)
                        .run(actorSystem);
        // #flow
        return control;
    }

    private CompletionStage<Done> run() throws Exception {
        actorSystem = ActorSystem.create(Behaviors.empty(), "KafkaToElasticSearch");
        // #es-setup
        // Elasticsearch client setup (4)
        elasticsearchClient = RestClient.builder(HttpHost.create(elasticsearchAddress)).build();
        // #es-setup

        List<Movie> movies = Arrays.asList(new Movie(23, "Psycho"), new Movie(423, "Citizen Kane"));
        CompletionStage<Done> writing = helper.writeToKafka(topic, movies, actorSystem);
        writing.toCompletableFuture().get(10, TimeUnit.SECONDS);

        Consumer.DrainingControl<Done> control = readFromKafkaToEleasticsearch();
        TimeUnit.SECONDS.sleep(5);
        CompletionStage<Done> copyingFinished = control.drainAndShutdown(actorSystem.executionContext());
        copyingFinished.toCompletableFuture().get(10, TimeUnit.SECONDS);
        CompletionStage<List<Movie>> reading = helper.readFromElasticsearch(elasticsearchClient, indexName, actorSystem);

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
        CompletionStage<Done> run = main.run();
        run.thenAccept(res -> {
            helper.stopContainers();
        });
    }
}
