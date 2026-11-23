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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.model.BonusGiftDrop
import com.example.model.GiveawayEvent
import com.example.model.GiveawayPrizeType
import com.example.model.LiveCommunityPoll
import com.example.model.PromoCodeItem
import com.example.model.TournamentMatch
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FireRed
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue

@Composable
fun AdminAndOwnerFunControlSection(
    matches: List<TournamentMatch>,
    promoCodes: List<PromoCodeItem>,
    bonusDrops: List<BonusGiftDrop>,
    communityPolls: List<LiveCommunityPoll>,
    onCreatePromoCode: (code: String, title: String, reward: Int, maxUses: Int) -> Pair<Boolean, String>,
    onTogglePromoActive: (promoId: String, active: Boolean) -> Pair<Boolean, String>,
    onDeletePromoCode: (promoId: String) -> Pair<Boolean, String>,
    onLaunchBonusDrop: (title: String, subtitle: String, amountPerClaim: Int, totalBoxes: Int) -> Pair<Boolean, String>,
    onCloseBonusDrop: (dropId: String) -> Pair<Boolean, String>,
    onDeleteBonusDrop: (dropId: String) -> Pair<Boolean, String>,
    onLaunchCommunityPoll: (question: String, options: List<String>) -> Pair<Boolean, String>,
    onCloseCommunityPoll: (pollId: String) -> Pair<Boolean, String>,
    onDeleteCommunityPoll: (pollId: String) -> Pair<Boolean, String>,
    onBroadcastMatchToChat: (matchId: String) -> Pair<Boolean, String>,
    giveaways: List<GiveawayEvent> = emptyList(),
    onCreateGiveaway: (String, String, GiveawayPrizeType, String, Int, Int, Int, Boolean) -> Pair<Boolean, String> = { _, _, _, _, _, _, _, _ -> false to "" },
    onDrawGiveawayWinners: (String) -> Pair<Boolean, String> = { false to "" },
    onCloseGiveaway: (String) -> Pair<Boolean, String> = { false to "" },
    onDeleteGiveaway: (String) -> Pair<Boolean, String> = { false to "" }
) {
    var statusBanner by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    // Bonus drop inputs
    var dropTitle by remember { mutableStateOf("🎉 ফ্ল্যাশ বোনাস রেইন!") }
    var dropSubtitle by remember { mutableStateOf("প্রথম যে ক্লেইম করবে সে সরাসরি ওয়ালেটে পাবে!") }
    var dropAmount by remember { mutableStateOf("5") }
    var dropBoxes by remember { mutableStateOf("10") }

    // Community poll inputs
    var pollQuestion by remember { mutableStateOf("পরের টুর্নামেন্ট কোন ম্যাপে চান?") }
    var pollOpt1 by remember { mutableStateOf("Bermuda") }
    var pollOpt2 by remember { mutableStateOf("Purgatory") }
    var pollOpt3 by remember { mutableStateOf("Kalahari") }
    var pollOpt4 by remember { mutableStateOf("Lone Wolf") }

    // Promo code inputs
    var promoCode by remember { mutableStateOf("") }
    var promoTitle by remember { mutableStateOf("🎉 স্পেশাল রিডিম বোনাস") }
    var promoReward by remember { mutableStateOf("10") }
    var promoMaxUses by remember { mutableStateOf("20") }

    // Match broadcast selector
    var selectedMatchId by remember(matches) { mutableStateOf(matches.firstOrNull()?.id.orEmpty()) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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

        // 0. Live Mega Giveaway & Lottery System
        GiveawayAdminControlCard(
            giveaways = giveaways,
            onCreateGiveaway = onCreateGiveaway,
            onDrawWinners = onDrawGiveawayWinners,
            onCloseGiveaway = onCloseGiveaway,
            onDeleteGiveaway = onDeleteGiveaway
        )

        // 1. Live Bonus Gift Drop (বোনাস রেইন)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CardGiftcard, contentDescription = "Launch Bonus Drop", tint = FlameYellow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🎁 লাইভ বোনাস গিফট ড্রপ (বোনাস রেইন)", fontWeight = FontWeight.Black, color = FlameYellow, fontSize = 14.sp)
                }

                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = false,
                        onClick = {
                            dropTitle = "🎁 স্পিড বোনাস রেইন!"
                            dropAmount = "5"
                            dropBoxes = "10"
                        },
                        label = { Text("৳৫ x ১০ বক্স", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = false,
                        onClick = {
                            dropTitle = "⚡ সুপার ফ্ল্যাশ গিভওয়ে!"
                            dropAmount = "10"
                            dropBoxes = "5"
                        },
                        label = { Text("৳১০ x ৫ বক্স", fontSize = 11.sp) }
                    )
                }

                OutlinedTextField(
                    value = dropTitle,
                    onValueChange = { dropTitle = it },
                    label = { Text("ড্রপ শিরোনাম", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("fun_drop_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameYellow, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
                OutlinedTextField(
                    value = dropSubtitle,
                    onValueChange = { dropSubtitle = it },
                    label = { Text("বিবরণ", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameYellow, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dropAmount,
                        onValueChange = { dropAmount = it.filter { ch -> ch.isDigit() } },
                        label = { Text("প্রতি বক্স (৳)", fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameYellow, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = dropBoxes,
                        onValueChange = { dropBoxes = it.filter { ch -> ch.isDigit() } },
                        label = { Text("মোট বক্স সংখ্যা", fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameYellow, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Button(
                    onClick = {
                        statusBanner = onLaunchBonusDrop(
                            dropTitle,
                            dropSubtitle,
                            dropAmount.toIntOrNull() ?: 5,
                            dropBoxes.toIntOrNull() ?: 10
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("launch_bonus_drop_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
                ) {
                    Text("🚀 লাইভ বোনাস ড্রপ ছাড়ুন", color = Color.Black, fontWeight = FontWeight.Black)
                }

                bonusDrops.forEach { drop ->
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkBorder)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(drop.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    text = "+৳${drop.amountPerClaim} • বাকি: ${drop.remainingBoxes}/${drop.totalBoxes} • ${if (drop.active) "লাইভ" else "বন্ধ"}",
                                    fontSize = 10.sp,
                                    color = if (drop.active) VictoryGreen else TextSecondary
                                )
                            }
                            if (drop.active) {
                                OutlinedButton(
                                    onClick = { statusBanner = onCloseBonusDrop(drop.id) },
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text("বন্ধ", fontSize = 11.sp, color = FlameOrange)
                                }
                            }
                            IconButton(
                                onClick = { statusBanner = onDeleteBonusDrop(drop.id) },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Drop", tint = FireRed)
                            }
                        }
                    }
                }
            }
        }

        // 2. Live Community Poll
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Poll, contentDescription = "Community Poll", tint = FlameCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("📊 লাইভ কমিউনিটি পোল / ভোটিং", fontWeight = FontWeight.Black, color = FlameCyan, fontSize = 14.sp)
                }

                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = false,
                        onClick = {
                            pollQuestion = "পরের ফ্ল্যাশ টুর্নামেন্ট কোন মোডে চান?"
                            pollOpt1 = "Solo (Bermuda)"
                            pollOpt2 = "Duo (Purgatory)"
                            pollOpt3 = "Squad Clash"
                            pollOpt4 = "Lone Wolf 1v1"
                        },
                        label = { Text("মোড পোল", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = false,
                        onClick = {
                            pollQuestion = "রুম ম্যাচ কখন শুরু করলে ভালো হয়?"
                            pollOpt1 = "বিকাল ৫:০০ PM"
                            pollOpt2 = "সন্ধ্যা ৬:৩০ PM"
                            pollOpt3 = "রাত ৮:০০ PM"
                            pollOpt4 = ""
                        },
                        label = { Text("সময় পোল", fontSize = 11.sp) }
                    )
                }

                OutlinedTextField(
                    value = pollQuestion,
                    onValueChange = { pollQuestion = it },
                    label = { Text("পোলের প্রশ্ন", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("fun_poll_question_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = pollOpt1, onValueChange = { pollOpt1 = it }, label = { Text("অপশন ১") }, singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = pollOpt2, onValueChange = { pollOpt2 = it }, label = { Text("অপশন ২") }, singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = pollOpt3, onValueChange = { pollOpt3 = it }, label = { Text("অপশন ৩ (ঐচ্ছিক)") }, singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                    OutlinedTextField(value = pollOpt4, onValueChange = { pollOpt4 = it }, label = { Text("অপশন ৪ (ঐচ্ছিক)") }, singleLine = true, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = FlameCyan, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary))
                }

                Button(
                    onClick = {
                        statusBanner = onLaunchCommunityPoll(pollQuestion, listOf(pollOpt1, pollOpt2, pollOpt3, pollOpt4))
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("launch_poll_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WaterBlue)
                ) {
                    Text("📊 লাইভ পোল প্রকাশ করুন", color = Color.White, fontWeight = FontWeight.Black)
                }

                communityPolls.forEach { poll ->
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkBorder)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(poll.question, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("মোট ভোট: ${poll.totalVotes} • ${if (poll.active) "চলমান" else "সমাপ্ত"}", fontSize = 10.sp, color = TextSecondary)
                            }
                            if (poll.active) {
                                OutlinedButton(onClick = { statusBanner = onCloseCommunityPoll(poll.id) }, modifier = Modifier.heightIn(min = 48.dp)) {
                                    Text("সমাপ্ত", fontSize = 11.sp, color = FlameOrange)
                                }
                            }
                            IconButton(onClick = { statusBanner = onDeleteCommunityPoll(poll.id) }, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Poll", tint = FireRed)
                            }
                        }
                    }
                }
            }
        }

        // 3. Promo Redeem Code Generator
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ConfirmationNumber, contentDescription = "Promo Codes", tint = VictoryGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🎟️ প্রোমো ও গিফট কোড জেনারেটর", fontWeight = FontWeight.Black, color = VictoryGreen, fontSize = 14.sp)
                }

                OutlinedTextField(
                    value = promoCode,
                    onValueChange = { promoCode = it.uppercase().filter { ch -> ch.isLetterOrDigit() } },
                    label = { Text("কোড লিখুন (যেমন: THWIN25)", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("fun_promo_code_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
                OutlinedTextField(
                    value = promoTitle,
                    onValueChange = { promoTitle = it },
                    label = { Text("প্রোমো শিরোনাম", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = promoReward,
                        onValueChange = { promoReward = it.filter { ch -> ch.isDigit() } },
                        label = { Text("বোনাস (৳)", fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                    OutlinedTextField(
                        value = promoMaxUses,
                        onValueChange = { promoMaxUses = it.filter { ch -> ch.isDigit() } },
                        label = { Text("সর্বোচ্চ ইউজ", fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = VictoryGreen, unfocusedBorderColor = DarkBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
                    )
                }

                Button(
                    onClick = {
                        statusBanner = onCreatePromoCode(
                            promoCode,
                            promoTitle,
                            promoReward.toIntOrNull() ?: 10,
                            promoMaxUses.toIntOrNull() ?: 20
                        )
                        if (statusBanner?.first == true) promoCode = ""
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("create_promo_code_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                ) {
                    Text("🎟️ প্রোমো কোড তৈরি ও লাইভ করুন", color = Color.Black, fontWeight = FontWeight.Black)
                }

                promoCodes.forEach { item ->
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkBorder)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${item.code} • +৳${item.rewardAmount}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = FlameYellow)
                                Text("ব্যবহার: ${item.usedCount}/${item.maxUses} জন • ${item.title}", fontSize = 10.sp, color = TextSecondary)
                            }
                            Switch(
                                checked = item.active,
                                onCheckedChange = { statusBanner = onTogglePromoActive(item.id, it) }
                            )
                            IconButton(onClick = { statusBanner = onDeletePromoCode(item.id) }, modifier = Modifier.size(48.dp)) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Promo", tint = FireRed)
                            }
                        }
                    }
                }
            }
        }

        // 4. 1-Tap Broadcast Match to Global Chat
        if (matches.isNotEmpty()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkCard,
                border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.45f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Campaign, contentDescription = "Broadcast Match", tint = FlameOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("📢 গ্লোবাল চ্যাটে ম্যাচ প্রোমো ব্রডকাস্ট", fontWeight = FontWeight.Black, color = FlameOrange, fontSize = 14.sp)
                    }
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        matches.forEach { m ->
                            FilterChip(
                                selected = m.id == selectedMatchId,
                                onClick = { selectedMatchId = m.id },
                                label = { Text(m.title.take(22), fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange.copy(alpha = 0.25f))
                            )
                        }
                    }
                    Button(
                        onClick = {
                            if (selectedMatchId.isNotBlank()) {
                                statusBanner = onBroadcastMatchToChat(selectedMatchId)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("broadcast_match_chat_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                    ) {
                        Text("📢 পিন্ড পোস্ট হিসেবে চ্যাটে পাঠান", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
