package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.server.command.CommandManager;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryPlaylistRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemorySongRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryUserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SpotifyClientHandler implements Runnable {
    private final Socket clientSocket;
    private final InMemoryUserRepository userRepository;
    private final InMemoryPlaylistRepository playlistRepository;
    private final InMemorySongRepository songRepository;
    private final boolean loggedIn = false;

    public SpotifyClientHandler(Socket clientSocket, InMemoryUserRepository userRepository,
                                InMemoryPlaylistRepository playlistRepository, InMemorySongRepository songRepository) {
        this.clientSocket = clientSocket;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Received message: " + clientMessage);
                String[] tokens = clientMessage.split("\\s+");
                CommandManager.processCommand(userRepository, playlistRepository, songRepository,
                        writer, tokens, loggedIn);
            }
        } catch (IOException e) {
            System.err.println("Client disconnected abruptly");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Client failed to close connection");
            }
        }
    }

}

