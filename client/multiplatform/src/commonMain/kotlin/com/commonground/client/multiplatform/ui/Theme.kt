package com.commonground.client.multiplatform.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

enum class ThemeMode { Dark, Light, System }

object ThemeState {
    val current = mutableStateOf(ThemeMode.Dark)
}

// ── Brand palette ──────────────────────────────────────────────────
private val Emerald = Color(0xFF2E8B57)
private val EmeraldLight = Color(0xFF3CB371)
private val EmeraldContainer = Color(0xFFD4EDDA)
private val SkyBlue = Color(0xFF4F9DFF)
private val SkyBlueLight = Color(0xFF6BAFFF)
private val SkyBlueContainer = Color(0xFFD6EAFF)
private val Coral = Color(0xFFFF6B6B)
private val CoralLight = Color(0xFFFF8585)
private val CoralContainer = Color(0xFFFFE0E0)
private val AlmostBlack = Color(0xFF1E1E1E)
private val DarkSurface = Color(0xFF121212)
private val DarkSurfaceVariant = Color(0xFF1E1E1E)

// ── Light scheme — using user's specified palette ───────────────────
private val LightScheme = lightColorScheme(
    primary = Emerald,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = Color(0xFF0A2A1A),
    secondary = SkyBlue,
    onSecondary = Color.White,
    secondaryContainer = SkyBlueContainer,
    onSecondaryContainer = Color(0xFF0A1F3A),
    tertiary = Coral,
    onTertiary = Color.White,
    tertiaryContainer = CoralContainer,
    onTertiaryContainer = Color(0xFF3A0A0A),
    background = Color(0xFFFAFAFA),
    onBackground = AlmostBlack,
    surface = Color(0xFFF3F5F7),
    onSurface = AlmostBlack,
    surfaceVariant = Color(0xFFE8EAED),
    onSurfaceVariant = Color(0xFF525252),
    outline = Color(0xFFD0D0D0),
    outlineVariant = Color(0xFFE0E0E0),
    error = Color(0xFFD32F2F),
)

// ── Dark scheme — derived from light palette ────────────────────────
private val DarkScheme = darkColorScheme(
    primary = EmeraldLight,
    onPrimary = Color(0xFF0A2A1A),
    primaryContainer = Emerald,
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = SkyBlueLight,
    onSecondary = Color(0xFF0A1F3A),
    secondaryContainer = SkyBlue,
    onSecondaryContainer = Color(0xFFB3D6FF),
    tertiary = CoralLight,
    onTertiary = Color(0xFF3A0A0A),
    tertiaryContainer = Coral,
    onTertiaryContainer = Color(0xFFFFCDCD),
    background = Color(0xFF0A0A0A),
    onBackground = Color(0xFFE0E0E0),
    surface = DarkSurface,
    onSurface = Color(0xFFE8E8E8),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF2A2A2A),
    error = Color(0xFFEF5350),
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
    val scheme = if (isDark) DarkScheme else LightScheme
    MaterialTheme(colorScheme = scheme, content = content)
}
