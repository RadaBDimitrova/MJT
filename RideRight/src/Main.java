import bg.sofia.uni.fmi.mjt.itinerary.City;
import bg.sofia.uni.fmi.mjt.itinerary.Journey;
import bg.sofia.uni.fmi.mjt.itinerary.Location;
import bg.sofia.uni.fmi.mjt.itinerary.RideRight;
import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;
import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;
import java.util.List;

public class Main {
    public static void main(String[] args) throws CityNotKnownException, NoPathToDestinationException {
        City sofia = new City("Sofia", new Location(0, 2000));
        City plovdiv = new City("Plovdiv", new Location(4000, 1000));
        City varna = new City("Varna", new Location(9000, 3000));
        City burgas = new City("Burgas", new Location(9000, 1000));
        City ruse = new City("Ruse", new Location(7000, 4000));
        City blagoevgrad = new City("Blagoevgrad", new Location(0, 1000));
        City kardzhali = new City("Kardzhali", new Location(3000, 0));
        City tarnovo = new City("Tarnovo", new Location(5000, 3000));
        City aitos = new City("Aitos", new Location(4000, 1000));

        List<Journey> schedule = List.of(
                new Journey(VehicleType.BUS, sofia, blagoevgrad, new BigDecimal("20")),
                new Journey(VehicleType.BUS, blagoevgrad, sofia, new BigDecimal("20")),
                new Journey(VehicleType.BUS, sofia, plovdiv, new BigDecimal("90")),
                new Journey(VehicleType.BUS, plovdiv, sofia, new BigDecimal("90")),
                new Journey(VehicleType.BUS, plovdiv, kardzhali, new BigDecimal("50")),
                new Journey(VehicleType.BUS, kardzhali, plovdiv, new BigDecimal("50")),
                new Journey(VehicleType.BUS, plovdiv, burgas, new BigDecimal("90")),
                new Journey(VehicleType.BUS, burgas, plovdiv, new BigDecimal("90")),
                new Journey(VehicleType.BUS, burgas, aitos, new BigDecimal("90")),
                new Journey(VehicleType.BUS, aitos, kardzhali, new BigDecimal("50")),
                new Journey(VehicleType.BUS, burgas, varna, new BigDecimal("60")),
                new Journey(VehicleType.BUS, varna, burgas, new BigDecimal("60")),
                new Journey(VehicleType.BUS, sofia, tarnovo, new BigDecimal("150")),
                new Journey(VehicleType.BUS, tarnovo, sofia, new BigDecimal("150")),
                new Journey(VehicleType.BUS, plovdiv, tarnovo, new BigDecimal("40")),
                new Journey(VehicleType.BUS, tarnovo, plovdiv, new BigDecimal("40")),
                new Journey(VehicleType.BUS, tarnovo, ruse, new BigDecimal("70")),
                new Journey(VehicleType.BUS, ruse, tarnovo, new BigDecimal("70")),
                new Journey(VehicleType.BUS, varna, ruse, new BigDecimal("70")),
                new Journey(VehicleType.BUS, ruse, varna, new BigDecimal("70")),
                new Journey(VehicleType.PLANE, varna, burgas, new BigDecimal("200")),
                new Journey(VehicleType.PLANE, burgas, varna, new BigDecimal("200")),
                new Journey(VehicleType.PLANE, burgas, sofia, new BigDecimal("150")),
                new Journey(VehicleType.PLANE, sofia, burgas, new BigDecimal("250")),
                new Journey(VehicleType.PLANE, varna, sofia, new BigDecimal("290")),
                new Journey(VehicleType.PLANE, sofia, varna, new BigDecimal("300"))
        );

        RideRight rideRight = new RideRight(schedule);
        var res = rideRight.findCheapestPath(varna, burgas, false);
        System.out.println(res);
    }
}