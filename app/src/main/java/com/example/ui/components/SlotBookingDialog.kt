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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.DialogProperties
import com.example.model.TournamentMatch
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SlotBookingDialog(
    match: TournamentMatch,
    officialBkashNumber: String,
    initialSlotNumber: Int? = null,
    onDismiss: () -> Unit,
    onConfirmBooking: (slotNumber: Int, ign: String, uid: String, bkashNumber: String, trxId: String) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    var selectedSlot by remember(initialSlotNumber) {
        mutableIntStateOf(initialSlotNumber ?: (match.bookedSlots + 1).coerceAtMost(match.totalSlots))
    }
    var ign by remember { mutableStateOf("TH_Sniper_BD") }
    var uid by remember { mutableStateOf("284710924") }
    var senderBkash by remember { mutableStateOf("") }
    var trxId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVerifyingPayment by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { if (!isVerifyingPayment) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 700.dp)
                .testTag("slot_booking_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "বিকাশ দিয়ে স্লট বুকিং",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                color = Color.White
                            )
                        }
                        Text(
                            text = match.title,
                            style = MaterialTheme.typography.bodySmall.copy(color = FlameYellow)
                        )
                    }

                    if (!isVerifyingPayment) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_booking_dialog_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Official bKash Payment Box with 01861583924
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BkashPink.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BkashPink.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(BkashPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Payment,
                                        contentDescription = "bKash",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "বিকাশ সেন্ড মানি (Send Money)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }

                            Text(
                                text = "ফি: ৳${match.entryFee}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BkashPink
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Copyable bKash Number Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F0F1B))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = officialBkashNumber,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = FlameYellow
                            )

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(officialBkashNumber))
                                    Toast.makeText(context, "বিকাশ নম্বর ($officialBkashNumber) কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("copy_bkash_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "কপি করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "উপরের নম্বরে বিকাশ থেকে ৳${match.entryFee} সেন্ড মানি করুন। অ্যাপ স্বয়ংক্রিয়ভাবে আপনার পেমেন্ট সনাক্ত করে আপনার স্লট বরাদ্দ করবে।",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Slot Number Picker
                Text(
                    text = "পছন্দের স্লট নম্বর নির্বাচন করুন:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val availableSlotsList = (1..match.totalSlots).filter { it > match.bookedSlots }
                    items(if (availableSlotsList.isEmpty()) listOf(1) else availableSlotsList) { slot ->
                        val isSelected = selectedSlot == slot
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) FlameYellow else DarkBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedSlot = slot }
                                .testTag("slot_item_$slot"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#$slot",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input: FF Name (IGN)
                OutlinedTextField(
                    value = ign,
                    onValueChange = { ign = it },
                    label = { Text("ফ্রি ফায়ার নাম (In-Game Name)") },
                    placeholder = { Text("যেমন: TH_Sniper_BD") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Person, contentDescription = "IGN", tint = FlameYellow)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_player_ign"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input: FF UID
                OutlinedTextField(
                    value = uid,
                    onValueChange = { uid = it },
                    label = { Text("ফ্রি ফায়ার ইউআইডি (Player UID)") },
                    placeholder = { Text("যেমন: 284710924") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Tag, contentDescription = "UID", tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_player_uid"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input: Sender bKash Number
                OutlinedTextField(
                    value = senderBkash,
                    onValueChange = { senderBkash = it },
                    label = { Text("যে বিকাশ নম্বর থেকে টাকা পাঠিয়েছেন") },
                    placeholder = { Text("যেমন: 018XXXXXXXX") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.PhoneAndroid, contentDescription = "bKash", tint = BkashPink)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_sender_bkash"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BkashPink,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Input: Transaction ID (TrxID)
                OutlinedTextField(
                    value = trxId,
                    onValueChange = { trxId = it.uppercase() },
                    label = { Text("বিকাশ ট্রানজেকশন আইডি (TrxID)") },
                    placeholder = { Text("যেমন: 9KJ3L49M") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "TrxID", tint = VictoryGreen)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_trx_id"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VictoryGreen,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // TrxID Helper Guide Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = FlameYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "💡 TrxID কী এবং কোথায় পাবেন?",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlameYellow
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "বিকাশ অ্যাপ বা *247# দিয়ে সেন্ড মানি করার পর বিকাশ থেকে আসা কনফার্মেশন SMS-এ বা বিকাশ অ্যাপের 'ইনবক্স > লেনদেন' অপশনে ৮-১০ অক্ষরের একটি ইউনিক কোড থাকে (যেমন: 9KJ3L49M)। এটিই আপনার ট্রানজেকশন আইডি (TrxID)।",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.45f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, VictoryGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "📩 বিকাশ SMS এর নমুনা:",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = VictoryGreen,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "Send Money Tk ${match.entryFee}.00 successful... TrxID 9KJ3L49M",
                                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                                Button(
                                    onClick = {
                                        trxId = "9KJ3L49M"
                                        Toast.makeText(context, "নমুনা TrxID যুক্ত করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen.copy(alpha = 0.25f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VictoryGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(28.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("নমুনা বসান", fontSize = 10.sp, color = VictoryGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color(0xFFEF4444),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isVerifyingPayment) {
                    // Auto-detection status display
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF132E27),
                        border = androidx.compose.foundation.BorderStroke(1.dp, VictoryGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = VictoryGreen,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "বিকাশ পেমেন্ট সনাক্ত করা হচ্ছে...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = VictoryGreen
                                )
                                Text(
                                    text = "স্লট #$selectedSlot এর জন্য ট্রানজেকশন যাচাই চলছে",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color(0xFFA7F3D0)
                                )
                            }
                        }
                    }
                } else {
                    // Submit Button
                    Button(
                        onClick = {
                            if (ign.isBlank()) {
                                errorMessage = "দয়া করে আপনার ফ্রি ফায়ার নাম দিন"
                                return@Button
                            }
                            if (uid.isBlank()) {
                                errorMessage = "দয়া করে আপনার ফ্রি ফায়ার ইউআইডি দিন"
                                return@Button
                            }
                            if (senderBkash.length < 11) {
                                errorMessage = "সঠিক ১১ ডিজিটের বিকাশ নম্বর দিন"
                                return@Button
                            }
                            if (trxId.isBlank()) {
                                errorMessage = "বিকাশ TrxID দিন"
                                return@Button
                            }

                            errorMessage = null
                            isVerifyingPayment = true

                            // Simulate real-time payment detection
                            scope.launch {
                                delay(1200) // Realistic detection delay
                                isVerifyingPayment = false
                                onConfirmBooking(selectedSlot, ign, uid, senderBkash, trxId)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_booking_submit_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BkashPink,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Payment,
                            contentDescription = "Pay",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "পেমেন্ট সনাক্ত ও স্লট বুক করুন (৳${match.entryFee})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = "Auto verify",
                        tint = VictoryGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "পেমেন্ট সনাক্ত হওয়ার সাথে সাথে আপনার স্লট নিশ্চিত হবে",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
