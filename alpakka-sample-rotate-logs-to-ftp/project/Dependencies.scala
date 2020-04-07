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
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-file" % AlpakkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-ftp" % AlpakkaVersion,
    // #deps
    // Playground file system and FTP server
    // https://mina.apache.org/ftpserver-project/downloads.html
    "org.apache.ftpserver" % "ftpserver-core" % "1.1.1", // ApacheV2
    "org.apache.sshd" % "sshd-scp" % "2.4.0", // ApacheV2
    "org.apache.sshd" % "sshd-sftp" % "2.4.0", // ApacheV2
    "com.google.jimfs" % "jimfs" % "1.1", // ApacheV2
    // Logging
    "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
}
