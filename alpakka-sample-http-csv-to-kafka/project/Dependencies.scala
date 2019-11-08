import sbt._

object Dependencies {
  val scalaVer = "2.12.10"
  // #dependencies
  val ScalaTestVersion = "3.0.6"
  val AkkaVersion = "2.6.0"
  val AkkaHttpVersion = "10.1.10"
  val AlpakkaVersion = "1.1.2"
  val AlpakkaKafkaVersion = "1.1.0"

  val dependencies = List(
    "com.lightbend.akka" %% "akka-stream-alpakka-csv" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    // Used from Scala
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
    // Used from Java
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.10.0",
    "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.0",

    "org.testcontainers" % "kafka" % "1.12.3",
    
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
  // #dependencies
}
