package com.example.recipesharingappfinal2.network

/**
 * Represents the response from a search query to the Spoonacular API.
 *
 * @property results The list of recipes returned as search results.
 */
data class SearchRecipeResponse(
    val results: List<Recipe>
)

/**
 * Represents a single recipe with its details.
 *
 * @property id The unique identifier for the recipe.
 * @property title The name of the recipe.
 * @property image The URL of the recipe's image.
 * @property summary A brief summary of the recipe (optional).
 * @property ingredients A string containing the ingredients required for the recipe.
 *                      Default value is an empty string to handle null inputs.
 * @property instructions The instructions or steps to prepare the recipe (optional).
 * @property description A description of the recipe.
 */
data class Recipe(
    val id: String,
    val title: String,
    val image: String,
    val summary: String? = null,
    val ingredients: String = null.toString(),
    val instructions: String? = null,
    val description: String
)
