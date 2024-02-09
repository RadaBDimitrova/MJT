package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryPlaylistRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemorySongRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryUserRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SpotifyServer {
    public static final int SERVER_PORT = 9999;
    private static final String SERVER_HOST = "localhost";
    private static final String EXCEPTION_LOG_FILE = "exception-log.txt";

    private static final int BUFFER_SIZE = 1024;
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(100);
    private static final InMemoryUserRepository USER_REPOSITORY = new InMemoryUserRepository();
    private static final InMemoryPlaylistRepository PLAYLIST_REPOSITORY = new InMemoryPlaylistRepository();
    private static final InMemorySongRepository SONG_REPOSITORY = new InMemorySongRepository();

    static {
        try {
            PrintStream fileStream = new PrintStream(new FileOutputStream(EXCEPTION_LOG_FILE));
            System.setErr(fileStream);
        } catch (IOException e) {
            logException(e);
            throw new RuntimeException("Problem with the exception log file", e);
        }
    }

    public static void logException(Throwable t) {
        System.err.println("Exception logged: " + t.getMessage());
    }

    public static void main(String[] args) {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.bind(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }
                handleSelectedKeys(selector, buffer);
            }
        } catch (IOException e) {
            logException(e);
            throw new RuntimeException("Problem with the server socket", e);
        }
    }

    private static void handleSelectedKeys(Selector selector, ByteBuffer buffer) throws IOException {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();

                buffer.clear();
                int req = sc.read(buffer);
                if (req < 0) {
                    System.out.println("Client has closed the connection");
                    sc.close();
                    continue;
                }
                buffer.flip();
                sc.write(buffer);

            } else if (key.isAcceptable()) {
                ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                SocketChannel accept = sockChannel.accept();
                accept.configureBlocking(false);
                accept.register(selector, SelectionKey.OP_READ);
                EXECUTOR_SERVICE.submit(new SpotifyClientHandler(accept.socket(),
                        USER_REPOSITORY, PLAYLIST_REPOSITORY, SONG_REPOSITORY));
            }
            keyIterator.remove();
        }
    }
}
