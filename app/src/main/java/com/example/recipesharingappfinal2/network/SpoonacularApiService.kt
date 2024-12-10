package com.example.recipesharingappfinal2.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface defining the API service for interacting with the Spoonacular API.
 * Contains endpoint declarations for recipe-related API requests.
 */
interface SpoonacularApiService {

    /**
     * Makes a GET request to the `recipes/complexSearch` endpoint to fetch a list of recipes
     * based on the search query and additional parameters.
     *
     * @param apiKey The API key for authenticating the request. This parameter is automatically added by the interceptor.
     * @param query The search keyword or phrase for finding recipes (e.g., "pasta", "chicken").
     * @param number The maximum number of results to return. Defaults to 10.
     * @param sort The sorting criteria for the results. Defaults to "popularity".
     * @return A `Response` object wrapping the `SearchRecipeResponse`, which contains the list of matching recipes.
     */
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("sort") sort: String = "popularity"
    ): Response<SearchRecipeResponse>
}
