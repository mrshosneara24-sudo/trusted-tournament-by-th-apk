package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppUser
import com.example.model.BadgeAppeal
import com.example.model.BadgeType
import com.example.model.ChatMessage
import com.example.model.GlobalChatMessage
import com.example.model.LeaderboardPlayer
import com.example.model.ProfileBannerColor
import com.example.model.SlotBooking
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.FireRed
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import java.util.Locale

data class PlayerProfileCardData(
    val userId: String = "",
    val accountName: String,
    val ign: String,
    val uid: String,
    val avatarId: String = "th_logo",
    val customPfpUri: String? = null,
    val customBannerUri: String? = null,
    val bannerColorId: String = ProfileBannerColor.DEFAULT_CRIMSON.id,
    val role: UserRole = UserRole.PLAYER,
    val joinedDate: String = "আজ",
    val bio: String = "নতুন চ্যালেঞ্জার! 🎯",
    val matchesPlayed: Int = 0,
    val totalSpent: Int = 0,
    val totalWins: Int = 0,
    val totalKills: Int = 0,
    val totalEarnings: Int = 0,
    val badges: List<BadgeType> = emptyList(),
    val favoriteWeapon: String = "MP40",
    val activeCharacter: String = "Alok",
    val guildName: String = "TRUSTED TH",
    val playerRole: String = "সোলো প্লেয়ার",
    val level: Int = 1,
    val kudosCount: Int = 0,
    val isBanned: Boolean = false,
    val isOnline: Boolean = true,
    val isSelf: Boolean = false
)

fun AppUser.toPlayerProfileCard(
    isSelf: Boolean = false,
    isOnline: Boolean = true
): PlayerProfileCardData = PlayerProfileCardData(
    userId = id,
    accountName = name,
    ign = ign,
    uid = uid,
    avatarId = avatarId,
    customPfpUri = customPfpUri,
    customBannerUri = customBannerUri,
    bannerColorId = bannerColorId,
    role = role,
    joinedDate = joinedDate,
    bio = battleTagline.ifBlank { "TRUSTED TOURNAMENT গেমার 🎯" },
    matchesPlayed = matchesPlayed,
    totalSpent = totalSpent,
    totalWins = totalWins,
    totalKills = totalKills,
    totalEarnings = totalEarnings,
    badges = effectiveBadges,
    favoriteWeapon = favoriteWeapon,
    activeCharacter = activeCharacter,
    guildName = guildName,
    playerRole = playerRole,
    level = level,
    kudosCount = kudosCount,
    isBanned = isBanned,
    isOnline = isOnline && !isBanned,
    isSelf = isSelf
)

fun UserProfile.toPlayerProfileCard(kudosCount: Int = 0): PlayerProfileCardData = PlayerProfileCardData(
    userId = phone.ifBlank { uid },
    accountName = accountName.ifBlank { "টুর্নামেন্ট প্লেয়ার" },
    ign = ign.ifBlank { "Player" },
    uid = uid.ifBlank { "N/A" },
    avatarId = avatarId,
    customPfpUri = customPfpUri,
    customBannerUri = customBannerUri,
    bannerColorId = bannerColorId,
    role = role,
    joinedDate = joinedDate.ifBlank { "আজ" },
    bio = battleTagline.ifBlank { "নতুন চ্যালেঞ্জার! 🎯" },
    matchesPlayed = matchesPlayed,
    totalSpent = totalSpent,
    totalWins = totalWins,
    totalKills = totalKills,
    totalEarnings = totalEarnings,
    badges = earnedBadges,
    favoriteWeapon = favoriteWeapon,
    activeCharacter = activeCharacter,
    guildName = guildName,
    playerRole = playerRole,
    level = level,
    kudosCount = kudosCount,
    isBanned = isBanned,
    isOnline = !isBanned,
    isSelf = true
)

