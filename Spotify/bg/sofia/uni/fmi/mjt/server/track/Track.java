package bg.sofia.uni.fmi.mjt.server.track;

import bg.sofia.uni.fmi.mjt.server.exceptions.InvalidAudioFileException;

import static bg.sofia.uni.fmi.mjt.server.SpotifyServer.logException;

public class Track {
    public static final String WAV = ".wav";
    public static final int BYTES = 4096;
    private static final int PARTS_SIZE = 2;
    public static final String SONGS_PATH = "src/songs/";
    private final String name;
    private final String artist;
    private int timesPlayed;
    private boolean stopRequested;

    public Track(String name, String artist, int timesPlayed) {
        this.name = name;
        this.artist = artist;
        this.timesPlayed = timesPlayed;
    }

    public static Track createTrackFromFileName(String fileName) {
        String[] parts = fileName.split("_");
        if (parts.length != PARTS_SIZE) {
            logException(new InvalidAudioFileException("Invalid file name"));
            throw new InvalidAudioFileException("Invalid file name");
        }
        return new Track(parts[0], parts[1], 0);
    }

    public String name() {
        return name;
    }

    public String artist() {
        return artist;
    }

    public int timesPlayed() {
        return timesPlayed;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Track: ").append(name).append(" Artist: ").append(artist);
        return sb.toString();
    }

}
