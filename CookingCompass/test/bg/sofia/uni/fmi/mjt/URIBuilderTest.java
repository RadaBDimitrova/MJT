package bg.sofia.uni.fmi.mjt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class URIBuilderTest {

    @Test
    public void testBuildWithSingleParameter() {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri = uriBuilder.addParameter("q", "chicken").build();
        assertEquals("https://api.edamam.com/api/recipes/v2?type=public&q=chicken", uri.toString());
    }

    @Test
    public void testBuildWithMultipleParameters() {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri = uriBuilder
                .addParameter("q", "chicken")
                .addParameters("health", Arrays.asList("vegan", "gluten-free"))
                .build();
        assertEquals("https://api.edamam.com/api/recipes/v2?type=public&q=chicken&health=vegan&health=gluten-free", uri.toString());
    }

    @Test
    public void testBuildWithFormattedKeywords() {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri = uriBuilder.addParameter("q", "chicken sandwich").build();
        assertEquals("https://api.edamam.com/api/recipes/v2?type=public&q=chicken%20sandwich", uri.toString());
    }

    @Test
    public void testBuildWithEmptyParameters() {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri = uriBuilder.build();
        assertEquals("https://api.edamam.com/api/recipes/v2?type=public", uri.toString());
    }

    @Test
    public void testBuildWithEmptyValue() {
        URIBuilder uriBuilder = new URIBuilder();
        URI uri = uriBuilder.addParameter("q", "").build();
        assertEquals("https://api.edamam.com/api/recipes/v2?type=public", uri.toString());
    }

    @Test
    void testAddParametersNonHealth() {
        URIBuilder uriBuilder = new URIBuilder();

        String paramName = "mealType";
        String paramValue1 = "Lunch";
        String paramValue2 = "Dinner";
        String paramValue3 = "Snack";
        String expectedUri = "https://api.edamam.com/api/recipes/v2?type=public&mealType=Lunch&mealType=Dinner&mealType=Snack";

        uriBuilder.addParameters(paramName, List.of(paramValue1, paramValue2, paramValue3));

        String actualUri = uriBuilder.build().toString();
        assertEquals(expectedUri, actualUri);
    }
}
