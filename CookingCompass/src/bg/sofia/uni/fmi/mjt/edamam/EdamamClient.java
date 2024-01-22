package bg.sofia.uni.fmi.mjt.edamam;

import bg.sofia.uni.fmi.mjt.exceptions.InvalidQueryException;
import bg.sofia.uni.fmi.mjt.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.recipe.RecipeQueryArguments;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;


public class EdamamClient {
    protected static final int RESULTS_PER_PAGE = 20;
    private static final int PAGES_TO_LOAD = 3;
    private EdamamResponseParser responseParser;
    private HttpClient httpClient;

    public EdamamClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.responseParser = new EdamamResponseParser();
    }

    public List<Recipe> searchRecipes(RecipeQueryArguments query) {
        return searchRecipes(query, 1);
    }

    public List<Recipe> searchRecipes(RecipeQueryArguments query, int currentPage) {
        EdamamRequest baseRequest = new EdamamRequest();
        HttpRequest request = baseRequest.buildRequest(query, currentPage);

        try {
            boolean hasNextPage = true;
            List<Recipe> recipes = new ArrayList<>();

            while (hasNextPage) {
                HttpResponse<String> response = getResponse(request);
                List<Recipe> currentPageRecipes = responseParser.parseRecipes(response.body());
                recipes.addAll(currentPageRecipes);

                int pagesCount = responseParser.calculatePagesCount(response.body(), RESULTS_PER_PAGE, PAGES_TO_LOAD);

                hasNextPage = currentPage < pagesCount;

                if (hasNextPage) {
                    String nextLink = responseParser.parseNextLink(response.body());
                    if (nextLink != null && !nextLink.isEmpty()) {
                        request = HttpRequest.newBuilder().uri(new URI(nextLink)).build();
                    } else {
                        break;
                    }
                }
            }
            return recipes;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error during search", e);
        } catch (URISyntaxException e) {
            throw new InvalidQueryException("URI is invalid", e);
        }
    }

    protected HttpResponse<String> getResponse(HttpRequest request) throws IOException, InterruptedException {
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}

