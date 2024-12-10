package com.example.recipesharingappfinal2.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recipesharingappfinal2.R

/**
 * Composable function representing the App Settings screen.
 *
 * This screen allows the user to toggle various application settings, such as enabling dark mode
 * and notifications. It also includes navigation options for returning to the previous screen or
 * saving the changes and navigating back to the account screen.
 *
 * @param navController The NavController used to handle navigation between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }
    var isNotificationsEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SmartRecipe Logo
        Image(
            painter = painterResource(R.drawable.large_smartrecipe),
            contentDescription = "SmartRecipe Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )

        // "App Settings" with Back Button
        TopAppBar(
            title = { Text("App Settings") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Settings Options
        SettingsOptionItem(title = "Dark Mode", isChecked = isDarkMode, onCheckedChange = { isDarkMode = it })
        SettingsOptionItem(title = "Enable Notifications", isChecked = isNotificationsEnabled, onCheckedChange = { isNotificationsEnabled = it })

        Spacer(modifier = Modifier.weight(1f))

        // Save Button
        Button(
            onClick = { navController.popBackStack()
                        navController.navigate("account")
                      },
            colors = ButtonDefaults.buttonColors(
                containerColor = androidx.compose.ui.graphics.Color(0xFFF16722),
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Save Settings", modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Save Settings")
        }
    }
}

/**
 * Composable function representing an individual settings option item.
 *
 * Each item displays a title and a toggle switch that allows the user to enable or disable the option.
 *
 * @param title The title of the settings option.
 * @param isChecked A Boolean indicating whether the option is currently enabled.
 * @param onCheckedChange A lambda function triggered when the switch is toggled.
 */
@Composable
fun SettingsOptionItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
