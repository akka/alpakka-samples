# Alpakka sample

### Listen to a MQTT topic, download from URL passed in received message, and upload data from that address to AWS S3

Running this requires:
- MQTT broker running (provided via docker-compose)
- AWS S3 account configured in ./src/main/resources/credentials.conf as exemplified in credentials.conf-RENAME 
- AWS bucket "alpakka.samples" created 

Messages to MQTT can be published by `PublishDataToMqtt`.
