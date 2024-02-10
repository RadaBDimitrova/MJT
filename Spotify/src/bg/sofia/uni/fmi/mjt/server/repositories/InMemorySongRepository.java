package bg.sofia.uni.fmi.mjt.server.repositories;

import bg.sofia.uni.fmi.mjt.server.exceptions.SongDoesNotExist;
import bg.sofia.uni.fmi.mjt.server.track.Track;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static bg.sofia.uni.fmi.mjt.server.SpotifyServer.logException;
import static bg.sofia.uni.fmi.mjt.server.track.Track.createTrackFromFileName;

public class InMemorySongRepository implements SongRepository {
    private static final String SONGS_PATH = "src/songs";
    private Map<Track, String> songs = new ConcurrentHashMap<>();

    public InMemorySongRepository() {
        songs = loadAllSongs();
    }

    private Map<Track, String> loadAllSongs() {
        Map<Track, String> result = new ConcurrentHashMap<>();
        try (Stream<Path> paths = Files.walk(Paths.get(SONGS_PATH))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".wav"))
                    .forEach(path -> {
                        Track track = createTrackFromFileName(path.getFileName().toString().replace(".wav", ""));
                        result.put(track, path.toString());
                    });
        } catch (IOException e) {
            logException(e);
            throw new UncheckedIOException("Could not load songs", e);
        }
        return result;
    }

    @Override
    public Track searchSongByName(String songName, String artistName) {
        try {
            return songs.keySet().stream()
                    .filter(song -> song.name().equals(songName) && song.artist().equals(artistName))
                    .findFirst()
                    .orElseThrow(() -> new SongDoesNotExist("Song with name " + songName + " does not exist"));
        } catch (Throwable t) {
            logException(t);
            throw t;
        }
    }

    @Override
    public List<Track> searchSongByKeyword(String keyword) {
        return songs.keySet().stream().filter(song -> (song.name().contains(keyword))
                || song.artist().contains(keyword)).toList();
    }

    @Override
    public List<Track> topNSongsByTimesPlayed(int n) {
        return songs.keySet().stream().sorted(Comparator.comparingInt(Track::timesPlayed).reversed()).limit(n).toList();
    }
}
