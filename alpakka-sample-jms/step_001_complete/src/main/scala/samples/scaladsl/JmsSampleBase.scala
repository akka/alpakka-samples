/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.alpakka.jms.JmsProducerSettings
import akka.stream.alpakka.jms.scaladsl.JmsProducer
import akka.stream.scaladsl.{Sink, Source}
import javax.jms.ConnectionFactory

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class JmsSampleBase {

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  def wait(duration: FiniteDuration): Unit = Thread.sleep(duration.toMillis)

  def terminateActorSystem(): Unit =
    Await.result(actorSystem.terminate(), 1.seconds)

  def enqueue(connectionFactory: ConnectionFactory)(msgs: String*): Unit = {
    val jmsSink: Sink[String, Future[Done]] =
      JmsProducer.textSink(
        JmsProducerSettings(actorSystem, connectionFactory).withQueue("test")
      )
    Source(msgs.toList).runWith(jmsSink)
  }
}
