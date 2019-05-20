enablePlugins(AkkaParadoxPlugin, ParadoxSitePlugin, GhpagesPlugin)

name := "Alpakka Samples"
version := "current"
isSnapshot := false
previewFixedPort := Some(8085)
Paradox / sourceDirectory := sourceDirectory.value / "main"
Paradox / siteSubdirName := ""
Paradox / paradoxProperties ++= Map(
  "project.url" -> "https://akka.io/alpakka-samples/",
  "canonical.base_url" -> "https://akka.io/alpakka-samples",
)
resolvers += Resolver.jcenterRepo
git.remoteRepo := "git@github.com:akka/alpakka-samples.git"
ghpagesNoJekyll := true
  
ghpagesCleanSite / excludeFilter := new FileFilter{
  def accept(f: File) = (ghpagesRepository.value / "http-csv-to-kafka").getCanonicalPath == f.getCanonicalPath
}
