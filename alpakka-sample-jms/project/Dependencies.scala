import sbt._

object Dependencies {
  val scalaVer = "2.13.3"
  // #deps
  val AkkaVersion = "2.6.8"
  val AkkaHttpVersion = "10.1.12"
  val AlpakkaVersion = "2.0.1"

  // #deps

  val dependencies = List(
    // #deps
    "com.lightbend.akka" %% "akka-stream-alpakka-jms" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-http"   % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    // https://github.com/javaee/javax.jms
    "javax.jms" % "jms" % "1.1", // CDDL Version 1.1
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    // #deps
    // http://activemq.apache.org/download.html
    "org.apache.activemq" % "activemq-all" % "5.16.0" exclude("log4j", "log4j") exclude("org.slf4j", "slf4j-log4j12") // ApacheV2
  )
}
