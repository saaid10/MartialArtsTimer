package com.saee.combattimer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CombatRed = Color(0xFFE31E24)
private val CombatRedDark = Color(0xFF8E1216)
private val CombatAmber = Color(0xFFFFD400)
private val SurfaceDark = Color(0xFF0B1020)
private val SurfaceDarkAlt = Color(0xFF121A33)

private val DarkColors = darkColorScheme(
    primary = CombatRed,
    secondary = CombatAmber,
    background = SurfaceDark,
    surface = SurfaceDarkAlt,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColors = lightColorScheme(
    primary = CombatRedDark,
    secondary = CombatAmber,
)

@Composable
fun CombatTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
