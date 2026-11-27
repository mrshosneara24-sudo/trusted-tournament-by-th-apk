package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCustomization
import com.example.model.AppLogoOption
import com.example.model.GameCategory
import com.example.model.MatchPresetTemplate
import com.example.model.MatchStatus
import com.example.model.TournamentMatch
import com.example.model.TournamentRuleItem
import com.example.model.formatMatchClockTimeLabel
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FireRed
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue

@Composable
fun OwnerCommandAndPromoSection(
    appCustomization: AppCustomization,
    matches: List<TournamentMatch>,
    strictRules: List<TournamentRuleItem>,
    auditSummaryText: String,
    onRefreshCloudState: () -> String,
    onUpdateAppBranding: (appName: String, appTagline: String, logo: AppLogoOption, noticeTicker: String, announcement: String) -> Unit,
    onUpdateFinancialSettings: (bkash: String, nagad: String, whatsAppUrl: String, minWithdraw: Int, maxWithdraw: Int) -> Unit,
    onToggleMaintenanceMode: (enabled: Boolean, message: String) -> Unit,
    onUpdateExistingMatch: (TournamentMatch) -> Boolean,
    onDeleteMatch: (String) -> Boolean,
    onAddStrictRule: (title: String, description: String, penalty: String, emoji: String) -> Boolean,
    onDeleteStrictRule: (String) -> Boolean,
    onResetDefaultRules: () -> Unit,
    matchPresets: List<MatchPresetTemplate> = emptyList(),
    onLaunchMatchPreset: (presetId: String, overrideStartsInMinutes: Int?) -> Pair<Boolean, String> = { _, _ -> false to "" },
    onDeleteMatchPreset: (presetId: String) -> Pair<Boolean, String> = { false to "" },
    onHoldBrAndCsForLoneWolf: () -> Pair<Boolean, String> = { false to "" },
    onRescheduleMatchMinutes: (matchId: String, minutesFromNow: Int) -> Pair<Boolean, String> = { _, _ -> false to "" }
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    // Maintenance & Branding state
    var maintenanceEnabled by remember(appCustomization.isMaintenanceMode) { mutableStateOf(appCustomization.isMaintenanceMode) }
    var maintenanceMsg by remember(appCustomization.maintenanceMessage) { mutableStateOf(appCustomization.maintenanceMessage) }
    var appNameInput by remember(appCustomization.appName) { mutableStateOf(appCustomization.appName) }
    var taglineInput by remember(appCustomization.appTagline) { mutableStateOf(appCustomization.appTagline) }
    var selectedLogo by remember(appCustomization.selectedLogo) { mutableStateOf(appCustomization.selectedLogo) }
    var tickerInput by remember(appCustomization.noticeTickerText) { mutableStateOf(appCustomization.noticeTickerText) }

    // Financial state
    var bkashInput by remember(appCustomization.officialBkashNumber) { mutableStateOf(appCustomization.officialBkashNumber) }
    var nagadInput by remember(appCustomization.officialNagadNumber) { mutableStateOf(appCustomization.officialNagadNumber) }
    var whatsAppInput by remember(appCustomization.officialWhatsAppUrl) { mutableStateOf(appCustomization.officialWhatsAppUrl) }
    var minWInput by remember(appCustomization.minWithdraw) { mutableStateOf(appCustomization.minWithdraw.toString()) }
    var maxWInput by remember(appCustomization.maxWithdraw) { mutableStateOf(appCustomization.maxWithdraw.toString()) }

    // Match Editor state
    var selectedMatchId by remember(matches) { mutableStateOf(matches.firstOrNull()?.id.orEmpty()) }
    val activeMatch = matches.find { it.id == selectedMatchId } ?: matches.firstOrNull()
    var editTitle by remember(activeMatch?.id) { mutableStateOf(activeMatch?.title.orEmpty()) }
    var editDateTime by remember(activeMatch?.id) { mutableStateOf(activeMatch?.matchDateTime.orEmpty()) }
    var editEntry by remember(activeMatch?.id) { mutableStateOf((activeMatch?.entryFee ?: 0).toString()) }
    var editPrize by remember(activeMatch?.id) { mutableStateOf((activeMatch?.totalPrize ?: 0).toString()) }
    var editPerKill by remember(activeMatch?.id) { mutableStateOf((activeMatch?.perKillPrize ?: 0).toString()) }
    var editBooyah by remember(activeMatch?.id) { mutableStateOf((activeMatch?.booyahPrize ?: 0).toString()) }
    var editStatus by remember(activeMatch?.id) { mutableStateOf(activeMatch?.status ?: MatchStatus.UPCOMING) }
    var editStartsInMinutes by remember(activeMatch?.id) { mutableStateOf((activeMatch?.startsInMinutes ?: 15).toString()) }
    var shouldApplyNewCountdown by remember(activeMatch?.id) { mutableStateOf(false) }
    var confirmDeleteMatchId by remember { mutableStateOf<String?>(null) }
    var presetLaunchMinutes by remember { mutableIntStateOf(15) }

    LaunchedEffect(activeMatch?.id) {
        activeMatch?.let { m ->
            editTitle = m.title
            editDateTime = m.matchDateTime
            editEntry = m.entryFee.toString()
            editPrize = m.totalPrize.toString()
            editPerKill = m.perKillPrize.toString()
            editBooyah = m.booyahPrize.toString()
            editStatus = m.status
            editStartsInMinutes = m.startsInMinutes.toString()
            shouldApplyNewCountdown = false
            confirmDeleteMatchId = null
        }
    }

    // Strict Rule Creator
    var ruleEmoji by remember { mutableStateOf("⚠️") }
    var ruleTitle by remember { mutableStateOf("") }
    var ruleDesc by remember { mutableStateOf("") }
    var rulePenalty by remember { mutableStateOf("তাৎক্ষণিক ব্যান") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. Live Platform Financial & Audit Summary Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("owner_audit_card")
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Assessment, contentDescription = "Owner Live Audit", tint = FlameCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("📊 লাইভ প্ল্যাটফর্ম অডিট ও সিঙ্ক স্ট্যাটাস", fontWeight = FontWeight.Black, color = FlameCyan, fontSize = 14.sp)
                }
                Surface(shape = RoundedCornerShape(12.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkBorder)) {
                    Text(
                        text = auditSummaryText,
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth().padding(10.dp)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(auditSummaryText))
                            Toast.makeText(context, "অডিট রিপোর্ট কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("owner_copy_audit_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Audit", modifier = Modifier.size(16.dp), tint = FlameYellow)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("রিপোর্ট কপি", fontSize = 11.sp, color = FlameYellow, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = {
                            val msg = onRefreshCloudState()
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(48.dp).testTag("owner_refresh_sync_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WaterBlue)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Sync Refresh", modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("লাইভ রিফ্রেশ", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Maintenance Mode & Branding Studio
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Build, contentDescription = "App Studio", tint = FlameYellow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🎛️ অ্যাপ ব্র্যান্ডিং, মেইনটেন্যান্স ও পেমেন্ট", fontWeight = FontWeight.Black, color = FlameYellow, fontSize = 14.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🚧 মেইনটেন্যান্স মোড", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("চালু করলে সব স্ক্রিনে সতর্কতা ব্যানার দেখাবে", fontSize = 10.sp, color = TextSecondary)
                    }
                    Switch(
                        checked = maintenanceEnabled,
                        onCheckedChange = {
                            maintenanceEnabled = it
                            onToggleMaintenanceMode(it, maintenanceMsg)
                        },
                        modifier = Modifier.testTag("owner_maintenance_switch")
                    )
                }

                OutlinedTextField(
                    value = maintenanceMsg,
                    onValueChange = { maintenanceMsg = it },
                    label = { Text("মেইনটেন্যান্স বার্তা") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameYellow, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )

                OutlinedTextField(value = appNameInput, onValueChange = { appNameInput = it }, label = { Text("অ্যাপের নাম") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameYellow, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                OutlinedTextField(value = taglineInput, onValueChange = { taglineInput = it }, label = { Text("অ্যাপ ট্যাগলাইন") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameYellow, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                OutlinedTextField(value = tickerInput, onValueChange = { tickerInput = it }, label = { Text("উপরের রানিং নোটিশ টেক্সট") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameYellow, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))

                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppLogoOption.entries.forEach { logo ->
                        FilterChip(
                            selected = selectedLogo == logo,
                            onClick = { selectedLogo = logo },
                            label = { Text("${logo.iconEmoji} ${logo.title.take(16)}", fontSize = 11.sp) }
                        )
                    }
                }

                Button(
                    onClick = {
                        onUpdateAppBranding(appNameInput, taglineInput, selectedLogo, tickerInput, appCustomization.activeAnnouncement)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("owner_save_branding_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
                ) {
                    Text("✨ ব্র্যান্ডিং ও নোটিশ সেভ করুন", color = Color.Black, fontWeight = FontWeight.Black)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = bkashInput, onValueChange = { bkashInput = it }, label = { Text("বিকাশ নম্বর") }, singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = nagadInput, onValueChange = { nagadInput = it }, label = { Text("নগদ নম্বর") }, singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                }
                OutlinedTextField(value = whatsAppInput, onValueChange = { whatsAppInput = it }, label = { Text("WhatsApp লিঙ্ক") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = minWInput, onValueChange = { minWInput = it.filter { ch -> ch.isDigit() } }, label = { Text("Min উইথড্র (৳)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = maxWInput, onValueChange = { maxWInput = it.filter { ch -> ch.isDigit() } }, label = { Text("Max উইথড্র (৳)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                }
                Button(
                    onClick = {
                        onUpdateFinancialSettings(
                            bkashInput,
                            nagadInput,
                            whatsAppInput,
                            minWInput.toIntOrNull() ?: 50,
                            maxWInput.toIntOrNull() ?: 200
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("owner_save_financial_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                ) {
                    Text("💰 পেমেন্ট ও উইথড্র লিমিট সেভ", color = Color.Black, fontWeight = FontWeight.Black)
                }
            }
        }

        // 2.5 Saved BR & CS Preset Slots + Lone Wolf Only Mode
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.55f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("owner_presets_card")
        ) {
            val lwCount = matches.count { it.category == GameCategory.LONE_WOLF }
            val brCount = matches.count { it.category == GameCategory.BR_RANK }
            val csCount = matches.count { it.category == GameCategory.CS_RANK }
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Schedule, contentDescription = "Saved Presets", tint = FlameYellow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🐺 Lone Wolf জার্নি ও সেভড BR/CS প্রিসেট স্লট", fontWeight = FontWeight.Black, color = FlameYellow, fontSize = 14.sp)
                }
                Text(
                    text = "লাইভ ম্যাচ: Lone Wolf ($lwCount) • BR Rank ($brCount) • CS Rank ($csCount)",
                    fontFamily = MonoFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = FlameCyan
                )
                Button(
                    onClick = {
                        val res = onHoldBrAndCsForLoneWolf()
                        Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("owner_hold_br_cs_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("🐺 BR ও CS ম্যাচ হোল্ড করুন (শুধু Lone Wolf সক্রিয়)", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }

                Text("🚀 প্রিসেট চালু করার সময় (মিনিট পর শুরু):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(7, 10, 15, 20, 30, 45).forEach { mins ->
                        FilterChip(
                            selected = presetLaunchMinutes == mins,
                            onClick = { presetLaunchMinutes = mins },
                            label = { Text("${mins}মি.", fontFamily = MonoFontFamily, fontSize = 11.sp) }
                        )
                    }
                }

                matchPresets.forEach { preset ->
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkSurface, border = BorderStroke(1.dp, preset.category.accentColor.copy(alpha = 0.45f))) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(preset.presetName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    text = "${preset.totalSlots} স্লট • ${preset.mapName} • এন্ট্রি ৳${preset.entryFee} • প্রাইজ ৳${preset.totalPrize}",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                            if (!preset.isDefaultSaved) {
                                IconButton(
                                    onClick = {
                                        val res = onDeleteMatchPreset(preset.id)
                                        Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete Preset", tint = FireRed)
                                }
                            }
                            Button(
                                onClick = {
                                    val res = onLaunchMatchPreset(preset.id, presetLaunchMinutes)
                                    Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("owner_launch_preset_${preset.id}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                            ) {
                                Text("🚀 লাইভ (${presetLaunchMinutes}মি)", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        // 3. Match Editor, Live Countdown Reset & Safe Delete
        if (activeMatch != null) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkCard,
                border = BorderStroke(1.dp, WaterBlue.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Edit, contentDescription = "Match Editor", tint = FlameCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("⚔️ ম্যাচ এডিটর, লাইভ টাইমার ও ডিলিট", fontWeight = FontWeight.Black, color = FlameCyan, fontSize = 14.sp)
                    }

                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        matches.forEach { m ->
                            FilterChip(
                                selected = m.id == activeMatch.id,
                                onClick = { selectedMatchId = m.id },
                                label = { Text(m.title.take(20), fontSize = 11.sp) }
                            )
                        }
                    }

                    // Live countdown quick rescheduler
                    Text(
                        text = "⏱️ রিয়েল-টাইম কাউন্টডাউন: ${activeMatch.liveCountdownText()}",
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlameYellow
                    )
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(5, 7, 10, 15, 30, 60).forEach { mins ->
                            FilterChip(
                                selected = shouldApplyNewCountdown && editStartsInMinutes.toIntOrNull() == mins,
                                onClick = {
                                    editStartsInMinutes = mins.toString()
                                    shouldApplyNewCountdown = true
                                    editDateTime = formatMatchClockTimeLabel(System.currentTimeMillis() + mins * 60_000L)
                                },
                                label = { Text("${mins}মি.", fontFamily = MonoFontFamily, fontSize = 11.sp) }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = editStartsInMinutes,
                            onValueChange = { raw ->
                                val digits = raw.filter { it.isDigit() }
                                editStartsInMinutes = digits
                                shouldApplyNewCountdown = true
                                digits.toIntOrNull()?.takeIf { it > 0 }?.let { mins ->
                                    editDateTime = formatMatchClockTimeLabel(System.currentTimeMillis() + mins * 60_000L)
                                }
                            },
                            label = { Text("বাকি মিনিট") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                        )
                        Button(
                            onClick = {
                                val mins = (editStartsInMinutes.toIntOrNull() ?: 15).coerceIn(1, 100000)
                                val res = onRescheduleMatchMinutes(activeMatch.id, mins)
                                shouldApplyNewCountdown = false
                                Toast.makeText(context, res.second, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp)
                                .testTag("owner_reschedule_timer_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
                        ) {
                            Text("⏱️ লাইভ টাইমার সেট", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }

                    OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("ম্যাচ টাইটেল") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = editDateTime, onValueChange = { editDateTime = it }, label = { Text("সময় ও তারিখ") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = editEntry, onValueChange = { editEntry = it.filter { ch -> ch.isDigit() } }, label = { Text("এন্ট্রি (৳)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                        OutlinedTextField(value = editPrize, onValueChange = { editPrize = it.filter { ch -> ch.isDigit() } }, label = { Text("প্রাইজপুল (৳)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (activeMatch.category != com.example.model.GameCategory.LONE_WOLF) {
                            OutlinedTextField(value = editPerKill, onValueChange = { editPerKill = it.filter { ch -> ch.isDigit() } }, label = { Text("প্রতি কিল (৳)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                        }
                        OutlinedTextField(value = editBooyah, onValueChange = { editBooyah = it.filter { ch -> ch.isDigit() } }, label = { Text(if (activeMatch.category == com.example.model.GameCategory.LONE_WOLF) "উইন প্রাইজ (৳)" else "Booyah (৳)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    }

                    Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MatchStatus.entries.forEach { st ->
                            FilterChip(
                                selected = editStatus == st,
                                onClick = { editStatus = st },
                                label = { Text(st.banglaLabel, fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val parsedMins = (editStartsInMinutes.toIntOrNull() ?: activeMatch.startsInMinutes).coerceIn(1, 100000)
                                val updatedScheduled = if (shouldApplyNewCountdown) {
                                    System.currentTimeMillis() + parsedMins * 60_000L
                                } else {
                                    activeMatch.effectiveStartMillis()
                                }
                                val safePerKill = if (activeMatch.category == com.example.model.GameCategory.LONE_WOLF) {
                                    0
                                } else {
                                    editPerKill.toIntOrNull() ?: activeMatch.perKillPrize
                                }
                                onUpdateExistingMatch(
                                    activeMatch.copy(
                                        title = editTitle.ifBlank { activeMatch.title },
                                        matchDateTime = editDateTime.ifBlank { activeMatch.matchDateTime },
                                        startsInMinutes = parsedMins,
                                        scheduledStartAtMillis = updatedScheduled,
                                        entryFee = editEntry.toIntOrNull() ?: activeMatch.entryFee,
                                        totalPrize = editPrize.toIntOrNull() ?: activeMatch.totalPrize,
                                        perKillPrize = safePerKill,
                                        booyahPrize = editBooyah.toIntOrNull() ?: activeMatch.booyahPrize,
                                        status = editStatus
                                    )
                                )
                                shouldApplyNewCountdown = false
                            },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("owner_save_match_edit_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WaterBlue)
                        ) {
                            Text("সেভ পরিবর্তন", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                if (confirmDeleteMatchId == activeMatch.id) {
                                    onDeleteMatch(activeMatch.id)
                                    confirmDeleteMatchId = null
                                } else {
                                    confirmDeleteMatchId = activeMatch.id
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp).testTag("owner_delete_match_btn"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, FireRed)
                        ) {
                            Text(
                                text = if (confirmDeleteMatchId == activeMatch.id) "নিশ্চিত মুছুন!" else "ডিলিট + রিফান্ড",
                                color = FireRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // 4. Strict Tournament Rules Manager
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Gavel, contentDescription = "Strict Rules", tint = FlameOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📜 কঠোর টুর্নামেন্ট নিয়ম (${strictRules.size}টি)", fontWeight = FontWeight.Black, color = FlameOrange, fontSize = 14.sp)
                    }
                    OutlinedButton(
                        onClick = onResetDefaultRules,
                        modifier = Modifier.heightIn(min = 48.dp).testTag("owner_reset_rules_btn")
                    ) {
                        Text("ডিফল্ট রিসেট", fontSize = 11.sp, color = FlameYellow)
                    }
                }

                strictRules.forEach { rule ->
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkBorder)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${rule.iconEmoji} ${rule.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("${rule.description} • শাস্তি: ${rule.penalty}", fontSize = 10.sp, color = TextSecondary)
                            }
                            IconButton(onClick = { onDeleteStrictRule(rule.id) }, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Rule", tint = FireRed)
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = ruleEmoji, onValueChange = { ruleEmoji = it }, label = { Text("ইমোজি") }, singleLine = true, modifier = Modifier.width(84.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameOrange, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = ruleTitle, onValueChange = { ruleTitle = it }, label = { Text("নতুন নিয়মের শিরোনাম") }, singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameOrange, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                }
                OutlinedTextField(value = ruleDesc, onValueChange = { ruleDesc = it }, label = { Text("নিয়মের বিবরণ") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameOrange, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                OutlinedTextField(value = rulePenalty, onValueChange = { rulePenalty = it }, label = { Text("শাস্তি") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameOrange, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                Button(
                    onClick = {
                        if (onAddStrictRule(ruleTitle, ruleDesc, rulePenalty, ruleEmoji)) {
                            ruleTitle = ""
                            ruleDesc = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("owner_add_rule_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("➕ নতুন নিয়ম যোগ করুন", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
