import sbt._

object Dependencies {
  val scalaVer = "2.13.15"
  // #dependencies
  val ScalaTestVersion = "3.1.4"
  val AkkaVersion = "2.10.5"
  val AkkaHttpVersion = "10.7.1"
  val AlpakkaVersion = "9.0.2"
  val AlpakkaKafkaVersion = "7.0.2"
  val AkkaDiagnosticsVersion = "2.2.1"

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
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.18.3",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.18.3",

    "org.testcontainers" % "kafka" % "1.14.3",
    
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
  // #dependencies
}
