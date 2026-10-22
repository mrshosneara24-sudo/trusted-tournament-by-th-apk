package com.example.ui.components

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.Cancel
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun RoomSlotGridDialog(
    match: TournamentMatch,
    isOwner: Boolean = false,
    bookings: List<SlotBooking> = emptyList(),
    onDismiss: () -> Unit,
    onSelectSlotToBook: (Int) -> Unit,
    onCancelSlotBookingAndRefund: ((bookingId: String) -> Unit)? = null
) {
    var slotToRefundBooking by remember { mutableStateOf<SlotBooking?>(null) }
    var slotToRefundNum by remember { mutableStateOf<Int?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 660.dp)
                .testTag("room_slot_grid_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "লাইভ কাস্টম রুম স্লট গ্রিড",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${match.title} (${match.totalSlots} স্লট)",
                            style = MaterialTheme.typography.bodySmall.copy(color = FlameYellow)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(VictoryGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "ফাঁকা স্লট (কিনুন)", fontSize = 11.sp, color = Color.White)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOwner) "বুকড (ট্যাপে রিফান্ড)" else "বুকড",
                            fontSize = 11.sp,
                            color = if (isOwner) FlameOrange else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isOwner) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FlameOrange.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "👑 ওনার মোড: যেকোনো লাল বুকড স্লটে ট্যাপ করে সরাসরি বাতিল ও প্লেয়ারের ওয়ালেটে ১০০% অটো-রিফান্ড করতে পারবেন।",
                            style = MaterialTheme.typography.labelSmall,
                            color = FlameYellow,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Slots Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(match.totalSlots) { index ->
                        val slotNum = index + 1
                        val isBooked = slotNum <= match.bookedSlots
                        val isClickable = !isBooked || (isOwner && onCancelSlotBookingAndRefund != null)

                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isBooked) Color(0xFF201524) else Color(0xFF0F3124)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isBooked) {
                                        if (isOwner) Color(0xFFEF4444) else Color(0xFF3F2034)
                                    } else VictoryGreen.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(enabled = isClickable) {
                                    if (!isBooked) {
                                        onSelectSlotToBook(slotNum)
                                    } else if (isOwner && onCancelSlotBookingAndRefund != null) {
                                        val found = bookings.find { it.matchId == match.id && it.slotNumber == slotNum }
                                        slotToRefundBooking = found
                                        slotToRefundNum = slotNum
                                    }
                                }
                                .testTag("slot_grid_item_$slotNum"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "#$slotNum",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBooked) {
                                        if (isOwner) Color(0xFFFCA5A5) else Color(0xFF94A3B8)
                                    } else VictoryGreen
                                )
                                if (isBooked) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "Booked",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else {
                                    Text(
                                        text = "ফাঁকা",
                                        fontSize = 9.sp,
                                        color = VictoryGreen
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val firstAvailable = (match.bookedSlots + 1).coerceAtMost(match.totalSlots)
                        onSelectSlotToBook(firstAvailable)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Filled.SportsEsports, contentDescription = "Buy", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "যেকোনো ফাঁকা স্লট বুক করুন (৳${match.entryFee})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Owner Refund Confirmation from Grid
    slotToRefundNum?.let { slotNum ->
        val booking = slotToRefundBooking
        AlertDialog(
            onDismissRequest = {
                slotToRefundNum = null
                slotToRefundBooking = null
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Cancel, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ওনার: স্লট #$slotNum বাতিল ও রিফান্ড", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "আপনি কি নিশ্চিত যে স্লট #$slotNum বাতিল করতে চান?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "ম্যাচ: ${match.title}\nস্লট ফি: ৳${booking?.feePaid ?: match.entryFee}\nপ্লেয়ার: ${booking?.playerIgn ?: "বুকড প্লেয়ার"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FlameYellow
                    )
                    Text(
                        text = "বাতিল করলে প্লেয়ারের ওয়ালেটে স্বয়ংক্রিয়ভাবে ৳${booking?.feePaid ?: match.entryFee} রিফান্ড যোগ হবে এবং স্লটটি ফাঁকা হয়ে যাবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = VictoryGreen
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bookingId = booking?.bookingId ?: ""
                        if (bookingId.isNotBlank()) {
                            onCancelSlotBookingAndRefund?.invoke(bookingId)
                        }
                        slotToRefundNum = null
                        slotToRefundBooking = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("হ্যাঁ, বাতিল ও রিফান্ড করুন", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    slotToRefundNum = null
                    slotToRefundBooking = null
                }) {
                    Text("ফিরে যান", color = Color.White.copy(alpha = 0.7f))
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
