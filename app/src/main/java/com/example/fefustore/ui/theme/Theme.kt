package com.example.fefustore.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = LightBluePrimary,
    primaryContainer = LightBluePrimaryContainer,
    secondary = LightBlueSecondary,
    background = LightBlueBackground,
    surface = LightBlueSurface,
    onPrimary = LightBlueOnPrimary,
    onBackground = LightBlueOnBackground,
    onSurface = LightBlueOnSurface
)

@Composable
fun FEFUStoreTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}