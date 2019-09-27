lazy val commonSettings = Seq(
  organization := "com.lightbend.akka",
  version := "0.0.1",
  scalaVersion := Dependencies.scalaVer,
  libraryDependencies ++= Dependencies.dependencies
)

lazy val base = (project in file("."))
  .aggregate(
    common,
    step_001_complete
 ).settings(commonSettings: _*)

lazy val common = project.settings(commonSettings: _*)

lazy val step_001_complete = project
  .settings(commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")
