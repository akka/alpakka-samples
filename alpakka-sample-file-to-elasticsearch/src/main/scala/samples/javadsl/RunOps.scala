package samples.javadsl
import java.nio.file.Path
import java.util.concurrent.CompletionStage

import akka.actor.{ActorSystem, Terminated}
import akka.stream.Materializer
import org.elasticsearch.client.RestClient
import samples.scaladsl

import scala.compat.java8.FutureConverters._

object RunOps {
  val elasticsearchAddress: String = scaladsl.RunOps.elasticsearchContainer.getHttpHostAddress

  def stopContainers()(implicit esClient: RestClient): Unit = scaladsl.RunOps.stopContainers()

  def now(): Long = scaladsl.RunOps.now()

  def listFiles(path: String)(implicit mat: Materializer): CompletionStage[Seq[Path]] = {
    scaladsl.RunOps.listFiles(path).toJava
  }

  def copyTestDataTo(source: String, destination: String)(implicit mat: Materializer): CompletionStage[Unit] = {
    scaladsl.RunOps.copyTestDataTo(source, destination).toJava
  }

  def deleteAllFilesFrom(path: String)(implicit mat: Materializer): CompletionStage[Unit] = {
    scaladsl.RunOps.deleteAllFilesFrom(path).toJava
  }

  def shutdown(actorSystem: ActorSystem)(implicit esClient: RestClient): CompletionStage[Terminated] = {
    scaladsl.RunOps.shutdown(actorSystem).toJava
  }
}
