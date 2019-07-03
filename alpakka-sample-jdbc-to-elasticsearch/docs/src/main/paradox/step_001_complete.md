@@include[readme](/step_001_complete/README.md)


### Imports

Java
: @@snip [snip](/step_001_complete/src/main/java/samples/javadsl/Main.java) { #imports }

Scala
: @@snip [snip](/step_001_complete/src/main/scala/samples/scaladsl/Main.scala)  { #imports }


### Flow
- Construct the Slick `Source` for the H2 table and query all data in the table
@scala[Map each tuple into a `Movie` case class instance]
- The first argument of the `IndexMessage` is the *id* of the document.
- Prepare the Elastic `Sink` that the data needs to be drained to
- Close the Elastic client upon completion of indexing the data

Java
: @@snip [snip](/step_001_complete/src/main/java/samples/javadsl/Main.java) { #sample }

Scala
: @@snip [snip](/step_001_complete/src/main/scala/samples/scaladsl/Main.scala) { #sample }


### Slick setup
- Instantiate a Slick database session using the config parameters defined in key `slick-h2-mem` and mount closing it on shutdown of the Actor System
@scala[- Slick definition of the MOVIE table]

Java
: @@snip [snip](/step_001_complete/src/main/java/samples/javadsl/Main.java) { #slick-setup }

Scala
: @@snip [snip](/step_001_complete/src/main/scala/samples/scaladsl/Main.scala) { #slick-setup }


### Data class and JSON mapping for Elasticsearch

- Class that holds the Movie data
- @scala[Instantiate the Spray json format that converts the `Movie` case class to json]@java[Instantiate the Jackson Object mapper that converts the `Movie` class to json]

Java
: @@snip [snip](/step_001_complete/src/main/java/samples/javadsl/Main.java) { #data-class }

Scala
: @@snip [snip](/step_001_complete/src/main/scala/samples/scaladsl/Main.scala)  { #data-class }


### Elasticsearch setup
- Instantiate Elastic REST client

Java
: @@snip [snip](/step_001_complete/src/main/java/samples/javadsl/Main.java) { #es-setup }

Scala
: @@snip [snip](/step_001_complete/src/main/scala/samples/scaladsl/Main.scala) { #es-setup }

