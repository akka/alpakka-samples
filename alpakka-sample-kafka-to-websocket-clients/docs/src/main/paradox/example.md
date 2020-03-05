## Example code

This page highlights the most important sections of the example.

### Subscribe to the Kafka topic

Use an @extref:[Alpakka Kafka](alpakka-kafka:) consumer to subscribe to a topic in Kafka. The received `String` values are sent to a @extref[BroadcastHub](akka:stream/stream-dynamic.html#using-the-broadcasthub) which creates a `Source` for the clients to connect to.

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #kafka-to-broadcast }

### Handler for websocket requests

This `websocketHandler` is a `Flow` which will be used when a websocket client connects. It ignores data sent to it and publishes all data received from the `topicSource()` which is backed by the @extref[BroadcastHub](akka:stream/stream-dynamic.html#using-the-broadcasthub).

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #websocket-handler }

### Akka HTTP routes

This example code uses two routes
* `/events` which opens a websocket to subscribe to the messages from the Kafka topic
* `/push` which writes the text from the parameter `value` to the Kafka topic

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #routes }

### All imports

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #imports }

