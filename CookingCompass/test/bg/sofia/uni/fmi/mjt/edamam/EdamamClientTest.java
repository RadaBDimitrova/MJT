package bg.sofia.uni.fmi.mjt.edamam;

import bg.sofia.uni.fmi.mjt.exceptions.InvalidQueryException;
import bg.sofia.uni.fmi.mjt.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.recipe.RecipeQueryArguments;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertThrows;

public class EdamamClientTest {
    @Test
    public void testSearchRecipes() {
        EdamamClient client = new EdamamClient();

        assertThrows(InvalidQueryException.class, () -> client.searchRecipes(new RecipeQueryArguments(
                null, null, null), 1));
    }

}
