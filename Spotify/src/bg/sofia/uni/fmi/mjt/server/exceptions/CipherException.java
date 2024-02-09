package bg.sofia.uni.fmi.mjt.server.exceptions;

public class CipherException extends Exception {
    public CipherException(String message, Exception e) {
        super(message, e);
    }
}
