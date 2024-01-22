package bg.sofia.uni.fmi.mjt.itinerary;

import java.util.Comparator;

public class JourneyComparator implements Comparator<Journey> {
    @Override
    public int compare(Journey journey1, Journey journey2) {
        int res = journey1.getPriceForDistance().compareTo(journey2.getPriceForDistance());
        if (res != 0) {
            return res;
        } else {
            return journey1.calculatePrice().compareTo(journey2.calculatePrice());
        }
    }
}
