# Alpakka sample

### Listen to an Amazon SQS topic, enrich the message via calling an actor, publish a new message to SQS and acknowledge/delete the original message

Running this requires:
- Amazon SQS (this sample uses ElasticMQ, provided via docker-compose)

Messages to SQS can be published by `PublishToSqs`.
