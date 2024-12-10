package com.example.recipesharingappfinal2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.recipesharingappfinal2.ui.screens.*
import com.example.recipesharingappfinal2.ui.theme.RecipeSharingAppFinal2Theme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * Main entry point of the Recipe Sharing App.
 * This activity initializes Firebase and sets up the Compose-based UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)
        val auth = Firebase.auth
        val db = Firebase.firestore

        setContent {
            RecipeSharingAppFinal2Theme {
                MainScreen(auth, db)
            }
        }
    }
}

/**
 * MainScreen composable that sets up the navigation and Scaffold for the app.
 *
 * @param auth FirebaseAuth instance for user authentication.
 * @param db FirebaseFirestore instance for accessing Firestore database.
 */
@Composable
fun MainScreen(auth: FirebaseAuth, db: FirebaseFirestore) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    var isBottomNavEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(currentRoute) {
        isBottomNavEnabled = currentRoute != "account" && currentRoute != "login" && currentRoute != "signup"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isBottomNavEnabled) {
                BottomNavBar(navController)
            }
        },
        floatingActionButton = {
            if (currentRoute !in listOf(
                    "account", "addRecipe", "login", "signup",
                    "editProfile", "changePassword", "appSettings", "forgotPassword",
                    "search", "searchResults/{query}"
                )
            ) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("addRecipe")
                    },
                    containerColor = Color(0xFFF16722),
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.offset(y = (-10).dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add Recipe",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController = navController, db = db)
            }
            composable("search") {
                SearchScreen(navController) // Pass navController to handle search
            }
            composable("favourites") { FavouriteScreen() }
            composable("account") {
                AccountScreen(navController, auth)
                LaunchedEffect(true) {
                    isBottomNavEnabled = true
                }
            }
            composable("addRecipe") { AddRecipeScreen(db = db, navController = navController) }
            composable("signup") {
                SignUpScreen(navController, auth, onBottomNavEnabled = { isBottomNavEnabled = it })
            }
            composable("login") {
                LoginScreen(navController, auth, onBottomNavEnabled = { isBottomNavEnabled = it })
            }
            composable("forgotPassword") {
                ForgotPasswordScreen(navController, auth)
            }
            composable("editProfile") {
                EditProfileScreen(
                    navController = navController,
                    auth = auth,
                    currentName = auth.currentUser?.displayName ?: "",
                    currentEmail = auth.currentUser?.email ?: ""
                )
                LaunchedEffect(true) {
                    isBottomNavEnabled = false // Hiding the bottom nav on EditProfile screen
                }
            }
            composable("changePassword") {
                ChangePasswordScreen(auth = auth, navController = navController)
                LaunchedEffect(true) {
                    isBottomNavEnabled = false // Hiding the bottom nav on ChangePassword screen
                }
            }
            composable("appSettings") {
                AppSettingsScreen(navController = navController)
                LaunchedEffect(true) {
                    isBottomNavEnabled = false // Hiding the bottom nav on AppSettings screen
                }
            }
            composable(
                "recipeDetail/{recipeId}",
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                RecipeDetailScreen(recipeId, db)
            }
            composable("searchResults/{query}") { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                SearchResultsScreen(query) // Only pass the search query to the results screen
            }
        }
    }
}

/**
 * BottomNavBar composable that displays navigation options for the app.
 *
 * @param navController NavHostController for handling navigation actions.
 */
@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = false,
            onClick = { navController.navigate("search") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favourites") },
            label = { Text("Favourites") },
            selected = false,
            onClick = { navController.navigate("favourites") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Account") },
            label = { Text("Account") },
            selected = false,
            onClick = { navController.navigate("account") }
        )
    }
}
