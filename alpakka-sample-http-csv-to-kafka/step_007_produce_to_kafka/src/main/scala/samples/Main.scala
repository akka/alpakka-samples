/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples

import java.util.concurrent.TimeUnit

import akka.{ Done, NotUsed }
import akka.actor._
import akka.stream._
import akka.http.scaladsl._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse, MediaRanges }
import akka.kafka.scaladsl.{ Consumer, Producer }
import akka.kafka.{ ConsumerSettings, ProducerSettings, Subscriptions }
import akka.stream.alpakka.csv.scaladsl.{ CsvParsing, CsvToMap }
import akka.stream.scaladsl.{ Keep, Sink, Source }
import akka.util.ByteString
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ StringDeserializer, StringSerializer }
import org.testcontainers.containers.KafkaContainer
import spray.json.{ DefaultJsonProtocol, JsValue, JsonWriter }

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object Main
  extends App
    with DefaultJsonProtocol {

  implicit val actorSystem = ActorSystem("alpakka-samples")

  import actorSystem.dispatcher

  val httpRequest = HttpRequest(uri = "https://www.nasdaq.com/screening/companies-by-name.aspx?exchange=NASDAQ&render=download")
    .withHeaders(Accept(MediaRanges.`text/*`))

  def extractEntityData(response: HttpResponse): Source[ByteString, _] =
    response match {
      case HttpResponse(OK, _, entity, _) => entity.dataBytes
      case notOkResponse =>
        Source.failed(new RuntimeException(s"illegal response $notOkResponse"))
    }

  def cleanseCsvData(csvData: Map[String, ByteString]): Map[String, String] =
    csvData
      .filterNot { case (key, _) => key.isEmpty }
      .view
      .mapValues(_.utf8String)
      .toMap

  def toJson(map: Map[String, String])(
    implicit jsWriter: JsonWriter[Map[String, String]]): JsValue = jsWriter.write(map)

  val kafkaBroker: KafkaContainer = new KafkaContainer()
  kafkaBroker.start()

  private val bootstrapServers: String = kafkaBroker.getBootstrapServers()

  val kafkaProducerSettings = ProducerSettings(actorSystem, new StringSerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServers)

  val future: Future[Done] =
    Source
      .single(httpRequest) //: HttpRequest
      .mapAsync(1)(Http().singleRequest(_)) //: HttpResponse
      .flatMapConcat(extractEntityData) //: ByteString
      .via(CsvParsing.lineScanner()) //: List[ByteString]
      .via(CsvToMap.toMap()) //: Map[String, ByteString]
      .map(cleanseCsvData) //: Map[String, String]
      .map(toJson) //: JsValue
      .map(_.compactPrint) //: String (JSON formatted)
      .map { elem =>
        new ProducerRecord[String, String]("topic1", elem) //: Kafka ProducerRecord
      }
      .runWith(Producer.plainSink(kafkaProducerSettings))

  val cs: CoordinatedShutdown = CoordinatedShutdown(actorSystem)
  cs.addTask(CoordinatedShutdown.PhaseServiceStop, "shut-down-client-http-pool")( () =>
    Http().shutdownAllConnectionPools().map(_ => Done)
  )

  val kafkaConsumerSettings = ConsumerSettings(actorSystem, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(bootstrapServers)
    .withGroupId("topic1")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val control = Consumer
    .atMostOnceSource(kafkaConsumerSettings, Subscriptions.topics("topic1"))
    .map(_.value)
    .toMat(Sink.foreach(println))(Keep.both)
    .mapMaterializedValue(Consumer.DrainingControl.apply)
    .run()

  for {
    _ <- future
    _ <- control.drainAndShutdown()
  } {
    kafkaBroker.stop()
    cs.run(CoordinatedShutdown.UnknownReason)
  }
}
