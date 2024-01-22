package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;

public record Journey(VehicleType vehicleType, City from, City to, BigDecimal price) {
    static final int DOLLAR = 20;
    static final int METERS_TO_KILOMETERS = 1000;

    public Journey {
        if (vehicleType == null || from == null || to == null) {
            throw new IllegalArgumentException("Vehicle, start or destination is null.");
        }
        if (price.doubleValue() < 0.0) {
            throw new IllegalArgumentException("Price for journey cannot be negative.");
        }
    }

    public BigDecimal calculateDistance() {
        return BigDecimal.valueOf(from.distanceTo(to.location()) / METERS_TO_KILOMETERS);
    }

    public BigDecimal calculatePrice() {
        BigDecimal result = price;
        return result.add(result.multiply(vehicleType.getGreenTax()));
    }

    public BigDecimal getPriceForDistance() { //price+dist*dollar
        BigDecimal result = price;
        BigDecimal base = calculateDistance().multiply(BigDecimal.valueOf(DOLLAR));
        return result.add(base);
    }

}
