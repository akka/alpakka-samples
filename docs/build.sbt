enablePlugins(AkkaParadoxPlugin, ParadoxSitePlugin, GhpagesPlugin)

name := "Alpakka Samples"
version := "current"
isSnapshot := false
previewFixedPort := Some(8085)

val HttpCsvToKafka = config("HttpCsvToKafka")
ParadoxPlugin.paradoxSettings(HttpCsvToKafka)
ParadoxSitePlugin.paradoxSettings(HttpCsvToKafka)
AkkaParadoxPlugin.akkaParadoxSettings(HttpCsvToKafka)
HttpCsvToKafka / siteSubdirName := "http-csv-to-kafka"
HttpCsvToKafka / paradox / sourceDirectory := baseDirectory.value / ".." / "alpakka-sample-http-csv-to-kafka" / "docs" / "src" / "main" / "paradox"
HttpCsvToKafka / paradoxProperties ++= Map(
  "snip.build.base_dir" -> s"${baseDirectory.value}/../alpakka-sample-http-csv-to-kafka",
  "scaladoc.akka.base_url" -> s"https://doc.akka.io/api/akka/${Dependencies.HttpCsvToKafka.AkkaVersion}",
  "extref.alpakka-kafka-docs.base_url" -> s"https://doc.akka.io/docs/alpakka-kafka/${Dependencies.HttpCsvToKafka.AlpakkaKafkaVersion}/%s",
  "extref.akka-http-docs.base_url" -> s"https://doc.akka.io/docs/akka-http/${Dependencies.HttpCsvToKafka.AkkaHttpVersion}/%s",
  "extref.alpakka-docs.base_url" -> s"https://doc.akka.io/docs/alpakka/${Dependencies.HttpCsvToKafka.AlpakkaVersion}/%s",
  "extref.akka-docs.base_url" -> s"https://doc.akka.io/docs/akka/${Dependencies.HttpCsvToKafka.AkkaVersion}/%s",
)
HttpCsvToKafka / paradoxGroups := Map("Language" -> Seq("Java", "Scala"))

Paradox / siteSubdirName := ""
paradoxProperties ++= Map(
  "project.url" -> "https://akka.io/alpakka-samples/",
  "canonical.base_url" -> "https://akka.io/alpakka-samples",
  "extref.http-csv-to-kafka.base_url" -> s"${(HttpCsvToKafka / siteSubdirName).value}/",
)

resolvers += Resolver.jcenterRepo
git.remoteRepo := "git@github.com:akka/alpakka-samples.git"
ghpagesNoJekyll := true
  
ghpagesCleanSite / excludeFilter := new FileFilter{
  def accept(f: File) = (ghpagesRepository.value / "http-csv-to-kafka").getCanonicalPath == f.getCanonicalPath
}
