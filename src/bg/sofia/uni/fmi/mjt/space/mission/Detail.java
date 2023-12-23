package bg.sofia.uni.fmi.mjt.space.mission;

public record Detail(String rocketName, String payload) {
    public Detail {
        if ( rocketName() == null || payload() == null ||
                rocketName().isEmpty() || rocketName().isBlank() || payload().isEmpty() || payload().isBlank()) {
            throw new IllegalArgumentException("Invalid fields");
        }
    }
}
