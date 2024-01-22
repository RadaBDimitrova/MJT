package bg.sofia.uni.fmi.mjt.edamam;

import bg.sofia.uni.fmi.mjt.exceptions.InvalidQueryException;
import bg.sofia.uni.fmi.mjt.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.recipe.RecipeQueryArguments;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class EdamamClientTest {

    @Test
    public void testSearchRecipes() {
        EdamamClient client = new EdamamClient();

        List<Recipe> recipes = client.searchRecipes(mockRecipeQueryArguments());

        assertEquals(22, recipes.size());
        assertThrows(InvalidQueryException.class, () -> client.searchRecipes(new RecipeQueryArguments(
                null, null, null), 1));
    }

    private RecipeQueryArguments mockRecipeQueryArguments() {
        return new RecipeQueryArguments("chicken sandwich", List.of("Snack"), List.of("dairy-free"));
    }

}
