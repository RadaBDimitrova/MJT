package bg.sofia.uni.fmi.mjt.server.repositories;

import bg.sofia.uni.fmi.mjt.server.exceptions.PlaylistDoesNotExist;
import bg.sofia.uni.fmi.mjt.server.exceptions.SongDoesNotExist;
import bg.sofia.uni.fmi.mjt.server.playlist.Playlist;
import bg.sofia.uni.fmi.mjt.server.track.Track;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static bg.sofia.uni.fmi.mjt.server.SpotifyServer.logException;

public class InMemoryPlaylistRepository implements PlaylistRepository {
    private static final String PLAYLISTS = "src/playlists/playlists.txt";
    private static final String TXT = ".txt";
    private Map<String, Playlist> playlists = new ConcurrentHashMap<>();

    public InMemoryPlaylistRepository() {
        loadAllPlaylistsFromFile();
    }

    @Override
    public void addSongToPlaylist(Track song, String playlistName) {
        if (playlistName == null) {
            throw new SongDoesNotExist("Playlist name cannot be null");
        }
        if (playlists.get(playlistName + TXT).getTracks().containsKey(song.name())) {
            throw new IllegalArgumentException("Song already exists in playlist");
        }
        if (playlists.containsKey(playlistName + TXT)) {
            playlists.get(playlistName + TXT).addTrack(song);
            savePlaylistToFile(playlistName);
        } else {
            PlaylistDoesNotExist exception =
                    new PlaylistDoesNotExist("Playlist with name " + playlistName + " does not exist");
            logException(exception);
            throw exception;
        }
    }

    @Override
    public void removeSongFromPlaylist(Track song, String playlistName) {
        if (playlists.containsKey(playlistName + TXT)) {
            playlists.get(playlistName + TXT).removeTrack(song);
            savePlaylistToFile(playlistName);
        } else {
            PlaylistDoesNotExist exception =
                    new PlaylistDoesNotExist("Playlist with name " + playlistName + " does not exist");
            logException(exception);
            throw exception;
        }
    }

    @Override
    public void createPlaylist(String playlistName) {
        if (playlists.containsKey(playlistName + TXT)) {
            throw new IllegalArgumentException("Playlist with name " + playlistName + " already exists");
        }
        playlists.put(playlistName + TXT, new Playlist(playlistName, new ConcurrentHashMap<>()));
        saveNewPlaylistToPlaylistNamesFile(playlistName);
        savePlaylistToFile(playlistName);
        loadAllPlaylistsFromFile();
    }

    @Override
    public void deletePlaylist(String playlistName) {
        playlists.remove(playlistName + TXT);
        deletePlaylistFromFile(playlistName);
        deletePlaylistFromPlaylistNamesFile(playlistName);
    }

    private void deletePlaylistFromPlaylistNamesFile(String playlistName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PLAYLISTS));
             BufferedWriter writer = new BufferedWriter(new FileWriter(PLAYLISTS))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(playlistName + TXT)) {
                    lines.add(line);
                }
            }
            reader.close();
            for (String updated : lines) {
                writer.write(updated);
                writer.newLine();
            }
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("Error deleting playlist from file of playlist names: " + playlistName, e);
        }
    }

    private void deletePlaylistFromFile(String playlistName) {
        Path path = Paths.get(playlistName + TXT);
        try {
            Files.delete(path);
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("Error deleting file of playlist " + playlistName, e);
        }
    }

    private void saveNewPlaylistToPlaylistNamesFile(String playlistName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PLAYLISTS, true))) {
            writer.write(playlistName + TXT + System.lineSeparator());
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("Error saving new playlist to file of playlist " + playlistName + TXT, e);
        }
    }

    @Override
    public Playlist getPlaylist(String playlistName) {
        Playlist result = playlists.get(playlistName + TXT);
        if (result == null) {
            PlaylistDoesNotExist exception =
                    new PlaylistDoesNotExist("Playlist with name " + playlistName + " does not exist");
            logException(exception);
            throw exception;
        }
        return result;
    }

    @Override
    public void savePlaylistToFile(String playlistName) {
        Playlist playlist = playlists.get(playlistName + TXT);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playlistName + TXT))) {
            writer.write(playlist.getName() + System.lineSeparator());
            for (Track track : playlist.getTracks().values()) {
                writer.write(track.name() + "," + track.artist() + ","
                        + track.timesPlayed() + System.lineSeparator());
            }
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("Error during saving of playlist", e);
        }
    }

    @Override
    public Playlist loadPlaylistFromFile(String playlistName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(playlistName))) {
            String name = reader.readLine();
            if (name == null) {
                PlaylistDoesNotExist exception =
                        new PlaylistDoesNotExist("Playlist not found in file, name cannot be null");
                logException(exception);
                throw exception;
            }
            Map<String, Track> tracks = new ConcurrentHashMap<>();
            String track;
            while ((track = reader.readLine()) != null) {
                String[] trackInfo = track.split(",");
                tracks.put(trackInfo[0] + TXT, new Track(trackInfo[0], trackInfo[1], Integer.parseInt(trackInfo[2])));
            }
            Playlist playlist = new Playlist(name, tracks);
            playlists.put(name + TXT, playlist);
            return playlist;
        } catch (IOException e) {
            logException(e);
            throw new PlaylistDoesNotExist("Error loading playlist from file with name: " + playlistName, e);
        }
    }

    @Override
    public Map<String, Playlist> loadAllPlaylistsFromFile() {
        Map<String, Playlist> playlists = new ConcurrentHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PLAYLISTS))) {
            String playlistName;
            while ((playlistName = reader.readLine()) != null) {
                playlists.put(playlistName, loadPlaylistFromFile(playlistName));
            }
            this.playlists = playlists;
            return playlists;
        } catch (FileNotFoundException e) {
            logException(e);
            throw new RuntimeException("Error finding files of playlists from file with playlist names", e);
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("Error loading playlists from file with playlist names", e);
        }
    }
}
