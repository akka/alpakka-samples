import sbt._

object Dependencies {
  val scalaVer = "2.13.12"
  // #dependencies
  val ScalaTestVersion = "3.1.4"
  val AkkaVersion = "2.9.0"
  val AkkaHttpVersion = "10.6.0"
  val AlpakkaVersion = "7.0.2"
  val AlpakkaKafkaVersion = "5.0.0"
  val AkkaDiagnosticsVersion = "2.1.0"

  val dependencies = List(
    "com.lightbend.akka" %% "akka-stream-alpakka-csv" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.lightbend.akka" %% "akka-diagnostics" % AkkaDiagnosticsVersion,
    // Used from Scala
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    // Used from Java
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.11.4",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.11.4",

    "org.testcontainers" % "kafka" % "1.14.3",
    
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
  // #dependencies
}
