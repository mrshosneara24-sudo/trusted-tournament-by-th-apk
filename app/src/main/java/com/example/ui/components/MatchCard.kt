package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameCategory
import com.example.model.MatchStatus
import com.example.model.TournamentMatch
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.EmberGold
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.theme.FireWaterBorderGradient
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterCyan

@Composable
fun MatchCard(
    match: TournamentMatch,
    isAlreadyBooked: Boolean,
    onBookClick: () -> Unit,
    onViewRoomClick: () -> Unit,
    onViewSlotGridClick: () -> Unit,
    nowMillis: Long = System.currentTimeMillis(),
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isHyped by rememberSaveable(match.id) { mutableStateOf(false) }
    val baseHypeCount = remember(match.id) { 12 + (match.bookedSlots % 19) }
    val isLiveMatch = match.status == MatchStatus.ROOM_OPEN || match.status == MatchStatus.ONGOING

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("match_card_${match.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = if (isAlreadyBooked) {
            BorderStroke(1.4.dp, VictoryGreen.copy(alpha = 0.7f))
        } else {
            BorderStroke(1.4.dp, FireWaterBorderGradient)
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Fire & Water Dual-Element Top Accent Strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(FireRed, EmberGold, WaterCyan, WaterBlue)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top Row: Category Pill, Type & Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = match.category.accentColor.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, match.category.accentColor.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = "${match.category.iconEmoji} ${match.category.banglaTitle}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = match.category.accentColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkElevated
                        ) {
                            Text(
                                text = match.matchType.banglaLabel,
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isHyped) FlameYellow.copy(alpha = 0.18f) else DarkCard,
                            border = BorderStroke(
                                1.dp,
                                if (isHyped) FlameYellow else DarkBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    isHyped = !isHyped
                                }
                                .testTag("hype_match_btn_${match.id}")
                        ) {
                            Text(
                                text = "🔥 ${baseHypeCount + if (isHyped) 1 else 0}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHyped) FlameYellow else TextSecondary
                                ),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = match.status.color.copy(alpha = 0.16f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isLiveMatch) {
                                    LiveMatchStatusDot(color = match.status.color)
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                                Text(
                                    text = match.status.banglaLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = match.status.color
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Match Title
                Text(
                    text = match.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = DisplayFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Time & Countdown Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = "Time",
                            tint = FlameYellow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${match.matchDateTime} • ${match.mapName}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        )
                    }

                    val remainingMs = match.remainingMillis(nowMillis)
                    val countdownBadgeColor = when {
                        match.status == MatchStatus.ONGOING -> FireOrange
                        match.status == MatchStatus.ROOM_OPEN -> VictoryGreen
                        match.status == MatchStatus.UPCOMING && remainingMs <= 5 * 60_000L -> FireRed
                        else -> FlameYellow
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = countdownBadgeColor.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, countdownBadgeColor.copy(alpha = 0.55f))
                    ) {
                        Text(
                            text = match.liveCountdownText(nowMillis),
                            fontFamily = MonoFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = countdownBadgeColor,
                            modifier = Modifier
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                .testTag("match_countdown_${match.id}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Prize, Per Kill & Entry Fee Grid
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.EmojiEvents, contentDescription = "Prize", tint = FlameYellow, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("মোট পুরস্কার", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "৳${match.totalPrize}",
                                fontFamily = MonoFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameYellow
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(DarkBorder))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val showPerKill = match.category != GameCategory.LONE_WOLF && match.perKillPrize > 0
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.TrackChanges,
                                    contentDescription = if (showPerKill) "Per kill" else "Win prize",
                                    tint = NeonPink,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (showPerKill) "প্রতি কিল" else "উইন প্রাইজ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (showPerKill) "৳${match.perKillPrize}" else "৳${match.booyahPrize}",
                                fontFamily = MonoFontFamily,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(DarkBorder))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Payments, contentDescription = "Entry Fee", tint = WaterCyan, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("এন্ট্রি ফি", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "৳${match.entryFee}",
                                fontFamily = MonoFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = WaterCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Elite Fire & Water Live Slot Embed Box
                val safeTotal = match.totalSlots.coerceAtLeast(1)
                val progress = (match.bookedSlots.toFloat() / safeTotal).coerceIn(0f, 1f)
                val percent = (progress * 100).toInt()
                val previewSlots = safeTotal.coerceAtMost(14)

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0A1222),
                    border = BorderStroke(
                        1.dp,
                        if (match.isFull) FireRed.copy(alpha = 0.65f) else WaterCyan.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onViewSlotGridClick() }
                        .testTag("slot_embed_box_${match.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🎟️ লাইভ স্লট: ${match.bookedSlots}/${match.totalSlots}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = MonoFontFamily,
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = if (match.isFull) FireRed else EmberGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (match.isFull) FireRed.copy(alpha = 0.18f) else WaterCyan.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "$percent%",
                                        fontFamily = MonoFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (match.isFull) FireRed else WaterCyan,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (match.isFull) "স্লট পূর্ণ!" else "${match.availableSlots} টি স্লট বাকি →",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (match.isFull) FireRed else VictoryGreen
                            )
                        }

                        // Mini Visual Arena Slot Tiles Preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 1..previewSlots) {
                                val tileBooked = i <= match.bookedSlots
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            if (tileBooked) {
                                                Brush.verticalGradient(listOf(FireRed, FireOrange))
                                            } else {
                                                Brush.verticalGradient(listOf(VictoryGreen, WaterCyan.copy(alpha = 0.7f)))
                                            }
                                        )
                                )
                            }
                            if (safeTotal > previewSlots) {
                                Text(
                                    text = "+${safeTotal - previewSlots}",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WaterCyan,
                                    modifier = Modifier.padding(start = 3.dp)
                                )
                            }
                        }

                        // Dual-Element Progress Track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(DarkElevated)
                        ) {
                            if (progress > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            if (match.isFull) {
                                                Brush.horizontalGradient(listOf(FireRed, FireOrange))
                                            } else {
                                                Brush.horizontalGradient(listOf(FireOrange, EmberGold, WaterCyan))
                                            }
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                if (isAlreadyBooked) {
                    Button(
                        onClick = onViewRoomClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("view_room_btn_${match.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen, contentColor = Color.Black),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Key,
                            contentDescription = "Room ID",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (match.status == MatchStatus.ROOM_OPEN) "রুম আইডি ও পাসওয়ার্ড দেখুন" else "আপনার স্লট বুকড (রুম শীঘ্রই আসবে)",
                            fontWeight = FontWeight.Black
                        )
                    }
                } else if (match.isFull) {
                    OutlinedButton(
                        onClick = onViewSlotGridClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Icon(imageVector = Icons.Filled.GridView, contentDescription = "Grid", modifier = Modifier.size(16.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "সব স্লট পূর্ণ (স্লট গ্রিড দেখুন)", color = TextSecondary)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onViewSlotGridClick,
                            modifier = Modifier
                                .weight(0.42f)
                                .height(48.dp)
                                .testTag("slot_grid_btn_${match.id}"),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FlameYellow)
                        ) {
                            Icon(imageVector = Icons.Filled.GridView, contentDescription = "Slots", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "স্লট গ্রিড", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onBookClick,
                            modifier = Modifier
                                .weight(0.58f)
                                .height(48.dp)
                                .testTag("book_slot_btn_${match.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FireRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SportsEsports,
                                contentDescription = "Buy Slot",
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "স্লট কিনুন (৳${match.entryFee})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveMatchStatusDot(color: Color) {
    val liveTransition = rememberInfiniteTransition(label = "match_status_dot")
    val statusDotAlpha by liveTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "dot_alpha"
    )
    Box(
        modifier = Modifier
            .size(6.dp)
            .graphicsLayer { alpha = statusDotAlpha }
            .clip(CircleShape)
            .background(color)
    )
}
