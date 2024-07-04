package bg.sofia.uni.fmi.mjt.server.exceptions;

public class SongDoesNotExist extends RuntimeException {
    public SongDoesNotExist(String message) {
        super(message);
    }

    public SongDoesNotExist(String message, Throwable cause) {
        super(message, cause);
    }
}
