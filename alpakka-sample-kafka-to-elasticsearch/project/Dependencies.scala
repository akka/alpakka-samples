import sbt._

object Dependencies {
  val scalaVer = "2.12.8"
  // #deps
  val AkkaVersion = "2.5.23"
  val AlpakkaVersion = "1.0.2"
  val AlpakkaKafkaVersion = "1.0.4"

  // #deps

  val dependencies = List(
  // #deps
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
    // for JSON in Scala
    "io.spray" %% "spray-json" % "1.3.5",
    // for JSON in Java
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.9.9",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.9.9",
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
  // #deps
    "org.testcontainers" % "elasticsearch" % "1.11.3",
    "org.testcontainers" % "kafka" % "1.11.3"
  )
}
