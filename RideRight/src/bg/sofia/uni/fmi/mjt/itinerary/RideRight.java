package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SequencedCollection;
import java.util.Set;

public class RideRight implements ItineraryPlanner {
    List<Journey> schedule;

    public RideRight(List<Journey> schedule) {
        this.schedule = filterAndKeepCheapestJourneys(schedule);
    }

    private List<Journey> filterAndKeepCheapestJourneys(List<Journey> inputJourneys) {
        Set<String> uniqueJourneyKeys = new HashSet<>();
        List<Journey> filteredJourneys = new ArrayList<>();

        for (Journey journey : inputJourneys) {
            String journeyKey = journey.from().name() + journey.to().name();

            if (!uniqueJourneyKeys.contains(journeyKey)) {
                uniqueJourneyKeys.add(journeyKey);
                filteredJourneys.add(journey);
            } else {
                Journey existingJourney =
                        findJourneyByStartAndDestination(filteredJourneys, journey.from(), journey.to());
                if (existingJourney != null &&
                        journey.calculatePrice().compareTo(existingJourney.calculatePrice()) < 0) {
                    filteredJourneys.remove(existingJourney);
                    filteredJourneys.add(journey);
                }
            }
        }

        return filteredJourneys;
    }

    private Journey findJourneyByStartAndDestination(List<Journey> journeys, City start, City destination) {
        for (Journey journey : journeys) {
            if (journey.from().equals(start) && journey.to().equals(destination)) {
                return journey;
            }
        }
        return null;
    }

    private List<Journey> getSchedules(List<Journey> schedule) {
        List<Journey> result = new ArrayList<>();
        result.add(schedule.get(0));

        for (Journey journey : schedule) {
            boolean updated = false;
            for (Journey j : result) {
                if (journey.from().equals(j.from()) && journey.to().equals(j.to()) &&
                        journey.calculatePrice().doubleValue() < j.calculatePrice().doubleValue()) {
                    result.set(result.indexOf(j), journey);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                result.add(journey);
            }
        }

        return result;
    }


    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
            throws CityNotKnownException, NoPathToDestinationException {
        if (start.equals(destination)) {
            throw new NoPathToDestinationException("Start end destination city are the same");
        }
        boolean validStart = false;
        boolean validEnd = false;
        for (Journey journey : schedule) {
            if (journey.from().equals(start) || journey.to().equals(start)) {
                validStart = true;
            }
            if (journey.to().equals(destination) || journey.from().equals(destination)) {
                validEnd = true;
            }
        }
        if (!validEnd || !validStart) {
            throw new CityNotKnownException("Start or end city is not present in Journeys.");
        }

        if (!allowTransfer) {
            return findCheapestPathTransferNotAllowed(start, destination);
        } else { //A*
            Queue<Node> openSet = new PriorityQueue<>(new NodeComparator());
            Queue<Node> closedSet = new PriorityQueue<>(new NodeComparator());
            Node startNode = new Node(start, 0, null);

            openSet.add(startNode);

            while (!openSet.isEmpty()) {
                Node current = openSet.poll();

                if (current.getCity().equals(destination)) {
                    return constructJourney(current);
                }
                closedSet.add(current);

                for (Journey journey : getJourneysFromCity(current.getCity())) {
                    City neighborCity = journey.to();

                    if (isCityInClosedSet(closedSet, neighborCity)) {
                        continue;
                    }

                    BigDecimal newCostSoFar = BigDecimal.valueOf(current.getCostSoFar())
                            .add(calculateTotalCost(journey))
                            .add(heuristicCost(current.getCity(), neighborCity));

                    if (!isCityInOpenSet(openSet, neighborCity, newCostSoFar.doubleValue())) {
                        Node neighborNode =
                                new Node(neighborCity, newCostSoFar.doubleValue(), current);
                        neighborNode.setTotalCost(
                                (newCostSoFar.add(heuristicCost(neighborCity, destination))).doubleValue());
                        openSet.add(neighborNode);
                    }
                }
            }

            throw new NoPathToDestinationException("No path to destination found.");
        }
    }

    private BigDecimal calculateTotalCost(Journey journey) {
        BigDecimal basePrice = journey.calculatePrice();
        return basePrice.add(basePrice.multiply(journey.vehicleType().getGreenTax()));
    }

    private SequencedCollection<Journey> findCheapestPathTransferNotAllowed(City start, City destination)
            throws NoPathToDestinationException {
        Queue<Journey> sorted = getJourneysFromCityToOther(start, destination);
        if (sorted.isEmpty()) {
            throw new NoPathToDestinationException("There is no direct journey like this.");
        }

        Journey journey = sorted.poll();
        List<Journey> path = new ArrayList<>();
        path.add(journey);
        return path;
    }

    private SequencedCollection<Journey> constructJourney(Node destinationNode) {
        List<Journey> path = new ArrayList<>();
        Node current = destinationNode;

        while (current.getParent() != null) {
            path.add(getJourney(current.getParent().getCity(), current.getCity()));
            current = current.getParent();
        }
        Collections.reverse(path);
        return new ArrayList<>(path);
    }

    private List<Journey> getJourneysFromCity(City city) {
        List<Journey> result = new ArrayList<>();

        for (Journey journey : schedule) {
            if (journey.from().equals(city)) {
                result.add(journey);
            }
        }
        return result;
    }

    private Queue<Journey> getJourneysFromCityToOther(City from, City to) {
        Queue<Journey> sorted = new PriorityQueue<>(new JourneyComparator());

        for (Journey journey : schedule) {
            if (journey.from().equals(from) && journey.to().equals(to)) {
                sorted.add(journey);
            }
        }
        return sorted;
    }

    private Journey getJourney(City from, City to) {
        for (Journey journey : schedule) {
            if (journey.from().equals(from) && journey.to().equals(to)) {
                return journey;
            }
        }
        return null;
    }

    private boolean isCityInOpenSet(Queue<Node> openSet, City city, double cost) {
        for (Node node : openSet) {
            if (node.getCity().equals(city) && node.getTotalCost() <= cost) {
                return true;
            }
        }
        return false;
    }

    private Node pollFromOpenSet(Queue<Node> openSet, City city, double cost) {
        Node res = openSet.poll();
        double temp = cost;
        for (Node node : openSet) {
            if (node.getCity().equals(city) && node.getTotalCost() <= temp) {
                res = node;
                temp = node.getTotalCost();
            }
        }
        return res;
    }

    private boolean isCityInClosedSet(Queue<Node> closedSet, City city) {
        for (Node node : closedSet) {
            if (node.getCity().equals(city)) {
                return true;
            }
        }
        return false;
    }

    private BigDecimal heuristicCost(City from, City to) {
        Journey heuristicJourney = getJourney(from, to);
        if (heuristicJourney != null) {
            return heuristicJourney.getPriceForDistance();
        }
        return BigDecimal.ZERO;
    }
}
