package alpakka.sample.triggereddownload;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.Uri;
import akka.japi.JavaPartialFunction;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.alpakka.mqtt.streaming.*;
import akka.stream.alpakka.mqtt.streaming.javadsl.ActorMqttClientSession;
import akka.stream.alpakka.mqtt.streaming.javadsl.Mqtt;
import akka.stream.alpakka.mqtt.streaming.javadsl.MqttClientSession;
import akka.stream.alpakka.s3.javadsl.S3;
import akka.stream.javadsl.*;
import akka.util.ByteString;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import scala.Option;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletionStage;

public class Main {

    final ActorSystem system;
    final Materializer materializer;
    final Http http;

    public static void main(String[] args) throws Exception {
        Main me = new Main();
        me.run();
    }

    public Main() {
        system = ActorSystem.create();
        materializer = ActorMaterializer.create(system);
        http = Http.get(system);
    }

    final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    final ObjectReader downloadCommandReader = mapper.readerFor(DownloadCommand.class);

    final String mqttBroker = "tcp://localhost:1883";
    // Remember to set up topic in MQTT server's acl config
    final String topic = "downloads/trigger";
    final String s3Bucket = "alpakka.samples";

    private String createS3BucketKey(DownloadCommand info) {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + Uri.create(info.url).getPathString().replace("/", "-");
    }

    void run() throws Exception {
        MqttSessionSettings settings = MqttSessionSettings.create();
        MqttClientSession mqttClientSession = ActorMqttClientSession.create(settings, materializer, system);

        Flow<ByteString, ByteString, CompletionStage<Tcp.OutgoingConnection>> tcpConnection =
                Tcp.get(system).outgoingConnection("localhost", 1883);

        // Run MQTT via TCP
        Flow<Command<Object>, DecodeErrorOrEvent<Object>, NotUsed> mqttFlow =
                Mqtt.clientSessionFlow(mqttClientSession).join(tcpConnection);

        LoggingAdapter logging = Logging.getLogger(system, this);

        Pair<SourceQueueWithComplete<Command<Object>>, Source<DecodeErrorOrEvent<Object>, NotUsed>> run =
                Source.<Command<Object>>queue(3, OverflowStrategy.fail())
                        .log("Command sending", logging)
                        .via(mqttFlow)
                        .log("Received", logging)
                        .toMat(BroadcastHub.of(DecodeErrorOrEvent.classOf()), Keep.both())
                        .run(materializer);

        SourceQueueWithComplete<Command<Object>> commands = run.first();
        Source<DecodeErrorOrEvent<Object>, NotUsed> subscription = run.second();

        CompletionStage<Done> receiver = subscription
                .log("Subscription received", logging)
                .collect( // we are only interested in `Publish` events
                        new JavaPartialFunction<DecodeErrorOrEvent<Object>, Publish>() {
                            @Override
                            public Publish apply(DecodeErrorOrEvent<Object> x, boolean isCheck) {
                                if (x.getEvent().isPresent() && x.getEvent().get().event() instanceof Publish) {
                                    Publish publish = (Publish) x.getEvent().get().event();
                                    // Acknowledge received Publish
                                    Option<Integer> packetIdOption = publish.packetId().map(i -> i.underlying());
                                    commands.offer(new Command<>(new PubAck(packetIdOption.get())));
                                    return publish;
                                } else throw noMatch();
                            }
                        })
                .map(m -> m.payload().utf8String())
                .log("URL received", logging)
                .<DownloadCommand>map(downloadCommandReader::readValue)
                .mapAsync(4, info -> {
                            String s3BucketKey = createS3BucketKey(info);
                            return Source.single(info.url)
                                    .map(HttpRequest::GET)
                                    .mapAsync(1, http::singleRequest)
                                    .flatMapConcat(httpResponse -> httpResponse.entity().getDataBytes())
                                    .runWith(S3.multipartUpload(s3Bucket, s3BucketKey, ContentTypes.TEXT_HTML_UTF8), materializer)
                                    .runWith(Sink.ignore(), materializer)
                                    .thenApply(done -> info.url);
                        }
                )
                .runWith(Sink.foreach(res -> System.out.println(res)), materializer)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return Done.done();
                });

        // let MQTT connect and subscribe to the topic
        commands.offer(new Command<>(new Connect("upload-control2", ConnectFlags.CleanSession())));
        commands.offer(new Command<>(new Subscribe(topic)));

        Thread.sleep(60000);
        commands.offer(new Command<>(Disconnect$.MODULE$));
        Thread.sleep(5000);
        logging.debug("Shutting down");
        system.terminate();
    }

}
