import sbt._

object Dependencies {
  val scalaVer = "2.13.1"
  // #deps
  val AkkaVersion = "2.5.29"
  val AkkaHttpVersion = "10.1.11"
  val AlpakkaKafkaVersion = "2.0.2"
  // #deps
  val dependencies = List(
    // #deps
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,

    // for JSON in Scala
    "io.spray" %% "spray-json" % "1.3.5",
    // for JSON in Java
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.10.0",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.0",
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    // #deps

    "org.testcontainers" % "kafka" % "1.12.2",

    "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
    "com.google.guava" % "guava" % "28.2-jre" % Test,
    "junit" % "junit" % "4.12" % Test,

  )
}
