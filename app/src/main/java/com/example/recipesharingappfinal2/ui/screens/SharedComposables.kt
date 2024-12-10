package com.example.recipesharingappfinal2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Composable function to display a horizontally scrollable row of recipe cards.
 * Each card represents a recipe name passed as a list of strings.
 *
 * @param recipes A list of recipe names to be displayed in the row.
 */
@Composable
fun RecipeRow(recipes: List<String>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(recipes) { recipe ->
            RecipeCard(recipeName = recipe)
        }
    }
}

/**
 * Composable function to display a card for a single recipe.
 * The card contains the recipe's name displayed in the center.
 *
 * @param recipeName The name of the recipe to be displayed in the card.
 */
@Composable
fun RecipeCard(recipeName: String) {
    Card(
        modifier = Modifier.size(width = 200.dp, height = 120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = recipeName,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}