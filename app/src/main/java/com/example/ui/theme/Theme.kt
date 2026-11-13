package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsportsDarkColorScheme = darkColorScheme(
    primary = NeonPink,
    onPrimary = TextPrimary,
    primaryContainer = Color(0xFF330C20),
    onPrimaryContainer = Color.White,
    secondary = FlameYellow,
    onSecondary = Color(0xFF121218),
    secondaryContainer = Color(0xFF2D2508),
    onSecondaryContainer = WarmYellow,
    tertiary = BkashPink,
    onTertiary = TextPrimary,
    tertiaryContainer = Color(0xFF38091E),
    onTertiaryContainer = Color.White,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder
)

@Composable
fun TrustedTournamentTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsportsDarkColorScheme,
        typography = Typography,
        content = content
    )
}
