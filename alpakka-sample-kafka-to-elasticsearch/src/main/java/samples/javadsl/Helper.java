package samples.javadsl;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ProducerSettings;
import akka.kafka.javadsl.Producer;
import akka.stream.Materializer;
import akka.stream.alpakka.elasticsearch.ElasticsearchSourceSettings;
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchSource;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.util.List;
import java.util.concurrent.CompletionStage;

public class Helper {

    private static final Logger log = LoggerFactory.getLogger(Helper.class);

    private ElasticsearchContainer elasticsearchContainer;
    public String elasticsearchAddress;

    private KafkaContainer kafka;
    public String kafkaBootstrapServers;

    public Helper() {
    }

    public void startContainers() {
        elasticsearchContainer =
                new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:6.4.3");
        elasticsearchContainer.start();
        elasticsearchAddress = elasticsearchContainer.getHttpHostAddress();

        kafka = new KafkaContainer("5.1.2"); // contains Kafka 2.1.x
        kafka.start();
        kafkaBootstrapServers = kafka.getBootstrapServers();
    }

    public void stopContainers() {
        kafka.stop();
        elasticsearchContainer.stop();
    }

    CompletionStage<Done> writeToKafka(String topic, List<Movie> movies, ActorSystem actorSystem) {
        ProducerSettings<Integer, String> kafkaProducerSettings =
                ProducerSettings.create(actorSystem, new IntegerSerializer(), new StringSerializer())
                        .withBootstrapServers(kafkaBootstrapServers);

        CompletionStage<Done> producing =
                Source.from(movies)
                        .map(
                                movie -> {
                                    log.debug("producing {}", movie);
                                    String json = JsonMappers.movieWriter.writeValueAsString(movie);
                                    return new ProducerRecord<>(topic, movie.id, json);
                                })
                        .runWith(Producer.plainSink(kafkaProducerSettings), actorSystem);
        producing.thenAccept(s -> log.info("Producing finished"));
        return producing;
    }

    CompletionStage<List<Movie>> readFromElasticsearch(RestClient elasticsearchClient, String indexName, ActorSystem actorSystem) {
        CompletionStage<List<Movie>> reading =
                ElasticsearchSource.typed(
                        indexName,
                        "_doc",
                        "{\"match_all\": {}}",
                        ElasticsearchSourceSettings.create(),
                        elasticsearchClient,
                        Movie.class)
                        .map(readResult -> readResult.source())
                        .runWith(Sink.seq(), actorSystem);
        reading.thenAccept(
                non -> {
                    log.info("Reading finished");
                });
        return reading;
    }


}
