package com.pantrycheck.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pantrycheck.app.ui.navigation.PantryCheckNavHost
import com.pantrycheck.app.ui.theme.PantryCheckTheme

@Composable
fun PantryCheckApp() {
    PantryCheckTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            PantryCheckNavHost()
        }
    }
}
