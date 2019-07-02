import sbt._

object Dependencies {
  val scalaVer = "2.12.8"
  // #deps
  val AkkaVersion = "2.5.23"
  val AlpakkaVersion = "1.0.2"

  // #deps

  val dependencies = List(
  // #deps
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-slick" % AlpakkaVersion,
    // for JSON in Scala
    "io.spray" %% "spray-json" % "1.3.5",
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
  // #deps
    "com.h2database" % "h2" % "1.4.197",
    "org.testcontainers" % "elasticsearch" % "1.11.3",
    "org.testcontainers" % "postgresql" % "1.11.3"
  )
}
