package bg.sofia.uni.fmi.mjt.server.command;

import bg.sofia.uni.fmi.mjt.server.exceptions.PlaylistDoesNotExist;
import bg.sofia.uni.fmi.mjt.server.exceptions.SongDoesNotExist;
import bg.sofia.uni.fmi.mjt.server.exceptions.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryPlaylistRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemorySongRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.InMemoryUserRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.PlaylistRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.SongRepository;
import bg.sofia.uni.fmi.mjt.server.repositories.UserRepository;

import java.io.PrintWriter;
import java.util.Arrays;

import static bg.sofia.uni.fmi.mjt.server.SpotifyServer.logException;

public class CommandManager {
    private static Boolean loggedIn = false;

    public static void processCommand(InMemoryUserRepository userRepository,
                                      InMemoryPlaylistRepository playlistRepository,
                                      InMemorySongRepository songRepository, PrintWriter writer,
                                      String[] tokens) {
        String commandType = tokens[0].toLowerCase();
        if (!loggedIn) {
            handleNotLoggedIn(userRepository, writer, tokens);
        } else {
            handleLoggedIn(commandType, playlistRepository, songRepository, writer, tokens);
        }
    }

    private static void handleNotLoggedIn(InMemoryUserRepository userRepository, PrintWriter writer, String[] tokens) {
        String commandType = tokens[0].toLowerCase();
        if (commandType.equals("register")) {
            registerUser(userRepository, writer, tokens);
            loggedIn = true;
        } else if (commandType.equals("login")) {
            loginUser(userRepository, writer, tokens);
            loggedIn = true;
        } else {
            writer.println("You need to be logged in to use this command");
        }
    }

    private static void handleLoggedIn(String commandType, InMemoryPlaylistRepository playlistRepository,
                                       InMemorySongRepository songRepository,
                                       PrintWriter writer, String[] tokens) {
        switch (commandType) {
            case "register", "login":
                writer.println("You are already logged in");
                break;
            case "search":
                searchSongs(songRepository, writer, tokens);
                break;
            case "top":
                topSongs(songRepository, writer, tokens);
                break;
            case "create-playlist":
                createPlaylist(playlistRepository, writer, tokens);
                break;
            case "add-song-to":
                addSongToPlaylist(playlistRepository, songRepository, writer, tokens);
                break;
            case "show-playlist":
                showPlaylist(playlistRepository, writer, tokens);
                break;
            case "play":
                playSong(songRepository, writer, tokens);
                break;
            case "stop":
                stopSong(songRepository, writer, tokens);
                break;
            default:
                writer.println("Invalid command: " + Arrays.toString(tokens));
        }
    }

    private static void stopSong(InMemorySongRepository songRepository, PrintWriter writer, String[] tokens) {
        try {
            songRepository.searchSongByName(tokens[1], tokens[2]).stop();
        } catch (SongDoesNotExist e) {
            logException(e);
            writer.println("Song with that name does not exist");
        } catch (ArrayIndexOutOfBoundsException e) {
            logException(e);
            writer.println("Invalid command arguments");
        }
        writer.println("Song stopped");
    }

    private static void showPlaylist(InMemoryPlaylistRepository playlistRepository,
                                     PrintWriter writer, String[] tokens) {
        try {
            String info = playlistRepository.getPlaylist(tokens[1]).getPlaylistInfo();
            writer.println(tokens[1] + ": " + info);
        } catch (PlaylistDoesNotExist e) {
            logException(e);
            writer.println("Playlist with that name does not exist");
        } catch (ArrayIndexOutOfBoundsException e) {
            logException(e);
            writer.println("Invalid command arguments");
        }
    }

    private static void registerUser(InMemoryUserRepository userRepository, PrintWriter writer, String[] tokens) {
        try {
            userRepository.createUser(tokens[1], tokens[2]);
        } catch (UserAlreadyExistsException e) {
            logException(e);
            writer.println("User with that email already exists");
        } catch (ArrayIndexOutOfBoundsException e) {
            logException(e);
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
            logException(e);
            writer.println("Invalid command arguments");
        }
        writer.println("User logged in successfully");
    }

    private static void createPlaylist(PlaylistRepository playlistRepository, PrintWriter writer, String[] tokens) {
        try {
            playlistRepository.createPlaylist(tokens[1]);
        } catch (IllegalArgumentException e) {
            logException(e);
            writer.println("Playlist with that name already exists");
        } catch (ArrayIndexOutOfBoundsException e) {
            logException(e);
            writer.println("Invalid command arguments");
        }
        writer.println("Playlist created successfully");
    }

    private static void addSongToPlaylist(PlaylistRepository playlistRepository, SongRepository songRepository,
                                          PrintWriter writer, String[] tokens) {
        try {
            String[] songTokens = tokens[2].split(",");
            songTokens[1] = songTokens[1].replace("_", " ");
            songTokens[0] = songTokens[0].replace("_", " "); //TODO: clean up this code
            playlistRepository.addSongToPlaylist(songRepository
                    .searchSongByName(songTokens[0], songTokens[1]), tokens[1]);
            writer.println("Song added to the playlist successfully");
        } catch (SongDoesNotExist e) {
            logException(e);
            writer.println("Song with that name does not exist");
        } catch (PlaylistDoesNotExist e) {
            logException(e);
            writer.println("Playlist with that name does not exist");
        } catch (ArrayIndexOutOfBoundsException e) {
            logException(e);
            writer.println("Invalid command arguments for add-song-to");
        }
    }

    private static void searchSongs(SongRepository songRepository, PrintWriter writer, String[] tokens) {
        try {
            StringBuilder searchResults = new StringBuilder();
            songRepository.searchSongByKeyword(tokens[1])
                    .forEach(track -> searchResults.append(track.print()).append(", "));
            writer.println(searchResults);

        } catch (ArrayIndexOutOfBoundsException e) {
            logException(e);
            writer.println("Invalid command arguments");
        }
    }

    private static void topSongs(SongRepository songRepository, PrintWriter writer, String[] tokens) {
        try {
            StringBuilder searchResults = new StringBuilder();
            songRepository.topNSongsByTimesPlayed(Integer.parseInt(tokens[1]))
                    .forEach(track -> searchResults.append(track.print()).append(", "));
            writer.println(searchResults);
        } catch (ArrayIndexOutOfBoundsException e) {
            logException(e);
            writer.println("Invalid command arguments");
        } catch (Exception e) {
            logException(e);
            writer.println("Error getting top songs");
        }
    }

    private static void playSong(SongRepository songRepository, PrintWriter writer, String[] tokens) {
        writer.println("Now playing the song");
        String[] songTokens = tokens[2].split(",");
        songTokens[1] = songTokens[1].replace("_", " ");
        songTokens[0] = songTokens[0].replace("_", " ");
        try {
            songRepository.searchSongByName(songTokens[0], songTokens[1]).play();
        } catch (SongDoesNotExist e) {
            logException(e);
            writer.println("Song with that name does not exist");
        } catch (ArrayIndexOutOfBoundsException e) {
            logException(e);
            writer.println("Invalid command arguments");
        }
    }

}

