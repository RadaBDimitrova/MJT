package bg.sofia.uni.fmi.mjt.gym;

public class GymCapacityExceededException extends RuntimeException {
    public GymCapacityExceededException(String name) {
        super(name);
    }
}
