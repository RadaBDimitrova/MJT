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
    public static void main(String[] args) {

        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            Thread.currentThread().setName("Client thread " + socket.getLocalPort());

            while (true) {
                String message = scanner.nextLine();

                if ("disconnect".equals(message)) {
                    System.out.println("Disconnected from the server");
                    break;
                }

                writer.println(message);
                String reply = reader.readLine();
                System.out.println(reply);
            }

        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("There is a problem with the network communication", e);
        }
    }
}