fun LeaderboardPlayer.toPlayerProfileCard(
    registeredUsers: List<AppUser>,
    currentUser: UserProfile
): PlayerProfileCardData {
    val cleanIgn = ign.replace(" (আপনি)", "").trim()
    val matchedUser = registeredUsers.find {
        (uid.isNotBlank() && it.uid == uid) ||
            it.ign.equals(cleanIgn, ignoreCase = true)
    }
    if (matchedUser != null) {
        val isSelf = (currentUser.uid.isNotBlank() && matchedUser.uid == currentUser.uid) ||
            (currentUser.phone.isNotBlank() && matchedUser.phone == currentUser.phone)
        return matchedUser.toPlayerProfileCard(isSelf = isSelf).copy(
            totalKills = maxOf(matchedUser.totalKills, totalKills),
            totalWins = maxOf(matchedUser.totalWins, booyahs),
            totalEarnings = maxOf(matchedUser.totalEarnings, totalEarnings),
            totalSpent = maxOf(matchedUser.totalSpent, totalSpent),
            matchesPlayed = maxOf(matchedUser.matchesPlayed, matchesPlayed),
            badges = (matchedUser.effectiveBadges + primaryBadge).distinct()
        )
    }
    val isSelf = (currentUser.uid.isNotBlank() && currentUser.uid == uid) || ign.contains("আপনি")
    if (isSelf) {
        return currentUser.toPlayerProfileCard()
    }
    return PlayerProfileCardData(
        userId = uid,
        accountName = cleanIgn,
        ign = cleanIgn,
        uid = uid,
        avatarId = "avatar_cyber",
        role = UserRole.PLAYER,
        joinedDate = "অক্টোবর ২০২৬",
        bio = "র‍্যাঙ্ক #$rank লিডারবোর্ড চ্যাম্পিয়ন যোদ্ধা 🏆",
        matchesPlayed = matchesPlayed,
        totalSpent = totalSpent,
        totalWins = booyahs,
        totalKills = totalKills,
        totalEarnings = totalEarnings,
        badges = listOf(primaryBadge),
        level = (matchesPlayed + 5).coerceAtLeast(1)
    )
}

fun ChatMessage.toPlayerProfileCard(
    registeredUsers: List<AppUser>,
    currentUser: UserProfile
): PlayerProfileCardData {
    val matchedUser = if (isFromAdmin) {
        registeredUsers.firstOrNull { it.role == UserRole.OWNER || it.role == UserRole.ADMIN }
    } else {
        registeredUsers.firstOrNull {
            it.ign.equals(senderName, ignoreCase = true) ||
                it.name.equals(senderName, ignoreCase = true)
        }
    }
    if (matchedUser != null) {
        val isSelf = (currentUser.phone.isNotBlank() && matchedUser.phone == currentUser.phone) ||
            (currentUser.uid.isNotBlank() && matchedUser.uid == currentUser.uid)
        return matchedUser.toPlayerProfileCard(isSelf = isSelf)
    }
    return if (!isFromAdmin && currentUser.ign.equals(senderName, ignoreCase = true)) {
        currentUser.toPlayerProfileCard()
    } else {
        PlayerProfileCardData(
            userId = id,
            accountName = senderName,
            ign = senderName,
            uid = if (isFromAdmin) "STAFF-TH" else "N/A",
            avatarId = if (isFromAdmin) "th_logo" else "avatar_wolf",
            role = if (isFromAdmin) UserRole.ADMIN else UserRole.PLAYER,
            joinedDate = "অক্টোবর ২০২৬",
            bio = if (isFromAdmin) "TRUSTED TOURNAMENT অফিসিয়াল সাপোর্ট টিম 🛡️" else "সক্রিয় টুর্নামেন্ট খেলোয়াড় 🎮",
            badges = if (isFromAdmin) listOf(BadgeType.MODERATOR) else emptyList()
        )
    }
}

fun SlotBooking.toPlayerProfileCard(
    registeredUsers: List<AppUser>,
    currentUser: UserProfile
): PlayerProfileCardData {
    val matchedUser = registeredUsers.firstOrNull {
        (playerUid.isNotBlank() && it.uid == playerUid) ||
            it.ign.equals(playerIgn, ignoreCase = true)
    }
    if (matchedUser != null) {
        val isSelf = (currentUser.phone.isNotBlank() && matchedUser.phone == currentUser.phone) ||
            (currentUser.uid.isNotBlank() && matchedUser.uid == currentUser.uid)
        return matchedUser.toPlayerProfileCard(isSelf = isSelf)
    }
    if (currentUser.uid == playerUid || currentUser.ign.equals(playerIgn, ignoreCase = true)) {
        return currentUser.toPlayerProfileCard()
    }
    return PlayerProfileCardData(
        userId = bookingId,
        accountName = playerIgn,
        ign = playerIgn,
        uid = playerUid,
        avatarId = "avatar_wolf",
        role = UserRole.PLAYER,
        joinedDate = "আজ",
        bio = "স্লট #$slotNumber • ${matchTitle}",
        matchesPlayed = 1,
        totalSpent = feePaid
    )
}

