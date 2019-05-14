lazy val docs = project
  .enablePlugins(AkkaParadoxPlugin)
  .settings(
    name := "Alpakka",
//    sourceDirectory := sourceDirectory.value / "main",
    paradoxProperties ++= Map(
      "akka.version" -> Dependencies.AkkaVersion,
      "akka-http.version" -> Dependencies.AkkaHttpVersion,
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
    ),
    paradoxGroups := Map("Language" -> Seq("Java", "Scala")),
    resolvers += Resolver.jcenterRepo,
  )
