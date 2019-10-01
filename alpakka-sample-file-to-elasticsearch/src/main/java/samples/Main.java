package samples;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.KillSwitches;
import akka.stream.Materializer;
import akka.stream.UniqueKillSwitch;
import akka.stream.alpakka.elasticsearch.ElasticsearchSourceSettings;
import akka.stream.alpakka.elasticsearch.ElasticsearchWriteSettings;
import akka.stream.alpakka.elasticsearch.WriteMessage;
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchFlow;
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchSource;
import akka.stream.alpakka.file.DirectoryChange;
import akka.stream.alpakka.file.javadsl.DirectoryChangesSource;
import akka.stream.alpakka.file.javadsl.FileTailSource;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samples.common.DateTimeExtractor;
import samples.common.RunOps;
import samples.common.RunOpsImpl;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static scala.compat.java8.FutureConverters.toJava;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private ActorSystem actorSystem;
    private Materializer materializer;
    private RestClient elasticsearchClient;

    private final RunOps runOps;
    private final String elasticsearchAddress;

    public Main(RunOps runOps) {
        this.runOps = runOps;
        this.elasticsearchAddress = runOps.elasticsearchAddress();
    }

    private final static Duration streamRuntime = Duration.ofSeconds(10L);
    private final static Integer streamParallelism = 47;

    private final static String indexName = "logs";
    private final static String typeName = "_doc";

    private final static String testDataPath = "./test-data";
    private final static String inputLogsPath = testDataPath + "/input";


    private Source<Pair<Path, DirectoryChange>, NotUsed> directoryChangesSource() {
        final FileSystem fs = FileSystems.getDefault();
        final Duration pollingInterval = Duration.ofSeconds(1);
        final int maxBufferSize = 1000;
        return DirectoryChangesSource.create(fs.getPath(inputLogsPath), pollingInterval, maxBufferSize);
    }

    private Source<LogLine, NotUsed> fileTailSource(Path path) {
        String filename = path.getFileName().toString();
        String directory = path.getParent().toString();

        return FileTailSource
                .createLines(path, 8192, Duration.ofMillis(250))
                .map(line -> {
                    log.debug("Parsed > " + line);
                    return line;
                })
                .scan(new LogAcc(), (acc, line) -> {
                    Long lineNo = acc.lineNo + 1;
                    Long date = DateTimeExtractor.extractDate(line);
                    LogLine logLine = new LogLine(line, lineNo, date, filename, directory);
                    return new LogAcc(lineNo, Optional.of(logLine));
                })
                .mapConcat(logAcc -> {
                    if (logAcc.logLine.isPresent()) {
                        ArrayList<LogLine> list = new ArrayList<>();
                        list.add(logAcc.logLine.get());
                        return list;
                    }
                    return new ArrayList<>();
                });
    }

    private CompletionStage<List<LogLine>> queryAllRecordsFromElasticsearch(
            RestClient elasticsearchClient, String indexName, ActorSystem actorSystem, Materializer materializer) {
        CompletionStage<List<LogLine>> reading =
                ElasticsearchSource.typed(
                        indexName,
                        "_doc",
                        "{\"match_all\": {}}",
                        ElasticsearchSourceSettings.create(),
                        elasticsearchClient,
                        LogLine.class)
                        .map(readResult -> readResult.source())
                        .runWith(Sink.seq(), materializer);
        reading.thenAccept(non -> log.info("Reading finished"));
        return reading;
    }

    private void printResults(List<LogLine> results, Map<Pair<String, String>, LogFileSummary> summaries) {
        results.stream().forEach(result -> log.debug("Results < " + result));

        String fmt = "%-32s%-32s%-16s%-16s%s";
        String header = String.format(fmt, "Directory", "File", "First Seen", "Last Updated", "Number of Lines");
        String summariesStr = summaries
                .values()
                .stream()
                .map(s -> String.format(fmt, s.directory, s.filename, s.firstSeen, s.lastUpdated, s.numberOfLines))
                .collect(Collectors.joining("\n"));

        log.info("LogFileSummaries:\n" + header + "\n" + summariesStr);
    }

    private CompletionStage<Terminated> run() throws Exception {
        this.actorSystem = ActorSystem.create();
        this.materializer = ActorMaterializer.create(actorSystem);
        this.elasticsearchClient = RestClient.builder(HttpHost.create(this.elasticsearchAddress)).build();

        RunnableGraph<Pair<UniqueKillSwitch, CompletionStage<HashMap<Pair<String, String>, LogFileSummary>>>> graph = directoryChangesSource()
                .filter(pair -> pair.second() == DirectoryChange.Creation)
                .map(pair -> {
                    Path path = pair.first();
                    log.info("File create detected: " + path.toString());
                    return fileTailSource(path);
                })
                .flatMapMerge(streamParallelism, source -> source)
                .map(WriteMessage::createIndexMessage)
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
                })
                .scan(new HashMap<Pair<String, String>, LogFileSummary>(), (summaries, logLine) -> {
                    Pair<String, String> key = Pair.create(logLine.directory, logLine.filename);
                    Long timestamp = runOps.now();
                    if (summaries.containsKey(key)) {
                        LogFileSummary summary = summaries.get(key);
                        LogFileSummary newSummary = new LogFileSummary(summary.directory, summary.filename, summary.firstSeen, timestamp, logLine.lineNo);
                        summaries.put(key, newSummary);
                    } else {
                        LogFileSummary newSummary = new LogFileSummary(logLine.directory, logLine.filename, timestamp, timestamp, logLine.lineNo);
                        summaries.put(key, newSummary);
                    }
                    return summaries;
                })
                .viaMat(KillSwitches.single(), Keep.right())
                .toMat(Sink.last(), Keep.both());

        toJava(runOps.deleteAllFilesFrom(inputLogsPath, materializer)).toCompletableFuture().get(10, TimeUnit.SECONDS);

        Pair<UniqueKillSwitch, CompletionStage<HashMap<Pair<String, String>, LogFileSummary>>> running = graph.run(materializer);
        UniqueKillSwitch control = running.first();
        CompletionStage<HashMap<Pair<String, String>, LogFileSummary>> stream = running.second();

        toJava(runOps.copyTestDataTo(testDataPath, inputLogsPath, materializer)).toCompletableFuture().get(10, TimeUnit.SECONDS);

        log.info("Running index stream for" + streamRuntime.toString());
        Thread.sleep(streamRuntime.toMillis());
        log.info("Shutting down index stream");
        control.shutdown();
        log.info("Wait for index stream to shutdown");
        Map<Pair<String, String>, LogFileSummary> summaries = stream.toCompletableFuture().get(10, TimeUnit.SECONDS);

        List<LogLine> results = queryAllRecordsFromElasticsearch(elasticsearchClient, indexName, actorSystem, materializer).toCompletableFuture().get(10, TimeUnit.SECONDS);

        printResults(results, summaries);

        toJava(runOps.deleteAllFilesFrom(inputLogsPath, materializer)).toCompletableFuture().get(10, TimeUnit.SECONDS);

        return toJava(runOps.shutdown(actorSystem, elasticsearchClient));
    }

    public static void main(String[] args) throws Exception {
        RunOpsImpl runOps = new RunOpsImpl();
        Main main = new Main(runOps);
        CompletionStage<Terminated> run = main.run();

        run.toCompletableFuture().get(10, TimeUnit.SECONDS);
    }
}
