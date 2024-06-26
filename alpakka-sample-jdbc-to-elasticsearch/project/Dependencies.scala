import sbt._

object Dependencies {
  val scalaVer = "2.13.12"
  // #deps
  val AkkaVersion = "2.9.0"
  val AlpakkaVersion = "7.0.2"
  val AkkaDiagnosticsVersion = "2.1.0"
  
  // #deps

  val dependencies = List(
  // #deps
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-slick" % AlpakkaVersion,
    "com.lightbend.akka" %% "akka-diagnostics" % AkkaDiagnosticsVersion,
    // for JSON in Scala
    "io.spray" %% "spray-json" % "1.3.6",
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.11",
  // #deps
    "com.h2database" % "h2" % "2.1.214",
    "org.testcontainers" % "elasticsearch" % "1.17.3",
    "org.testcontainers" % "postgresql" % "1.17.3"
  )
}
