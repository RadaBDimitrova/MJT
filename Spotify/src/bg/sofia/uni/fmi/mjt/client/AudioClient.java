package bg.sofia.uni.fmi.mjt.client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class AudioClient {
    private SourceDataLine sourceDataLine;
    private boolean playing = true;
    public void playSong(String songPath) {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(songPath))) {
            AudioFormat format = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceDataLine.open(format);
            sourceDataLine.start();

            byte[] buffer = new byte[1024];
            int bytesRead;

            while (playing && (bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                sourceDataLine.write(buffer, 0, bytesRead);
                if(!playing){
                    throw new RuntimeException("Song stopped");
                }
            }

            sourceDataLine.drain();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            if (sourceDataLine != null && sourceDataLine.isOpen()) {
                sourceDataLine.stop();
                sourceDataLine.close();
            }
        }
    }

    public void stopSong() {
        playing = false;
        if (sourceDataLine != null && sourceDataLine.isOpen()) {
            sourceDataLine.stop();
            sourceDataLine.close();
        }
    }
}
