## Step 3: parse CSV

### Description

The binary data in @scaladoc:[ByteString](akka.util.ByteString)s is passed into @extref:[Alpakka CSV](alpakka-docs:data-transformations/csv.html) to be parsed and converted per line into Maps. The stream elements becomes a `Map[String, ByteString]`, one key/value pair per line in the CSV file, using the file's field headers as keys.
