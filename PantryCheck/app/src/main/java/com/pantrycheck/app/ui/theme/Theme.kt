package com.pantrycheck.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = BrandGreen,
    onPrimary = BrandCream,
    secondary = BrandGreenDark,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = BrandGreen,
    secondary = BrandGreenDark,
    error = ErrorRed
)

@Composable
fun PantryCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
