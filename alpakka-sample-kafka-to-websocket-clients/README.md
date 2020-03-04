# Alpakka sample

## Read from a Kafka topic and push the data to connected websocket clients

Clients may connect via websockets and will receive data read from a Kafka topic. The websockets are implemented in @extref[Akka HTTP](akka-http:) and [Alpakka Kafka](alpakka-kafka:) subscribes to the Kafka topic.

Browse the sources at @link:[Github](https://github.com/akka/alpakka-samples/tree/master/alpakka-sample-kafka-to-websocket-clients) { open=new }.

To try out this project clone @link:[the Alpakka Samples repository](https://github.com/akka/alpakka-samples) { open=new } and find it in the `alpakka-sample-kafka-to-websocket-clients` directory.
