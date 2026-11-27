package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppUser
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.model.BadgeType
import com.example.model.UserProfile
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.EmberGold
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.theme.FireWaterBorderGradient
import com.example.ui.theme.FireWaterButtonGradient
import com.example.ui.theme.FireWaterHeaderGradient
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue
import com.example.ui.theme.WaterCyan

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
    onGiveKudos: ((String) -> Int)? = null,
    onGrantBadge: ((String, BadgeType) -> Pair<Boolean, String>)? = null,
    onRevokeBadge: ((String, BadgeType) -> Pair<Boolean, String>)? = null
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
    val freeCount = (match.totalSlots - bookedSlotSet.size).coerceAtLeast(0)
    val fillFraction = (bookedSlotSet.size.toFloat() / match.totalSlots.coerceAtLeast(1)).coerceIn(0f, 1f)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(26.dp))
                .border(2.dp, FireWaterBorderGradient, RoundedCornerShape(26.dp))
                .testTag("room_slot_grid_dialog"),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Fire & Water Esports Arena Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FireWaterHeaderGradient)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            Brush.linearGradient(listOf(FireRed, FireOrange, WaterBlue))
                                        )
                                        .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SportsEsports,
                                        contentDescription = "Slot Arena",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "🔥 লাইভ কাস্টম রুম স্লট ❄️",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = DisplayFontFamily,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "${match.title} • ${match.mapName}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = EmberGold,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }

                        // Live Telemetry Stats Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SlotMetricBadge(
                                label = "মোট স্লট",
                                value = "${match.totalSlots}",
                                accent = WaterCyan,
                                modifier = Modifier.weight(1f)
                            )
                            SlotMetricBadge(
                                label = "বুকড স্লট",
                                value = "${bookedSlotSet.size}",
                                accent = FireOrange,
                                modifier = Modifier.weight(1f)
                            )
                            SlotMetricBadge(
                                label = "ফাঁকা স্লট",
                                value = "$freeCount",
                                accent = VictoryGreen,
                                modifier = Modifier.weight(1f)
                            )
                            SlotMetricBadge(
                                label = "এন্ট্রি ফি",
                                value = "৳${match.entryFee}",
                                accent = EmberGold,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Custom Fire-to-Water Arena Fill Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkElevated)
                        ) {
                            if (fillFraction > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fillFraction)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(FireRed, EmberGold, WaterCyan)
                                            )
                                        )
                                )
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LegendChip(color = VictoryGreen, label = "ফাঁকা (কিনুন)")
                        LegendChip(
                            color = FireRed,
                            label = if (isOwner) "বুকড (ট্যাপে রিফান্ড)" else "বুকড"
                        )
                        LegendChip(color = WaterCyan, label = "আমার স্লট")
                    }

                    if (isOwner) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FlameOrange.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.55f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "👑 ওনার মোড: যেকোনো লাল বুকড স্লটে ট্যাপ করে সরাসরি বাতিল ও ১০০% অটো-রিফান্ড করতে পারবেন।",
                                style = MaterialTheme.typography.labelSmall,
                                color = FlameYellow,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val gridRows = ((match.totalSlots + 4) / 5).coerceAtLeast(1)
                    val gridHeight = (gridRows * 60).coerceIn(112, 250).dp

                    // Beveled Fire & Water Slot Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(gridHeight)
                    ) {
                        items(match.totalSlots) { index ->
                            val slotNum = index + 1
                            val explicitBooking = explicitBookingsMap[slotNum]
                            val isBooked = slotNum in bookedSlotSet
                            val isOwnSlot = explicitBooking != null && (
                                (currentUser.uid.isNotBlank() && explicitBooking.playerUid == currentUser.uid) ||
                                    (currentUser.ign.isNotBlank() && explicitBooking.playerIgn.equals(currentUser.ign, ignoreCase = true))
                                )
                            val canOwnerRefund = isOwner && (onCancelSlotBookingAndRefund != null || onCancelLegacySlot != null)
                            val isClickable = !isBooked || canOwnerRefund

                            val tileBrush = when {
                                isOwnSlot -> Brush.verticalGradient(listOf(Color(0xFF0A3248), Color(0xFF071B2C)))
                                isBooked -> Brush.verticalGradient(listOf(Color(0xFF3A0F1A), Color(0xFF1F0810)))
                                else -> Brush.verticalGradient(listOf(Color(0xFF0D2E25), Color(0xFF081D20)))
                            }
                            val tileBorder = when {
                                isOwnSlot -> WaterCyan
                                isBooked -> if (isOwner) FireOrange else FireRed.copy(alpha = 0.72f)
                                else -> VictoryGreen.copy(alpha = 0.7f)
                            }

                            Box(
                                modifier = Modifier
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(tileBrush)
                                    .border(
                                        width = if (isOwnSlot) 1.6.dp else 1.dp,
                                        color = tileBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable(enabled = isClickable) {
                                        if (!isBooked) {
                                            onSelectSlotToBook(slotNum)
                                        } else if (canOwnerRefund) {
                                            slotToRefundBooking = explicitBooking
                                            slotToRefundNum = slotNum
                                        }
                                    }
                                    .testTag("slot_grid_item_$slotNum"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier.padding(horizontal = 2.dp)
                                ) {
                                    Text(
                                        text = "#$slotNum",
                                        fontFamily = MonoFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = when {
                                            isOwnSlot -> WaterCyan
                                            isBooked -> Color(0xFFFCA5A5)
                                            else -> VictoryGreen
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    if (isOwnSlot) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = "My Slot",
                                                tint = EmberGold,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "আমার",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WaterCyan,
                                                maxLines = 1
                                            )
                                        }
                                    } else if (isBooked) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.Lock,
                                                contentDescription = "Booked",
                                                tint = FireRed,
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = explicitBooking?.playerIgn?.take(4) ?: "বুকড",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.8f),
                                                maxLines = 1
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "ফাঁকা",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = VictoryGreen.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (explicitBookingsMap.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "👥 বুকড স্কোয়াড রোস্টার (ট্যাপে প্লেয়ার প্রোফাইল):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WaterCyan
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
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkCard,
                                    border = BorderStroke(1.dp, WaterCyan.copy(alpha = 0.55f)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { openBookingProfile(booking) }
                                        .testTag("slot_booked_player_chip_${booking.slotNumber}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = null,
                                            tint = EmberGold,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "#${booking.slotNumber} ${booking.playerIgn}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = DarkCard
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .background(
                                    if (firstAvailable != null) FireWaterButtonGradient
                                    else Brush.horizontalGradient(listOf(DarkCard, DarkCard))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.SportsEsports,
                                    contentDescription = "Buy",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (firstAvailable != null) {
                                        "⚡ ফাঁকা স্লট #$firstAvailable বুক করুন (৳${match.entryFee})"
                                    } else {
                                        "সব স্লট পূর্ণ হয়েছে"
                                    },
                                    fontWeight = FontWeight.Black,
                                    color = if (firstAvailable != null) Color.White else TextSecondary
                                )
                            }
                        }
                    }
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
            onGiveKudos = onGiveKudos,
            hasGivenKudos = hasActorGivenKudos(profileData, registeredUsers, currentUser),
            viewerRole = currentUser.role,
            onGrantBadge = onGrantBadge,
            onRevokeBadge = onRevokeBadge
        )
    }
}

@Composable
private fun SlotMetricBadge(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0A101E),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = MonoFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = accent
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LegendChip(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = label, fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}
