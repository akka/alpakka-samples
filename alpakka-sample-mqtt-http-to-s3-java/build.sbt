organization := "com.lightbend.akka.samples"
name := "alpakka-samples-mqtt-http-to-s3-java"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.21",
  "com.typesafe.akka" %% "akka-http" % "10.1.7",
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt" % "1.0-M3",
  "com.lightbend.akka" %% "akka-stream-alpakka-mqtt-streaming" % "1.0-M3",
  "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "1.0-M3",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % "2.9.8",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % "2.9.8",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))

javacOptions ++= Seq(
  "-Xlint:deprecation"
)
