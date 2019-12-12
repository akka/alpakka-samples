enablePlugins(AkkaParadoxPlugin, ParadoxSitePlugin, GhpagesPlugin)

name := "Alpakka Samples"
previewFixedPort := Some(8085)
scmInfo := Some(ScmInfo(url("https://github.com/akka/alpakka-samples"), "git@github.com:akka/alpakka-samples.git"))
homepage := Some(url("https://akka.io/alpakka-samples"))
description := "Example solutions for Enterprise Integrations using Alpakka and Reactive Streams."
version := {
  val time = java.time.LocalDateTime.now().withSecond(0).withNano(0)
  java.time.format.DateTimeFormatter.ISO_DATE.format(time) + " " + java.time.format.DateTimeFormatter.ISO_TIME.format(time)
}
isSnapshot := true

val FtpToFile = config("ftp-to-file")
ParadoxPlugin.paradoxSettings(FtpToFile)
ParadoxSitePlugin.paradoxSettings(FtpToFile)
AkkaParadoxPlugin.akkaParadoxSettings(FtpToFile)
FtpToFile / siteSubdirName := FtpToFile.name
FtpToFile / paradox / sourceDirectory := baseDirectory.value / ".." / s"alpakka-sample-${FtpToFile.name}" / "docs" / "src" / "main" / "paradox"
FtpToFile / paradoxProperties ++= Map(
  "project.url" -> s"${homepage.value.get}/${FtpToFile.name}",
  "canonical.base_url" -> s"${homepage.value.get}/${FtpToFile.name}",
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-${FtpToFile.name}",
  "github.root.base_dir" -> s"${baseDirectory.value}/..",
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.FtpToFile.AkkaVersion}",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.FtpToFile.AlpakkaVersion}/%s",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.FtpToFile.AkkaVersion}/%s",
)
FtpToFile / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))

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

val FileToElasticsearch = config("file-to-elasticsearch")
ParadoxPlugin.paradoxSettings(FileToElasticsearch)
ParadoxSitePlugin.paradoxSettings(FileToElasticsearch)
AkkaParadoxPlugin.akkaParadoxSettings(FileToElasticsearch)
FileToElasticsearch / siteSubdirName := FileToElasticsearch.name
FileToElasticsearch / paradox / sourceDirectory := baseDirectory.value / ".." / s"alpakka-sample-${FileToElasticsearch.name}" / "docs" / "src" / "main" / "paradox"
FileToElasticsearch / paradoxProperties ++= Map(
  "project.url" -> s"${homepage.value.get}/${FileToElasticsearch.name}",
  "canonical.base_url" -> s"${homepage.value.get}/${FileToElasticsearch.name}",
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-${FileToElasticsearch.name}",
  "github.root.base_dir" -> s"${baseDirectory.value}/..",
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.FileToElasticsearch.AkkaVersion}",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.FileToElasticsearch.AlpakkaVersion}/%s",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.FileToElasticsearch.AkkaVersion}/%s",
)
FileToElasticsearch / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))

val RotateLogsToFtp = config("rotate-logs-to-ftp")
ParadoxPlugin.paradoxSettings(RotateLogsToFtp)
ParadoxSitePlugin.paradoxSettings(RotateLogsToFtp)
AkkaParadoxPlugin.akkaParadoxSettings(RotateLogsToFtp)
RotateLogsToFtp / siteSubdirName := RotateLogsToFtp.name
RotateLogsToFtp / paradox / sourceDirectory := baseDirectory.value / ".." / s"alpakka-sample-${RotateLogsToFtp.name}" / "docs" / "src" / "main" / "paradox"
RotateLogsToFtp / paradoxProperties ++= Map(
  "project.url" -> s"${homepage.value.get}/${RotateLogsToFtp.name}",
  "canonical.base_url" -> s"${homepage.value.get}/${RotateLogsToFtp.name}",
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-${RotateLogsToFtp.name}",
  "github.root.base_dir" -> s"${baseDirectory.value}/..",
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.RotateLogsToFtp.AkkaVersion}",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.RotateLogsToFtp.AlpakkaVersion}/%s",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.RotateLogsToFtp.AkkaVersion}/%s",
)
RotateLogsToFtp / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))


Paradox / siteSubdirName := ""
paradoxProperties ++= Map(
  "extref.ftp-to-file.base_url" -> s"${(FtpToFile / siteSubdirName).value}/",
  "extref.http-csv-to-kafka.base_url" -> s"${(HttpCsvToKafka / siteSubdirName).value}/",
  "extref.jdbc-to-elasticsearch.base_url" -> s"${(JdbcToElasticsearch / siteSubdirName).value}/",
  "extref.jms.base_url" -> s"${(Jms / siteSubdirName).value}/",
  "extref.kafka-to-elasticsearch.base_url" -> s"${(KafkaToElasticsearch / siteSubdirName).value}/",
  "extref.file-to-elasticsearch.base_url" -> s"${(FileToElasticsearch / siteSubdirName).value}/",
  "extref.rotate-logs-to-ftp.base_url" -> s"${(RotateLogsToFtp / siteSubdirName).value}/",
)

resolvers += Resolver.jcenterRepo
git.remoteRepo := "git@github.com:akka/alpakka-samples.git"
ghpagesNoJekyll := true

