package alpakka.sample.sqssample;

import akka.Done;
import akka.NotUsed;
import akka.actor.*;

import static akka.pattern.PatternsCS.ask;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.sqs.*;
import akka.stream.alpakka.sqs.javadsl.SqsAckSink;
import akka.stream.alpakka.sqs.javadsl.SqsPublishFlow;
import akka.stream.alpakka.sqs.javadsl.SqsSource;
import akka.stream.javadsl.*;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

public class Main {

    final static Logger log = LoggerFactory.getLogger(Main.class);

    final static ObjectMapper mapper = new ObjectMapper();
    final static ObjectReader fromSqsReader = mapper.readerFor(MessageFromSqs.class);
    final static ObjectWriter enrichedMessageWriter = mapper.writerFor(EnrichedMessage.class);
    final static String sourceQueueUrl = "http://localhost:9324/queue/reading-from-this";
    final static String publishUrl = "http://localhost:9324/queue/publishing-to-this";

    final ActorSystem system;
    final Materializer materializer;
    final ActorRef enrichingActor;
    final LoggingAdapter logAsMain;

    public static void main(String[] args) throws Exception {
        Main me = new Main();
        me.run();
    }

    public Main() {
        system = ActorSystem.create();
        logAsMain = Logging.getLogger(system, this);
        materializer = ActorMaterializer.create(system);
        enrichingActor = system.actorOf(Props.create(EnrichActor.class, EnrichActor::new));
    }

    void run() throws Exception {
        // create SQS client
        String sqsEndpoint = "this-uses-ElasticMQ";
        AWSCredentialsProvider credentialsProvider =
                new AWSStaticCredentialsProvider(new BasicAWSCredentials("x", "x"));
        AmazonSQSAsync sqsClient =
                AmazonSQSAsyncClientBuilder.standard()
                        .withCredentials(credentialsProvider)
                        .withEndpointConfiguration(
                                new AwsClientBuilder.EndpointConfiguration(sqsEndpoint, "eu-central-1"))
                        .build();
        system.registerOnTermination(() -> sqsClient.shutdown());

        // configure SQS
        SqsSourceSettings settings = SqsSourceSettings.create().withCloseOnEmptyReceive(true);
        SqsAckSettings ackSettings = SqsAckSettings.create();

        // create running stream
        CompletionStage<Done> streamCompletion = SqsSource.create(sourceQueueUrl, settings, sqsClient)
                .log("read from SQS", logAsMain)
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

    CompletionStage<SqsPublishResult> enrichAndPublish(AmazonSQSAsync sqsClient, Message sqsMsg) {
        SqsPublishSettings publishSettings = SqsPublishSettings.create();
        final Flow<SendMessageRequest, SqsPublishResult, NotUsed> publishFlow = SqsPublishFlow.create(publishUrl, publishSettings, sqsClient);
        return Source.<Message>single(sqsMsg)
                .map(Main::transform)
                .mapAsync(16, (MessageFromSqs msg) -> {
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
                .map(amsg -> new SendMessageRequest(publishUrl, enrichedMessageWriter.writeValueAsString(amsg)))
                .log("sending to publish queue", logAsMain)
                .via(publishFlow)
                .runWith(Sink.head(), materializer);
    }

    private static MessageFromSqs transform(Message message) throws IOException {
        return fromSqsReader.readValue(message.getBody());
    }

}
