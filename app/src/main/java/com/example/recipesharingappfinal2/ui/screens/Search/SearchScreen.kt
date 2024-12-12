package com.example.recipesharingappfinal2.ui.screens.Search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recipesharingappfinal2.R

/**
 * Composable function to display the search screen where users can input a search query to find recipes.
 * The screen includes a logo, a search bar, a placeholder for results, and a list of recent searches.
 */
@Composable
fun SearchScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Smart Recipe Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.large_smartrecipe),
                contentDescription = "SmartRecipe Logo",
                contentScale = ContentScale.Crop
            )
        }

        // Search Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Placeholder for Results
        Text(text = "Start typing to search for recipes...")

        // Recent Searches Section
        Text(
            text = "Recent Searches",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            RecentSearchItem("Spaghetti Bolognese", navController)
            RecentSearchItem("Vegan Pancakes", navController)
            RecentSearchItem("Chicken Alfredo", navController)
        }

        // Search Button to Navigate to Results Screen
        Button(
            onClick = {
                if (searchQuery.text.isNotEmpty()) {
                    navController.navigate("searchResults/${searchQuery.text}")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF16722)
            )
        ) {
            Text(
                text = "Search",
                color = Color.White
            )
        }
    }
}

/**
 * Composable function to display an individual recent search item as a clickable button.
 *
 * @param query The search query to be displayed as a recent search item.
 * @param navController NavHostController for navigation.
 */
@Composable
fun RecentSearchItem(query: String, navController: NavHostController) {
    TextButton(
        onClick = {
            navController.navigate("searchResults/$query")
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = query,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
