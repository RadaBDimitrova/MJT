package bg.sofia.uni.fmi.mjt.simcity.exception;

public class BuildableNotFoundException extends RuntimeException {
    public BuildableNotFoundException(String name) {
        super(name);
    }
}
