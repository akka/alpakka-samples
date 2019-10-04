@@include[readme](/README.md)

## Dependencies

Dependencies (sbt notation)
: @@snip [snip](/project/Dependencies.scala) { #deps }

## Example code walkthrough

### Description
- Data class mapped to Elasticsearch (1)
- @scala[Spray JSON]@java[Jackson] conversion for the data class (2)
- Create `DirectoryChangeSource` (3) to watch for file events in a given directory (4)
- Only watch for file creation events (5)
- Create a new `FileTailSource` and return it as a sub-stream (6)
- Create `FileTailSource` for a given `path` (7)
- Count each line from the log file (8)
- Extract the date timestamp from the log line (9)
- Create a `LogLine` record (10)
- Create an ES index wrapper message for `LogLine` (11)
- Use Alpakka Elasticsearch to create a new `LogLine` record. (12)
- Track statistics per log file (13)
- Use Alpakka Elasticsearch to return all entries from the provided index (14)
- Compose stream together starting with the `DirectoryChangesSource` (15)
- Run the graph and capture the materialized values (16)
- Run a new graph to query all records from Elasticsearch and get the results (17)

### Data class and JSON mapping

Java
: @@snip [snip](/src/main/java/samples/javadsl/LogLine.java) { #logline }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/LogLine.scala) { #logline }


### Setup `DirectoryChangeSource`

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #directory-change-source }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #directory-change-source }

### Tail log files and create a `LogLine` per line

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #tail-logs }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #tail-logs }

### Index `LogLines` into Elasticsearch

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #es-index-flow }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #es-index-flow }

### Log files statistics summary

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #summarize-log-stats-flow }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #summarize-log-stats-flow }

### Query Elasticsearch

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #query-elasticsearch }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #query-elasticsearch }

### Composing everything together 

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #stream-composing }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #stream-composing }

### Running the application

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #running-the-app }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #running-the-app }

## All Alpakka samples

Show [Alpakka samples listing](../index.html).

@@toc

@@@ index

* [6](full-source.md)

@@@
