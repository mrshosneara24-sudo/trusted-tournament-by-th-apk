package com.example.ui.components

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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GiveawayEvent
import com.example.model.GiveawayPrizeType
import com.example.model.GiveawayStatus
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FireRed
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen

@Composable
fun GiveawayAdminControlCard(
    giveaways: List<GiveawayEvent>,
    onCreateGiveaway: (
        title: String,
        description: String,
        prizeType: GiveawayPrizeType,
        prizeLabel: String,
        cashAmountPerWinner: Int,
        winnersCount: Int,
        maxEntries: Int,
        announceInChat: Boolean
    ) -> Pair<Boolean, String>,
    onDrawWinners: (giveawayId: String) -> Pair<Boolean, String>,
    onCloseGiveaway: (giveawayId: String) -> Pair<Boolean, String>,
    onDeleteGiveaway: (giveawayId: String) -> Pair<Boolean, String>
) {
    var statusBanner by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var gwTitle by remember { mutableStateOf("🎉 মেগা সানডে ডায়মন্ড গিভওয়ে!") }
    var gwDesc by remember { mutableStateOf("ফ্রি টিকিট নিন — লাইভ লটারি ড্র-এ বিজয়ী ঘোষণা হবে!") }
    var gwPrizeType by remember { mutableStateOf(GiveawayPrizeType.FF_DIAMONDS) }
    var gwPrizeLabel by remember { mutableStateOf("১০০ ডায়মন্ড") }
    var gwCashAmount by remember { mutableStateOf("50") }
    var gwWinnersCount by remember { mutableStateOf("1") }
    var gwMaxEntries by remember { mutableStateOf("50") }
    var announceInChat by remember { mutableStateOf(true) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkCard,
        border = BorderStroke(1.2.dp, NeonPink.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("giveaway_admin_control_card")
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Celebration, contentDescription = "Mega Giveaway Manager", tint = NeonPink, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🎊 মেগা গিভওয়ে ও লাইভ লটারি সিস্টেম",
                    fontWeight = FontWeight.Black,
                    color = FlameYellow,
                    fontSize = 14.sp
                )
            }

            statusBanner?.let { (ok, msg) ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (ok) VictoryGreen.copy(alpha = 0.16f) else FireRed.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, if (ok) VictoryGreen else FireRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = msg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ok) VictoryGreen else Color(0xFFFCA5A5),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Quick Presets
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = false,
                    onClick = {
                        gwTitle = "💎 ১০০ ডায়মন্ড মেগা গিভওয়ে!"
                        gwPrizeType = GiveawayPrizeType.FF_DIAMONDS
                        gwPrizeLabel = "১০০ ডায়মন্ড"
                        gwWinnersCount = "1"
                        gwMaxEntries = "50"
                    },
                    label = { Text("💎 ১০০ ডায়মন্ড", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = false,
                    onClick = {
                        gwTitle = "💰 ৳৫০ লাইভ ক্যাশ লটারি!"
                        gwPrizeType = GiveawayPrizeType.CASH_TAKA
                        gwPrizeLabel = "৳৫০ নগদ"
                        gwCashAmount = "50"
                        gwWinnersCount = "2"
                        gwMaxEntries = "40"
                    },
                    label = { Text("💰 ৳৫০ x ২ বিজয়ী", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = false,
                    onClick = {
                        gwTitle = "👑 উইকলি মেম্বারশিপ গিভওয়ে!"
                        gwPrizeType = GiveawayPrizeType.WEEKLY_PASS
                        gwPrizeLabel = "উইকলি পাস"
                        gwWinnersCount = "1"
                        gwMaxEntries = "100"
                    },
                    label = { Text("👑 উইকলি পাস", fontSize = 11.sp) }
                )
            }

            // Prize Type Chips
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GiveawayPrizeType.entries.forEach { pType ->
                    val selected = gwPrizeType == pType
                    FilterChip(
                        selected = selected,
                        onClick = {
                            gwPrizeType = pType
                            gwPrizeLabel = when (pType) {
                                GiveawayPrizeType.CASH_TAKA -> "৳${gwCashAmount.ifBlank { "50" }} নগদ"
                                GiveawayPrizeType.FF_DIAMONDS -> "১০০ ডায়মন্ড"
                                GiveawayPrizeType.WEEKLY_PASS -> "উইকলি পাস"
                            }
                        },
                        label = { Text("${pType.emoji} ${pType.banglaTitle}", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NeonPink.copy(alpha = 0.28f))
                    )
                }
            }

            OutlinedTextField(
                value = gwTitle,
                onValueChange = { gwTitle = it },
                label = { Text("গিভওয়ে শিরোনাম", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("gw_title_input"),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPink, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )
            OutlinedTextField(
                value = gwDesc,
                onValueChange = { gwDesc = it },
                label = { Text("বিবরণ", fontSize = 12.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPink, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = gwPrizeLabel,
                    onValueChange = { gwPrizeLabel = it },
                    label = { Text("পুরস্কার লেবেল", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("gw_prize_label_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NeonPink, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
                if (gwPrizeType == GiveawayPrizeType.CASH_TAKA) {
                    OutlinedTextField(
                        value = gwCashAmount,
                        onValueChange = { gwCashAmount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("নগদ (৳)", fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(0.8f).testTag("gw_cash_amount_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = gwWinnersCount,
                    onValueChange = { gwWinnersCount = it.filter { ch -> ch.isDigit() } },
                    label = { Text("বিজয়ী সংখ্যা", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
                OutlinedTextField(
                    value = gwMaxEntries,
                    onValueChange = { gwMaxEntries = it.filter { ch -> ch.isDigit() } },
                    label = { Text("সর্বোচ্চ টিকিট", fontSize = 12.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📢 গ্লোবাল চ্যাটে ঘোষণা পিন করুন", fontSize = 12.sp, color = TextPrimary)
                Switch(checked = announceInChat, onCheckedChange = { announceInChat = it })
            }

            Button(
                onClick = {
                    statusBanner = onCreateGiveaway(
                        gwTitle,
                        gwDesc,
                        gwPrizeType,
                        gwPrizeLabel,
                        gwCashAmount.toIntOrNull() ?: 50,
                        gwWinnersCount.toIntOrNull() ?: 1,
                        gwMaxEntries.toIntOrNull() ?: 50,
                        announceInChat
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("launch_giveaway_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
            ) {
                Text("🎉 লাইভ মেগা গিভওয়ে চালু করুন", color = Color.White, fontWeight = FontWeight.Black)
            }

            // Active & Drawn Giveaways List
            giveaways.forEach { gw ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(gw.title, fontSize = 12.sp, fontWeight = FontWeight.Black, color = FlameYellow)
                                Text(
                                    text = "${gw.prizeType.emoji} ${gw.prizeLabel} • টিকিট: ${gw.entries.size}/${gw.maxEntries} • ${gw.status.banglaLabel}",
                                    fontSize = 10.sp,
                                    color = if (gw.status == GiveawayStatus.LIVE) VictoryGreen else TextSecondary
                                )
                            }
                            IconButton(
                                onClick = { statusBanner = onDeleteGiveaway(gw.id) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Giveaway", tint = FireRed)
                            }
                        }

                        if (gw.winners.isNotEmpty()) {
                            Text(
                                text = "🏆 বিজয়ী: ${gw.winners.joinToString(", ") { "${it.ign} (${it.uid})" }}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VictoryGreen
                            )
                        }

                        if (gw.status == GiveawayStatus.LIVE) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { statusBanner = onDrawWinners(gw.id) },
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("draw_giveaway_btn_${gw.id}"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                                ) {
                                    Icon(Icons.Filled.Casino, contentDescription = "Draw Winner", tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("🎲 লটারি ড্র", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                }
                                OutlinedButton(
                                    onClick = { statusBanner = onCloseGiveaway(gw.id) },
                                    modifier = Modifier.heightIn(min = 48.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("বন্ধ", fontSize = 11.sp, color = FlameOrange)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
