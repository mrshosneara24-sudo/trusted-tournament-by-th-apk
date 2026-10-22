package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.UserProfile
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameYellow

data class AvatarItem(
    val id: String,
    val title: String,
    val drawableRes: Int
)

fun getAvailableAvatars(): List<AvatarItem> {
    return listOf(
        AvatarItem("th_logo", "TH অফিসিয়াল লোগো", R.drawable.th_logo),
        AvatarItem("avatar_cyber", "সাইবার ফাইটার", R.drawable.avatar_cyber),
        AvatarItem("avatar_wolf", "লোন উলফ স্পেশাল", R.drawable.avatar_wolf)
    )
}

fun getAvatarDrawableRes(avatarId: String): Int {
    return when (avatarId) {
        "avatar_cyber" -> R.drawable.avatar_cyber
        "avatar_wolf" -> R.drawable.avatar_wolf
        else -> R.drawable.th_logo
    }
}

@Composable
fun EditAccountDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (accountName: String, ign: String, uid: String, phone: String, bkash: String, avatarId: String) -> Unit
) {
    var accountName by remember { mutableStateOf(userProfile.accountName) }
    var ign by remember { mutableStateOf(userProfile.ign) }
    var uid by remember { mutableStateOf(userProfile.uid) }
    var phone by remember { mutableStateOf(userProfile.phone) }
    var bkash by remember { mutableStateOf(userProfile.bkashNumber) }
    var selectedAvatarId by remember { mutableStateOf(userProfile.avatarId) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val avatars = remember { getAvailableAvatars() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 700.dp)
                .testTag("edit_account_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = FlameYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "একাউন্ট তথ্য এডিট করুন",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "আপনার নাম, ছবি ও গেম আইডি আপডেট করুন",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Avatar Photo Selection Section
                Text(
                    text = "প্রোফাইল ছবি / অবতার নির্বাচন করুন:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = FlameYellow
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Current Selected Big Avatar Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(3.dp, FlameYellow, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = getAvatarDrawableRes(selectedAvatarId)),
                            contentDescription = "Selected Avatar",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Avatar Choice List
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(avatars) { item ->
                        val isSelected = item.id == selectedAvatarId
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else DarkBorder)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) FlameYellow else DarkBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedAvatarId = item.id }
                                .padding(8.dp)
                        ) {
                            Box(modifier = Modifier.size(54.dp)) {
                                Image(
                                    painter = painterResource(id = item.drawableRes),
                                    contentDescription = item.title,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(FlameYellow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (isSelected) FlameYellow else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Account Name Field
                OutlinedTextField(
                    value = accountName,
                    onValueChange = {
                        accountName = it
                        errorMessage = null
                    },
                    label = { Text("একাউন্টের নাম (Account Name)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Name",
                            tint = FlameYellow
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = FlameYellow,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Free Fire In-Game Name (IGN)
                OutlinedTextField(
                    value = ign,
                    onValueChange = {
                        ign = it
                        errorMessage = null
                    },
                    label = { Text("ফ্রি ফায়ার গেমের নাম (Free Fire IGN)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.SportsEsports,
                            contentDescription = "IGN",
                            tint = FlameYellow
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_ign_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = FlameYellow,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Free Fire UID
                OutlinedTextField(
                    value = uid,
                    onValueChange = {
                        uid = it
                        errorMessage = null
                    },
                    label = { Text("ফ্রি ফায়ার UID (Player ID)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Badge,
                            contentDescription = "UID",
                            tint = FlameYellow
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_uid_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = FlameYellow,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Number
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        errorMessage = null
                    },
                    label = { Text("মোবাইল নম্বর (Contact Phone)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = "Phone",
                            tint = FlameYellow
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_phone_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = FlameYellow,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Saved bKash Number for payouts
                OutlinedTextField(
                    value = bkash,
                    onValueChange = {
                        bkash = it
                        errorMessage = null
                    },
                    label = { Text("বিকাশ নম্বর (টাকা তোলার জন্য)") },
                    leadingIcon = {
                        Text(
                            text = "৳",
                            color = BkashPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_bkash_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BkashPink,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = BkashPink,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save Action Button
                Button(
                    onClick = {
                        if (accountName.isBlank()) {
                            errorMessage = "অনুগ্রহ করে আপনার নাম দিন"
                            return@Button
                        }
                        if (ign.isBlank()) {
                            errorMessage = "ফ্রি ফায়ার গেমের নাম দিন"
                            return@Button
                        }
                        if (uid.isBlank()) {
                            errorMessage = "ফ্রি ফায়ার প্লেয়ার UID দিন"
                            return@Button
                        }
                        onSaveProfile(accountName, ign, uid, phone, bkash, selectedAvatarId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_account_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "সংরক্ষণ করুন (Save Changes)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}
