package samples;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Optional;

public class LogLine {
    public final String line;
    public final Long lineNo;
    public final Long date;
    public final String filename;
    public final String directory;

    @JsonCreator
    public LogLine(
            @JsonProperty("line") String line,
            @JsonProperty("lineNo") Long lineNo,
            @JsonProperty("date") Long date,
            @JsonProperty("filename") String filename,
            @JsonProperty("directory") String directory) {
        this.line = line;
        this.lineNo = lineNo;
        this.date = date;
        this.filename = filename;
        this.directory = directory;
    }

    @Override
    public java.lang.String toString() {
        return "LogLine(line=\"" + line + "\", lineNo=" + lineNo.toString() + ", date=" + date.toString() +
                ", filename=\"" + filename + "\", directory=\"" + directory + "\")";
    }
}

class JsonMappers {
    // Jackson conversion setup
    public final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    public final static ObjectWriter logLineWriter = mapper.writerFor(LogLine.class);
    public final static ObjectReader logLineReader = mapper.readerFor(LogLine.class);
}

class LogAcc {
    public final Long lineNo;
    public final Optional<LogLine> logLine;

    public LogAcc() {
        this.lineNo = 0L;
        this.logLine = Optional.empty();
    }

    public LogAcc(Long lineNo, Optional<LogLine> logLine) {
        this.lineNo = lineNo;
        this.logLine = logLine;
    }
}

class LogFileSummary {
    public final String directory;
    public final String filename;
    public final Long firstSeen;
    public final Long lastUpdated;
    public final Long numberOfLines;

    public LogFileSummary(String directory, String filename, Long firstSeen, Long lastUpdated, Long numberOfLines) {
        this.directory = directory;
        this.filename = filename;
        this.firstSeen = firstSeen;
        this.lastUpdated = lastUpdated;
        this.numberOfLines = numberOfLines;
    }
}