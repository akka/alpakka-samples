
lazy val alpakka_sample_master = (project in file("."))
  .aggregate(
    common,
    step_001_HTTP_request,
    step_002_Extract_HTTP_entity,
    step_003_Parse_CSV,
    step_004_Producing_JSON,
    step_005_Cleanse_lines,
    step_006_Coordinated_shutdown,
    step_007_Almost_complete,
    step_008_Completed
 ).settings(CommonSettings.commonSettings: _*)

lazy val common = project.settings(CommonSettings.commonSettings: _*)

lazy val step_001_HTTP_request = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_002_Extract_HTTP_entity = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_003_Parse_CSV = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_004_Producing_JSON = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_005_Cleanse_lines = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_006_Coordinated_shutdown = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_007_Almost_complete = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_008_Completed = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")
       