organization := "com.lightbend.alpakka.samples"
name := "alpakka-samples-sqs-java"

ThisBuild / scalaVersion := "2.13.15"

val AkkaVersion = "2.10.5"
val AlpakkaVersion = "9.0.2"
val AkkaDiagnosticsVersion = "2.2.1"
val JacksonCoreVersion = "2.18.3"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % AlpakkaVersion,
  "com.lightbend.akka" %% "akka-diagnostics" % AkkaDiagnosticsVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % JacksonCoreVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % JacksonCoreVersion,
  "ch.qos.logback" % "logback-classic" % "1.5.18"
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

javacOptions ++= Seq(
  "-Xlint:deprecation"
)
