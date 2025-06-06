
ThisBuild / scalaVersion := "2.13.15"

enablePlugins(AkkaParadoxPlugin, ParadoxSitePlugin, PublishRsyncPlugin)

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
  // Alpakka
  "scaladoc.akka.stream.alpakka.base_url" -> s"https://doc.akka.io/api/alpakka/${Dependencies.Jms.AlpakkaVersion}",
  "javadoc.akka.base_url" -> "",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.FtpToFile.AlpakkaVersion}/%s",
  // Akka
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.FtpToFile.AkkaVersion}",
  "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.FtpToFile.AkkaVersion}",
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
  // Alpakka
  "scaladoc.akka.stream.alpakka.base_url" -> s"https://doc.akka.io/api/alpakka/${Dependencies.HttpCsvToKafka.AlpakkaVersion}",
  "javadoc.akka.base_url" -> "",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.HttpCsvToKafka.AlpakkaVersion}/%s",
  // Alpakka Kafka
  "scaladoc.akka.kafka.base_url" -> s"https://doc.akka.io/api/alpakka-kafka/${Dependencies.HttpCsvToKafka.AlpakkaKafkaVersion}",
  "javadoc.akka.kafka.base_url" -> "",
  "extref.alpakka-kafka.base_url" -> s"https://doc.akka.io/docs/alpakka-kafka/${Dependencies.HttpCsvToKafka.AlpakkaKafkaVersion}/%s",
  // Akka
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.HttpCsvToKafka.AkkaVersion}",
  "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.HttpCsvToKafka.AkkaVersion}",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.HttpCsvToKafka.AkkaVersion}/%s",
  // Akka HTTP
  "scaladoc.akka.http.base_url" -> s"https://doc.akka.io/api/akka-http/${Dependencies.HttpCsvToKafka.AkkaHttpVersion}",
  "javadoc.akka.http.base_url" -> s"https://doc.akka.io/japi/akka-http/${Dependencies.HttpCsvToKafka.AkkaHttpVersion}",
  "extref.akka-http.base_url" -> s"https://doc.akka.io/docs/akka-http/${Dependencies.HttpCsvToKafka.AkkaHttpVersion}/%s",
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
  // Alpakka
  "scaladoc.akka.stream.alpakka.base_url" -> s"https://doc.akka.io/api/alpakka/${Dependencies.JdbcToElasticsearch.AlpakkaVersion}",
  "javadoc.akka.base_url" -> "",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.JdbcToElasticsearch.AlpakkaVersion}/%s",
  // Akka
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.JdbcToElasticsearch.AkkaVersion}",
  "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.JdbcToElasticsearch.AkkaVersion}",
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
  // Alpakka
  "scaladoc.akka.stream.alpakka.base_url" -> s"https://doc.akka.io/api/alpakka/${Dependencies.Jms.AlpakkaVersion}",
  "javadoc.akka.base_url" -> "",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.Jms.AlpakkaVersion}/%s",
  // Akka
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.Jms.AkkaVersion}",
  "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.Jms.AkkaVersion}",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.Jms.AkkaVersion}/%s",
  // Akka HTTP
  "scaladoc.akka.http.base_url" -> s"https://doc.akka.io/api/akka-http/${Dependencies.Jms.AkkaHttpVersion}",
  "javadoc.akka.http.base_url" -> s"https://doc.akka.io/japi/akka-http/${Dependencies.Jms.AkkaHttpVersion}",
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
  // Alpakka
  "scaladoc.akka.stream.alpakka.base_url" -> s"https://doc.akka.io/api/alpakka/${Dependencies.KafkaToElasticsearch.AlpakkaVersion}",
  "javadoc.akka.base_url" -> "",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.KafkaToElasticsearch.AlpakkaVersion}/%s",
  // Alpakka Kafka
  "scaladoc.akka.kafka.base_url" -> s"https://doc.akka.io/api/alpakka-kafka/${Dependencies.KafkaToElasticsearch.AlpakkaKafkaVersion}",
  "javadoc.akka.kafka.base_url" -> "",
  "extref.alpakka-kafka.base_url" -> s"https://doc.akka.io/docs/alpakka-kafka/${Dependencies.KafkaToElasticsearch.AlpakkaKafkaVersion}/%s",
  // Akka
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.KafkaToElasticsearch.AkkaVersion}",
  "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.KafkaToElasticsearch.AkkaVersion}",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.KafkaToElasticsearch.AkkaVersion}/%s",
)
KafkaToElasticsearch / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))

