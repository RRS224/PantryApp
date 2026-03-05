package com.pantrycheck.app.ui.navigation

sealed class PantryRoute(val route: String) {
    data object Home : PantryRoute("home")
    data object Settings : PantryRoute("settings")
}
