package alpakka.sample.sqssample;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

final class EnrichedMessage {
    public final int id;
    public final String name;
    public final String url;
    public final String moreData;

    @JsonCreator
    public EnrichedMessage(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("url") String url, String moreData) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.moreData = moreData;
    }

    @Override
    public String toString() {
        return "EnrichedMessage(" + id + ", name=" + name + ", url=" + url + ", moreData=" + moreData + ")";
    }
}
