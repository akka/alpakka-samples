/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

// #imports
import akka.Done
import akka.actor.ActorSystem
import akka.stream.alpakka.elasticsearch.WriteMessage.createIndexMessage
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSink
import akka.stream.alpakka.slick.javadsl.SlickSession
import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.{ActorMaterializer, Materializer}
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import spray.json.DefaultJsonProtocol.{jsonFormat4, _}
import spray.json.JsonFormat

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
// #imports

object Main extends App with Helper {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: Materializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  def wait(duration: FiniteDuration): Unit = Thread.sleep(duration.toMillis)

  def terminateActorSystem(): Unit =
    Await.result(actorSystem.terminate(), 1.seconds)

  // #slick-setup

  implicit val session = SlickSession.forConfig("slick-h2-mem")                         // (1)
  actorSystem.registerOnTermination(session.close())

  import session.profile.api._
  class Movies(tag: Tag) extends Table[(Int, String, String, Double)](tag, "MOVIE") {   // (2)
    def id = column[Int]("ID")
    def title = column[String]("TITLE")
    def genre = column[String]("GENRE")
    def gross = column[Double]("GROSS")

    override def * = (id, title, genre, gross)
  }
  // #slick-setup
  Await.result(Helper.populateDataForTable(), 2.seconds)


  // #data-class
  case class Movie(id: Int, title: String, genre: String, gross: Double)

  implicit val format: JsonFormat[Movie] = jsonFormat4(Movie)
  // #data-class

  // #es-setup
  implicit val elasticSearchClient: RestClient =
    RestClient.builder(HttpHost.create(elasticsearchAddress)).build()
  // #es-setup

  // #sample
  val done: Future[Done] =
    Slick
      .source(TableQuery[Movies].result)
      .map {
        case (id, genre, title, gross) => Movie(id, genre, title, gross)
      }
      .map(movie => createIndexMessage(movie.id.toString, movie))
      .runWith(ElasticsearchSink.create[Movie]("movie", "_doc"))

  done.onComplete {
    case _ =>
      elasticSearchClient.close()
  }
  // #sample
  done.onComplete {
    case _ =>
      stopContainers()
  }
  wait(10.seconds)
  terminateActorSystem()

}
