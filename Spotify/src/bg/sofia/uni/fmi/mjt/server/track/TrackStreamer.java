package bg.sofia.uni.fmi.mjt.server.track;

import bg.sofia.uni.fmi.mjt.server.SpotifyServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TrackStreamer implements Runnable {
    private final int port;
    private final Track song;
    private final SpotifyServer spotifyServer;

    public TrackStreamer(int port, Track song, SpotifyServer spotifyServer) {
        this.port = port;
        this.song = song;
        this.spotifyServer = spotifyServer;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            try (Socket socket = serverSocket.accept();
                 BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(Files.newInputStream(
                         Path.of(Track.SONGS_PATH + song.getFileName())))) {

                byte[] toWrite = new byte[Track.BYTES];
                while (bufferedInputStream.available() > 0) {
                    int readBytes = bufferedInputStream.read(toWrite, 0, toWrite.length);

                    outputStream.write(toWrite, 0, readBytes);
                }

                outputStream.flush();
            } catch (SocketException e) {
            }
        } catch (IOException e) {
            System.out.println("A Problem occurred while streaming Song");
        }

        song.play();//TODO

        System.out.println("Song has ended");
    }
}
