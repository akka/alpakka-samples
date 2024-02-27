/*
 * Copyright (C) 2016-2024 Lightbend Inc. <https://www.lightbend.com>
 */

package samples.javadsl;

// #sample

import akka.Done;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.japi.Pair;
import akka.stream.KillSwitch;
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
import playground.ActiveMqBroker;
import scala.concurrent.ExecutionContext;

import javax.jms.ConnectionFactory;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

// #sample

public class JmsToOneFilePerMessage {

  public static void main(String[] args) throws Exception {
    JmsToOneFilePerMessage me = new JmsToOneFilePerMessage();
    me.run();
  }

  private final ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "JmsToOneFilePerMessage");
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

    Source<String, JmsConsumerControl> jmsConsumer = // (1)
        JmsConsumer.textSource(
            JmsConsumerSettings.create(system, connectionFactory).withQueue("test"));

    int parallelism = 5;
    Pair<JmsConsumerControl, CompletionStage<Done>> pair =
        jmsConsumer // : String
            .map(ByteString::fromString) // : ByteString             (2)
            .zipWithIndex() // : Pair<ByteString, Long> (3)
            .mapAsyncUnordered(
                parallelism,
                (in) -> {
                  ByteString byteString = in.first();
                  Long number = in.second();
                  return Source // (4)
                      .single(byteString)
                      .runWith(
                          FileIO.toPath(Paths.get("target/out-" + number + ".txt")), system);
                }) // : IoResult
            .toMat(Sink.ignore(), Keep.both())
            .run(system);

    // #sample

    KillSwitch runningSource = pair.first();
    CompletionStage<Done> streamCompletion = pair.second();

    Thread.sleep(2 * 1000);

    runningSource.shutdown();
    streamCompletion.thenAccept(res -> system.terminate());
    system.getWhenTerminated().thenCompose(t -> activeMqBroker.stopCs(ec)).toCompletableFuture().get(5, TimeUnit.SECONDS);
  }
}
