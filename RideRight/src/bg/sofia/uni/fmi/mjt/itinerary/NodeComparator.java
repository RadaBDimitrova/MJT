package bg.sofia.uni.fmi.mjt.itinerary;

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {
    @Override
    public int compare(Node node1, Node node2) {
        int costComparison = Double.compare(node1.getCostSoFar(), node2.getCostSoFar());

        if (costComparison != 0) {
            return costComparison;
        } else {
            return node1.getCity().name().compareTo(node2.getCity().name());
        }
    }
}

