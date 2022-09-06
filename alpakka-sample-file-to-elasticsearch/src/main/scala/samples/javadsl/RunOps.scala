package samples.javadsl
import java.nio.file.Path
import java.util.concurrent.CompletionStage

import akka.Done
import akka.actor.typed.ActorSystem
import samples.scaladsl

import scala.compat.java8.FutureConverters._

object RunOps {
  val elasticsearchAddress: String = scaladsl.RunOps.elasticsearchAddress

  def stopContainers(): Unit = scaladsl.RunOps.stopContainers()

  def now(): Long = scaladsl.RunOps.now()

  def listFiles(path: String)(implicit system: ActorSystem[_]): CompletionStage[Seq[Path]] = {
    scaladsl.RunOps.listFiles(path).toJava
  }

  def copyTestDataTo(source: String, destination: String)(implicit system: ActorSystem[_]): CompletionStage[Unit] = {
    scaladsl.RunOps.copyTestDataTo(source, destination).toJava
  }

  def deleteAllFilesFrom(path: String)(implicit system: ActorSystem[_]): CompletionStage[Unit] = {
    scaladsl.RunOps.deleteAllFilesFrom(path).toJava
  }

  def shutdown(actorSystem: ActorSystem[_]): CompletionStage[Done] = {
    scaladsl.RunOps.shutdown(actorSystem).toJava
  }
}
