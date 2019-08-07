### Read text messages from JMS queue and send to web socket

- listens to the JMS queue "test" receiving `String`s (1),
- configures a web socket flow to localhost (2),
- converts incoming data to a @scala[@scaladoc[ws.TextMessage](akka.http.scaladsl.model.ws.TextMessage)]@java[@scaladoc[akka.http.javadsl.model.ws.TextMessage](akka.http.javadsl.model.ws.TextMessage)] (3),
- pass the message via the web socket flow (4),
- convert the (potentially chunked) web socket reply to a `String` (5),
- prefix the `String` (6),
- end the stream by writing the values to standard out (7).

Scala
: @@snip [snip](/step_001_complete/src/main/scala/samples/scaladsl/JmsToWebSocket.scala) { #sample }

Java
: @@snip [snip](/step_001_complete/src/main/java/samples/javadsl/JmsToWebSocket.java) { #sample }
