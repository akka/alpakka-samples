package alpakka.sample.sqssample;

import akka.Done;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.stream.alpakka.sqs.SqsPublishSettings;
import akka.stream.alpakka.sqs.javadsl.SqsPublishFlow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.github.matsluni.akkahttpspi.AkkaHttpClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.net.URI;
import java.util.concurrent.CompletionStage;

public class PublishToSqs {

    final static Logger log = LoggerFactory.getLogger(PublishToSqs.class);
    final static String sourceQueueUrl = Main.sourceQueueUrl;

    final ActorSystem<Void> system;

    public static void main(String[] args) throws Exception {
        PublishToSqs me = new PublishToSqs();
        me.run();
    }

    public PublishToSqs() {
        system = ActorSystem.create(Behaviors.empty(), "PublishToSqs");
    }

    void run() throws Exception {
        // create SQS client
        String sqsEndpoint = "http://localhost:9324";
        SqsAsyncClient sqsClient =
                SqsAsyncClient.builder()
                        .credentialsProvider(
                                StaticCredentialsProvider.create(AwsBasicCredentials.create("x", "x")))
                        .endpointOverride(URI.create(sqsEndpoint))
                        .region(Region.EU_CENTRAL_1)
                        .httpClient(AkkaHttpClient.builder().withActorSystem(system.classicSystem()).build())
                        .build();
        system.getWhenTerminated().thenAccept((notUsed) -> sqsClient.close());

        publishMessageToSourceTopic(sqsClient, "{\"id\":423,\"name\":\"Alpakka\"}")
                .thenAccept(done -> system.terminate());
    }

    private CompletionStage<Done> publishMessageToSourceTopic(SqsAsyncClient sqsClient, String msgJson) {
        return Source.single(msgJson)
                .map(s -> SendMessageRequest.builder().messageBody(s).build())
                .via(SqsPublishFlow.create(sourceQueueUrl, SqsPublishSettings.create(), sqsClient))
                .runWith(Sink.foreach(System.out::println), system);
    }

}
