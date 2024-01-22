package bg.sofia.uni.fmi.mjt.exceptions;

public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidQueryException(Throwable cause) {
        super(cause);
    }

    public InvalidQueryException(String message) {
        super(message);
    }
}
