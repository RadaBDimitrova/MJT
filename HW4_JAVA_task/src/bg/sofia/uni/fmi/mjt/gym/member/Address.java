package bg.sofia.uni.fmi.mjt.gym.member;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public record Address(double longitude, double latitude) {
    public Address {
    }

    public double getDistanceTo(Address other) {
        return sqrt(pow(this.latitude - other.latitude, 2) + pow(this.longitude - other.longitude, 2));
    }
}
