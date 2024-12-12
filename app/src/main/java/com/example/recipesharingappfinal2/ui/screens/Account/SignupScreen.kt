package com.example.recipesharingappfinal2.ui.screens.Account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipesharingappfinal2.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

/**
 * Composable function that displays the sign-up screen where users can create a new account.
 * It handles user input for name, email, password, and confirm password fields, and validates
 * the input. Upon successful sign-up, user information is saved to Firebase and the user is
 * navigated to the home screen.
 *
 * @param navController The [NavController] used for navigation to other screens.
 * @param auth The [FirebaseAuth] instance used to authenticate users.
 * @param onBottomNavEnabled A callback function to control the visibility of the bottom navigation bar.
 */
@Composable
fun SignUpScreen(navController: NavController, auth: FirebaseAuth, onBottomNavEnabled: (Boolean) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val firestore = FirebaseFirestore.getInstance()

    // Disabling bottom navigation
    onBottomNavEnabled(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Smart Recipe Logo
        Image(
            painter = painterResource(R.drawable.large_smartrecipe),
            contentDescription = "SmartRecipe Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Create Your Account",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Name Input
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
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

        Spacer(modifier = Modifier.height(8.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
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

        // Sign Up Button
        Button(
            onClick = {
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else if (email.isBlank() || password.isBlank() || name.isBlank()) {
                    errorMessage = "All fields are required"
                } else {
                    errorMessage = null
                    isLoading = true
                    coroutineScope.launch {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.let {
                                        val userData = hashMapOf(
                                            "email" to it.email,
                                            "uid" to it.uid,
                                            "name" to name,
                                        )

                                        firestore.collection("users")
                                            .document(it.uid)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                navController.navigate("home") {
                                                    popUpTo("signup") { inclusive = true }
                                                }
                                            }
                                            .addOnFailureListener { exception ->
                                                errorMessage = "Failed to save user info: ${exception.localizedMessage}"
                                            }
                                    }
                                } else {
                                    errorMessage = task.exception?.localizedMessage
                                        ?: "Sign up failed. Please try again."
                                }
                            }
                    }
                }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF16722),
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(text = "Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Already have an account? Login Button
        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already have an account? Log in")
        }
    }
}

