import sbt.Keys.isSnapshot

lazy val docs = project
  .enablePlugins(AkkaParadoxPlugin, ParadoxSitePlugin, GhpagesPlugin)
  .settings(
    name := "Alpakka Samples",
    version := "current",
    isSnapshot := false,
    previewFixedPort := Some(8085),
    Paradox / sourceDirectory := sourceDirectory.value / "main",
    Paradox / siteSubdirName := "http-csv-to-kafka",
    Paradox / paradoxProperties ++= Map(
      "akka.version" -> Dependencies.AkkaVersion,
      "akka-http.version" -> Dependencies.AkkaHttpVersion,
      "extref.alpakka-docs.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.AlpakkaVersion}/%s",
      "extref.alpakka-kafka-docs.base_url" -> s"https://doc.akka.io/docs/alpakka-kafka/${Dependencies.AlpakkaKafkaVersion}/%s",
      "extref.akka-docs.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.AkkaVersion}/%s",
      "extref.akka-http-docs.base_url" -> s"https://doc.akka.io/docs/akka-http/${Dependencies.AkkaHttpVersion}/%s",
      "extref.java-api.base_url" -> "https://docs.oracle.com/javase/8/docs/api/index.html?%s.html",
      "extref.javaee-api.base_url" -> "https://docs.oracle.com/javaee/7/api/index.html?%s.html",
      "javadoc.base_url" -> "https://docs.oracle.com/javase/8/docs/api/",
      "javadoc.javax.jms.base_url" -> "https://docs.oracle.com/javaee/7/api/",
      "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.AkkaVersion}/",
      "javadoc.akka.http.base_url" -> s"https://doc.akka.io/japi/akka-http/${Dependencies.AkkaHttpVersion}/",
      "scaladoc.scala.base_url" -> s"https://www.scala-lang.org/api/current/",
      "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.AkkaVersion}",
      "scaladoc.akka.http.base_url" -> s"https://doc.akka.io/api/akka-http/${Dependencies.AkkaHttpVersion}/",
      "scaladoc.akka.stream.alpakka.base_url" ->  s"https://doc.akka.io/api/alpakka/${Dependencies.AlpakkaVersion}",
      "scaladoc.akka.kafka.base_url" ->  s"https://doc.akka.io/api/alpakka-kafka/${Dependencies.AlpakkaKafkaVersion}",
    ),
    Paradox / paradoxGroups := Map("Language" -> Seq("Java", "Scala")),
    resolvers += Resolver.jcenterRepo,
    git.remoteRepo := "git@github.com:akka/alpakka-samples.git",
    ghpagesNoJekyll := true
  )
