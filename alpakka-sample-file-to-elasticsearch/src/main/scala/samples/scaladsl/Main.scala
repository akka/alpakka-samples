/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

import java.nio.file._

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.scaladsl.{ElasticsearchFlow, ElasticsearchSource}
import akka.stream.alpakka.file.DirectoryChange
import akka.stream.alpakka.file.scaladsl.{DirectoryChangesSource, FileTailSource}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitches, Materializer, UniqueKillSwitch}
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import samples.common.{DateTimeExtractor, RunOps}
import samples.scaladsl.LogFileSummary.LogFileSummaries

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object Main extends App with RunOps {

  import JsonFormats._

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val streamRuntime = 10.seconds
  val streamParallelism = 47

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
  val directoryChangesSource: Source[(Path, DirectoryChange), NotUsed] =
    DirectoryChangesSource(fs.getPath(inputLogsPath), pollInterval = 1.second, maxBufferSize = 1000)

  val tailNewLogs: Flow[(Path, DirectoryChange), Source[LogLine, NotUsed], NotUsed] =
    Flow[(Path, DirectoryChange)]
      .collect { case (path, DirectoryChange.Creation) => path }
      .map { path =>
        log.info(s"File create detected: $path")
        fileTailSource(path)
      }

  def fileTailSource(path: Path): Source[LogLine, NotUsed] = {
    val filename = path.getFileName.toString
    val directory = path.getParent.toString

    FileTailSource
      .lines(
        path = path,
        maxLineSize = 8192,
        pollingInterval = 250.millis
      )
      .map { line =>
        log.debug(s"Parsed > $line")
        line
      }
      .scan(LogAcc()) { (acc, line) =>
        val lineNo = acc.lineNo + 1
        val date = DateTimeExtractor.extractDate(line)
        LogAcc(lineNo,
          Some(LogLine(line, lineNo, date, filename, directory))
        )
      }
      .mapConcat(_.logLine.toList)
  }

  val elasticsearchIndexFlow: Flow[LogLine, LogLine, NotUsed] =
    Flow[LogLine]
      .map(WriteMessage.createIndexMessage[LogLine])
      .via(ElasticsearchFlow.create(indexName, typeName))
      .mapConcat { writeResult =>
        writeResult.error.foreach { errorJson =>
          throw new RuntimeException(s"Elasticsearch index failed ${writeResult.errorReason.getOrElse(errorJson)}")
        }
        writeResult.message.source.toList
      }

  val summarizeLogStatsFlow: Flow[LogLine, LogFileSummaries, NotUsed] =
    Flow[LogLine]
      .scan(Map[(String, String), LogFileSummary]()) { (summaries, logLine) =>
        val key = (logLine.directory, logLine.filename)
        val timestamp = now
        val summary = summaries
          .get(key)
          .map(_.copy(lastUpdated = timestamp, numberOfLines = logLine.lineNo))
          .getOrElse(LogFileSummary(logLine.directory, logLine.filename, timestamp, timestamp, logLine.lineNo))
        summaries + (key -> summary)
      }

  def queryAllRecordsFromElasticsearch(indexName: String): Future[immutable.Seq[LogLine]] = {
    val reading = ElasticsearchSource
      .typed[LogLine](indexName, "_doc", """{"match_all": {}}""")
      .map(_.source)
      .runWith(Sink.seq)
    reading.foreach(_ => log.info("Reading finished"))(actorSystem.dispatcher)
    reading
  }

  def runStreamForAwhileAndShutdown(waitInterval: FiniteDuration,
                                    control: UniqueKillSwitch,
                                    stream: Future[LogFileSummaries])(implicit mat: Materializer): Future[LogFileSummaries] = {
    log.info(s"Running index stream for $waitInterval")
    Thread.sleep(waitInterval.toMillis)
    log.info(s"Shutting down index stream")
    control.shutdown()
    log.info(s"Wait for index stream to shutdown")
    stream
  }

  def printResults(results: Seq[LogLine], summaries: LogFileSummaries): Unit = {
    results.foreach(m => log.debug(s"Results < $m"))

    val fmt = "%-32s%-32s%-16s%-16s%s"
    val header = fmt.format("Directory", "File", "First Seen", "Last Updated", "Number of Lines")
    val summariesStr = summaries.values.map { summary =>
      import summary._
      fmt.format(directory, filename, firstSeen, lastUpdated, numberOfLines)
    }.mkString("\n")

    log.info(s"LogFileSummaries:\n$header\n$summariesStr")
  }

  val graph: RunnableGraph[(UniqueKillSwitch, Future[LogFileSummaries])] = directoryChangesSource
    .via(tailNewLogs)
    .flatMapMerge(streamParallelism, identity)
    .via(elasticsearchIndexFlow)
    .via(summarizeLogStatsFlow)
    .viaMat(KillSwitches.single)(Keep.right)
    .toMat(Sink.last)(Keep.both)

  val run = for {
    _ <-                deleteAllFilesFrom(inputLogsPath)
    (control, stream) = graph.run()
    _ <-                copyTestDataTo(testDataPath, inputLogsPath)
    summaries <-        runStreamForAwhileAndShutdown(streamRuntime, control, stream)
    results <-          queryAllRecordsFromElasticsearch(indexName)
    _ =                 printResults(results, summaries)
    _ <-                deleteAllFilesFrom(inputLogsPath)
    _ <-                shutdown(actorSystem)
  } yield ()

  Await.result(run, streamRuntime + 20.seconds)
}
