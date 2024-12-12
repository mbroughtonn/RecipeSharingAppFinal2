package com.example.recipesharingappfinal2.ui.screens.Search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipesharingappfinal2.network.RetrofitInstance
import com.example.recipesharingappfinal2.network.Recipe

/**
 * Composable function that displays search results for recipes based on a query.
 * It fetches data from the Spoonacular API, handles loading and error states, and
 * displays a list of recipes in a scrollable column.
 *
 * @param query The search term entered by the user to fetch recipes.
 */
@Composable
fun SearchResultsScreen(query: String) {
    var searchResults by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch search results based on the query from Spoonacular API
    LaunchedEffect(query) {
        try {
            isLoading = true
            val response = RetrofitInstance.api.searchRecipes(
                apiKey = "27ad4fb1e9f142a7b530bd3e61ec36d3",
                query = query,
                number = 10,
                sort = "popularity"
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    searchResults = body.results ?: emptyList()
                } else {
                    errorMessage = "No data received."
                }
            } else {
                errorMessage = "Error: ${response.code()} - ${response.message()}"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Search Results for \"$query\"",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (searchResults.isEmpty()) {
                Text("No results found for \"$query\"", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(searchResults) { recipe ->
                        SearchRecipeCard(recipe = recipe)
                    }
                }
            }
        }
    }
}

/**
 * Composable function that represents a card displaying a single recipe.
 *
 * @param recipe The recipe data to be displayed, including title and description.
 */
@Composable
fun SearchRecipeCard(recipe: Recipe) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = recipe.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = recipe.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/**
 * Preview function to visualize the SearchResultsScreen in Android Studio's Preview pane.
 * Used for design and layout testing.
 */
@Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    // Replace with your actual context
    SearchResultsScreen(query = "Pasta")
}
