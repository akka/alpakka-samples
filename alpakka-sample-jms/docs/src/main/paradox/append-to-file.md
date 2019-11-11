### Read text messages from JMS queue and append to file

- listens to the JMS queue "test" receiving `String`s (1),
- converts incoming data to `akka.util.ByteString` (3),
- and appends the data to the file `target/out` (2).

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/JmsToFile.scala) { #sample }

Java
: @@snip [snip](/src/main/java/samples/javadsl/JmsToFile.java) { #sample }
