package alpakka.sample.sqssample;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

final class MessageFromSqs {
    public final int id;
    public final String name;
    public final String url;

    @JsonCreator
    public MessageFromSqs(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("url") String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    @Override
    public String toString() {
        return "MessageFromSqs(" + id + ", name=" + name + ", url=" + url + ")";
    }
}
