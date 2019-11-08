## Example code

### Description
- Configure Kafka consumer (1)
- Data class mapped to Elasticsearch (2)
- @scala[Spray JSON]@java[Jackson] conversion for the data class (3)
- Elasticsearch client setup (4)
- Kafka consumer keeping the offset for committing as context (5)
- Parse message from Kafka to `Movie` and create Elasticsearch write message (6)
- Use `createWithContext` to use an Elasticsearch flow with context-support (so it passes through the Kafka committ offset) (7)
- React on write errors (8)
- Let the `Committer.sinkWithOffsetContext` aggregate commits from the context to batches and commit to Kafka (9)
- Combine consumer control and stream completion into `DrainingControl` (10)

