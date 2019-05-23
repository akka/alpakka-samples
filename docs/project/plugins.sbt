addSbtPlugin("com.lightbend.akka" % "sbt-paradox-akka" % "0.18")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.4.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")
resolvers += Resolver.jcenterRepo

libraryDependencies += "org.scalameta" %% "scalameta" % "4.1.0"
