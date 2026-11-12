package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.AppCustomization
import com.example.model.AppLogoOption
import com.example.model.GameCategory
import com.example.model.MatchStatus
import com.example.model.MatchType
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.model.TournamentRuleItem
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

fun getLogoDrawable(option: AppLogoOption): Int {
    return when (option) {
        AppLogoOption.TH_LOGO -> R.drawable.th_logo
        AppLogoOption.TH_ESPORTS -> R.drawable.th_esports_logo_1792210276945
        AppLogoOption.CYBER_WOLF -> R.drawable.avatar_wolf
        AppLogoOption.PRO_GAMER -> R.drawable.avatar_cyber
    }
}

@Composable
fun MasterAppEditorDialog(
    appCustomization: AppCustomization,
    matches: List<TournamentMatch>,
    bookings: List<SlotBooking>,
    strictRules: List<TournamentRuleItem>,
    onDismiss: () -> Unit,
    onUpdateAppBranding: (appName: String, appTagline: String, logo: AppLogoOption, ticker: String, announcement: String) -> Unit,
    onUpdateFinancials: (bkash: String, nagad: String, whatsApp: String, minW: Int, maxW: Int) -> Unit,
    onToggleMaintenance: (enabled: Boolean, msg: String) -> Unit,
    onCreateMatch: (title: String, category: GameCategory, type: MatchType, map: String, time: String, fee: Int, totalPrize: Int, perKill: Int, booyah: Int, slots: Int) -> Unit,
    onUpdateExistingMatch: (TournamentMatch) -> Unit,
    onDeleteMatch: (matchId: String) -> Unit,
    onUpdateSlotCapacity: (matchId: String, newCapacity: Int) -> Unit,
    onCreateManualSlot: (matchId: String, slotNumber: Int, ign: String, uid: String, phone: String, fee: Int) -> Unit,
    onCancelSlotAndRefund: (bookingId: String) -> Unit,
    onCancelMatchAndRefundAll: (matchId: String) -> Unit,
    onAddRule: (title: String, desc: String, penalty: String, emoji: String) -> Unit,
    onUpdateRule: (TournamentRuleItem) -> Unit,
    onDeleteRule: (ruleId: String) -> Unit,
    onResetRules: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "🏷️ ব্র্যান্ডিং ও লোগো",
        "⚔️ ম্যাচ এডিটর",
        "🎟️ স্লট ক্রিয়েট/ডিলিট",
        "📜 কঠোর রুলস",
        "💰 বিকাশ ও গেটওয়ে"
    )

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
                .testTag("master_app_editor_dialog"),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    color = DarkCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(FlameYellow.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👑", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "A-to-Z ওনার অ্যাপ এডিটর",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = FlameYellow
                                )
                                Text(
                                    text = "লোগো, স্লট ক্রিয়েট/ডিলিট, ম্যাচ, রুলস ও বিকাশ নিয়ন্ত্রণ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                // Tab Bar
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
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (selectedTab == index) FlameYellow else Color.White.copy(alpha = 0.65f)
                                )
                            }
                        )
                    }
                }

                HorizontalDivider(color = DarkBorder)

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(DarkSurface)
                ) {
                    when (selectedTab) {
                        0 -> BrandingEditorTab(
                            currentConfig = appCustomization,
                            onSave = onUpdateAppBranding,
                            onToggleMaintenance = onToggleMaintenance
                        )
                        1 -> MatchEditorTab(
                            matches = matches,
                            onCreateMatch = onCreateMatch,
                            onUpdateMatch = onUpdateExistingMatch,
                            onDeleteMatch = onDeleteMatch
                        )
                        2 -> SlotManagementTab(
                            matches = matches,
                            bookings = bookings,
                            onUpdateSlotCapacity = onUpdateSlotCapacity,
                            onCreateManualSlot = onCreateManualSlot,
                            onCancelSlotAndRefund = onCancelSlotAndRefund,
                            onCancelMatchAndRefundAll = onCancelMatchAndRefundAll
                        )
                        3 -> StrictRulesEditorTab(
                            rules = strictRules,
                            onAddRule = onAddRule,
                            onUpdateRule = onUpdateRule,
                            onDeleteRule = onDeleteRule,
                            onResetRules = onResetRules
                        )
                        4 -> FinancialsEditorTab(
                            currentConfig = appCustomization,
                            onSave = onUpdateFinancials
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TAB 0: BRANDING & LOGO EDITOR
// ==========================================
@Composable
private fun BrandingEditorTab(
    currentConfig: AppCustomization,
    onSave: (appName: String, appTagline: String, logo: AppLogoOption, ticker: String, announcement: String) -> Unit,
    onToggleMaintenance: (enabled: Boolean, msg: String) -> Unit
) {
    val context = LocalContext.current
    var appName by remember { mutableStateOf(currentConfig.appName) }
    var appTagline by remember { mutableStateOf(currentConfig.appTagline) }
    var selectedLogo by remember { mutableStateOf(currentConfig.selectedLogo) }
    var noticeTicker by remember { mutableStateOf(currentConfig.noticeTickerText) }
    var announcement by remember { mutableStateOf(currentConfig.activeAnnouncement) }
    var isMaintenance by remember { mutableStateOf(currentConfig.isMaintenanceMode) }
    var maintenanceMsg by remember { mutableStateOf(currentConfig.maintenanceMessage) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "🎨 অ্যাপ আইডেন্টিটি ও লোগো সিলেক্টর",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow
            )
        }

        // Logo Selection Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "অ্যাপ লোগো নির্বাচন করুন (লাইভ প্রিভিউ):",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLogoOption.entries.forEach { option ->
                            val isSelected = selectedLogo == option
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedLogo = option }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) FlameYellow else DarkBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                color = if (isSelected) FlameYellow.copy(alpha = 0.15f) else DarkSurface
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = getLogoDrawable(option)),
                                        contentDescription = option.title,
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = option.iconEmoji,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = option.title.split(" ").first(),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) FlameYellow else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // App Name Input
        item {
            OutlinedTextField(
                value = appName,
                onValueChange = { appName = it },
                label = { Text("অ্যাপের নাম (App Title)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        // Tagline Input
        item {
            OutlinedTextField(
                value = appTagline,
                onValueChange = { appTagline = it },
                label = { Text("ট্যাগলাইন ও স্লোগান (Tagline)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        // Notice Ticker
        item {
            OutlinedTextField(
                value = noticeTicker,
                onValueChange = { noticeTicker = it },
                label = { Text("হেডার নোটিশ স্ক্রলার (Top Notice Ticker)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        // Active Announcement Banner
        item {
            OutlinedTextField(
                value = announcement,
                onValueChange = { announcement = it },
                label = { Text("বিশেষ ঘোষণা বার্তা (Announcement Banner)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        // Maintenance Mode Toggle Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isMaintenance) Color(0xFF331414) else DarkCard),
                border = BorderStroke(1.dp, if (isMaintenance) Color(0xFFEF4444) else DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚠️ মেইনটেন্যান্স মোড (সার্ভার ডাউন)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isMaintenance) Color(0xFFEF4444) else Color.White
                            )
                            Text(
                                text = "চালু করলে ইউজাররা অ্যাপে নোটিশ দেখতে পাবে",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Switch(
                            checked = isMaintenance,
                            onCheckedChange = {
                                isMaintenance = it
                                onToggleMaintenance(it, maintenanceMsg)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFEF4444)
                            )
                        )
                    }

                    if (isMaintenance) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = maintenanceMsg,
                            onValueChange = { maintenanceMsg = it },
                            label = { Text("মেইনটেন্যান্স নোটিশ টেক্সট") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Save Branding Button
        item {
            Button(
                onClick = {
                    onSave(appName, appTagline, selectedLogo, noticeTicker, announcement)
                    Toast.makeText(context, "👑 ব্র্যান্ডিং সফলভাবে সেভ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ব্র্যান্ডিং ও লোগো পরিবর্তন সেভ করুন", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// TAB 1: MATCH EDITOR (CREATE, EDIT, DELETE)
// ==========================================
@Composable
private fun MatchEditorTab(
    matches: List<TournamentMatch>,
    onCreateMatch: (title: String, category: GameCategory, type: MatchType, map: String, time: String, fee: Int, totalPrize: Int, perKill: Int, booyah: Int, slots: Int) -> Unit,
    onUpdateMatch: (TournamentMatch) -> Unit,
    onDeleteMatch: (matchId: String) -> Unit
) {
    val context = LocalContext.current
    var showCreateDialog by remember { mutableStateOf(false) }
    var matchToEdit by remember { mutableStateOf<TournamentMatch?>(null) }
    var matchToDelete by remember { mutableStateOf<TournamentMatch?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "⚔️ টুর্নামেন্ট ও ম্যাচ কন্ট্রোল",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = FlameYellow
                    )
                    Text(
                        text = "মোট ম্যাচ: ${matches.size} টি",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("নতুন ম্যাচ", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }
            }
        }

        items(matches) { match ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = match.category.accentColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${match.category.iconEmoji} ${match.category.banglaTitle}",
                                color = match.category.accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = match.status.color.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = match.status.banglaLabel,
                                color = match.status.color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = match.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "ম্যাপ: ${match.mapName} | সময়: ${match.matchDateTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ফি: ৳${match.entryFee}", color = FlameYellow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("মোট প্রাইজ: ৳${match.totalPrize}", color = VictoryGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("স্লট: ${match.bookedSlots}/${match.totalSlots}", color = FlameCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (match.roomId != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "রুম আইডি: ${match.roomId} | পাস: ${match.roomPassword}",
                            color = FlameOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { matchToEdit = match },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = FlameYellow)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("এডিট", color = FlameYellow, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { matchToDelete = match },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ডিলিট", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Create Match Dialog
    if (showCreateDialog) {
        CreateMatchDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { t, c, tp, m, dt, f, p, k, b, s ->
                onCreateMatch(t, c, tp, m, dt, f, p, k, b, s)
                showCreateDialog = false
                Toast.makeText(context, "নতুন ম্যাচ তৈরি সফল!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit Match Dialog
    matchToEdit?.let { targetMatch ->
        EditMatchDialog(
            match = targetMatch,
            onDismiss = { matchToEdit = null },
            onSave = { updated ->
                onUpdateMatch(updated)
                matchToEdit = null
                Toast.makeText(context, "ম্যাচ সফলভাবে আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Match Confirm
    matchToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { matchToDelete = null },
            title = { Text("ম্যাচ মুছে ফেলতে চান?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Text(
                    "আপনি কি নিশ্চিত যে '${target.title}' ম্যাচটি ডিলিট করতে চান? যারা এই ম্যাচে স্লট কিনেছে, তাদের সবার টাকা স্বয়ংক্রিয়ভাবে ওয়ালেটে রিফান্ড হবে।",
                    color = Color.White.copy(alpha = 0.85f)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteMatch(target.id)
                        matchToDelete = null
                        Toast.makeText(context, "ম্যাচ ডিলিট ও রিফান্ড সম্পন্ন!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("হ্যাঁ, ডিলিট করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { matchToDelete = null }) {
                    Text("বাতিল", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }
}

// ==========================================
// TAB 2: SLOT MANAGEMENT (CREATE / DELETE)
// ==========================================
@Composable
private fun SlotManagementTab(
    matches: List<TournamentMatch>,
    bookings: List<SlotBooking>,
    onUpdateSlotCapacity: (matchId: String, newCapacity: Int) -> Unit,
    onCreateManualSlot: (matchId: String, slotNumber: Int, ign: String, uid: String, phone: String, fee: Int) -> Unit,
    onCancelSlotAndRefund: (bookingId: String) -> Unit,
    onCancelMatchAndRefundAll: (matchId: String) -> Unit
) {
    val context = LocalContext.current
    var selectedMatchId by remember { mutableStateOf(matches.firstOrNull()?.id ?: "") }
    var showManualSlotDialog by remember { mutableStateOf(false) }
    var newCapacityInput by remember { mutableStateOf("") }

    val activeMatch = matches.find { it.id == selectedMatchId } ?: matches.firstOrNull()
    val matchBookings = bookings.filter { it.matchId == activeMatch?.id }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "🎟️ স্লট তৈরি, ক্যাপাসিটি এডিট ও অটো-রিফান্ড",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow
            )
        }

        // Match Picker
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ম্যাচ নির্বাচন করুন:", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        matches.take(4).forEach { match ->
                            val isSel = match.id == activeMatch?.id
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) FlameYellow.copy(alpha = 0.2f) else DarkSurface,
                                border = BorderStroke(1.dp, if (isSel) FlameYellow else DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedMatchId = match.id }
                            ) {
                                Text(
                                    text = match.category.banglaTitle,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) FlameYellow else Color.White,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Match Slot Status & Capacity Adjust
        activeMatch?.let { match ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(match.title, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "মোট বরাদ্দকৃত স্লট: ${match.bookedSlots} / মোট ধারণক্ষমতা: ${match.totalSlots}",
                            color = FlameYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Adjust capacity
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newCapacityInput,
                                onValueChange = { newCapacityInput = it },
                                label = { Text("নতুন স্লট সংখ্যা (যেমন ৪৮)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val cap = newCapacityInput.toIntOrNull()
                                    if (cap != null && cap in 2..100) {
                                        onUpdateSlotCapacity(match.id, cap)
                                        Toast.makeText(context, "স্লট সংখ্যা $cap করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                        newCapacityInput = ""
                                    } else {
                                        Toast.makeText(context, "সঠিক সংখ্যা দিন (২ থেকে ১০০)", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
                            ) {
                                Text("সেট", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showManualSlotDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("VIP স্লট তৈরি", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    onCancelMatchAndRefundAll(match.id)
                                    Toast.makeText(context, "ম্যাচ বাতিল ও রিফান্ড সম্পন্ন!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("সব রিফান্ড", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Booked Slots List with Instant Delete & 100% Refund
        item {
            Text(
                text = "বুকিংকৃত স্লট তালিকা (১-ট্যাপে রিফান্ড):",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        if (matchBookings.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "এই ম্যাচে এখনও কোনো স্লট বুক করা হয়নি বা সব রিফান্ড হয়েছে।",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(matchBookings) { booking ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = FlameYellow.copy(alpha = 0.2f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("#${booking.slotNumber}", fontWeight = FontWeight.Black, color = FlameYellow, fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(booking.playerIgn, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text("UID: ${booking.playerUid} | ফি: ৳${booking.feePaid}", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
                        }

                        Button(
                            onClick = {
                                onCancelSlotAndRefund(booking.bookingId)
                                Toast.makeText(context, "স্লট #${booking.slotNumber} বাতিল ও ৳${booking.feePaid} রিফান্ড!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("বাতিল ও রিফান্ড", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Manual Slot Create Dialog
    if (showManualSlotDialog && activeMatch != null) {
        ManualSlotDialog(
            match = activeMatch,
            onDismiss = { showManualSlotDialog = false },
            onConfirm = { slotNum, ign, uid, phone, fee ->
                onCreateManualSlot(activeMatch.id, slotNum, ign, uid, phone, fee)
                showManualSlotDialog = false
                Toast.makeText(context, "স্লট #$slotNum সফলভাবে তৈরি হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==========================================
// TAB 3: STRICT RULES EDITOR
// ==========================================
@Composable
private fun StrictRulesEditorTab(
    rules: List<TournamentRuleItem>,
    onAddRule: (title: String, desc: String, penalty: String, emoji: String) -> Unit,
    onUpdateRule: (TournamentRuleItem) -> Unit,
    onDeleteRule: (ruleId: String) -> Unit,
    onResetRules: () -> Unit
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var ruleToEdit by remember { mutableStateOf<TournamentRuleItem?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "📜 টুর্নামেন্ট কঠোর রুলস কন্ট্রোল",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = FlameYellow
                    )
                    Text(
                        text = "মোট নিয়ম: ${rules.size} টি",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Row {
                    OutlinedButton(
                        onClick = {
                            onResetRules()
                            Toast.makeText(context, "ডিফল্ট নিয়মে রিসেট হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = FlameYellow)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("রিসেট", color = FlameYellow, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Button(
                        onClick = { showAddDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("যোগ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(rules) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(rule.iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(rule.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        }

                        Row {
                            IconButton(onClick = { ruleToEdit = rule }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.Edit, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(16.dp))
                            }
                            IconButton(onClick = { onDeleteRule(rule.id) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Filled.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = rule.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF261014),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚠️ শাস্তি: ${rule.penalty}",
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }

    // Add Rule Dialog
    if (showAddDialog) {
        RuleInputDialog(
            rule = null,
            onDismiss = { showAddDialog = false },
            onSave = { t, d, p, e ->
                onAddRule(t, d, p, e)
                showAddDialog = false
                Toast.makeText(context, "নতুন নিয়ম যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit Rule Dialog
    ruleToEdit?.let { target ->
        RuleInputDialog(
            rule = target,
            onDismiss = { ruleToEdit = null },
            onSave = { t, d, p, e ->
                onUpdateRule(target.copy(title = t, description = d, penalty = p, iconEmoji = e))
                ruleToEdit = null
                Toast.makeText(context, "নিয়ম আপডেট হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// ==========================================
// TAB 4: FINANCIALS & GATEWAYS
// ==========================================
@Composable
private fun FinancialsEditorTab(
    currentConfig: AppCustomization,
    onSave: (bkash: String, nagad: String, whatsApp: String, minW: Int, maxW: Int) -> Unit
) {
    val context = LocalContext.current
    var bkash by remember { mutableStateOf(currentConfig.officialBkashNumber) }
    var nagad by remember { mutableStateOf(currentConfig.officialNagadNumber) }
    var whatsApp by remember { mutableStateOf(currentConfig.officialWhatsAppUrl) }
    var minW by remember { mutableStateOf(currentConfig.minWithdraw.toString()) }
    var maxW by remember { mutableStateOf(currentConfig.maxWithdraw.toString()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "💰 বিকাশ, নগদ ও অর্থনৈতিক গেটওয়ে সেটিংস",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow
            )
        }

        item {
            OutlinedTextField(
                value = bkash,
                onValueChange = { bkash = it },
                label = { Text("অফিসিয়াল বিকাশ নম্বর (Personal bKash)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BkashPink,
                    focusedTextColor = Color.White
                )
            )
        }

        item {
            OutlinedTextField(
                value = nagad,
                onValueChange = { nagad = it },
                label = { Text("অফিসিয়াল নগদ নম্বর (Nagad Number)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = whatsApp,
                onValueChange = { whatsApp = it },
                label = { Text("অফিসিয়াল WhatsApp টুর্নামেন্ট লিঙ্ক") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VictoryGreen,
                    focusedTextColor = Color.White
                )
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = minW,
                    onValueChange = { minW = it },
                    label = { Text("মিনিমাম ক্যাশআউট (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = maxW,
                    onValueChange = { maxW = it },
                    label = { Text("ম্যাক্সিমাম ক্যাশআউট (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Button(
                onClick = {
                    val minVal = minW.toIntOrNull() ?: 50
                    val maxVal = maxW.toIntOrNull() ?: 200
                    onSave(bkash, nagad, whatsApp, minVal, maxVal)
                    Toast.makeText(context, "অর্থনৈতিক সেটিংস সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BkashPink)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("গেটওয়ে ও বিকাশ সেটিংস সেভ করুন", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Dialog for Creating New Match
@Composable
private fun CreateMatchDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, category: GameCategory, type: MatchType, map: String, time: String, fee: Int, totalPrize: Int, perKill: Int, booyah: Int, slots: Int) -> Unit
) {
    var title by remember { mutableStateOf("বিআর র‍্যাঙ্ক - বারমুডা সোলো রাশ") }
    var selectedCat by remember { mutableStateOf(GameCategory.BR_RANK) }
    var selectedType by remember { mutableStateOf(MatchType.SOLO) }
    var mapName by remember { mutableStateOf("বারমুডা (Bermuda)") }
    var timeStr by remember { mutableStateOf("আজ সন্ধ্যা ৭:০০ PM") }
    var feeStr by remember { mutableStateOf("10") }
    var totalPrizeStr by remember { mutableStateOf("350") }
    var perKillStr by remember { mutableStateOf("3") }
    var booyahStr by remember { mutableStateOf("100") }
    var slotsStr by remember { mutableStateOf("48") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন টুর্নামেন্ট তৈরি করুন", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("ম্যাচ টাইটেল") })
                OutlinedTextField(value = mapName, onValueChange = { mapName = it }, label = { Text("ম্যাপের নাম") })
                OutlinedTextField(value = timeStr, onValueChange = { timeStr = it }, label = { Text("ম্যাচের সময়") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = feeStr, onValueChange = { feeStr = it }, label = { Text("এন্ট্রি ফি (৳)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = slotsStr, onValueChange = { slotsStr = it }, label = { Text("মোট স্লট") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = totalPrizeStr, onValueChange = { totalPrizeStr = it }, label = { Text("মোট প্রাইজ (৳)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = perKillStr, onValueChange = { perKillStr = it }, label = { Text("প্রতি কিল (৳)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                OutlinedTextField(value = booyahStr, onValueChange = { booyahStr = it }, label = { Text("বুইয়া প্রাইজ (৳)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        title,
                        selectedCat,
                        selectedType,
                        mapName,
                        timeStr,
                        feeStr.toIntOrNull() ?: 10,
                        totalPrizeStr.toIntOrNull() ?: 350,
                        perKillStr.toIntOrNull() ?: 3,
                        booyahStr.toIntOrNull() ?: 100,
                        slotsStr.toIntOrNull() ?: 48
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
            ) {
                Text("তৈরি করুন", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = Color.White) }
        },
        containerColor = DarkSurface
    )
}

// Dialog for Editing Existing Match
@Composable
private fun EditMatchDialog(
    match: TournamentMatch,
    onDismiss: () -> Unit,
    onSave: (TournamentMatch) -> Unit
) {
    var title by remember { mutableStateOf(match.title) }
    var mapName by remember { mutableStateOf(match.mapName) }
    var timeStr by remember { mutableStateOf(match.matchDateTime) }
    var feeStr by remember { mutableStateOf(match.entryFee.toString()) }
    var totalPrizeStr by remember { mutableStateOf(match.totalPrize.toString()) }
    var perKillStr by remember { mutableStateOf(match.perKillPrize.toString()) }
    var booyahStr by remember { mutableStateOf(match.booyahPrize.toString()) }
    var slotsStr by remember { mutableStateOf(match.totalSlots.toString()) }
    var roomId by remember { mutableStateOf(match.roomId ?: "") }
    var roomPassword by remember { mutableStateOf(match.roomPassword ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ম্যাচ এডিট করুন", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("ম্যাচ টাইটেল") })
                OutlinedTextField(value = mapName, onValueChange = { mapName = it }, label = { Text("ম্যাপের নাম") })
                OutlinedTextField(value = timeStr, onValueChange = { timeStr = it }, label = { Text("ম্যাচের সময়") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = feeStr, onValueChange = { feeStr = it }, label = { Text("এন্ট্রি ফি (৳)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = slotsStr, onValueChange = { slotsStr = it }, label = { Text("মোট স্লট") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = totalPrizeStr, onValueChange = { totalPrizeStr = it }, label = { Text("মোট প্রাইজ (৳)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = perKillStr, onValueChange = { perKillStr = it }, label = { Text("প্রতি কিল (৳)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = roomId, onValueChange = { roomId = it }, label = { Text("রুম আইডি") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = roomPassword, onValueChange = { roomPassword = it }, label = { Text("রুম পাসওয়ার্ড") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = match.copy(
                        title = title,
                        mapName = mapName,
                        matchDateTime = timeStr,
                        entryFee = feeStr.toIntOrNull() ?: match.entryFee,
                        totalSlots = slotsStr.toIntOrNull() ?: match.totalSlots,
                        totalPrize = totalPrizeStr.toIntOrNull() ?: match.totalPrize,
                        perKillPrize = perKillStr.toIntOrNull() ?: match.perKillPrize,
                        roomId = roomId.ifBlank { null },
                        roomPassword = roomPassword.ifBlank { null },
                        status = if (roomId.isNotBlank()) MatchStatus.ROOM_OPEN else match.status
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
            ) {
                Text("আপডেট সেভ করুন", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = Color.White) }
        },
        containerColor = DarkSurface
    )
}

// Dialog for Creating Manual Slot (VIP Reservation)
@Composable
private fun ManualSlotDialog(
    match: TournamentMatch,
    onDismiss: () -> Unit,
    onConfirm: (slotNumber: Int, ign: String, uid: String, phone: String, fee: Int) -> Unit
) {
    var slotNumStr by remember { mutableStateOf("1") }
    var ign by remember { mutableStateOf("") }
    var uid by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var feeStr by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ম্যানুয়াল স্লট ক্রিয়েট (VIP/প্লেয়ার)", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = slotNumStr, onValueChange = { slotNumStr = it }, label = { Text("স্লট নম্বর (১-${match.totalSlots})") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                OutlinedTextField(value = ign, onValueChange = { ign = it }, label = { Text("প্লেয়ারের গেমিং IGN নাম") })
                OutlinedTextField(value = uid, onValueChange = { uid = it }, label = { Text("ফ্রি ফায়ার UID") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("ফোন / বিকাশ নম্বর") })
                OutlinedTextField(value = feeStr, onValueChange = { feeStr = it }, label = { Text("ফি (৳, VIP হলে 0)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sNum = slotNumStr.toIntOrNull() ?: 1
                    onConfirm(sNum, ign, uid, phone, feeStr.toIntOrNull() ?: 0)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
            ) {
                Text("স্লট বরাদ্দ করুন", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = Color.White) }
        },
        containerColor = DarkSurface
    )
}

// Dialog for Adding/Editing Rules
@Composable
private fun RuleInputDialog(
    rule: TournamentRuleItem?,
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, penalty: String, emoji: String) -> Unit
) {
    var title by remember { mutableStateOf(rule?.title ?: "") }
    var desc by remember { mutableStateOf(rule?.description ?: "") }
    var penalty by remember { mutableStateOf(rule?.penalty ?: "") }
    var emoji by remember { mutableStateOf(rule?.iconEmoji ?: "⚠️") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (rule == null) "নতুন নিয়ম যোগ করুন" else "নিয়ম এডিট করুন", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = emoji, onValueChange = { emoji = it }, label = { Text("ইমোজি আইকন") })
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("নিয়মের শিরোনাম") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("বিস্তারিত বিবরণ") })
                OutlinedTextField(value = penalty, onValueChange = { penalty = it }, label = { Text("শাস্তি / পেনাল্টি") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, desc, penalty, emoji) },
                colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
            ) {
                Text("সংরক্ষণ", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = Color.White) }
        },
        containerColor = DarkSurface
    )
}
