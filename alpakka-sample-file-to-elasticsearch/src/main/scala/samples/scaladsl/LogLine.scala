package samples.scaladsl

import spray.json.DefaultJsonProtocol._
import spray.json._

// Type in Elasticsearch
case class LogLine(line: String, lineNo: Long, date: Long, filename: String, directory: String)

object JsonFormats {
  // Spray JSON conversion setup
  implicit val logLineFormat: JsonFormat[LogLine] = jsonFormat5(LogLine)
}
