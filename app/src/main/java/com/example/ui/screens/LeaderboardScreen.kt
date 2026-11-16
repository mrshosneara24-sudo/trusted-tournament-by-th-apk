package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Forum
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
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.model.AppUser
import com.example.model.BadgeAppeal
import com.example.model.BadgeType
import com.example.model.LeaderboardPlayer
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.BadgeEmblemArt
import com.example.ui.components.BadgePill
import com.example.ui.components.BadgeShowcaseDialog
import com.example.ui.components.PlayerProfileCardData
import com.example.ui.components.PlayerProfileCardDialog
import com.example.ui.components.ProfileAvatarImage
import com.example.ui.components.toPlayerProfileCard
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen

@Composable
fun LeaderboardScreen(
    players: List<LeaderboardPlayer>,
    modifier: Modifier = Modifier,
    currentBadges: List<BadgeType> = emptyList(),
    registeredUsers: List<AppUser> = emptyList(),
    currentUser: UserProfile = UserProfile(),
    onPlayerProfileClick: ((PlayerProfileCardData) -> Unit)? = null,
    onGiveKudos: ((String) -> Int)? = null,
    onOpenGlobalChat: (() -> Unit)? = null,
    badgeAppeals: List<BadgeAppeal> = emptyList(),
    onSubmitBadgeAppeal: ((BadgeType, String) -> Pair<Boolean, String>)? = null,
    onSendAppealChat: ((String, String, String?) -> Pair<Boolean, String>)? = null
) {
    var showBadgeDialog by remember { mutableStateOf(false) }
    var localPlayerProfileCard by remember { mutableStateOf<PlayerProfileCardData?>(null) }

    val openProfile: (PlayerProfileCardData) -> Unit = { data ->
        if (onPlayerProfileClick != null) {
            onPlayerProfileClick(data)
        } else {
            localPlayerProfileCard = data
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("leaderboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = FlameYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "চ্যাম্পিয়ন লিডারবোর্ড",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (onOpenGlobalChat != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = FlameCyan.copy(alpha = 0.16f),
                            border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onOpenGlobalChat() }
                                .testTag("leaderboard_open_global_chat_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Forum,
                                    contentDescription = "Open Global Chat",
                                    tint = FlameCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "গ্লোবাল চ্যাট",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameCyan
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "বাংলাদেশের শীর্ষ ফ্রি ফায়ার টুর্নামেন্ট প্লেয়ার ও উইনিং তালিকা",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1A1408), DarkCard)
                                )
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            BadgeEmblemArt(badge = BadgeType.V_BADGE, size = 34.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            BadgeEmblemArt(badge = BadgeType.PRIME_SPENDER, size = 34.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "অফিসিয়াল ব্যাজ গ্যালারি (৯টি ব্যাজ)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "ভি-ব্যাজ • প্রো • প্রাইম স্পেন্ডার • অ্যাডমিন • ওনার",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FlameYellow,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        if (players.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .testTag("leaderboard_empty_state"),
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.4f)),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(DarkElevated)
                                .border(1.dp, FlameYellow.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Empty Leaderboard",
                                tint = FlameYellow,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "লিডারবোর্ড এখনো ফাঁকা!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "টুর্নামেন্ট ম্যাচ শুরু হলে খেলোয়াড়দের কিল, বুইয়া এবং মোট প্রাইজ অনুযায়ী শীর্ষ র‍্যাঙ্কিং এখানে প্রদর্শিত হবে।",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            lineHeight = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            // Top 1 Champion Spotlight Card
            item {
                val topPlayer = players.firstOrNull()
                if (topPlayer != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                openProfile(topPlayer.toPlayerProfileCard(registeredUsers, currentUser))
                            }
                            .testTag("leaderboard_top_player_card"),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.5.dp, NeonPink.copy(alpha = 0.65f)),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF330A22),
                                            Color(0xFF140A19)
                                        )
                                    )
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .background(GoldChampagne),
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
            items(players, key = { "${it.rank}_${it.uid}_${it.ign}" }) { player ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        openProfile(player.toPlayerProfileCard(registeredUsers, currentUser))
                    }
                    .testTag("leaderboard_player_row_${player.uid}"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (player.ign.contains("আপনি")) NeonPink.copy(alpha = 0.6f) else DarkBorder),
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
                                1 -> GoldChampagne
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

        // Community Players Profile Directory (Always accessible!)
        if (registeredUsers.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .testTag("leaderboard_community_directory"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Groups,
                                contentDescription = null,
                                tint = FlameCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "কমিউনিটি প্লেয়ার প্রোফাইল",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = Color.White
                                )
                                Text(
                                    text = "যেকোনো প্লেয়ারে ক্লিক করে প্লেয়ারের প্রোফাইল কার্ড দেখুন",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FlameCyan
                                )
                            }
                        }
                    }

                    registeredUsers.forEach { member ->
                        val isSelf = (currentUser.phone.isNotBlank() && member.phone == currentUser.phone) ||
                            (currentUser.uid.isNotBlank() && member.uid == currentUser.uid)
                        val primaryBadge = member.primaryBadge

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    openProfile(member.toPlayerProfileCard(isSelf = isSelf))
                                }
                                .testTag("directory_player_card_${member.uid}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCard),
                            border = BorderStroke(
                                1.dp,
                                if (member.role == UserRole.OWNER) FlameYellow.copy(alpha = 0.65f) else DarkBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(48.dp)) {
                                    ProfileAvatarImage(
                                        avatarId = member.avatarId,
                                        customPfpUri = member.customPfpUri,
                                        role = member.role,
                                        contentDescription = member.ign,
                                        showGifBadge = true,
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .border(
                                                1.5.dp,
                                                if (member.role == UserRole.OWNER) FlameYellow else FlameCyan,
                                                CircleShape
                                            )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(13.dp)
                                            .clip(CircleShape)
                                            .background(DarkCard)
                                            .padding(2.dp)
                                            .clip(CircleShape)
                                            .background(if (member.isBanned) Color(0xFFEF4444) else Color(0xFF23A55A))
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = member.ign,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        BadgePill(
                                            badge = primaryBadge,
                                            compact = true,
                                            animateEntrance = true
                                        )
                                    }
                                    Text(
                                        text = member.battleTagline,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "🎮 ${member.matchesPlayed} ম্যাচ • 💸 ৳${member.totalSpent} খরচ • 📅 ${member.joinedDate}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FlameYellow,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = "View Profile",
                                    tint = FlameCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBadgeDialog) {
        BadgeShowcaseDialog(
            currentBadges = currentBadges,
            onDismiss = { showBadgeDialog = false },
            userProfile = if (currentUser.isLoggedIn) currentUser else null,
            badgeAppeals = badgeAppeals,
            onSubmitBadgeAppeal = onSubmitBadgeAppeal,
            onSendAppealChat = onSendAppealChat
        )
    }

    localPlayerProfileCard?.let { profileData ->
        PlayerProfileCardDialog(
            profile = profileData,
            onDismiss = { localPlayerProfileCard = null },
            onGiveKudos = onGiveKudos,
            onViewAllBadges = {
                localPlayerProfileCard = null
                showBadgeDialog = true
            }
        )
    }
}
