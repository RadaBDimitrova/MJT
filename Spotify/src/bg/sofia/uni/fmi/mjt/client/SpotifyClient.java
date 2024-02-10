package bg.sofia.uni.fmi.mjt.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Scanner;

import static bg.sofia.uni.fmi.mjt.server.SpotifyServer.logException;

public class SpotifyClient {

    private static final int SERVER_PORT = 4444;
    private static final String PLAY_COMMAND = "play";
    private static final String STOP_COMMAND = "stop";

    private static final String WAV_PATH = "src/songs/";

    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("Client thread " + socket.getLocalPort());

            AudioClient audioClient = new AudioClient();

            while (true) {
                String message = scanner.nextLine();

                if ("disconnect".equals(message)) {
                    System.out.println("Disconnected from the server");
                    break;
                }
                if (message.startsWith(STOP_COMMAND)) {
                    audioClient.stopSong();
                }

                writer.println(message);
                String reply = reader.readLine();
                System.out.println(reply);

                if (message.startsWith(PLAY_COMMAND)) {
                    audioClient.playSong(WAV_PATH+"Smooth_Sade.wav");
                }
            }

        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("There is a problem with the network communication", e);
        }
    }
}
