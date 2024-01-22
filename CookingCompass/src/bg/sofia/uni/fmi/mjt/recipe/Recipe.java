package bg.sofia.uni.fmi.mjt.recipe;

import bg.sofia.uni.fmi.mjt.labels.Health;
import bg.sofia.uni.fmi.mjt.labels.MealType;

import java.util.List;

public record Recipe(String label, List<String> dietLabels, List<Health> healthLabels,
                     double totalWeight, List<String> cuisineType, List<MealType> mealType,
                     List<String> dishType, List<String> ingredientLines) {
}
