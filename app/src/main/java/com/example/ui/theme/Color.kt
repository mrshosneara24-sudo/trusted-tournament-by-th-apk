package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core Black, White, Yellow & Pink Balanced Palette
val ObsidianBlack = Color(0xFF09090D)
val VelvetBlack = Color(0xFF121218)
val SmokedPlum = Color(0xFF1C1A24)
val NeonPink = Color(0xFFFF2E88)
val BkashPink = Color(0xFFE2136E)
val RosePink = Color(0xFFFF5CA4)
val SoftPink = Color(0xFFFF85BD)
val BlushPink = Color(0xFFFFD6E8)
val GoldChampagne = Color(0xFFFFD60A)
val WarmYellow = Color(0xFFFFE57F)
val ErrorRed = Color(0xFFEF4444)

// Mapped Semantic Tokens (Balanced Black + Crisp White + Esports Yellow + Pink Accent)
val FieryOrange = NeonPink
val FlameOrange = Color(0xFFFFB703)
val FlameYellow = GoldChampagne
val CyanGamer = Color(0xFF38BDF8)
val FlameCyan = Color(0xFF38BDF8)
val VictoryGreen = Color(0xFF10B981)

val DarkBackground = ObsidianBlack
val DarkSurface = VelvetBlack
val DarkCard = Color(0xFF16151E)
val DarkSurfaceVariant = SmokedPlum
val DarkBorder = Color(0xFF322E40)

val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFE8EAF0)
val TextMuted = Color(0xFFB4B8C4)

val PinkBlackHeaderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1E0B18),
        Color(0xFF111118),
        Color(0xFF09090D)
    )
)

val PinkBlackButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        NeonPink,
        BkashPink
    )
)


