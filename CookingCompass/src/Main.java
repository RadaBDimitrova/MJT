import bg.sofia.uni.fmi.mjt.edamam.EdamamClient;

import bg.sofia.uni.fmi.mjt.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.recipe.RecipeQueryArguments;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EdamamClient client = new EdamamClient();
        RecipeQueryArguments query = new RecipeQueryArguments(
                "chicken sandwich", List.of("Snack"), List.of("dairy-free"));
        List<Recipe> recipes = client.searchRecipes(query);

        if (recipes.isEmpty()) {
            throw new RuntimeException("No such recipes");
        }

        System.out.println(recipes.size());
        System.out.println("Recipes: " + recipes);

    }
}