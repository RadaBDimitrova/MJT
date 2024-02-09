package bg.sofia.uni.fmi.mjt.server.repositories;

import bg.sofia.uni.fmi.mjt.server.exceptions.SongDoesNotExist;
import bg.sofia.uni.fmi.mjt.server.track.Track;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySongRepository implements SongRepository {
    private static final String SONGS_PATH = "resources/songs/";
    private Map<Track, String> songs;

    public InMemorySongRepository() {
        songs = loadAllSongsFromFile();
    }

    private Map<Track, String> loadAllSongsFromFile() {
        Map<Track, String> result = new ConcurrentHashMap<>();
        //TODO
    }

    @Override
    public Track searchSongByName(String songName) {
        return songs.keySet().stream().filter(song -> song.name().equals(songName)).findFirst()
                .orElseThrow(() -> new SongDoesNotExist("Song with name " + songName + " does not exist"));
    }

    @Override
    public List<Track> searchSongByKeyword(String keyword) {
        return songs.keySet().stream().filter(song -> (song.name().contains(keyword))
                || song.artist().contains(keyword)).toList();
    }

    @Override
    public List<Track> searchSongByArtist(String artist) {
        return songs.keySet().stream().filter(song -> song.artist().equals(artist)).toList();
    }

    @Override
    public List<Track> topNSongsByTimesPlayed(int n) {
        return songs.keySet().stream().sorted((s1, s2) -> s2.timesPlayed() - s1.timesPlayed()).limit(n).toList();
    }
}
