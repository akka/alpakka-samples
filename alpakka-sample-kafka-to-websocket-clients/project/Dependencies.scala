import sbt._

object Dependencies {
  val scalaVer = "2.13.18"
  // #deps
  val AkkaVersion = "2.11.0"
  val AkkaHttpVersion = "10.8.0"
  val AlpakkaKafkaVersion = "9.0.0"
  val AkkaDiagnosticsVersion = "2.3.0"
  // #deps
  val dependencies = List(
    // #deps
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
    "com.lightbend.akka" %% "akka-diagnostics" % AkkaDiagnosticsVersion,

    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.5.18",
    // #deps

    "org.testcontainers" % "kafka" % "1.17.5",

    "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
    "com.google.guava" % "guava" % "28.2-jre" % Test,
    "junit" % "junit" % "4.13" % Test,

  )
}
