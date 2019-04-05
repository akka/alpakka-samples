package alpakka.sample.sqssample;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
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

    final ActorSystem system;
    final Materializer materializer;
    final ActorRef enrichingActor;
    final LoggingAdapter log;

    public static void main(String[] args) throws Exception {
        Main me = new Main();
        me.run();
    }

    public Main() {
        system = ActorSystem.create();
        log = Logging.getLogger(system, this);
        materializer = ActorMaterializer.create(system);
        enrichingActor = system.actorOf(Props.create(EnrichActor.class, EnrichActor::new));
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
        system.registerOnTermination(() -> sqsClient.close());

        // configure SQS
        SqsSourceSettings settings = SqsSourceSettings.create().withCloseOnEmptyReceive(true);
        SqsAckSettings ackSettings = SqsAckSettings.create();

        // create running stream
        CompletionStage<Done> streamCompletion = SqsSource.create(sourceQueueUrl, settings, sqsClient)
                .log("read from SQS", log)
                .mapAsync(8, (Message msg) -> {
                    return enrichAndPublish(sqsClient, msg)
                            // upon completion ignore the result and pass on the original message
                            .thenApply(result -> msg);
                })
                .map(msg -> MessageAction.delete(msg))
                .runWith(
                        SqsAckSink.create(sourceQueueUrl, ackSettings, sqsClient),
                        materializer
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
                    return ask(enrichingActor, msg.id, Duration.ofSeconds(2))
                            .thenApply(response -> {
                                log.debug("ask received '{}'", response);
                                if (response instanceof ActorResponseMsg) {
                                    // combine msg and response
                                    ActorResponseMsg resp = (ActorResponseMsg) response;
                                    return new EnrichedMessage(msg.id, msg.name, msg.url, resp.data);
                                } else {
                                    throw new RuntimeException("received unexpected response");
                                }
                            });
                })
                .map(amsg -> SendMessageRequest.builder().messageBody(enrichedMessageWriter.writeValueAsString(amsg)).build())
                .log("sending to publish queue", log)
                .via(publishFlow)
                .runWith(Sink.head(), materializer);
    }

    private static MessageFromSqs transform(Message message) throws IOException {
        return fromSqsReader.readValue(message.body());
    }

}
