package bg.sofia.uni.fmi.mjt.edamam;

import bg.sofia.uni.fmi.mjt.recipe.RecipeQueryArguments;
import org.junit.jupiter.api.Test;

import java.net.http.HttpRequest;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EdamamRequestTest {

    @Test
    void testBuildRequest() {
        RecipeQueryArguments queryArguments = new RecipeQueryArguments("chicken sandwich", List.of("Snack"), List.of("dairy-free"));

        RequestBuildingStrategy edamamRequestTest = new EdamamRequest();
        HttpRequest httpRequest = edamamRequestTest.buildRequest(queryArguments, 1);
        String uri = "https://api.edamam.com/api/recipes/v2?type=public&q=chicken%20sandwich" +
                "&app_id=865bc1aa&app_key=5020429240aa7550faf84afbeb5ea0b9&health=dairy-free&mealType=Snack";
        assertEquals(httpRequest.uri().toString(), uri);
        assertEquals("application/json", httpRequest.headers().firstValue("Accept").orElse(null));
        assertEquals("en", httpRequest.headers().firstValue("Accept-Language").orElse(null));
    }
}
