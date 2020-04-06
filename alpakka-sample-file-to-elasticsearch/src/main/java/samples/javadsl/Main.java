package samples.javadsl;

import akka.Done;
import akka.NotUsed;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.japi.Pair;
import akka.stream.KillSwitches;
import akka.stream.UniqueKillSwitch;
import akka.stream.alpakka.elasticsearch.ElasticsearchSourceSettings;
import akka.stream.alpakka.elasticsearch.ElasticsearchWriteSettings;
import akka.stream.alpakka.elasticsearch.ReadResult;
import akka.stream.alpakka.elasticsearch.WriteMessage;
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchFlow;
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchSource;
import akka.stream.alpakka.file.DirectoryChange;
import akka.stream.alpakka.file.javadsl.DirectoryChangesSource;
import akka.stream.alpakka.file.javadsl.FileTailSource;
import akka.stream.javadsl.*;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samples.common.DateTimeExtractor;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private ActorSystem<?> system;
    private RestClient elasticsearchClient;

    private final String elasticsearchAddress;

    public Main() {
        this.elasticsearchAddress = RunOps.elasticsearchAddress();
    }

    private final static Duration streamRuntime = Duration.ofSeconds(10L);
    private final static Integer streamParallelism = 47;

    private final static String indexName = "logs";
    private final static String typeName = "_doc";

    // #directory-change-source

    private final static String testDataPath = "./test-data";
    private final static String inputLogsPath = testDataPath + "/input"; // watch directory (4)

    private Source<Pair<Path, DirectoryChange>, NotUsed> directoryChangesSource() {
        final FileSystem fs = FileSystems.getDefault();
        final Duration pollingInterval = Duration.ofSeconds(1);
        final int maxBufferSize = 1000;
        return DirectoryChangesSource.create(fs.getPath(inputLogsPath), pollingInterval, maxBufferSize); // source (3)
    }

    // #directory-change-source

    // #tail-logs

    private Flow<Pair<Path, DirectoryChange>, Source<LogLine, NotUsed>, NotUsed> tailNewLogs() {
        return Flow.<Pair<Path, DirectoryChange>>create()
                // only watch for file creation events (5)
                .filter(pair -> pair.second() == DirectoryChange.Creation)
                .map(pair -> {
                    Path path = pair.first();
                    log.info("File create detected: {}", path.toString());
                    // create a new `FileTailSource` and return it as a sub-stream (6)
                    return fileTailSource(path);
                });
    }

    private Source<LogLine, NotUsed> fileTailSource(Path path) {
        String filename = path.getFileName().toString();
        String directory = path.getParent().toString();

        // create `FileTailSource` for a given `path` (7)
        return FileTailSource
                .createLines(path, 8192, Duration.ofMillis(250))
                .map(line -> {
                    log.debug("Parsed > {}", line);
                    return line;
                })
                .scan(new LogAcc(), (acc, line) -> {
                    // count each line from the log file (8)
                    Long lineNo = acc.lineNo + 1;
                    // extract the date timestamp from the log line (9)
                    Long date = DateTimeExtractor.extractDate(line);
                    // create a `LogLine` record (10)
                    LogLine logLine = new LogLine(line, lineNo, date, filename, directory);
                    return new LogAcc(lineNo, Optional.of(logLine));
                })
                .mapConcat(logAcc -> {
                    if (logAcc.logLine.isPresent()) {
                        Collections.singletonList(logAcc.logLine.get());
                    }
                    return Collections.emptyList();
                });
    }

    // #tail-logs

    // #es-index-flow

    private Flow<LogLine, LogLine, NotUsed> elasticsearchIndexFlow() {
        return Flow.<LogLine>create()
                // create an ES index wrapper message for `LogLine` (11)
                .map(WriteMessage::createIndexMessage)
                // use Alpakka Elasticsearch to create a new `LogLine` record. (12)
                // takes `ObjectMapper` for `LogLine` for serialization
                .via(ElasticsearchFlow.create(
                        indexName,
                        typeName,
                        ElasticsearchWriteSettings.create(),
                        elasticsearchClient,
                        JsonMappers.mapper))
                .map(writeResult -> {
                    writeResult
                            .getError()
                            .ifPresent(errorJson -> {
                                throw new RuntimeException("Elasticsearch update failed "
                                        + writeResult.getErrorReason().orElse(errorJson));
                            });
                    return writeResult.message().source().get();
                });
    }

    // #es-index-flow

    // #summarize-log-stats-flow

    private Flow<LogLine, HashMap<Pair<String, String>, LogFileSummary>, NotUsed> summarizeLogStatsFlow() {
        return Flow.<LogLine>create()
                // track statistics per log file (13)
                .scan(new HashMap<>(), (summaries, logLine) -> {
                    Pair<String, String> key = Pair.create(logLine.directory, logLine.filename);
                    Long timestamp = RunOps.now();
                    if (summaries.containsKey(key)) {
                        LogFileSummary summary = summaries.get(key);
                        LogFileSummary newSummary = new LogFileSummary(summary.directory, summary.filename, summary.firstSeen, timestamp, logLine.lineNo);
                        summaries.put(key, newSummary);
                    } else {
                        LogFileSummary newSummary = new LogFileSummary(logLine.directory, logLine.filename, timestamp, timestamp, logLine.lineNo);
                        summaries.put(key, newSummary);
                    }
                    return summaries;
                });
    }

    // #summarize-log-stats-flow

    // #query-elasticsearch

    private CompletionStage<List<LogLine>> queryAllRecordsFromElasticsearch(RestClient elasticsearchClient, String indexName, ActorSystem system) {
        CompletionStage<List<LogLine>> reading =
                // use Alpakka Elasticsearch to return all entries from the provided index (14)
                ElasticsearchSource
                        .typed(
                            indexName,
                            "_doc",
                            "{\"match_all\": {}}",
                            ElasticsearchSourceSettings.create(),
                            elasticsearchClient,
                            LogLine.class)
                        .map(ReadResult::source)
                        .runWith(Sink.seq(), system);
        reading.thenAccept(non -> log.info("Reading finished"));
        return reading;
    }

    // #query-elasticsearch

    private void printResults(List<LogLine> results, Map<Pair<String, String>, LogFileSummary> summaries) {
        results.stream().forEach(result -> log.debug("Results < {}", result.toString()));

        String fmt = "%-32s%-32s%-16s%-16s%s";
        String header = String.format(fmt, "Directory", "File", "First Seen", "Last Updated", "Number of Lines");
        String summariesStr = summaries
                .values()
                .stream()
                .map(s -> String.format(fmt, s.directory, s.filename, s.firstSeen, s.lastUpdated, s.numberOfLines))
                .collect(Collectors.joining("\n"));

        log.info("LogFileSummaries:\n{}\n{}", header, summariesStr);
    }

    private CompletionStage<Done> run() throws Exception {
        this.system = ActorSystem.create(Behaviors.empty(), "FileToElasticSearch");
        this.elasticsearchClient = RestClient.builder(HttpHost.create(this.elasticsearchAddress)).build();

        // #stream-composing

        // compose stream together starting with the `DirectoryChangesSource` (15)
        RunnableGraph<Pair<UniqueKillSwitch, CompletionStage<HashMap<Pair<String, String>, LogFileSummary>>>> graph = directoryChangesSource()
                // create `FileTailSource` sub-streams
                .via(tailNewLogs())
                // merge the sub-streams together and emit all file `LogLine` records downstream
                .flatMapMerge(streamParallelism, source -> source)
                // index into Elasticsearch
                .via(elasticsearchIndexFlow())
                // summarize log statistics
                .via(summarizeLogStatsFlow())
                // create a `KillSwitch` so we can shutdown the stream from the outside. use this as the materialized value.
                .viaMat(KillSwitches.single(), Keep.right())
                // materialize the last recorded log stats summarization.
                // return both a `UniqueKillSwitch` `CompletionStage<Map<Pair<String, String>, LogFileSummary>>`
                .toMat(Sink.last(), Keep.both());

        // #stream-composing

        // #running-the-app

        RunOps.deleteAllFilesFrom(inputLogsPath, system).toCompletableFuture().get(10, TimeUnit.SECONDS);

        // run the graph and capture the materialized values (16)
        Pair<UniqueKillSwitch, CompletionStage<HashMap<Pair<String, String>, LogFileSummary>>> running = graph.run(system);
        UniqueKillSwitch control = running.first();
        CompletionStage<HashMap<Pair<String, String>, LogFileSummary>> stream = running.second();

        RunOps.copyTestDataTo(testDataPath, inputLogsPath, system).toCompletableFuture().get(10, TimeUnit.SECONDS);

        log.info("Running index stream for ", streamRuntime.toString());
        Thread.sleep(streamRuntime.toMillis());
        log.info("Shutting down index stream");
        control.shutdown();
        log.info("Wait for index stream to shutdown");
        Map<Pair<String, String>, LogFileSummary> summaries = stream.toCompletableFuture().get(10, TimeUnit.SECONDS);

        // run a new graph to query all records from Elasticsearch and get the results (17)
        List<LogLine> results = queryAllRecordsFromElasticsearch(elasticsearchClient, indexName, system).toCompletableFuture().get(10, TimeUnit.SECONDS);

        printResults(results, summaries);

        RunOps.deleteAllFilesFrom(inputLogsPath, system).toCompletableFuture().get(10, TimeUnit.SECONDS);

        return RunOps.shutdown(system, elasticsearchClient);

        // #running-the-app
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        CompletionStage<Done> run = main.run();

        run.toCompletableFuture().get(10, TimeUnit.SECONDS);
    }
}
