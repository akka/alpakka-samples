package samples.scaladsl

import spray.json.DefaultJsonProtocol._
import spray.json._

// Type in Elasticsearch
final case class LogLine(line: String, lineNo: Long, date: Long, filename: String, directory: String)

object JsonFormats {
  // Spray JSON conversion setup
  implicit val logLineFormat: JsonFormat[LogLine] = jsonFormat5(LogLine)
}

final case class LogAcc(lineNo: Long = 0L, logLine: Option[LogLine] = None)

object LogFileSummary {
  type LogFileSummaries = Map[(String, String), LogFileSummary]
}

final case class LogFileSummary(directory: String, filename: String, firstSeen: Long, lastUpdated: Long, numberOfLines: Long)