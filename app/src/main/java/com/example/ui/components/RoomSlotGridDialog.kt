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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.model.AppUser
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.model.UserProfile
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
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
    onCancelSlotBookingAndRefund: ((bookingId: String) -> Unit)? = null,
    onCancelLegacySlot: ((slotNumber: Int) -> Unit)? = null,
    registeredUsers: List<AppUser> = emptyList(),
    currentUser: UserProfile = UserProfile(),
    onPlayerProfileClick: ((PlayerProfileCardData) -> Unit)? = null,
    onGiveKudos: ((String) -> Int)? = null
) {
    var slotToRefundBooking by remember { mutableStateOf<SlotBooking?>(null) }
    var slotToRefundNum by remember { mutableStateOf<Int?>(null) }
    var localPlayerProfileCard by remember { mutableStateOf<PlayerProfileCardData?>(null) }

    val openBookingProfile: (SlotBooking) -> Unit = { booking ->
        val profileData = booking.toPlayerProfileCard(registeredUsers, currentUser)
        if (onPlayerProfileClick != null) {
            onPlayerProfileClick(profileData)
        } else {
            localPlayerProfileCard = profileData
        }
    }

    val explicitBookingsMap = remember(bookings, match.id) {
        bookings.filter { it.matchId == match.id }.associateBy { it.slotNumber }
    }
    val bookedSlotSet = remember(match.bookedSlots, match.totalSlots, explicitBookingsMap) {
        val set = explicitBookingsMap.keys.toMutableSet()
        var candidate = 1
        while (set.size < match.bookedSlots && candidate <= match.totalSlots) {
            if (candidate !in set) {
                set.add(candidate)
            }
            candidate++
        }
        set.toSet()
    }

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
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, BkashPink.copy(alpha = 0.6f))
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
                        val isBooked = slotNum in bookedSlotSet
                        val canOwnerRefund = isOwner && (onCancelSlotBookingAndRefund != null || onCancelLegacySlot != null)
                        val isClickable = !isBooked || canOwnerRefund

                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isBooked) Color(0xFF280B1C) else Color(0xFF0E2921)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isBooked) {
                                        if (isOwner) com.example.ui.theme.NeonPink else Color(0xFF4E1B3B)
                                    } else VictoryGreen.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable(enabled = isClickable) {
                                    if (!isBooked) {
                                        onSelectSlotToBook(slotNum)
                                    } else if (canOwnerRefund) {
                                        slotToRefundBooking = explicitBookingsMap[slotNum]
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
                                        if (isOwner) com.example.ui.theme.BlushPink else Color(0xFFA7829B)
                                    } else VictoryGreen
                                )
                                if (isBooked) {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "Booked",
                                        tint = com.example.ui.theme.NeonPink,
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

                if (explicitBookingsMap.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "👥 বুকড প্লেয়ার (ট্যাপে প্লেয়ার প্রোফাইল):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlameCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        explicitBookingsMap.values.sortedBy { it.slotNumber }.forEach { booking ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkCard,
                                border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.45f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { openBookingProfile(booking) }
                                    .testTag("slot_booked_player_chip_${booking.slotNumber}")
                            ) {
                                Text(
                                    text = "#${booking.slotNumber} ${booking.playerIgn}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val firstAvailable = (1..match.totalSlots).firstOrNull { it !in bookedSlotSet }
                Button(
                    onClick = {
                        if (firstAvailable != null) {
                            onSelectSlotToBook(firstAvailable)
                        }
                    },
                    enabled = firstAvailable != null,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Filled.SportsEsports, contentDescription = "Buy", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (firstAvailable != null) {
                            "যেকোনো ফাঁকা স্লট বুক করুন (৳${match.entryFee})"
                        } else {
                            "সব স্লট পূর্ণ হয়েছে"
                        },
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
                        } else {
                            onCancelLegacySlot?.invoke(slotNum)
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

    localPlayerProfileCard?.let { profileData ->
        PlayerProfileCardDialog(
            profile = profileData,
            onDismiss = { localPlayerProfileCard = null },
            onGiveKudos = onGiveKudos
        )
    }
}
