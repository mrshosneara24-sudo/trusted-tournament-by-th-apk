package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Calm, High-Contrast Luxury Dark Esports Palette
val ObsidianBlack = Color(0xFF07080D)
val VelvetBlack = Color(0xFF10121B)
val SmokedPlum = Color(0xFF1A1D2B)
val NeonPink = Color(0xFFFF2E88)
val BkashPink = Color(0xFFE2136E)
val RosePink = Color(0xFFFF5CA4)
val SoftPink = Color(0xFFFF85BD)
val BlushPink = Color(0xFFFFD6E8)
val GoldChampagne = Color(0xFFFFD60A)
val WarmYellow = Color(0xFFFFE57F)
val ErrorRed = Color(0xFFEF4444)

// Mapped Semantic Tokens
val FieryOrange = NeonPink
val FlameOrange = Color(0xFFFFB703)
val FlameYellow = GoldChampagne
val CyanGamer = Color(0xFF38BDF8)
val FlameCyan = Color(0xFF38BDF8)
val VictoryGreen = Color(0xFF10B981)

val DarkBackground = ObsidianBlack
val DarkSurface = VelvetBlack
val DarkCard = Color(0xFF151824)
val DarkElevated = Color(0xFF1D2132)
val DarkSurfaceVariant = SmokedPlum
val DarkBorder = Color(0xFF282D42)

// Calm Visual Hierarchy for Text
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF9EA8BF)
val TextMuted = Color(0xFF6B738C)

val PinkBlackHeaderGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF1A0D1C),
        Color(0xFF0C0D15),
        Color(0xFF07080D)
    )
)

val PinkBlackButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        NeonPink,
        BkashPink
    )
)

val GoldAccentGradient = Brush.horizontalGradient(
    colors = listOf(
        GoldChampagne,
        FlameOrange
    )
)

val WalletHeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF231022),
        Color(0xFF141724),
        Color(0xFF0B0D14)
    )
)
