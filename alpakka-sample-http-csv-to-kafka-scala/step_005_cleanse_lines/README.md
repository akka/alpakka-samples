# Step 5: Cleanse lines from data source

### Description

The CSV data we receive happens to contain an empty column without a header. The `cleanseCsvData` removes that column and turns the column values from @scaladoc:[ByteString](akka.util.ByteString)s into regular `String`s.
