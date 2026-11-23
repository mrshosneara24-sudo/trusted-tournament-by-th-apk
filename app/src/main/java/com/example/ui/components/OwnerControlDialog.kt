package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.AppCustomization
import com.example.model.AppLogoOption
import com.example.model.AppUser
import com.example.model.AppealStatus
import com.example.model.BadgeAppeal
import com.example.model.BadgeType
import com.example.model.BonusGiftDrop
import com.example.model.DepositRequest
import com.example.model.LiveCommunityPoll
import com.example.model.MatchResult
import com.example.model.ModerationLogEntry
import com.example.model.PromoCodeItem
import com.example.model.RequestStatus
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.model.TournamentRuleItem
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.WithdrawRequest
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.theme.FireWaterBorderGradient
import com.example.ui.theme.FireWaterHeaderGradient
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterCyan

@Composable
fun OwnerControlDialog(
    userProfile: UserProfile,
    allUsers: List<AppUser>,
    allBookings: List<SlotBooking> = emptyList(),
    matches: List<TournamentMatch> = emptyList(),
    depositRequests: List<DepositRequest> = emptyList(),
    withdrawRequests: List<WithdrawRequest> = emptyList(),
    onDismiss: () -> Unit,
    onClaimOwner: (key: String) -> Boolean,
    onBanUser: (userIdOrPhone: String, reason: String) -> Unit,
    onUnbanUser: (userIdOrPhone: String) -> Unit,
    onPromoteToAdmin: (target: String, passcode: String, name: String?, ign: String?, uid: String?) -> Unit,
    onDemoteAdmin: (userIdOrPhone: String) -> Unit,
    onUpdateMasterPin: (newPin: String) -> Boolean,
    onCancelSlotAndRefund: (bookingId: String) -> Unit = {},
    onCancelLegacySlot: (matchId: String, slotNumber: Int) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onAddManualSlot: (matchId: String, slotNumber: Int, ign: String, uid: String, phone: String, feePaid: Int) -> Pair<Boolean, String> = { _, _, _, _, _, _ -> false to "" },
    onUpdateMatchSlotCapacity: (matchId: String, newCapacity: Int) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onCancelMatchAndRefundAll: (matchId: String) -> Unit = {},
    onAdjustUserBalance: (target: String, amount: Int, isAdd: Boolean, reason: String) -> Boolean = { _, _, _, _ -> false },
    onIncrementUserMatchesPlayed: (target: String, delta: Int) -> Boolean = { _, _ -> false },
    onApproveDeposit: (requestId: String) -> Boolean = { false },
    onRejectDeposit: (requestId: String) -> Boolean = { false },
    onApproveWithdraw: (requestId: String) -> Boolean = { false },
    onRejectWithdraw: (requestId: String) -> Boolean = { false },
    onGrantBadge: (target: String, badge: BadgeType) -> Boolean = { _, _ -> false },
    onRevokeBadge: (target: String, badge: BadgeType) -> Boolean = { _, _ -> false },
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
    onPermBanWithResult: (targetKey: String, reason: String) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onUnbanUserWithResult: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    onGrantBadgeResult: (targetKey: String, badge: BadgeType) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onRevokeBadgeResult: (targetKey: String, badge: BadgeType) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onStripAllBadges: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    onPardonAll: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    appCustomization: AppCustomization = AppCustomization(),
    results: List<MatchResult> = emptyList(),
    onPublishAnnouncement: (title: String, message: String, isUrgent: Boolean) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    onClearAnnouncement: () -> Pair<Boolean, String> = { false to "" },
    onPublishMatchResult: (matchId: String, winnerTargetKey: String, winnerKills: Int, customPrizeAmount: Int?, runnerUpIgn: String, markMatchFinished: Boolean) -> Pair<Boolean, String> = { _, _, _, _, _, _ -> false to "" },
    onAwardPlayerStats: (targetKey: String, killsToAdd: Int, winsToAdd: Int, matchesToAdd: Int, prizeAmount: Int, note: String) -> Pair<Boolean, String> = { _, _, _, _, _, _ -> false to "" },
    promoCodes: List<PromoCodeItem> = emptyList(),
    bonusDrops: List<BonusGiftDrop> = emptyList(),
    communityPolls: List<LiveCommunityPoll> = emptyList(),
    strictRules: List<TournamentRuleItem> = emptyList(),
    auditSummaryText: String = "",
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
    onRefreshCloudState: () -> String = { "রিফ্রেশ সম্পন্ন!" },
    onUpdateAppBranding: (String, String, AppLogoOption, String, String) -> Unit = { _, _, _, _, _ -> },
    onUpdateFinancialSettings: (String, String, String, Int, Int) -> Unit = { _, _, _, _, _ -> },
    onToggleMaintenanceMode: (Boolean, String) -> Unit = { _, _ -> },
    onUpdateExistingMatch: (TournamentMatch) -> Boolean = { false },
    onDeleteMatch: (String) -> Boolean = { false },
    onAddStrictRule: (String, String, String, String) -> Boolean = { _, _, _, _ -> false },
    onDeleteStrictRule: (String) -> Boolean = { false },
    onResetDefaultRules: () -> Unit = {},
    onResetAppForFreshDownload: () -> Unit = {}
) {
    val context = LocalContext.current
    var isUnlocked by remember { mutableStateOf(userProfile.role == UserRole.OWNER) }
    var ownerKeyInput by remember { mutableStateOf("") }
    var unlockError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .heightIn(max = 820.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, FireWaterBorderGradient, RoundedCornerShape(24.dp))
                .testTag("owner_control_dialog"),
            color = DarkSurface
        ) {
            if (!isUnlocked) {
                // Owner Authentication Screen (Supreme Security Gate)
                OwnerAuthGate(
                    ownerKeyInput = ownerKeyInput,
                    onKeyChange = {
                        ownerKeyInput = it
                        unlockError = null
                    },
                    unlockError = unlockError,
                    onUnlock = {
                        if (ownerKeyInput.isBlank()) {
                            unlockError = "দয়া করে ওনার মাস্টার পাসকি বা পিন লিখুন"
                        } else {
                            val success = onClaimOwner(ownerKeyInput.trim())
                            if (success) {
                                isUnlocked = true
                                Toast.makeText(context, "👑 ওনার প্যানেল আনলক হয়েছে!", Toast.LENGTH_SHORT).show()
                            } else {
                                unlockError = "❌ ভুল মাস্টার পাসকি! অ্যাক্সেস কেবল মূল মালিকের জন্য।"
                            }
                        }
                    },
                    onDismiss = onDismiss
                )
            } else {
                // Full Master Owner Dashboard
                OwnerDashboardContent(
                    allUsers = allUsers,
                    allBookings = allBookings,
                    matches = matches,
                    depositRequests = depositRequests,
                    withdrawRequests = withdrawRequests,
                    onDismiss = onDismiss,
                    onBanUser = onBanUser,
                    onUnbanUser = onUnbanUser,
                    onPromoteToAdmin = onPromoteToAdmin,
                    onDemoteAdmin = onDemoteAdmin,
                    onUpdateMasterPin = onUpdateMasterPin,
                    onCancelSlotAndRefund = onCancelSlotAndRefund,
                    onCancelLegacySlot = onCancelLegacySlot,
                    onAddManualSlot = onAddManualSlot,
                    onUpdateMatchSlotCapacity = onUpdateMatchSlotCapacity,
                    onCancelMatchAndRefundAll = onCancelMatchAndRefundAll,
                    onAdjustUserBalance = onAdjustUserBalance,
                    onIncrementUserMatchesPlayed = onIncrementUserMatchesPlayed,
                    onApproveDeposit = onApproveDeposit,
                    onRejectDeposit = onRejectDeposit,
                    onApproveWithdraw = onApproveWithdraw,
                    onRejectWithdraw = onRejectWithdraw,
                    onGrantBadge = onGrantBadge,
                    onRevokeBadge = onRevokeBadge,
                    badgeAppeals = badgeAppeals,
                    onApproveBadgeAppeal = onApproveBadgeAppeal,
                    onDenyBadgeAppeal = onDenyBadgeAppeal,
                    onSendAppealChat = onSendAppealChat,
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
                    onPermBanWithResult = onPermBanWithResult,
                    onUnbanUserWithResult = onUnbanUserWithResult,
                    onGrantBadgeResult = onGrantBadgeResult,
                    onRevokeBadgeResult = onRevokeBadgeResult,
                    onStripAllBadges = onStripAllBadges,
                    onPardonAll = onPardonAll,
                    appCustomization = appCustomization,
                    results = results,
                    onPublishAnnouncement = onPublishAnnouncement,
                    onClearAnnouncement = onClearAnnouncement,
                    onPublishMatchResult = onPublishMatchResult,
                    onAwardPlayerStats = onAwardPlayerStats,
                    promoCodes = promoCodes,
                    bonusDrops = bonusDrops,
                    communityPolls = communityPolls,
                    strictRules = strictRules,
                    auditSummaryText = auditSummaryText,
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
                    onRefreshCloudState = onRefreshCloudState,
                    onUpdateAppBranding = onUpdateAppBranding,
                    onUpdateFinancialSettings = onUpdateFinancialSettings,
                    onToggleMaintenanceMode = onToggleMaintenanceMode,
                    onUpdateExistingMatch = onUpdateExistingMatch,
                    onDeleteMatch = onDeleteMatch,
                    onAddStrictRule = onAddStrictRule,
                    onDeleteStrictRule = onDeleteStrictRule,
                    onResetDefaultRules = onResetDefaultRules,
                    onResetAppForFreshDownload = onResetAppForFreshDownload
                )
            }
        }
    }
}

