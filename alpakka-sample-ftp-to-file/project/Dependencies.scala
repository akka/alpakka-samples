import sbt._

object Dependencies {
  val scalaVer = "2.13.12"
  // #deps
  val AkkaVersion = "2.10.0"
  val AlpakkaVersion = "9.0.0"
  val AkkaDiagnosticsVersion = "2.2.0"

  // #deps

  val dependencies = List(
  // #deps
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-ftp" % AlpakkaVersion,
    "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    "com.lightbend.akka" %% "akka-diagnostics" % AkkaDiagnosticsVersion,
    // #deps
    // Playground file system and FTP server
    // https://mina.apache.org/ftpserver-project/downloads.html
    "org.apache.ftpserver" % "ftpserver-core" % "1.1.1", // ApacheV2
    "com.google.jimfs" % "jimfs" % "1.1", // ApacheV2
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
}
