package bg.sofia.uni.fmi.mjt.server.exceptions;

public class InvalidAudioFileException extends RuntimeException {
    public InvalidAudioFileException(String message) {
        super(message);
    }

    public InvalidAudioFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
