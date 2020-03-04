## Example code



### Subscribe to the Kafka topic

Use an @extref:[Alpakka Kafka](alpakka-kafka:) consumer to subscribe to a topic in Kafka. The received `String` values are sent to a @apidoc[BroadcastHub] which creates a @apidoc[Source] for the clients to connect to.

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #kafka-to-broadcast }

### Handler for websocket requests

This `websocketHandler` is a @apidoc[Flow] which will be used when a websocket client connects. It ignores data sent to it and publishes all data received from the `topicSource()` which is backed by the @apidoc[BroadcastHub].

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #websocket-handler }
