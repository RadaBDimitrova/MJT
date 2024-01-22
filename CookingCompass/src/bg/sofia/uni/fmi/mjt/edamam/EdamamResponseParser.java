package bg.sofia.uni.fmi.mjt.edamam;

import bg.sofia.uni.fmi.mjt.recipe.Recipe;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class EdamamResponseParser implements RecipeParserStrategy {
    private final Gson gson;

    public EdamamResponseParser() {
        this.gson = new Gson();
    }

    @Override
    public List<Recipe> parseRecipes(String responseBody) {
        List<Recipe> recipes = new ArrayList<>();
        JsonElement jsonResponse = gson.fromJson(responseBody, JsonElement.class);

        if (jsonResponse.isJsonObject() && jsonResponse.getAsJsonObject().has("hits")) {
            JsonArray hitsArray = jsonResponse.getAsJsonObject().getAsJsonArray("hits");
            for (JsonElement hitElement : hitsArray) {
                if (hitElement.isJsonObject()) {
                    JsonObject recipeNode = hitElement.getAsJsonObject();
                    Recipe recipe = gson.fromJson(recipeNode.getAsJsonObject("recipe").toString(), Recipe.class);
                    recipes.add(recipe);
                }
            }
        }
        return recipes;
    }

    public int calculatePagesCount(String responseBody, int resultsPerPage, int pagesToLoad) {
        int totalRecipes = getTotalRecipes(responseBody);
        int remainingPages = (totalRecipes + resultsPerPage - 1) / resultsPerPage;

        return Math.max(Math.min(pagesToLoad, remainingPages), 1);
    }

    public int getTotalRecipes(String responseBody) {
        JsonElement jsonResponse = gson.fromJson(responseBody, JsonElement.class);

        if (jsonResponse.isJsonObject() && jsonResponse.getAsJsonObject().has("count")) {
            return jsonResponse.getAsJsonObject().getAsJsonPrimitive("count").getAsInt();
        }
        return 0;
    }

    public String parseNextLink(String responseBody) {
        JsonElement jsonResponse = gson.fromJson(responseBody, JsonElement.class);

        if (jsonResponse.isJsonObject() && jsonResponse.getAsJsonObject().has("_links")) {
            JsonObject linksObject = jsonResponse.getAsJsonObject().getAsJsonObject("_links");
            if (linksObject.has("next")) {
                JsonObject nextLink = linksObject.getAsJsonObject("next");
                if (nextLink.has("href")) {
                    return nextLink.getAsJsonPrimitive("href").getAsString();
                }
            }
        }
        return null;
    }
}

