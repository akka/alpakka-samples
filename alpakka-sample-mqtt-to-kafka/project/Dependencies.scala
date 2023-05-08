import sbt._

object Dependencies {
  val scalaVer = "2.13.7"
  // #deps
  val AkkaVersion = "2.7.0"
  val AlpakkaVersion = "6.0.1"
  val AlpakkaKafkaVersion = "4.0.0"
  val JacksonDatabindVersion = "2.11.4"

  // #deps

  val dependencies = List(
  // #deps
    "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.4",
    // JSON
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % JacksonDatabindVersion,
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % JacksonDatabindVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % JacksonDatabindVersion,
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
  // #deps
    "org.testcontainers" % "kafka" % "1.16.0"
  )
}
