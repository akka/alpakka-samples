import sbt.Keys._
import sbt._

object CommonSettings {
  lazy val commonSettings = Seq(
    organization := "com.lightbend.akka",
    version := "1.3.0",
    scalaVersion := Dependencies.scalaVer,
    scalacOptions ++= CompileOptions.compileOptions,
    Compile / unmanagedSourceDirectories := List((Compile / scalaSource).value, (Compile / javaSource).value),
    Test / unmanagedSourceDirectories := List((Compile / scalaSource).value, (Compile / javaSource).value),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
    Test / parallelExecution := false,
    Test / logBuffered := false,
    ThisBuild / parallelExecution := false,
    GlobalScope / parallelExecution := false,
    Test / fork := true,
    libraryDependencies ++= Dependencies.dependencies
  )
}
