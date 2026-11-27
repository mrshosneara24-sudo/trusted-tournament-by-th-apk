package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BonusGiftDrop
import com.example.model.DailyTriviaProgress
import com.example.model.GiveawayEvent
import com.example.model.GiveawayStatus
import com.example.model.LiveCommunityPoll
import com.example.model.ReflexGameStats
import com.example.ui.components.DailyTriviaChallengeCard
import com.example.ui.components.LiveBonusDropBannerCard
import com.example.ui.components.LiveCommunityPollCard
import com.example.ui.components.LiveMegaGiveawayCard
import com.example.ui.components.ReflexSpeedEntryCard
import com.example.ui.components.ReflexTapChallengeDialog
import com.example.ui.components.SensitivityProDialog
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FireWaterBorderGradient
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue

@Composable
fun FunAndGiveawayScreen(
    onDailySpinClick: () -> Unit,
    onDailyStreakClick: () -> Unit,
    onSupportChatClick: () -> Unit,
    bonusDrops: List<BonusGiftDrop> = emptyList(),
    communityPolls: List<LiveCommunityPoll> = emptyList(),
    triviaProgress: DailyTriviaProgress = DailyTriviaProgress(),
    currentActorKeys: Set<String> = emptySet(),
    onClaimBonusDrop: (String) -> Pair<Boolean, String> = { false to "" },
    onVoteCommunityPoll: (String, String) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onAnswerTrivia: (Int) -> Pair<Boolean, String> = { false to "" },
    giveaways: List<GiveawayEvent> = emptyList(),
    reflexStats: ReflexGameStats = ReflexGameStats(),
    onJoinGiveaway: (String) -> Pair<Boolean, String> = { false to "" },
    onRecordReflexResult: (Int, Int, Int) -> ReflexGameStats = { _, _, _ -> reflexStats },
    modifier: Modifier = Modifier
) {
    var showSensitivityLab by remember { mutableStateOf(false) }
    var showReflexChallenge by remember { mutableStateOf(false) }

    val visibleGiveaways = remember(giveaways) {
        giveaways.filter { it.status == GiveawayStatus.LIVE || it.status == GiveawayStatus.DRAWN }
    }
    val activeDrops = remember(bonusDrops) { bonusDrops.filter { it.active } }
    val activePolls = remember(communityPolls) { communityPolls.filter { it.active } }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("fun_giveaway_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Hub Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.2.dp, FireWaterBorderGradient),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1F1209),
                                    DarkCard,
                                    WaterBlue.copy(alpha = 0.2f)
                                )
                            )
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(FlameYellow.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = "Fun and Giveaway Hub",
                            tint = FlameYellow,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ফান জোন ও মেগা গিভওয়ে",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = FlameYellow
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "লাকি স্পিন, ডেইলি স্ট্রিক, রিফ্লেক্স গেম, গিভওয়ে ও প্রো সেনসিটিভিটি সব এখানে।",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Quick Action Row: Lucky Spin & Daily Streak
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
                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.5f))
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
                                text = "লাকি স্পিন",
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
                    border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.55f))
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
                                text = "দৈনিক স্ট্রিক",
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

        // Live Mega Giveaways & Lottery Draw Events
        if (visibleGiveaways.isNotEmpty()) {
            items(visibleGiveaways, key = { "gw_${it.id}" }) { gw ->
                LiveMegaGiveawayCard(
                    giveaway = gw,
                    currentActorKeys = currentActorKeys,
                    onJoinGiveaway = onJoinGiveaway
                )
            }
        } else {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = "No active giveaway",
                            tint = FlameCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "নতুন মেগা গিভওয়ে শীঘ্রই আসছে",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "ওনার বা অ্যাডমিন গিভওয়ে চালু করলেই এখান থেকে ফ্রি অংশ নিতে পারবেন।",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // FF One-Tap Reflex Speed Lab Mini-Game
        item {
            ReflexSpeedEntryCard(
                stats = reflexStats,
                onPlayClick = { showReflexChallenge = true }
            )
        }

        // Live Bonus Gift Drops
        if (activeDrops.isNotEmpty()) {
            items(activeDrops, key = { "drop_${it.id}" }) { drop ->
                val hasClaimed = currentActorKeys.any { it in drop.claimedByKeys }
                LiveBonusDropBannerCard(
                    drop = drop,
                    hasClaimed = hasClaimed,
                    onClaimClick = onClaimBonusDrop
                )
            }
        }

        // Live Community Polls
        if (activePolls.isNotEmpty()) {
            items(activePolls, key = { "poll_${it.id}" }) { poll ->
                val hasVoted = currentActorKeys.any { it in poll.votedByKeys }
                LiveCommunityPollCard(
                    poll = poll,
                    hasVoted = hasVoted,
                    onVote = onVoteCommunityPoll
                )
            }
        }

        // Daily Free Fire Trivia Challenge
        item {
            DailyTriviaChallengeCard(
                progress = triviaProgress,
                onAnswerQuestion = onAnswerTrivia
            )
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
                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.4f))
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
                                    text = "FF প্রো হেডশট সেনসিটিভিটি ল্যাব",
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
                    border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.35f))
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
                                    text = "Talk with AI (এআই সহকারী)",
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
    }

    if (showSensitivityLab) {
        SensitivityProDialog(onDismiss = { showSensitivityLab = false })
    }

    if (showReflexChallenge) {
        ReflexTapChallengeDialog(
            stats = reflexStats,
            onRecordResult = onRecordReflexResult,
            onDismiss = { showReflexChallenge = false }
        )
    }
}
