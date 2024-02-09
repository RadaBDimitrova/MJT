package bg.sofia.uni.fmi.mjt.server.playlist;

import bg.sofia.uni.fmi.mjt.server.track.Track;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Playlist {
    private static final String NAME = "Name: ";
    private static final String TRACKS = "Tracks: ";
    private static final String TRACK_NAME = "Track name: ";
    private static final String ARTIST = "Artist: ";
    private static final String TIMES_PLAYED = "Times played: ";
    private String name;
    private final Map<Track, Integer> tracks = new ConcurrentHashMap<>();

    public Playlist(String name, Map<Track, Integer> tracks) {
        this.name = name;
        this.tracks.putAll(tracks);
    }

    public synchronized void addTrack(Track track) {
        tracks.put(track, track.timesPlayed());
    }

    public synchronized void removeTrack(Track track) {
        tracks.remove(track);
    }

    public String getPlaylistInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(NAME).append(name).append(System.lineSeparator());
        sb.append(TRACKS).append(System.lineSeparator());
        for (Track track : tracks.keySet()) {
            sb.append(TRACK_NAME).append(track.name()).append(System.lineSeparator());
            sb.append(ARTIST).append(track.artist()).append(System.lineSeparator());
            sb.append(TIMES_PLAYED).append(track.timesPlayed()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public Map<Track, Integer> getTracks() {
        return tracks;
    }
}
