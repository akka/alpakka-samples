/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.javadsl;

import akka.Done;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.MediaRanges;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.headers.Accept;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

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

    private Source<ByteString, ?> extractEntityData(HttpResponse httpResponse) {
        if (httpResponse.status() == StatusCodes.OK) {
            return httpResponse.entity().getDataBytes();
        } else {
            return Source.failed(new RuntimeException("illegal response " + httpResponse));
        }
    }

    private void run() throws Exception {
        ActorSystem system = ActorSystem.create();
        Materializer materializer = ActorMaterializer.create(system);
        Http http = Http.get(system);

        CompletionStage<Done> completion =
                Source.single(httpRequest) // : HttpRequest
                        .mapAsync(1, http::singleRequest) // : HttpResponse
                        .flatMapConcat(this::extractEntityData) // : ByteString
                        .runWith(Sink.foreach(bs -> System.out.println(bs.utf8String())), materializer);

        completion
                .thenAccept(
                        done -> {
                            System.out.println("Done!");
                            system.terminate();
                        });
    }
}
