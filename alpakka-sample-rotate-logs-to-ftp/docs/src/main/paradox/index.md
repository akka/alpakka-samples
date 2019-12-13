@@include[readme](/README.md)

### Dependencies

Dependencies (sbt notation)
: @@snip [snip](/project/Dependencies.scala) { #deps }

### Imports

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #imports }

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala)  { #imports }


- generate data stream with changing contents over time (1),
- function that tracks last element and outputs a new path when contents in the stream change (2),
- prepare SFTP credentials and settings (3),
- compress ByteStrings (4)

Scala
: @@snip [snip](/src/main/scala/samples/scaladsl/Main.scala) { #sample }

Java
: @@snip [snip](/src/main/java/samples/javadsl/Main.java) { #sample }


### All Alpakka samples

Show [Alpakka samples listing](../index.html).

@@toc

@@@ index

* [6](full-source.md)

@@@
