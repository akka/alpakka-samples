@@include[readme](/step_001_complete/README.md)


### Imports

Java
: @@snip [snip](/step_001_complete/src/main/java/samples/javadsl/Main.java) { #imports }

Scala
: @@snip [snip](/step_001_complete/src/main/scala/samples/scaladsl/Main.scala)  { #imports }


### Flow

- list FTP server contents (1),
- just bother about file entries (2),
- for each file prepare for awaiting @scala[`Future`]@java[`CompletionStage`] results ignoring the stream order (3),
- run a new stream copying the file contents to a local file (4),
- combine the filename and the copying result (5),
- collect all filenames with results into a sequence (6)

Java
: @@snip [snip](/step_001_complete/src/main/java/samples/javadsl/Main.java) { #sample }

Scala
: @@snip [snip](/step_001_complete/src/main/scala/samples/scaladsl/Main.scala) { #sample }

