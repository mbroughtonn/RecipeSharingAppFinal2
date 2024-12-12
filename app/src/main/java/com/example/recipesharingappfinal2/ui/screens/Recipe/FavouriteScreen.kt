package com.example.recipesharingappfinal2.ui.screens.Recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.recipesharingappfinal2.R
import com.example.recipesharingappfinal2.network.Recipe
import com.example.recipesharingappfinal2.network.RetrofitInstance
import kotlinx.coroutines.launch

/**
 * Composable function representing the FavouriteScreen, which displays three categories of favourite recipes:
 * Breakfast, Dinner, and Desserts. Each category is displayed with its respective recipes.
 *
 * The recipes are fetched from a network source using Retrofit and displayed in a LazyRow for each category.
 * A loading indicator is shown while fetching the recipes.
 */
@Composable
fun FavouriteScreen() {
    val coroutineScope = rememberCoroutineScope()

    var favouriteBreakfasts by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var favouriteDinners by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var favouriteDesserts by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isBreakfastLoading by remember { mutableStateOf(true) }
    var isDinnersLoading by remember { mutableStateOf(true) }
    var isDessertsLoading by remember { mutableStateOf(true) }

    // Fetching recipes for each category on launch
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            favouriteBreakfasts = fetchRecipes("breakfast")
            isBreakfastLoading = false
        }
        coroutineScope.launch {
            favouriteDinners = fetchRecipes("dinner")
            isDinnersLoading = false
        }
        coroutineScope.launch {
            favouriteDesserts = fetchRecipes("dessert")
            isDessertsLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // SmartRecipe logo
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.large_smartrecipe),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }

        // Sections for each category
        Section("Favourite Breakfasts", favouriteBreakfasts, isBreakfastLoading)
        Section("Favourite Dinners", favouriteDinners, isDinnersLoading)
        Section("Favourite Desserts", favouriteDesserts, isDessertsLoading)
    }
}

/**
 * A composable that displays a section for a specific category of recipes.
 * It shows the category title, followed by either a loading indicator or a list of recipes.
 *
 * @param title The title of the section (e.g., "Favourite Breakfasts").
 * @param recipes The list of recipes to display.
 * @param isLoading Boolean indicating whether the recipes are still loading.
 */
@Composable
fun Section(title: String, recipes: List<Recipe>, isLoading: Boolean) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        RecipeRow(recipes = recipes)
    }
}

/**
 * A composable that displays a list of recipe cards in a horizontal scrollable row.
 *
 * @param recipes The list of recipes to display.
 */
@Composable
fun RecipeRow(recipes: List<Recipe>) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(recipes) { recipe ->
            RecipeCard(recipe = recipe)
        }
    }
}

/**
 * A composable that displays a single recipe card with an image and title.
 *
 * @param recipe The recipe data to display (title and image).
 */
@Composable
fun RecipeCard(recipe: Recipe) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(recipe.image),
            contentDescription = recipe.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = recipe.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * A suspend function that fetches recipes of a specific category from the network.
 * It uses Retrofit to call the API and return the list of recipes.
 *
 * @param category The category of recipes to fetch (e.g., "breakfast", "dinner", "dessert").
 * @return A list of recipes belonging to the specified category.
 */
suspend fun fetchRecipes(category: String): List<Recipe> {
    val response = RetrofitInstance.api.searchRecipes(
        apiKey = "27ad4fb1e9f142a7b530bd3e61ec36d3",
        query = category,
        number = 5,
        sort = "popularity"
    )
    return if (response.isSuccessful) {
        response.body()?.results ?: emptyList()
    } else {
        emptyList()
    }
}
