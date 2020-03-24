organization := "com.lightbend.akka.samples"
name := "alpakka-samples-mqtt-http-to-s3-java"

ThisBuild / scalaVersion := "2.13.1"

val AkkaVersion = "2.6.4"
val AkkaHttpVersion = "10.1.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "2.0.0-RC1",
  "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "2.0.0-RC1",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.10.3",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.10.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

javacOptions ++= Seq(
  "-Xlint:deprecation"
)
