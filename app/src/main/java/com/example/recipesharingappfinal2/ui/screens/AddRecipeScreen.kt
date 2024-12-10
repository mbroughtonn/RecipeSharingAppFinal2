package com.example.recipesharingappfinal2.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.rememberAsyncImagePainter

/**
 * Composable function to display the Add Recipe screen.
 *
 * This screen provides a form for users to add a new recipe with fields for the recipe name,
 * description, ingredients, instructions, and an option to upload an image.
 * It includes functionality to save the recipe to Firebase Firestore.
 *
 * @param db An optional instance of [FirebaseFirestore] for interacting with the Firestore database.
 * @param navController A [NavController] instance for handling navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(db: FirebaseFirestore?, navController: NavController) {
    var recipeName by remember { mutableStateOf("") }
    var recipeDescription by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var saveStatus by remember { mutableStateOf("") }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri.toString()
            println("Image URI: $imageUri")
        }
    }

    /**
     * Saves the recipe to Firestore after validating the input fields.
     * Displays appropriate messages on success or failure.
     */
    fun saveRecipe() {
        if (recipeName.isBlank() || recipeDescription.isBlank() || ingredients.isBlank() || instructions.isBlank()) {
            saveStatus = "Please fill in all fields."
            return
        }

        if (db != null) {
            val recipeData = hashMapOf(
                "name" to recipeName,
                "description" to recipeDescription,
                "ingredients" to ingredients,
                "instructions" to instructions,
                "imageUri" to imageUri
            )

            db.collection("recipes")
                .add(recipeData)
                .addOnSuccessListener {
                    saveStatus = "Recipe saved successfully!"
                    navController.popBackStack()
                }
                .addOnFailureListener { e ->
                    saveStatus = "Failed to save recipe: ${e.message}"
                }
        } else {
            saveStatus = "Database not initialized."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // "Add Recipe" with Back Button
        TopAppBar(
            title = { Text("Add Recipe") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Recipe Name
        TextField(
            value = recipeName,
            onValueChange = { recipeName = it },
            label = { Text("Recipe Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Recipe Description
        TextField(
            value = recipeDescription,
            onValueChange = { recipeDescription = it },
            label = { Text("Recipe Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Recipe Ingredients
        TextField(
            value = ingredients,
            onValueChange = { ingredients = it },
            label = { Text("Ingredients") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Recipe Instructions
        TextField(
            value = instructions,
            onValueChange = { instructions = it },
            label = { Text("Instructions") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Recipe Image Upload
        Text(text = "Upload Recipe Image", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(MaterialTheme.shapes.medium.copy(CornerSize(16.dp)))
                .background(Color.Gray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text("Upload Image")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = { saveRecipe() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF16722)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Recipe")
        }

        // Status Message if fails/passes
        if (saveStatus.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = saveStatus,
                color = if (saveStatus.startsWith("Failed")) Color.Red else Color.Green
            )
        }
    }
}
