package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.model.DailyGamerMission
import com.example.model.GameCategory
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.ui.components.MatchCard
import com.example.ui.components.TournamentHeader
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun MatchesScreen(
    matches: List<TournamentMatch>,
    myBookings: List<SlotBooking>,
    selectedCategory: GameCategory,
    searchQuery: String,
    walletBalance: Int,
    onCategorySelected: (GameCategory) -> Unit,
    onSearchChanged: (String) -> Unit,
    onWalletClick: () -> Unit,
    onBookSlotClick: (TournamentMatch) -> Unit,
    onViewRoomClick: (TournamentMatch) -> Unit,
    onViewSlotGridClick: (TournamentMatch) -> Unit,
    onDailySpinClick: () -> Unit,
    onSupportChatClick: () -> Unit,
    onDailyStreakClick: () -> Unit = {},
    dailyMissions: List<DailyGamerMission> = emptyList(),
    onClaimMission: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val bookedMatchIds = myBookings.map { it.matchId }.toSet()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("matches_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        item {
            TournamentHeader(
                walletBalance = walletBalance,
                onWalletClick = onWalletClick
            )
        }

        // Action Quick Row: Lucky Spin & Daily Streak
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Lucky Spin card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onDailySpinClick() }
                        .testTag("daily_spin_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2C1E07))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Spin",
                            tint = FlameYellow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "লাকি স্পিন 🎡",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlameYellow
                            )
                            Text(
                                text = "৩-৫৳ নিশ্চিত (জ্যাকপট ৩০৳)",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                }

                // Daily Streak card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onDailyStreakClick() }
                        .testTag("daily_streak_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2E1308)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = "Daily Streak",
                            tint = FlameOrange,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "দৈনিক স্ট্রিক 🔥",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlameOrange
                            )
                            Text(
                                text = "৭ দিনের নগদ বোনাস",
                                fontSize = 10.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 24/7 AI & Support Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSupportChatClick() }
                    .testTag("support_chat_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF132032)),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameCyan.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(FlameCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = "Talk with AI",
                            tint = FlameCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🤖 Talk with AI (এআই সহকারী)",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlameCyan
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "24/7 লাইভ",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                                color = FlameYellow
                            )
                        }
                        Text(
                            text = "ডিপোজিট, ক্যাশআউট বা রুম কোড সংক্রান্ত যেকোনো সমস্যায় তাৎক্ষণিক সাহায্য নিন",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                placeholder = { Text("ম্যাচ খুঁজুন (যেমন: লোন উলফ, বারমুডা)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("match_search_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = DarkBorder
                ),
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Category Horizontal Bar
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "টুর্নামেন্ট ক্যাটাগরি:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(GameCategory.values()) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) FlameYellow else DarkBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onCategorySelected(cat) }
                                .testTag("category_tab_${cat.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cat.iconEmoji,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = cat.banglaTitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "উপলব্ধ টুর্নামেন্ট (${matches.size} টি)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    text = "বিকাশ নম্বর: 01861583924",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = FlameYellow
                    )
                )
            }
        }

        // Match Items
        if (matches.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.SportsEsports,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "কোন ম্যাচ পাওয়া যায়নি",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(matches, key = { it.id }) { match ->
                MatchCard(
                    match = match,
                    isAlreadyBooked = bookedMatchIds.contains(match.id),
                    onBookClick = { onBookSlotClick(match) },
                    onViewRoomClick = { onViewRoomClick(match) },
                    onViewSlotGridClick = { onViewSlotGridClick(match) }
                )
            }
        }
    }
}
