/*
 * Copyright (C) 2016-2024 Lightbend Inc. <https://www.lightbend.com>
 */

package samples.scaladsl

import akka.Done
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.alpakka.jms.JmsProducerSettings
import akka.stream.alpakka.jms.scaladsl.JmsProducer
import akka.stream.scaladsl.{Sink, Source}
import javax.jms.ConnectionFactory

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class JmsSampleBase {

  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "JmsSample")
  implicit val executionContext: ExecutionContext = system.executionContext

  def wait(duration: FiniteDuration): Unit = Thread.sleep(duration.toMillis)

  def terminateActorSystem(): Unit = {
    system.terminate()
    Await.result(system.whenTerminated, 1.seconds)
  }

  def enqueue(connectionFactory: ConnectionFactory)(msgs: String*): Unit = {
    val jmsSink: Sink[String, Future[Done]] =
      JmsProducer.textSink(
        JmsProducerSettings(system, connectionFactory).withQueue("test")
      )
    Source(msgs.toList).runWith(jmsSink)
  }
}
