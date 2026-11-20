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
import androidx.compose.material.icons.filled.GpsFixed
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.SensitivityProDialog
import com.example.ui.components.TournamentHeader
import com.example.model.AppCustomization
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
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
    appCustomization: AppCustomization = AppCustomization(),
    modifier: Modifier = Modifier
) {
    val bookedMatchIds = remember(myBookings) { myBookings.map { it.matchId }.toSet() }
    var showSensitivityLab by remember { mutableStateOf(false) }

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
                onWalletClick = onWalletClick,
                appCustomization = appCustomization
            )
        }

        // Action Quick Row: Lucky Spin & Daily Streak
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onDailySpinClick() }
                        .testTag("daily_spin_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(FlameYellow.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Spin",
                                tint = FlameYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "লাকি স্পিন 🎡",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = FlameYellow
                            )
                            Text(
                                text = "৳৩-৳৩০ জয়ের সুযোগ",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onDailyStreakClick() }
                        .testTag("daily_streak_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange.copy(alpha = 0.55f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(FlameOrange.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = "Daily Streak",
                                tint = FlameOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "দৈনিক স্ট্রিক 🔥",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "৭ দিনের নগদ বোনাস",
                                fontSize = 10.sp,
                                color = FlameYellow
                            )
                        }
                    }
                }
            }
        }

        // Daily Gamer Missions Strip (now interactive & claimable!)
        if (dailyMissions.isNotEmpty()) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎯 দৈনিক গেমার মিশন",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = FlameYellow
                        )
                        val doneCount = dailyMissions.count { it.isClaimed }
                        Text(
                            text = "$doneCount/${dailyMissions.size} সম্পন্ন",
                            style = MaterialTheme.typography.labelSmall,
                            color = VictoryGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(dailyMissions, key = { it.id }) { mission ->
                            Card(
                                modifier = Modifier
                                    .width(210.dp)
                                    .testTag("mission_card_${mission.id}"),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (mission.isCompleted && !mission.isClaimed) {
                                        Color(0xFF211C0A)
                                    } else {
                                        DarkSurface
                                    }
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when {
                                        mission.isClaimed -> VictoryGreen.copy(alpha = 0.55f)
                                        mission.isCompleted -> FlameYellow
                                        else -> Color.White.copy(alpha = 0.2f)
                                    }
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${mission.iconEmoji} ${mission.title}",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BkashPink.copy(alpha = 0.25f)
                                        ) {
                                            Text(
                                                text = "+৳${mission.rewardAmount}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = FlameYellow,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = mission.description,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        lineHeight = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            if (mission.isCompleted && !mission.isClaimed) {
                                                onClaimMission(mission.id)
                                            } else if (!mission.isCompleted) {
                                                when (mission.actionType) {
                                                    "SPIN" -> onDailySpinClick()
                                                    "STREAK" -> onDailyStreakClick()
                                                    "AI_CHAT" -> onSupportChatClick()
                                                }
                                            }
                                        },
                                        enabled = !mission.isClaimed,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(34.dp)
                                            .testTag("claim_mission_${mission.id}"),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = when {
                                                mission.isClaimed -> DarkCard
                                                mission.isCompleted -> VictoryGreen
                                                else -> BkashPink.copy(alpha = 0.7f)
                                            }
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text(
                                            text = when {
                                                mission.isClaimed -> "সংগ্রহ হয়েছে ✓"
                                                mission.isCompleted -> "ক্লেইম +৳${mission.rewardAmount}"
                                                else -> "মিশন শুরু করুন"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pro Sensitivity Lab & 24/7 AI Help Cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showSensitivityLab = true }
                        .testTag("sensitivity_lab_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(FlameYellow.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GpsFixed,
                                contentDescription = "Sensitivity Pro Lab",
                                tint = FlameYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🎯 FF প্রো হেডশট সেনসিটিভিটি ল্যাব",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = FlameYellow
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(5.dp), color = DarkElevated) {
                                    Text(
                                        text = "PRO",
                                        fontFamily = MonoFontFamily,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "ফোনের RAM ও খেলার স্টাইল অনুযায়ী সেরা One-Tap সেটিংস কপি করুন",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSupportChatClick() }
                        .testTag("support_chat_card"),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameCyan.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(FlameCyan.copy(alpha = 0.14f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SmartToy,
                                contentDescription = "Talk with AI",
                                tint = FlameCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🤖 Talk with AI (এআই সহকারী)",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "24/7 লাইভ",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                    color = VictoryGreen
                                )
                            }
                            Text(
                                text = "ডিপোজিট, ক্যাশআউট বা রুম কোড সংক্রান্ত যেকোনো সমস্যায় তাৎক্ষণিক সাহায্য নিন",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                placeholder = {
                    Text(
                        "ম্যাচ খুঁজুন (যেমন: লোন উলফ, বারমুডা)",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = FlameYellow
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("match_search_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = DarkCard,
                    unfocusedContainerColor = DarkCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder
                ),
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Category Horizontal Bar
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "টুর্নামেন্ট ক্যাটাগরি",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(GameCategory.entries, key = { it.name }) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) FlameYellow else DarkBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onCategorySelected(cat) }
                                .testTag("category_tab_${cat.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
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
                                        color = if (isSelected) Color.White else TextSecondary
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
                        color = TextPrimary
                    )
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "বিকাশ: ${appCustomization.officialBkashNumber}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = FlameYellow
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
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

    if (showSensitivityLab) {
        SensitivityProDialog(onDismiss = { showSensitivityLab = false })
    }
}
