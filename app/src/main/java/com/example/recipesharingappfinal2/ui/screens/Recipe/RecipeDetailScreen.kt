package com.example.recipesharingappfinal2.ui.screens.Recipe

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.recipesharingappfinal2.network.Recipe
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Composable function to display the details of a recipe fetched from Firebase Firestore.
 * The screen includes an image of the recipe, the recipe's title, description, ingredients, and instructions.
 * If the recipe data is still loading, a loading spinner is displayed.
 *
 * @param recipeId The unique identifier of the recipe to be displayed.
 * @param db FirebaseFirestore instance to interact with the Firestore database.
 */
@Composable
fun RecipeDetailScreen(recipeId: String, db: FirebaseFirestore) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }

    LaunchedEffect(recipeId) {
        val docRef = db.collection("recipes").document(recipeId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.data ?: return@addOnSuccessListener
                    recipe = Recipe(
                        id = recipeId,
                        title = (data["name"] ?: "Untitled").toString(),
                        image = (data["imageUrl"] ?: "").toString(),
                        description = (data["description"] ?: "").toString(),
                        ingredients = (((data["ingredients"] as? List<String>) ?: emptyList()).toString()),
                        instructions = (((data["instructions"] as? List<String>) ?: emptyList()).toString())
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.e("RecipeDetailScreen", "Error fetching recipe", e)
            }
    }

    if (recipe == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // SmartRecipe Logo
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(recipe?.image),
                    contentDescription = recipe?.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recipe Title
            Text(
                text = recipe?.title ?: "Unknown Recipe",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Recipe Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = recipe?.description ?: "No description available.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.Gray.copy(alpha = 0.1f), shape = MaterialTheme.shapes.medium)
                    .padding(8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recipe Ingredients
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                recipe?.ingredients?.forEach { ingredient ->
                    Text(
                        text = "- $ingredient",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recipe Instructions
            Text(
                text = "Instructions",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                recipe?.instructions?.forEachIndexed { index, instruction ->
                    Text(
                        text = "${index + 1}. $instruction",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }
            }
        }
    }
}