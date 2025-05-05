organization := "com.lightbend.akka.samples"
name := "alpakka-samples-mqtt-http-to-s3-java"

ThisBuild / scalaVersion := "2.13.15"

val AkkaVersion = "2.10.5"
val AkkaHttpVersion = "10.7.1"
val AkkaDiagnosticsVersion = "2.2.1"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "com.lightbend.akka" %% "akka-diagnostics" % AkkaDiagnosticsVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "3.0.4",
  "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "3.0.4",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.18.3",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.18.3",
  "ch.qos.logback" % "logback-classic" % "1.5.18"
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

javacOptions ++= Seq(
  "-Xlint:deprecation"
)
