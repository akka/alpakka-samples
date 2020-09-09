import sbt._

object Dependencies {
  val scalaVer = "2.13.3"
  // #deps
  val AkkaVersion = "2.6.8"
  val AlpakkaVersion = "2.0.1"
  val AlpakkaKafkaVersion = "2.0.5"

  // #deps

  val dependencies = List(
  // #deps
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    // for JSON in Scala
    "io.spray" %% "spray-json" % "1.3.5",
    // for JSON in Java
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.10.5",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.5",
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
  // #deps
    "org.testcontainers" % "elasticsearch" % "1.14.3",
    "org.testcontainers" % "kafka" % "1.14.3"
  )
}
