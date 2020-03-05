# Full source

The full example contains code to run Kafka in a Docker container via [Testcontainers](https://www.testcontainers.org/modules/kafka/).

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { filterLabels=true }

## Helper

The helper uses [Testcontainers](https://www.testcontainers.org/modules/kafka/) to start a Kafka broker.
The `writeToKafka()` method populates the Kafka topic using @extref[Alpakka Kafka](alpakka-kafka:producer.html)

Java
: @@snip [snip](/src/main/java/samples/javadsl/Helper.java) { filterLabels=true }
