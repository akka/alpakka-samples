import sbt._

object Dependencies {
  val scalaVer = "2.12.8"
  val ScalaTestVersion = "3.0.6"
  val AkkaVersion = "2.5.21"
  val AkkaHttpVersion = "10.1.8"
  val AlpakkaVersion = "1.0.0"
  val AlpakkaKafkaVersion = "1.0.3"

  val dependencies = List(
    "com.lightbend.akka" %% "akka-stream-alpakka-csv" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,

    "org.testcontainers" % "kafka" % "1.11.1",
    
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",

    "org.scalatest" %% "scalatest" % ScalaTestVersion % Test
  )
}
