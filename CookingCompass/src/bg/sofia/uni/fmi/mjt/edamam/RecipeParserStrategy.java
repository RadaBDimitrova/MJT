package bg.sofia.uni.fmi.mjt.edamam;

import bg.sofia.uni.fmi.mjt.recipe.Recipe;

import java.util.List;

public interface RecipeParserStrategy {
    List<Recipe> parseRecipes(String responseBody);
}
