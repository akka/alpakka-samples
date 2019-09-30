/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

import java.nio.file._
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneOffset, ZonedDateTime}

import akka.actor.ActorSystem
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchFlow
import akka.stream.alpakka.file.DirectoryChange
import akka.stream.alpakka.file.scaladsl.{DirectoryChangesSource, FileTailSource}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, KillSwitches, Materializer, UniqueKillSwitch}
import akka.{Done, NotUsed}
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.matching.Regex

object Main extends App with RunOps {

  import JsonFormats._

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val streamRuntime = 5.seconds
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

  sealed trait DateTimeExtractor {
    def regex: Regex
    def parse(dateStr: String): Long
    def maybeParse(dateStr: String): Option[Long] = {
      val matched: Option[String] = regex.findFirstIn(dateStr)
      matched.map(parse)
    }
  }

  /**
   * ZonedDateTime
   * Ex)
   * 2016-01-19T15:21:32.59+02:00
   * https://regex101.com/r/LYluKk/4
   */
  final class ZonedDateTimeExtractor extends DateTimeExtractor {
    val regex: Regex = """((?:(\d{4}-\d{2}-\d{2})[T| ](\d{2}:\d{2}:\d{2}(?:\.\d+)?))(Z|[\+-]\d{2}:\d{2})+)""".r
    def parse(dateStr: String): Long = ZonedDateTime.parse(dateStr, DateTimeFormatter.ISO_ZONED_DATE_TIME).toInstant.toEpochMilli
  }

  /**
   * LocalDateTime
   * Ex)
   * 2019-09-20 21:18:24,774
   * https://regex101.com/r/LYluKk/3
   */
  final class LocalDateTimeExtractor extends DateTimeExtractor {
    private val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd kk:mm:ss,SSS")
    val regex: Regex = """((?:(\d{4}-\d{2}-\d{2})[T| ](\d{2}:\d{2}:\d{2}(?:\.\d+)?)),(\d{3}?))""".r
    def parse(dateStr: String): Long = LocalDateTime.parse(dateStr, pattern).toInstant(ZoneOffset.UTC).toEpochMilli
  }

  val dateTimeExtractors = List(new ZonedDateTimeExtractor, new LocalDateTimeExtractor)

  val fs = FileSystems.getDefault
  val directoryChangesSource: Source[(Path, DirectoryChange), NotUsed] =
    DirectoryChangesSource(fs.getPath(inputLogsPath), pollInterval = 1.second, maxBufferSize = 1000)

  val tailNewLogs: Flow[(Path, DirectoryChange), Source[LogLine, NotUsed], NotUsed] =
    Flow[(Path, DirectoryChange)]
      .collect { case (path, DirectoryChange.Creation) => path }
      .map { path =>
        println(s"File create detected: $path")
        fileTailSource(path)
      }

  final case class LogAcc(lineNo: Long = 0L, logLine: Option[LogLine] = None)

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
        val date = extractDate(line)
        LogAcc(lineNo,
          Some(LogLine(line, lineNo, date, filename, directory))
        )
      }
      .mapConcat(_.logLine.toList)
  }

  def extractDate(line: String): Long = {
    dateTimeExtractors
      .view
      .map(_.maybeParse(line))
      .collectFirst {
        case Some(d) => d
      }
      .getOrElse(-1L)
  }

  val elasticsearchIndexSink: Sink[LogLine, (UniqueKillSwitch, Future[Done])] =
    Flow[LogLine]
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

  val graph: RunnableGraph[(UniqueKillSwitch, Future[Done])] = directoryChangesSource
    .via(tailNewLogs)
    .flatMapMerge(streamParallelism, identity)
    .toMat(elasticsearchIndexSink)(Keep.right)

  val run = for {
    _ <-                deleteAllFilesFrom(inputLogsPath)
    (control, stream) = graph.run()
    _ <-                copyTestDataTo(testDataPath, inputLogsPath)
    _ <-                runStreamForAwhileAndShutdown(streamRuntime, control, stream)
    results <-          queryElasticsearch(indexName)
    _ =                 printResults(results)
    _ <-                deleteAllFilesFrom(inputLogsPath)
    _ <-                shutdown(actorSystem)
  } yield ()

  Await.result(run, streamRuntime + 20.seconds)
}
