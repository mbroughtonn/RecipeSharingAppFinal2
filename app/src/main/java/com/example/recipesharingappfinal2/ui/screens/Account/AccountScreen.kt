package com.example.recipesharingappfinal2.ui.screens.Account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recipesharingappfinal2.R

/**
 * Composable function representing the Account screen of the Recipe Sharing App.
 *
 * This screen displays user profile information such as the profile picture,
 * display name, and email address. It also provides account-related options like
 * editing the profile, changing the password, accessing app settings, and logging out.
 *
 * @param navController The NavHostController used to navigate between screens in the app.
 * @param auth The FirebaseAuth instance used to retrieve and manage the current user's authentication state.
 */
@Composable
fun AccountScreen(navController: NavHostController, auth: com.google.firebase.auth.FirebaseAuth) {
    // Fetching the current user from Firebase
    val currentUser = auth.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // SmartRecipe logo
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

        // Profile Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
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

            // Displaying current user's display name
            Text(
                text = currentUser?.displayName ?: "User Name",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Displaying current user's email
            Text(
                text = currentUser?.email ?: "Email not available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(16.dp))

        // Account Options
        AccountOptionItem(
            title = "Edit Profile",
            icon = Icons.Default.Edit,
            onClick = { navController.navigate("editProfile") }
        )
        AccountOptionItem(
            title = "Change Password",
            icon = Icons.Default.Lock,
            onClick = { navController.navigate("changePassword") }
        )
        AccountOptionItem(
            title = "App Settings",
            icon = Icons.Default.Settings,
            onClick = { navController.navigate("appSettings") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = {
                auth.signOut() // Log out the user
                navController.navigate("login") { // Navigate to LoginScreen
                    popUpTo("home") { inclusive = true } // Clear back stack
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF16722),
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Logout")
        }
    }
}

/**
 * Composable function for a single account option item.
 *
 * Each item represents an actionable option in the Account screen, displaying
 * an icon and a title. When the item is clicked, the provided onClick function is executed.
 *
 * @param title The title of the account option displayed next to the icon.
 * @param icon The icon associated with the account option.
 * @param onClick A lambda function executed when the account option is selected.
 */
@Composable
fun AccountOptionItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = "$title Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
