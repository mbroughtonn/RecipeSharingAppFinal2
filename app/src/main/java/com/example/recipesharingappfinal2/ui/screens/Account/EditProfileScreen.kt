package com.example.recipesharingappfinal2.ui.screens.Account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipesharingappfinal2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

/**
 * Composable function representing the Edit Profile screen.
 *
 * This screen allows the user to edit their name and email. It displays
 * the current user's profile picture and provides input fields for editing
 * the user's name and email address. After making changes, the user can save
 * their changes, and if successful, the updated profile is saved to Firebase.
 *
 * @param navController The NavController used for navigating between screens.
 * @param auth The FirebaseAuth instance used for managing user authentication.
 * @param currentName The current display name of the user.
 * @param currentEmail The current email of the user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    auth: FirebaseAuth,
    currentName: String,
    currentEmail: String
) {
    var name by remember { mutableStateOf(TextFieldValue(currentName)) }
    var email by remember { mutableStateOf(TextFieldValue(currentEmail)) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // SmartRecipe Logo
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

        // "Edit Profile" with Back Button
        TopAppBar(
            title = { Text("Edit Profile") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Image(
                painter = painterResource(R.drawable.ic_account),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name Input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Save Button
            Button(
                onClick = {
                    when {
                        name.text.isBlank() || email.text.isBlank() -> {
                            errorMessage = "Name and Email cannot be empty."
                        }
                        email.text != currentEmail && !email.text.contains('@') -> {
                            errorMessage = "Please enter a valid email address."
                        }
                        else -> {
                            auth.currentUser?.let { user ->
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name.text)
                                    .build()

                                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        user.updateEmail(email.text).addOnCompleteListener { emailTask ->
                                            if (emailTask.isSuccessful) {
                                                errorMessage = null
                                                navController.popBackStack()
                                            } else {
                                                errorMessage = emailTask.exception?.localizedMessage
                                                    ?: "Failed to update email."
                                            }
                                        }
                                    } else {
                                        errorMessage = task.exception?.localizedMessage
                                            ?: "Failed to update name."
                                    }
                                }
                            } ?: run {
                                errorMessage = "User not authenticated."
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFF16722),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Icon(Icons.Filled.Done, contentDescription = "Save", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Changes")
            }
        }
    }
}
