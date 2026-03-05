package com.pantrycheck.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pantrycheck.app.ui.screens.HomePlaceholderScreen
import com.pantrycheck.app.ui.screens.SettingsPlaceholderScreen

@Composable
fun PantryCheckNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = PantryRoute.Home.route
    ) {
        composable(PantryRoute.Home.route) {
            HomePlaceholderScreen(
                onOpenSettings = { navController.navigate(PantryRoute.Settings.route) }
            )
        }
        composable(PantryRoute.Settings.route) {
            SettingsPlaceholderScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
