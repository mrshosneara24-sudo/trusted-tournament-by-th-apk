package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.model.BadgeType
import com.example.model.LeaderboardPlayer
import com.example.ui.components.BadgePill
import com.example.ui.components.BadgeShowcaseDialog
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun LeaderboardScreen(
    players: List<LeaderboardPlayer>,
    modifier: Modifier = Modifier
) {
    var showBadgeDialog by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("leaderboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = FlameYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "চ্যাম্পিয়ন লিডারবোর্ড",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Text(
                    text = "বাংলাদেশের শীর্ষ ফ্রি ফায়ার টুর্নামেন্ট প্লেয়ার ও উইনিং তালিকা",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Official Badges Guide Quick Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showBadgeDialog = true }
                        .testTag("leaderboard_badge_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Ⓥ", fontSize = 16.sp, color = FlameYellow, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "অফিসিয়াল ব্যাজ সিস্টেম (ভি-ব্যাজ, ওনার, প্রো)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "টাকা লাগালে V-Badge • বেশি খেললে PRO Badge",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FlameYellow,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        // Top 1 Champion Spotlight Card
        item {
            val topPlayer = players.firstOrNull()
            if (topPlayer != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(FlameYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "১",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = topPlayer.ign,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "👑", fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "UID: ${topPlayer.uid}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    BadgePill(
                                        badge = topPlayer.primaryBadge,
                                        compact = true,
                                        onClick = { showBadgeDialog = true }
                                    )
                                }
                                Text(
                                    text = "মোট কিল: ${topPlayer.totalKills} | বুইয়া: ${topPlayer.booyahs}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "মোট ইনকাম",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "৳${topPlayer.totalEarnings}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = VictoryGreen
                                )
                            )
                        }
                    }
                }
            }
        }

        // Leaderboard List
        items(players) { player ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (player.ign.contains("আপনি")) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else DarkSurface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = when (player.rank) {
                                1 -> FlameYellow
                                2 -> Color(0xFFC0C0C0)
                                3 -> Color(0xFFCD7F32)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${player.rank}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (player.rank <= 3) Color.Black else Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = player.ign,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "কিল: ${player.totalKills} • বুইয়া: ${player.booyahs} বার",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "৳${player.totalEarnings}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = VictoryGreen
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        BadgePill(
                            badge = player.primaryBadge,
                            compact = true,
                            onClick = { showBadgeDialog = true }
                        )
                    }
                }
            }
        }
    }

    if (showBadgeDialog) {
        BadgeShowcaseDialog(
            currentBadges = listOf(BadgeType.V_BADGE, BadgeType.GREEN_VERIFIED, BadgeType.PRO_PLAYER),
            onDismiss = { showBadgeDialog = false }
        )
    }
}
