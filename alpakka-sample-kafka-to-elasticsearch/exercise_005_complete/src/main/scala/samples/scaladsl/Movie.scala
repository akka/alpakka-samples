package samples.scaladsl

import spray.json.DefaultJsonProtocol._
import spray.json._

// Type in Elasticsearch (2)
case class Movie(id: Int, title: String)

object JsonFormats {
  // Spray JSON conversion setup (3)
  implicit val movieFormat: JsonFormat[Movie] = jsonFormat2(Movie)
}
