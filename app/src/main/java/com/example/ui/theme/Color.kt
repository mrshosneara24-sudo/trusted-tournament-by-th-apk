package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Fire & Water Logo-Inspired Obsidian Esports Palette (matched to th_logo)
val ObsidianBlack = Color(0xFF060912)
val VelvetBlack = Color(0xFF0B1120)
val SmokedPlum = Color(0xFF141D33)

// Fire Spectrum (Left side of Logo)
val FireRed = Color(0xFFFF3D00)
val FireOrange = Color(0xFFFF6D00)
val EmberGold = Color(0xFFFFB703)
val GoldChampagne = Color(0xFFFFD60A)
val WarmYellow = Color(0xFFFFE57F)

// Water / Ice Spectrum (Right side of Logo)
val WaterCyan = Color(0xFF00E5FF)
val WaterBlue = Color(0xFF0EA5E9)
val DeepCobalt = Color(0xFF2563EB)
val IceMist = Color(0xFFBAE6FD)

// Functional Accents
val BkashPink = Color(0xFFE2136E) // Strictly reserved for official bKash payment badges
val ErrorRed = Color(0xFFEF4444)
val VictoryGreen = Color(0xFF10B981)

// Semantic Tokens mapped cleanly to Fire & Water
val NeonPink = FireRed
val RosePink = FireOrange
val SoftPink = EmberGold
val BlushPink = IceMist
val FieryOrange = FireOrange
val FlameOrange = FireOrange
val FlameYellow = EmberGold
val CyanGamer = WaterCyan
val FlameCyan = WaterCyan

val DarkBackground = ObsidianBlack
val DarkSurface = VelvetBlack
val DarkCard = Color(0xFF10182B)
val DarkElevated = Color(0xFF17223B)
val DarkSurfaceVariant = SmokedPlum
val DarkBorder = Color(0xFF233354)

// Crisp High-Contrast Text Hierarchy
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF9FB0CE)
val TextMuted = Color(0xFF6B7D9E)

// Fire & Water Dual-Element Gradients
val FireWaterHeaderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF280D07),
        Color(0xFF0B1120),
        Color(0xFF062135)
    )
)

val FireWaterButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        FireRed,
        FireOrange,
        WaterBlue
    )
)

val FireWaterBorderGradient = Brush.horizontalGradient(
    colors = listOf(
        FireOrange,
        EmberGold,
        WaterCyan
    )
)

val PinkBlackHeaderGradient = FireWaterHeaderGradient

val PinkBlackButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        FireRed,
        FireOrange,
        DeepCobalt
    )
)

val GoldAccentGradient = Brush.horizontalGradient(
    colors = listOf(
        EmberGold,
        FireOrange,
        WaterCyan
    )
)

val WalletHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF261009),
        Color(0xFF0E1629),
        Color(0xFF062134)
    )
)
