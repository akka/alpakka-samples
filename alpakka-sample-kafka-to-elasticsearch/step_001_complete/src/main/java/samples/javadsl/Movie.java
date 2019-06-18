package samples.javadsl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// Type in Elasticsearch (2)
public class Movie {
    public final int id;
    public final String title;

    @JsonCreator
    public Movie(@JsonProperty("id") int id, @JsonProperty("title") String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Movie(" + id + ", title=" + title + ")";
    }
}

class JsonMappers {
    // Jackson conversion setup (3)
    public final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public final static ObjectWriter movieWriter = mapper.writerFor(Movie.class);
    public final static ObjectReader movieReader = mapper.readerFor(Movie.class);
}
