package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppCustomization
import com.example.model.AppUser
import com.example.model.BonusGiftDrop
import com.example.model.DepositRequest
import com.example.model.GameCategory
import com.example.model.GiveawayEvent
import com.example.model.GiveawayPrizeType
import com.example.model.LiveCommunityPoll
import com.example.model.MatchPresetTemplate
import com.example.model.MatchType
import com.example.model.ModerationLogEntry
import com.example.model.PromoCodeItem
import com.example.model.TournamentMatch
import com.example.model.AppealStatus
import com.example.model.BadgeAppeal
import com.example.model.BadgeType
import com.example.model.UserRole
import com.example.model.WithdrawRequest
import com.example.model.formatMatchClockTimeLabel
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FireRed
import com.example.ui.theme.FireWaterBorderGradient
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen

@Composable
fun AdminControlDialog(
    matches: List<TournamentMatch>,
    currentWhatsAppUrl: String,
    allUsers: List<AppUser> = emptyList(),
    depositRequests: List<DepositRequest> = emptyList(),
    withdrawRequests: List<WithdrawRequest> = emptyList(),
    onDismiss: () -> Unit,
    onPublishRoom: (matchId: String, roomId: String, password: String) -> Unit,
    onUpdateWhatsAppUrl: (url: String) -> Unit,
    onCreateNewMatch: (
        title: String,
        category: GameCategory,
        matchType: MatchType,
        mapName: String,
        matchDateTime: String,
        entryFee: Int,
        totalPrize: Int,
        perKill: Int,
        booyahPrize: Int,
        totalSlots: Int,
        startsInMinutes: Int
    ) -> Unit,
    onAdjustUserBalance: (target: String, amount: Int, isAdd: Boolean, reason: String) -> Boolean = { _, _, _, _ -> false },
    onIncrementUserMatchesPlayed: (target: String, delta: Int) -> Boolean = { _, _ -> false },
    onApproveDeposit: (requestId: String) -> Boolean = { false },
    onRejectDeposit: (requestId: String) -> Boolean = { false },
    onApproveWithdraw: (requestId: String) -> Boolean = { false },
    onRejectWithdraw: (requestId: String) -> Boolean = { false },
    badgeAppeals: List<BadgeAppeal> = emptyList(),
    onApproveBadgeAppeal: (appealId: String, selectedBadge: BadgeType) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onDenyBadgeAppeal: (appealId: String, reason: String) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onSendAppealChat: (appealId: String, text: String, proofImageUri: String?) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    moderationLogs: List<ModerationLogEntry> = emptyList(),
    onWarnUser: (targetKey: String, reason: String) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onClearWarnings: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    onTimeoutUser: (targetKey: String, durationMinutes: Long, reason: String) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    onRemoveTimeout: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    onMuteChat: (targetKey: String, durationHours: Long, reason: String) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    onUnmuteChat: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    onSetMatchBan: (targetKey: String, banned: Boolean, reason: String) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    onSetWalletFreeze: (targetKey: String, frozen: Boolean, reason: String) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    onTempBanUser: (targetKey: String, days: Int, reason: String) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    onPermBanUser: (targetKey: String, reason: String) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onUnbanUser: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    onGrantBadge: (targetKey: String, badge: BadgeType) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onRevokeBadge: (targetKey: String, badge: BadgeType) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onStripAllBadges: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    onPardonAll: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    appCustomization: AppCustomization = AppCustomization(),
    onPublishAnnouncement: (title: String, message: String, isUrgent: Boolean) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    onClearAnnouncement: () -> Pair<Boolean, String> = { false to "" },
    promoCodes: List<PromoCodeItem> = emptyList(),
    bonusDrops: List<BonusGiftDrop> = emptyList(),
    communityPolls: List<LiveCommunityPoll> = emptyList(),
    onCreatePromoCode: (String, String, Int, Int) -> Pair<Boolean, String> = { _, _, _, _ -> false to "" },
    onTogglePromoActive: (String, Boolean) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onDeletePromoCode: (String) -> Pair<Boolean, String> = { false to "" },
    onLaunchBonusDrop: (String, String, Int, Int) -> Pair<Boolean, String> = { _, _, _, _ -> false to "" },
    onCloseBonusDrop: (String) -> Pair<Boolean, String> = { false to "" },
    onDeleteBonusDrop: (String) -> Pair<Boolean, String> = { false to "" },
    onLaunchCommunityPoll: (String, List<String>) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onCloseCommunityPoll: (String) -> Pair<Boolean, String> = { false to "" },
    onDeleteCommunityPoll: (String) -> Pair<Boolean, String> = { false to "" },
    onBroadcastMatchToChat: (String) -> Pair<Boolean, String> = { false to "" },
    giveaways: List<GiveawayEvent> = emptyList(),
    onCreateGiveaway: (String, String, GiveawayPrizeType, String, Int, Int, Int, Boolean) -> Pair<Boolean, String> = { _, _, _, _, _, _, _, _ -> false to "" },
    onDrawGiveawayWinners: (String) -> Pair<Boolean, String> = { false to "" },
    onCloseGiveaway: (String) -> Pair<Boolean, String> = { false to "" },
    onDeleteGiveaway: (String) -> Pair<Boolean, String> = { false to "" },
    matchPresets: List<MatchPresetTemplate> = emptyList(),
    onLaunchMatchPreset: (presetId: String, overrideStartsInMinutes: Int?) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onSaveMatchPreset: (MatchPresetTemplate) -> Pair<Boolean, String> = { false to "" },
    onDeleteMatchPreset: (presetId: String) -> Pair<Boolean, String> = { false to "" },
    onHoldBrAndCsForLoneWolf: () -> Pair<Boolean, String> = { false to "" }
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Room, 1: WhatsApp, 2: Create Match, 3: Balance, 4: Requests, 5: Badge Appeals, 6: Moderation, 7: Live Announcement
    val pendingBadgeAppealsCount = remember(badgeAppeals) { badgeAppeals.count { it.status == AppealStatus.PENDING } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .testTag("admin_control_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, FireWaterBorderGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                                .background(FlameOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = FlameYellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TH অ্যাডমিন কন্ট্রোল সেন্টার",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = FlameYellow
                            )
                            Text(
                                text = "১-ট্যাপে রুম আইডি, পাসওয়ার্ড ও WhatsApp লিঙ্ক কন্ট্রোল",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Selector
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 6.dp,
                    containerColor = DarkCard,
                    contentColor = FlameYellow,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = FlameYellow,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "🚀 রুম প্রকাশ",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) FlameYellow else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "🟢 WhatsApp",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) VictoryGreen else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "➕ নতুন ম্যাচ",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 2) FlameYellow else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Text(
                                text = "💰 ব্যালেন্স",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 3) FlameYellow else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        text = {
                            Text(
                                text = "💳 রিকোয়েস্ট",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 4) FlameYellow else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 5,
                        onClick = { selectedTab = 5 },
                        modifier = Modifier.testTag("admin_tab_badge_appeals"),
                        text = {
                            val tabTitle = if (pendingBadgeAppealsCount > 0) {
                                "🏅 ব্যাজ আবেদন ($pendingBadgeAppealsCount)"
                            } else {
                                "🏅 ব্যাজ আবেদন"
                            }
                            Text(
                                text = tabTitle,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 5) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 5) FlameCyan else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 6,
                        onClick = { selectedTab = 6 },
                        modifier = Modifier.testTag("admin_tab_moderation"),
                        text = {
                            Text(
                                text = "🛡️ মডারেশন",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 6) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 6) FlameCyan else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 7,
                        onClick = { selectedTab = 7 },
                        modifier = Modifier.testTag("admin_tab_announcement"),
                        text = {
                            Text(
                                text = "📢 ঘোষণা",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 7) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 7) FlameYellow else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 8,
                        onClick = { selectedTab = 8 },
                        modifier = Modifier.testTag("admin_tab_fun_controls"),
                        text = {
                            Text(
                                text = "🎉 গিভওয়ে/ড্রপ/পোল",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 8) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 8) VictoryGreen else Color.White
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        PublishRoomSection(
                            matches = matches,
                            onPublish = { matchId, roomId, pass ->
                                onPublishRoom(matchId, roomId, pass)
                            }
                        )
                    }
                    1 -> {
                        WhatsAppGroupSection(
                            currentUrl = currentWhatsAppUrl,
                            onUpdateUrl = onUpdateWhatsAppUrl
                        )
                    }
                    2 -> {
                        CreateMatchSection(
                            matchPresets = matchPresets,
                            onCreateMatch = onCreateNewMatch,
                            onLaunchPreset = onLaunchMatchPreset,
                            onSavePreset = onSaveMatchPreset,
                            onDeletePreset = onDeleteMatchPreset,
                            onHoldBrAndCsForLoneWolf = onHoldBrAndCsForLoneWolf
                        )
                    }
                    3 -> {
                        UserBalanceControlContent(
                            allUsers = allUsers,
                            onAdjustUserBalance = onAdjustUserBalance,
                            onIncrementUserMatchesPlayed = onIncrementUserMatchesPlayed
                        )
                    }
                    4 -> {
                        DepositWithdrawQueueContent(
                            depositRequests = depositRequests,
                            withdrawRequests = withdrawRequests,
                            onApproveDeposit = onApproveDeposit,
                            onRejectDeposit = onRejectDeposit,
                            onApproveWithdraw = onApproveWithdraw,
                            onRejectWithdraw = onRejectWithdraw
                        )
                    }
                    5 -> {
                        BadgeAppealsReviewSection(
                            appeals = badgeAppeals,
                            allUsers = allUsers,
                            onApproveAppeal = onApproveBadgeAppeal,
                            onDenyAppeal = onDenyBadgeAppeal,
                            onSendAppealChat = onSendAppealChat
                        )
                    }
                    6 -> {
                        ModerationPanelSection(
                            actorRole = UserRole.ADMIN,
                            allUsers = allUsers,
                            moderationLogs = moderationLogs,
                            onWarnUser = onWarnUser,
                            onClearWarnings = onClearWarnings,
                            onTimeoutUser = onTimeoutUser,
                            onRemoveTimeout = onRemoveTimeout,
                            onMuteChat = onMuteChat,
                            onUnmuteChat = onUnmuteChat,
                            onSetMatchBan = onSetMatchBan,
                            onSetWalletFreeze = onSetWalletFreeze,
                            onTempBanUser = onTempBanUser,
                            onPermBanUser = onPermBanUser,
                            onUnbanUser = onUnbanUser,
                            onGrantBadge = onGrantBadge,
                            onRevokeBadge = onRevokeBadge,
                            onStripAllBadges = onStripAllBadges,
                            onPardonAll = onPardonAll
                        )
                    }
                    7 -> {
                        LiveAnnouncementControlSection(
                            actorRole = UserRole.ADMIN,
                            appCustomization = appCustomization,
                            onPublishAnnouncement = onPublishAnnouncement,
                            onClearAnnouncement = onClearAnnouncement
                        )
                    }
                    8 -> {
                        AdminAndOwnerFunControlSection(
                            matches = matches,
                            promoCodes = promoCodes,
                            bonusDrops = bonusDrops,
                            communityPolls = communityPolls,
                            onCreatePromoCode = onCreatePromoCode,
                            onTogglePromoActive = onTogglePromoActive,
                            onDeletePromoCode = onDeletePromoCode,
                            onLaunchBonusDrop = onLaunchBonusDrop,
                            onCloseBonusDrop = onCloseBonusDrop,
                            onDeleteBonusDrop = onDeleteBonusDrop,
                            onLaunchCommunityPoll = onLaunchCommunityPoll,
                            onCloseCommunityPoll = onCloseCommunityPoll,
                            onDeleteCommunityPoll = onDeleteCommunityPoll,
                            onBroadcastMatchToChat = onBroadcastMatchToChat,
                            giveaways = giveaways,
                            onCreateGiveaway = onCreateGiveaway,
                            onDrawGiveawayWinners = onDrawGiveawayWinners,
                            onCloseGiveaway = onCloseGiveaway,
                            onDeleteGiveaway = onDeleteGiveaway
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PublishRoomSection(
    matches: List<TournamentMatch>,
    onPublish: (matchId: String, roomId: String, password: String) -> Unit
) {
    var selectedMatchId by remember { mutableStateOf(matches.firstOrNull()?.id ?: "") }
    var roomIdInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("TH77") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val currentMatch = matches.find { it.id == selectedMatchId }

    Column {
        Text(
            text = "১-ট্যাপে রুম আইডি ও পাসওয়ার্ড পাঠান:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = FlameYellow
        )
        Text(
            text = "এখানে রুম নম্বর ও পাসওয়ার্ড দিয়ে বাটনে চাপলে, যে প্লেয়াররা স্লট কিনেছে সবাই এক নিমিষে তাদের অ্যাপে কোড দেখতে পাবে।",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ম্যাচ নির্বাচন করুন:",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Match Select Buttons
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            matches.forEach { match ->
                val isSelected = match.id == selectedMatchId
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else DarkCard,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) FlameYellow else DarkBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedMatchId = match.id
                            if (!match.roomId.isNullOrBlank()) {
                                roomIdInput = match.roomId
                            }
                            if (!match.roomPassword.isNullOrBlank()) {
                                passwordInput = match.roomPassword
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = match.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) FlameYellow else Color.White
                            )
                            Text(
                                text = "${match.matchDateTime} • স্লট: ${match.bookedSlots}/${match.totalSlots}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (match.roomId != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = VictoryGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "রুম ওপেন ✓",
                                    color = VictoryGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Room ID Input
        OutlinedTextField(
            value = roomIdInput,
            onValueChange = { roomIdInput = it },
            label = { Text("ফ্রি ফায়ার রুম আইডি (Room ID)") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Key, contentDescription = null, tint = FlameYellow)
            },
            placeholder = { Text("যেমন: 8849102") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_room_id_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlameYellow,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Room Password Input
        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("রুম পাসওয়ার্ড (Password)") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = FlameYellow)
            },
            placeholder = { Text("যেমন: TH77") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_room_pass_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlameYellow,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Publish Button
        Button(
            onClick = {
                if (roomIdInput.isBlank()) {
                    statusMessage = "অনুগ্রহ করে রুম আইডি লিখুন"
                    return@Button
                }
                if (passwordInput.isBlank()) {
                    statusMessage = "অনুগ্রহ করে রুম পাসওয়ার্ড লিখুন"
                    return@Button
                }
                onPublish(selectedMatchId, roomIdInput.trim(), passwordInput.trim())
                statusMessage = "রুম আইডি সফলভাবে সকল প্লেয়ারদের কাছে প্রকাশিত হয়েছে!"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("admin_publish_room_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Filled.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🚀 ১-ট্যাপে সব প্লেয়ারদের কাছে পাঠান",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        statusMessage?.let { msg ->
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = msg,
                color = VictoryGreen,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun WhatsAppGroupSection(
    currentUrl: String,
    onUpdateUrl: (url: String) -> Unit
) {
    var urlInput by remember(currentUrl) { mutableStateOf(currentUrl) }
    var isSaved by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "অফিসিয়াল WhatsApp টুর্নামেন্ট গ্রুপ লিঙ্ক:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = VictoryGreen
        )
        Text(
            text = "এখানে আপনি আপনার WhatsApp গ্রুপের লিঙ্ক দিন। যে প্লেয়াররা স্লট কিনবে তারা অ্যাপে ১-ট্যাপে সরাসরি এই গ্রুপে জয়েন করতে পারবে।",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = urlInput,
            onValueChange = {
                urlInput = it
                isSaved = false
            },
            label = { Text("WhatsApp গ্রুপ ইনভাইট লিঙ্ক") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Link, contentDescription = null, tint = VictoryGreen)
            },
            placeholder = { Text("https://chat.whatsapp.com/...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_whatsapp_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VictoryGreen,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onUpdateUrl(urlInput.trim())
                isSaved = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("admin_save_whatsapp_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
        ) {
            Icon(imageVector = Icons.Filled.Chat, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🟢 লিঙ্ক ১-ট্যাপে আপডেট ও শেয়ার করুন",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        if (isSaved) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "✓ WhatsApp গ্রুপ লিঙ্ক সফলভাবে আপডেট হয়েছে! প্লেয়াররা এখন এই লিঙ্কে জয়েন করতে পারবে।",
                color = VictoryGreen,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun CreateMatchSection(
    matchPresets: List<MatchPresetTemplate>,
    onCreateMatch: (
        title: String,
        category: GameCategory,
        matchType: MatchType,
        mapName: String,
        matchDateTime: String,
        entryFee: Int,
        totalPrize: Int,
        perKill: Int,
        booyahPrize: Int,
        totalSlots: Int,
        startsInMinutes: Int
    ) -> Unit,
    onLaunchPreset: (presetId: String, overrideStartsInMinutes: Int?) -> Pair<Boolean, String>,
    onSavePreset: (MatchPresetTemplate) -> Pair<Boolean, String>,
    onDeletePreset: (presetId: String) -> Pair<Boolean, String>,
    onHoldBrAndCsForLoneWolf: () -> Pair<Boolean, String>
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("লোন উলফ - ১ বনাম ১ কিং ডুয়েল") }
    var selectedCategory by remember { mutableStateOf(GameCategory.LONE_WOLF) }
    var matchType by remember { mutableStateOf(MatchType.SOLO) }
    var mapName by remember { mutableStateOf("আয়রন কেজ (Iron Cage)") }
    var startsInMinutesInput by remember { mutableStateOf("15") }
    var matchDateTime by remember {
        mutableStateOf(formatMatchClockTimeLabel(System.currentTimeMillis() + 15 * 60_000L))
    }
    var entryFee by remember { mutableStateOf("10") }
    var totalPrize by remember { mutableStateOf("30") }
    var perKill by remember { mutableStateOf("0") }
    var booyahPrize by remember { mutableStateOf("30") }
    var totalSlots by remember { mutableStateOf("2") }

    val applyMinutesToClock: (Int) -> Unit = { mins ->
        val safe = mins.coerceIn(1, 100000)
        startsInMinutesInput = safe.toString()
        matchDateTime = formatMatchClockTimeLabel(System.currentTimeMillis() + safe * 60_000L)
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Saved BR & CS Preset Slots Card + Lone Wolf Hold Action
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_presets_panel")
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💾 সেভড BR ও CS প্রিসেট স্লট প্যানেল",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                        color = FlameCyan
                    )
                }
                Text(
                    text = "বারমুডা (BR Rank) ও ক্ল্যাশ স্কোয়াড (CS Rank) এখানে সেভ করা আছে — এখন শুধু Lone Wolf চালু রেখে পরে ১-ট্যাপে লাইভ করতে পারবেন।",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )

                OutlinedButton(
                    onClick = {
                        val res = onHoldBrAndCsForLoneWolf()
                        Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_hold_br_cs_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.7f))
                ) {
                    Text(
                        text = "🐺 BR/CS হোল্ড রাখুন — শুধু Lone Wolf সক্রিয় করুন",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlameYellow
                    )
                }

                matchPresets.forEach { preset ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurface,
                        border = BorderStroke(1.dp, preset.category.accentColor.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = preset.presetName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${preset.totalSlots} স্লট • ${preset.mapName} • এন্ট্রি ৳${preset.entryFee} • প্রাইজ ৳${preset.totalPrize} • ⏱️ ${preset.defaultStartsInMinutes}মি.",
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                                if (!preset.isDefaultSaved) {
                                    IconButton(
                                        onClick = {
                                            val res = onDeletePreset(preset.id)
                                            Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete Preset",
                                            tint = FireRed
                                        )
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        selectedCategory = preset.category
                                        matchType = preset.matchType
                                        title = preset.matchTitle
                                        mapName = preset.mapName
                                        entryFee = preset.entryFee.toString()
                                        totalPrize = preset.totalPrize.toString()
                                        perKill = preset.perKillPrize.toString()
                                        booyahPrize = preset.booyahPrize.toString()
                                        totalSlots = preset.totalSlots.toString()
                                        applyMinutesToClock(preset.defaultStartsInMinutes)
                                        Toast.makeText(context, "ফর্মে লোড হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("admin_load_preset_${preset.id}"),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("ফর্মে লোড", fontSize = 11.sp, color = FlameCyan, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        val overrideMins = startsInMinutesInput.toIntOrNull()?.coerceIn(1, 100000)
                                        val res = onLaunchPreset(preset.id, overrideMins)
                                        Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(48.dp)
                                        .testTag("admin_launch_preset_${preset.id}"),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                                ) {
                                    Text("🚀 লাইভ করুন", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = DarkBorder)

        Text(
            text = "নতুন টুর্নামেন্ট ম্যাচ ও লাইভ টাইমার তৈরি:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = FlameYellow
        )

        // Match Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("ম্যাচ টাইটেল") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlameYellow,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        // Category Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(GameCategory.LONE_WOLF, GameCategory.BR_RANK, GameCategory.CS_RANK).forEach { cat ->
                val isSelected = cat == selectedCategory
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) FlameOrange else DarkCard,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedCategory = cat
                            if (cat == GameCategory.LONE_WOLF) {
                                matchType = MatchType.SOLO
                                totalSlots = "2"
                                title = "লোন উলফ - ১ বনাম ১ কিং ডুয়েল"
                                mapName = "আয়রন কেজ (Iron Cage)"
                                entryFee = "10"
                                totalPrize = "30"
                                perKill = "0"
                                booyahPrize = "30"
                            } else if (cat == GameCategory.CS_RANK) {
                                matchType = MatchType.SQUAD
                                totalSlots = "8"
                                title = "সিএস র‍্যাঙ্ক - ৪ বনাম ৪ ক্ল্যাশ স্কোয়াড"
                                mapName = "ক্লক টাওয়ার (Clock Tower)"
                                entryFee = "15"
                                totalPrize = "100"
                                perKill = "0"
                                booyahPrize = "100"
                            } else {
                                matchType = MatchType.SOLO
                                totalSlots = "48"
                                title = "বিআর র‍্যাঙ্ক - বারমুডা সোলো এক্সপ্রেস"
                                mapName = "বারমুডা (Bermuda)"
                                entryFee = "15"
                                totalPrize = "250"
                                perKill = "4"
                                booyahPrize = "100"
                            }
                        }
                ) {
                    Text(
                        text = cat.banglaTitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Live Countdown Quick Chips
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Schedule, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(15.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "⏱️ কত মিনিট পর ম্যাচ শুরু হবে (রিয়েল-টাইম):",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = FlameYellow
            )
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(7, 10, 15, 20, 30, 45, 60).forEach { mins ->
                val isSelected = startsInMinutesInput.toIntOrNull() == mins
                FilterChip(
                    selected = isSelected,
                    onClick = { applyMinutesToClock(mins) },
                    label = { Text("${mins}মি.", fontFamily = MonoFontFamily, fontSize = 11.sp) }
                )
            }
        }

        // Countdown minutes & Clock Time & Entry Fee
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = startsInMinutesInput,
                onValueChange = { raw ->
                    val digits = raw.filter { it.isDigit() }
                    startsInMinutesInput = digits
                    digits.toIntOrNull()?.takeIf { it > 0 }?.let { m ->
                        matchDateTime = formatMatchClockTimeLabel(System.currentTimeMillis() + m * 60_000L)
                    }
                },
                label = { Text("বাকি (মিনিট)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(0.9f)
                    .testTag("admin_starts_in_minutes_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = matchDateTime,
                onValueChange = { matchDateTime = it },
                label = { Text("ম্যাচের সময়") },
                modifier = Modifier.weight(1.3f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = entryFee,
                onValueChange = { entryFee = it.filter { ch -> ch.isDigit() } },
                label = { Text("ফি ৳") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.8f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        // Prize, PerKill, Booyah, Slots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = totalPrize,
                onValueChange = { totalPrize = it.filter { ch -> ch.isDigit() } },
                label = { Text("মোট প্রাইজ ৳") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            if (selectedCategory != GameCategory.LONE_WOLF) {
                OutlinedTextField(
                    value = perKill,
                    onValueChange = { perKill = it.filter { ch -> ch.isDigit() } },
                    label = { Text("প্রতি কিল ৳") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }
            OutlinedTextField(
                value = booyahPrize,
                onValueChange = { booyahPrize = it.filter { ch -> ch.isDigit() } },
                label = { Text(if (selectedCategory == GameCategory.LONE_WOLF) "উইন প্রাইজ ৳" else "বুইয়া ৳") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = totalSlots,
                onValueChange = { totalSlots = it.filter { ch -> ch.isDigit() } },
                label = { Text("স্লট") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.9f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = {
                val mins = (startsInMinutesInput.toIntOrNull() ?: 15).coerceIn(1, 100000)
                val safePerKill = if (selectedCategory == GameCategory.LONE_WOLF) 0 else (perKill.toIntOrNull() ?: 0)
                onCreateMatch(
                    title.ifBlank { "${selectedCategory.banglaTitle} ম্যাচ" },
                    selectedCategory,
                    matchType,
                    mapName,
                    matchDateTime,
                    entryFee.toIntOrNull() ?: 10,
                    totalPrize.toIntOrNull() ?: 30,
                    safePerKill,
                    booyahPrize.toIntOrNull() ?: 30,
                    (totalSlots.toIntOrNull() ?: 2).coerceIn(2, 100),
                    mins
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("admin_create_match_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ম্যাচ পাবলিশ করুন (Live Countdown সহ)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        OutlinedButton(
            onClick = {
                val mins = (startsInMinutesInput.toIntOrNull() ?: 15).coerceIn(1, 100000)
                val cleanTitle = title.trim().ifBlank { "${selectedCategory.banglaTitle} প্রিসেট" }
                val safePerKill = if (selectedCategory == GameCategory.LONE_WOLF) 0 else (perKill.toIntOrNull() ?: 0)
                val res = onSavePreset(
                    MatchPresetTemplate(
                        id = "preset_custom_${System.currentTimeMillis()}",
                        presetName = "${selectedCategory.iconEmoji} $cleanTitle",
                        matchTitle = cleanTitle,
                        category = selectedCategory,
                        matchType = matchType,
                        mapName = mapName,
                        defaultStartsInMinutes = mins,
                        entryFee = entryFee.toIntOrNull() ?: 10,
                        totalPrize = totalPrize.toIntOrNull() ?: 30,
                        perKillPrize = safePerKill,
                        booyahPrize = booyahPrize.toIntOrNull() ?: 30,
                        totalSlots = (totalSlots.toIntOrNull() ?: 2).coerceIn(2, 100),
                        isDefaultSaved = false
                    )
                )
                Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("admin_save_preset_btn"),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.7f))
        ) {
            Icon(imageVector = Icons.Filled.BookmarkAdded, contentDescription = null, tint = FlameCyan, modifier = Modifier.size(17.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "💾 প্যানেলে প্রিসেট স্লট হিসেবে সেভ রাখুন",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FlameCyan
            )
        }
    }
}