val KafkaToWebsocketClients = config("kafka-to-websocket-clients")
ParadoxPlugin.paradoxSettings(KafkaToWebsocketClients)
ParadoxSitePlugin.paradoxSettings(KafkaToWebsocketClients)
AkkaParadoxPlugin.akkaParadoxSettings(KafkaToWebsocketClients)
KafkaToWebsocketClients / siteSubdirName := KafkaToWebsocketClients.name
KafkaToWebsocketClients / paradox / sourceDirectory := baseDirectory.value / ".." / s"alpakka-sample-${KafkaToWebsocketClients.name}" / "docs" / "src" / "main" / "paradox"
KafkaToWebsocketClients / paradoxProperties ++= Map(
  "project.url" -> s"${homepage.value.get}/${KafkaToWebsocketClients.name}",
  "canonical.base_url" -> s"${homepage.value.get}/${KafkaToWebsocketClients.name}",
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-${KafkaToWebsocketClients.name}",
  "github.root.base_dir" -> s"${baseDirectory.value}/..",
  // Alpakka
//  "scaladoc.akka.stream.alpakka.base_url" -> s"https://doc.akka.io/api/alpakka/${Dependencies.KafkaToWebsocketClients.AlpakkaVersion}",
//  "javadoc.akka.base_url" -> "",
//  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.KafkaToWebsocketClients.AlpakkaVersion}/%s",
  // Alpakka Kafka
  "scaladoc.akka.kafka.base_url" -> s"https://doc.akka.io/api/alpakka-kafka/${Dependencies.KafkaToWebsocketClients.AlpakkaKafkaVersion}",
  "javadoc.akka.kafka.base_url" -> "",
  "extref.alpakka-kafka.base_url" -> s"https://doc.akka.io/docs/alpakka-kafka/${Dependencies.KafkaToWebsocketClients.AlpakkaKafkaVersion}/%s",
  // Akka
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.KafkaToWebsocketClients.AkkaVersion}",
  "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.KafkaToWebsocketClients.AkkaVersion}",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.KafkaToWebsocketClients.AkkaVersion}/%s",
  // Akka HTTP
  "scaladoc.akka.http.base_url" -> s"https://doc.akka.io/api/akka-http/${Dependencies.KafkaToWebsocketClients.AkkaHttpVersion}",
  "javadoc.akka.http.base_url" -> s"https://doc.akka.io/japi/akka-http/${Dependencies.KafkaToWebsocketClients.AkkaHttpVersion}",
  "extref.akka-http.base_url" -> s"https://doc.akka.io/docs/akka-http/${Dependencies.KafkaToWebsocketClients.AkkaHttpVersion}/%s",
)
KafkaToWebsocketClients / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))

