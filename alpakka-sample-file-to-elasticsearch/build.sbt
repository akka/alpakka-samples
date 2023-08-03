import sbt.Keys._

name := "alpakka-sample-file-to-elasticsearch"
organization := "samples"
version := "1.3.0"
scalaVersion := Dependencies.scalaVer
resolvers += "Akka library repository".at("https://repo.akka.io/maven")
libraryDependencies ++= Dependencies.dependencies