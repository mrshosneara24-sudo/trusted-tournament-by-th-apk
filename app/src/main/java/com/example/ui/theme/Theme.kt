package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsportsDarkColorScheme = darkColorScheme(
    primary = FieryOrange,
    onPrimary = TextPrimary,
    primaryContainer = Color(0xFF381507),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = FlameYellow,
    onSecondary = Color(0xFF1E1600),
    secondaryContainer = Color(0xFF3E2D00),
    onSecondaryContainer = Color(0xFFFFE082),
    tertiary = BkashPink,
    onTertiary = TextPrimary,
    tertiaryContainer = Color(0xFF42021B),
    onTertiaryContainer = Color(0xFFFFD8E4),
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
