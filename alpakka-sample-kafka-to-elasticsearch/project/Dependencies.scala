import sbt._

object Dependencies {
  val scalaVer = "2.12.8"
  val ScalaTestVersion = "3.0.8"
  val AkkaVersion = "2.5.23"
  val AkkaHttpVersion = "10.1.8"
  val AlpakkaVersion = "1.0.2"
  val AlpakkaKafkaVersion = "1.0.4"

  val dependencies = List(
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.9.9",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.9.9",

    "org.testcontainers" % "elasticsearch" % "1.11.3",
    "org.testcontainers" % "kafka" % "1.11.3",

    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",

    "org.scalatest" %% "scalatest" % ScalaTestVersion % Test
  )
}
