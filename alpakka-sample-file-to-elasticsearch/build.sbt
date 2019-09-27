import sbt.Keys._
import sbt._

lazy val base = (project in file("."))
  .settings(
    name := "alpakka-sample-file-to-elasticsearch",
    organization := "samples",
    version := "1.3.0",
    scalaVersion := Dependencies.scalaVer,
    unmanagedSourceDirectories in Compile := List((scalaSource in Compile).value, (javaSource in Compile).value),
    unmanagedSourceDirectories in Test := List((scalaSource in Compile).value, (javaSource in Compile).value),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
    parallelExecution in Test := false,
    logBuffered in Test := false,
    parallelExecution in ThisBuild := false,
    parallelExecution in GlobalScope := false,
    fork in Test := true,
    libraryDependencies ++= Dependencies.dependencies
  )