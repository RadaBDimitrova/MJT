package bg.sofia.uni.fmi.mjt.simcity.plot;

import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.simcity.exception.BuildableNotFoundException;
import bg.sofia.uni.fmi.mjt.simcity.exception.InsufficientPlotAreaException;
import bg.sofia.uni.fmi.mjt.simcity.property.buildable.Buildable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Plot<E extends Buildable> implements PlotAPI<E> {
    int buildableArea;
    Map<String, E> buildables;

    public Plot(int buildableArea) {
        this.buildableArea = buildableArea;
        this.buildables = new HashMap<>();
    }

    @Override
    public void construct(String address, E buildable) {
        if (address == null || address.isBlank() || buildable == null) {
            throw new IllegalArgumentException("Address or buildable is null or empty.");
        }
        if (buildables.containsKey(address)) {
            throw new BuildableAlreadyExistsException("Address is already occupied.");
        }
        if (buildable.getArea() > buildableArea) {
            throw new InsufficientPlotAreaException("Not enough area to build.");
        }
        buildables.put(address, buildable);
        buildableArea -= buildable.getArea();
    }

    @Override
    public void demolish(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address is null or empty.");
        }
        if (!buildables.containsKey(address)) {
            throw new BuildableNotFoundException("Address is not occupied");
        }
        buildableArea += buildables.get(address).getArea();
        buildables.remove(address);
    }

    @Override
    public void demolishAll() {
        Set<String> addresses = new HashSet<>(buildables.keySet());
        for (String address : addresses) {
            demolish(address);
        }
    }

    @Override
    public Map<String, E> getAllBuildables() {
        return Map.copyOf(buildables);
    }

    @Override
    public int getRemainingBuildableArea() {
        return buildableArea;
    }

    @Override
    public void constructAll(Map<String, E> buildables) {
        if (buildables == null || buildables.isEmpty()) {
            throw new IllegalArgumentException("Null buildables.");
        }
        for (Map.Entry<String, E> entry : buildables.entrySet()) {
            if (entry == null) {
                throw new IllegalArgumentException("Null entry in buildables.");
            }
            construct(entry.getKey(), entry.getValue());
        }
    }
}
