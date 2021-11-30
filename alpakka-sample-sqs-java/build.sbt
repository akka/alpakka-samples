organization := "com.lightbend.alpakka.samples"
name := "alpakka-samples-sqs-java"

ThisBuild / scalaVersion := "2.13.7"

val AkkaVersion = "2.6.14"
val AlpakkaVersion = "3.0.4"
val jacksonVersion = "2.11.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % AlpakkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

javacOptions ++= Seq(
  "-Xlint:deprecation"
)