@Composable
private fun OwnerAuthGate(
    ownerKeyInput: String,
    onKeyChange: (String) -> Unit,
    unlockError: String?,
    onUnlock: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(FlameYellow.copy(alpha = 0.15f))
                .border(1.5.dp, FlameYellow, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = FlameYellow,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "👑 ওনার ভেরিফিকেশন",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = FlameYellow
        )
        Text(
            text = "TRUSTED TOURNAMENT - OWNER ONLY",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🔒 কঠোর নিরাপত্তা নোটিশ:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = FlameYellow
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "এই প্যানেলে শুধুমাত্র অ্যাপের মূল মালিক (Owner) প্রবেশ করতে পারবেন। সাধারণ প্লেয়ার বা এমনকি সাধারণ অ্যাডমিনও এখানে ঢুকতে পারবে না। ওনার হিসেবে নিজেকে ভেরিফাই করতে আপনার মাস্টার কী বা পিন প্রবেশ করান।",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "আপনার গোপন ওনার পাসকি বা পিন দিয়ে ভেরিফাই করুন",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = VictoryGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = ownerKeyInput,
            onValueChange = onKeyChange,
            label = { Text("ওনার মাস্টার পাসকি বা পিন") },
            placeholder = { Text("গোপন পাসকি বা পিন লিখুন") },
            leadingIcon = {
                Icon(Icons.Filled.Key, contentDescription = null, tint = FlameYellow)
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            isError = unlockError != null,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("owner_passkey_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlameYellow,
                unfocusedBorderColor = DarkBorder,
                focusedLabelColor = FlameYellow,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        if (unlockError != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = unlockError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("বন্ধ করুন", color = Color.White)
            }
            Button(
                onClick = onUnlock,
                modifier = Modifier
                    .weight(1.5f)
                    .testTag("owner_unlock_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("আনলক করুন", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun OwnerDashboardContent(
    allUsers: List<AppUser>,
    allBookings: List<SlotBooking>,
    matches: List<TournamentMatch>,
    depositRequests: List<DepositRequest>,
    withdrawRequests: List<WithdrawRequest>,
    onDismiss: () -> Unit,
    onBanUser: (userIdOrPhone: String, reason: String) -> Unit,
    onUnbanUser: (userIdOrPhone: String) -> Unit,
    onPromoteToAdmin: (target: String, passcode: String, name: String?, ign: String?, uid: String?) -> Unit,
    onDemoteAdmin: (userIdOrPhone: String) -> Unit,
    onUpdateMasterPin: (newPin: String) -> Boolean,
    onCancelSlotAndRefund: (bookingId: String) -> Unit,
    onCancelLegacySlot: (matchId: String, slotNumber: Int) -> Pair<Boolean, String>,
    onAddManualSlot: (matchId: String, slotNumber: Int, ign: String, uid: String, phone: String, feePaid: Int) -> Pair<Boolean, String>,
    onUpdateMatchSlotCapacity: (matchId: String, newCapacity: Int) -> Pair<Boolean, String>,
    onCancelMatchAndRefundAll: (matchId: String) -> Unit,
    onAdjustUserBalance: (target: String, amount: Int, isAdd: Boolean, reason: String) -> Boolean,
    onIncrementUserMatchesPlayed: (target: String, delta: Int) -> Boolean,
    onApproveDeposit: (requestId: String) -> Boolean,
    onRejectDeposit: (requestId: String) -> Boolean,
    onApproveWithdraw: (requestId: String) -> Boolean,
    onRejectWithdraw: (requestId: String) -> Boolean,
    onGrantBadge: (target: String, badge: BadgeType) -> Boolean,
    onRevokeBadge: (target: String, badge: BadgeType) -> Boolean,
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
    onPermBanWithResult: (targetKey: String, reason: String) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onUnbanUserWithResult: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    onGrantBadgeResult: (targetKey: String, badge: BadgeType) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onRevokeBadgeResult: (targetKey: String, badge: BadgeType) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onStripAllBadges: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    onPardonAll: (targetKey: String) -> Pair<Boolean, String> = { false to "" },
    appCustomization: AppCustomization = AppCustomization(),
    results: List<MatchResult> = emptyList(),
    onPublishAnnouncement: (title: String, message: String, isUrgent: Boolean) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    onClearAnnouncement: () -> Pair<Boolean, String> = { false to "" },
    onPublishMatchResult: (matchId: String, winnerTargetKey: String, winnerKills: Int, customPrizeAmount: Int?, runnerUpIgn: String, markMatchFinished: Boolean) -> Pair<Boolean, String> = { _, _, _, _, _, _ -> false to "" },
    onAwardPlayerStats: (targetKey: String, killsToAdd: Int, winsToAdd: Int, matchesToAdd: Int, prizeAmount: Int, note: String) -> Pair<Boolean, String> = { _, _, _, _, _, _ -> false to "" },
    promoCodes: List<PromoCodeItem> = emptyList(),
    bonusDrops: List<BonusGiftDrop> = emptyList(),
    communityPolls: List<LiveCommunityPoll> = emptyList(),
    strictRules: List<TournamentRuleItem> = emptyList(),
    auditSummaryText: String = "",
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
    onRefreshCloudState: () -> String = { "রিফ্রেশ সম্পন্ন!" },
    onUpdateAppBranding: (String, String, AppLogoOption, String, String) -> Unit = { _, _, _, _, _ -> },
    onUpdateFinancialSettings: (String, String, String, Int, Int) -> Unit = { _, _, _, _, _ -> },
    onToggleMaintenanceMode: (Boolean, String) -> Unit = { _, _ -> },
    onUpdateExistingMatch: (TournamentMatch) -> Boolean = { false },
    onDeleteMatch: (String) -> Boolean = { false },
    onAddStrictRule: (String, String, String, String) -> Boolean = { _, _, _, _ -> false },
    onDeleteStrictRule: (String) -> Boolean = { false },
    onResetDefaultRules: () -> Unit = {},
    onResetAppForFreshDownload: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val pendingBadgeAppealsCount = remember(badgeAppeals) { badgeAppeals.count { it.status == AppealStatus.PENDING } }
    val pendingDepWdrCount = remember(depositRequests, withdrawRequests) {
        depositRequests.count { it.status == RequestStatus.PENDING } +
            withdrawRequests.count { it.status == RequestStatus.PENDING }
    }
    val bannedUsersCount = remember(allUsers) { allUsers.count { it.isBanned } }
    val requestsTabTitle = if (pendingDepWdrCount > 0) "💳 ডিপোজিট/উইথড্র ($pendingDepWdrCount)" else "💳 ডিপোজিট/উইথড্র"
    val badgeTabTitle = if (pendingBadgeAppealsCount > 0) "🏅 ব্যাজ কন্ট্রোল ($pendingBadgeAppealsCount)" else "🏅 ব্যাজ কন্ট্রোল"
    val tabs = listOf(
        "💰 ব্যালেন্স",
        requestsTabTitle,
        badgeTabTitle,
        "🛡️ মডারেশন",
        "🚫 ফ্রড ব্যান",
        "🛡️ অ্যাডমিন নিয়োগ",
        "🎟️ স্লট ও রিফান্ড",
        "🏆 রেজাল্ট ও প্রাইজ",
        "📢 ঘোষণা",
        "📖 ওনার গাইড",
        "⚙️ সেটিংস",
        "🎁 ড্রপ ও পোল",
        "🎛️ অ্যাপ স্টুডিও"
    )
    val tabTags = listOf(
        "owner_tab_balance",
        "owner_tab_requests",
        "owner_tab_badges",
        "owner_tab_moderation",
        "owner_tab_ban",
        "owner_tab_admins",
        "owner_tab_slots",
        "owner_tab_results",
        "owner_tab_announcement",
        "owner_tab_guide",
        "owner_tab_settings",
        "owner_tab_fun_controls",
        "owner_tab_owner_studio"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Fire & Water Top Header + Live Command Overview Strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(FireWaterHeaderGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(FlameYellow.copy(alpha = 0.16f))
                                .border(1.2.dp, FlameYellow.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.badge_owner_logo),
                                contentDescription = "Official Owner Crown",
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "TH ওনার কন্ট্রোল প্যানেল",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = DisplayFontFamily,
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = FlameYellow
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = VictoryGreen.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.55f))
                                ) {
                                    Text(
                                        text = "LIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = MonoFontFamily,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        ),
                                        color = VictoryGreen,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "ব্যালেন্স, ব্যাজ, ডিপোজিট/উইথড্র ও সম্পূর্ণ নিয়ন্ত্রণ",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // 4-Tile Command Overview Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("owner_command_overview"),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OwnerOverviewTile(
                        label = "মোট ইউজার",
                        value = "${allUsers.size}",
                        accent = FlameCyan,
                        modifier = Modifier.weight(1f)
                    )
                    OwnerOverviewTile(
                        label = "পেন্ডিং রিকোয়েস্ট",
                        value = "${pendingDepWdrCount + pendingBadgeAppealsCount}",
                        accent = if (pendingDepWdrCount + pendingBadgeAppealsCount > 0) FlameYellow else VictoryGreen,
                        modifier = Modifier.weight(1f)
                    )
                    OwnerOverviewTile(
                        label = "মোট বুকিং",
                        value = "${allBookings.size}",
                        accent = VictoryGreen,
                        modifier = Modifier.weight(1f)
                    )
                    OwnerOverviewTile(
                        label = "ব্যানড ইউজার",
                        value = "$bannedUsersCount",
                        accent = if (bannedUsersCount > 0) FireRed else Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 8.dp,
            containerColor = DarkSurface,
            contentColor = FlameYellow,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = FlameYellow,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag(tabTags[index]),
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (selectedTab == index) FlameYellow else Color.White.copy(alpha = 0.6f)
                        )
                    }
                )
            }
        }

        HorizontalDivider(color = DarkBorder)

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(DarkSurface)
        ) {
            when (selectedTab) {
                0 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    item {
                        UserBalanceControlContent(
                            allUsers = allUsers,
                            onAdjustUserBalance = onAdjustUserBalance,
                            onIncrementUserMatchesPlayed = onIncrementUserMatchesPlayed
                        )
                    }
                }
                1 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    item {
                        DepositWithdrawQueueContent(
                            depositRequests = depositRequests,
                            withdrawRequests = withdrawRequests,
                            onApproveDeposit = onApproveDeposit,
                            onRejectDeposit = onRejectDeposit,
                            onApproveWithdraw = onApproveWithdraw,
                            onRejectWithdraw = onRejectWithdraw
                        )
                    }
                }
                2 -> BadgeManagementTab(
                    users = allUsers,
                    onGrantBadge = onGrantBadge,
                    onRevokeBadge = onRevokeBadge,
                    badgeAppeals = badgeAppeals,
                    onApproveBadgeAppeal = onApproveBadgeAppeal,
                    onDenyBadgeAppeal = onDenyBadgeAppeal,
                    onSendAppealChat = onSendAppealChat
                )
                3 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    item {
                        ModerationPanelSection(
                            actorRole = UserRole.OWNER,
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
                            onPermBanUser = onPermBanWithResult,
                            onUnbanUser = onUnbanUserWithResult,
                            onGrantBadge = onGrantBadgeResult,
                            onRevokeBadge = onRevokeBadgeResult,
                            onStripAllBadges = onStripAllBadges,
                            onPardonAll = onPardonAll
                        )
                    }
                }
                4 -> FraudBanTab(
                    users = allUsers,
                    onBanUser = onBanUser,
                    onUnbanUser = onUnbanUser
                )
                5 -> AdminManagementTab(
                    users = allUsers,
                    onPromoteToAdmin = onPromoteToAdmin,
                    onDemoteAdmin = onDemoteAdmin
                )
                6 -> SlotRefundControlTab(
                    bookings = allBookings,
                    matches = matches,
                    onCancelSlotAndRefund = onCancelSlotAndRefund,
                    onCancelLegacySlot = onCancelLegacySlot,
                    onAddManualSlot = onAddManualSlot,
                    onUpdateMatchSlotCapacity = onUpdateMatchSlotCapacity,
                    onCancelMatchAndRefundAll = onCancelMatchAndRefundAll
                )
                7 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    item {
                        OwnerMatchResultAndCommandSection(
                            matches = matches,
                            allBookings = allBookings,
                            allUsers = allUsers,
                            results = results,
                            onPublishMatchResult = onPublishMatchResult,
                            onAwardPlayerStats = onAwardPlayerStats
                        )
                    }
                }
                8 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    item {
                        LiveAnnouncementControlSection(
                            actorRole = UserRole.OWNER,
                            appCustomization = appCustomization,
                            onPublishAnnouncement = onPublishAnnouncement,
                            onClearAnnouncement = onClearAnnouncement
                        )
                    }
                }
                9 -> OwnerInstructionsGuideTab()
                10 -> OwnerSettingsTab(
                    onUpdateMasterPin = onUpdateMasterPin,
                    onResetAppForFreshDownload = {
                        onResetAppForFreshDownload()
                        onDismiss()
                    }
                )
                11 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    item {
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
                            onBroadcastMatchToChat = onBroadcastMatchToChat
                        )
                    }
                }
                12 -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    item {
                        OwnerCommandAndPromoSection(
                            appCustomization = appCustomization,
                            matches = matches,
                            strictRules = strictRules,
                            auditSummaryText = auditSummaryText,
                            onRefreshCloudState = onRefreshCloudState,
                            onUpdateAppBranding = onUpdateAppBranding,
                            onUpdateFinancialSettings = onUpdateFinancialSettings,
                            onToggleMaintenanceMode = onToggleMaintenanceMode,
                            onUpdateExistingMatch = onUpdateExistingMatch,
                            onDeleteMatch = onDeleteMatch,
                            onAddStrictRule = onAddStrictRule,
                            onDeleteStrictRule = onDeleteStrictRule,
                            onResetDefaultRules = onResetDefaultRules
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 1: FRAUD USER BAN MANAGER
// ==========================================
@Composable
private fun FraudBanTab(
    users: List<AppUser>,
    onBanUser: (userIdOrPhone: String, reason: String) -> Unit,
    onUnbanUser: (userIdOrPhone: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var userToBan by remember { mutableStateOf<AppUser?>(null) }
    var manualPhoneInput by remember { mutableStateOf("") }
    var manualReasonInput by remember { mutableStateOf("") }

    val filteredUsers = remember(users, searchQuery) {
        if (searchQuery.isBlank()) users
        else users.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phone.contains(searchQuery) ||
                    it.uid.contains(searchQuery) ||
                    it.ign.contains(searchQuery, ignoreCase = true) ||
                    it.bkashNumber.contains(searchQuery)
        }
    }

    val bannedCount = users.count { it.isBanned }
    val activeCount = users.count { !it.isBanned && it.role != UserRole.OWNER }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(
                    title = "মোট ইউজার",
                    value = "${users.size} জন",
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    title = "সক্রিয় প্লেয়ার",
                    value = "$activeCount জন",
                    color = VictoryGreen,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    title = "ব্যানড ফ্রড",
                    value = "$bannedCount জন",
                    color = FireRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("নাম, ফোন, UID বা bKash দিয়ে খুঁজুন...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = FlameYellow) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        // Manual Direct Ban Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Block, contentDescription = null, tint = FireRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "সরাসরি নম্বর/UID দিয়ে ফ্রড ব্যান করুন",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = manualPhoneInput,
                        onValueChange = { manualPhoneInput = it },
                        placeholder = { Text("প্রতারকের ফোন নম্বর বা Free Fire UID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = manualReasonInput,
                        onValueChange = { manualReasonInput = it },
                        placeholder = { Text("ব্যান করার কারণ (যেমন: ভুয়া TrxID প্রতারণা)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (manualPhoneInput.isNotBlank()) {
                                onBanUser(manualPhoneInput.trim(), manualReasonInput.trim())
                                manualPhoneInput = ""
                                manualReasonInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FireRed),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("তাত্ক্ষণিক অ্যাপ ব্যান কার্যকর করুন", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "ইউজার তালিকা (${filteredUsers.size})",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Users List
        items(filteredUsers, key = { it.id }) { user ->
            UserBanCard(
                user = user,
                onInitiateBan = { userToBan = user },
                onUnban = { onUnbanUser(user.phone) }
            )
        }
    }

    // Ban Confirmation Dialog with presets
    if (userToBan != null) {
        val target = userToBan!!
        var selectedReason by remember { mutableStateOf("ভুয়া বিকাশ TrxID / পেমেন্ট প্রতারণা") }
        var customReason by remember { mutableStateOf("") }

        val presetReasons = listOf(
            "ভুয়া বিকাশ TrxID / পেমেন্ট প্রতারণা",
            "Free Fire হ্যাক বা স্ক্রিপ্ট ব্যবহার",
            "ভুয়া রুম কোড ছড়ানো বা স্প্যাম",
            "গ্রুপে গালাগালি বা অশোভন আচরণ"
        )

        AlertDialog(
            onDismissRequest = { userToBan = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Block, contentDescription = null, tint = FireRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ইউজার ব্যান নিশ্চিতকরণ",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "আপনি কি নিশ্চিতভাবে ${target.name} (${target.phone}) কে ব্যান করতে চান? ব্যান করলে সে আর অ্যাপে ঢুকতে বা স্লট কিনতে পারবে না।",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ব্যান করার সুনির্দিষ্ট কারণ নির্বাচন করুন:",
                        fontWeight = FontWeight.Bold,
                        color = FlameYellow,
                        style = MaterialTheme.typography.labelSmall
                    )
                    presetReasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                        ) {
                            RadioButton(
                                selected = selectedReason == reason,
                                onClick = { selectedReason = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = FlameYellow)
                            )
                            Text(reason, style = MaterialTheme.typography.bodySmall, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customReason,
                        onValueChange = { customReason = it },
                        placeholder = { Text("অন্য কারণ থাকলে লিখুন...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameYellow,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalReason = if (customReason.isNotBlank()) customReason.trim() else selectedReason
                        onBanUser(target.phone, finalReason)
                        userToBan = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FireRed)
                ) {
                    Text("হ্যাঁ, চিরতরে ব্যান করুন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToBan = null }) {
                    Text("বাতিল", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun UserBanCard(
    user: AppUser,
    onInitiateBan: () -> Unit,
    onUnban: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isBanned) FireRed.copy(alpha = 0.12f) else DarkCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (user.isBanned) FireRed else DarkBorder
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (user.role) {
                            UserRole.OWNER -> FlameYellow.copy(alpha = 0.2f)
                            UserRole.ADMIN -> FlameOrange.copy(alpha = 0.2f)
                            UserRole.PLAYER -> Color.White.copy(alpha = 0.1f)
                        }
                    ) {
                        Text(
                            text = "${user.role.badge} ${user.role.banglaTitle}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (user.role) {
                                UserRole.OWNER -> FlameYellow
                                UserRole.ADMIN -> FlameOrange
                                UserRole.PLAYER -> Color.White.copy(alpha = 0.8f)
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (user.isBanned) FireRed else VictoryGreen
                ) {
                    Text(
                        text = if (user.isBanned) "🔴 ব্যানড" else "🟢 সক্রিয়",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "মোবাইল: ${user.phone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Free Fire IGN: ${user.ign} (UID: ${user.uid})",
                        style = MaterialTheme.typography.bodySmall,
                        color = FlameYellow
                    )
                    Text(
                        text = "bKash: ${user.bkashNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            if (user.isBanned && user.banReason != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = FireRed.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🚫 ব্যান কারণ: ${user.banReason} (${user.bannedAt ?: "সম্প্রতি"})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            if (user.role != UserRole.OWNER) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString("${user.phone} | UID: ${user.uid}"))
                            Toast.makeText(context, "তথ্য কপি হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    if (user.isBanned) {
                        Button(
                            onClick = onUnban,
                            colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ব্যান প্রত্যাহার (Unban)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onInitiateBan,
                            colors = ButtonDefaults.buttonColors(containerColor = FireRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Filled.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ব্যান করুন (BAN)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 2: ADMIN MANAGEMENT TAB
// ==========================================
@Composable
private fun AdminManagementTab(
    users: List<AppUser>,
    onPromoteToAdmin: (target: String, passcode: String, name: String?, ign: String?, uid: String?) -> Unit,
    onDemoteAdmin: (userIdOrPhone: String) -> Unit
) {
    var adminName by remember { mutableStateOf("") }
    var adminPhone by remember { mutableStateOf("") }
    var adminIgn by remember { mutableStateOf("") }
    var adminUid by remember { mutableStateOf("") }
    var adminPasscode by remember { mutableStateOf("TH-ADMIN-2026") }
    val context = LocalContext.current

    val currentAdmins = users.filter { it.role == UserRole.ADMIN || it.role == UserRole.OWNER }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Create Admin Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PersonAdd, contentDescription = null, tint = FlameYellow)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "নতুন টুর্নামেন্ট অ্যাডমিন নিয়োগ করুন",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = FlameYellow
                        )
                    }
                    Text(
                        text = "আপনার বিশ্বস্ত কাউকে অ্যাডমিন বানান যাতে সে রুম আইডি ও পাসওয়ার্ড প্লেয়ারদের কাছে পাঠাতে পারে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = adminName,
                        onValueChange = { adminName = it },
                        label = { Text("অ্যাডমিনের নাম (উদা: রাকিব আহমেদ)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameYellow,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = adminPhone,
                        onValueChange = { adminPhone = it },
                        label = { Text("অ্যাডমিনের মোবাইল নম্বর (উদা: 017xxxxxxxx)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameYellow,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = adminIgn,
                            onValueChange = { adminIgn = it },
                            label = { Text("FF IGN (ঐচ্ছিক)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FlameYellow,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = adminUid,
                            onValueChange = { adminUid = it },
                            label = { Text("FF UID (ঐচ্ছিক)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FlameYellow,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = adminPasscode,
                        onValueChange = { adminPasscode = it },
                        label = { Text("অ্যাডমিন সিক্রেট পিন (যাতে সে অ্যাডমিন প্যানেল চালাতে পারে)") },
                        leadingIcon = { Icon(Icons.Filled.VpnKey, contentDescription = null, tint = FlameYellow) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameYellow,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (adminPhone.isBlank()) {
                                Toast.makeText(context, "ফোন নম্বর দেয়া আবশ্যক!", Toast.LENGTH_SHORT).show()
                            } else {
                                onPromoteToAdmin(
                                    adminPhone.trim(),
                                    adminPasscode.trim(),
                                    adminName.ifBlank { "টুর্নামেন্ট অ্যাডমিন" },
                                    adminIgn.ifBlank { "TH★ADMIN" },
                                    adminUid.ifBlank { "9876543210" }
                                )
                                adminName = ""
                                adminPhone = ""
                                adminIgn = ""
                                adminUid = ""
                                Toast.makeText(context, "🛡️ অ্যাডমিন সফলভাবে নিয়োগ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameYellow),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🚀 অ্যাডমিন হিসেবে নিয়োগ দিন", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "বর্তমান অ্যাডমিন ও ওনার তালিকা (${currentAdmins.size})",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow
            )
        }

        items(currentAdmins, key = { it.id }) { admin ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (admin.role == UserRole.OWNER) FlameYellow else DarkBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = admin.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (admin.role == UserRole.OWNER) FlameYellow else FlameOrange
                            ) {
                                Text(
                                    text = if (admin.role == UserRole.OWNER) "👑 সুপ্রিম ওনার" else "🛡️ অ্যাডমিন",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ফোন: ${admin.phone} | IGN: ${admin.ign}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        if (admin.role == UserRole.ADMIN) {
                            Text(
                                text = "অ্যাডমিন পিন: ${admin.adminPasscode ?: "TH-ADMIN-2026"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = VictoryGreen
                            )
                        }
                    }

                    if (admin.role == UserRole.ADMIN) {
                        OutlinedButton(
                            onClick = { onDemoteAdmin(admin.phone) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FireRed),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FireRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("অব্যাহতি", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 3: OWNER STEP-BY-STEP INSTRUCTIONS GUIDE
// ==========================================
@Composable
private fun OwnerInstructionsGuideTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "📖 ওনার হ্যান্ডবুক ও সিস্টেম নিয়মাবলী",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = FlameYellow
                    )
                    Text(
                        text = "আপনি যেভাবে অ্যাপের একমাত্র ওনার হবেন এবং পুরো সিস্টেম একা পরিচালনা করবেন:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            GuideStepCard(
                stepNumber = "১",
                title = "আপনি নিজে কীভাবে একাই ওনার (Owner) হবেন?",
                description = "আপনি অ্যাপ ডাউনলোড করার পর আপনার পারসোনাল ফোন নম্বর (যেমন: 01861583924) দিয়ে লগইন বা রেজিস্টার করবেন। এরপর ওনার প্যানেলে প্রবেশের সময় ওনার মাস্টার পাসকি (TH-OWNER-7788) বা মাস্টার পিন (7788) প্রবেশ করাবেন। একবার প্রবেশ করলেই অ্যাপে আপনার প্রোফাইল '👑 অ্যাপ ওনার (মালিক)' হিসেবে চূড়ান্তভাবে ভেরিফাইড হয়ে যাবে। সাধারণ কোনো প্লেয়ার এই মাস্টার পাসকি জানবে না, তাই পুরো অ্যাপে আপনি ছাড়া অন্য কেউ ওনার হতে পারবে না।"
            )
        }

        item {
            GuideStepCard(
                stepNumber = "২",
                title = "প্রতারক ও ফ্রড ইউজারদের কীভাবে ব্যান করবেন?",
                description = "অনেক সময় অসাধু প্লেয়ার বিকাশ পেমেন্ট না করে ভুয়া TrxID দিয়ে স্লট বুক করার চেষ্টা করে, অথবা টুর্নামেন্টে হ্যাক/স্ক্রিপ্ট ব্যবহার করে। 'ফ্রড ব্যান' ট্যাবে গিয়ে তাদের ফোন নম্বর, UID বা নাম সার্চ করুন। এরপর 'ব্যান করুন (BAN)' বাটনে চাপ দিয়ে কারণ নির্বাচন করুন (যেমন: ভুয়া পেমেন্ট বা হ্যাকিং)। সাথে সাথে ওই ইউজার চিরতরে অ্যাপ থেকে ব্লক হয়ে যাবে এবং সে আর কখনোই কোনো ম্যাচে স্লট বুক করতে পারবে না।"
            )
        }

        item {
            GuideStepCard(
                stepNumber = "৩",
                title = "নতুন অ্যাডমিন কীভাবে বানাবেন এবং তাদের ক্ষমতা কতটুকু?",
                description = "টুর্নামেন্টে একসাথে অনেক ম্যাচ থাকলে একা রুম কোড দেওয়া কষ্টকর হতে পারে। তাই আপনার বিশ্বস্ত বন্ধু বা ভাইদের 'অ্যাডমিন নিয়োগ' ট্যাবে গিয়ে তাদের নাম, ফোন নম্বর এবং একটি সিক্রেট অ্যাডমিন পিন দিয়ে অ্যাডমিন বানিয়ে দিন।\n\n⚠️ মনে রাখবেন:\n• অ্যাডমিন কেবল রুম আইডি ও পাসওয়ার্ড প্লেয়ারদের পাঠাতে পারবে।\n• অ্যাডমিন কোনো প্লেয়ারকে ব্যান করতে পারবে না।\n• অ্যাডমিন ওনার প্যানেলে ঢুকতে পারবে না বা অন্য কাউকে অ্যাডমিন বানাতে পারবে না!\n• আপনি চাইলে যেকোনো সময় 'অব্যাহতি' বাটনে এক ট্যাপে তাদের অ্যাডমিন পদ কেড়ে নিতে পারবেন।"
            )
        }

        item {
            GuideStepCard(
                stepNumber = "৪",
                title = "ওনার সিকিউরিটি ও গোপনীয়তা রক্ষার নিয়ম",
                description = "• আপনার মাস্টার পাসকি (TH-OWNER-7788) বা পিন (7788) কোনো সাধারণ প্লেয়ারকে কখনোই জানাবেন না।\n• ওনার প্যানেলে প্রবেশাধিকার শুধুই আপনার জন্য সংরক্ষিত।\n• টুর্নামেন্টের বিকাশ নম্বর (01861583924) সবসময় নিজের নিয়ন্ত্রণে রাখুন।"
            )
        }
    }
}

@Composable
private fun GuideStepCard(
    stepNumber: String,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = FlameYellow,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stepNumber,
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun OwnerOverviewTile(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.Black.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = MonoFontFamily,
                    fontWeight = FontWeight.Black
                ),
                color = accent,
                maxLines = 1
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = Color.White.copy(alpha = 0.75f),
                maxLines = 1
            )
        }
    }
}

// ==========================================
// TAB 4: OWNER MASTER SETTINGS
// ==========================================
@Composable
private fun OwnerSettingsTab(
    onUpdateMasterPin: (newPin: String) -> Boolean,
    onResetAppForFreshDownload: () -> Unit = {}
) {
    var newPinInput by remember { mutableStateOf("") }
    var showFreshResetConfirm by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🔐 ওনার মাস্টার পিন পরিবর্তন",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = FlameYellow
                )
                Text(
                    text = "ওনার প্যানেলে দ্রুত আনলক করার জন্য নিজের গোপন পিন সেট করুন (যেমন: 7788)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = newPinInput,
                    onValueChange = { newPinInput = it },
                    label = { Text("নতুন মাস্টার পিন (কমপক্ষে ৪ সংখ্যা)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (newPinInput.length >= 4) {
                            val success = onUpdateMasterPin(newPinInput.trim())
                            if (success) {
                                Toast.makeText(context, "পিন আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
                                newPinInput = ""
                            }
                        } else {
                            Toast.makeText(context, "পিন কমপক্ষে ৪ সংখ্যার হতে হবে", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameYellow),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("মাস্টার পিন সেভ করুন", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = VictoryGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "অ্যাপ স্ট্যাটাস ও সিস্টেম হেলথ",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("• সার্ভার স্ট্যাটাস: সচল ও সুরক্ষিত 🟢", style = MaterialTheme.typography.bodySmall, color = VictoryGreen)
                Text("• বিকাশ মার্চেন্ট নম্বর: 01861583924", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                Text("• প্রতারক সুরক্ষা: অটোমেটিক ব্লকিং অ্যাক্টিভ 🛡️", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                Text("• ম্যাচ সময়সূচী: বিকাল ৫:০০ - রাত ৮:০০ (সুপারফাস্ট এক্সপ্রেস)", style = MaterialTheme.typography.bodySmall, color = FlameYellow)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("owner_prepare_download_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.2.dp, FireWaterBorderGradient)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = null, tint = WaterCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🔄 রিসেট ও ডাউনলোডের জন্য প্রস্তুত করুন",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = FlameYellow
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "APK/AAB এক্সপোর্ট বা শেয়ার করার আগে সব টেস্ট ইউজার, পেন্ডিং রিকোয়েস্ট, বুকিং ও অস্থায়ী ডেটা মুছে একদম ফ্রেশ ডাউনলোড স্টেটে ফিরিয়ে নিন:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.82f),
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("✓ ওনার প্রোফাইল ক্লিন ও ভেরিফাইড থাকবে", style = MaterialTheme.typography.labelSmall, color = VictoryGreen)
                Text("✓ ৮টি জিরো-টলারেন্স টুর্নামেন্ট রুলস সক্রিয় থাকবে", style = MaterialTheme.typography.labelSmall, color = VictoryGreen)
                Text("✓ সব ম্যাচ ০ স্লট ও টেস্ট লেনদেন সম্পূর্ণ ক্লিয়ার হবে", style = MaterialTheme.typography.labelSmall, color = VictoryGreen)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { showFreshResetConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("owner_fresh_reset_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FireRed, contentColor = Color.White)
                ) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ফ্রেশ রিসেট ও ডাউনলোড রেডি করুন", fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }
    }

    if (showFreshResetConfirm) {
        AlertDialog(
            onDismissRequest = { showFreshResetConfirm = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "ফ্রেশ রিসেট নিশ্চিত করবেন?",
                    fontWeight = FontWeight.Black,
                    color = FlameYellow
                )
            },
            text = {
                Text(
                    text = "সব টেস্ট প্লেয়ার, স্লট বুকিং, ডিপোজিট/উইথড্র রিকোয়েস্ট এবং লিডারবোর্ড মুছে অ্যাপটি ডাউনলোড-রেডি অবস্থায় রিসেট হয়ে যাবে।",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFreshResetConfirm = false
                        onResetAppForFreshDownload()
                        Toast.makeText(context, "🔄 অ্যাপ ডাউনলোড-রেডি ফ্রেশ অবস্থায় রিসেট হয়েছে!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FireRed, contentColor = Color.White)
                ) {
                    Text("হ্যাঁ, রিসেট করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFreshResetConfirm = false }) {
                    Text("বাতিল", color = Color.White.copy(alpha = 0.7f))
                }
            }
        )
    }
}

@Composable
private fun StatChip(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
            Text(value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black), color = color)
        }
    }
}

// ==========================================
// TAB 3: SLOT ADD, CAPACITY, REMOVE & AUTOMATIC REFUND
// ==========================================
@Composable
private fun SlotRefundControlTab(
    bookings: List<SlotBooking>,
    matches: List<TournamentMatch>,
    onCancelSlotAndRefund: (bookingId: String) -> Unit,
    onCancelLegacySlot: (matchId: String, slotNumber: Int) -> Pair<Boolean, String>,
    onAddManualSlot: (matchId: String, slotNumber: Int, ign: String, uid: String, phone: String, feePaid: Int) -> Pair<Boolean, String>,
    onUpdateMatchSlotCapacity: (matchId: String, newCapacity: Int) -> Pair<Boolean, String>,
    onCancelMatchAndRefundAll: (matchId: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var bookingToCancel by remember { mutableStateOf<SlotBooking?>(null) }
    var legacySlotToCancel by remember { mutableStateOf<Pair<TournamentMatch, Int>?>(null) }
    var matchToCancel by remember { mutableStateOf<TournamentMatch?>(null) }
    var feedbackBanner by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    var selectedMatchId by remember(matches) {
        mutableStateOf(matches.firstOrNull()?.id ?: "")
    }
    val selectedMatch = remember(matches, selectedMatchId) {
        matches.firstOrNull { it.id == selectedMatchId } ?: matches.firstOrNull()
    }

    val matchBookingsMap = remember(bookings, selectedMatch) {
        if (selectedMatch == null) emptyMap()
        else bookings.filter { it.matchId == selectedMatch.id }.associateBy { it.slotNumber }
    }
    val occupiedSlotSet = remember(selectedMatch, matchBookingsMap) {
        if (selectedMatch == null) emptySet()
        else {
            val set = matchBookingsMap.keys.toMutableSet()
            var candidate = 1
            while (set.size < selectedMatch.bookedSlots && candidate <= selectedMatch.totalSlots) {
                if (candidate !in set) set.add(candidate)
                candidate++
            }
            set.toSet()
        }
    }
    val firstFreeSlot = remember(selectedMatch, occupiedSlotSet) {
        if (selectedMatch == null) 1
        else (1..selectedMatch.totalSlots).firstOrNull { it !in occupiedSlotSet } ?: 1
    }

    var slotNumInput by remember(selectedMatch?.id, firstFreeSlot) {
        mutableStateOf(firstFreeSlot.toString())
    }
    var playerIgnInput by remember { mutableStateOf("") }
    var playerUidInput by remember { mutableStateOf("") }
    var playerPhoneInput by remember { mutableStateOf("") }

    val filteredBookings = remember(bookings, searchQuery) {
        if (searchQuery.isBlank()) bookings
        else bookings.filter {
            it.playerIgn.contains(searchQuery, ignoreCase = true) ||
                    it.playerUid.contains(searchQuery, ignoreCase = true) ||
                    it.matchTitle.contains(searchQuery, ignoreCase = true) ||
                    it.bkashSenderNumber.contains(searchQuery, ignoreCase = true) ||
                    it.trxId.contains(searchQuery, ignoreCase = true) ||
                    it.slotNumber.toString() == searchQuery.trim()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("owner_slot_control_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Fire & Water Slot Control Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.5.dp, FireWaterBorderGradient)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FireWaterHeaderGradient)
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FlameOrange.copy(alpha = 0.22f))
                                .border(1.dp, WaterCyan.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎟️", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ফায়ার ও ওয়াটার স্লট অ্যাড, রিমুভ ও রিফান্ড কন্ট্রোল",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = FlameYellow
                            )
                            Text(
                                text = "ম্যানুয়াল স্লট যোগ • ক্যাপাসিটি +/− • রিমুভ করলে প্লেয়ারের ওয়ালেটে ১০০% রিফান্ড • লাইভ ক্লাউড সিঙ্ক",
                                style = MaterialTheme.typography.labelSmall,
                                color = WaterCyan
                            )
                        }
                    }
                }
            }
        }

        // Action Feedback Banner
        feedbackBanner?.let { (isOk, msg) ->
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isOk) VictoryGreen.copy(alpha = 0.16f) else FireRed.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, if (isOk) VictoryGreen else FireRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("owner_slot_feedback_banner")
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    )
                }
            }
        }

        // Match Selector + Slot Capacity Adjuster + Interactive Grid + Slot Add
        if (matches.isNotEmpty() && selectedMatch != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "১) ম্যাচ নির্বাচন করুন (${matches.size}টি):",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = FlameYellow
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        matches.forEach { m ->
                            val isSelected = m.id == selectedMatch.id
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFF1F1209) else DarkCard,
                                border = BorderStroke(
                                    1.2.dp,
                                    if (isSelected) WaterCyan else DarkBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedMatchId = m.id
                                        feedbackBanner = null
                                    }
                                    .testTag("owner_slot_match_chip_${m.id}")
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                    Text(
                                        text = m.title,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) FlameYellow else Color.White
                                    )
                                    Text(
                                        text = "স্লট: ${m.bookedSlots}/${m.totalSlots} • ফি ৳${m.entryFee}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) WaterCyan else Color.White.copy(alpha = 0.65f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Capacity +/- Control Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, WaterCyan.copy(alpha = 0.45f))
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🎟️ মোট স্লট ক্যাপাসিটি (+ / −)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = WaterCyan
                                )
                                Text(
                                    text = "বর্তমান বুকড: ${selectedMatch.bookedSlots} • মোট স্লট: ${selectedMatch.totalSlots}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val next = (selectedMatch.totalSlots - 2).coerceAtLeast(selectedMatch.bookedSlots.coerceAtLeast(2))
                                        feedbackBanner = onUpdateMatchSlotCapacity(selectedMatch.id, next)
                                    },
                                    enabled = selectedMatch.totalSlots > selectedMatch.bookedSlots.coerceAtLeast(2),
                                    colors = ButtonDefaults.buttonColors(containerColor = FireRed),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("owner_slot_capacity_dec_btn")
                                ) {
                                    Icon(Icons.Filled.Remove, contentDescription = "Decrease slots", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text("-২", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Text(
                                    text = "${selectedMatch.totalSlots}",
                                    fontFamily = MonoFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FlameYellow,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                Button(
                                    onClick = {
                                        val next = (selectedMatch.totalSlots + 2).coerceAtMost(100)
                                        feedbackBanner = onUpdateMatchSlotCapacity(selectedMatch.id, next)
                                    },
                                    enabled = selectedMatch.totalSlots < 100,
                                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("owner_slot_capacity_inc_btn")
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Increase slots", tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Text("+২", fontWeight = FontWeight.Black, color = Color.Black)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(2, 12, 24, 48, 52, 60).forEach { preset ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedMatch.totalSlots == preset) FlameYellow else DarkSurface,
                                    border = BorderStroke(1.dp, if (selectedMatch.totalSlots == preset) FlameYellow else DarkBorder),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            feedbackBanner = onUpdateMatchSlotCapacity(selectedMatch.id, preset)
                                        }
                                ) {
                                    Text(
                                        text = "$preset স্লট",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (selectedMatch.totalSlots == preset) Color.Black else Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Visual Slot Grid (Tap green = pick for Add, Tap red = Remove & Refund)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.45f))
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.GridView, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "লাইভ স্লট গ্রিড (সবুজ=যোগ, লাল=রিমুভ)",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "${occupiedSlotSet.size}/${selectedMatch.totalSlots}",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = WaterCyan,
                                fontSize = 12.sp
                            )
                        }

                        val slotRows = (1..selectedMatch.totalSlots).toList().chunked(6)
                        slotRows.forEach { rowSlots ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowSlots.forEach { slotNum ->
                                    val booking = matchBookingsMap[slotNum]
                                    val isOccupied = slotNum in occupiedSlotSet
                                    val isChosen = slotNumInput.toIntOrNull() == slotNum && !isOccupied
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .clip(RoundedCornerShape(9.dp))
                                            .background(
                                                when {
                                                    isOccupied -> Color(0xFF3A1019)
                                                    isChosen -> Color(0xFF0A3242)
                                                    else -> Color(0xFF0D2820)
                                                }
                                            )
                                            .border(
                                                width = if (isChosen) 1.5.dp else 1.dp,
                                                color = when {
                                                    isOccupied -> FireRed
                                                    isChosen -> WaterCyan
                                                    else -> VictoryGreen.copy(alpha = 0.65f)
                                                },
                                                shape = RoundedCornerShape(9.dp)
                                            )
                                            .clickable {
                                                if (isOccupied) {
                                                    if (booking != null) {
                                                        bookingToCancel = booking
                                                    } else {
                                                        legacySlotToCancel = selectedMatch to slotNum
                                                    }
                                                } else {
                                                    slotNumInput = slotNum.toString()
                                                    feedbackBanner = null
                                                }
                                            }
                                            .testTag("owner_slot_tile_$slotNum"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "#$slotNum",
                                                fontFamily = MonoFontFamily,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isOccupied) Color(0xFFFCA5A5) else if (isChosen) WaterCyan else VictoryGreen
                                            )
                                            Text(
                                                text = if (isOccupied) (booking?.playerIgn?.take(4) ?: "রিমুভ") else "ফাঁকা",
                                                fontSize = 8.sp,
                                                color = Color.White.copy(alpha = 0.85f),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                                repeat(6 - rowSlots.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Manual Slot Add Form Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.55f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AddCircle, contentDescription = null, tint = VictoryGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "➕ স্লট অ্যাড প্যানেল (${selectedMatch.title})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = VictoryGreen
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = slotNumInput,
                                onValueChange = { slotNumInput = it.filter { c -> c.isDigit() } },
                                label = { Text("স্লট #") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(0.32f)
                                    .testTag("owner_add_slot_num_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WaterCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = playerIgnInput,
                                onValueChange = { playerIgnInput = it },
                                label = { Text("প্লেয়ার IGN") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(0.68f)
                                    .testTag("owner_add_slot_ign_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WaterCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = playerUidInput,
                                onValueChange = { playerUidInput = it },
                                label = { Text("গেম UID") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(0.5f)
                                    .testTag("owner_add_slot_uid_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WaterCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            OutlinedTextField(
                                value = playerPhoneInput,
                                onValueChange = { playerPhoneInput = it },
                                label = { Text("ফোন / বিকাশ") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(0.5f)
                                    .testTag("owner_add_slot_phone_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WaterCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                        Button(
                            onClick = {
                                val parsedSlot = slotNumInput.toIntOrNull() ?: firstFreeSlot
                                val res = onAddManualSlot(
                                    selectedMatch.id,
                                    parsedSlot,
                                    playerIgnInput.ifBlank { "VIP_PLAYER" },
                                    playerUidInput.ifBlank { "0000000000" },
                                    playerPhoneInput,
                                    selectedMatch.entryFee
                                )
                                feedbackBanner = res
                                if (res.first) {
                                    playerIgnInput = ""
                                    playerUidInput = ""
                                    playerPhoneInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("owner_add_slot_btn")
                        ) {
                            Icon(Icons.Filled.AddCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "✅ স্লট #${slotNumInput.ifBlank { firstFreeSlot.toString() }} যোগ ও ক্লাউড সিঙ্ক করুন",
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Stats Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(
                    title = "মোট বুকিং",
                    value = "${bookings.size} টি",
                    color = FlameYellow,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    title = "জমা ফি",
                    value = "৳${bookings.sumOf { it.feePaid }}",
                    color = VictoryGreen,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    title = "অটো-রিফান্ড",
                    value = "ইন্সট্যান্ট",
                    color = FlameOrange,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("প্লেয়ার IGN, UID, TrxID বা স্লট নম্বর খুঁজুন") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = FlameYellow)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Color.White)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_slot_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedLabelColor = FlameYellow,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "বুকড স্লট তালিকা (${filteredBookings.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                if (filteredBookings.isNotEmpty()) {
                    Text(
                        text = "১-ক্লিকে রিমুভ ও রিফান্ড",
                        style = MaterialTheme.typography.labelSmall,
                        color = FlameOrange
                    )
                }
            }
        }

        // Empty State
        if (filteredBookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎮", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "বর্তমানে কোনো বুকড স্লট নেই" else "খোঁজার সাথে কোনো বুকিং মেলেনি",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "প্লেয়াররা যখন টুর্নামেন্টে স্লট কিনবে, সেগুলো এখানে তালিকাভুক্ত হবে।",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            // Bookings List
            items(filteredBookings, key = { it.bookingId }) { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_card_${booking.bookingId}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Match Title and Slot Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = booking.matchTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "সময়: ${booking.matchDateTime}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.65f)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FlameOrange.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange)
                            ) {
                                Text(
                                    text = "স্লট #${booking.slotNumber}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = FlameYellow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = DarkBorder
                        )

                        // Player details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "প্লেয়ার IGN:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = booking.playerIgn,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "গেম UID:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = booking.playerUid,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = VictoryGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Payment Details
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.35f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ফি: ৳${booking.feePaid} • বিকাশ: ${booking.bkashSenderNumber}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = "Trx: ${booking.trxId}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = BkashPink
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Cancel & Auto-Refund Button
                        Button(
                            onClick = { bookingToCancel = booking },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cancel_slot_btn_${booking.bookingId}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Cancel,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🚫 স্লট বাতিল ও অটো রিফান্ড (+৳${booking.feePaid})",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Full Match Cancellation & All-Refund Option
        val activeMatchesWithBookings = matches.filter { it.bookedSlots > 0 }
        if (activeMatchesWithBookings.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Block,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "⚠️ সম্পূর্ণ ম্যাচ বাতিল ও সকল প্লেয়ারকে রিফান্ড",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFFCA5A5)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "কোনো ম্যাচ বাতিল হলে সকল বুকড প্লেয়ারের টাকা ১-ক্লিকে তাদের ওয়ালেটে ফেরত দেওয়া যাবে।",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        activeMatchesWithBookings.forEach { match ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.Black.copy(alpha = 0.4f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = match.title,
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            text = "বুকড: ${match.bookedSlots}/${match.totalSlots} স্লট • ফি: ৳${match.entryFee}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = FlameYellow
                                        )
                                    }
                                    OutlinedButton(
                                        onClick = { matchToCancel = match },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color(0xFFEF4444)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("ম্যাচ বাতিল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Single Slot Cancellation & Automatic Refund
    bookingToCancel?.let { booking ->
        AlertDialog(
            onDismissRequest = { bookingToCancel = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "স্লট বাতিল ও অটো-রিফান্ড",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "আপনি কি নিশ্চিত যে প্লেয়ার '${booking.playerIgn}' এর স্লট #${booking.slotNumber} বাতিল করতে চান?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🎮 ম্যাচ: ${booking.matchTitle}", style = MaterialTheme.typography.labelMedium, color = FlameYellow)
                            Text("👤 প্লেয়ার IGN: ${booking.playerIgn} (UID: ${booking.playerUid})", style = MaterialTheme.typography.labelSmall, color = Color.White)
                            Text("💰 রিফান্ড পরিমাণ: ৳${booking.feePaid}", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = VictoryGreen)
                            Text("⚡ রিফান্ড মাধ্যম: প্লেয়ার ওয়ালেটে অটো-ক্রেডিট", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                    Text(
                        text = "⚠️ নিশ্চিত করলে স্লটটি তৎক্ষণাৎ মুক্ত হবে এবং প্লেয়ারের অ্যাকাউন্টে স্বয়ংক্রিয়ভাবে ৳${booking.feePaid} রিফান্ড ট্রানজেকশন যোগ হবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFCA5A5)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelSlotAndRefund(booking.bookingId)
                        feedbackBanner = true to "✅ ${booking.playerIgn}-এর স্লট #${booking.slotNumber} বাতিল ও রিফান্ড হয়েছে!"
                        bookingToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_refund_btn")
                ) {
                    Text("হ্যাঁ, বাতিল ও রিফান্ড করুন", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { bookingToCancel = null }) {
                    Text("ফিরে যান", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Confirmation Dialog for Legacy / Occupied Slot Removal
    legacySlotToCancel?.let { (match, slotNum) ->
        AlertDialog(
            onDismissRequest = { legacySlotToCancel = null },
            title = {
                Text(
                    text = "স্লট #$slotNum রিমুভ করবেন?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = FireRed
                )
            },
            text = {
                Text(
                    text = "ম্যাচ: ${match.title}\nস্লট #$slotNum খালি ও রিসেট করা হবে।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        feedbackBanner = onCancelLegacySlot(match.id, slotNum)
                        legacySlotToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("হ্যাঁ, স্লট রিমুভ করুন", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { legacySlotToCancel = null }) {
                    Text("ফিরে যান", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Confirmation Dialog for Full Match Cancellation & All-Refund
    matchToCancel?.let { match ->
        AlertDialog(
            onDismissRequest = { matchToCancel = null },
            title = {
                Text(
                    text = "সম্পূর্ণ ম্যাচ বাতিল করবেন?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFEF4444)
                )
            },
            text = {
                Column {
                    Text(
                        text = "ম্যাচ: ${match.title}\n\nএই ম্যাচের সকল বুকড স্লটের প্লেয়ারের টাকা (মোট ${match.bookedSlots} টি স্লট) স্বয়ংক্রিয়ভাবে তাদের ওয়ালেটে ফেরত দেওয়া হবে এবং ম্যাচ বাতিল করা হবে।",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelMatchAndRefundAll(match.id)
                        matchToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("হ্যাঁ, ম্যাচ বাতিল ও সবাইকে রিফান্ড করুন", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { matchToCancel = null }) {
                    Text("ফিরে যান", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// ==========================================
// TAB: OWNER BADGE CONTROL (GRANT & REVOKE)
// ==========================================
@Composable
private fun BadgeManagementTab(
    users: List<AppUser>,
    onGrantBadge: (target: String, badge: BadgeType) -> Boolean,
    onRevokeBadge: (target: String, badge: BadgeType) -> Boolean,
    badgeAppeals: List<BadgeAppeal> = emptyList(),
    onApproveBadgeAppeal: (appealId: String, selectedBadge: BadgeType) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onDenyBadgeAppeal: (appealId: String, reason: String) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onSendAppealChat: (appealId: String, text: String, proofImageUri: String?) -> Pair<Boolean, String> = { _, _, _ -> false to "" }
) {
    var targetInput by remember { mutableStateOf("") }
    var userSearchQuery by remember { mutableStateOf("") }
    var feedbackBanner by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val cleanTarget = targetInput.trim()
    val matchedUser = remember(users, cleanTarget) {
        if (cleanTarget.isBlank()) null
        else users.find {
            it.id == cleanTarget ||
                it.phone == cleanTarget ||
                it.uid == cleanTarget ||
                it.bkashNumber == cleanTarget ||
                it.ign.equals(cleanTarget, ignoreCase = true)
        }
    }

    val filteredUsers = remember(users, userSearchQuery) {
        if (userSearchQuery.isBlank()) users
        else users.filter {
            it.name.contains(userSearchQuery, ignoreCase = true) ||
                it.phone.contains(userSearchQuery) ||
                it.uid.contains(userSearchQuery) ||
                it.ign.contains(userSearchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("owner_badge_manage_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Overview Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.MilitaryTech,
                            contentDescription = null,
                            tint = FlameYellow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🏅 ওনার ব্যাজ কন্ট্রোল (ব্যাজ দিন ও নিন)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = FlameYellow
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "যেকোনো প্লেয়ার বা অ্যাডমিনের ফোন নম্বর, UID অথবা IGN নির্বাচন করে যেকোনো ব্যাজ দিতে বা তুলে নিতে পারবেন। নিচের তালিকা থেকে প্লেয়ার ট্যাপ করলেও সিলেক্ট হবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Badge Appeals Queue (Accept -> Select Badge OR Deny -> +1 Warn / 7-day Ban)
        item {
            BadgeAppealsReviewSection(
                appeals = badgeAppeals,
                allUsers = users,
                onApproveAppeal = onApproveBadgeAppeal,
                onDenyAppeal = onDenyBadgeAppeal,
                onSendAppealChat = onSendAppealChat
            )
        }

        // Target User Input Field
        item {
            OutlinedTextField(
                value = targetInput,
                onValueChange = {
                    targetInput = it
                    feedbackBanner = null
                },
                label = { Text("টার্গেট প্লেয়ার (ফোন নম্বর, UID বা IGN)") },
                placeholder = { Text("যেমন: 017xxxxxxxx বা IGN") },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = FlameYellow)
                },
                trailingIcon = {
                    if (targetInput.isNotEmpty()) {
                        IconButton(onClick = {
                            targetInput = ""
                            feedbackBanner = null
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Color.White)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_badge_target_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedLabelColor = FlameYellow,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        // Quick-Pick Player Chips Row (1-tap select without scrolling)
        if (users.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    users.forEach { user ->
                        val isChipSelected = matchedUser?.id == user.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isChipSelected) FlameYellow else DarkCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isChipSelected) FlameYellow else DarkBorder
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    targetInput = user.phone
                                    feedbackBanner = null
                                }
                                .testTag("owner_badge_quick_chip_${user.phone}")
                        ) {
                            Text(
                                text = "${user.ign} (${user.effectiveBadges.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isChipSelected) Color.Black else Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Selected / Matched User Active Badges Card
        if (matchedUser != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("owner_badge_selected_user_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF172030)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "সিলেক্টেড: ${matchedUser.name}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "IGN: ${matchedUser.ign} • ফোন: ${matchedUser.phone} • UID: ${matchedUser.uid}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FlameYellow
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = VictoryGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${matchedUser.effectiveBadges.size}টি ব্যাজ",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = VictoryGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        val activeBadges = matchedUser.effectiveBadges
                        if (activeBadges.isEmpty()) {
                            Text(
                                text = "বর্তমানে এই ইউজারের কোনো সক্রিয় ব্যাজ নেই।",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                activeBadges.forEach { badge ->
                                    BadgePill(badge = badge, compact = true)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Feedback Banner
        feedbackBanner?.let { (isSuccess, msg) ->
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSuccess) VictoryGreen.copy(alpha = 0.16f) else FireRed.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSuccess) VictoryGreen else FireRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("owner_badge_feedback_banner")
                ) {
                    Text(
                        text = msg,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Section: All 9 Badges with Grant & Revoke Actions
        item {
            Text(
                text = "ব্যাজ নির্বাচন করুন (${BadgeType.values().size}টি ব্যাজ — দিন বা তুলে নিন):",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow
            )
        }

        items(BadgeType.values().toList(), key = { it.name }) { badge ->
            val badgeColor = Color(badge.hexColor)
            val userHasBadge = matchedUser?.effectiveBadges?.contains(badge) == true

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("owner_badge_card_${badge.name}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (userHasBadge) badgeColor.copy(alpha = 0.13f) else DarkCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (userHasBadge) 1.5.dp else 1.dp,
                    color = if (userHasBadge) badgeColor else DarkBorder
                )
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
                            BadgeEmblemArt(badge = badge, size = 42.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = badgeColor
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    BadgePill(badge = badge, compact = true)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = badgeColor.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = badge.tierLabel,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black
                                            ),
                                            color = badgeColor,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (matchedUser != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (userHasBadge) VictoryGreen.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = if (userHasBadge) "সক্রিয় ✓" else "নেই",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (userHasBadge) VictoryGreen else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "🎯 শর্ত: ${badge.criteria}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = FlameYellow,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val ok = onGrantBadge(cleanTarget, badge)
                                feedbackBanner = if (ok) {
                                    true to "✅ '${badge.title}' ব্যাজটি সফলভাবে দেওয়া হয়েছে!"
                                } else {
                                    false to "⚠️ দয়া করে সঠিক প্লেয়ারের ফোন, UID বা IGN লিখুন।"
                                }
                            },
                            enabled = cleanTarget.isNotBlank() && !userHasBadge,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VictoryGreen,
                                disabledContainerColor = VictoryGreen.copy(alpha = 0.25f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("grant_badge_btn_${badge.name}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (userHasBadge) "দেওয়া আছে" else "ব্যাজ দিন",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val ok = onRevokeBadge(cleanTarget, badge)
                                feedbackBanner = if (ok) {
                                    true to "🗑️ '${badge.title}' ব্যাজটি প্লেয়ারের থেকে তুলে নেওয়া হয়েছে!"
                                } else {
                                    false to "⚠️ এই প্লেয়ারের কাছে '${badge.title}' ব্যাজটি নেই বা প্লেয়ার পাওয়া যায়নি।"
                                }
                            },
                            enabled = cleanTarget.isNotBlank() && (matchedUser == null || userHasBadge),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = FireRed,
                                disabledContentColor = Color.White.copy(alpha = 0.3f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (cleanTarget.isNotBlank() && (matchedUser == null || userHasBadge)) FireRed else DarkBorder
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("revoke_badge_btn_${badge.name}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.RemoveCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ব্যাজ নিন",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Quick-Pick Registered Users List
        item {
            HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "দ্রুত প্লেয়ার নির্বাচন করুন (${filteredUsers.size} জন):",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = userSearchQuery,
                onValueChange = { userSearchQuery = it },
                placeholder = { Text("তালিকা থেকে নাম, ফোন বা IGN খুঁজুন...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = FlameYellow) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        items(filteredUsers, key = { "badge_user_${it.id}" }) { user ->
            val isSelected = matchedUser?.id == user.id
            val badges = user.effectiveBadges

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        targetInput = user.phone
                        feedbackBanner = null
                    }
                    .testTag("owner_badge_user_row_${user.phone}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) FlameYellow.copy(alpha = 0.14f) else DarkCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) FlameYellow else DarkBorder
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${user.name} (${user.ign})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "ফোন: ${user.phone} • UID: ${user.uid}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) FlameYellow else FlameOrange.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isSelected) "সিলেক্টেড ✓" else "সিলেক্ট",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.Black else FlameYellow,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    if (badges.isEmpty()) {
                        Text(
                            text = "কোনো ব্যাজ নেই",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            badges.forEach { b ->
                                BadgePill(badge = b, compact = true)
                            }
                        }
                    }
                }
            }
        }
    }
}


