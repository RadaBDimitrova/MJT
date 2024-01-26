package bg.sofia.uni.fmi.mjt.gym.member;

import java.util.Comparator;

public class ProximityComparator implements Comparator<GymMember> {
    Address address;

    public ProximityComparator(Address address) {
        this.address = address;
    }

    @Override
    public int compare(GymMember o1, GymMember o2) {
        if (address == null) {
            throw new IllegalStateException("Address is not initialized.");
        }
        double distance1 = o1.getAddress().getDistanceTo(address);
        double distance2 = o2.getAddress().getDistanceTo(address);
        return Double.compare(distance1, distance2);
    }
}