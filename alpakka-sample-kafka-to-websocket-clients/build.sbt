import sbt.Keys._

name := "alpakka-sample-kafka-to-websocket-clients"
organization := "com.lightbend.akka"
version := "1.3.0"
scalaVersion := Dependencies.scalaVer
resolvers += "Akka library repository".at("https://repo.akka.io/maven")
libraryDependencies ++= Dependencies.dependencies

fork / run := true
connectInput / run := true
