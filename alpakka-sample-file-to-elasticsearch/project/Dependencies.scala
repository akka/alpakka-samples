import sbt._

object Dependencies {
  val scalaVer = "2.13.8"
  // #deps
  val AkkaVersion = "2.6.19"
  val AlpakkaVersion = "4.0.0"
  val AkkaDiagnosticsVersion = "2.0.0-M3"
  // #deps
  val dependencies = List(
    // #deps
    "com.lightbend.akka" %% "akka-stream-alpakka-file" % AlpakkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    "com.lightbend.akka" %% "akka-diagnostics" % AkkaDiagnosticsVersion,
    // for JSON in Scala
    "io.spray" %% "spray-json" % "1.3.6",
    // for JSON in Java
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.13.4",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.13.4",
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.11",
    // #deps
    "org.testcontainers" % "elasticsearch" % "1.17.3"
  )
}
