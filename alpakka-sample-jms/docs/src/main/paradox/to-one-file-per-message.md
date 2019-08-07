### Read text messages from JMS queue and create one file per message

- listens to the JMS queue "test" receiving `String`s (1),
- converts incoming data to `akka.util.ByteString` (2),
- combines the incoming data with a counter (3),
- creates an intermediary stream writing the incoming data to a file using the counter 
value to create unique file names (4). 

Scala
: @@snip [snip](/step_001_complete/src/main/scala/samples/scaladsl/JmsToOneFilePerMessage.scala) { #sample }

Java
: @@snip [snip](/step_001_complete/src/main/java/samples/javadsl/JmsToOneFilePerMessage.java) { #sample }
