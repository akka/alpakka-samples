/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

import akka.actor.typed.ActorSystem
import akka.kafka._
import akka.kafka.scaladsl.Producer
import akka.actor.typed.scaladsl.adapter._
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSource
import akka.stream.scaladsl.{Sink, Source}
import akka.Done
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization._
import org.slf4j.LoggerFactory
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.elasticsearch.ElasticsearchContainer
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import JsonFormats._
import akka.stream.Materializer
import samples.scaladsl.Main.{elasticsearchClient, elasticsearchContainer, kafka}

trait Helper {

  final val log = LoggerFactory.getLogger(getClass)

  // Testcontainers: start Elasticsearch in Docker
  val elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:6.4.3")
  elasticsearchContainer.start()
  val elasticsearchAddress = elasticsearchContainer.getHttpHostAddress

  // Testcontainers: start Kafka in Docker
  // [[https://hub.docker.com/r/confluentinc/cp-kafka/tags Available Docker images]]
  // [[https://docs.confluent.io/current/installation/versions-interoperability.html Kafka versions in Confluent Platform]]
  val kafka = new KafkaContainer("5.4.1") // contains Kafka 2.4.x
  kafka.start()
  val kafkaBootstrapServers = kafka.getBootstrapServers

  def writeToKafka(topic: String, movies: immutable.Iterable[Movie])(implicit actorSystem: ActorSystem[_], materializer: Materializer) = {
    val kafkaProducerSettings = ProducerSettings(actorSystem.toClassic, new IntegerSerializer, new StringSerializer)
      .withBootstrapServers(kafkaBootstrapServers)

    val producing: Future[Done] = Source(movies)
      .map { movie =>
        log.debug("producing {}", movie)
        new ProducerRecord(topic, Int.box(movie.id), movie.toJson.compactPrint)
      }
      .runWith(Producer.plainSink(kafkaProducerSettings))
    producing.foreach(_ => log.info("Producing finished"))(actorSystem.executionContext)
    producing
  }

  def readFromElasticsearch(indexName: String)(implicit actorSystem: ActorSystem[_], materializer: Materializer): Future[immutable.Seq[Movie]] = {
    val reading = ElasticsearchSource
      .typed[Movie](indexName, "_doc", """{"match_all": {}}""")
      .map(_.source)
      .runWith(Sink.seq)
    reading.foreach(_ => log.info("Reading finished"))(actorSystem.executionContext)
    reading
  }

  def stopContainers() = {
    kafka.stop()
    elasticsearchClient.close()
    elasticsearchContainer.stop()
  }
}
