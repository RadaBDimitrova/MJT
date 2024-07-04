package bg.sofia.uni.fmi.mjt.client;

import bg.sofia.uni.fmi.mjt.server.command.CommandManager;
import bg.sofia.uni.fmi.mjt.server.track.Track;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static bg.sofia.uni.fmi.mjt.server.SpotifyClientHandler.getTokens;
import static bg.sofia.uni.fmi.mjt.server.SpotifyServer.logException;
import static bg.sofia.uni.fmi.mjt.server.command.CommandManager.getSongTokens;

public class SpotifyClient {

    private static final int SERVER_PORT = 4444;
    private static final String PLAY_COMMAND = "play";
    private static final String STOP_COMMAND = "stop";

    private static final String WAV_PATH = "src/songs/";

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in);
             ExecutorService executorService = Executors.newFixedThreadPool(2)) {

            Thread.currentThread().setName("Client thread " + socket.getLocalPort());
            AudioClient audioClient = new AudioClient();

            while (true) {
                String message = scanner.nextLine();
                if ("disconnect".equals(message)) {
                    System.out.println("Disconnected from the server");
                    CommandManager.setLoggedIn(false);
                    socket.close();
                    break;
                }
                writer.println(message);
                String reply = reader.readLine();
                System.out.println(reply);

                if (message.startsWith(PLAY_COMMAND)) {
                    handlePlayCommand(message, executorService, audioClient, scanner);
                }
                if (socket.isClosed()) {
                    break;
                }
            }
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("There is a problem with the network communication", e);
        }
    }

    private static void handlePlayCommand(String message, ExecutorService executorService,
                                          AudioClient audioClient, Scanner scanner) {
        String[] tokens = getTokens(message);
        String[] songTokens = getSongTokens(tokens);
        String path = WAV_PATH + songTokens[0] + "_" + songTokens[1] + Track.WAV;
        executorService.execute(() -> audioClient.playSong(path));
        String stopMessage = scanner.nextLine();
        if (stopMessage != null && stopMessage.startsWith(STOP_COMMAND)) {
            executorService.execute(audioClient::stopSong);
        }
    }
}
