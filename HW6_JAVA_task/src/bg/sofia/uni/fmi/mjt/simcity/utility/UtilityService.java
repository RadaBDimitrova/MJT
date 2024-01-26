package bg.sofia.uni.fmi.mjt.simcity.utility;

import bg.sofia.uni.fmi.mjt.simcity.property.billable.Billable;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

public class UtilityService implements UtilityServiceAPI {
    Map<UtilityType, Double> taxRates;

    public UtilityService(Map<UtilityType, Double> taxRates) {
        this.taxRates = new HashMap<UtilityType, Double>();
        this.taxRates.putAll(taxRates);
    }

    @Override
    public <T extends Billable> double getUtilityCosts(UtilityType utilityType, T billable) {
        double costs = 0.0;
        if (utilityType == null || billable == null) {
            throw new IllegalArgumentException("Null utility or billable.");
        }
        for (Map.Entry<UtilityType, Double> entry : taxRates.entrySet()) {
            if (entry == null) {
                throw new IllegalArgumentException("Null entry in taxRates.");
            }
            UtilityType key = entry.getKey();
            Double value = entry.getValue();
            if (key.equals(utilityType)) {
                costs += addUtilityCosts(key, billable, value);
            }
        }
        return costs;
    }

    @Override
    public <T extends Billable> double getTotalUtilityCosts(T billable) {
        double costs = 0.0;
        if (billable == null) {
            throw new IllegalArgumentException("Null billable.");
        }
        for (Map.Entry<UtilityType, Double> entry : taxRates.entrySet()) {
            if (entry == null) {
                throw new IllegalArgumentException("Null entry in taxRates.");
            }
            UtilityType key = entry.getKey();
            Double value = entry.getValue();
            costs += addUtilityCosts(key, billable, value);
        }
        return costs;
    }

    @Override
    public <T extends Billable> Map<UtilityType, Double> computeCostsDifference(T firstBillable, T secondBillable) {
        if (firstBillable == null || secondBillable == null) {
            throw new IllegalArgumentException("Null first or second billable.");
        }
        Map<UtilityType, Double> costDifferenceMap = new HashMap<>();
        for (UtilityType utilityType : UtilityType.values()) {
            double costFirstBillable = getUtilityCosts(utilityType, firstBillable);
            double costSecondBillable = getUtilityCosts(utilityType, secondBillable);

            double costDifference = abs(costSecondBillable - costFirstBillable);

            costDifferenceMap.put(utilityType, costDifference);
        }
        return Map.copyOf(costDifferenceMap);
    }

    private <T extends Billable> double addUtilityCosts(UtilityType type, T billable, Double value) {

        return switch (type) {
            case UtilityType.ELECTRICITY -> billable.getElectricityConsumption() * value;
            case UtilityType.WATER -> billable.getWaterConsumption() * value;
            case UtilityType.NATURAL_GAS -> billable.getNaturalGasConsumption() * value;
        };
    }
}
