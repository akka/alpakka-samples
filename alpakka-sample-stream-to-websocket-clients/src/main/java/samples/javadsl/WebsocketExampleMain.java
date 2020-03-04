/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.javadsl;

// #imports

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.japi.Pair;
import akka.kafka.ConsumerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.BroadcastHub;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
// #imports

public class WebsocketExampleMain extends AllDirectives {

    private static final Logger log = LoggerFactory.getLogger(WebsocketExampleMain.class);

    private final Helper helper;
    private final String kafkaBootstrapServers;

    private final String topic = "message-topic";
    private final String groupId = "docs-group";

    private ActorSystem actorSystem;
    private Materializer materializer;

    public WebsocketExampleMain(Helper helper) {
        this.kafkaBootstrapServers = helper.kafkaBootstrapServers;
        this.helper = helper;
    }

    public static void main(String[] args) throws Exception {
        Helper helper = new Helper();
        helper.startContainers();
        WebsocketExampleMain main = new WebsocketExampleMain(helper);
        main.run();
        helper.stopContainers();
    }

    private void run() throws Exception {
        actorSystem = ActorSystem.create("KafkaToWebSocket");
        materializer = ActorMaterializer.create(actorSystem);
        Http http = Http.get(actorSystem);

        Flow<Message, Message, ?> webSocketHandler =
            Flow.fromSinkAndSource(
                Sink.ignore(),
                topicSource()
                    // decouple clients from each other: if a client is too slow and more than 1000 elements to be sent to
                    // to the client queue up here, we fail this client
                    .buffer(1000, OverflowStrategy.fail())
                    .via(addIndexFlow())
                    .map(TextMessage::create));

        final Flow<HttpRequest, HttpResponse, ?> routeFlow = createRoute(webSocketHandler).flow(actorSystem, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("localhost", 8081), materializer);

        binding.toCompletableFuture().get(10, TimeUnit.SECONDS);

        System.out.println("Server online at http://localhost:8081/\nPress RETURN to stop...");
        System.in.read(); // let it run until user presses return
    }

    public Flow<String, String, NotUsed> addIndexFlow() {
        final Pair<Integer, String> seed = Pair.create(0, "start");
        return Flow.of(String.class)
                   .scan(seed, (acc, message) -> {
                       Integer index = acc.first();
                       return Pair.create(index + 1, String.format("index: %s, message: %s", index, message));
                   })
                .filterNot(p -> p == seed)
                .map(Pair::second);
    }

    private Route createRoute(Flow<Message, Message, ?> webSocketHandler) {
        return concat(
                path("events", () -> handleWebSocketMessages(webSocketHandler)),
                path("push", () -> parameter("value", v -> {
                    CompletionStage<Done> written = helper.writeToKafka(topic, v, actorSystem, materializer);
                    return onSuccess(written, done -> complete("Ok"));
                }))
        );
    }

    private Source<String, ?> topicSource() {
        ConsumerSettings<Integer, String> kafkaConsumerSettings =
        ConsumerSettings.create(actorSystem, new IntegerDeserializer(), new StringDeserializer())
                .withBootstrapServers(kafkaBootstrapServers)
                .withGroupId(groupId)
                .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
                .withStopTimeout(Duration.ofSeconds(5));

        return
            Consumer.plainSource(kafkaConsumerSettings, Subscriptions.topics(topic))
                    .map(consumerRecord -> consumerRecord.value())
                    // using a broadcast hub here, ensures that all websocket clients will use the same
                    // consumer
                    .runWith(BroadcastHub.of(String.class), materializer);
    }
}