fun GlobalChatMessage.toPlayerProfileCard(
    registeredUsers: List<AppUser>,
    currentUser: UserProfile
): PlayerProfileCardData {
    val matchedUser = registeredUsers.firstOrNull {
        (senderPhone.isNotBlank() && it.phone == senderPhone) ||
            (senderUid.isNotBlank() && senderUid != "N/A" && it.uid == senderUid) ||
            it.ign.equals(senderIgn, ignoreCase = true)
    }
    if (matchedUser != null) {
        val isSelf = (currentUser.phone.isNotBlank() && matchedUser.phone == currentUser.phone) ||
            (currentUser.uid.isNotBlank() && matchedUser.uid == currentUser.uid)
        return matchedUser.toPlayerProfileCard(isSelf = isSelf)
    }
    val isSelf = (currentUser.phone.isNotBlank() && currentUser.phone == senderPhone) ||
        (currentUser.uid.isNotBlank() && currentUser.uid == senderUid)
    if (isSelf) {
        return currentUser.toPlayerProfileCard()
    }
    return PlayerProfileCardData(
        userId = senderUserId.ifBlank { id },
        accountName = senderName.ifBlank { senderIgn },
        ign = senderIgn,
        uid = senderUid.ifBlank { "N/A" },
        avatarId = senderAvatarId,
        customPfpUri = senderCustomPfpUri,
        role = senderRole,
        joinedDate = "অক্টোবর ২০২৬",
        bio = when (senderRole) {
            UserRole.OWNER -> "TRUSTED TOURNAMENT BY TH অফিসিয়াল ওনার 👑"
            UserRole.ADMIN -> "অফিসিয়াল টুর্নামেন্ট অ্যাডমিন 🛡️"
            UserRole.PLAYER -> "গ্লোবাল কমিউনিটি মেম্বার 🎮"
        },
        badges = senderBadges.ifEmpty { listOf(senderPrimaryBadge) }
    )
}

