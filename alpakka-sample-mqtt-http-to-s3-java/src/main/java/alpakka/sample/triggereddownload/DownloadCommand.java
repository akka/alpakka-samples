package alpakka.sample.triggereddownload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

final class DownloadCommand {
    public final Instant timestamp;
    public final String url;

    @JsonCreator
    public DownloadCommand(@JsonProperty("timestamp") Instant timestamp, @JsonProperty("url") String url) {
        this.timestamp = timestamp;
        this.url = url;
    }

    @Override
    public String toString() {
        return "DownloadCommand(" + timestamp.toString() + ", url=" + url + ")";
    }
}
