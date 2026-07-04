organization := "com.lightbend.akka.samples"
name := "alpakka-sample-rotate-logs-to-ftp"
version := "0.0.1"
scalaVersion := Dependencies.scalaVer
resolvers += "Akka library repository".at("REPLACE_WITH_REPO_URL_FROM_https://account.akka.io/token")
libraryDependencies ++= Dependencies.dependencies
javacOptions += "-Xlint:unchecked"
