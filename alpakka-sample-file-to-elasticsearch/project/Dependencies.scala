import sbt._

object Dependencies {
  val scalaVer = "2.13.1"
  // #deps
  val AkkaVersion = "2.5.25"
  val AlpakkaVersion = "1.1.1"
  // #deps
  val dependencies = List(
    // #deps
    "com.lightbend.akka" %% "akka-stream-alpakka-file" % AlpakkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
    // for JSON in Scala
    "io.spray" %% "spray-json" % "1.3.5",
    // for JSON in Java
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.10.0",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.0",
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    // #deps
    "org.testcontainers" % "elasticsearch" % "1.12.2"
  )
}
