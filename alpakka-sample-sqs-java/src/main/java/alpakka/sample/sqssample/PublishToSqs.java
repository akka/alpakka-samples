package alpakka.sample.sqssample;

import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.sqs.SqsPublishSettings;
import akka.stream.alpakka.sqs.javadsl.SqsPublishFlow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionStage;

public class PublishToSqs {

    final static Logger log = LoggerFactory.getLogger(PublishToSqs.class);
    final static String sourceQueueUrl = Main.sourceQueueUrl;

    final ActorSystem system;
    final Materializer materializer;

    public static void main(String[] args) throws Exception {
        PublishToSqs me = new PublishToSqs();
        me.run();
    }

    public PublishToSqs() {
        system = ActorSystem.create();
        materializer = ActorMaterializer.create(system);
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

        publishMessageToSourceTopic(sqsClient, "{\"id\":423,\"name\":\"Alpakka\"}")
                .thenAccept(done -> system.terminate());
    }

    private CompletionStage<Done> publishMessageToSourceTopic(AmazonSQSAsync sqsClient, String msgJson) {
        return
        Source.single(msgJson)
                .map(s -> new SendMessageRequest(sourceQueueUrl, s))
                .via(SqsPublishFlow.create(sourceQueueUrl, SqsPublishSettings.create(), sqsClient))
                .runWith(Sink.foreach(System.out::println), materializer);
    }

}
