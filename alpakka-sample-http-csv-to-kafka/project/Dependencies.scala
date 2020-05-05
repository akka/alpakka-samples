import sbt._

object Dependencies {
  val scalaVer = "2.13.1"
  // #dependencies
  val ScalaTestVersion = "3.1.0"
  val AkkaVersion = "2.6.4"
  val AkkaHttpVersion = "10.1.11"
  val AlpakkaVersion = "2.0.0-RC2"
  val AlpakkaKafkaVersion = "2.0.2"

  val dependencies = List(
    "com.lightbend.akka" %% "akka-stream-alpakka-csv" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    // Used from Scala
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    // Used from Java
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.10.3",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.3",

    "org.testcontainers" % "kafka" % "1.13.0",
    
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
  // #dependencies
}
