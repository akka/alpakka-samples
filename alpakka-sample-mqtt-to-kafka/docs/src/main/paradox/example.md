## Example code

This example uses an MQTT broker which may be started via Docker compose and a Kafka broker which is created from within the JVM by [Testcontainers](https://www.testcontainers.org/).

Starting the MQTT broker
```$bash
> docker-compose up -d
```

docker-compose.yml
: @@snip [snip](/docker-compose.yml)

### Restarting of the source

The MQTT source gets wrapped by a `RestartSource` to mitigate the 
@extref:[Paho initial connections problem](alpakka:/mqtt.html#settings).

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #restarting }

### Json helper code

To use Java 8 time types (`Instant`) with Jackson, extra dependencies are required.

@@dependency [sbt,Maven,Gradle] {
  group1=com.fasterxml.jackson.datatype
  artifact1=jackson-datatype-jdk8
  version1=2.10.0
  group2=com.fasterxml.jackson.datatype
  artifact2=jackson-datatype-jsr310
  version2=2.10.0
}
### Data class and JSON mapping

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #json-mechanics }


### Flow

- (1) connection details to MQTT broker
- (2) settings for MQTT source specifying the topic to listen to
- (3) use helper method to cater for Paho failures on initial connect
- (4) add a kill switch to allow for stopping the subscription
- (5) convert incoming ByteString to String
- (6) parse JSON
- (7) group up to 50 messages into one, as long as they appear with 5 seconds
- (8) convert the list of measurements to a JSON array structure
- (9) store the JSON in a Kafka producer record
- (10) producer to Kafka


Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #flow }
