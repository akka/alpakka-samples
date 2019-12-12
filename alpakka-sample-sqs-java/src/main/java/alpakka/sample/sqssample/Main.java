package alpakka.sample.sqssample;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
import akka.stream.alpakka.sqs.*;
import akka.stream.alpakka.sqs.javadsl.SqsAckSink;
import akka.stream.alpakka.sqs.javadsl.SqsPublishFlow;
import akka.stream.alpakka.sqs.javadsl.SqsSource;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

public class Main {

    final static ObjectMapper mapper = new ObjectMapper();
    final static ObjectReader fromSqsReader = mapper.readerFor(MessageFromSqs.class);
    final static ObjectWriter enrichedMessageWriter = mapper.writerFor(EnrichedMessage.class);
    final static String sourceQueueUrl = "http://localhost:9324/queue/reading-from-this";
    final static String publishUrl = "http://localhost:9324/queue/publishing-to-this";

    final Logger log = LoggerFactory.getLogger(Main.class);
    final ActorSystem<EnrichActor.Enrich> system;

    public static void main(String[] args) throws Exception {
        Main me = new Main();
        me.run();
    }

    public Main() {
        system = ActorSystem.create(EnrichActor.create(), "SqsSample");
    }

    void run() throws Exception {
        // create SQS client
        String sqsEndpoint = "this-uses-ElasticMQ";
        SqsAsyncClient sqsClient =
                SqsAsyncClient.builder()
                        .credentialsProvider(
                                StaticCredentialsProvider.create(AwsBasicCredentials.create("x", "x")))
                        .endpointOverride(URI.create(sqsEndpoint))
                        .region(Region.EU_CENTRAL_1)
                        .build();
        system.getWhenTerminated().thenAccept(notUsed -> sqsClient.close());

        // configure SQS
        SqsSourceSettings settings = SqsSourceSettings.create().withCloseOnEmptyReceive(true);
        SqsAckSettings ackSettings = SqsAckSettings.create();

        // create running stream
        CompletionStage<Done> streamCompletion = SqsSource.create(sourceQueueUrl, settings, sqsClient)
                .log("read from SQS")
                .mapAsync(8, (Message msg) -> {
                    return enrichAndPublish(sqsClient, msg)
                            // upon completion ignore the result and pass on the original message
                            .thenApply(result -> msg);
                })
                .map(msg -> MessageAction.delete(msg))
                .runWith(
                        SqsAckSink.create(sourceQueueUrl, ackSettings, sqsClient),
                        system
                );

        // terminate the actor system when the stream completes (see withCloseOnEmptyReceive)
        streamCompletion.thenAccept(done -> system.terminate());
    }

    CompletionStage<SqsPublishResult<SendMessageResponse>> enrichAndPublish(SqsAsyncClient sqsClient, Message sqsMsg) {
        SqsPublishSettings publishSettings = SqsPublishSettings.create();
        final Flow<SendMessageRequest, SqsPublishResult<SendMessageResponse>, NotUsed> publishFlow = SqsPublishFlow.create(publishUrl, publishSettings, sqsClient);
        return Source.<Message>single(sqsMsg)
                .map(Main::transform)
                .mapAsync(1, (MessageFromSqs msg) -> {
                    CompletionStage<EnrichActor.Enriched> response =
                        AskPattern.ask(system, ref -> new EnrichActor.Enrich(msg.id, ref), Duration.ofSeconds(2), system.scheduler());
                    return response.thenApply(res -> {
                                log.debug("ask received '{}'", res);
                                return new EnrichedMessage(msg.id, msg.name, msg.url, res.data);
                            });
                })
                .map(amsg -> SendMessageRequest.builder().messageBody(enrichedMessageWriter.writeValueAsString(amsg)).build())
                .log("sending to publish queue")
                .via(publishFlow)
                .runWith(Sink.head(), system);
    }

    private static MessageFromSqs transform(Message message) throws IOException {
        return fromSqsReader.readValue(message.body());
    }

}
