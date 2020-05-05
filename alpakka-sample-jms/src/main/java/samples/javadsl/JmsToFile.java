/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.javadsl;

// #sample

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.japi.Pair;
import akka.stream.IOResult;
import akka.stream.alpakka.jms.JmsConsumerSettings;
import akka.stream.alpakka.jms.JmsProducerSettings;
import akka.stream.alpakka.jms.javadsl.JmsConsumer;
import akka.stream.alpakka.jms.javadsl.JmsConsumerControl;
import akka.stream.alpakka.jms.javadsl.JmsProducer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import scala.concurrent.ExecutionContext;

import javax.jms.ConnectionFactory;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

// #sample

import playground.ActiveMqBroker;

public class JmsToFile {

  public static void main(String[] args) throws Exception {
    JmsToFile me = new JmsToFile();
    me.run();
  }

  private final ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "JmsToFile");
  private final ExecutionContext ec = system.executionContext();

  private void enqueue(ConnectionFactory connectionFactory, String... msgs) {
    Sink<String, ?> jmsSink =
        JmsProducer.textSink(
            JmsProducerSettings.create(system, connectionFactory).withQueue("test"));
    Source.from(Arrays.asList(msgs)).runWith(jmsSink, system);
  }

  private void run() throws Exception {
    ActiveMqBroker activeMqBroker = new ActiveMqBroker();
    activeMqBroker.start();

    ConnectionFactory connectionFactory = activeMqBroker.createConnectionFactory();
    enqueue(connectionFactory, "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
    // #sample

    Source<String, JmsConsumerControl> jmsSource = // (1)
        JmsConsumer.textSource(
            JmsConsumerSettings.create(system, connectionFactory).withQueue("test"));

    Sink<ByteString, CompletionStage<IOResult>> fileSink =
        FileIO.toPath(Paths.get("target/out.txt")); // (2)

    Pair<JmsConsumerControl, CompletionStage<IOResult>> pair =
        jmsSource // : String
            .map(ByteString::fromString) // : ByteString    (3)
            .toMat(fileSink, Keep.both())
            .run(system);

    // #sample

    JmsConsumerControl runningSource = pair.first();
    CompletionStage<IOResult> streamCompletion = pair.second();

    Thread.sleep(4000);

    runningSource.shutdown();
    streamCompletion.thenAccept(res -> system.terminate());
    system.getWhenTerminated().thenCompose(t -> activeMqBroker.stopCs(ec)).toCompletableFuture().get(5, TimeUnit.SECONDS);
  }
}
