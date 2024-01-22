package bg.sofia.uni.fmi.mjt.edamam;

import bg.sofia.uni.fmi.mjt.recipe.RecipeQueryArguments;

import java.net.http.HttpRequest;

public interface RequestBuildingStrategy {
    HttpRequest buildRequest(RecipeQueryArguments query, int currentPage);
}

