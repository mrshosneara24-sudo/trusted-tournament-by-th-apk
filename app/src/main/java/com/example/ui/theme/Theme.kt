package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsportsDarkColorScheme = darkColorScheme(
    primary = FireOrange,
    onPrimary = TextPrimary,
    primaryContainer = Color(0xFF331207),
    onPrimaryContainer = Color.White,
    secondary = WaterCyan,
    onSecondary = Color(0xFF04111D),
    secondaryContainer = Color(0xFF06293A),
    onSecondaryContainer = IceMist,
    tertiary = EmberGold,
    onTertiary = Color(0xFF1A1002),
    tertiaryContainer = Color(0xFF2E2105),
    onTertiaryContainer = WarmYellow,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    error = ErrorRed
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
