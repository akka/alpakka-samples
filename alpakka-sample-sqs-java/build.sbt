organization := "com.lightbend.alpakka.samples"
name := "alpakka-samples-sqs-java"

val akkaVersion = "2.6.1"
val alpakkaVersion = "1.0.0"
val jacksonVersion = "2.9.8"

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
