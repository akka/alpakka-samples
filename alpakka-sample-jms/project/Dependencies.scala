import sbt._

object Dependencies {
  val scalaVer = "2.12.9"
  // #deps
  val AkkaVersion = "2.6.1"
  val AkkaHttpVersion = "10.1.10"
  val AlpakkaVersion = "1.1.0"

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
    "org.apache.activemq" % "activemq-all" % "5.15.9" exclude("log4j", "log4j") exclude("org.slf4j", "slf4j-log4j12") // ApacheV2
  )
}
