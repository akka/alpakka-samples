organization := "com.lightbend.akka.samples"
name := "alpakka-samples-mqtt-http-to-s3-java"

val AkkaVersion = "2.6.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "1.1.2",
  "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "1.1.2",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.10.0",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

javacOptions ++= Seq(
  "-Xlint:deprecation"
)
