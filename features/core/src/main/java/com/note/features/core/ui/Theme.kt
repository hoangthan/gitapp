package com.git.features.core.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Colors.PrimaryLight,
    onPrimary = Colors.OnPrimaryLight,
    background = Colors.BackgroundLight,
    onBackground = Colors.OnBackgroundLight,
    surface = Colors.SurfaceLight,
    onSurface = Colors.OnSurfaceLight,
    surfaceVariant = Colors.SurfaceVariantLight
)

private val DarkColors = darkColorScheme(
    primary = Colors.PrimaryDark,
    onPrimary = Colors.OnPrimaryDark,
    background = Colors.BackgroundDark,
    onBackground = Colors.OnBackgroundDark,
    surface = Colors.SurfaceDark,
    onSurface = Colors.OnSurfaceDark,
    surfaceVariant = Colors.SurfaceVariantDark
)

@Composable
fun ComposeAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
