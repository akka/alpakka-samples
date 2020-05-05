package samples.javadsl;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.testkit.javadsl.TestKit;
import com.google.common.collect.Streams;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;

public class MainTest {
    static ActorSystem system;
    static Materializer materializer;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("WebsocketExampleMainTest");
        materializer = ActorMaterializer.create(system);
    }

    @AfterClass
    public static void tearDown() {
        TestKit.shutdownActorSystem(system);
        system = null;
        materializer = null;
    }

    @Test
    public void addIndexFlow_Test() throws Exception {
        final Main example = new Main(new Helper());

        List<String> messages = Arrays.asList(
                "I say high, you say low",
                "You say why and I say I don't know",
                "Oh, no",
                "You say goodbye and I say hello"
        );

        final Flow<String, String, NotUsed> addIndexFlow = example.addIndexFlow();

        final CompletionStage<List<String>> future = Source.from(messages)
                .via(addIndexFlow)
                .runWith(Sink.seq(), materializer);
        final List<String> result = future.toCompletableFuture().get(3, TimeUnit.SECONDS);

        assert(result.size() == messages.size());

        final Pattern pattern = Pattern.compile("index: \\d+, message: (.*)");
        Streams.zip(
                messages.stream(),
                result.stream(),
                Pair::create)
                .forEachOrdered(pair -> {
                    String message = pair.first();
                    String resultMessage = pair.second();
                    Matcher matcher = pattern.matcher(resultMessage);
                    assertTrue(matcher.find());
                    assert(matcher.groupCount() == 1);
                    assert(matcher.group(1).equals(message));
                });
    }
}
