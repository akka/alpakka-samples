organization := "com.lightbend.alpakka.samples"
name := "alpakka-samples-sqs-java"

val akkaVersion = "2.5.17"
val akkaHttpVersion = "10.1.5"
val alpakkaVersion = "1.0-M1"
val jacksonVersion = "2.9.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.lightbend.akka" %% "akka-stream-alpakka-sqs" % alpakkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
