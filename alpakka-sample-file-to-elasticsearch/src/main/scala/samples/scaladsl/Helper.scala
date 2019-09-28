/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.scaladsl

import java.nio.file.{Files, Path, Paths, StandardCopyOption}

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSource
import akka.stream.alpakka.file.scaladsl.Directory
import akka.stream.scaladsl.{Keep, Sink}
import org.slf4j.LoggerFactory
import org.testcontainers.elasticsearch.ElasticsearchContainer
import samples.scaladsl.Main.elasticsearchClient

import scala.collection.immutable
import scala.concurrent.Future

trait Helper {

  final val log = LoggerFactory.getLogger(getClass)

  import JsonFormats._

  // Testcontainers: start Elasticsearch in Docker
  val elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:6.4.3")
  elasticsearchContainer.start()
  val elasticsearchAddress = elasticsearchContainer.getHttpHostAddress

  def readFromElasticsearch(indexName: String)(implicit actorSystem: ActorSystem, materializer: Materializer): Future[immutable.Seq[LogLine]] = {
    val reading = ElasticsearchSource
      .typed[LogLine](indexName, "_doc", """{"match_all": {}}""")
      .map(_.source)
      .runWith(Sink.seq)
    reading.foreach(_ => log.info("Reading finished"))(actorSystem.dispatcher)
    reading
  }

  def stopContainers() = {
    elasticsearchClient.close()
    elasticsearchContainer.stop()
  }

  def listFiles(path: String)(implicit mat: Materializer): Future[Seq[Path]] =
    Directory.ls(Paths.get(path)).filterNot(Files.isDirectory(_)).toMat(Sink.seq)(Keep.right).run()

  def copyTo(source: String, destination: String)(implicit mat: Materializer): Future[Unit] = {
    implicit val ec = mat.executionContext
    for {
      sourceFiles <- listFiles(source)
    } yield sourceFiles foreach { sourceFile =>
      val destFile = Paths.get(destination, sourceFile.getFileName.toString)
      Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING)
    }
  }

  def deleteFrom(path: String)(implicit mat: Materializer): Future[Unit] = {
    implicit val ec = mat.executionContext
    for {
      files <- listFiles(path)
    } yield files foreach { file => Files.delete(file) }
  }
}
