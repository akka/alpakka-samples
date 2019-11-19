organization := "com.lightbend.akka.samples"
version := "1.3.0"
scalaVersion := Dependencies.scalaVer
libraryDependencies ++= Dependencies.dependencies
// Having JBoss as a first resolver is a workaround for https://github.com/coursier/coursier/issues/200
externalResolvers := ("jboss" at "http://repository.jboss.org/nexus/content/groups/public") +: externalResolvers.value
