package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryPlaylistRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemorySongRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryUserRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
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
    public static final int SERVER_PORT = 4444;
    private static final String SERVER_HOST = "localhost";
    private static final String EXCEPTION_LOG_FILE = "exception-log.txt";
    private static final int THREADS_COUNT = 100;
    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADS_COUNT);
    private static InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private static InMemoryPlaylistRepository playlistRepository = new InMemoryPlaylistRepository();
    private static InMemorySongRepository songRepository = new InMemorySongRepository();

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
        ExecutorService executor = Executors.newFixedThreadPool(THREADS_COUNT);

        Thread.currentThread().setName("Order Server Thread");

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            Socket clientSocket;
            while (true) {
                clientSocket = serverSocket.accept();
                SpotifyClientHandler clientHandler =
                        new SpotifyClientHandler(clientSocket, userRepository, playlistRepository, songRepository);
                executor.execute(clientHandler);
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
                executorService.submit(new SpotifyClientHandler(accept.socket(),
                        userRepository, playlistRepository, songRepository));
            }
            keyIterator.remove();
        }
    }
}
