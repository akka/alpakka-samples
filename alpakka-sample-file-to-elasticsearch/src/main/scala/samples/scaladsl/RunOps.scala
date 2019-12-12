package samples.scaladsl
import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import java.time.ZonedDateTime

import akka.Done
import akka.actor.typed.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.file.scaladsl.Directory
import akka.stream.scaladsl.{Keep, Sink}
import org.elasticsearch.client.RestClient
import org.slf4j.LoggerFactory
import org.testcontainers.elasticsearch.ElasticsearchContainer

import scala.concurrent.Future

object RunOps {
  final val log = LoggerFactory.getLogger(getClass)

  // Testcontainers: start Elasticsearch in Docker
  private[samples] val elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:6.4.3")
  elasticsearchContainer.start()
  val elasticsearchAddress: String = elasticsearchContainer.getHttpHostAddress

  def stopContainers()(implicit esClient: RestClient): Unit = {
    esClient.close()
    elasticsearchContainer.stop()
  }

  def now(): Long = ZonedDateTime.now.toInstant.toEpochMilli

  def listFiles(path: String)(implicit mat: Materializer): Future[Seq[Path]] =
    Directory.ls(Paths.get(path)).filterNot(Files.isDirectory(_)).toMat(Sink.seq)(Keep.right).run()

  def copyTestDataTo(source: String, destination: String)(implicit mat: Materializer): Future[Unit] = {
    implicit val ec = mat.executionContext
    for {
      sourceFiles <- listFiles(source)
    } yield sourceFiles foreach { sourceFile =>
      val destFile = Paths.get(destination, sourceFile.getFileName.toString)
      log.info(s"Copying file $sourceFile to $destFile")
      Files.copy(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING)
    }
  }

  def deleteAllFilesFrom(path: String)(implicit mat: Materializer): Future[Unit] = {
    implicit val ec = mat.executionContext
    for {
      files <- listFiles(path)
    } yield files filterNot(_.getFileName.toString == ".gitignore") foreach { file =>
      log.info(s"Deleting file: $file")
      Files.delete(file)
    }
  }

  def shutdown(system: ActorSystem[_])(implicit esClient: RestClient): Future[Done] = {
    log.info(s"Stop containers")
    stopContainers()
    log.info(s"Kill actor system")
    system.terminate()
    system.whenTerminated
  }
}
