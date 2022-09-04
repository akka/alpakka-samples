package samples.javadsl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

class JsonMappers {
    // Jackson conversion setup (3)
    public final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public final static ObjectWriter movieWriter = mapper.writerFor(Movie.class);
    public final static ObjectReader movieReader = mapper.readerFor(Movie.class);
}
