package bg.sofia.uni.fmi.mjt.edamam;

import bg.sofia.uni.fmi.mjt.URIBuilder;
import bg.sofia.uni.fmi.mjt.exceptions.InvalidQueryException;
import bg.sofia.uni.fmi.mjt.recipe.RecipeQueryArguments;

import java.net.URI;
import java.net.http.HttpRequest;


public class EdamamRequest implements RequestBuildingStrategy {
    private static final String APP_ID = "865bc1aa";
    private static final String APP_KEY = "5020429240aa7550faf84afbeb5ea0b9";
    private final URIBuilder uriBuilder;

    public EdamamRequest() {
        this.uriBuilder = new URIBuilder();
    }

    @Override
    public HttpRequest buildRequest(RecipeQueryArguments query, int currentPage) {
        if (isQueryTextRequired(query)) {
            if (query.keywords() == null || query.keywords().isEmpty()) {
                throw new InvalidQueryException("You can't request with blank query parameters.");
            }
        }
        URI uri = uriBuilder
                .addParameter("q", query.keywords())
                .addParameter("app_id", APP_ID)
                .addParameter("app_key", APP_KEY)
                .addParameters("health", query.healthLabels())
                .addParameters("mealType", query.mealTypes())
                .build();

        return HttpRequest.newBuilder()
                .uri(uri)
                .headers("Accept", "application/json", "Accept-Language", "en")
                .build();
    }

    private boolean isQueryTextRequired(RecipeQueryArguments query) {
        return (query.healthLabels() == null || query.healthLabels().isEmpty()) &&
                (query.healthLabels() == null || query.mealTypes().isEmpty());
    }

}

