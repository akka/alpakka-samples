organization := "com.lightbend.akka"
version := "1.3.0"
scalaVersion := Dependencies.scalaVer
resolvers += "Akka library repository".at("REPLACE_WITH_REPO_URL_FROM_https://account.akka.io/token")
libraryDependencies ++= Dependencies.dependencies
