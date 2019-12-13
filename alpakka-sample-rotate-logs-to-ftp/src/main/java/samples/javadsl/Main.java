package samples.javadsl;

// #imports
import java.net.InetAddress;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.FileSystem;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.japi.Pair;
import akka.japi.function.Creator;
import akka.japi.function.Function;
import akka.stream.IOResult;
import akka.stream.alpakka.file.javadsl.Directory;
import akka.stream.alpakka.file.javadsl.LogRotatorSink;
import akka.stream.alpakka.ftp.javadsl.Sftp;

import akka.stream.alpakka.ftp.FtpCredentials;
import akka.stream.alpakka.ftp.SftpIdentity;
import akka.stream.alpakka.ftp.KeyFileSftpIdentity;
import akka.stream.alpakka.ftp.SftpSettings;
import akka.stream.javadsl.Compression;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

import org.apache.mina.util.AvailablePortFinder;
import playground.filesystem.FileSystemMock;
import playground.SftpServerEmbedded;

import akka.stream.javadsl.Sink;
// #imports

public class Main {

    private void run() throws IOException {
        final ActorSystem<Void> actorSystem = ActorSystem.create(Behaviors.empty(), "RotateLogsToFtp");

        final FileSystem ftpFileSystem = new FileSystemMock().fileSystem;

        final String privateKeyPassphrase = new String(SftpServerEmbedded.clientPrivateKeyPassphrase());
        final String pathToIdentityFile = SftpServerEmbedded.clientPrivateKeyFile();
        final String username = "username";
        final String password = username;
        final String hostname = "localhost";

        int port = AvailablePortFinder.getNextAvailable(21_000);

        Path home = ftpFileSystem.getPath(SftpServerEmbedded.FtpRootDir()).resolve("tmp");
        if (!Files.exists(home)) Files.createDirectories(home);

        SftpServerEmbedded.start(ftpFileSystem, port);

        // #sample
        Iterator<ByteString> data =
                Arrays.asList('a', 'b', 'c', 'd').stream()
                        .map(
                                e -> {
                                    char[] arr = new char[100];
                                    Arrays.fill(arr, e);
                                    return ByteString.fromString(String.valueOf(arr));
                                })
                        .iterator();

        // (2)
        Creator<Function<ByteString, Optional<String>>> rotator =
                () -> {
                    final char[] last = {' '};
                    return (bs) -> {
                        char c = (char) bs.head();
                        if (c != last[0]) {
                            last[0] = c;
                            return Optional.of("log-" + c + ".z");
                        } else {
                            return Optional.empty();
                        }
                    };
                };

        // (3)
        KeyFileSftpIdentity identity =
                SftpIdentity.createFileSftpIdentity(pathToIdentityFile, privateKeyPassphrase.getBytes());
        SftpSettings settings =
                SftpSettings.create(InetAddress.getByName(hostname))
                        .withPort(port)
                        .withSftpIdentity(identity)
                        .withStrictHostKeyChecking(false)
                        .withCredentials(FtpCredentials.create(username, password));

        Function<String, Sink<ByteString, CompletionStage<IOResult>>> sink =
                path -> {
                    Sink<ByteString, CompletionStage<IOResult>> ftpSink = Sftp.toPath("tmp/" + path, settings);
                    return Flow.<ByteString>create()
                            .via(Compression.gzip()) // (4)
                            .toMat(ftpSink, Keep.right());
                };

        CompletionStage<Done> completion =
                Source.fromIterator(() -> data)
                        .runWith(LogRotatorSink.withSinkFactory(rotator, sink), actorSystem);
        // #sample

        completion
                .thenApply(
                        (i) ->
                                Directory.ls(home)
                                        .runForeach((f) -> System.out.println(f.toString()), actorSystem))
                .whenComplete(
                        (res, ex) -> {
                            if (ex != null) {
                                ex.printStackTrace();
                            }
                            actorSystem.terminate();
                            actorSystem.getWhenTerminated().thenAccept(t -> SftpServerEmbedded.stopServer());
                        });
    }

    public static void main(String[] args) throws IOException {
        new Main().run();
    }
}
