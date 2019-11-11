/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

// #imports
import akka.actor.ActorSystem
import akka.kafka._
import akka.kafka.scaladsl.{Committer, Consumer}
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchFlow
import akka.stream.scaladsl.Keep
import akka.stream.{ActorMaterializer, Materializer}
import akka.{Done, NotUsed}
import org.apache.http.HttpHost
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization._
import org.elasticsearch.client.RestClient
import spray.json._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
// #imports

object Main extends App with Helper {

  import JsonFormats._

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val topic = "movies-to-elasticsearch"
  private val groupId = "docs-group"

  // #es-setup
  // Elasticsearch client setup (4)
  implicit val elasticsearchClient: RestClient =
    RestClient
      .builder(HttpHost.create(elasticsearchAddress))
      .build()

  val indexName = "movies"
  // #es-setup

  // #kafka-setup
  // configure Kafka consumer (1)
  val kafkaConsumerSettings = ConsumerSettings(actorSystem, new IntegerDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrapServers)
    .withGroupId(groupId)
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    .withStopTimeout(0.seconds)
  // #kafka-setup

  private def readFromKafkaWriteToElasticsearch() = {
    // #flow
    val control: Consumer.DrainingControl[Done] = Consumer
      .sourceWithOffsetContext(kafkaConsumerSettings, Subscriptions.topics(topic)) // (5)
      .map { consumerRecord => // (6)
        val movie = consumerRecord.value().parseJson.convertTo[Movie]
        WriteMessage.createUpsertMessage(movie.id.toString, movie)
      }
      .via(ElasticsearchFlow.createWithContext(indexName, "_doc")) // (7)
      .map { writeResult => // (8)
        writeResult.error.foreach { errorJson =>
          throw new RuntimeException(s"Elasticsearch update failed ${writeResult.errorReason.getOrElse(errorJson)}")
        }
        NotUsed
      }
      .toMat(Committer.sinkWithOffsetContext(CommitterSettings(actorSystem)))(Keep.both) // (9)
      .mapMaterializedValue(Consumer.DrainingControl.apply) // (10)
      .run()
    // #flow
    control
  }

  val movies = List(Movie(23, "Psycho"), Movie(423, "Citizen Kane"))
  val writing: Future[Done] = writeToKafka(topic, movies)
  Await.result(writing, 10.seconds)

  val control = readFromKafkaWriteToElasticsearch()
  // Let the read/write stream run a bit
  Thread.sleep(5.seconds.toMillis)
  val copyingFinished = control.drainAndShutdown()
  Await.result(copyingFinished, 10.seconds)

  for {
    read <- readFromElasticsearch(indexName)
  } {
    read.foreach(m => println(s"read $m"))
    stopContainers()
    actorSystem.terminate()
  }
}
