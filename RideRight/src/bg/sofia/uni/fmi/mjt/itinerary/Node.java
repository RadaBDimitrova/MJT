package bg.sofia.uni.fmi.mjt.itinerary;

import java.util.Comparator;

public class Node {
    private City city;
    private double costSoFar;
    private double totalCost;
    private Node parent;

    public Node(City city, double costSoFar, Node parent) {
        this.city = city;
        this.costSoFar = costSoFar;
        this.parent = parent;
        this.totalCost = costSoFar;
    }

    public City getCity() {
        return city;
    }

    public double getCostSoFar() {
        return costSoFar;
    }

    public void setCostSoFar(double cost) {
        costSoFar = cost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public Node getParent() {
        return parent;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public static Comparator<Node> getComparator() {
        return Comparator.comparingDouble(Node::getTotalCost)
                .thenComparing(node -> node.getCity().name());
    }
}

