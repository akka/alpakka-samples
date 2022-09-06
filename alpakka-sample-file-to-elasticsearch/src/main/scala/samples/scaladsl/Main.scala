/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

import java.nio.file._
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.stream.alpakka.elasticsearch._
import akka.stream.alpakka.elasticsearch.scaladsl.{ElasticsearchFlow, ElasticsearchSource}
import akka.stream.alpakka.file.DirectoryChange
import akka.stream.alpakka.file.scaladsl.{DirectoryChangesSource, FileTailSource}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.stream.{KillSwitches, Materializer, UniqueKillSwitch}
import samples.common.DateTimeExtractor
import samples.scaladsl.LogFileSummary.LogFileSummaries

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

object Main extends App {

  import RunOps._
  import JsonFormats._

  implicit val actorSystem: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "FileToElasticSearch")
  implicit val executionContext: ExecutionContext = actorSystem.executionContext

  val streamRuntime = 10.seconds
  val streamParallelism = 47

  val connectionSettings = ElasticsearchConnectionSettings.create(RunOps.elasticsearchAddress)

  val indexName = "logs"
  val typeName = "_doc"

  // #directory-change-source

  val testDataPath = "./test-data"
  val inputLogsPath = s"$testDataPath/input" // watch directory (4)

  val fs = FileSystems.getDefault
  val directoryChangesSource: Source[(Path, DirectoryChange), NotUsed] =
    DirectoryChangesSource(fs.getPath(inputLogsPath), pollInterval = 1.second, maxBufferSize = 1000) // source (3)

  // #directory-change-source

  // #tail-logs

  val tailNewLogs: Flow[(Path, DirectoryChange), Source[LogLine, NotUsed], NotUsed] =
    Flow[(Path, DirectoryChange)]
      // only watch for file creation events (5)
      .collect { case (path, DirectoryChange.Creation) => path }
      .map { path =>
        log.info("File create detected: {}", path)
        // create a new `FileTailSource` and return it as a sub-stream (6)
        fileTailSource(path)
      }

  def fileTailSource(path: Path): Source[LogLine, NotUsed] = {
    val filename = path.getFileName.toString
    val directory = path.getParent.toString

    // create `FileTailSource` for a given `path` (7)
    FileTailSource
      .lines(
        path = path,
        maxLineSize = 8192,
        pollingInterval = 250.millis
      )
      .map { line =>
        log.debug("Parsed > {}", line)
        line
      }
      .scan(LogAcc()) { (acc, line) =>
        // count each line from the log file (8)
        val lineNo = acc.lineNo + 1
        // extract the date timestamp from the log line (9)
        val date = DateTimeExtractor.extractDate(line)
        LogAcc(lineNo,
          // create a `LogLine` record (10)
          Some(LogLine(line, lineNo, date, filename, directory))
        )
      }
      .mapConcat(_.logLine.toList)
  }

  // #tail-logs

  // #es-index-flow

  val elasticsearchIndexFlow: Flow[LogLine, LogLine, NotUsed] =
    Flow[LogLine]
      // create an ES index wrapper message for `LogLine` (11)
      .map(WriteMessage.createIndexMessage[LogLine])
      // use Alpakka Elasticsearch to create a new `LogLine` record. (12)
      // implicitly takes `JsonFormat` for `LogLine` for serialization
      .via(ElasticsearchFlow.create(ElasticsearchParams.V5(indexName, typeName),
        ElasticsearchWriteSettings.create(connectionSettings).withApiVersion(ApiVersion.V5)))
      .mapConcat { writeResult =>
        writeResult.error.foreach { errorJson =>
          throw new RuntimeException(s"Elasticsearch index failed ${writeResult.errorReason.getOrElse(errorJson)}")
        }
        writeResult.message.source.toList
      }

  // #es-index-flow

  // #summarize-log-stats-flow

  val summarizeLogStatsFlow: Flow[LogLine, LogFileSummaries, NotUsed] =
    Flow[LogLine]
      // track statistics per log file (13)
      .scan(Map[(String, String), LogFileSummary]()) { (summaries, logLine) =>
        val key = (logLine.directory, logLine.filename)
        val timestamp = now()
        val summary = summaries
          .get(key)
          .map(_.copy(lastUpdated = timestamp, numberOfLines = logLine.lineNo))
          .getOrElse(LogFileSummary(logLine.directory, logLine.filename, timestamp, timestamp, logLine.lineNo))
        summaries + (key -> summary)
      }

  // #summarize-log-stats-flow

  // #query-elasticsearch

  def queryAllRecordsFromElasticsearch(indexName: String): Future[immutable.Seq[LogLine]] = {
    val reading = ElasticsearchSource
      // use Alpakka Elasticsearch to return all entries from the provided index (14)
      .typed[LogLine](ElasticsearchParams.V5(indexName, typeName), """{"match_all": {}}""",
        ElasticsearchSourceSettings.create(connectionSettings).withApiVersion(ApiVersion.V5))
      .map(_.source)
      .runWith(Sink.seq)
    reading.foreach(_ => log.info("Reading finished"))
    reading
  }

  // #query-elasticsearch

  def runStreamForAwhileAndShutdown(waitInterval: FiniteDuration,
                                    control: UniqueKillSwitch,
                                    stream: Future[LogFileSummaries])(implicit mat: Materializer): Future[LogFileSummaries] = {
    log.info("Running index stream for {}", waitInterval)
    Thread.sleep(waitInterval.toMillis)
    log.info("Shutting down index stream")
    control.shutdown()
    log.info("Wait for index stream to shutdown")
    stream
  }

  def printResults(results: Seq[LogLine], summaries: LogFileSummaries): Unit = {
    results.foreach(m => log.debug("Results < {}", m))

    val fmt = "%-32s%-32s%-16s%-16s%s"
    val header = fmt.format("Directory", "File", "First Seen", "Last Updated", "Number of Lines")
    val summariesStr = summaries.values.map { summary =>
      import summary._
      fmt.format(directory, filename, firstSeen, lastUpdated, numberOfLines)
    }.mkString("\n")

    log.info("LogFileSummaries:\n{}\n{}", header, summariesStr)
  }

  // #stream-composing

  // compose stream together starting with the `DirectoryChangesSource` (15)
  val graph: RunnableGraph[(UniqueKillSwitch, Future[LogFileSummaries])] = directoryChangesSource
    // create `FileTailSource` sub-streams
    .via(tailNewLogs)
    // merge the sub-streams together and emit all file `LogLine` records downstream
    .flatMapMerge(streamParallelism, identity)
    // index into Elasticsearch
    .via(elasticsearchIndexFlow)
    // summarize log statistics
    .via(summarizeLogStatsFlow)
    // create a `KillSwitch` so we can shutdown the stream from the outside. use this as the materialized value.
    .viaMat(KillSwitches.single)(Keep.right)
    // materialize the last recorded log stats summarization.
    // return both a `UniqueKillSwitch` `Future[LogFileSummaries]`
    .toMat(Sink.last)(Keep.both)

  // #stream-composing

  // #running-the-app

  val run = for {
    _ <-                deleteAllFilesFrom(inputLogsPath)
    // run the graph and capture the materialized values (16)
    (control, stream) = graph.run()
    _ <-                copyTestDataTo(testDataPath, inputLogsPath)
    summaries <-        runStreamForAwhileAndShutdown(streamRuntime, control, stream)
    // run a new graph to query all records from Elasticsearch and get the results (17)
    results <-          queryAllRecordsFromElasticsearch(indexName)
    _ =                 printResults(results, summaries)
    _ <-                deleteAllFilesFrom(inputLogsPath)
    _ <-                shutdown(actorSystem)
  } yield ()

  Await.result(run, streamRuntime + 20.seconds)

  // #running-the-app
}
