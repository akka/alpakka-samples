## Step 2: extract HTTP entity

### Description

The HTTP response with status OK is expected and the contained HTTP entity is extracted. Instead of the HTTP respoinse the contained entity (page content) continues in the stream in the form of @scaladoc:[ByteString](akka.util.ByteString) elements.
