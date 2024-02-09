package bg.sofia.uni.fmi.mjt.server.track;

import bg.sofia.uni.fmi.mjt.server.exceptions.InvalidAudioFileException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import static bg.sofia.uni.fmi.mjt.server.SpotifyServer.logException;

public class Track {
    private final String name;
    private final String artist;
    private int timesPlayed;
    private boolean stopRequested;
    private static final String WAV = ".wav";
    private static final int BYTES = 4096;
    private static final int PARTS_SIZE = 3;

    public Track(String name, String artist, int timesPlayed) {
        this.name = name;
        this.artist = artist;
        this.timesPlayed = timesPlayed;
    }

    public void play() {
        timesPlayed++;
        File audioFile = new File(name + "_" + artist + "_" + timesPlayed + WAV);
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            playAudio(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
            logException(e);
            throw new InvalidAudioFileException("Error accessing song file", e);
        }
    }

    private void playAudio(AudioInputStream audioInputStream) {
        AudioFormat audioFormat = audioInputStream.getFormat();
        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        try (SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo)) {
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            byte[] buffer = new byte[BYTES];
            int bytesRead;
            while (!stopRequested && (bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                sourceDataLine.write(buffer, 0, bytesRead);
            }
            sourceDataLine.drain();
        } catch (LineUnavailableException | IOException e) {
            logException(e);
            throw new InvalidAudioFileException("Error while playing audio", e);
        } finally {
            stopRequested = false;
        }
    }

    public void stop() {
        stopRequested = true;
    }

    public static Track createTrackFromFileName(String fileName) {
        String[] parts = fileName.split("_");
        if (parts.length != PARTS_SIZE) {
            logException(new InvalidAudioFileException("Invalid file name"));
            throw new InvalidAudioFileException("Invalid file name");
        }
        return new Track(parts[0], parts[1], Integer.parseInt(parts[2]));
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
}
