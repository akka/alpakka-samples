# Alpakka sample

## Tail files added to a directory and publish to Elasticsearch

This sample is a simplified implementation of a log file to Elasticsearch utility @link:[`logstash`](https://www.elastic.co/products/logstash).
The application will use @extref[Alpakka File](alpakka-file:) to watch a directory for new file creation events and then tail the files for their contents.
The log file is tailed until it is deleted or the stream is complete.
Lines from the log file have their date parsed based on several compatible date time formats (ISO 8601 Zoned and Local datetime timestamps).
A record is created that is comprised of the following fields.

1. The full log line
2. An extracted timestamp
3. Directory
4. Filename
5. Line number

Each log line record is indexed in an Elasticsearch index called `logs`.  
The application will use @extref[Alpakka Elasticsearch](alpakka-elasticsearch:) to index log lines into Elasticsearch and query them from the index once the stream is complete.

When the stream completes, @extref[Alpakka Elasticsearch](alpakka-elasticsearch:) is used to query all log lines that were indexed and log them.
A summary of tailed log files is also logged, which includes the following fields.

1. Directory
2. Filename
3. First seen timestamp
4. Last updated timestamp
5. Total log lines parsed

To try out this project clone @link:[the Alpakka Samples repository](https://github.com/akka/alpakka-samples) { open=new } and find it in the `alpakka-sample-file-to-elasticsearch` directory.