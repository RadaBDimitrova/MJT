package bg.sofia.uni.fmi.mjt.server.exceptions;

public class PlaylistDoesNotExist extends RuntimeException {
    public PlaylistDoesNotExist(String message) {
        super(message);
    }

    public PlaylistDoesNotExist(String message, Throwable cause) {
        super(message, cause);
    }
}
