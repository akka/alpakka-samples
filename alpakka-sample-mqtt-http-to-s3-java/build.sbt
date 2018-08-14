organization := "com.lightbend.akka.samples"
name := "alpakka-samples-mqtt-http-to-s3-java"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.14",
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "0.20",
  "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "0.20",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.9.6",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.9.6",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
