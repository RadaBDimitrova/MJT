package bg.sofia.uni.fmi.mjt.itinerary;

import static java.lang.Math.abs;

public record City(String name, Location location) {
    public City {
        if (name == null || name.isBlank() || name.isEmpty()) {
            throw new IllegalArgumentException("Invalid name (null, blank or empty).");
        }
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null.");
        }
    }

    public double distanceTo(Location location) {
        return abs(this.location().x() - location.x()) + abs(this.location().y() - location.y());
    }
}
