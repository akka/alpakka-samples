addSbtPlugin("com.lightbend.akka" % "sbt-paradox-akka" % "0.53")
addSbtPlugin("com.github.sbt" % "sbt-site-paradox" % "1.5.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-publish-rsync" % "0.2")
resolvers += Resolver.jcenterRepo
resolvers += "Akka library repository".at("https://repo.akka.io/maven")

libraryDependencies += "org.scalameta" %% "scalameta" % "4.4.6"
