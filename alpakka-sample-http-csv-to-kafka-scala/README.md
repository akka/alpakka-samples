# Alpakka step-by-step example

## Fetch CSV via Akka HTTP and publish the data as JSON to Kafka

This example uses @extref[Akka HTTP to send the HTTP request](akka-http-docs:client-side/connection-level.html#opening-http-connections) and Akka HTTPs primary JSON support via @extref[Spray JSON](akka-http-docs:common/json-support.html#spray-json-support) to convert the map into a JSON structure.
