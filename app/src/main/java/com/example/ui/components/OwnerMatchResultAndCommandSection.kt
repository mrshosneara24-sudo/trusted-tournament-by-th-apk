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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppUser
import com.example.model.MatchResult
import com.example.model.MatchStatus
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue

@Composable
fun OwnerMatchResultAndCommandSection(
    matches: List<TournamentMatch>,
    allBookings: List<SlotBooking>,
    allUsers: List<AppUser>,
    results: List<MatchResult>,
    onPublishMatchResult: (
        matchId: String,
        winnerTargetKey: String,
        winnerKills: Int,
        customPrizeAmount: Int?,
        runnerUpIgn: String,
        markMatchFinished: Boolean
    ) -> Pair<Boolean, String>,
    onAwardPlayerStats: (
        targetKey: String,
        killsToAdd: Int,
        winsToAdd: Int,
        matchesToAdd: Int,
        prizeAmount: Int,
        note: String
    ) -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    val activeMatches = remember(matches) {
        matches.filter { it.status != MatchStatus.CANCELLED }
    }
    var selectedMatchId by remember(activeMatches) {
        mutableStateOf(
            activeMatches.firstOrNull { it.status != MatchStatus.FINISHED }?.id
                ?: activeMatches.firstOrNull()?.id
                ?: ""
        )
    }
    val selectedMatch = activeMatches.find { it.id == selectedMatchId } ?: activeMatches.firstOrNull()

    val matchBookings = remember(allBookings, selectedMatch?.id) {
        if (selectedMatch == null) emptyList()
        else allBookings.filter { it.matchId == selectedMatch.id }
    }

    var winnerTargetKey by remember { mutableStateOf("") }
    var killsInput by remember { mutableStateOf("5") }
    var customPrizeInput by remember { mutableStateOf("") }
    var runnerUpInput by remember { mutableStateOf("") }
    var markMatchFinished by remember { mutableStateOf(true) }
    var resultFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    // Quick Kill/Stat Booster state (for #2, #3 or per-kill bonus)
    var quickTargetKey by remember { mutableStateOf("") }
    var quickKillsInput by remember { mutableStateOf("2") }
    var quickWinsInput by remember { mutableStateOf("0") }
    var quickPrizeInput by remember { mutableStateOf("20") }
    var quickNoteInput by remember { mutableStateOf("কিল ও পারফরম্যান্স বোনাস") }
    var quickFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    // Live Command Telemetry Calculations
    val totalCollectedEntry = remember(allBookings) { allBookings.sumOf { it.feePaid } }
    val totalActiveBookedSlots = remember(matches) { matches.sumOf { it.bookedSlots } }
    val totalDistributedPrize = remember(results) { results.sumOf { it.prizeMoney } }
    val estimatedHostNet = (totalCollectedEntry - totalDistributedPrize)

    val parsedKills = (killsInput.toIntOrNull() ?: 0).coerceAtLeast(0)
    val autoPrizeEstimate = selectedMatch?.let { it.booyahPrize + parsedKills * it.perKillPrize } ?: 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("owner_match_result_command_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Live Command Telemetry Overview Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.55f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(FireOrange.copy(alpha = 0.14f), DarkCard, WaterBlue.copy(alpha = 0.14f))
                        )
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Leaderboard,
                            contentDescription = "Command Telemetry",
                            tint = FlameCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "লাইভ কমান্ড ও প্রাইজ টেলিমেট্রি",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = FlameYellow
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VictoryGreen.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "AUTO-RANK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            color = VictoryGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryMiniCard(
                        label = "মোট এন্ট্রি সংগ্রহ",
                        value = "৳$totalCollectedEntry",
                        accent = FlameCyan,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryMiniCard(
                        label = "মোট প্রাইজ প্রদান",
                        value = "৳$totalDistributedPrize",
                        accent = VictoryGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryMiniCard(
                        label = "অ্যাক্টিভ বুকড স্লট",
                        value = "$totalActiveBookedSlots টি",
                        accent = FlameYellow,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryMiniCard(
                        label = "আনুমানিক হোস্ট নিট",
                        value = "${if (estimatedHostNet >= 0) "+" else ""}৳$estimatedHostNet",
                        accent = if (estimatedHostNet >= 0) VictoryGreen else FireRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. 1-Tap Booyah Winner Result + Auto Wallet Payout + Live Leaderboard Sync
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.55f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "🏆 ১-ট্যাপ ম্যাচ রেজাল্ট ও প্রাইজ পেআউট",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = FlameYellow
                        )
                        Text(
                            text = "বুইয়া বিজয়ীকে অটো ওয়ালেটে টাকা পাঠান ও লাইভ লিডারবোর্ড আপডেট করুন",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Match Picker Chips
                Text(
                    text = "ম্যাচ নির্বাচন করুন:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                if (activeMatches.isEmpty()) {
                    Text(
                        text = "কোনো ম্যাচ পাওয়া যায়নি।",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        activeMatches.forEach { match ->
                            val isSelected = selectedMatch?.id == match.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) FireOrange.copy(alpha = 0.28f) else DarkElevated,
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) FlameYellow else DarkBorder
                                ),
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedMatchId = match.id
                                        resultFeedback = null
                                    }
                                    .testTag("result_match_chip_${match.id}")
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                    Text(
                                        text = match.title,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) FlameYellow else Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "বুইয়া ৳${match.booyahPrize} • কিল ৳${match.perKillPrize} (${match.status.banglaLabel})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = FlameCyan
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick-Pick Booked Players for Selected Match
                if (matchBookings.isNotEmpty()) {
                    Text(
                        text = "এই ম্যাচের বুকড প্লেয়ার (ট্যাপ করে সিলেক্ট):",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = VictoryGreen
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        matchBookings.forEach { bk ->
                            val chosen = winnerTargetKey.equals(bk.playerUid, true) ||
                                winnerTargetKey.equals(bk.playerIgn, true)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (chosen) VictoryGreen.copy(alpha = 0.25f) else DarkElevated,
                                border = BorderStroke(1.dp, if (chosen) VictoryGreen else DarkBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        winnerTargetKey = bk.playerUid.ifBlank { bk.playerIgn }
                                    }
                            ) {
                                Text(
                                    text = "#${bk.slotNumber} ${bk.playerIgn}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (chosen) VictoryGreen else Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                } else if (allUsers.isNotEmpty()) {
                    Text(
                        text = "রেজিস্টার্ড প্লেয়ার (দ্রুত নির্বাচন):",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = FlameCyan
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allUsers.take(12).forEach { usr ->
                            val chosen = winnerTargetKey == usr.phone || winnerTargetKey == usr.uid
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (chosen) FlameCyan.copy(alpha = 0.22f) else DarkElevated,
                                border = BorderStroke(1.dp, if (chosen) FlameCyan else DarkBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { winnerTargetKey = usr.uid.ifBlank { usr.phone } }
                            ) {
                                Text(
                                    text = "${usr.ign} (${usr.uid})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (chosen) FlameCyan else Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = winnerTargetKey,
                    onValueChange = {
                        winnerTargetKey = it
                        resultFeedback = null
                    },
                    label = { Text("বিজয়ী প্লেয়ারের ফোন, FF UID বা IGN") },
                    placeholder = { Text("যেমন: 017... অথবা 11410365896") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("result_winner_target_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = killsInput,
                        onValueChange = { killsInput = it.filter(Char::isDigit).take(2) },
                        label = { Text("মোট কিল") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("result_winner_kills_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameYellow,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = customPrizeInput,
                        onValueChange = { customPrizeInput = it.filter(Char::isDigit).take(6) },
                        label = { Text("প্রাইজ ৳ (খালি=অটো ৳$autoPrizeEstimate)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("result_prize_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VictoryGreen,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                OutlinedTextField(
                    value = runnerUpInput,
                    onValueChange = { runnerUpInput = it.take(40) },
                    label = { Text("রানার-আপ IGN (#২ — ঐচ্ছিক)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("result_runner_up_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { markMatchFinished = !markMatchFinished }
                ) {
                    Checkbox(
                        checked = markMatchFinished,
                        onCheckedChange = { markMatchFinished = it },
                        colors = CheckboxDefaults.colors(checkedColor = VictoryGreen)
                    )
                    Text(
                        text = "প্রকাশের পর ম্যাচ স্ট্যাটাস 'সমাপ্ত (Finished)' করুন",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary
                    )
                }

                Button(
                    onClick = {
                        val matchId = selectedMatch?.id.orEmpty()
                        val prizeOverride = customPrizeInput.trim().toIntOrNull()
                        resultFeedback = onPublishMatchResult(
                            matchId,
                            winnerTargetKey.trim(),
                            parsedKills,
                            prizeOverride,
                            runnerUpInput.trim(),
                            markMatchFinished
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("publish_match_result_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                ) {
                    Icon(Icons.Filled.Paid, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🏆 ফলাফল প্রকাশ ও প্রাইজ দিন (৳${customPrizeInput.toIntOrNull() ?: autoPrizeEstimate})",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                resultFeedback?.let { (ok, msg) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (ok) VictoryGreen.copy(alpha = 0.16f) else FireRed.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, if (ok) VictoryGreen else FireRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("result_publish_feedback")
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (ok) VictoryGreen else FireRed,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // 3. Quick Kill / Runner-Up Bonus & Live Leaderboard Stat Booster
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, WaterBlue.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Bolt, contentDescription = null, tint = FlameCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚡ প্লেয়ার কিল / বোনাস ও লিডারবোর্ড বুস্টার",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = FlameCyan
                    )
                }

                OutlinedTextField(
                    value = quickTargetKey,
                    onValueChange = { quickTargetKey = it },
                    label = { Text("প্লেয়ারের ফোন, UID বা IGN") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quick_stat_target_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quickKillsInput,
                        onValueChange = { quickKillsInput = it.filter(Char::isDigit).take(3) },
                        label = { Text("+কিল") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_stat_kills_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = quickWinsInput,
                        onValueChange = { quickWinsInput = it.filter(Char::isDigit).take(2) },
                        label = { Text("+বুইয়া") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_stat_wins_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameYellow,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = quickPrizeInput,
                        onValueChange = { quickPrizeInput = it.filter(Char::isDigit).take(5) },
                        label = { Text("বোনাস ৳") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("quick_stat_prize_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VictoryGreen,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                OutlinedTextField(
                    value = quickNoteInput,
                    onValueChange = { quickNoteInput = it.take(60) },
                    label = { Text("নোট / কারণ") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Button(
                    onClick = {
                        quickFeedback = onAwardPlayerStats(
                            quickTargetKey.trim(),
                            quickKillsInput.toIntOrNull() ?: 0,
                            quickWinsInput.toIntOrNull() ?: 0,
                            1,
                            quickPrizeInput.toIntOrNull() ?: 0,
                            quickNoteInput.trim().ifBlank { "কিল ও পারফরম্যান্স বোনাস" }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp)
                        .testTag("award_quick_stat_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameCyan)
                ) {
                    Icon(Icons.Filled.MilitaryTech, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "⚡ কিল/প্রাইজ যোগ ও লিডারবোর্ড আপডেট",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                quickFeedback?.let { (ok, msg) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (ok) VictoryGreen.copy(alpha = 0.16f) else FireRed.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, if (ok) VictoryGreen else FireRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (ok) VictoryGreen else FireRed,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        // 4. Recent Published Results List
        if (results.isNotEmpty()) {
            Text(
                text = "সাম্প্রতিক প্রকাশিত ফলাফল (${results.size})",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow
            )
            results.take(6).forEach { res ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkElevated,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
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
                                text = "👑 ${res.winnerIgn} (${res.winnerKills} কিল)",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = FlameYellow
                            )
                            Text(
                                text = res.matchTitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "+৳${res.prizeMoney}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = VictoryGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryMiniCard(
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
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = MonoFontFamily,
                    fontWeight = FontWeight.Black
                ),
                color = accent,
                maxLines = 1
            )
        }
    }
}
