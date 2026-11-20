package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppUser
import com.example.model.DepositRequest
import com.example.model.RequestStatus
import com.example.model.WalletPaymentMethod
import com.example.model.WithdrawRequest
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.VictoryGreen

@Composable
fun UserBalanceControlContent(
    allUsers: List<AppUser>,
    onAdjustUserBalance: (target: String, amount: Int, isAdd: Boolean, reason: String) -> Boolean,
    onIncrementUserMatchesPlayed: (target: String, delta: Int) -> Boolean = { _, _ -> false },
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var targetInput by remember { mutableStateOf(allUsers.firstOrNull()?.phone ?: "") }
    var amountInput by remember { mutableStateOf("50") }
    var reasonInput by remember { mutableStateOf("") }
    var feedbackMsg by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val filteredUsers = remember(allUsers, searchQuery) {
        if (searchQuery.isBlank()) allUsers
        else allUsers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery) ||
                it.uid.contains(searchQuery) ||
                it.ign.contains(searchQuery, ignoreCase = true)
        }
    }

    val selectedUser = remember(allUsers, targetInput) {
        val key = targetInput.trim()
        allUsers.find { it.phone == key || it.uid == key || it.id == key }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Control Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.2.dp, FlameYellow.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF280D07),
                                    DarkCard,
                                    Color(0xFF062135)
                                )
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 12.dp)
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
                                    .background(FlameYellow.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = FlameYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "প্লেয়ার ব্যালেন্স কন্ট্রোল",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = DisplayFontFamily,
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = FlameYellow
                                )
                                Text(
                                    text = "যেকোনো ইউজার সিলেক্ট করে ব্যালেন্স যোগ/কর্তন করুন",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.78f)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VictoryGreen.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "INSTANT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                ),
                                color = VictoryGreen,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
                Column(modifier = Modifier.padding(14.dp)) {

                OutlinedTextField(
                    value = targetInput,
                    onValueChange = {
                        targetInput = it
                        feedbackMsg = null
                    },
                    label = { Text("ইউজারের ফোন নম্বর বা UID") },
                    placeholder = { Text("01XXXXXXXXX বা UID") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("balance_target_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                if (selectedUser != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.35f),
                        border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${selectedUser.name} (${selectedUser.ign})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "মোট ম্যাচ: ${selectedUser.matchesPlayed} টি • রোল: ${selectedUser.role.banglaTitle}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FlameYellow
                                )
                            }
                            Text(
                                text = "৳${selectedUser.walletBalance}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = VictoryGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Amount Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(10, 20, 50, 100, 500).forEach { amt ->
                        FilterChip(
                            selected = amountInput == amt.toString(),
                            onClick = {
                                amountInput = amt.toString()
                                feedbackMsg = null
                            },
                            label = { Text("৳$amt", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FlameYellow,
                                selectedLabelColor = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = {
                        amountInput = it.filter { ch -> ch.isDigit() }
                        feedbackMsg = null
                    },
                    label = { Text("টাকার পরিমাণ (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("balance_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reasonInput,
                    onValueChange = { reasonInput = it },
                    label = { Text("কারণ / নোট (ঐচ্ছিক)") },
                    placeholder = { Text("যেমন: প্রাইজ মানি / ভুল লেনদেন সংশোধন") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                feedbackMsg?.let { (ok, msg) ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = msg,
                        color = if (ok) VictoryGreen else Color(0xFFEF4444),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val amt = amountInput.toIntOrNull() ?: 0
                            if (targetInput.isBlank() || amt <= 0) {
                                feedbackMsg = false to "সঠিক ইউজার নম্বর ও টাকার পরিমাণ দিন"
                                return@Button
                            }
                            val ok = onAdjustUserBalance(targetInput.trim(), amt, true, reasonInput.trim())
                            feedbackMsg = if (ok) {
                                true to "✅ ৳$amt সফলভাবে যোগ হয়েছে"
                            } else {
                                false to "❌ ইউজার পাওয়া যায়নি বা অকার্যকর তথ্য"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VictoryGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("add_user_balance_btn")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ব্যালেন্স যোগ", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val amt = amountInput.toIntOrNull() ?: 0
                            if (targetInput.isBlank() || amt <= 0) {
                                feedbackMsg = false to "সঠিক ইউজার নম্বর ও টাকার পরিমাণ দিন"
                                return@Button
                            }
                            val ok = onAdjustUserBalance(targetInput.trim(), amt, false, reasonInput.trim())
                            feedbackMsg = if (ok) {
                                true to "✅ ৳$amt সফলভাবে কর্তন হয়েছে"
                            } else {
                                false to "❌ পর্যাপ্ত ব্যালেন্স নেই বা ইউজার পাওয়া যায়নি"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = com.example.ui.theme.FireRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("remove_user_balance_btn")
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ব্যালেন্স কাটুন", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
                }
            }
        }

        // User Search & Quick Pick List
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ইউজার খুঁজুন (নাম, ফোন, UID)...") },
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

        Text(
            text = "নিবন্ধিত ইউজার তালিকা (${filteredUsers.size} জন) — ট্যাপ করে সিলেক্ট করুন",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = FlameYellow
        )

        filteredUsers.forEach { user ->
            val isSelected = user.phone == targetInput.trim() || user.uid == targetInput.trim()
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) FlameYellow.copy(alpha = 0.14f) else DarkCard,
                border = BorderStroke(1.dp, if (isSelected) FlameYellow else DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        targetInput = user.phone
                        feedbackMsg = null
                    }
                    .testTag("balance_user_row_${user.phone}")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${user.name} (${user.ign})",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "ফোন: ${user.phone} • ম্যাচ: ${user.matchesPlayed} টি",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.72f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = VictoryGreen.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = "৳${user.walletBalance}",
                                color = VictoryGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        OutlinedButton(
                            onClick = { onIncrementUserMatchesPlayed(user.phone, 1) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, FlameOrange),
                            modifier = Modifier.height(30.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Filled.SportsEsports, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("+১ ম্যাচ", fontSize = 10.sp, color = FlameOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DepositWithdrawQueueContent(
    depositRequests: List<DepositRequest>,
    withdrawRequests: List<WithdrawRequest>,
    onApproveDeposit: (requestId: String) -> Boolean,
    onRejectDeposit: (requestId: String) -> Boolean,
    onApproveWithdraw: (requestId: String) -> Boolean,
    onRejectWithdraw: (requestId: String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val pendingDeposits = depositRequests.filter { it.status == RequestStatus.PENDING }
    val pendingWithdraws = withdrawRequests.filter { it.status == RequestStatus.PENDING }
    val pendingDepositAmount = pendingDeposits.sumOf { it.amount }
    val pendingWithdrawAmount = pendingWithdraws.sumOf { it.amount }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary Badges with Live Totals
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkCard,
                border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.55f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(VictoryGreen.copy(alpha = 0.14f), Color.Transparent)
                            )
                        )
                        .padding(12.dp)
                ) {
                    Text("অপেক্ষমাণ ডিপোজিট", fontSize = 11.sp, color = Color.White.copy(alpha = 0.78f))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${pendingDeposits.size} টি • ৳$pendingDepositAmount",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Black
                        ),
                        color = VictoryGreen
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkCard,
                border = BorderStroke(1.dp, BkashPink.copy(alpha = 0.55f)),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(BkashPink.copy(alpha = 0.14f), Color.Transparent)
                            )
                        )
                        .padding(12.dp)
                ) {
                    Text("অপেক্ষমাণ উইথড্র", fontSize = 11.sp, color = Color.White.copy(alpha = 0.78f))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${pendingWithdraws.size} টি • ৳$pendingWithdrawAmount",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Black
                        ),
                        color = BkashPink
                    )
                }
            }
        }

        // Deposit Requests Section
        Text(
            text = "📥 ডিপোজিট রিকোয়েস্ট (${depositRequests.size})",
            style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = VictoryGreen
        )

        if (depositRequests.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "এখনো কোনো ডিপোজিট রিকোয়েস্ট আসেনি।",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            depositRequests.forEach { req ->
                val statusColor = when (req.status) {
                    RequestStatus.PENDING -> FlameYellow
                    RequestStatus.APPROVED -> VictoryGreen
                    RequestStatus.REJECTED -> Color(0xFFEF4444)
                }
                val isBkash = req.paymentMethod == WalletPaymentMethod.BKASH
                val methodBadgeColor = if (isBkash) BkashPink else FlameOrange
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_deposit_card_${req.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.55f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${req.userName} (${req.userPhone})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = methodBadgeColor.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, methodBadgeColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = if (isBkash) "💗 bKash" else "🧡 Nagad",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        ),
                                        color = methodBadgeColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "+৳${req.amount}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Black
                                ),
                                color = VictoryGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "সেন্ডার: ${req.senderNumber}  •  TrxID: ${req.trxId}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = FlameYellow,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (req.status == RequestStatus.PENDING) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onApproveDeposit(req.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .testTag("approve_deposit_btn_${req.id}")
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("অনুমোদন", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                                OutlinedButton(
                                    onClick = { onRejectDeposit(req.id) },
                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .testTag("reject_deposit_btn_${req.id}")
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("বাতিল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Text(
                                text = "স্ট্যাটাস: ${req.status.banglaLabel}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = DarkBorder)

        // Withdraw Requests Section
        Text(
            text = "📤 ক্যাশআউট / উইথড্র রিকোয়েস্ট (${withdrawRequests.size})",
            style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = DisplayFontFamily,
                fontWeight = FontWeight.Bold
            ),
            color = BkashPink
        )

        if (withdrawRequests.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "এখনো কোনো উইথড্র রিকোয়েস্ট আসেনি।",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f),
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            withdrawRequests.forEach { req ->
                val statusColor = when (req.status) {
                    RequestStatus.PENDING -> FlameYellow
                    RequestStatus.APPROVED -> VictoryGreen
                    RequestStatus.REJECTED -> Color(0xFFEF4444)
                }
                val isBkash = req.paymentMethod == WalletPaymentMethod.BKASH
                val methodBadgeColor = if (isBkash) BkashPink else FlameOrange
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_withdraw_card_${req.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.55f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${req.userName} (${req.userPhone})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = methodBadgeColor.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, methodBadgeColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = if (isBkash) "💗 bKash" else "🧡 Nagad",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        ),
                                        color = methodBadgeColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "-৳${req.amount}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Black
                                ),
                                color = BkashPink
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${req.paymentMethod.displayName} প্রাপক নম্বর: ${req.targetNumber}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = FlameCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (req.status == RequestStatus.PENDING) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onApproveWithdraw(req.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen, contentColor = Color.Black),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .testTag("approve_withdraw_btn_${req.id}")
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("পেমেন্ট সম্পন্ন", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                                OutlinedButton(
                                    onClick = { onRejectWithdraw(req.id) },
                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .testTag("reject_withdraw_btn_${req.id}")
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("বাতিল ও রিফান্ড", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Text(
                                text = "স্ট্যাটাস: ${req.status.banglaLabel}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }
                    }
                }
            }
        }
    }
}
