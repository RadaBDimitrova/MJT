package bg.sofia.uni.fmi.mjt.server;

import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryPlaylistRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemorySongRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryUserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

import static bg.sofia.uni.fmi.mjt.server.SpotifyServer.logException;
import static bg.sofia.uni.fmi.mjt.server.command.CommandManager.processCommand;

public class SpotifyClientHandler implements Runnable {
    private final Socket clientSocket;
    private final InMemoryUserRepository userRepository;
    private final InMemoryPlaylistRepository playlistRepository;
    private final InMemorySongRepository songRepository;

    public SpotifyClientHandler(Socket clientSocket, InMemoryUserRepository userRepository,
                                InMemoryPlaylistRepository playlistRepository, InMemorySongRepository songRepository) {
        this.clientSocket = clientSocket;
        this.userRepository = userRepository;
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Client Request Handler for " + clientSocket.getRemoteSocketAddress());

        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String[] tokens = inputLine.split("\\s+|(?<=<)|(?=>)"); //TODO: fix this regex and cleanup code
                tokens = Arrays.stream(tokens)
                        .filter(token -> !(token.contains("<") || token.contains(">"))).toArray(String[]::new);
                processCommand(userRepository, playlistRepository, songRepository, out, tokens);
            }

        } catch (IOException e) {
            logException(e);
            System.err.println("Client disconnected abruptly");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logException(e);
                System.err.println("Client failed to close connection");
            }
        }
    }

}

