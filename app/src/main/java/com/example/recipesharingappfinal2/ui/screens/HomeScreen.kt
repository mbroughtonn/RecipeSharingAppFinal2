package com.example.recipesharingappfinal2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.recipesharingappfinal2.R
import com.example.recipesharingappfinal2.network.RetrofitInstance
import com.example.recipesharingappfinal2.network.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/**
 * Composable function representing the Home Screen of the app.
 * Displays trending recipes, latest recipes, and user-added recipes.
 * Fetches data from an external API (Spoonacular) and Firestore.
 *
 * @param navController Navigation controller to handle screen transitions.
 * @param db Firebase Firestore instance to retrieve user recipes.
 */
@Composable
fun HomeScreen(navController: NavController, db: FirebaseFirestore) {
    var trendingRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var latestRecipes by remember { mutableStateOf<List<Recipe>>(emptyList()) }
    var userRecipes by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch recipes from Spoonacular API and Firebase Firestore
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Fetch trending recipes
                val trendingResponse = RetrofitInstance.api.searchRecipes(
                    apiKey = "27ad4fb1e9f142a7b530bd3e61ec36d3",
                    query = "",
                    number = 5,
                    sort = "popularity"
                )
                if (trendingResponse.isSuccessful) {
                    trendingRecipes = trendingResponse.body()?.results ?: emptyList()
                }

                // Fetch latest recipes
                val latestResponse = RetrofitInstance.api.searchRecipes(
                    apiKey = "27ad4fb1e9f142a7b530bd3e61ec36d3",
                    query = "",
                    number = 5,
                    sort = "time"
                )
                if (latestResponse.isSuccessful) {
                    latestRecipes = latestResponse.body()?.results ?: emptyList()
                }

                // Fetch user-created recipes from Firestore
                db.collection("recipes").get()
                    .addOnSuccessListener { querySnapshot ->
                        userRecipes = querySnapshot.documents.mapNotNull { doc ->
                            doc.data as? Map<String, String>
                        }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App logo or header
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(R.drawable.large_smartrecipe),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        }

        // Trending Recipes Section
        Text(
            text = "Trending Recipes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            HomeRecipeRow(recipes = trendingRecipes, navController = navController) // Pass navController to the row
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Latest Recipes Section
        Text(
            text = "Latest Recipes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            HomeRecipeRow(recipes = latestRecipes, navController = navController) // Pass navController to the row
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Your Recipes Section
        Text(
            text = "Your Recipes",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (userRecipes.isEmpty()) {
            Text(text = "No recipes added yet.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(userRecipes) { recipe ->
                    UserRecipeCard(recipe = recipe, navController = navController) // Pass navController here
                }
            }
        }
    }
}

/**
 * Composable function to display a row of recipes in a horizontally scrollable list.
 *
 * @param recipes List of Recipe objects to display.
 * @param navController Navigation controller to handle navigation to recipe details.
 */
@Composable
fun HomeRecipeRow(recipes: List<Recipe>, navController: NavController) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(recipes) { recipe ->
            HomeRecipeCard(recipe = recipe, navController = navController)
        }
    }
}

/**
 * Composable function to display individual recipe card in the home screen.
 *
 * @param recipe Recipe object to display.
 * @param navController Navigation controller to handle navigation to recipe details.
 */
@Composable
fun HomeRecipeCard(recipe: Recipe, navController: NavController) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp)
            .clickable {
                // Navigate to RecipeDetailScreen when clicked
                navController.navigate("recipeDetail/${recipe.id}")
            }
    ) {
        // Recipe Image
        Image(
            painter = rememberAsyncImagePainter(recipe.image),
            contentDescription = recipe.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentScale = ContentScale.Crop
        )

        // Recipe Title
        Text(
            text = recipe.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1, // Restrict to one line
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/**
 * Composable function to display user-added recipes in a list format.
 *
 * @param recipe Map representing the user recipe.
 * @param navController Navigation controller to handle navigation to recipe details.
 */
@Composable
fun UserRecipeCard(recipe: Map<String, String>, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .clickable {
                // Navigate to RecipeDetailScreen if user clicks on the recipe
                navController.navigate("recipeDetail/${recipe["id"]}")
            }
    ) {
        // Recipe Name
        Text(
            text = recipe["name"] ?: "Untitled Recipe",
            style = MaterialTheme.typography.bodyLarge
        )
        // Recipe Description
        Text(
            text = recipe["description"] ?: "No description available",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}