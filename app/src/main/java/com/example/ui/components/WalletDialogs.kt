package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun AddMoneyDialog(
    officialBkashNumber: String,
    onDismiss: () -> Unit,
    onConfirmDeposit: (amount: Int, bkashNumber: String, trxId: String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedAmount by remember { mutableIntStateOf(50) }
    var senderBkash by remember { mutableStateOf("") }
    var trxId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_money_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ওয়ালেটে টাকা অ্যাড করুন",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // bKash send money guide
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BkashPink.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "বিকাশ সেন্ড মানি নম্বর (Send Money):",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = officialBkashNumber,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FlameYellow
                                )
                            )
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(officialBkashNumber))
                                    Toast.makeText(context, "নম্বর ($officialBkashNumber) কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("কপি", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "টাকার পরিমাণ নির্বাচন করুন:",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(20, 50, 100, 200).forEach { amt ->
                        FilterChip(
                            selected = selectedAmount == amt,
                            onClick = { selectedAmount = amt },
                            label = { Text("৳$amt", fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = senderBkash,
                    onValueChange = { senderBkash = it },
                    label = { Text("প্রেরক বিকাশ নম্বর") },
                    placeholder = { Text("018XXXXXXXX") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BkashPink,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trxId,
                    onValueChange = { trxId = it.uppercase() },
                    label = { Text("বিকাশ TrxID") },
                    placeholder = { Text("যেমন: 9KM2L8") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = VictoryGreen,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, FlameYellow.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💡 TrxID হলো সেন্ড মানির পর বিকাশ SMS এ পাওয়া ৮-১০ অক্ষরের ট্রানজেকশন কোড (যেমন: 9KM2L8)",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 10.sp
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (senderBkash.length < 11) {
                            errorMessage = "সঠিক বিকাশ নম্বর দিন"
                            return@Button
                        }
                        if (trxId.isBlank()) {
                            errorMessage = "TrxID পূরণ করুন"
                            return@Button
                        }
                        errorMessage = null
                        onConfirmDeposit(selectedAmount, senderBkash, trxId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_add_money_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("টাকা যোগ করুন (৳$selectedAmount)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WithdrawDialog(
    currentBalance: Int,
    onDismiss: () -> Unit,
    onConfirmWithdraw: (amount: Int, bkashNumber: String) -> Unit
) {
    var amountText by remember { mutableStateOf("25") }
    var targetBkash by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("withdraw_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "বিকাশে ক্যাশআউট (উইথড্র)",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
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
                                text = "লিমিট: ৳৫০ - ৳২০০",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BkashPink
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quick amount chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(50, 100, 150, 200).forEach { preset ->
                        val isSelected = amountText == preset.toString()
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) BkashPink else DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) BkashPink else DarkBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    amountText = preset.toString()
                                }
                        ) {
                            Text(
                                text = "৳$preset",
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
                    onValueChange = { amountText = it },
                    label = { Text("উইথড্র পরিমাণ (টাকা)") },
                    placeholder = { Text("৫০ থেকে ২০০ টাকা") },
                    supportingText = { Text("সর্বনিম্ন ৳৫০ এবং সর্বোচ্চ ৳২০০") },
                    modifier = Modifier.fillMaxWidth(),
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
                    value = targetBkash,
                    onValueChange = { targetBkash = it },
                    label = { Text("আপনার বিকাশ পার্সোনাল নম্বর") },
                    placeholder = { Text("018XXXXXXXX") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BkashPink,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMessage!!, color = Color(0xFFEF4444), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val amt = amountText.toIntOrNull() ?: 0
                        if (amt < 50) {
                            errorMessage = "সর্বনিম্ন ৫০ টাকা উইথড্র করা যাবে"
                            return@Button
                        }
                        if (amt > 200) {
                            errorMessage = "সর্বোচ্চ ২০০ টাকা একসাথে উইথড্র করা যাবে"
                            return@Button
                        }
                        if (amt > currentBalance) {
                            errorMessage = "আপনার ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই"
                            return@Button
                        }
                        if (targetBkash.length < 11) {
                            errorMessage = "সঠিক বিকাশ নম্বর দিন (১১ ডিজিট)"
                            return@Button
                        }

                        errorMessage = null
                        onConfirmWithdraw(amt, targetBkash)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_withdraw_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ক্যাশআউট নিশ্চিত করুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
