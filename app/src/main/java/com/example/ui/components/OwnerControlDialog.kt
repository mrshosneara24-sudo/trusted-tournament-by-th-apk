package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppUser
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun OwnerControlDialog(
    userProfile: UserProfile,
    allUsers: List<AppUser>,
    allBookings: List<SlotBooking> = emptyList(),
    matches: List<TournamentMatch> = emptyList(),
    onDismiss: () -> Unit,
    onClaimOwner: (key: String) -> Boolean,
    onBanUser: (userIdOrPhone: String, reason: String) -> Unit,
    onUnbanUser: (userIdOrPhone: String) -> Unit,
    onPromoteToAdmin: (target: String, passcode: String, name: String?, ign: String?, uid: String?) -> Unit,
    onDemoteAdmin: (userIdOrPhone: String) -> Unit,
    onUpdateMasterPin: (newPin: String) -> Boolean,
    onCancelSlotAndRefund: (bookingId: String) -> Unit = {},
    onCancelMatchAndRefundAll: (matchId: String) -> Unit = {}
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
                .heightIn(max = 760.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, FlameYellow, RoundedCornerShape(24.dp))
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
                    onDismiss = onDismiss,
                    onBanUser = onBanUser,
                    onUnbanUser = onUnbanUser,
                    onPromoteToAdmin = onPromoteToAdmin,
                    onDemoteAdmin = onDemoteAdmin,
                    onUpdateMasterPin = onUpdateMasterPin,
                    onCancelSlotAndRefund = onCancelSlotAndRefund,
                    onCancelMatchAndRefundAll = onCancelMatchAndRefundAll
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
                        text = "ডিফল্ট মাস্টার পাসকি: TH-OWNER-7788 অথবা পিন: 7788",
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
            placeholder = { Text("TH-OWNER-7788 বা 7788") },
            leadingIcon = {
                Icon(Icons.Filled.Key, contentDescription = null, tint = FlameYellow)
            },
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
    onDismiss: () -> Unit,
    onBanUser: (userIdOrPhone: String, reason: String) -> Unit,
    onUnbanUser: (userIdOrPhone: String) -> Unit,
    onPromoteToAdmin: (target: String, passcode: String, name: String?, ign: String?, uid: String?) -> Unit,
    onDemoteAdmin: (userIdOrPhone: String) -> Unit,
    onUpdateMasterPin: (newPin: String) -> Boolean,
    onCancelSlotAndRefund: (bookingId: String) -> Unit,
    onCancelMatchAndRefundAll: (matchId: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "🚫 ফ্রড ব্যান",
        "🛡️ অ্যাডমিন নিয়োগ",
        "🎟️ স্লট ও রিফান্ড",
        "📖 ওনার গাইড",
        "⚙️ সেটিংস"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Royal Top Header
        Surface(
            color = DarkCard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FlameYellow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👑", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TH ওনার কন্ট্রোল প্যানেল",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = FlameYellow
                        )
                        Text(
                            text = "সর্বোচ্চ ক্ষমতা • প্রতারক ব্যান ও স্লট অটো-রিফান্ড",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
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
                0 -> FraudBanTab(
                    users = allUsers,
                    onBanUser = onBanUser,
                    onUnbanUser = onUnbanUser
                )
                1 -> AdminManagementTab(
                    users = allUsers,
                    onPromoteToAdmin = onPromoteToAdmin,
                    onDemoteAdmin = onDemoteAdmin
                )
                2 -> SlotRefundControlTab(
                    bookings = allBookings,
                    matches = matches,
                    onCancelSlotAndRefund = onCancelSlotAndRefund,
                    onCancelMatchAndRefundAll = onCancelMatchAndRefundAll
                )
                3 -> OwnerInstructionsGuideTab()
                4 -> OwnerSettingsTab(onUpdateMasterPin = onUpdateMasterPin)
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
                    color = BkashPink,
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
                        Icon(Icons.Filled.Block, contentDescription = null, tint = BkashPink, modifier = Modifier.size(18.dp))
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
                        colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
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
                    Icon(Icons.Filled.Block, contentDescription = null, tint = BkashPink)
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
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink)
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
            containerColor = if (user.isBanned) BkashPink.copy(alpha = 0.12f) else DarkCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (user.isBanned) BkashPink else DarkBorder
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
                    color = if (user.isBanned) BkashPink else VictoryGreen
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
                    color = BkashPink.copy(alpha = 0.2f),
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
                            colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
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
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BkashPink),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BkashPink),
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

// ==========================================
// TAB 4: OWNER MASTER SETTINGS
// ==========================================
@Composable
private fun OwnerSettingsTab(
    onUpdateMasterPin: (newPin: String) -> Boolean
) {
    var newPinInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
// TAB 3: SLOT CANCELLATION & AUTOMATIC REFUND
// ==========================================
@Composable
private fun SlotRefundControlTab(
    bookings: List<SlotBooking>,
    matches: List<TournamentMatch>,
    onCancelSlotAndRefund: (bookingId: String) -> Unit,
    onCancelMatchAndRefundAll: (matchId: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var bookingToCancel by remember { mutableStateOf<SlotBooking?>(null) }
    var matchToCancel by remember { mutableStateOf<TournamentMatch?>(null) }

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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Explanation Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(FlameOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎟️", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ওনার স্লট বাতিল ও অটো-রিফান্ড সিস্টেম",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = FlameYellow
                            )
                            Text(
                                text = "সরাসরি প্লেয়ারের ওয়ালেটে ১০০% টাকা ক্যাশব্যাক",
                                style = MaterialTheme.typography.labelSmall,
                                color = VictoryGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "ওনার হিসেবে আপনি যেকোনো অসাধু, ফেক TrxID বা অপ্রয়োজনীয় স্লট বাতিল করতে পারেন। বাতিল বাটনে চাপ দেওয়ার সাথে সাথে প্লেয়ারের ওয়ালেটে স্বয়ংক্রিয়ভাবে বুকিং ফি রিফান্ড হয়ে যাবে এবং স্লটটি খালি হয়ে যাবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
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
                        text = "১-ক্লিকে রিফান্ড",
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

