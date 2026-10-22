package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DailyStreakInfo
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun DailyStreakDialog(
    streakInfo: DailyStreakInfo,
    onClaimReward: () -> Int,
    onDismiss: () -> Unit
) {
    var justClaimedAmount by remember { mutableStateOf<Int?>(null) }

    val currentDay = streakInfo.currentStreakDay.coerceIn(1, 7)
    val todayReward = streakInfo.rewards.getOrElse(currentDay - 1) { 1 }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, FlameOrange, RoundedCornerShape(24.dp))
                .testTag("daily_streak_dialog"),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(FlameOrange, FlameYellow))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = "Fire Streak",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "দৈনিক লগইন স্ট্রিক 🔥",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "প্রতিদিন লগইন করে ফ্রি টাকা জিতুন",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Streak Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FlameOrange.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "স্ট্রিক প্রগ্রেস: দিন $currentDay / ৭",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlameYellow
                            )
                            Text(
                                text = if (streakInfo.isTodayClaimed) "আজকের বোনাস নেওয়া হয়েছে!" else "আজকের ৳$todayReward বোনাস ক্লেইম করুন",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (streakInfo.isTodayClaimed) VictoryGreen.copy(alpha = 0.25f) else FlameYellow.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = if (streakInfo.isTodayClaimed) "✔ কমপ্লিট" else "🎁 ওপেন",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (streakInfo.isTodayClaimed) VictoryGreen else FlameYellow
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 7-Day Rewards Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(streakInfo.rewards) { index, reward ->
                        val dayNumber = index + 1
                        val isDayClaimed = streakInfo.streakHistoryDays.contains(dayNumber)
                        val isDayCurrent = dayNumber == currentDay
                        val isDayLocked = dayNumber > currentDay

                        val cardBg = when {
                            isDayClaimed -> VictoryGreen.copy(alpha = 0.15f)
                            isDayCurrent && !streakInfo.isTodayClaimed -> FlameYellow.copy(alpha = 0.2f)
                            isDayCurrent && streakInfo.isTodayClaimed -> VictoryGreen.copy(alpha = 0.15f)
                            else -> DarkCard
                        }

                        val borderColor = when {
                            isDayClaimed -> VictoryGreen.copy(alpha = 0.6f)
                            isDayCurrent && !streakInfo.isTodayClaimed -> FlameYellow
                            else -> DarkBorder
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = cardBg,
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (dayNumber == 7) "৭ম দিন ★" else "দিন $dayNumber",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (dayNumber == 7) FlameYellow else Color.White.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                if (dayNumber == 7) {
                                    Icon(
                                        imageVector = Icons.Filled.WorkspacePremium,
                                        contentDescription = "Day 7",
                                        tint = FlameYellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else if (isDayClaimed) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = "Claimed",
                                        tint = VictoryGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else if (isDayCurrent && !streakInfo.isTodayClaimed) {
                                    Icon(
                                        imageVector = Icons.Filled.EmojiEvents,
                                        contentDescription = "Today",
                                        tint = FlameYellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "Locked",
                                        tint = Color.White.copy(alpha = 0.35f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "৳$reward",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (dayNumber == 7) FlameYellow else Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Just claimed success banner
                AnimatedVisibility(
                    visible = justClaimedAmount != null,
                    enter = fadeIn(tween(300)) + scaleIn(tween(300))
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VictoryGreen.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, VictoryGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VictoryGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🎉 অভিনন্দন! ৳${justClaimedAmount} আপনার ওয়ালেটে যোগ হয়েছে!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VictoryGreen
                            )
                        }
                    }
                }

                // Claim Button
                Button(
                    onClick = {
                        if (!streakInfo.isTodayClaimed) {
                            val reward = onClaimReward()
                            justClaimedAmount = reward
                        }
                    },
                    enabled = !streakInfo.isTodayClaimed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("claim_daily_streak_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlameOrange,
                        disabledContainerColor = DarkCard
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (!streakInfo.isTodayClaimed) Icons.Filled.LocalFireDepartment else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (!streakInfo.isTodayClaimed) Color.White else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (!streakInfo.isTodayClaimed) "আজকের ৳$todayReward রিওয়ার্ড ক্লেইম করুন" else "আজকের রিওয়ার্ড নেওয়া হয়েছে (আগামীকাল আসুন)",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = if (!streakInfo.isTodayClaimed) Color.White else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
