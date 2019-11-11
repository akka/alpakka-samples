## Example code

### Description
- Configure Kafka consumer (1)
- Data class mapped to Elasticsearch (2)
- @scala[Spray JSON]@java[Jackson] conversion for the data class (3)
- Elasticsearch client setup (4)
- Kafka consumer with committing support (5)
- Use `FlowWithContext` to focus on `ConsumerRecord` (6)
- Parse message from Kafka to `Movie` and create Elasticsearch write message (7)
- Use `createWithContext` to use an Elasticsearch flow with context-support (so it passes through the Kafka committ offset) (8)
- React on write errors (9)
- Make the context visible again and just keep the committable offset (10)
- Let the `Committer.sink` aggregate commits to batches and commit to Kafka (11)
- Combine consumer control and stream completion into `DrainingControl` (12)

### Data class and JSON mapping

Java
: @@snip [snip](/src/main/java/samples/javadsl/Movie.java)

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Movie.scala)



### Flow
Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #flow }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #flow }


### Kafka setup
Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #kafka-setup }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #kafka-setup }


### Elasticsearch setup
Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #es-setup }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #es-setup }

