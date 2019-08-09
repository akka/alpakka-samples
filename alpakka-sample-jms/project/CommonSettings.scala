import sbt.Keys._
import sbt._
import sbtstudent.AdditionalSettings

object CommonSettings {
  lazy val commonSettings = Seq(
    organization := "com.lightbend.training",
    version := "1.3.0",
    scalaVersion := Dependencies.scalaVer,
    scalacOptions ++= CompileOptions.compileOptions,
    unmanagedSourceDirectories in Compile := List((scalaSource in Compile).value, (javaSource in Compile).value),
    unmanagedSourceDirectories in Test := List((scalaSource in Compile).value, (javaSource in Compile).value),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-v"),
    parallelExecution in Test := false,
    logBuffered in Test := false,
    parallelExecution in ThisBuild := false,
    parallelExecution in GlobalScope := false,
    fork in Test := true,
    libraryDependencies ++= Dependencies.dependencies,
    // Having JBoss as a first resolver is a workaround for https://github.com/coursier/coursier/issues/200
    externalResolvers := ("jboss" at "http://repository.jboss.org/nexus/content/groups/public") +: externalResolvers.value
  ) ++
    AdditionalSettings.initialCmdsConsole ++
    AdditionalSettings.initialCmdsTestConsole ++
    AdditionalSettings.cmdAliases
}
