package bg.sofia.uni.fmi.mjt.edamam;

import bg.sofia.uni.fmi.mjt.recipe.Recipe;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EdamamResponseParserTest {
    private static EdamamResponseParser responseParser;

    @BeforeAll
    static void setUpParser() {
        responseParser = new EdamamResponseParser();
    }

    @Test
    void testParseRecipes() {
        String responseBody = """
                {
                  "hits": [
                    {
                      "recipe": {
                        "label": "Recipe 1"
                      }
                    },
                    {
                      "recipe": {
                        "label": "Recipe 2"
                      }
                    }
                  ]
                }
                """;


        List<Recipe> recipes = responseParser.parseRecipes(responseBody);

        assertEquals(2, recipes.size());
        assertEquals("Recipe 1", recipes.get(0).label());
        assertEquals("Recipe 2", recipes.get(1).label());
    }

    @Test
    void testCalculatePagesCount() {

        String responseBody = """
                {
                  "count": 27
                }
                """;

        int resultsPerPage = 20;
        int pagesToLoad = 3;
        int pagesCount = responseParser.calculatePagesCount(responseBody, resultsPerPage, pagesToLoad);

        assertEquals(2, pagesCount);
    }

    @Test
    void testGetTotalRecipesWithoutCount() {
        String responseBody = "{}";
        int totalRecipes = responseParser.getTotalRecipes(responseBody);
        assertEquals(0, totalRecipes);
    }

    @Test
    void testGetTotalRecipes() {
        String responseBody = """
                {
                  "count": 22
                }
                """;

        int totalCount = responseParser.getTotalRecipes(responseBody);
        assertEquals(22, totalCount);
    }

    @Test
    void testParseNextLink() {
        String responseBody = """
                {
                  "_links": {
                    "next": {
                      "href": "https://api.example.com/next"
                    }
                  }
                }
                """;

        String nextLink = responseParser.parseNextLink(responseBody);
        assertEquals("https://api.example.com/next", nextLink);
    }

    @Test
    void testParseNextLinkWithoutHref() {
        String responseBody = "{ \"_links\": { \"next\": { } } }";
        String nextLink = responseParser.parseNextLink(responseBody);
        assertNull(nextLink);
    }
}

