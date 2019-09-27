/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

import java.nio.file._

import akka.actor.ActorSystem
import akka.stream.alpakka.elasticsearch.{WriteMessage, WriteResult}
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchFlow
import akka.stream.alpakka.file.DirectoryChange
import akka.stream.alpakka.file.scaladsl.FileTailSource
import akka.stream.alpakka.file.scaladsl.DirectoryChangesSource
import akka.stream.scaladsl.{Flow, Keep, MergeHub, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitches, Materializer, UniqueKillSwitch}
import akka.{Done, NotUsed}
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import samples.scaladsl.Main.{listFiles, testDataPath}
import spray.json._

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object Main extends App with Helper {

  import JsonFormats._

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val streamRuntime = 5.seconds

  // Elasticsearch client setup
  implicit val elasticsearchClient: RestClient =
    RestClient
      .builder(HttpHost.create(elasticsearchAddress))
      .build()

  val indexName = "logs"
  val typeName = "_doc"

  val testDataPath = "./test-data"
  val inputLogsPath = s"$testDataPath/input"

  val fs = FileSystems.getDefault
  val changes = DirectoryChangesSource(fs.getPath(inputLogsPath), pollInterval = 1.second, maxBufferSize = 1000)

  val (control, stream): (UniqueKillSwitch, Future[Done]) = changes
    .statefulMapConcat { () =>
      val sink = indexInElasticsearch()
      val tailedLogs = mutable.Map[Path, UniqueKillSwitch]()

      {
        case (path, DirectoryChange.Creation) =>
          println(s"File create detected: $path")
          val killSwitch: UniqueKillSwitch = tailLog(path).toMat(sink)(Keep.left).run()
          tailedLogs += path -> killSwitch
          Nil
        case (path, DirectoryChange.Deletion) =>
          println(s"File delete detected: $path")
          tailedLogs.get(path).foreach { killSwitch =>
            println(s"Shutting tail log stream for: $path")
            killSwitch.shutdown()
            tailedLogs -= path
          }
          Nil
        case _                                => Nil
      }
    }
    .viaMat(KillSwitches.single)(Keep.right)
    .toMat(Sink.ignore)(Keep.both).run()

  def tailLog(path: Path): Source[LogLine, UniqueKillSwitch] = FileTailSource
    .lines(
      path = path,
      maxLineSize = 8192,
      pollingInterval = 250.millis
    )
    .map { line =>
      //println(line)
      LogLine(line, 0)
    }
    .viaMat(KillSwitches.single)(Keep.right)

  def indexInElasticsearch(): Sink[LogLine, NotUsed] = {
    val elasticsearchSink = Flow[LogLine]
      .map(WriteMessage.createIndexMessage[LogLine])
      .via(ElasticsearchFlow.create(indexName, typeName))
      .map { writeResult =>
        writeResult.error.foreach { errorJson =>
          throw new RuntimeException(s"Elasticsearch index failed ${writeResult.errorReason.getOrElse(errorJson)}")
        }
        writeResult
      }
      .toMat(Sink.ignore)(Keep.left)

    val mergeHub = MergeHub
      .source(perProducerBufferSize = 16)
      .to(elasticsearchSink)

    mergeHub.run()
  }

  val run = for {
    _ <- {
      println(s"Copying test data to: $inputLogsPath")
      copyTo(testDataPath, inputLogsPath)
    }
    _ <- {
      println(s"Running index stream for $streamRuntime")
      Thread.sleep(streamRuntime.toMillis)
      println(s"Shutting down index stream")
      control.shutdown()
      println(s"Wait for index stream to shutdown")
      stream
    }
    results <- {
      println(s"Querying elasticsearch for ...")
      readFromElasticsearch(indexName)
    }
    _ <- {
      println(s"Deleting logs from: $inputLogsPath")
      deleteFrom(inputLogsPath)
    }
    _ <- {
      results.foreach(m => println(s"Results:\n$m"))
      println(s"Stop containers..")
      stopContainers()
      println(s"Kill actor system")
      actorSystem.terminate()
    }
  } yield ()

  Await.result(run, 20.seconds)
}
