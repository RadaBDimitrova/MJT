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

public record Track(String name, String artist, int timesPlayed) {
    private static final String WAV = ".wav";
    private static final int BYTES = 4096;

    public void play() {
        timesPlayed++;
        File audioFile = new File(name + "-" + artist + WAV);
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile)) {
            playAudio(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException e) {
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
            throw new InvalidAudioFileException("Error while playing audio", e);
        } finally {
            stopRequested = false;
        }
    }

    public void stop() {
        //TODO: implement
    }

}
