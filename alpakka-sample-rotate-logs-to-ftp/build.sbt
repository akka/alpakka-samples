organization := "com.lightbend.akka.samples"
name := "alpakka-sample-rotate-logs-to-ftp"
version := "0.0.1"
scalaVersion := Dependencies.scalaVer
resolvers += "Akka library repository".at("https://repo.akka.io/maven")
libraryDependencies ++= Dependencies.dependencies
javacOptions += "-Xlint:unchecked"
