package alpakka.sample.triggereddownload;

import akka.Done;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.mqtt.MqttConnectionSettings;
import akka.stream.alpakka.mqtt.MqttMessage;
import akka.stream.alpakka.mqtt.MqttQoS;
import akka.stream.alpakka.mqtt.javadsl.MqttSink;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;

public class PublishDataToMqtt {
    final ActorSystem system;
    final Materializer materializer;
    final Http http;

    public static void main(String[] args) throws Exception {
        PublishDataToMqtt me = new PublishDataToMqtt();
        me.run();
    }

    public PublishDataToMqtt() {
        system = ActorSystem.create();
        materializer = ActorMaterializer.create(system);
        http = Http.get(system);
    }

    final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    final ObjectWriter downloadCommandWriter = mapper.writerFor(DownloadCommand.class);


    void run() throws Exception {


        final MqttConnectionSettings connectionSettings =
                MqttConnectionSettings.create(
                        "tcp://localhost:1883", "test-java-client", new MemoryPersistence());
        Sink<MqttMessage, CompletionStage<Done>> mqttSink =
                MqttSink.create(connectionSettings.withClientId("source-test/sink"), MqttQoS.atLeastOnce());

        DownloadCommand command = new DownloadCommand(Instant.now(), "https://developer.lightbend.com/docs/alpakka/current/s3.html");
        MqttMessage message = MqttMessage.create("downloads/trigger", ByteString.fromString(downloadCommandWriter.writeValueAsString(command)));

        Source.tick(Duration.ofSeconds(5), Duration.ofSeconds(30), message).runWith(mqttSink, materializer);
    }
}
