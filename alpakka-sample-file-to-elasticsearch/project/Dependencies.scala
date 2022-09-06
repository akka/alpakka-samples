import sbt._

object Dependencies {
  val scalaVer = "2.13.8"
  // #deps
  val AkkaVersion = "2.6.19"
  val AlpakkaVersion = "4.0.0-M1"
  // #deps
  val dependencies = List(
    // #deps
    "com.lightbend.akka" %% "akka-stream-alpakka-file" % AlpakkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
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
