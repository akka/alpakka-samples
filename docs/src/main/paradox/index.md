# Alpakka Samples

### @extref:[Download all files from an FTP server](ftp-to-file:index.html)
This example uses Alpakka FTP to download files from an FTP server.

### @extref:[Fetch CSV via HTTP and publish to Kafka](http-csv-to-kafka:index.html)
This example uses Akka HTTP to send the HTTP request and Akka HTTP's JSON support to convert the map into a JSON structure which gets published to a Kafka topic.

### @extref:[Examples using JMS](jms:index.html)
This example uses receives JMS messages with Alpakka JMS and sends those to different technologies.

### @extref:[Read from a database and write to Elasticsearch](jdbc-to-elasticsearch:index.html)
This example uses Alpakka Slick to read from a relational database and write the data to Elasticsearch.

### @extref:[Read from Kafka and write to Elasticsearch](kafka-to-elasticsearch:index.html)
This example uses Alpakka Kafka to read from an Apache Kafka topic, parse the messages as JSON and write the data to Elasticsearch, committing offsets to Kafka after successfully updating Elasticsearch.

### @link:[Amazon SQS](https://github.com/akka/alpakka-samples/tree/master/alpakka-sample-sqs-java) { open=new }
Listen to an Amazon SQS topic, enrich the message via calling an actor, publish a new message to SQS and acknowledge/delete the original message. (Java only)

### @link:[MQTT topic triggers file download which is uploaded to AWS S3](https://github.com/akka/alpakka-samples/tree/master/alpakka-sample-mqtt-http-to-s3-java) { open=new }

Listen to a MQTT topic, download from the URL passed in the received message, and upload the data from that address to AWS S3. (Java only)
