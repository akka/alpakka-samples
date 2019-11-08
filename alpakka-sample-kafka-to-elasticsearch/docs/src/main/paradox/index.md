@@include[readme](/README.md)

### Dependencies

Dependencies (sbt notation)
: @@snip [snip](/project/Dependencies.scala) { #deps }

### All Alpakka samples

Show [Alpakka samples listing](../index.html).


## Example code

### Description
- Configure Kafka consumer (1)
- Data class mapped to Elasticsearch (2)
- @scala[Spray JSON]@java[Jackson] conversion for the data class (3)
- Elasticsearch client setup (4)
- Kafka consumer keeping the offset for committing as context (5)
- Parse message from Kafka to `Movie` and create Elasticsearch write message (6)
- Use `createWithContext` to use an Elasticsearch flow with context-support (so it passes through the Kafka committ offset) (7)
- React on write errors (8)
- Let the `Committer.sinkWithOffsetContext` aggregate commits from the context to batches and commit to Kafka (9)
- Combine consumer control and stream completion into `DrainingControl` (10)

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

