package samples.scaladsl

import spray.json.DefaultJsonProtocol._
import spray.json._

// Type in Elasticsearch (2)
case class LogLine(line: String, date: Long)

object JsonFormats {
  // Spray JSON conversion setup (3)
  implicit val logLineFormat: JsonFormat[LogLine] = jsonFormat2(LogLine)
}
