/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

import java.nio.file._

import akka.actor.ActorSystem
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchFlow
import akka.stream.alpakka.file.DirectoryChange
import akka.stream.alpakka.file.scaladsl.{DirectoryChangesSource, FileTailSource}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitches, Materializer, UniqueKillSwitch}
import akka.{Done, NotUsed}
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object Main extends App with Helper {

  import JsonFormats._

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val streamRuntime = 5.seconds
  val substreamMergeParallelism = 47

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

  lazy val elasticsearchIndexSink: Sink[LogLine, (UniqueKillSwitch, Future[Done])] = {
    val sink = Flow[LogLine]
      .map(WriteMessage.createIndexMessage[LogLine])
      .via(ElasticsearchFlow.create(indexName, typeName))
      .map { writeResult =>
        writeResult.error.foreach { errorJson =>
          throw new RuntimeException(s"Elasticsearch index failed ${writeResult.errorReason.getOrElse(errorJson)}")
        }
        writeResult
      }
      .viaMat(KillSwitches.single)(Keep.right)
      .toMat(Sink.ignore)(Keep.both)

    sink
  }

  val (control, stream) = changes
    .mapConcat {
      case (path, DirectoryChange.Creation) =>
        println(s"File create detected: $path")
        val source = tailLog(path)
        List(source)
      case _                                => Nil
    }
    .flatMapMerge(substreamMergeParallelism, identity) // merge substreams and flatten elements emitted
    .toMat(elasticsearchIndexSink)(Keep.right)
    .run()

  def tailLog(path: Path): Source[LogLine, NotUsed] = FileTailSource
    .lines(
      path = path,
      maxLineSize = 8192,
      pollingInterval = 250.millis
    )
    .map { line =>
      println(s"> $line")
      LogLine(line, 0)
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

  Await.result(run, streamRuntime + 20.seconds)
}
