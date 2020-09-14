package samples.javadsl;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;

import java.util.concurrent.CompletionStage;

public class Helper {

    private static final Logger log = LoggerFactory.getLogger(Helper.class);

    private KafkaContainer kafka;
    public String kafkaBootstrapServers;

    public Helper() {
    }

    public void startContainers() {
        kafka = new KafkaContainer("5.1.2"); // contains Kafka 2.1.x
        kafka.start();
        kafkaBootstrapServers = kafka.getBootstrapServers();
    }

    public void stopContainers() {
        kafka.stop();
    }

    CompletionStage<Done> writeToKafka(String topic, String item, ActorSystem actorSystem) {
        ProducerSettings<Integer, String> kafkaProducerSettings =
                ProducerSettings.create(actorSystem, new IntegerSerializer(), new StringSerializer())
                        .withBootstrapServers(kafkaBootstrapServers);

        CompletionStage<Done> producing =
                Source.single(new ProducerRecord<Integer, String>(topic, item))
                        .runWith(Producer.plainSink(kafkaProducerSettings), actorSystem);
        producing.thenAccept(s -> log.info("Producing finished"));
        return producing;
    }

}
