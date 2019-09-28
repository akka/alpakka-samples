package samples.scaladsl

import spray.json.DefaultJsonProtocol._
import spray.json._

// Type in Elasticsearch
case class LogLine(line: String, date: Long)

object JsonFormats {
  // Spray JSON conversion setup
  implicit val logLineFormat: JsonFormat[LogLine] = jsonFormat2(LogLine)
}