fun hasActorGivenKudos(
    profile: PlayerProfileCardData,
    registeredUsers: List<AppUser>,
    currentUser: UserProfile
): Boolean {
    val actorMatched = registeredUsers.firstOrNull {
        (currentUser.phone.isNotBlank() && it.phone == currentUser.phone) ||
            (currentUser.uid.isNotBlank() && it.uid == currentUser.uid) ||
            (currentUser.ign.isNotBlank() && it.ign.equals(currentUser.ign, ignoreCase = true))
    }
    val actorKeys = setOfNotNull(
        actorMatched?.id?.takeIf { it.isNotBlank() },
        currentUser.phone.takeIf { it.isNotBlank() },
        currentUser.uid.takeIf { it.isNotBlank() },
        currentUser.ign.takeIf { it.isNotBlank() }
    )
    if (actorKeys.isEmpty()) return false
    val targetUser = registeredUsers.firstOrNull { u ->
        (profile.userId.isNotBlank() && (u.id == profile.userId || u.phone == profile.userId)) ||
            (profile.uid.isNotBlank() && profile.uid != "N/A" && u.uid == profile.uid) ||
            u.ign.equals(profile.ign, ignoreCase = true)
    } ?: return false
    return actorKeys.any { it in targetUser.kudosGiverKeys }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayerProfileCardDialog(
    profile: PlayerProfileCardData,
    onDismiss: () -> Unit,
    onEditOwnProfile: (() -> Unit)? = null,
    onGiveKudos: ((String) -> Int)? = null,
    hasGivenKudos: Boolean = false,
    onViewAllBadges: (() -> Unit)? = null,
    userProfileForAppeal: UserProfile? = null,
    badgeAppeals: List<BadgeAppeal> = emptyList(),
    onSubmitBadgeAppeal: ((BadgeType, String) -> Pair<Boolean, String>)? = null,
    onSendAppealChat: ((String, String, String?) -> Pair<Boolean, String>)? = null,
    viewerRole: UserRole = UserRole.PLAYER,
    onGrantBadge: ((String, BadgeType) -> Pair<Boolean, String>)? = null,
    onRevokeBadge: ((String, BadgeType) -> Pair<Boolean, String>)? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showBadgeShowcase by remember { mutableStateOf(false) }
    var showBadgePicker by remember(profile.userId, profile.uid) { mutableStateOf(false) }
    var badgeFeedback by remember(profile.userId, profile.uid) { mutableStateOf<Pair<Boolean, String>?>(null) }
    var currentBadges by remember(profile.userId, profile.uid, profile.badges) { mutableStateOf(profile.badges) }
    var highlightedBadge by remember { mutableStateOf<BadgeType?>(null) }
    var localKudos by remember(profile.userId, profile.kudosCount) { mutableIntStateOf(profile.kudosCount) }
    var localHasGivenKudos by remember(profile.userId, profile.kudosCount, hasGivenKudos) { mutableStateOf(hasGivenKudos) }
    var hasHypePop by remember { mutableStateOf(false) }

    val canManageTargetBadges = (viewerRole == UserRole.OWNER || viewerRole == UserRole.ADMIN) &&
        onGrantBadge != null &&
        onRevokeBadge != null &&
        !(profile.role == UserRole.OWNER && viewerRole == UserRole.ADMIN) &&
        profile.uid != "STAFF-TH" &&
        (profile.userId.isNotBlank() || (profile.uid.isNotBlank() && profile.uid != "N/A"))

    val targetLookupKey = profile.userId.takeIf { it.isNotBlank() && it != "N/A" }
        ?: profile.uid.takeIf { it.isNotBlank() && it != "N/A" }
        ?: profile.ign

    val hypeScale by animateFloatAsState(
        targetValue = if (hasHypePop) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        finishedListener = { hasHypePop = false },
        label = "profile_hype_btn_scale"
    )

    val accentBorderColor = when {
        profile.isBanned -> Color(0xFFEF4444)
        profile.role == UserRole.OWNER -> FlameYellow
        profile.role == UserRole.ADMIN -> FlameOrange
        else -> NeonPink
    }

    val tierTitle = when {
        profile.isBanned -> "🚫 সাসপেন্ডেড"
        profile.role == UserRole.OWNER -> "👑 অ্যাপ প্রতিষ্ঠাতা"
        profile.role == UserRole.ADMIN -> "🛡️ অফিসিয়াল অ্যাডমিন"
        profile.totalSpent >= 150 -> "💎 প্রাইম বিগ স্পেন্ডার"
        profile.matchesPlayed >= 10 -> "Ⓥ গোল্ডেন ভিআইপি যোদ্ধা"
        profile.matchesPlayed >= 5 -> "🔥 প্রো টুর্নামেন্ট প্লেয়ার"
        else -> "⚡ অ্যাক্টিভ চ্যালেঞ্জার"
    }

    val winRate = if (profile.matchesPlayed > 0) {
        ((profile.totalWins * 100f) / profile.matchesPlayed).toInt().coerceIn(0, 100)
    } else 0

    val killPerMatch = if (profile.matchesPlayed > 0) {
        String.format(Locale.US, "%.1f", profile.totalKills.toDouble() / profile.matchesPlayed)
    } else "0.0"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 730.dp)
                .testTag("player_profile_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111319)),
            border = BorderStroke(1.5.dp, accentBorderColor.copy(alpha = 0.75f))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. Esports Banner + Overlapping Circular Avatar
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Banner (Role & Prime-8 gated animated GIF / picture / app banner color)
                        ProfileBannerBackground(
                            role = profile.role,
                            badges = currentBadges,
                            customBannerUri = profile.customBannerUri,
                            bannerColorId = profile.bannerColorId,
                            isBanned = profile.isBanned,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(114.dp)
                                .testTag("player_profile_banner")
                        ) {
                            // Top-left tier badge over banner (close/edit pinned cleanly at TopEnd)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 12.dp, top = 10.dp, end = 92.dp, bottom = 10.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color.Black.copy(alpha = 0.45f),
                                    border = BorderStroke(1.dp, accentBorderColor.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = tierTitle,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = accentBorderColor,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                    // Bottom Banner Cutout & Overlapping Avatar Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 68.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Circular Avatar with Online Status Dot
                        Box(modifier = Modifier.size(86.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF111319))
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.5.dp,
                                        brush = Brush.linearGradient(listOf(accentBorderColor, FlameCyan)),
                                        shape = CircleShape
                                    )
                            ) {
                                ProfileAvatarImage(
                                    avatarId = profile.avatarId,
                                    customPfpUri = profile.customPfpUri,
                                    role = profile.role,
                                    contentDescription = profile.ign,
                                    showGifBadge = true,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }

                            // Online Status Ring Dot
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-2).dp, y = (-2).dp)
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF111319))
                                    .padding(3.5.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            profile.isBanned -> Color(0xFFEF4444)
                                            profile.isOnline -> Color(0xFF23A55A)
                                            else -> Color(0xFF80848E)
                                        }
                                    )
                            )
                        }

                        // Role Pill & Level Tag aligned to bottom-right of avatar row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = accentBorderColor.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, accentBorderColor.copy(alpha = 0.65f))
                            ) {
                                Text(
                                    text = "${profile.role.badge} ${profile.role.name}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accentBorderColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            if (profile.role != UserRole.OWNER) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = FlameCyan.copy(alpha = 0.14f),
                                    border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = "LVL ${profile.level}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = MonoFontFamily,
                                        color = FlameCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Main Player Profile Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Display Name (IGN), @Handle, Copyable UID & App Join Date
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = profile.ign,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 21.sp
                                ),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (currentBadges.isNotEmpty() || profile.role != UserRole.PLAYER) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = "Verified",
                                    tint = if (profile.role == UserRole.OWNER) VictoryGreen else FlameCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            if (profile.isSelf) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = VictoryGreen.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = "আপনি",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VictoryGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "@${profile.accountName} • ${if (profile.isBanned) "ব্যান করা হয়েছে" else "অনলাইন টুর্নামেন্টে"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // UID & Join/Download Date Pills Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkCard,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(profile.uid))
                                        Toast.makeText(context, "UID ${profile.uid} কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    }
                                    .testTag("player_profile_uid_copy")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("ফ্রি ফায়ার UID", fontSize = 9.sp, color = TextMuted)
                                        Text(
                                            text = profile.uid,
                                            fontSize = 11.sp,
                                            fontFamily = MonoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            color = FlameYellow,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = "Copy UID",
                                        tint = FlameYellow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkCard,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("player_profile_joined_date")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CalendarMonth,
                                        contentDescription = null,
                                        tint = FlameCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("অ্যাপ ডাউনলোড / জয়েন", fontSize = 9.sp, color = TextMuted)
                                        Text(
                                            text = profile.joinedDate,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Player "ABOUT ME / BIO" Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("player_profile_bio_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📝 আমার সম্পর্কে (BIO)",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = TextSecondary
                                )
                                if (profile.isSelf && onEditOwnProfile != null) {
                                    Text(
                                        text = "এডিট",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FlameYellow,
                                        modifier = Modifier.clickable { onEditOwnProfile() }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = profile.bio,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 19.sp,
                                    fontSize = 13.sp
                                ),
                                color = TextPrimary
                            )
                        }
                    }

                    // 4. Interactive Animated Badges Card + Direct Staff Badge Role Picker
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkCard,
                        border = BorderStroke(
                            1.dp,
                            if (showBadgePicker) FlameYellow.copy(alpha = 0.75f) else FlameYellow.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("player_profile_badges_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
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
                                        imageVector = Icons.Filled.MilitaryTech,
                                        contentDescription = null,
                                        tint = FlameYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "অর্জিত ব্যাজসমূহ (${currentBadges.size})",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                        color = FlameYellow,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (canManageTargetBadges) {
                                        Surface(
                                            onClick = { showBadgePicker = !showBadgePicker },
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (showBadgePicker) {
                                                VictoryGreen.copy(alpha = 0.18f)
                                            } else {
                                                FlameYellow.copy(alpha = 0.18f)
                                            },
                                            border = BorderStroke(
                                                1.dp,
                                                if (showBadgePicker) VictoryGreen else FlameYellow
                                            ),
                                            modifier = Modifier
                                                .widthIn(min = 48.dp)
                                                .heightIn(min = 36.dp)
                                                .testTag("profile_toggle_badge_picker_btn")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (showBadgePicker) Icons.Filled.Check else Icons.Filled.Add,
                                                    contentDescription = if (showBadgePicker) "ব্যাজ সিলেকশন সম্পন্ন" else "ব্যাজ যোগ করুন",
                                                    tint = if (showBadgePicker) VictoryGreen else FlameYellow,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Text(
                                                    text = if (showBadgePicker) "সম্পন্ন" else "ব্যাজ দিন",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (showBadgePicker) VictoryGreen else FlameYellow
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = "গাইড ›",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FlameCyan,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                if (onViewAllBadges != null) onViewAllBadges() else showBadgeShowcase = true
                                            }
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (currentBadges.isEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (canManageTargetBadges) {
                                        Surface(
                                            onClick = { showBadgePicker = !showBadgePicker },
                                            shape = CircleShape,
                                            color = FlameYellow.copy(alpha = 0.16f),
                                            border = BorderStroke(1.2.dp, FlameYellow),
                                            modifier = Modifier
                                                .minimumInteractiveComponentSize()
                                                .size(40.dp)
                                                .testTag("profile_inline_add_badge_btn")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Filled.Add,
                                                    contentDescription = "ব্যাজ যোগ করুন",
                                                    tint = FlameYellow,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = DarkElevated,
                                        border = BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = if (canManageTargetBadges) {
                                                "🌱 কোনো ব্যাজ নেই — '+' বাটনে ট্যাপ করে এই প্রোফাইলে ব্যাজ দিন!"
                                            } else {
                                                "🌱 নবাগত প্লেয়ার — ম্যাচ খেলে বা স্লট বুক করে প্রো ও ভি-ব্যাজ আনলক করুন!"
                                            },
                                            fontSize = 11.sp,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (canManageTargetBadges) {
                                        Surface(
                                            onClick = { showBadgePicker = !showBadgePicker },
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (showBadgePicker) {
                                                VictoryGreen.copy(alpha = 0.16f)
                                            } else {
                                                FlameYellow.copy(alpha = 0.14f)
                                            },
                                            border = BorderStroke(
                                                1.2.dp,
                                                if (showBadgePicker) VictoryGreen else FlameYellow.copy(alpha = 0.85f)
                                            ),
                                            modifier = Modifier
                                                .widthIn(min = 48.dp)
                                                .heightIn(min = 48.dp)
                                                .testTag("profile_inline_add_badge_btn")
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (showBadgePicker) Icons.Filled.Check else Icons.Filled.Add,
                                                    contentDescription = "ব্যাজ যোগ বা পরিবর্তন",
                                                    tint = if (showBadgePicker) VictoryGreen else FlameYellow,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = if (showBadgePicker) "বন্ধ" else "+ যোগ",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (showBadgePicker) VictoryGreen else FlameYellow
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier
                                            .weight(1f)
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        currentBadges.forEachIndexed { idx, badge ->
                                            InteractiveProfileBadgeTile(
                                                badge = badge,
                                                staggerIndex = idx,
                                                isHighlighted = highlightedBadge == badge,
                                                onClick = {
                                                    highlightedBadge = if (highlightedBadge == badge) null else badge
                                                }
                                            )
                                        }
                                    }
                                }
                                highlightedBadge?.let { selected ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(selected.hexColor).copy(alpha = 0.14f),
                                        border = BorderStroke(1.dp, Color(selected.hexColor).copy(alpha = 0.45f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${selected.title}: ${selected.description} (${selected.criteria})",
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            AnimatedVisibility(
                                visible = canManageTargetBadges && showBadgePicker,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .testTag("profile_badge_picker_panel")
                                ) {
                                    HorizontalDivider(color = DarkBorder)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "✨ রোল ও ব্যাজ সিলেক্টর — ট্যাপ করে যোগ বা রিমুভ করুন",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FlameCyan
                                    )
                                    badgeFeedback?.let { (ok, msg) ->
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = (if (ok) VictoryGreen else FireRed).copy(alpha = 0.14f),
                                            border = BorderStroke(
                                                1.dp,
                                                (if (ok) VictoryGreen else FireRed).copy(alpha = 0.55f)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("profile_badge_feedback_text")
                                        ) {
                                            Text(
                                                text = msg,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (ok) VictoryGreen else FireRed,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        BadgeType.entries.forEach { badge ->
                                            val isAssigned = badge in currentBadges
                                            val badgeColor = Color(badge.hexColor)
                                            Surface(
                                                onClick = {
                                                    if (isAssigned) {
                                                        val res = onRevokeBadge?.invoke(targetLookupKey, badge)
                                                            ?: Pair(false, "ব্যাজ আপডেট সম্ভব হয়নি!")
                                                        badgeFeedback = res
                                                        if (res.first) {
                                                            currentBadges = currentBadges.filter { it != badge }
                                                        }
                                                    } else {
                                                        val res = onGrantBadge?.invoke(targetLookupKey, badge)
                                                            ?: Pair(false, "ব্যাজ আপডেট সম্ভব হয়নি!")
                                                        badgeFeedback = res
                                                        if (res.first && badge !in currentBadges) {
                                                            currentBadges = currentBadges + badge
                                                        }
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (isAssigned) {
                                                    badgeColor.copy(alpha = 0.18f)
                                                } else {
                                                    DarkElevated
                                                },
                                                border = BorderStroke(
                                                    width = if (isAssigned) 1.4.dp else 1.dp,
                                                    color = if (isAssigned) badgeColor else DarkBorder
                                                ),
                                                modifier = Modifier.testTag("profile_badge_option_${badge.name}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(8.dp)
                                                            .clip(CircleShape)
                                                            .background(badgeColor)
                                                    )
                                                    BadgeEmblemArt(
                                                        badge = badge,
                                                        size = 18.dp
                                                    )
                                                    Text(
                                                        text = badge.title,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isAssigned) FontWeight.Black else FontWeight.SemiBold,
                                                        color = if (isAssigned) Color.White else TextSecondary,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                    Icon(
                                                        imageVector = if (isAssigned) Icons.Filled.CheckCircle else Icons.Filled.Add,
                                                        contentDescription = if (isAssigned) "সক্রিয়" else "যুক্ত করুন",
                                                        tint = if (isAssigned) VictoryGreen else FlameYellow,
                                                        modifier = Modifier.size(15.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 5 & 6. Clean Official Owner Verification Card OR Player Esports Stats + Loadout
                    if (profile.role == UserRole.OWNER) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF141B12),
                            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.55f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("owner_profile_clean_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "👑 অফিসিয়াল অ্যাপ ওনার পরিচয়",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = FlameYellow
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    LoadoutMiniBox(
                                        title = "🛡️ পদবি",
                                        value = "অফিসিয়াল ওনার",
                                        color = VictoryGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    LoadoutMiniBox(
                                        title = "🔰 প্ল্যাটফর্ম",
                                        value = profile.guildName.ifBlank { "TRUSTED TH" },
                                        color = FlameYellow,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    } else {
                        // 5. Esports Career & Spending Stats Grid
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkCard,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("player_profile_stats_card")
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "📊 টুর্নামেন্ট ক্যারিয়ার ও খরচ",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = TextSecondary
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ProfileStatTile(
                                        emoji = "🎮",
                                        label = "মোট ম্যাচ",
                                        value = "${profile.matchesPlayed} টি",
                                        accent = FlameCyan,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("player_profile_stat_matches")
                                    )
                                    ProfileStatTile(
                                        emoji = "💸",
                                        label = "মোট খরচ",
                                        value = "৳${profile.totalSpent}",
                                        accent = BkashPink,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("player_profile_stat_spent")
                                    )
                                    ProfileStatTile(
                                        emoji = "🏆",
                                        label = "মোট বুইয়া",
                                        value = "${profile.totalWins}",
                                        accent = FlameYellow,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ProfileStatTile(
                                        emoji = "🎯",
                                        label = "মোট কিল",
                                        value = "${profile.totalKills}",
                                        accent = FlameOrange,
                                        modifier = Modifier.weight(1f)
                                    )
                                    ProfileStatTile(
                                        emoji = "💰",
                                        label = "প্রাইজ আয়",
                                        value = "৳${profile.totalEarnings}",
                                        accent = VictoryGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    ProfileStatTile(
                                        emoji = "📈",
                                        label = "উইন / K-M",
                                        value = "$winRate% • $killPerMatch",
                                        accent = NeonPink,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // 6. Tactical Esports Loadout & Guild Details
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkCard,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "⚔️ গেম লোডআউট ও স্কোয়াড তথ্য",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    ),
                                    color = TextSecondary
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    LoadoutMiniBox(
                                        title = "🔫 ফেভারিট গান",
                                        value = profile.favoriteWeapon,
                                        color = FlameYellow,
                                        modifier = Modifier.weight(1f)
                                    )
                                    LoadoutMiniBox(
                                        title = "⚡ ক্যারেক্টার",
                                        value = profile.activeCharacter,
                                        color = FlameCyan,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    LoadoutMiniBox(
                                        title = "🔰 গিল্ড / স্কোয়াড",
                                        value = profile.guildName,
                                        color = VictoryGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    LoadoutMiniBox(
                                        title = "🎯 প্লে-স্টাইল",
                                        value = profile.playerRole,
                                        color = FlameOrange,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = DarkBorder)

                    // 7. Interactive Hype / Respect & Share Action Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                hasHypePop = true
                                val targetKey = profile.userId.ifBlank { profile.uid }.ifBlank { profile.ign }
                                if (onGiveKudos != null) {
                                    val serverCount = onGiveKudos.invoke(targetKey)
                                    localKudos = serverCount.coerceAtLeast(0)
                                    localHasGivenKudos = !localHasGivenKudos
                                } else {
                                    localHasGivenKudos = !localHasGivenKudos
                                    localKudos = if (localHasGivenKudos) localKudos + 1 else (localKudos - 1).coerceAtLeast(0)
                                }
                            },
                            modifier = Modifier
                                .weight(1.25f)
                                .height(48.dp)
                                .graphicsLayer {
                                    scaleX = hypeScale
                                    scaleY = hypeScale
                                }
                                .testTag("player_profile_give_kudos_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (localHasGivenKudos) Color(0xFF14532D) else BkashPink,
                                contentColor = Color.White
                            ),
                            border = if (localHasGivenKudos) BorderStroke(1.dp, VictoryGreen) else null,
                            contentPadding = PaddingValues(horizontal = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (localHasGivenKudos) {
                                    "GG দেওয়া হয়েছে ($localKudos) • সরান"
                                } else {
                                    "রেস্পেক্ট / GG ($localKudos)"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val summary = """
                                    🎮 ${profile.ign} (${profile.role.banglaTitle})
                                    🆔 UID: ${profile.uid}
                                    📅 ডাউনলোড/জয়েন: ${profile.joinedDate}
                                    📝 বায়ো: ${profile.bio}
                                    🎯 ম্যাচ: ${profile.matchesPlayed} | 💸 খরচ: ৳${profile.totalSpent} | 🏆 বুইয়া: ${profile.totalWins}
                                    🔥 রেস্পেক্ট: $localKudos GG
                                """.trimIndent()
                                clipboardManager.setText(AnnotatedString(summary))
                                Toast.makeText(context, "${profile.ign}-এর প্রোফাইল কার্ড কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("player_profile_copy_btn"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.65f))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "Share Profile",
                                tint = FlameYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "শেয়ার",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameYellow
                            )
                        }
                    }
                }
            }

            // Pinned top-right floating Edit & Close controls (always accessible even when scrolled)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (profile.isSelf && onEditOwnProfile != null) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.65f),
                        border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { onEditOwnProfile() }
                            .testTag("player_profile_edit_btn")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = FlameYellow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() }
                        .testTag("player_profile_close_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        }
    }

    if (showBadgeShowcase) {
        BadgeShowcaseDialog(
            currentBadges = currentBadges,
            onDismiss = { showBadgeShowcase = false },
            userProfile = if (profile.isSelf) userProfileForAppeal else null,
            badgeAppeals = badgeAppeals,
            onSubmitBadgeAppeal = if (profile.isSelf) onSubmitBadgeAppeal else null,
            onSendAppealChat = if (profile.isSelf) onSendAppealChat else null
        )
    }
}

@Composable
private fun ProfileStatTile(
    emoji: String,
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkElevated,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$emoji $label", fontSize = 9.sp, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = MonoFontFamily,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LoadoutMiniBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkElevated,
        border = BorderStroke(1.dp, DarkBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)) {
            Text(text = title, fontSize = 9.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
