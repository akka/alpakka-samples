import sbt._

object Dependencies {
  val scalaVer = "2.13.15"
  // #deps
  val AkkaVersion = "2.10.5"
  val AkkaHttpVersion = "10.7.1"
  val AlpakkaVersion = "9.0.2"
  val AkkaDiagnosticsVersion = "2.2.1"

  // #deps

  val dependencies = List(
    // #deps
    "com.lightbend.akka" %% "akka-stream-alpakka-jms" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-http"   % AkkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    "com.lightbend.akka" %% "akka-diagnostics" % AkkaDiagnosticsVersion,
    // https://github.com/javaee/javax.jms
    "javax.jms" % "jms" % "1.1", // CDDL Version 1.1
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.5.18",
    // #deps
    // http://activemq.apache.org/download.html
    "org.apache.activemq" % "activemq-all" % "5.16.0" exclude("log4j", "log4j") exclude("org.slf4j", "slf4j-log4j12") // ApacheV2
  )
}
