addSbtPlugin("com.lightbend.akka" % "sbt-paradox-akka" % "0.18")
// has following PRs merged in:
// * https://github.com/sbt/sbt-site/pull/141
// * https://github.com/sbt/sbt-site/pull/139
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.2+24-b76fdbbe")
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")
resolvers += Resolver.jcenterRepo