val MqttToKafka = config("mqtt-to-kafka")
ParadoxPlugin.paradoxSettings(MqttToKafka)
ParadoxSitePlugin.paradoxSettings(MqttToKafka)
AkkaParadoxPlugin.akkaParadoxSettings(MqttToKafka)
MqttToKafka / siteSubdirName := MqttToKafka.name
MqttToKafka / paradox / sourceDirectory := baseDirectory.value / ".." / s"alpakka-sample-${MqttToKafka.name}" / "docs" / "src" / "main" / "paradox"
MqttToKafka / paradoxProperties ++= Map(
  "project.url" -> s"${homepage.value.get}/${MqttToKafka.name}",
  "canonical.base_url" -> s"${homepage.value.get}/${MqttToKafka.name}",
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-${MqttToKafka.name}",
  "github.root.base_dir" -> s"${baseDirectory.value}/..",
  // Alpakka
  "scaladoc.akka.stream.alpakka.base_url" -> s"https://doc.akka.io/api/alpakka/${Dependencies.MqttToKafka.AlpakkaVersion}",
  "javadoc.akka.base_url" -> "",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.MqttToKafka.AlpakkaVersion}/%s",
  // Alpakka Kafka
  "scaladoc.akka.kafka.base_url" -> s"https://doc.akka.io/api/alpakka-kafka/${Dependencies.MqttToKafka.AlpakkaKafkaVersion}",
  "javadoc.akka.kafka.base_url" -> "",
  "extref.alpakka-kafka.base_url" -> s"https://doc.akka.io/docs/alpakka-kafka/${Dependencies.MqttToKafka.AlpakkaKafkaVersion}/%s",
  // Akka
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.MqttToKafka.AkkaVersion}",
  "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.MqttToKafka.AkkaVersion}",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.MqttToKafka.AkkaVersion}/%s",
)
MqttToKafka / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))


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
  // Alpakka
  "scaladoc.akka.stream.alpakka.base_url" -> s"https://doc.akka.io/api/alpakka/${Dependencies.FileToElasticsearch.AlpakkaVersion}",
  "javadoc.akka.base_url" -> "",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.FileToElasticsearch.AlpakkaVersion}/%s",
  // Akka
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.FileToElasticsearch.AkkaVersion}",
  "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.FileToElasticsearch.AkkaVersion}",
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
  // Alpakka
  "scaladoc.akka.stream.alpakka.base_url" -> s"https://doc.akka.io/api/alpakka/${Dependencies.RotateLogsToFtp.AlpakkaVersion}",
  "javadoc.akka.base_url" -> "",
  "extref.alpakka.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.RotateLogsToFtp.AlpakkaVersion}/%s",
  // Akka
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.RotateLogsToFtp.AkkaVersion}",
  "javadoc.akka.base_url" -> s"https://doc.akka.io/japi/akka/${Dependencies.RotateLogsToFtp.AkkaVersion}",
  "extref.akka.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.RotateLogsToFtp.AkkaVersion}/%s",
)
RotateLogsToFtp / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))


Paradox / siteSubdirName := ""
paradoxProperties ++= Map(
  "extref.akka.base_url" -> "https://doc.akka.io/docs/akka/current/",
  "extref.alpakka.base_url" -> "https://doc.akka.io/docs/alpakka/current/",
  "extref.alpakka-kafka.base_url" -> "https://doc.akka.io/docs/alpakka-kafka/current/",
  "extref.ftp-to-file.base_url" -> s"${(FtpToFile / siteSubdirName).value}/",
  "extref.http-csv-to-kafka.base_url" -> s"${(HttpCsvToKafka / siteSubdirName).value}/",
  "extref.jdbc-to-elasticsearch.base_url" -> s"${(JdbcToElasticsearch / siteSubdirName).value}/",
  "extref.jms.base_url" -> s"${(Jms / siteSubdirName).value}/",
  "extref.kafka-to-elasticsearch.base_url" -> s"${(KafkaToElasticsearch / siteSubdirName).value}/",
  "extref.kafka-to-websocket-clients.base_url" -> s"${(KafkaToWebsocketClients / siteSubdirName).value}/",
  "extref.mqtt-to-kafka.base_url" -> s"${(MqttToKafka / siteSubdirName).value}/",
  "extref.file-to-elasticsearch.base_url" -> s"${(FileToElasticsearch / siteSubdirName).value}/",
  "extref.rotate-logs-to-ftp.base_url" -> s"${(RotateLogsToFtp / siteSubdirName).value}/",
)

resolvers += Resolver.jcenterRepo

publishRsyncArtifacts += makeSite.value -> "akka.io/alpakka-samples/"
publishRsyncHost := "akkarepo@gustav.akka.io"
