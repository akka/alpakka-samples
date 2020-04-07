import sbt._

object Dependencies {
  val scalaVer = "2.13.1"
  // #deps
  val AkkaVersion = "2.6.4"
  val AlpakkaVersion = "2.0.0-RC2"

  // #deps

  val dependencies = List(
  // #deps
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-slick" % AlpakkaVersion,
    // for JSON in Scala
    "io.spray" %% "spray-json" % "1.3.5",
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
  // #deps
    "com.h2database" % "h2" % "1.4.200",
    "org.testcontainers" % "elasticsearch" % "1.13.0",
    "org.testcontainers" % "postgresql" % "1.13.0"
  )
}
