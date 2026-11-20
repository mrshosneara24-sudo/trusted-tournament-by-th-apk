package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppUser
import com.example.model.BadgeType
import com.example.model.ModerationActionType
import com.example.model.ModerationLogEntry
import com.example.model.UserRole
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.EmberGold
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.theme.FireWaterBorderGradient
import com.example.ui.theme.FireWaterHeaderGradient
import com.example.ui.theme.IceMist
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterCyan

enum class TimeoutDurationUnit(val banglaLabel: String, val multiplierMinutes: Long) {
    MINUTES("মিনিট", 1L),
    HOURS("ঘন্টা", 60L),
    DAYS("দিন", 24L * 60L)
}

@Composable
fun ModerationPanelSection(
    actorRole: UserRole,
    allUsers: List<AppUser>,
    moderationLogs: List<ModerationLogEntry>,
    onWarnUser: (targetKey: String, reason: String) -> Pair<Boolean, String>,
    onClearWarnings: (targetKey: String) -> Pair<Boolean, String>,
    onTimeoutUser: (targetKey: String, durationMinutes: Long, reason: String) -> Pair<Boolean, String>,
    onRemoveTimeout: (targetKey: String) -> Pair<Boolean, String>,
    onMuteChat: (targetKey: String, durationHours: Long, reason: String) -> Pair<Boolean, String>,
    onUnmuteChat: (targetKey: String) -> Pair<Boolean, String>,
    onSetMatchBan: (targetKey: String, banned: Boolean, reason: String) -> Pair<Boolean, String>,
    onSetWalletFreeze: (targetKey: String, frozen: Boolean, reason: String) -> Pair<Boolean, String>,
    onTempBanUser: (targetKey: String, days: Int, reason: String) -> Pair<Boolean, String>,
    onPermBanUser: (targetKey: String, reason: String) -> Pair<Boolean, String>,
    onUnbanUser: (targetKey: String) -> Pair<Boolean, String>,
    onGrantBadge: (targetKey: String, badge: BadgeType) -> Pair<Boolean, String>,
    onRevokeBadge: (targetKey: String, badge: BadgeType) -> Pair<Boolean, String>,
    onStripAllBadges: (targetKey: String) -> Pair<Boolean, String>,
    onPardonAll: (targetKey: String) -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var targetInput by remember { mutableStateOf("") }
    var reasonInput by remember { mutableStateOf("টুর্নামেন্ট নীতিমালা লঙ্ঘন") }
    var timeoutAmountInput by remember { mutableStateOf("1") }
    var timeoutUnit by remember { mutableStateOf(TimeoutDurationUnit.HOURS) }
    var muteHours by remember { mutableLongStateOf(24L) }
    var tempBanDays by remember { mutableIntStateOf(3) }
    var feedbackBanner by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val cleanTarget = targetInput.trim()
    val nowMillis = System.currentTimeMillis()

    val orderedUsers = remember(allUsers) {
        allUsers.sortedWith(
            compareBy<AppUser> { it.role == UserRole.OWNER }
                .thenBy { it.role == UserRole.ADMIN }
                .thenBy { it.ign }
        )
    }

    val filteredUsers = remember(orderedUsers, searchQuery) {
        if (searchQuery.isBlank()) orderedUsers
        else orderedUsers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.ign.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery) ||
                it.uid.contains(searchQuery)
        }
    }

    val matchedUser = remember(allUsers, cleanTarget) {
        if (cleanTarget.isBlank()) null
        else allUsers.find {
            it.id == cleanTarget ||
                it.phone == cleanTarget ||
                it.uid == cleanTarget ||
                it.bkashNumber == cleanTarget ||
                it.ign.equals(cleanTarget, ignoreCase = true)
        }
    }

    val totalWarned = remember(allUsers) { allUsers.count { it.moderationWarningsCount > 0 } }
    val totalTimedOut = remember(allUsers) { allUsers.count { (it.timeoutUntilMillis ?: 0L) > nowMillis } }
    val totalMuted = remember(allUsers) { allUsers.count { it.isChatMuted } }
    val totalBanned = remember(allUsers) { allUsers.count { it.isBanned } }

    val calculatedTimeoutMinutes = remember(timeoutAmountInput, timeoutUnit) {
        val raw = timeoutAmountInput.trim().toLongOrNull() ?: 0L
        (raw * timeoutUnit.multiplierMinutes).coerceAtLeast(0L)
    }

    val quickReasons = listOf(
        "স্প্যাম ও গালিগালাজ",
        "ভুয়া বিকাশ TrxID",
        "টিমিং বা হ্যাক ব্যবহার",
        "স্লট নিয়ম ভঙ্গ",
        "মিথ্যা ব্যাজ আপিল"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("moderation_panel_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Fire & Water Moderation Command Center Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.2.dp, FireWaterBorderGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FireWaterHeaderGradient)
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(FireRed.copy(alpha = 0.32f), WaterBlue.copy(alpha = 0.32f)))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Gavel,
                                contentDescription = "Moderation Command",
                                tint = WaterCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ফায়ার & ওয়াটার মডারেশন কমান্ড",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Black
                                ),
                                color = EmberGold
                            )
                            Text(
                                text = if (actorRole == UserRole.OWNER) {
                                    "ওনার পূর্ণ ক্ষমতা • ব্যাজ, ওয়ার্ন, টাইমআউট ও ব্যান কন্ট্রোল"
                                } else {
                                    "অ্যাডমিন মড প্যানেল • প্লেয়ার ব্যাজ, ওয়ার্ন, টাইমআউট ও শাস্তি"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = IceMist
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (actorRole == UserRole.OWNER) FireOrange.copy(alpha = 0.22f) else WaterBlue.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, if (actorRole == UserRole.OWNER) EmberGold else WaterCyan)
                    ) {
                        Text(
                            text = if (actorRole == UserRole.OWNER) "👑 OWNER" else "🛡️ ADMIN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = if (actorRole == UserRole.OWNER) EmberGold else WaterCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ModMiniStatPill("ওয়ার্নড", "$totalWarned", EmberGold, Modifier.weight(1f))
                    ModMiniStatPill("টাইমআউট", "$totalTimedOut", WaterCyan, Modifier.weight(1f))
                    ModMiniStatPill("মিউট", "$totalMuted", FireOrange, Modifier.weight(1f))
                    ModMiniStatPill("ব্যানড", "$totalBanned", FireRed, Modifier.weight(1f))
                }
            }
        }

        // 2. Target User Search + Quick Chips + Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "১. টার্গেট ইউজার নির্বাচন করুন",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = WaterCyan
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("নাম, IGN, ফোন বা UID দিয়ে ফিল্টার করুন...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = WaterCyan) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mod_search_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WaterCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                if (filteredUsers.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filteredUsers.forEach { user ->
                            val isSelected = matchedUser?.id == user.id
                            val isProtectedForAdmin = actorRole == UserRole.ADMIN && user.role != UserRole.PLAYER
                            val chipBorder = when {
                                isSelected -> WaterCyan
                                user.isBanned -> FireRed
                                (user.timeoutUntilMillis ?: 0L) > nowMillis -> FireOrange
                                else -> DarkBorder
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isSelected -> WaterBlue.copy(alpha = 0.28f)
                                    isProtectedForAdmin -> DarkElevated.copy(alpha = 0.65f)
                                    else -> DarkElevated
                                },
                                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, chipBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        targetInput = user.phone
                                        feedbackBanner = null
                                    }
                                    .testTag("mod_quick_chip_${user.phone}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${user.role.badge} ${user.ign}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) WaterCyan else TextPrimary
                                    )
                                    if (user.moderationWarningsCount > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "⚠${user.moderationWarningsCount}/7",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmberGold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = targetInput,
                    onValueChange = {
                        targetInput = it
                        feedbackBanner = null
                    },
                    label = { Text("ফোন নম্বর, FF UID বা IGN") },
                    placeholder = { Text("উদা: 017xxxxxxxx বা TH_Player") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = FireOrange) },
                    trailingIcon = {
                        if (targetInput.isNotEmpty()) {
                            IconButton(onClick = {
                                targetInput = ""
                                feedbackBanner = null
                            }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear", tint = TextPrimary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mod_target_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FireOrange,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = EmberGold,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Matched User Punishment Status Summary
                if (matchedUser != null) {
                    val isTimedOutNow = (matchedUser.timeoutUntilMillis ?: 0L) > nowMillis
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkElevated,
                        border = BorderStroke(1.dp, WaterCyan.copy(alpha = 0.55f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mod_selected_user_card")
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
                                        text = "${matchedUser.role.badge} ${matchedUser.name} (${matchedUser.ign})",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "ফোন: ${matchedUser.phone} • UID: ${matchedUser.uid} • ব্যালেন্স: ৳${matchedUser.walletBalance}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = IceMist
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (matchedUser.isBanned) FireRed.copy(alpha = 0.25f) else VictoryGreen.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (matchedUser.isBanned) "🚫 ব্যানড" else "🟢 সক্রিয়",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (matchedUser.isBanned) ErrorRed else VictoryGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Active Sanctions Badges Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SanctionTagPill("ওয়ার্ন ${matchedUser.moderationWarningsCount}/7", if (matchedUser.moderationWarningsCount >= 5) FireRed else EmberGold)
                                if (isTimedOutNow) {
                                    SanctionTagPill("⏳ টাইমআউট চালু", WaterCyan)
                                }
                                if (matchedUser.isChatMuted) {
                                    SanctionTagPill("🔇 চ্যাট মিউট", FireOrange)
                                }
                                if (matchedUser.isMatchBanned) {
                                    SanctionTagPill("🎯 ম্যাচ ব্যান", FireRed)
                                }
                                if (matchedUser.isWalletFrozen) {
                                    SanctionTagPill("❄️ ওয়ালেট ফ্রিজ", WaterBlue)
                                }
                            }

                            val activeBadges = matchedUser.effectiveBadges
                            if (activeBadges.isNotEmpty()) {
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
        }

        // Live Feedback Banner
        feedbackBanner?.let { (isOk, msg) ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isOk) VictoryGreen.copy(alpha = 0.18f) else FireRed.copy(alpha = 0.2f),
                border = BorderStroke(1.2.dp, if (isOk) VictoryGreen else FireRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mod_feedback_banner")
            ) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                )
            }
        }

        // 3. Reason & Quick Violation Chips
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "২. শাস্তির কারণ লিখুন বা বাছাই করুন",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = EmberGold
                )
                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = { reasonInput = it },
                    label = { Text("কারণ / মডারেশন নোট") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mod_reason_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmberGold,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickReasons.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (reasonInput == preset) FireOrange.copy(alpha = 0.25f) else DarkElevated,
                            border = BorderStroke(1.dp, if (reasonInput == preset) FireOrange else DarkBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { reasonInput = preset }
                        ) {
                            Text(
                                text = preset,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Warn Escalation Section (7 Warns -> Auto 10 Days App Ban)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, EmberGold.copy(alpha = 0.55f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val warnsCount = matchedUser?.moderationWarningsCount ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = EmberGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ওয়ার্নিং সিস্টেম (৭ ওয়ার্নে ১০ দিন অ্যাপ ব্যান)",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                            color = EmberGold
                        )
                    }
                    Text(
                        text = "$warnsCount / 7",
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Black,
                        color = if (warnsCount >= 6) FireRed else EmberGold
                    )
                }

                // 7-Step Visual Fire Progress Meter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (step in 1..7) {
                        val active = warnsCount >= step
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    when {
                                        active && step >= 6 -> FireRed
                                        active -> FireOrange
                                        else -> DarkElevated
                                    }
                                )
                        )
                    }
                }

                Text(
                    text = "১টি ওয়ার্ন করে বাড়বে — ঠিক ৭ নম্বর ওয়ার্নিং পেলেই ইউজার স্বয়ংক্রিয়ভাবে ১০ দিনের জন্য অ্যাপ থেকে ব্যান হয়ে যাবে!",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (cleanTarget.isBlank()) {
                                feedbackBanner = false to "⚠️ আগে উপরে টার্গেট প্লেয়ার সিলেক্ট করুন!"
                            } else {
                                feedbackBanner = onWarnUser(cleanTarget, reasonInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(42.dp)
                            .testTag("mod_warn_btn")
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+১ ওয়ার্ন দিন", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            if (cleanTarget.isBlank()) {
                                feedbackBanner = false to "⚠️ আগে উপরে টার্গেট প্লেয়ার সিলেক্ট করুন!"
                            } else {
                                feedbackBanner = onClearWarnings(cleanTarget)
                            }
                        },
                        border = BorderStroke(1.dp, WaterCyan),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WaterCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("mod_clear_warn_btn")
                    ) {
                        Text("ওয়ার্ন রিসেট", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // 5. Custom App Timeout with Live Blocking Countdown Timer Overlay
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.2.dp, WaterCyan.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = WaterCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⏳ কাস্টম অ্যাপ টাইমআউট (লাইভ লক টাইমার)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                        color = WaterCyan
                    )
                }
                Text(
                    text = "যেকোনো সময় (যেমন ১ ঘন্টা বা ইচ্ছেমতো মিনিট/ঘন্টা/দিন) টাইমআউট দিলে ইউজার পুরো স্ক্রিনে শুধু একটি টাইমার দেখবে — কোনো ট্যাপ বা বাটন কাজ করবে না!",
                    style = MaterialTheme.typography.labelSmall,
                    color = IceMist.copy(alpha = 0.9f)
                )

                // Preset Chips (5m, 15m, 30m, 1h, 6h, 12h, 24h)
                val presets = listOf(
                    5L to "৫মি",
                    15L to "১৫মি",
                    30L to "৩০মি",
                    60L to "১ ঘন্টা",
                    360L to "৬ ঘন্টা",
                    720L to "১২ ঘন্টা",
                    1440L to "২৪ ঘন্টা"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.forEach { (mins, label) ->
                        val isPresetSelected = calculatedTimeoutMinutes == mins
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isPresetSelected) WaterBlue.copy(alpha = 0.35f) else DarkElevated,
                            border = BorderStroke(1.dp, if (isPresetSelected) WaterCyan else DarkBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (mins >= 60 && mins % 60 == 0L) {
                                        timeoutUnit = TimeoutDurationUnit.HOURS
                                        timeoutAmountInput = (mins / 60L).toString()
                                    } else {
                                        timeoutUnit = TimeoutDurationUnit.MINUTES
                                        timeoutAmountInput = mins.toString()
                                    }
                                }
                                .testTag("mod_timeout_preset_$mins")
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isPresetSelected) WaterCyan else TextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Custom Numeric Duration + Unit Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = timeoutAmountInput,
                        onValueChange = { next ->
                            if (next.all { it.isDigit() } && next.length <= 4) {
                                timeoutAmountInput = next
                            }
                        },
                        label = { Text("ইচ্ছেমতো সময়") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mod_timeout_duration_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WaterCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TimeoutDurationUnit.entries.forEach { unit ->
                            val selected = timeoutUnit == unit
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) WaterCyan else DarkElevated,
                                border = BorderStroke(1.dp, if (selected) WaterCyan else DarkBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { timeoutUnit = unit }
                                    .testTag("mod_timeout_unit_${unit.name}")
                            ) {
                                Text(
                                    text = unit.banglaLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (selected) Color.Black else TextPrimary,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (cleanTarget.isBlank()) {
                                feedbackBanner = false to "⚠️ আগে উপরে টার্গেট প্লেয়ার সিলেক্ট করুন!"
                            } else if (calculatedTimeoutMinutes < 1L) {
                                feedbackBanner = false to "⚠️ কমপক্ষে ১ মিনিটের সঠিক সংখ্যা দিন!"
                            } else {
                                feedbackBanner = onTimeoutUser(cleanTarget, calculatedTimeoutMinutes, reasonInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.4f)
                            .height(42.dp)
                            .testTag("mod_apply_timeout_btn")
                    ) {
                        Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("টাইমআউট দিন", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            if (cleanTarget.isBlank()) {
                                feedbackBanner = false to "⚠️ আগে উপরে টার্গেট প্লেয়ার সিলেক্ট করুন!"
                            } else {
                                feedbackBanner = onRemoveTimeout(cleanTarget)
                            }
                        },
                        border = BorderStroke(1.dp, VictoryGreen),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = VictoryGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("mod_remove_timeout_btn")
                    ) {
                        Icon(Icons.Filled.LockOpen, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("তুলুন", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // 6. Comprehensive App-Handling Punishments Matrix
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, FireRed.copy(alpha = 0.55f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = FireRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚡ অ্যাপ হ্যান্ডলিং পানিশমেন্ট কন্ট্রোল",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                        color = FireOrange
                    )
                }

                // 6A. Global Chat & Appeal Mute
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🔇 গ্লোবাল ও আপিল চ্যাট মিউট", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1L to "১ ঘন্টা", 6L to "৬ ঘন্টা", 24L to "২৪ ঘন্টা", 0L to "আজীবন").forEach { (h, lbl) ->
                            val selected = muteHours == h
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) FireOrange.copy(alpha = 0.25f) else DarkElevated,
                                border = BorderStroke(1.dp, if (selected) FireOrange else DarkBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { muteHours = h }
                            ) {
                                Text(lbl, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) EmberGold else TextPrimary, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp))
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onMuteChat(cleanTarget, muteHours, reasonInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FireOrange),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f).height(38.dp).testTag("mod_chat_mute_btn")
                        ) {
                            Icon(Icons.Filled.VolumeOff, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("চ্যাট মিউট", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onUnmuteChat(cleanTarget)
                            },
                            border = BorderStroke(1.dp, VictoryGreen),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VictoryGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("mod_chat_unmute_btn")
                        ) {
                            Icon(Icons.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("আনমিউট", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                HorizontalDivider(color = DarkBorder)

                // 6B. Match Slot Entry Ban & Unban
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🎯 টুর্নামেন্ট ম্যাচ স্লট ব্যান (বুকিং বন্ধ)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onSetMatchBan(cleanTarget, true, reasonInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FireRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f).height(38.dp).testTag("mod_match_ban_btn")
                        ) {
                            Icon(Icons.Filled.SportsEsports, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("ম্যাচ স্লট ব্যান", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onSetMatchBan(cleanTarget, false, reasonInput)
                            },
                            border = BorderStroke(1.dp, VictoryGreen),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VictoryGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("mod_match_unban_btn")
                        ) {
                            Text("ব্যান তুলুন", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                HorizontalDivider(color = DarkBorder)

                // 6C. Wallet Freeze & Unfreeze
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("❄️ ওয়ালেট ফ্রিজ (ডিপোজিট / উইথড্র বন্ধ)", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onSetWalletFreeze(cleanTarget, true, reasonInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WaterBlue),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f).height(38.dp).testTag("mod_wallet_freeze_btn")
                        ) {
                            Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("ওয়ালেট ফ্রিজ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onSetWalletFreeze(cleanTarget, false, reasonInput)
                            },
                            border = BorderStroke(1.dp, EmberGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmberGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp).testTag("mod_wallet_unfreeze_btn")
                        ) {
                            Text("আনফ্রিজ", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                HorizontalDivider(color = DarkBorder)

                // 6D. Temp App Ban (1d/3d/7d/10d/30d) & Permanent Ban / Unban / Pardon All
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🚫 অস্থায়ী ও স্থায়ী অ্যাপ ব্যান", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 3, 7, 10, 30).forEach { days ->
                            val selected = tempBanDays == days
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selected) FireRed.copy(alpha = 0.28f) else DarkElevated,
                                border = BorderStroke(1.dp, if (selected) FireRed else DarkBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { tempBanDays = days }
                            ) {
                                Text("$days দিন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) ErrorRed else TextPrimary, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp))
                            }
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onTempBanUser(cleanTarget, tempBanDays, reasonInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FireRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("mod_temp_ban_btn")
                        ) {
                            Text("$tempBanDays দিন ব্যান", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onPermBanUser(cleanTarget, reasonInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("mod_perm_ban_btn")
                        ) {
                            Icon(Icons.Filled.Block, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("আজীবন ব্যান", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onUnbanUser(cleanTarget)
                            },
                            border = BorderStroke(1.dp, VictoryGreen),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VictoryGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("mod_unban_btn")
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ব্যান প্রত্যাহার", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ টার্গেট প্লেয়ার দিন!"
                                else onPardonAll(cleanTarget)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(40.dp).testTag("mod_pardon_all_btn")
                        ) {
                            Text("🌟 সব শাস্তি মওকুফ", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 7. Give Badge & Take Badge Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, EmberGold.copy(alpha = 0.5f))
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
                        Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = EmberGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🏅 ব্যাজ দিন ও ব্যাজ তুলে নিন",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                            color = EmberGold
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ আগে টার্গেট প্লেয়ার সিলেক্ট করুন!"
                            else onStripAllBadges(cleanTarget)
                        },
                        border = BorderStroke(1.dp, FireRed),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("mod_strip_badges_btn")
                    ) {
                        Icon(Icons.Filled.DeleteForever, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সব ব্যাজ নিন", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                BadgeType.entries.forEach { badge ->
                    val hasBadge = matchedUser?.effectiveBadges?.contains(badge) == true
                    val badgeAccent = Color(badge.hexColor)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (hasBadge) badgeAccent.copy(alpha = 0.12f) else DarkElevated,
                        border = BorderStroke(1.dp, if (hasBadge) badgeAccent.copy(alpha = 0.7f) else DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                BadgeEmblemArt(badge = badge, size = 30.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = badge.title,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = badgeAccent
                                    )
                                    Text(
                                        text = if (matchedUser == null) badge.tierLabel else if (hasBadge) "সক্রিয় ✓" else "ব্যাজ নেই",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (hasBadge) VictoryGreen else TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ আগে টার্গেট প্লেয়ার সিলেক্ট করুন!"
                                        else onGrantBadge(cleanTarget, badge)
                                    },
                                    enabled = !hasBadge,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VictoryGreen,
                                        disabledContainerColor = VictoryGreen.copy(alpha = 0.22f)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("mod_grant_badge_${badge.name}")
                                ) {
                                    Icon(Icons.Filled.AddCircle, contentDescription = "Give Badge", tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("দিন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = {
                                        feedbackBanner = if (cleanTarget.isBlank()) false to "⚠️ আগে টার্গেট প্লেয়ার সিলেক্ট করুন!"
                                        else onRevokeBadge(cleanTarget, badge)
                                    },
                                    enabled = matchedUser == null || hasBadge,
                                    border = BorderStroke(1.dp, if (matchedUser == null || hasBadge) FireRed else DarkBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = ErrorRed,
                                        disabledContentColor = TextSecondary.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("mod_revoke_badge_${badge.name}")
                                ) {
                                    Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Take Badge", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("নিন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 8. Moderation Audit Log
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mod_audit_log_list"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.History, contentDescription = null, tint = WaterCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "📜 মডারেশন অডিট লগ (${moderationLogs.size})",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                        color = WaterCyan
                    )
                }

                if (moderationLogs.isEmpty()) {
                    Text(
                        text = "এখনো কোনো মডারেশন অ্যাকশন রেকর্ড করা হয়নি।",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                } else {
                    moderationLogs.take(20).forEach { log ->
                        val accent = when (log.actionType) {
                            ModerationActionType.WARN -> EmberGold
                            ModerationActionType.TIMEOUT -> WaterCyan
                            ModerationActionType.CHAT_MUTE,
                            ModerationActionType.REWARD_BLOCK -> FireOrange
                            ModerationActionType.MATCH_BAN,
                            ModerationActionType.TEMP_BAN,
                            ModerationActionType.PERMANENT_BAN,
                            ModerationActionType.REMOVE_ALL_BADGES,
                            ModerationActionType.TAKE_BADGE,
                            ModerationActionType.APPEAL_BAN,
                            ModerationActionType.WALLET_FINE,
                            ModerationActionType.STATS_RESET -> FireRed
                            ModerationActionType.WALLET_FREEZE,
                            ModerationActionType.PROFILE_LOCK -> WaterBlue
                            ModerationActionType.CLEAR_WARNINGS,
                            ModerationActionType.REMOVE_TIMEOUT,
                            ModerationActionType.UNMUTE_CHAT,
                            ModerationActionType.UNBAN_MATCH,
                            ModerationActionType.UNFREEZE_WALLET,
                            ModerationActionType.UNBAN,
                            ModerationActionType.GIVE_BADGE,
                            ModerationActionType.UNBLOCK_REWARD,
                            ModerationActionType.UNBAN_APPEAL,
                            ModerationActionType.UNLOCK_PROFILE,
                            ModerationActionType.PARDON_ALL -> VictoryGreen
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkElevated,
                            border = BorderStroke(1.dp, accent.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${log.actionType.emoji} ${log.actionType.banglaLabel} • ${log.targetIgn}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = accent
                                    )
                                    if (log.durationLabel.isNotBlank()) {
                                        Text(
                                            text = log.durationLabel,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                            color = TextPrimary
                                        )
                                    }
                                }
                                if (log.reason.isNotBlank()) {
                                    Text(
                                        text = "কারণ: ${log.reason}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary.copy(alpha = 0.9f)
                                    )
                                }
                                Text(
                                    text = "দাতা: ${log.moderatorName} • ${log.timeLabel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModMiniStatPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkElevated,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = MonoFontFamily,
                    fontWeight = FontWeight.Black
                ),
                color = accent
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SanctionTagPill(text: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = accent.copy(alpha = 0.2f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.7f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
            color = accent,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
