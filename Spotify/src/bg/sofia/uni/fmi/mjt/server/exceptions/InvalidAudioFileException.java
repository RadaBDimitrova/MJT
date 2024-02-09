package bg.sofia.uni.fmi.mjt.server.exceptions;

public class InvalidAudioFileException extends RuntimeException {
    public InvalidAudioFileException(Exception e) {
        super(e);
    }

    public InvalidAudioFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
