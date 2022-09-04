/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

import akka.actor.typed.ActorSystem
import akka.stream.alpakka.elasticsearch.ElasticsearchConnectionSettings
import akka.stream.alpakka.elasticsearch.ElasticsearchParams
import akka.stream.alpakka.elasticsearch.ElasticsearchSourceSettings
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.scaladsl.{ElasticsearchFlow, ElasticsearchSource}
import akka.stream.alpakka.slick.javadsl.SlickSession
import akka.stream.alpakka.slick.scaladsl.Slick
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.{Done, NotUsed}
import org.slf4j.LoggerFactory
import org.testcontainers.elasticsearch.ElasticsearchContainer
import samples.scaladsl.Main.Movie
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

trait Helper {

  final val log = LoggerFactory.getLogger(getClass)

  // Testcontainers: start Elasticsearch in Docker
  val elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:7.10.2")
  elasticsearchContainer.start()
  val elasticsearchAddress: String = "http://" + elasticsearchContainer.getHttpHostAddress

  def readFromElasticsearch(indexName: String)(implicit actorSystem: ActorSystem[_]): Future[immutable.Seq[Movie]] = {
    val reading = ElasticsearchSource
      .typed[Movie](ElasticsearchParams.V7(indexName), """{"match_all": {}}""",
        ElasticsearchSourceSettings(ElasticsearchConnectionSettings(elasticsearchAddress)))
      .map(_.source)
      .runWith(Sink.seq)
    reading.foreach(_ => log.info("Reading finished"))(actorSystem.executionContext)
    reading
  }

  def stopContainers() = {
    elasticsearchContainer.stop()
  }
}

object Helper {
  def populateDataForTable()(implicit session: SlickSession, actorSystem: ActorSystem[_]) = {

    import session.profile.api._

    //Drop table if already exists
    val dropTableFut =
      sqlu"""drop table if exists MOVIE"""

    //Create movie table
    val createTableFut =
      sqlu"""create table MOVIE (ID INT PRIMARY KEY, TITLE varchar, GENRE varchar, GROSS numeric(10,2))"""

    Await.result(session.db.run(dropTableFut), 10.seconds)
    Await.result(session.db.run(createTableFut), 10.seconds)

    //A class just for organizing the data before using it in the insert clause.  Could have been insertFut with a Tuple too
    case class MovieInsert(id: Int, title: String, genre: String, gross: Double)

    val movies = List(
      MovieInsert(1, "Rogue One", "Adventure", 3.032),
      MovieInsert(2, "Beauty and the Beast", "Musical", 2.795),
      MovieInsert(3, "Wonder Woman", "Action", 2.744),
      MovieInsert(4, "Guardians of the Galaxy", "Action", 2.568),
      MovieInsert(5, "Moana", "Musical", 2.493),
      MovieInsert(6, "Spider-Man", "Action", 1.784)
    )

    Source(movies)
      .via(
        Slick.flow(
          movie => sqlu"INSERT INTO MOVIE VALUES (${movie.id}, ${movie.title}, ${movie.genre}, ${movie.gross})"
        )
      )
      .runWith(Sink.ignore)
  }

}
