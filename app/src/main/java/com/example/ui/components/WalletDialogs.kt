package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.WalletPaymentMethod
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.NeonPink
import com.example.ui.theme.VictoryGreen

@Composable
fun AddMoneyDialog(
    officialBkashNumber: String,
    officialNagadNumber: String = officialBkashNumber,
    defaultSenderNumber: String = "",
    onDismiss: () -> Unit,
    onConfirmDeposit: (amount: Int, method: WalletPaymentMethod, senderNumber: String, trxId: String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedMethod by remember { mutableStateOf(WalletPaymentMethod.BKASH) }
    var amountText by remember { mutableStateOf("50") }
    var senderNumber by remember(defaultSenderNumber) { mutableStateOf(defaultSenderNumber) }
    var trxId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val activeOfficialNumber = if (selectedMethod == WalletPaymentMethod.BKASH) officialBkashNumber else officialNagadNumber
    val accentColor = if (selectedMethod == WalletPaymentMethod.BKASH) BkashPink else FlameOrange

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 720.dp)
                .testTag("add_money_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.65f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Premium Gradient Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(accentColor.copy(alpha = 0.28f), DarkSurface)
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "ওয়ালেটে ডিপোজিট করুন",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontFamily = DisplayFontFamily,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = VictoryGreen.copy(alpha = 0.16f),
                                border = BorderStroke(0.8.dp, VictoryGreen.copy(alpha = 0.45f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.VerifiedUser,
                                        contentDescription = null,
                                        tint = VictoryGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "সুরক্ষিত সেন্ড মানি ও তাৎক্ষণিক যাচাই",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VictoryGreen
                                    )
                                }
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    // 3-Step Visual Progress Guide
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DepositStepBadge("১", "নম্বর কপি", accentColor, Modifier.weight(1f))
                        DepositStepBadge("২", "সেন্ড মানি", FlameYellow, Modifier.weight(1f))
                        DepositStepBadge("৩", "TrxID দিন", VictoryGreen, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Payment Method Selector (bKash / Nagad)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        WalletPaymentMethod.entries.forEach { method ->
                            val isSelected = selectedMethod == method
                            val methodColor = if (method == WalletPaymentMethod.BKASH) BkashPink else FlameOrange
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) methodColor.copy(alpha = 0.25f) else DarkCard,
                                border = BorderStroke(if (isSelected) 1.6.dp else 1.dp, if (isSelected) methodColor else DarkBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedMethod = method
                                        errorMessage = null
                                    }
                                    .testTag("deposit_method_${method.name.lowercase()}")
                            ) {
                                Text(
                                    text = if (method == WalletPaymentMethod.BKASH) "💗 বিকাশ (bKash)" else "🧡 নগদ (Nagad)",
                                    modifier = Modifier.padding(vertical = 11.dp, horizontal = 8.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Official Send Money Card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = accentColor.copy(alpha = 0.14f),
                        border = BorderStroke(1.2.dp, accentColor.copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "${selectedMethod.displayName} পার্সোনাল সেন্ড মানি নম্বর:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = activeOfficialNumber,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = MonoFontFamily,
                                        fontWeight = FontWeight.Black,
                                        color = FlameYellow,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(activeOfficialNumber))
                                        Toast.makeText(context, "নম্বর ($activeOfficialNumber) কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("কপি করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ডিপোজিট পরিমাণ নির্বাচন বা লিখুন (সর্বনিম্ন ৳১০):",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf(20, 50, 100, 200, 500)) { amt ->
                        val isSelected = amountText == amt.toString()
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                amountText = amt.toString()
                                errorMessage = null
                            },
                            label = { Text("৳$amt", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = accentColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { ch -> ch.isDigit() }
                        errorMessage = null
                    },
                    label = { Text("টাকার পরিমাণ (৳)") },
                    placeholder = { Text("যেমন: 100") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_amount_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = senderNumber,
                    onValueChange = {
                        senderNumber = it
                        errorMessage = null
                    },
                    label = { Text("যে ${selectedMethod.displayName} নম্বর থেকে পাঠিয়েছেন") },
                    placeholder = { Text("01XXXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_sender_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trxId,
                    onValueChange = {
                        trxId = it.uppercase()
                        errorMessage = null
                    },
                    label = { Text("${selectedMethod.displayName} TrxID (ট্রানজেকশন আইডি)") },
                    placeholder = { Text("যেমন: 9KM2L8X") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_trx_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VictoryGreen,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    border = BorderStroke(0.8.dp, FlameYellow.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 ধাপ: সেন্ড মানি করার পর SMS/অ্যাপ থেকে TrxID কপি করে এখানে সাবমিট করুন। ওনার/অ্যাডমিন যাচাই করে অনুমোদন করলেই ব্যালেন্স ওয়ালেটে যুক্ত হবে।",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                val parsedAmt = amountText.toIntOrNull() ?: 0
                Button(
                    onClick = {
                        if (parsedAmt < 10) {
                            errorMessage = "সর্বনিম্ন ৳১০ ডিপোজিট করা যাবে"
                            return@Button
                        }
                        if (senderNumber.trim().length < 11) {
                            errorMessage = "সঠিক ১১ ডিজিটের ${selectedMethod.displayName} নম্বর দিন"
                            return@Button
                        }
                        if (trxId.trim().length < 4) {
                            errorMessage = "সঠিক TrxID দিন (কমপক্ষে ৪ অক্ষর)"
                            return@Button
                        }
                        errorMessage = null
                        onConfirmDeposit(parsedAmt, selectedMethod, senderNumber.trim(), trxId.trim())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_add_money_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ডিপোজিট রিকোয়েস্ট পাঠান (৳$parsedAmt)", fontWeight = FontWeight.Black, color = Color.White)
                }
                }
            }
        }
    }
}

@Composable
fun WithdrawDialog(
    currentBalance: Int,
    matchesPlayed: Int = 0,
    minWithdraw: Int = 50,
    maxWithdraw: Int = 200,
    defaultBkashNumber: String = "",
    onDismiss: () -> Unit,
    onConfirmWithdraw: (amount: Int, method: WalletPaymentMethod, targetNumber: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf(WalletPaymentMethod.BKASH) }
    var amountText by remember(minWithdraw) { mutableStateOf(minWithdraw.toString()) }
    var targetNumber by remember(defaultBkashNumber) { mutableStateOf(defaultBkashNumber) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val hasPlayedFirstMatch = matchesPlayed >= 1
    val accentColor = if (selectedMethod == WalletPaymentMethod.BKASH) BkashPink else FlameOrange
    val presetAmounts = remember(minWithdraw, maxWithdraw) {
        val mid = ((minWithdraw + maxWithdraw) / 2 / 10) * 10
        listOf(minWithdraw, mid, (minWithdraw + maxWithdraw) / 2, maxWithdraw)
            .filter { it in minWithdraw..maxWithdraw }
            .distinct()
            .sorted()
            .take(4)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 720.dp)
                .testTag("withdraw_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.65f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Premium Cashout Gradient Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(accentColor.copy(alpha = 0.28f), DarkSurface)
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ক্যাশআউট / উইথড্র",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "বিকাশ অথবা নগদ পার্সোনাল নম্বরে দ্রুত টাকা তুলুন",
                                style = MaterialTheme.typography.labelSmall,
                                color = FlameYellow
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {

                // Career 1-Match Rule Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (hasPlayedFirstMatch) VictoryGreen.copy(alpha = 0.14f) else Color(0xFF3A0D18),
                    border = BorderStroke(1.dp, if (hasPlayedFirstMatch) VictoryGreen else NeonPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_career_rule_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (hasPlayedFirstMatch) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                            contentDescription = null,
                            tint = if (hasPlayedFirstMatch) VictoryGreen else NeonPink,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (hasPlayedFirstMatch) {
                                    "✅ প্রথম উইথড্র আনলকড! (ক্যারিয়ার ম্যাচ: $matchesPlayed টি)"
                                } else {
                                    "🔒 প্রথম উইথড্র করতে কমপক্ষে ১টি ম্যাচ খেলতে হবে"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (hasPlayedFirstMatch) VictoryGreen else Color.White
                            )
                            if (!hasPlayedFirstMatch) {
                                Text(
                                    text = "আপনার পুরো অ্যাপ ক্যারিয়ারে এখনও ০টি ম্যাচ খেলেছেন। ওয়ালেট বা বিকাশ দিয়ে যেকোনো ১টি টুর্নামেন্ট ম্যাচ খেললেই উইথড্র চালু হবে।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalanceWallet,
                                contentDescription = "Wallet",
                                tint = FlameYellow
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ব্যালেন্স: ৳$currentBalance",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BkashPink.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "লিমিট: ৳$minWithdraw - ৳$maxWithdraw",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FlameYellow
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Method selector (bKash / Nagad)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WalletPaymentMethod.entries.forEach { method ->
                        val isSelected = selectedMethod == method
                        val methodColor = if (method == WalletPaymentMethod.BKASH) BkashPink else FlameOrange
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) methodColor.copy(alpha = 0.25f) else DarkCard,
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) methodColor else DarkBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedMethod = method
                                    errorMessage = null
                                }
                        ) {
                            Text(
                                text = if (method == WalletPaymentMethod.BKASH) "💗 বিকাশ" else "🧡 নগদ",
                                modifier = Modifier.padding(vertical = 9.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick amount chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetAmounts.forEach { preset ->
                        val isSelected = amountText == preset.toString()
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) accentColor else DarkSurface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) NeonPink else DarkBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    amountText = preset.toString()
                                    errorMessage = null
                                }
                        ) {
                            Text(
                                text = "৳$preset",
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.85f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { ch -> ch.isDigit() }
                        errorMessage = null
                    },
                    label = { Text("উইথড্র পরিমাণ (টাকা)") },
                    placeholder = { Text("$minWithdraw থেকে $maxWithdraw টাকা") },
                    supportingText = { Text("সর্বনিম্ন ৳$minWithdraw এবং সর্বোচ্চ ৳$maxWithdraw") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_amount_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = DarkBorder,
                        focusedSupportingTextColor = Color.White.copy(alpha = 0.7f),
                        unfocusedSupportingTextColor = Color.White.copy(alpha = 0.6f)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetNumber,
                    onValueChange = {
                        targetNumber = it
                        errorMessage = null
                    },
                    label = { Text("আপনার ${selectedMethod.displayName} পার্সোনাল নম্বর") },
                    placeholder = { Text("01XXXXXXXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_bkash_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                    val parsedCashout = amountText.toIntOrNull() ?: 0
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkElevated,
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("প্রাপ্তির মাধ্যম: ${selectedMethod.displayName}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.78f))
                                Text("প্রাপক: ${targetNumber.ifBlank { "নম্বর দিন" }}", fontSize = 11.sp, fontFamily = MonoFontFamily, color = FlameCyan)
                            }
                            Text(
                                text = "৳$parsedCashout",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = VictoryGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (!hasPlayedFirstMatch) {
                                errorMessage = "প্রথমবার উইথড্র করতে কমপক্ষে ১টি ম্যাচ খেলতে হবে!"
                                return@Button
                            }
                            val amt = amountText.toIntOrNull() ?: 0
                            if (amt < minWithdraw) {
                                errorMessage = "সর্বনিম্ন $minWithdraw টাকা উইথড্র করা যাবে"
                                return@Button
                            }
                            if (amt > maxWithdraw) {
                                errorMessage = "সর্বোচ্চ $maxWithdraw টাকা একসাথে উইথড্র করা যাবে"
                                return@Button
                            }
                            if (amt > currentBalance) {
                                errorMessage = "আপনার ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই"
                                return@Button
                            }
                            val cleanNum = targetNumber.trim()
                            if (cleanNum.length != 11 || !cleanNum.startsWith("01") || !cleanNum.all { it.isDigit() }) {
                                errorMessage = "সঠিক ${selectedMethod.displayName} নম্বর দিন (১১ ডিজিট)"
                                return@Button
                            }

                            errorMessage = null
                            onConfirmWithdraw(amt, selectedMethod, cleanNum)
                        },
                        enabled = hasPlayedFirstMatch,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_withdraw_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            disabledContainerColor = DarkCard,
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (hasPlayedFirstMatch) "ক্যাশআউট নিশ্চিত করুন" else "🔒 কমপক্ষে ১টি ম্যাচ খেলুন",
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DepositStepBadge(
    stepNum: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkCard,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNum,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = accent
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}
