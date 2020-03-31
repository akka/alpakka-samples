addSbtPlugin("com.lightbend.akka" % "sbt-paradox-akka" % "0.32")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-publish-rsync" % "0.1")
resolvers += Resolver.jcenterRepo

libraryDependencies += "org.scalameta" %% "scalameta" % "4.3.7"
