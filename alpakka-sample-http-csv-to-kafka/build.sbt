
lazy val alpakka_sample_master = (project in file("."))
  .aggregate(
    common,
    step_001_http_request,
    step_002_extract_http_entity,
    step_003_parse_csv,
    step_004_producing_json,
    step_005_cleanse_lines,
    step_006_coordinated_shutdown,
    step_007_produce_to_kafka,
    step_008_scheduled_download
 ).settings(CommonSettings.commonSettings: _*)

lazy val common = project.settings(CommonSettings.commonSettings: _*)

lazy val step_001_http_request = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_002_extract_http_entity = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_003_parse_csv = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_004_producing_json = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_005_cleanse_lines = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_006_coordinated_shutdown = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_007_produce_to_kafka = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")

lazy val step_008_scheduled_download = project
  .settings(CommonSettings.commonSettings: _*)
  .dependsOn(common % "test->test;compile->compile")
