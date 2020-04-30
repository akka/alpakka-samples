addSbtPlugin("com.lightbend.akka" % "sbt-paradox-akka" % "0.33")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-publish-rsync" % "0.2")
resolvers += Resolver.jcenterRepo

libraryDependencies += "org.scalameta" %% "scalameta" % "4.3.7"
