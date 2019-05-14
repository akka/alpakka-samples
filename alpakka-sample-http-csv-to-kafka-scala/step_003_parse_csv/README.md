## Step 3: parse CSV

### Description

The binary data in @scaladoc:[ByteString](akka.util.ByteString)s is passed into @extref:[Alpakka CSV](alpakka-docs:data-transformations/csv.html) to be parsed and per line converted into Maps. The stream elements become `Map[String, ByteString]`, one per line in the CSV file using the file's headers as keys.
