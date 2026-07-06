addSbtPlugin("com.lightbend.akka" % "sbt-paradox-akka" % "0.55")
addSbtPlugin("com.github.sbt" % "sbt-site-paradox" % "1.5.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-publish-rsync" % "0.2")
resolvers += Resolver.jcenterRepo
resolvers += "Akka library repository".at("REPLACE_WITH_REPO_URL_FROM_https://account.akka.io/token")

libraryDependencies += "org.scalameta" %% "scalameta" % "4.4.35"
