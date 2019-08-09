enablePlugins(AkkaParadoxPlugin, ParadoxSitePlugin, GhpagesPlugin)

name := "Alpakka Samples"
previewFixedPort := Some(8085)
scmInfo := Some(ScmInfo(url("https://github.com/akka/alpakka-samples"), "git@github.com:akka/alpakka-samples.git"))
homepage := Some(url("https://akka.io/alpakka-samples"))
description := "Example solutions for Enterprise Integrations using Alpakka and Reactive Streams."

val HttpCsvToKafka = config("http-csv-to-kafka")
ParadoxPlugin.paradoxSettings(HttpCsvToKafka)
ParadoxSitePlugin.paradoxSettings(HttpCsvToKafka)
AkkaParadoxPlugin.akkaParadoxSettings(HttpCsvToKafka)
HttpCsvToKafka / siteSubdirName := HttpCsvToKafka.name
HttpCsvToKafka / paradox / sourceDirectory := baseDirectory.value / ".." / s"alpakka-sample-${HttpCsvToKafka.name}" / "docs" / "src" / "main" / "paradox"
HttpCsvToKafka / paradoxProperties ++= Map(
  "project.url" -> s"${homepage.value.get}/${HttpCsvToKafka.name}",
  "canonical.base_url" -> s"${homepage.value.get}/${HttpCsvToKafka.name}",
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-${HttpCsvToKafka.name}",
  "github.root.base_dir" -> s"${baseDirectory.value}/..",
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.HttpCsvToKafka.AkkaVersion}",
  "extref.alpakka-kafka.base_url" -> s"https://doc.akka.io/docs/alpakka-kafka/${Dependencies.HttpCsvToKafka.AlpakkaKafkaVersion}/%s",
  "extref.akka-http.base_url" -> s"https://doc.akka.io/docs/akka-http/${Dependencies.HttpCsvToKafka.AkkaHttpVersion}/%s",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.HttpCsvToKafka.AlpakkaVersion}/%s",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.HttpCsvToKafka.AkkaVersion}/%s",
)
HttpCsvToKafka / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))

val JdbcToElasticsearch = config("jdbc-to-elasticsearch")
ParadoxPlugin.paradoxSettings(JdbcToElasticsearch)
ParadoxSitePlugin.paradoxSettings(JdbcToElasticsearch)
AkkaParadoxPlugin.akkaParadoxSettings(JdbcToElasticsearch)
JdbcToElasticsearch / siteSubdirName := JdbcToElasticsearch.name
JdbcToElasticsearch / paradox / sourceDirectory := baseDirectory.value / ".." / s"alpakka-sample-${JdbcToElasticsearch.name}" / "docs" / "src" / "main" / "paradox"
JdbcToElasticsearch / paradoxProperties ++= Map(
  "project.url" -> s"${homepage.value.get}/${JdbcToElasticsearch.name}",
  "canonical.base_url" -> s"${homepage.value.get}/${JdbcToElasticsearch.name}",
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-${JdbcToElasticsearch.name}",
  "github.root.base_dir" -> s"${baseDirectory.value}/..",
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.JdbcToElasticsearch.AkkaVersion}",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.JdbcToElasticsearch.AlpakkaVersion}/%s",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.JdbcToElasticsearch.AkkaVersion}/%s",
)
JdbcToElasticsearch / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))

val Jms = config("jms")
ParadoxPlugin.paradoxSettings(Jms)
ParadoxSitePlugin.paradoxSettings(Jms)
AkkaParadoxPlugin.akkaParadoxSettings(Jms)
Jms / siteSubdirName := Jms.name
Jms / paradox / sourceDirectory := baseDirectory.value / ".." / s"alpakka-sample-${Jms.name}" / "docs" / "src" / "main" / "paradox"
Jms / paradoxProperties ++= Map(
  "project.url" -> s"${homepage.value.get}/${Jms.name}",
  "canonical.base_url" -> s"${homepage.value.get}/${Jms.name}",
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-${Jms.name}",
  "github.root.base_dir" -> s"${baseDirectory.value}/..",
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.Jms.AkkaVersion}",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.Jms.AlpakkaVersion}/%s",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.Jms.AkkaVersion}/%s",
  "extref.akka-http.base_url" -> s"https://doc.akka.io/docs/akka-http/${Dependencies.Jms.AkkaHttpVersion}/%s",
)
Jms / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))


val KafkaToElasticsearch = config("kafka-to-elasticsearch")
ParadoxPlugin.paradoxSettings(KafkaToElasticsearch)
ParadoxSitePlugin.paradoxSettings(KafkaToElasticsearch)
AkkaParadoxPlugin.akkaParadoxSettings(KafkaToElasticsearch)
KafkaToElasticsearch / siteSubdirName := KafkaToElasticsearch.name
KafkaToElasticsearch / paradox / sourceDirectory := baseDirectory.value / ".." / s"alpakka-sample-${KafkaToElasticsearch.name}" / "docs" / "src" / "main" / "paradox"
KafkaToElasticsearch / paradoxProperties ++= Map(
  "project.url" -> s"${homepage.value.get}/${KafkaToElasticsearch.name}",
  "canonical.base_url" -> s"${homepage.value.get}/${KafkaToElasticsearch.name}",
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-${KafkaToElasticsearch.name}",
  "github.root.base_dir" -> s"${baseDirectory.value}/..",
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.KafkaToElasticsearch.AkkaVersion}",
  "extref.alpakka-kafka.base_url" -> s"https://doc.akka.io/docs/alpakka-kafka/${Dependencies.KafkaToElasticsearch.AlpakkaKafkaVersion}/%s",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.KafkaToElasticsearch.AlpakkaVersion}/%s",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.KafkaToElasticsearch.AkkaVersion}/%s",
)
KafkaToElasticsearch / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))


Paradox / siteSubdirName := ""
paradoxProperties ++= Map(
  "extref.http-csv-to-kafka.base_url" -> s"${(HttpCsvToKafka / siteSubdirName).value}/",
  "extref.jdbc-to-elasticsearch.base_url" -> s"${(JdbcToElasticsearch / siteSubdirName).value}/",
  "extref.jms.base_url" -> s"${(Jms / siteSubdirName).value}/",
  "extref.kafka-to-elasticsearch.base_url" -> s"${(KafkaToElasticsearch / siteSubdirName).value}/",
)

resolvers += Resolver.jcenterRepo
git.remoteRepo := "git@github.com:akka/alpakka-samples.git"
ghpagesNoJekyll := true

