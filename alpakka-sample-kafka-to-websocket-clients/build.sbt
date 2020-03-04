import sbt.Keys._

name := "alpakka-sample-kafka-to-websocket-clients"
organization := "samples"
version := "1.3.0"
scalaVersion := Dependencies.scalaVer
libraryDependencies ++= Dependencies.dependencies

fork in run := true
connectInput in run := true