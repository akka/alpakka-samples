# Alpakka step-by-step example

## Read from a Kafka topic and publish to Elasticsearch

This example uses @extref[Alpakka Kafka](alpakka-kafka:) to subscribe to a Kafka topic, parses JSON into a data class and stores the object in Elasticsearch. After storing the Kafka offset is committed back to Kafka. This gives at-least-once semantics.
