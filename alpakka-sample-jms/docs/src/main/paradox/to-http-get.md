### Read text messages from JMS queue and send to web server

- listens to the JMS queue "test" receiving `String`s (1),
- converts incoming data to `akka.util.ByteString` (2),
- puts the received text into an `HttpRequest` (3),
- sends the created request via Akka Http (4),
- prints the `HttpResponse` to standard out (5).

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/JmsToHttpGet.scala) { #sample }

Java
: @@snip [snip](/src/main/java/samples/javadsl/JmsToHttpGet.java) { #sample }
