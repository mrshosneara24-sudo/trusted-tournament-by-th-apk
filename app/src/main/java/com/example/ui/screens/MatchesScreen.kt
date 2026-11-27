package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.model.AppCustomization
import com.example.model.GameCategory
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.ui.components.MatchCard
import com.example.ui.components.TournamentHeader
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

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
    appCustomization: AppCustomization = AppCustomization(),
    modifier: Modifier = Modifier
) {
    val bookedMatchIds = remember(myBookings) { myBookings.map { it.matchId }.toSet() }
    var liveNowMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            liveNowMillis = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000L)
        }
    }

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
                            border = BorderStroke(
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

        // Section Title (Immediately visible at the top without mini-game/quest clutter)
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
                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "সুরক্ষিত ওয়ালেট",
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
                val isHeldCategory = selectedCategory == GameCategory.BR_RANK || selectedCategory == GameCategory.CS_RANK
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.SportsEsports,
                            contentDescription = "Empty",
                            tint = if (isHeldCategory) FlameYellow else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isHeldCategory) {
                                "⏳ ${selectedCategory.banglaTitle} এখন হোল্ডে রাখা হয়েছে"
                            } else {
                                "কোন ম্যাচ পাওয়া যায়নি"
                            },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isHeldCategory) FlameYellow else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isHeldCategory) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "আপাতত শুধু Lone Wolf (লোন উলফ) ম্যাচ দিয়ে টুর্নামেন্ট যাত্রা শুরু হয়েছে 🐺। অ্যাডমিন/ওনার প্যানেল থেকে সেভড প্রিসেট দিয়ে যেকোনো সময় চালু করা যাবে!",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { onCategorySelected(GameCategory.LONE_WOLF) },
                                colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("empty_go_lone_wolf_btn")
                            ) {
                                Text(
                                    text = "🐺 Lone Wolf ম্যাচ দেখুন",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
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
                    onViewSlotGridClick = { onViewSlotGridClick(match) },
                    nowMillis = liveNowMillis
                )
            }
        }
    }
}
