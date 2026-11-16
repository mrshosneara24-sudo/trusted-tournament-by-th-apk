package com.example.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppUser
import com.example.model.AppealStatus
import com.example.model.BadgeAppeal
import com.example.model.BadgeType
import com.example.model.UserProfile
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun BadgeAppealRequestDialog(
    userProfile: UserProfile,
    myAppeals: List<BadgeAppeal>,
    initialBadge: BadgeType = BadgeType.PRO_PLAYER,
    onDismiss: () -> Unit,
    onSubmitAppeal: (requestedBadge: BadgeType, reasonNote: String) -> Pair<Boolean, String>,
    onSendAppealChat: (appealId: String, text: String, proofImageUri: String?) -> Pair<Boolean, String> = { _, _, _ -> false to "" }
) {
    var selectedBadge by remember(initialBadge) { mutableStateOf(initialBadge) }
    var reasonInput by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var activeChatAppealId by remember { mutableStateOf<String?>(null) }

    val requirementMet = selectedBadge.isRequirementMet(userProfile)
    val progressText = selectedBadge.requirementProgressText(userProfile)
    val alreadyHasBadge = selectedBadge in userProfile.earnedBadges
    val hasPendingForSelected = myAppeals.any {
        it.status == AppealStatus.PENDING && it.requestedBadge == selectedBadge
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, FlameCyan, RoundedCornerShape(24.dp))
                .testTag("badge_appeal_form_dialog"),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(FlameCyan.copy(alpha = 0.2f))
                                .border(1.dp, FlameCyan.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MilitaryTech,
                                contentDescription = "Badge Appeal",
                                tint = FlameCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "📩 ব্যাজের জন্য আবেদন (Appeal)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = FlameCyan
                            )
                            Text(
                                text = "শর্ত পূরণ হলে আবেদন করুন ও চ্যাটে প্রুফ দিন",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.72f)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Warning / 7-Day Ban Rule Notice Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (userProfile.isBanned) BkashPink.copy(alpha = 0.2f) else FlameOrange.copy(alpha = 0.14f),
                        border = BorderStroke(1.dp, if (userProfile.isBanned) BkashPink else FlameOrange.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("appeal_warning_status_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (userProfile.isBanned) Icons.Filled.Block else Icons.Filled.Warning,
                                contentDescription = null,
                                tint = if (userProfile.isBanned) BkashPink else FlameYellow,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (userProfile.isBanned) {
                                        "🚫 আপনার অ্যাকাউন্ট ব্যান আছে (ওয়ার্নিং: ${userProfile.warningsCount})"
                                    } else {
                                        "⚠️ বর্তমান ওয়ার্নিং: ${userProfile.warningsCount}/৩টি"
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = if (userProfile.isBanned) BkashPink else FlameYellow
                                )
                                Text(
                                    text = if (userProfile.isBanned) {
                                        userProfile.banReason ?: "৩টির বেশি ওয়ার্নিং পাওয়ায় ৭ দিনের জন্য অ্যাপ ব্যান করা হয়েছে।"
                                    } else {
                                        "সতর্কতা: শর্ত পূরণ না করে মিথ্যা আবেদন করলে আবেদন বাতিল হয়ে +১ ওয়ার্নিং যুক্ত হবে! ৩টির বেশি ওয়ার্নিং পেলে ৭ দিনের জন্য অ্যাপ ব্যান পাবেন।"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.88f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Select Target Badge
                    Text(
                        text = "কোন ব্যাজের জন্য আবেদন করবেন তা নির্বাচন করুন:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = FlameYellow
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BadgeType.values().forEach { badge ->
                            val isSelected = selectedBadge == badge
                            val met = badge.isRequirementMet(userProfile)
                            val badgeColor = Color(badge.hexColor)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) badgeColor.copy(alpha = 0.22f) else DarkCard,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) badgeColor else DarkBorder
                                ),
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedBadge = badge
                                        feedback = null
                                    }
                                    .testTag("appeal_select_badge_${badge.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BadgeEmblemArt(badge = badge, size = 28.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = badge.tagText,
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                            color = if (isSelected) badgeColor else Color.White
                                        )
                                        Text(
                                            text = if (met) "শর্ত পূরণ ✓" else "শর্ত বাকি",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = if (met) VictoryGreen else Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Selected Badge Details & Live Stats Check
                    val badgeColor = Color(selectedBadge.hexColor)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.65f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BadgeEmblemArt(badge = selectedBadge, size = 44.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedBadge.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                        color = badgeColor
                                    )
                                    Text(
                                        text = "🎯 শর্ত: ${selectedBadge.criteria}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (requirementMet) VictoryGreen.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.06f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (requirementMet) {
                                        "✅ আপনার অগ্রগতি ($progressText) — শর্ত পূরণ হয়েছে!"
                                    } else {
                                        "📊 আপনার অগ্রগতি ($progressText) — শর্ত সম্পূর্ণ পূরণ না হলে ডিনাই ও ওয়ার্নিং পেতে পারেন"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (requirementMet) VictoryGreen else FlameYellow,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "ম্যাচ: ${userProfile.matchesPlayed} • খরচ: ৳${userProfile.totalSpent} • বুইয়া: ${userProfile.totalWins} • কিল: ${userProfile.totalKills} • আয়: ৳${userProfile.totalEarnings}",
                                style = MaterialTheme.typography.labelSmall,
                                color = FlameCyan
                            )
                        }
                    }

                    // Proof / Reason Input
                    OutlinedTextField(
                        value = reasonInput,
                        onValueChange = {
                            reasonInput = it
                            feedback = null
                        },
                        label = { Text("আবেদনের কারণ / প্রমাণ লিখুন") },
                        placeholder = { Text("যেমন: আমি ${selectedBadge.criteria} সম্পন্ন করেছি, দয়া করে যাচাই করুন") },
                        minLines = 2,
                        maxLines = 4,
                        enabled = !userProfile.isBanned,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("badge_appeal_reason_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = FlameCyan,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    if (alreadyHasBadge) {
                        Text(
                            text = "ℹ️ আপনার প্রোফাইলে এই ব্যাজটি ইতিমধ্যেই সক্রিয় আছে, তবে প্রয়োজন হলে অন্য ব্যাজের জন্য আবেদন করতে পারেন।",
                            style = MaterialTheme.typography.labelSmall,
                            color = VictoryGreen
                        )
                    }

                    feedback?.let { (ok, msg) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (ok) VictoryGreen.copy(alpha = 0.16f) else BkashPink.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, if (ok) VictoryGreen else BkashPink),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("badge_appeal_submit_feedback")
                        ) {
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val note = reasonInput.trim().ifBlank {
                                "আমি ${selectedBadge.title} এর শর্ত (${selectedBadge.criteria}) পূরণ করেছি"
                            }
                            val res = onSubmitAppeal(selectedBadge, note)
                            feedback = res
                            if (res.first) {
                                reasonInput = ""
                            }
                        },
                        enabled = !userProfile.isBanned && !hasPendingForSelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_badge_appeal_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FlameCyan,
                            contentColor = Color.Black,
                            disabledContainerColor = DarkCard
                        )
                    ) {
                        Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when {
                                userProfile.isBanned -> "ব্যান থাকায় আবেদন বন্ধ"
                                hasPendingForSelected -> "এই ব্যাজের আবেদন অপেক্ষমাণ আছে"
                                else -> "আবেদন পাঠান (Submit Appeal)"
                            },
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Player's Own Appeal History with Live Chat / Talk to Owner & Admin
                    if (myAppeals.isNotEmpty()) {
                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "আপনার পূর্ববর্তী ব্যাজ আবেদনসমূহ (${myAppeals.size}টি):",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = FlameYellow
                        )
                        myAppeals.forEach { appeal ->
                            val statusColor = when (appeal.status) {
                                AppealStatus.PENDING -> FlameYellow
                                AppealStatus.APPROVED -> VictoryGreen
                                AppealStatus.REJECTED -> BkashPink
                            }
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkCard),
                                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("my_badge_appeal_row_${appeal.id}")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BadgePill(badge = appeal.grantedBadge ?: appeal.requestedBadge, compact = true)
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = statusColor.copy(alpha = 0.18f)
                                        ) {
                                            Text(
                                                text = appeal.status.banglaLabel,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                                color = statusColor,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "নোট: ${appeal.reasonNote}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                    if (appeal.adminNote.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = appeal.adminNote,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = statusColor
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { activeChatAppealId = appeal.id },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("player_appeal_chat_btn_${appeal.id}"),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.7f)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FlameCyan)
                                    ) {
                                        Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "💬 ওনার/অ্যাডমিনের সাথে কথা ও প্রুফ দিন (${appeal.chatMessages.size})",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Text("বন্ধ করুন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    val liveChatAppeal = activeChatAppealId?.let { id -> myAppeals.firstOrNull { it.id == id } }
    if (liveChatAppeal != null) {
        BadgeAppealChatDialog(
            appeal = liveChatAppeal,
            isStaffReviewer = false,
            userWarnings = userProfile.warningsCount,
            isUserBanned = userProfile.isBanned,
            onDismiss = { activeChatAppealId = null },
            onSendMessage = { text, uri -> onSendAppealChat(liveChatAppeal.id, text, uri) }
        )
    }
}

@Composable
fun BadgeAppealsReviewSection(
    appeals: List<BadgeAppeal>,
    allUsers: List<AppUser>,
    onApproveAppeal: (appealId: String, selectedBadge: BadgeType) -> Pair<Boolean, String>,
    onDenyAppeal: (appealId: String, reason: String) -> Pair<Boolean, String>,
    onSendAppealChat: (appealId: String, text: String, proofImageUri: String?) -> Pair<Boolean, String> = { _, _, _ -> false to "" },
    modifier: Modifier = Modifier
) {
    var acceptTargetAppeal by remember { mutableStateOf<BadgeAppeal?>(null) }
    var selectedGrantBadge by remember { mutableStateOf(BadgeType.PRO_PLAYER) }
    var denyTargetAppeal by remember { mutableStateOf<BadgeAppeal?>(null) }
    var denyReasonInput by remember { mutableStateOf("") }
    var activeChatAppealId by remember { mutableStateOf<String?>(null) }
    var actionBanner by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    val sortedAppeals = remember(appeals) {
        appeals.sortedBy { if (it.status == AppealStatus.PENDING) 0 else 1 }
    }
    val pendingCount = appeals.count { it.status == AppealStatus.PENDING }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("badge_appeals_review_section"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.55f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📩 প্লেয়ার ব্যাজ আবেদন যাচাই",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = FlameCyan
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (pendingCount > 0) FlameYellow.copy(alpha = 0.22f) else VictoryGreen.copy(alpha = 0.16f)
                    ) {
                        Text(
                            text = "অপেক্ষমাণ: $pendingCount",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                            color = if (pendingCount > 0) FlameYellow else VictoryGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💬 Chat with User-এ গিয়ে প্লেয়ারের সাথে কথা বলে প্রুফ যাচাই করুন এবং উপরের Accept/Deny বাটন চাপুন। Accept-এ ব্যাজ সিলেক্ট করলে তা অটোমেটিক চলে যাবে। Deny-তে +১ ওয়ার্ন ও ৩টির বেশি ওয়ার্নে ৭ দিন অ্যাপ ব্যান!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        actionBanner?.let { (ok, msg) ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (ok) VictoryGreen.copy(alpha = 0.16f) else BkashPink.copy(alpha = 0.18f),
                border = BorderStroke(1.dp, if (ok) VictoryGreen else BkashPink),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("badge_appeal_review_feedback")
            ) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        if (sortedAppeals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏅", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "এখনো কোনো ব্যাজ আবেদন আসেনি",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "কোনো প্লেয়ার শর্ত পূরণ করে Appeal for Badge করলে এখানে দেখা যাবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
            }
        } else {
            sortedAppeals.forEach { appeal ->
                val userRecord = allUsers.firstOrNull { u ->
                    (appeal.userId.isNotBlank() && u.id == appeal.userId) ||
                        (appeal.userPhone.isNotBlank() && u.phone == appeal.userPhone) ||
                        (appeal.userUid.isNotBlank() && u.uid == appeal.userUid)
                }
                val liveWarnings = userRecord?.warningsCount ?: 0
                val isUserBanned = userRecord?.isBanned == true
                val statusColor = when (appeal.status) {
                    AppealStatus.PENDING -> FlameYellow
                    AppealStatus.APPROVED -> VictoryGreen
                    AppealStatus.REJECTED -> BkashPink
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("badge_appeal_card_${appeal.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(
                        width = if (appeal.status == AppealStatus.PENDING) 1.5.dp else 1.dp,
                        color = statusColor.copy(alpha = 0.7f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${appeal.userIgn} (${appeal.userName})",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "UID: ${appeal.userUid} • ফোন: ${appeal.userPhone}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FlameCyan
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = statusColor.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = appeal.status.banglaLabel,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BadgePill(badge = appeal.requestedBadge, compact = true)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isUserBanned) BkashPink.copy(alpha = 0.2f) else FlameOrange.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = if (isUserBanned) "🚫 ব্যানড (${liveWarnings} ওয়ার্নিং)" else "⚠️ ওয়ার্নিং: $liveWarnings/৩",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                    color = if (isUserBanned) BkashPink else FlameYellow,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                val mPlayed = userRecord?.matchesPlayed ?: appeal.matchesPlayedSnapshot
                                val mSpent = userRecord?.totalSpent ?: appeal.totalSpentSnapshot
                                val mWins = userRecord?.totalWins ?: appeal.totalWinsSnapshot
                                val mKills = userRecord?.totalKills ?: appeal.totalKillsSnapshot
                                val mEarned = userRecord?.totalEarnings ?: appeal.totalEarningsSnapshot
                                Text(
                                    text = "📊 ম্যাচ: $mPlayed • খরচ: ৳$mSpent • বুইয়া: $mWins • কিল: $mKills • আয়: ৳$mEarned",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = FlameYellow
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "💬 প্লেয়ারের নোট: ${appeal.reasonNote}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.88f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (appeal.adminNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = appeal.adminNote,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Chat with User / Talk with User button (Proof verification & conversation)
                        Button(
                            onClick = { activeChatAppealId = appeal.id },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("chat_with_user_btn_${appeal.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FlameCyan,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "💬 Chat with User / প্রুফ যাচাই (${appeal.chatMessages.size})",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }

                        if (appeal.status == AppealStatus.PENDING) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        selectedGrantBadge = appeal.requestedBadge
                                        acceptTargetAppeal = appeal
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("accept_badge_appeal_btn_${appeal.id}"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                                ) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Accept", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        denyReasonInput = ""
                                        denyTargetAppeal = appeal
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("deny_badge_appeal_btn_${appeal.id}"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BkashPink),
                                    border = BorderStroke(1.dp, BkashPink)
                                ) {
                                    Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Deny + ওয়ার্ন", fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Live Appeal Proof Chat Modal (with Sticky TOP Accept & Deny buttons for Owner & Admin!)
    val liveChatAppeal = activeChatAppealId?.let { id -> appeals.firstOrNull { it.id == id } }
    if (liveChatAppeal != null) {
        val chatUser = allUsers.firstOrNull { u ->
            (liveChatAppeal.userId.isNotBlank() && u.id == liveChatAppeal.userId) ||
                (liveChatAppeal.userPhone.isNotBlank() && u.phone == liveChatAppeal.userPhone) ||
                (liveChatAppeal.userUid.isNotBlank() && u.uid == liveChatAppeal.userUid)
        }
        BadgeAppealChatDialog(
            appeal = liveChatAppeal,
            isStaffReviewer = true,
            userWarnings = chatUser?.warningsCount ?: 0,
            isUserBanned = chatUser?.isBanned == true,
            onDismiss = { activeChatAppealId = null },
            onSendMessage = { text, uri -> onSendAppealChat(liveChatAppeal.id, text, uri) },
            onTopAcceptClick = {
                selectedGrantBadge = liveChatAppeal.requestedBadge
                acceptTargetAppeal = liveChatAppeal
            },
            onTopDenyClick = {
                denyReasonInput = ""
                denyTargetAppeal = liveChatAppeal
            }
        )
    }

    // Accept -> Select Badge to Grant Dialog
    acceptTargetAppeal?.let { appeal ->
        Dialog(
            onDismissRequest = { acceptTargetAppeal = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 640.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.5.dp, VictoryGreen, RoundedCornerShape(22.dp))
                    .testTag("select_grant_badge_dialog"),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "🏅 কোন ব্যাজ দিবেন তা নির্বাচন করুন",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = VictoryGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "প্লেয়ার: ${appeal.userIgn} (${appeal.userPhone}) • আবেদনকৃত: ${appeal.requestedBadge.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FlameYellow
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(BadgeType.values().toList(), key = { it.name }) { badge ->
                            val isChosen = selectedGrantBadge == badge
                            val badgeColor = Color(badge.hexColor)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isChosen) badgeColor.copy(alpha = 0.2f) else DarkCard,
                                border = BorderStroke(
                                    width = if (isChosen) 1.5.dp else 1.dp,
                                    color = if (isChosen) badgeColor else DarkBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedGrantBadge = badge }
                                    .testTag("grant_appeal_badge_option_${badge.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isChosen,
                                        onClick = { selectedGrantBadge = badge },
                                        colors = RadioButtonDefaults.colors(selectedColor = badgeColor, unselectedColor = Color.White.copy(alpha = 0.5f))
                                    )
                                    BadgeEmblemArt(badge = badge, size = 34.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = badge.title,
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = badgeColor
                                        )
                                        Text(
                                            text = badge.criteria,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 10.sp
                                        )
                                    }
                                    if (isChosen) {
                                        Icon(Icons.Filled.Check, contentDescription = "Selected", tint = badgeColor, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { acceptTargetAppeal = null },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("বাতিল", color = Color.White)
                        }
                        Button(
                            onClick = {
                                val res = onApproveAppeal(appeal.id, selectedGrantBadge)
                                actionBanner = res
                                acceptTargetAppeal = null
                            },
                            modifier = Modifier
                                .weight(1.6f)
                                .height(48.dp)
                                .testTag("confirm_grant_selected_badge_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                        ) {
                            Text("নিশ্চিত ও ব্যাজ দিন", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }

    // Deny -> +1 Warning (and 7-day ban if > 3) Confirmation Dialog
    denyTargetAppeal?.let { appeal ->
        val userWarningsBefore = allUsers.firstOrNull { u ->
            (appeal.userId.isNotBlank() && u.id == appeal.userId) ||
                (appeal.userPhone.isNotBlank() && u.phone == appeal.userPhone) ||
                (appeal.userUid.isNotBlank() && u.uid == appeal.userUid)
        }?.warningsCount ?: 0
        val nextWarnings = userWarningsBefore + 1
        val willBan7Days = nextWarnings > 3

        AlertDialog(
            onDismissRequest = { denyTargetAppeal = null },
            containerColor = DarkSurface,
            modifier = Modifier.testTag("deny_appeal_confirm_dialog"),
            title = {
                Text(
                    text = if (willBan7Days) "🚫 আবেদন বাতিল ও ৭ দিনের ব্যান!" else "⚠️ আবেদন বাতিল ও +১ ওয়ার্নিং",
                    color = BkashPink,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${appeal.userIgn}-এর '${appeal.requestedBadge.title}' আবেদন বাতিল করলে তার ওয়ার্নিং $userWarningsBefore থেকে ${nextWarnings}টি হবে।",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (willBan7Days) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BkashPink.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, BkashPink)
                        ) {
                            Text(
                                text = "🚫 ৩টির বেশি ওয়ার্নিং (${nextWarnings}টি) হওয়ায় এই প্লেয়ারকে স্বয়ংক্রিয়ভাবে ৭ দিনের জন্য অ্যাপ থেকে ব্যান করা হবে!",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                    OutlinedTextField(
                        value = denyReasonInput,
                        onValueChange = { denyReasonInput = it },
                        label = { Text("বাতিলের কারণ (ঐচ্ছিক)") },
                        placeholder = { Text("যেমন: শর্ত পূরণ হয়নি বা সঠিক প্রুফ নেই") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("deny_appeal_reason_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BkashPink,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val res = onDenyAppeal(
                            appeal.id,
                            denyReasonInput.ifBlank { "ব্যাজের নির্ধারিত শর্ত পূরণ হয়নি (সঠিক প্রুফ বা যোগ্যতা নেই)" }
                        )
                        actionBanner = res
                        denyTargetAppeal = null
                    },
                    modifier = Modifier.testTag("confirm_deny_badge_appeal_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = BkashPink)
                ) {
                    Text(if (willBan7Days) "বাতিল ও ৭ দিন ব্যান" else "বাতিল ও ওয়ার্ন দিন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { denyTargetAppeal = null }) {
                    Text("ফিরে যান", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun BadgeAppealChatDialog(
    appeal: BadgeAppeal,
    isStaffReviewer: Boolean,
    userWarnings: Int,
    isUserBanned: Boolean,
    onDismiss: () -> Unit,
    onSendMessage: (text: String, proofImageUri: String?) -> Pair<Boolean, String>,
    onTopAcceptClick: (() -> Unit)? = null,
    onTopDenyClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var messageInput by remember { mutableStateOf("") }
    var attachedProofUri by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                attachedProofUri = uri.toString()
            }
        }
    )

    LaunchedEffect(appeal.chatMessages.size) {
        if (appeal.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(appeal.chatMessages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(min = 480.dp, max = 740.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, FlameCyan, RoundedCornerShape(24.dp))
                .testTag("badge_appeal_chat_dialog"),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Header
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(FlameCyan.copy(alpha = 0.2f))
                                .border(1.dp, FlameCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Chat, contentDescription = null, tint = FlameCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isStaffReviewer) "💬 ${appeal.userIgn} — প্রুফ চ্যাট" else "💬 ওনার/অ্যাডমিন প্রুফ চ্যাট",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = FlameCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "ব্যাজ: ${appeal.requestedBadge.title} • UID: ${appeal.userUid}",
                                style = MaterialTheme.typography.labelSmall,
                                color = FlameYellow,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("appeal_chat_close_btn")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Chat", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sticky TOP Decision Bar for Owner & Admin ("uporer dike accept deny button e click korbe")
                if (isStaffReviewer) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkCard,
                        border = BorderStroke(
                            1.dp,
                            when (appeal.status) {
                                AppealStatus.PENDING -> FlameYellow.copy(alpha = 0.7f)
                                AppealStatus.APPROVED -> VictoryGreen
                                AppealStatus.REJECTED -> BkashPink
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("appeal_chat_top_actions")
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ম্যাচ: ${appeal.matchesPlayedSnapshot} • খরচ: ৳${appeal.totalSpentSnapshot} • ওয়ার্ন: $userWarnings/৩",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isUserBanned) BkashPink else FlameYellow
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (appeal.status) {
                                        AppealStatus.PENDING -> FlameYellow.copy(alpha = 0.2f)
                                        AppealStatus.APPROVED -> VictoryGreen.copy(alpha = 0.2f)
                                        AppealStatus.REJECTED -> BkashPink.copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = appeal.status.banglaLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
                                        color = when (appeal.status) {
                                            AppealStatus.PENDING -> FlameYellow
                                            AppealStatus.APPROVED -> VictoryGreen
                                            AppealStatus.REJECTED -> BkashPink
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (appeal.status == AppealStatus.PENDING && onTopAcceptClick != null && onTopDenyClick != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = onTopAcceptClick,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("appeal_chat_top_accept_btn"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                                    ) {
                                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Accept (ব্যাজ দিন)", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = onTopDenyClick,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("appeal_chat_top_deny_btn"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BkashPink),
                                        border = BorderStroke(1.dp, BkashPink)
                                    ) {
                                        Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Deny + ওয়ার্ন", fontWeight = FontWeight.Black, fontSize = 11.sp)
                                    }
                                }
                            } else if (appeal.adminNote.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = appeal.adminNote,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (appeal.status == AppealStatus.APPROVED) VictoryGreen else BkashPink
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = VictoryGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "অবস্থা: ${appeal.status.banglaLabel} • এখানে চ্যাট ও স্ক্রিনশট/প্রুফ দিন",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Live Appeal Proof Chat Messages
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0A0F1A))
                        .padding(10.dp)
                        .testTag("appeal_chat_messages_list"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (appeal.chatMessages.isEmpty()) {
                        item {
                            Text(
                                text = "এখানে প্লেয়ার ও অ্যাডমিন/ওনার সরাসরি কথা বলে ব্যাজের শর্ত ও প্রুফ যাচাই করতে পারবেন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                    } else {
                        items(appeal.chatMessages, key = { it.id }) { msg ->
                            val bubbleAlign = if (msg.isFromStaff) Alignment.End else Alignment.Start
                            val bubbleBg = if (msg.isFromStaff) Color(0xFF1E293B) else Color(0xFF062C30)
                            val bubbleBorder = if (msg.isFromStaff) FlameYellow.copy(alpha = 0.65f) else FlameCyan.copy(alpha = 0.65f)
                            val decodedBitmap = remember(msg.proofImageUri) {
                                msg.proofImageUri?.let { uriStr ->
                                    runCatching {
                                        context.contentResolver.openInputStream(Uri.parse(uriStr))?.use { stream ->
                                            BitmapFactory.decodeStream(stream)
                                        }
                                    }.getOrNull()
                                }
                            }

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = bubbleAlign
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = bubbleBg,
                                    border = BorderStroke(1.dp, bubbleBorder),
                                    modifier = Modifier.widthIn(max = 285.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "${msg.senderName} • ${msg.senderRoleLabel}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp),
                                            color = if (msg.isFromStaff) FlameYellow else FlameCyan
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                        if (decodedBitmap != null) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Image(
                                                bitmap = decodedBitmap.asImageBitmap(),
                                                contentDescription = "Proof Screenshot",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(140.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .border(1.dp, FlameCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            )
                                        } else if (!msg.proofImageUri.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = FlameCyan.copy(alpha = 0.16f)
                                            ) {
                                                Text(
                                                    text = "📎 প্রুফ স্ক্রিনশট সংযুক্ত (${msg.proofImageUri.takeLast(20)})",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = FlameCyan,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = msg.timeAgo,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Chips (Staff Ask Proof / Player Send Proof)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickSuggestions = if (isStaffReviewer) {
                        listOf(
                            "আপনার প্রোফাইল ও ম্যাচ হিস্টোরির স্ক্রিনশট প্রুফ দিন।",
                            "আপনার কিল ও বুইয়া প্রমাণ পাঠালে এখনই Accept করব।",
                            "আপনার তথ্য যাচাই সম্পন্ন হয়েছে।"
                        )
                    } else {
                        listOf(
                            "আমি সব শর্ত পূরণ করেছি, প্রুফ ও UID দিচ্ছি।",
                            "আমার ম্যাচ সংখ্যা ও খরচ প্রোফাইলে চেক করুন।",
                            "স্ক্রিনশট প্রুফ পাঠালাম, দয়া করে দেখুন।"
                        )
                    }
                    quickSuggestions.forEachIndexed { idx, prompt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkCard,
                            border = BorderStroke(1.dp, DarkBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    onSendMessage(prompt, attachedProofUri)
                                    attachedProofUri = null
                                }
                                .testTag("appeal_chat_quick_chip_$idx")
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelSmall,
                                color = FlameYellow,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                attachedProofUri?.let { uri ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VictoryGreen.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, VictoryGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "📎 প্রুফ ছবি সংযুক্ত: ${uri.takeLast(22)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = VictoryGreen,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "মুছুন",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = BkashPink,
                                modifier = Modifier.clickable { attachedProofUri = null }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Message Input Row with Photo Picker Proof Attachment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCard)
                            .border(1.dp, FlameCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .testTag("appeal_chat_attach_photo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddPhotoAlternate,
                            contentDescription = "Attach Proof Screenshot",
                            tint = FlameCyan
                        )
                    }

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = { Text(if (isStaffReviewer) "প্লেয়ারের কাছে প্রুফ চান..." else "প্রুফ বা মেসেজ লিখুন...") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("appeal_chat_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameCyan,
                            unfocusedBorderColor = DarkBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    IconButton(
                        onClick = {
                            val res = onSendMessage(messageInput, attachedProofUri)
                            if (res.first) {
                                messageInput = ""
                                attachedProofUri = null
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(FlameCyan)
                            .testTag("appeal_chat_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}
