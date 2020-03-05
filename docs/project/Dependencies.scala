import sbt._
import scala.meta._

object Dependencies {

  object FtpToFile {
    val versions = {
      val source = IO.read(file(".") / ".." / "alpakka-sample-ftp-to-file" / "project" / "Dependencies.scala")
      val tree = source.parse[Source].get
      tree.collect {
        case q"val ${v: Pat.Var} = ${s: Lit.String}" => v.name.value -> s.value
      }.toMap
    }

    val ScalaVersion = versions("scalaVer")
    val AkkaVersion = versions("AkkaVersion")
    val AlpakkaVersion = versions("AlpakkaVersion")
  }

  object HttpCsvToKafka {
    val versions = {
      val source = IO.read(file(".") / ".." / "alpakka-sample-http-csv-to-kafka" / "project" / "Dependencies.scala")
      val tree = source.parse[Source].get
      tree.collect {
        case q"val ${v: Pat.Var} = ${s: Lit.String}" => v.name.value -> s.value
      }.toMap
    }

    val ScalaVersion = versions("scalaVer")
    val ScalaTestVersion = versions("ScalaTestVersion")
    val AkkaVersion = versions("AkkaVersion")
    val AkkaHttpVersion = versions("AkkaHttpVersion")
    val AlpakkaVersion = versions("AlpakkaVersion")
    val AlpakkaKafkaVersion = versions("AlpakkaKafkaVersion")
  }

  object JdbcToElasticsearch {
    val versions = {
      val source = IO.read(file(".") / ".." / "alpakka-sample-jdbc-to-elasticsearch" / "project" / "Dependencies.scala")
      val tree = source.parse[Source].get
      tree.collect {
        case q"val ${v: Pat.Var} = ${s: Lit.String}" => v.name.value -> s.value
      }.toMap
    }

    val ScalaVersion = versions("scalaVer")
    val AkkaVersion = versions("AkkaVersion")
    val AlpakkaVersion = versions("AlpakkaVersion")
  }

  object Jms {
    val versions = {
      val source = IO.read(file(".") / ".." / "alpakka-sample-jms" / "project" / "Dependencies.scala")
      val tree = source.parse[Source].get
      tree.collect {
        case q"val ${v: Pat.Var} = ${s: Lit.String}" => v.name.value -> s.value
      }.toMap
    }

    val ScalaVersion = versions("scalaVer")
    val AkkaVersion = versions("AkkaVersion")
    val AkkaHttpVersion = versions("AkkaHttpVersion")
    val AlpakkaVersion = versions("AlpakkaVersion")
  }

  object KafkaToElasticsearch {
    val versions = {
      val source = IO.read(file(".") / ".." / "alpakka-sample-kafka-to-elasticsearch" / "project" / "Dependencies.scala")
      val tree = source.parse[Source].get
      tree.collect {
        case q"val ${v: Pat.Var} = ${s: Lit.String}" => v.name.value -> s.value
      }.toMap
    }

    val ScalaVersion = versions("scalaVer")
    val AkkaVersion = versions("AkkaVersion")
    val AlpakkaVersion = versions("AlpakkaVersion")
    val AlpakkaKafkaVersion = versions("AlpakkaKafkaVersion")
  }
  
  object KafkaToWebsocketClients {
    val versions = {
      val source = IO.read(file(".") / ".." / "alpakka-sample-kafka-to-websocket-clients" / "project" / "Dependencies.scala")
      val tree = source.parse[Source].get
      tree.collect {
        case q"val ${v: Pat.Var} = ${s: Lit.String}" => v.name.value -> s.value
      }.toMap
    }

    val ScalaVersion = versions("scalaVer")
    val AkkaVersion = versions("AkkaVersion")
    val AlpakkaKafkaVersion = versions("AlpakkaKafkaVersion")
  }

  object MqttToKafka {
    val versions = {
      val source = IO.read(file(".") / ".." / "alpakka-sample-mqtt-to-kafka" / "project" / "Dependencies.scala")
      val tree = source.parse[Source].get
      tree.collect {
        case q"val ${v: Pat.Var} = ${s: Lit.String}" => v.name.value -> s.value
      }.toMap
    }

    val ScalaVersion = versions("scalaVer")
    val AkkaVersion = versions("AkkaVersion")
    val AlpakkaVersion = versions("AlpakkaVersion")
    val AlpakkaKafkaVersion = versions("AlpakkaKafkaVersion")
  }

  object FileToElasticsearch {
    val versions = {
      val source = IO.read(file(".") / ".." / "alpakka-sample-file-to-elasticsearch" / "project" / "Dependencies.scala")
      val tree = source.parse[Source].get
      tree.collect {
        case q"val ${v: Pat.Var} = ${s: Lit.String}" => v.name.value -> s.value
      }.toMap
    }

    val ScalaVersion = versions("scalaVer")
    val AkkaVersion = versions("AkkaVersion")
    val AlpakkaVersion = versions("AlpakkaVersion")
  }

  object RotateLogsToFtp {
    val versions = {
      val source = IO.read(file(".") / ".." / "alpakka-sample-rotate-logs-to-ftp" / "project" / "Dependencies.scala")
      val tree = source.parse[Source].get
      tree.collect {
        case q"val ${v: Pat.Var} = ${s: Lit.String}" => v.name.value -> s.value
      }.toMap
    }

    val ScalaVersion = versions("scalaVer")
    val AkkaVersion = versions("AkkaVersion")
    val AlpakkaVersion = versions("AlpakkaVersion")
  }

}
