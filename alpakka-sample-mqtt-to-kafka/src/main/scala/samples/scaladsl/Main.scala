/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */
package samples.scaladsl

import java.io.StringWriter
import java.time.Instant

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.event.{Logging, LoggingAdapter}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.alpakka.mqtt.{MqttConnectionSettings, MqttMessage, MqttQoS, MqttSubscriptions}
import akka.stream.alpakka.mqtt.scaladsl.{MqttSink, MqttSource}
import akka.stream.scaladsl.{Keep, RestartSource, Sink, Source}
import akka.stream.{KillSwitches, UniqueKillSwitch}
import akka.util.ByteString
import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.{ObjectMapper, ObjectWriter}
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.LoggerFactory
import org.testcontainers.containers.KafkaContainer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.jdk.CollectionConverters._

object Main extends App {
  val kafkaContainer = new KafkaContainer("5.4.1")
  kafkaContainer.start()
  try {
    val me = new Main
    me.run(kafkaContainer.getBootstrapServers)
  } finally kafkaContainer.stop()

  // #json-mechanics
  /**
   * Data elements sent via MQTT broker.
   */
  final case class Measurement(timestamp: Instant, level: Long)

  // #json-mechanics

}

class Main {
  implicit val system = ActorSystem(Behaviors.empty, "MqttToKafka")
  implicit val ec: ExecutionContext = system.executionContext
  val log = LoggerFactory.getLogger(classOf[Main])

  // #json-mechanics
  val jsonFactory = new JsonFactory
  val mapper = new ObjectMapper()
    .registerModule(new JavaTimeModule)
    .registerModule(com.fasterxml.jackson.module.scala.DefaultScalaModule)
  val measurementReader = mapper.readerFor(classOf[Main.Measurement])
  val measurementWriter = mapper.writerFor(classOf[Main.Measurement])

  private def asJsonArray(fieldName: String, list: Seq[AnyRef]) = {
    val sw = new StringWriter
    val generator = jsonFactory.createGenerator(sw)
    generator.writeStartObject()
    generator.writeFieldName(fieldName)
    measurementWriter.writeValues(generator).init(true).writeAll(list.asJava)
    generator.close()
    sw.toString
  }
  // #json-mechanics

  // #restarting
  /**
   * Wrap a source with restart logic and expose an equivalent materialized value.
   */
  private def wrapWithAsRestartSource[M](source: => Source[M, Future[Done]]): Source[M, Future[Done]] = {
    val fut = Promise[Done]
    RestartSource.withBackoff(100.millis, 3.seconds, randomFactor = 0.2d, maxRestarts = 5) {
      () => source.mapMaterializedValue(mat => fut.completeWith(mat))
    }.mapMaterializedValue(_ => fut.future)
  }
  // #restarting

  private def run(kafkaServer: String): Unit = {
    implicit val logAdapter: LoggingAdapter = Logging.getLogger(system.toClassic, getClass.getName)
    // #flow
    val connectionSettings = MqttConnectionSettings("tcp://localhost:1883", "coffee-client", new MemoryPersistence) // (1)

    val topic = "coffee/level"

    val subscriptions = MqttSubscriptions.create(topic, MqttQoS.atLeastOnce) // (2)

    val restartingMqttSource = wrapWithAsRestartSource( // (3)
      MqttSource.atMostOnce(connectionSettings.withClientId("coffee-control"), subscriptions, 8))

    // Set up Kafka producer sink
    val producerSettings = ProducerSettings(system.toClassic, new StringSerializer, new StringSerializer).withBootstrapServers(kafkaServer)
    val kafkaProducer = Producer.plainSink(producerSettings)
    val kafkaTopic = "measurements"

    val ((subscriptionInitialized, listener), streamCompletion) = restartingMqttSource
      .viaMat(KillSwitches.single)(Keep.both) // (4)
      .map(_.payload.utf8String) // (5)
      .map(measurementReader.readValue) // (6)
      .groupedWithin(50, 5.seconds) // (7)
      .map(list => asJsonArray("measurements", list)) // (8)
      .log("producing to Kafka")
      .map(json => new ProducerRecord[String, String](kafkaTopic, "", json)) // (9)
      .toMat(kafkaProducer)(Keep.both) // (10)
      .run
    // #flow

    // start producing messages to MQTT
    val producer = subscriptionInitialized.map(_ => produceMessages(connectionSettings, topic))
    streamCompletion
      .recover {
        case exception =>
          exception.printStackTrace()
          null
      }
      .foreach(_ => system.terminate)

    // read the messages from the Kafka topic
    val consumerControl = Consumer
      .plainSource(
        ConsumerSettings(system.toClassic, new StringDeserializer, new StringDeserializer).withBootstrapServers(kafkaServer).withGroupId("sample"),
        Subscriptions.topics(kafkaTopic)
      )
      .map(_.value)
      .log("read from Kafka")
      .toMat(Sink.ignore)(Keep.left)
      .run

    log.info("Letting things run for a while")
    Thread.sleep(20 * 1000)
    producer.foreach(_.shutdown)
    consumerControl.shutdown
    listener.shutdown()
  }

  /**
   * Simulate messages from MQTT by writing to topic registered in MQTT broker.
   */
  private def produceMessages(connectionSettings: MqttConnectionSettings, topic: String): UniqueKillSwitch = {
    import Main.Measurement
    val input = Seq(
      Measurement(Instant.now, 40),
      Measurement(Instant.now, 60),
      Measurement(Instant.now, 80),
      Measurement(Instant.now, 100),
      Measurement(Instant.now, 120)
    )

    val sinkSettings = connectionSettings.withClientId("coffee-supervisor")
    val killSwitch = Source
      .cycle(() => input.iterator)
      .throttle(4, 1.second)
      .map(measurementWriter.writeValueAsString)
      .map((s: String) => MqttMessage.create(topic, ByteString.fromString(s)))
      .viaMat(KillSwitches.single)(Keep.right)
      .toMat(MqttSink(sinkSettings, MqttQoS.atLeastOnce))(Keep.left)
      .run
    killSwitch
  }
}
