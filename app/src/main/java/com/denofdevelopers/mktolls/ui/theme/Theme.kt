package com.denofdevelopers.mktolls.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = YellowPrimary,
    secondary = OrangeSecondary,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = OnPrimary,
    onSecondary = OnSecondary,
    onBackground = OnBackground,
    onSurface = OnSurface
)

@Composable
fun MKTollsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // For now, we only use light theme for specific "toll" look

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
