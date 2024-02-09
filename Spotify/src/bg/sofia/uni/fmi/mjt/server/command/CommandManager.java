package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.server.exceptions.PlaylistDoesNotExist;
import bg.sofia.uni.fmi.mjt.server.exceptions.SongDoesNotExist;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryPlaylistRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemorySongRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryUserRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.PlaylistRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.SongRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.UserRepository;

import java.io.PrintWriter;
import java.util.Arrays;

public class CommandManager {

    public static void processCommand(InMemoryUserRepository userRepository,
                                      InMemoryPlaylistRepository playlistRepository,
                                      InMemorySongRepository songRepository, PrintWriter writer,
                                      String[] tokens, boolean loggedIn) {
        String commandType = tokens[0].toLowerCase();
        //TODO: refactor to use a map of commands
        //logged in logic
        switch (commandType) {
            case "register": registerUser(userRepository, writer, tokens);
                loggedIn = true;
                break;
            case "login": loginUser(userRepository, writer, tokens);
                loggedIn = true;
                break;
            case "search": searchSongs(songRepository, writer, tokens);
                break;
            case "top": topSongs(songRepository, writer, tokens);
                break;
            case "create-playlist": createPlaylist(playlistRepository, writer, tokens);
                break;
            case "add-song-to": addSongToPlaylist(playlistRepository, songRepository, writer, tokens);
                break;
            case "show-playlist": showPlaylist(playlistRepository, writer, tokens);
                break;
            case "play": playSong(songRepository, writer, tokens);
                break;
            case "stop": stopSong(songRepository, writer, tokens);
                break;
            default: writer.println("Invalid command: " + Arrays.toString(tokens));
        }
    }

    private static void stopSong(InMemorySongRepository songRepository, PrintWriter writer, String[] tokens) {
        try {
            songRepository.searchSongByName(tokens[1]).stop();
        } catch (SongDoesNotExist e) {
            writer.println("Song with that name does not exist");
        } catch (ArrayIndexOutOfBoundsException e) {
            writer.println("Invalid command arguments");
        }
        writer.println("Song stopped");
    }

    private static void showPlaylist(InMemoryPlaylistRepository playlistRepository,
                                     PrintWriter writer, String[] tokens) {
        try {
            String info = playlistRepository.loadPlaylistFromFile(tokens[1]).getPlaylistInfo();
            writer.println("Playlist: " + tokens[1] + info);
        } catch (PlaylistDoesNotExist e) {
            writer.println("Playlist with that name does not exist");
        } catch (ArrayIndexOutOfBoundsException e) {
            writer.println("Invalid command arguments");
        }
    }

    private static void registerUser(InMemoryUserRepository userRepository, PrintWriter writer, String[] tokens) {
        try {
            userRepository.createUser(tokens[1], tokens[2]);
        } catch (IllegalArgumentException e) {
            writer.println("User with that email already exists");
        } catch (ArrayIndexOutOfBoundsException e) {
            writer.println("Invalid command arguments");
        }
        writer.println("User registered successfully");
    }

    private static void loginUser(UserRepository userRepository, PrintWriter writer, String[] tokens) {
        try {
            if (!userRepository.authenticateUser(tokens[1], tokens[2])) {
                writer.println("Invalid email or password");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            writer.println("Invalid command arguments");
        }
        writer.println("User logged in successfully");
    }

    private static void createPlaylist(PlaylistRepository playlistRepository, PrintWriter writer, String[] tokens) {
        try {
            playlistRepository.createPlaylist(tokens[1]);
        } catch (IllegalArgumentException e) {
            writer.println("Playlist with that name already exists");
        } catch (ArrayIndexOutOfBoundsException e) {
            writer.println("Invalid command arguments");
        }
        writer.println("Playlist created successfully");
    }

    private static void addSongToPlaylist(PlaylistRepository playlistRepository, SongRepository songRepository,
                                          PrintWriter writer, String[] tokens) {
        try {
            playlistRepository.addSongToPlaylist(songRepository.searchSongByName(tokens[1]), tokens[2]);
        } catch (SongDoesNotExist e) {
            writer.println("Song with that name does not exist");
        } catch (PlaylistDoesNotExist e) {
            writer.println("Playlist with that name does not exist");
        } catch (ArrayIndexOutOfBoundsException e) {
            writer.println("Invalid command arguments");
        }
        writer.println("Song added to the playlist successfully");
    }

    private static void searchSongs(SongRepository songRepository, PrintWriter writer, String[] tokens) {
        try {
            writer.println("Search results: ");
            songRepository.searchSongByKeyword(tokens[1]).forEach(writer::println);
        } catch (ArrayIndexOutOfBoundsException e) {
            writer.println("Invalid command arguments");
        }
    }

    private static void topSongs(SongRepository songRepository, PrintWriter writer, String[] tokens) {
        try {
            writer.println("Top songs: ");
            songRepository.topNSongsByTimesPlayed(Integer.parseInt(tokens[1])).forEach(writer::println);
        } catch (ArrayIndexOutOfBoundsException e) {
            writer.println("Invalid command arguments");
        } catch (Exception e) {
            writer.println("Error getting top songs");
        }
    }

    private static void playSong(SongRepository songRepository, PrintWriter writer, String[] tokens) {
        writer.println("Now playing the song");
        songRepository.searchSongByName(tokens[1]).play();
    }

}

