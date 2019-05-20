/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.javadsl;

import akka.Done;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.MediaRanges;
import akka.http.javadsl.model.headers.Accept;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import java.util.Collections;
import java.util.concurrent.CompletionStage;

public class Main {

    public static void main(String[] args) throws Exception {
        Main me = new Main();
        me.run();
    }

    final HttpRequest httpRequest =
            HttpRequest.create(
                    "https://www.nasdaq.com/screening/companies-by-name.aspx?exchange=NASDAQ&render=download")
                    .withHeaders(Collections.singletonList(Accept.create(MediaRanges.ALL_TEXT)));

    private void run() throws Exception {
        ActorSystem system = ActorSystem.create();
        Materializer materializer = ActorMaterializer.create(system);
        Http http = Http.get(system);

        CompletionStage<Done> completion =
                Source.single(httpRequest) // : HttpRequest
                        .mapAsync(1, http::singleRequest) // : HttpResponse
                        .runWith(Sink.foreach(response -> System.out.println(response)), materializer);

        completion
                .thenAccept(
                        done -> {
                            System.out.println("Done!");
                            system.terminate();
                        });
    }
}
