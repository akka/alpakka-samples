package alpakka.sample.triggereddownload;

import akka.Done;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import static akka.actor.typed.javadsl.Adapter.*;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.Uri;
import akka.stream.alpakka.mqtt.MqttConnectionSettings;
import akka.stream.alpakka.mqtt.MqttQoS;
import akka.stream.alpakka.mqtt.MqttSubscriptions;
import akka.stream.alpakka.mqtt.javadsl.MqttSource;
import akka.stream.alpakka.s3.javadsl.S3;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    final ActorSystem<Void> system;
    final Http http;

    public static void main(String[] args) throws Exception {
        Main me = new Main();
        me.run();
    }

    public Main() {
        system = ActorSystem.create(Behaviors.empty(), "MqttHttpToS3");
        http = Http.get(toClassic(system));
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
        final MqttConnectionSettings mqttConnectionSettings =
                MqttConnectionSettings
                        .create(
                                mqttBroker,
                                "upload-control",
                                new MemoryPersistence()
                        );

        @SuppressWarnings("unchecked")
        MqttSubscriptions mqttSubscriptions =
                MqttSubscriptions.create(topic, MqttQoS.atLeastOnce());

        MqttSource
                .atMostOnce(mqttConnectionSettings, mqttSubscriptions, 8)
                .map(m -> m.payload().utf8String())
                .<DownloadCommand>map(downloadCommandReader::readValue)
                .mapAsync(4, info -> {
                            String s3BucketKey = createS3BucketKey(info);
                            return Source.single(info.url)
                                    .map(HttpRequest::GET)
                                    .mapAsync(1, http::singleRequest)
                                    .flatMapConcat(httpResponse -> httpResponse.entity().getDataBytes())
                                    .runWith(S3.multipartUpload(s3Bucket, s3BucketKey, ContentTypes.TEXT_HTML_UTF8), system);
                        }
                )
                .runForeach(res -> System.out.println(res), system)
                .exceptionally(e -> { e.printStackTrace(); return Done.done(); });
    }

}
