package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppCustomization
import com.example.model.UserRole
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue

private data class AnnouncementPreset(
    val label: String,
    val title: String,
    val message: String,
    val urgent: Boolean
)

@Composable
fun LiveAnnouncementControlSection(
    actorRole: UserRole,
    appCustomization: AppCustomization,
    onPublishAnnouncement: (title: String, message: String, isUrgent: Boolean) -> Pair<Boolean, String>,
    onClearAnnouncement: () -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    var titleInput by remember(appCustomization.announcementTitle, appCustomization.announcementUpdatedAtMillis) {
        mutableStateOf(appCustomization.announcementTitle.ifBlank { "📢 অফিসিয়াল ঘোষণা" })
    }
    var messageInput by remember(appCustomization.activeAnnouncement, appCustomization.announcementUpdatedAtMillis) {
        mutableStateOf(appCustomization.activeAnnouncement)
    }
    var isUrgent by remember(appCustomization.isAnnouncementUrgent, appCustomization.announcementUpdatedAtMillis) {
        mutableStateOf(appCustomization.isAnnouncementUrgent)
    }
    var feedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val presets = remember {
        listOf(
            AnnouncementPreset(
                label = "📲 আপডেট ও WhatsApp",
                title = "📢 অফিসিয়াল ঘোষণা",
                message = com.example.model.OFFICIAL_UPDATE_ANNOUNCEMENT,
                urgent = false
            ),
            AnnouncementPreset(
                label = "🔑 রুম কোড লাইভ",
                title = "🔑 কাস্টম রুম কোড প্রকাশিত!",
                message = "পরবর্তী ম্যাচের রুম আইডি ও পাসওয়ার্ড অ্যাপে দেওয়া হয়েছে! স্লট বুক করা প্লেয়াররা দ্রুত জয়েন করুন।",
                urgent = true
            ),
            AnnouncementPreset(
                label = "⏰ ম্যাচ নোটিশ",
                title = "⏰ ম্যাচ শুরুর সতর্কতা",
                message = "আর মাত্র ১০ মিনিট বাকি! সময়মতো Free Fire রুম লবিতে চলে আসুন, হ্যাকার/টিম-আপ করলে সাথে সাথে ব্যান হবে।",
                urgent = false
            ),
            AnnouncementPreset(
                label = "💸 পেমেন্ট আপডেট",
                title = "💸 বিকাশ/নগদ ও উইথড্র চালু",
                message = "সকল উইথড্র ও ডিপোজিট রিকোয়েস্ট ৫-১০ মিনিটে ভেরিফাই করা হচ্ছে। সঠিক TrxID দিয়ে স্লট বুক করুন!",
                urgent = false
            ),
            AnnouncementPreset(
                label = "🚨 হ্যাক সতর্কতা",
                title = "🚨 কড়া অ্যান্টি-হ্যাক নীতি",
                message = "যেকোনো প্যানেল, ফাইল বা টিম-আপ ধরা পড়লে প্রাইজ বাতিল করে স্থায়ী ব্যান দেওয়া হবে।",
                urgent = true
            )
        )
    }

    val isCurrentlyLive = appCustomization.isAnnouncementEnabled && appCustomization.activeAnnouncement.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_announcement_control_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header & Live Sync Badge
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(
                1.dp,
                if (isCurrentlyLive) FlameCyan.copy(alpha = 0.65f) else DarkBorder
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                if (isUrgent) FireRed.copy(alpha = 0.16f) else FireOrange.copy(alpha = 0.12f),
                                DarkCard,
                                WaterBlue.copy(alpha = 0.14f)
                            )
                        )
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(FlameCyan.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Campaign,
                                contentDescription = "Live Announcement",
                                tint = FlameCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "লাইভ অ্যাপ অ্যানাউন্সমেন্ট",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Black
                                ),
                                color = FlameYellow
                            )
                            Text(
                                text = "${if (actorRole == UserRole.OWNER) "ওনার" else "অ্যাডমিন"} থেকে পাঠানো মাত্র সব প্লেয়ারের স্ক্রিনে সরাসরি লাইভ দেখা যাবে",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCurrentlyLive) VictoryGreen.copy(alpha = 0.2f) else DarkElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isCurrentlyLive) VictoryGreen.copy(alpha = 0.65f) else DarkBorder
                        )
                    ) {
                        Text(
                            text = if (isCurrentlyLive) "🔴 LIVE" else "বন্ধ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = if (isCurrentlyLive) VictoryGreen else TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                if (isCurrentlyLive) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (appCustomization.isAnnouncementUrgent) FireRed.copy(alpha = 0.18f) else DarkElevated,
                        border = BorderStroke(
                            1.dp,
                            if (appCustomization.isAnnouncementUrgent) FireRed.copy(alpha = 0.7f) else FlameYellow.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("announcement_current_preview")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = appCustomization.announcementTitle.ifBlank { "📢 অফিসিয়াল ঘোষণা" },
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (appCustomization.isAnnouncementUrgent) FireRed else FlameYellow
                                )
                                Text(
                                    text = appCustomization.announcementAuthor,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FlameCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = appCustomization.activeAnnouncement,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Quick Preset Chips
        Text(
            text = "১-ট্যাপ রেডি টেমপ্লেট:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = FlameCyan
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEachIndexed { idx, preset ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkElevated,
                    border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .defaultMinSize(minHeight = 40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            titleInput = preset.title
                            messageInput = preset.message
                            isUrgent = preset.urgent
                            feedback = null
                        }
                        .testTag("announcement_preset_$idx")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = preset.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Title Input
        OutlinedTextField(
            value = titleInput,
            onValueChange = {
                titleInput = it.take(100)
                feedback = null
            },
            label = { Text("ঘোষণার শিরোনাম (Title)") },
            placeholder = { Text("যেমন: 🔑 কাস্টম রুম ওপেন / জরুরি নোটিশ") },
            leadingIcon = {
                Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = FlameYellow)
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("announcement_title_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlameYellow,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Message Input
        OutlinedTextField(
            value = messageInput,
            onValueChange = {
                messageInput = it.take(300)
                feedback = null
            },
            label = { Text("সব প্লেয়ারদের জন্য মূল ঘোষণা (সর্বোচ্চ ৩০০ অক্ষর)") },
            placeholder = { Text("রুম আপডেট, প্রাইজ বা জরুরি বার্তা লিখুন যা সব মোবাইলে লাইভ দেখাবে...") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("announcement_message_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlameCyan,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Urgent Alert Switch
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isUrgent) FireRed.copy(alpha = 0.14f) else DarkCard,
            border = BorderStroke(1.dp, if (isUrgent) FireRed.copy(alpha = 0.65f) else DarkBorder),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { isUrgent = !isUrgent }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = if (isUrgent) FireRed else FlameYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "🚨 জরুরি ফায়ার-অ্যালার্ট মোড",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (isUrgent) FireRed else Color.White
                        )
                        Text(
                            text = "চালু করলে লাল-আগুন হাইলাইটসহ সবার স্ক্রিনের উপরে পিন থাকবে",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }
                Switch(
                    checked = isUrgent,
                    onCheckedChange = { isUrgent = it },
                    modifier = Modifier.testTag("announcement_urgent_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = FireRed,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = DarkElevated
                    )
                )
            }
        }

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val res = onClearAnnouncement()
                    feedback = res
                    if (res.first) {
                        messageInput = ""
                        isUrgent = false
                    }
                },
                modifier = Modifier
                    .weight(0.9f)
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("clear_announcement_btn"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, FireRed.copy(alpha = 0.65f))
            ) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = "Clear Announcement", tint = FireRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("বন্ধ করুন", color = FireRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Button(
                onClick = {
                    feedback = onPublishAnnouncement(titleInput, messageInput, isUrgent)
                },
                modifier = Modifier
                    .weight(1.4f)
                    .defaultMinSize(minHeight = 48.dp)
                    .testTag("publish_announcement_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUrgent) FireRed else FlameCyan
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Publish Live Announcement",
                    tint = if (isUrgent) Color.White else Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "📢 লাইভ পাঠান",
                    color = if (isUrgent) Color.White else Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
        }

        feedback?.let { (ok, msg) ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (ok) VictoryGreen.copy(alpha = 0.16f) else FireRed.copy(alpha = 0.16f),
                border = BorderStroke(1.dp, if (ok) VictoryGreen else FireRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("announcement_feedback_banner")
            ) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (ok) VictoryGreen else FireRed,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}
