/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package samples.javadsl;

// #imports
import akka.Done;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.alpakka.elasticsearch.ElasticsearchWriteSettings;
import akka.stream.alpakka.elasticsearch.WriteMessage;
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchSink;
import akka.stream.alpakka.slick.javadsl.Slick;
import akka.stream.alpakka.slick.javadsl.SlickRow;
import akka.stream.alpakka.slick.javadsl.SlickSession;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import samples.scaladsl.Helper;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

// #imports

public class Main {

    public static void main(String[] args) {
        Main me = new Main();
        me.run();
    }

    // #data-class
    public static class Movie {
        public final int id;
        public final String title;
        public final String genre;
        public final double gross;

        @JsonCreator
        public Movie(
                @JsonProperty("id") int id,
                @JsonProperty("title") String title,
                @JsonProperty("genre") String genre,
                @JsonProperty("gross") double gross) {
            this.id = id;
            this.title = title;
            this.genre = genre;
            this.gross = gross;
        }
    }

    // #data-class

    void run() {
        // Testcontainers: start Elasticsearch in Docker
        ElasticsearchContainer elasticsearchContainer = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch-oss:6.4.3");
        elasticsearchContainer.start();
        String elasticsearchAddress = elasticsearchContainer.getHttpHostAddress();

        // #sample
        ActorSystem system = ActorSystem.create();
        Materializer materializer = ActorMaterializer.create(system);

        // #sample
        // #slick-setup
        SlickSession session = SlickSession.forConfig("slick-h2-mem");
        system.registerOnTermination(session::close);
        // #slick-setup

        Helper.populateDataForTable(session, materializer);

        // #es-setup
        RestClient elasticSearchClient = RestClient.builder(HttpHost.create(elasticsearchAddress)).build();
        // #es-setup

        // #data-class
        final ObjectMapper objectToJsonMapper = new ObjectMapper();
        // #data-class

        // #sample
        final CompletionStage<Done> done =
                Slick.source(
                        session,
                        "SELECT * FROM MOVIE",
                        (SlickRow row) ->
                                new Movie(row.nextInt(), row.nextString(), row.nextString(), row.nextDouble()))
                        .map(movie -> WriteMessage.createIndexMessage(String.valueOf(movie.id), movie))
                        .runWith(
                                ElasticsearchSink.create(
                                        "movie",
                                        "boxoffice",
                                        ElasticsearchWriteSettings.Default(),
                                        elasticSearchClient,
                                        objectToJsonMapper),
                                materializer);

        done.thenRunAsync(
                () -> {
                    try {
                        elasticSearchClient.close();
                    } catch (IOException ignored) {
                        ignored.printStackTrace();
                    }
                },
                system.dispatcher())
                // #sample
                .thenRunAsync(
                        () -> {
                            elasticsearchContainer.stop();
                            system.terminate();
                        });
    }
}
