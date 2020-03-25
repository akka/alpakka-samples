organization := "com.lightbend.alpakka.samples"
name := "alpakka-samples-sqs-java"

ThisBuild / scalaVersion := "2.13.1"

val akkaVersion = "2.6.4"
val alpakkaVersion = "2.0.0-RC1"
val jacksonVersion = "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % alpakkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

javacOptions ++= Seq(
  "-Xlint:deprecation"
)
