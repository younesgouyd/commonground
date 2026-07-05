package com.commonground.client.multiplatform.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

import androidx.compose.runtime.mutableStateOf

enum class ThemeMode { Dark, Light, System }

object ThemeState {
    val current = mutableStateOf(ThemeMode.Dark)
}

private val GreenPrimary = Color(0xFF4CAF50)
private val GreenPrimaryDark = Color(0xFF2E7D32)
private val GreenOnPrimary = Color(0xFF0D1F0D)
private val GreenContainer = Color(0xFF1B5E20)
private val GreenOnContainer = Color(0xFFA5D6A7)

private val DarkSurface = Color(0xFF0F0F0F)
private val DarkSurfaceVariant = Color(0xFF1A1A1A)
private val DarkBackground = Color(0xFF070707)

private val DarkGreenScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenOnContainer,
    secondary = Color(0xFF81C784),
    onSecondary = Color(0xFF0D1F0D),
    secondaryContainer = Color(0xFF1B5E20),
    onSecondaryContainer = Color(0xFFC8E6C9),
    tertiary = Color(0xFF66BB6A),
    onTertiary = Color(0xFF0D1F0D),
    background = DarkBackground,
    onBackground = Color(0xFFE0E0E0),
    surface = DarkSurface,
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF2A2A2A),
    error = Color(0xFFEF5350),
)

private val LightGreenScheme = lightColorScheme(
    primary = GreenPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF0D1F0D),
    secondary = Color(0xFF43A047),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF0D1F0D),
    tertiary = Color(0xFF2E7D32),
    onTertiary = Color.White,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFFCCCCCC),
    outlineVariant = Color(0xFFE0E0E0),
    error = Color(0xFFD32F2F),
)

@Composable
fun CommonGroundTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.Dark -> true
        ThemeMode.Light -> false
        ThemeMode.System -> isSystemInDarkTheme()
    }
    val scheme = if (isDark) DarkGreenScheme else LightGreenScheme
    MaterialTheme(colorScheme = scheme, content = content)
}
