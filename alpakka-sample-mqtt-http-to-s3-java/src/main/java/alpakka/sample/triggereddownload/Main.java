package alpakka.sample.triggereddownload;

import akka.Done;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.Uri;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.mqtt.MqttConnectionSettings;
import akka.stream.alpakka.mqtt.MqttQoS;
import akka.stream.alpakka.mqtt.MqttSourceSettings;
import akka.stream.alpakka.mqtt.javadsl.MqttSource;
import akka.stream.alpakka.s3.S3Settings;
import akka.stream.alpakka.s3.javadsl.S3Client;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

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
        final MqttConnectionSettings mqttConnectionSettings =
                MqttConnectionSettings
                        .create(
                                mqttBroker,
                                "upload-control",
                                new MemoryPersistence()
                        );

        @SuppressWarnings("unchecked")
        MqttSourceSettings mqttSourceSettings =
                MqttSourceSettings.create(mqttConnectionSettings)
                        .withSubscriptions(Pair.create(topic, MqttQoS.atLeastOnce()));

        S3Settings s3Settings = S3Settings.create(system);
        S3Client s3Client = S3Client.create(s3Settings, system, materializer);

        MqttSource
                .atMostOnce(mqttSourceSettings, 8)
                .map(m -> m.payload().utf8String())
                .<DownloadCommand>map(downloadCommandReader::readValue)
                .mapAsync(4, info -> {
                            String s3BucketKey = createS3BucketKey(info);
                            return Source.single(info.url)
                                    .map(HttpRequest::GET)
                                    .mapAsync(1, http::singleRequest)
                                    .flatMapConcat(httpResponse -> httpResponse.entity().getDataBytes())
                                    .runWith(s3Client.multipartUpload(s3Bucket, s3BucketKey, ContentTypes.TEXT_HTML_UTF8), materializer);
                        }
                )
                .runWith(Sink.ignore(), materializer);
    }

}
