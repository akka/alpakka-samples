package samples.scaladsl

import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import java.time.ZonedDateTime
import akka.Done
import akka.actor.typed.ActorSystem
import akka.stream.alpakka.file.scaladsl.Directory
import akka.stream.scaladsl.{Keep, Sink}
import org.slf4j.LoggerFactory
import org.testcontainers.elasticsearch.ElasticsearchContainer

import scala.concurrent.Future

object RunOps {
  final val log = LoggerFactory.getLogger(getClass)

  // Testcontainers: start Elasticsearch in Docker
  private val elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:7.10.2")
  elasticsearchContainer.start()
  private[samples] val elasticsearchAddress: String = "http://" + elasticsearchContainer.getHttpHostAddress

  def stopContainers(): Unit = {
    elasticsearchContainer.stop()
  }

  def now(): Long = ZonedDateTime.now.toInstant.toEpochMilli

  def listFiles(path: String)(implicit system: ActorSystem[_]): Future[Seq[Path]] =
    Directory.ls(Paths.get(path)).filterNot(Files.isDirectory(_)).toMat(Sink.seq)(Keep.right).run()

  def copyTestDataTo(source: String, destination: String)(implicit system: ActorSystem[_]): Future[Unit] = {
    implicit val ec = system.executionContext
    for {
      sourceFiles <- listFiles(source)
    } yield sourceFiles foreach { sourceFile =>
      val destFile = Paths.get(destination, sourceFile.getFileName.toString)
      log.info(s"Copying file $sourceFile to $destFile")
      Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING)
    }
  }

  def deleteAllFilesFrom(path: String)(implicit system: ActorSystem[_]): Future[Unit] = {
    implicit val ec = system.executionContext
    for {
      files <- listFiles(path)
    } yield files filterNot (_.getFileName.toString == ".gitignore") foreach { file =>
      log.info(s"Deleting file: $file")
      Files.delete(file)
    }
  }

  def shutdown(system: ActorSystem[_]): Future[Done] = {
    log.info(s"Stop containers")
    stopContainers()
    log.info(s"Kill actor system")
    system.terminate()
    system.whenTerminated
  }
}
