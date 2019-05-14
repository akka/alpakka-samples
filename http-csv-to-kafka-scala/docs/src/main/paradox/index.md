# Alpakka step-by-step example

## Fetch CSV from Internet and publish the data as JSON to Kafka

This example uses
@extref[Akka HTTP to send the HTTP request](akka-http-docs:client-side/connection-level.html#opening-http-connections)
and @scala[Akka HTTPs primary JSON support
via @extref[Spray JSON](akka-http-docs:common/json-support.html#spray-json-support) to convert the map into a JSON structure.]
@java[Jackson JSON generator to convert the map into a JSON-formatted string.]

@@toc { depth=2 }

@@@ index

* [1](step1.md)
* [2](step2.md)
* [3](step3.md)
* [4](step4.md)
* [5](step5.md)
* [6](step6.md)
* [7](step7.md)
* [8](step8.md)

@@@
