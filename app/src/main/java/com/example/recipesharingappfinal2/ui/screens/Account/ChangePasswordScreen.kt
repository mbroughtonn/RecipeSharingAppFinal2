package com.example.recipesharingappfinal2.ui.screens.Account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipesharingappfinal2.R
import com.google.firebase.auth.FirebaseAuth

/**
 * Composable function representing the Change Password screen.
 *
 * This screen allows authenticated users to change their password by providing their current password,
 * a new password, and confirming the new password. It includes validation for input fields and error
 * messages for invalid inputs. The new password must be at least 6 characters long, and the new password
 * must match the confirmation password.
 *
 * @param auth An instance of [FirebaseAuth] used to handle password updates for the current user.
 * @param navController The NavController used to handle navigation between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(auth: FirebaseAuth, navController: NavController) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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

        // "Change Password" with Back Button
        TopAppBar(
            title = { Text("Change Password") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Current Password Input
        OutlinedTextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Current Password") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        painter = painterResource(
                            if (showPassword) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                        ),
                        contentDescription = if (showPassword) "Hide Password" else "Show Password"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // New Password Input
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Confirm Password Input
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
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
                    currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() -> {
                        errorMessage = "All fields are required."
                    }
                    newPassword != confirmPassword -> {
                        errorMessage = "New Password and Confirm Password do not match."
                    }
                    newPassword.length < 6 -> {
                        errorMessage = "Password must be at least 6 characters."
                    }
                    else -> {
                        // Perform password update logic
                        auth.currentUser?.let { user ->
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        errorMessage = null
                                        // Navigate back to Account Screen
                                        navController.popBackStack() // Go back to AccountScreen
                                    } else {
                                        errorMessage = task.exception?.localizedMessage
                                            ?: "Failed to update password."
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
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Done, contentDescription = "Save", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save")
        }
    }
}
