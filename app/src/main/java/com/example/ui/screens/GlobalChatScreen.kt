package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppUser
import com.example.model.BadgeType
import com.example.model.GlobalChatMessage
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.PlayerProfileCardData
import com.example.ui.components.ProfileAvatarImage
import com.example.ui.components.getBadgeDrawableRes
import com.example.ui.components.toPlayerProfileCard
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen

private enum class GlobalChatFilter(val label: String) {
    ALL("💬 সবাই"),
    STAFF("👑 ওনার ও অ্যাডমিন"),
    PINNED("📌 পিনড")
}

private val quickChatChips = listOf(
    "GG 🔥",
    "রুম কোড কখন?",
    "কেউ ডুও/স্কোয়াড খেলবে?",
    "হ্যালো সবাই 👋",
    "শুভকামনা 🏆"
)

private val quickReactionEmojis = listOf("🔥", "👍", "😂", "🏆")

@Composable
fun GlobalChatScreen(
    messages: List<GlobalChatMessage>,
    currentUser: UserProfile,
    registeredUsers: List<AppUser>,
    onSendMessage: (text: String, replyToId: String?, pinAnnouncement: Boolean) -> Pair<Boolean, String>,
    onTogglePin: (messageId: String) -> Pair<Boolean, String>,
    onDeleteMessage: (messageId: String) -> Pair<Boolean, String>,
    onReactMessage: (messageId: String, emoji: String) -> Boolean,
    onPlayerProfileClick: (PlayerProfileCardData) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(GlobalChatFilter.ALL) }
    var replyingTo by remember { mutableStateOf<GlobalChatMessage?>(null) }
    var pinAsAnnouncement by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val isStaff = currentUser.role == UserRole.OWNER || currentUser.role == UserRole.ADMIN
    val pinnedMessage = remember(messages) { messages.lastOrNull { it.isPinnedAnnouncement } }

    val visibleMessages = remember(messages, selectedFilter) {
        when (selectedFilter) {
            GlobalChatFilter.ALL -> messages
            GlobalChatFilter.STAFF -> messages.filter {
                it.senderRole == UserRole.OWNER || it.senderRole == UserRole.ADMIN
            }
            GlobalChatFilter.PINNED -> messages.filter { it.isPinnedAnnouncement }
        }
    }

    LaunchedEffect(visibleMessages.size) {
        if (visibleMessages.isNotEmpty()) {
            listState.animateScrollToItem(visibleMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .imePadding()
            .testTag("global_chat_screen")
    ) {
        // 1. Top Community Global Chat Header + Filter Chips
        Surface(
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1A0F24), DarkSurface, Color(0xFF0C1A24))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                                .background(
                                    Brush.linearGradient(listOf(BkashPink, FlameOrange))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Forum,
                                contentDescription = "Global Chat",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "গ্লোবাল কমিউনিটি চ্যাট",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = DisplayFontFamily,
                                    fontWeight = FontWeight.Black
                                ),
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "ওনার, অ্যাডমিন ও সকল মেম্বারের উন্মুক্ত চ্যাট",
                                fontSize = 11.sp,
                                color = FlameCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = VictoryGreen.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.55f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(VictoryGreen)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "${registeredUsers.size.coerceAtLeast(1)} মেম্বার",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = VictoryGreen
                            )
                        }
                    }
                }

                // Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlobalChatFilter.entries.forEach { filter ->
                        val selected = selectedFilter == filter
                        FilterChip(
                            selected = selected,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = filter.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BkashPink,
                                selectedLabelColor = Color.White,
                                containerColor = DarkCard,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = DarkBorder,
                                selectedBorderColor = FlameYellow
                            ),
                            modifier = Modifier.testTag("global_chat_filter_${filter.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // 2. Sticky Pinned Announcement Banner (if any)
        pinnedMessage?.let { pinned ->
            Surface(
                color = Color(0xFF281907),
                border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.65f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("global_chat_pinned_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.PushPin,
                        contentDescription = "Pinned",
                        tint = FlameYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📌 পিনড ঘোষণা • ${pinned.senderIgn} (${pinned.senderRole.banglaTitle})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = FlameYellow,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = pinned.text,
                            fontSize = 11.sp,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (isStaff) {
                        IconButton(
                            onClick = { onTogglePin(pinned.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Unpin",
                                tint = FlameYellow,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Messages Feed
        if (visibleMessages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Forum,
                        contentDescription = null,
                        tint = FlameCyan.copy(alpha = 0.7f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "এই ফিল্টারে কোনো মেসেজ নেই",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "প্রথম মেসেজ পাঠিয়ে সবার সাথে আড্ডা শুরু করুন!",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("global_chat_list"),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(visibleMessages, key = { it.id }) { msg ->
                    val liveUser = registeredUsers.firstOrNull {
                        (msg.senderPhone.isNotBlank() && it.phone == msg.senderPhone) ||
                            (msg.senderUid.isNotBlank() && msg.senderUid != "N/A" && it.uid == msg.senderUid) ||
                            it.id == msg.senderUserId
                    }
                    val resolvedRole = liveUser?.role ?: msg.senderRole
                    val resolvedIgn = liveUser?.ign?.ifBlank { msg.senderIgn } ?: msg.senderIgn
                    val resolvedAvatarId = liveUser?.avatarId ?: msg.senderAvatarId
                    val resolvedCustomPfp = liveUser?.customPfpUri ?: msg.senderCustomPfpUri
                    val resolvedBadges = liveUser?.effectiveBadges ?: msg.senderBadges
                    val isOwnMessage = (currentUser.phone.isNotBlank() && msg.senderPhone == currentUser.phone) ||
                        (currentUser.uid.isNotBlank() && msg.senderUid == currentUser.uid)

                    GlobalChatMessageCard(
                        message = msg,
                        resolvedRole = resolvedRole,
                        resolvedIgn = resolvedIgn,
                        resolvedAvatarId = resolvedAvatarId,
                        resolvedCustomPfpUri = resolvedCustomPfp,
                        resolvedBadges = resolvedBadges,
                        isOwnMessage = isOwnMessage,
                        canModerate = isStaff,
                        onSenderClick = {
                            onPlayerProfileClick(msg.toPlayerProfileCard(registeredUsers, currentUser))
                        },
                        onReplyClick = { replyingTo = msg },
                        onReactClick = { emoji -> onReactMessage(msg.id, emoji) },
                        onPinClick = { onTogglePin(msg.id) },
                        onDeleteClick = { onDeleteMessage(msg.id) }
                    )
                }
            }
        }

        // 4. Bottom Composer Bar
        HorizontalDivider(color = DarkBorder)
        Surface(
            color = DarkSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (currentUser.isBanned) {
                    Surface(
                        color = Color(0xFF3A0919),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "আপনার একাউন্ট ব্যান করা আছে, তাই গ্লোবাল চ্যাটে মেসেজ পাঠানো বন্ধ।",
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    // Reply preview strip
                    AnimatedVisibility(visible = replyingTo != null) {
                        replyingTo?.let { reply ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkCard,
                                border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.55f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Reply,
                                        contentDescription = null,
                                        tint = FlameCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "রিপ্লাই: ${reply.senderIgn}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FlameCyan
                                        )
                                        Text(
                                            text = reply.text,
                                            fontSize = 10.sp,
                                            color = TextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    IconButton(
                                        onClick = { replyingTo = null },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Cancel Reply",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Quick Phrases + Owner/Admin Pin Toggle
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isStaff) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (pinAsAnnouncement) FlameYellow else DarkCard,
                                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.7f)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { pinAsAnnouncement = !pinAsAnnouncement }
                                        .testTag("global_chat_pin_toggle")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PushPin,
                                            contentDescription = null,
                                            tint = if (pinAsAnnouncement) Color.Black else FlameYellow,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (pinAsAnnouncement) "📌 পিন সক্রিয়" else "📌 পিন ঘোষণা",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (pinAsAnnouncement) Color.Black else FlameYellow
                                        )
                                    }
                                }
                            }
                        }
                        items(quickChatChips) { chipText ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = DarkCard,
                                border = BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        val (ok, _) = onSendMessage(chipText, replyingTo?.id, pinAsAnnouncement)
                                        if (ok) {
                                            replyingTo = null
                                            pinAsAnnouncement = false
                                        }
                                    }
                            ) {
                                Text(
                                    text = chipText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Input Text Field + Send Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                val rolePrefix = when (currentUser.role) {
                                    UserRole.OWNER -> "👑 ওনার হিসেবে লিখুন..."
                                    UserRole.ADMIN -> "🛡️ অ্যাডমিন হিসেবে লিখুন..."
                                    UserRole.PLAYER -> "সবার সাথে চ্যাট করুন..."
                                }
                                Text(text = rolePrefix, fontSize = 12.sp, color = TextMuted)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("global_chat_input"),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = when (currentUser.role) {
                                    UserRole.OWNER -> FlameYellow
                                    UserRole.ADMIN -> FlameOrange
                                    UserRole.PLAYER -> BkashPink
                                },
                                unfocusedBorderColor = DarkBorder,
                                focusedContainerColor = DarkCard,
                                unfocusedContainerColor = DarkCard
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val msgToSend = inputText
                                    val (ok, _) = onSendMessage(msgToSend, replyingTo?.id, pinAsAnnouncement)
                                    if (ok) {
                                        inputText = ""
                                        replyingTo = null
                                        pinAsAnnouncement = false
                                    }
                                }
                            },
                            shape = CircleShape,
                            color = when (currentUser.role) {
                                UserRole.OWNER -> FlameYellow
                                UserRole.ADMIN -> FlameOrange
                                UserRole.PLAYER -> BkashPink
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("global_chat_send_btn")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Global Chat Message",
                                    tint = if (currentUser.role == UserRole.PLAYER) Color.White else Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GlobalChatMessageCard(
    message: GlobalChatMessage,
    resolvedRole: UserRole,
    resolvedIgn: String,
    resolvedAvatarId: String,
    resolvedCustomPfpUri: String?,
    resolvedBadges: List<BadgeType>,
    isOwnMessage: Boolean,
    canModerate: Boolean,
    onSenderClick: () -> Unit,
    onReplyClick: () -> Unit,
    onReactClick: (String) -> Unit,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val cardBorderColor = when {
        message.isPinnedAnnouncement -> FlameYellow
        resolvedRole == UserRole.OWNER -> FlameYellow.copy(alpha = 0.7f)
        resolvedRole == UserRole.ADMIN -> FlameOrange.copy(alpha = 0.65f)
        isOwnMessage -> NeonPink.copy(alpha = 0.6f)
        else -> DarkBorder
    }

    val cardBgColor = when {
        resolvedRole == UserRole.OWNER -> Color(0xFF1F1808)
        resolvedRole == UserRole.ADMIN -> Color(0xFF11192B)
        isOwnMessage -> Color(0xFF230F1D)
        else -> DarkCard
    }

    val displayBadges = remember(resolvedBadges, resolvedRole) {
        resolvedBadges.filterNot { badge ->
            (resolvedRole == UserRole.OWNER && badge == BadgeType.GREEN_VERIFIED) ||
                (resolvedRole == UserRole.ADMIN && badge == BadgeType.MODERATOR)
        }
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardBgColor,
        border = BorderStroke(1.dp, cardBorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("global_chat_msg_${message.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Sender Header Row (Direct Clickable PFP + IGN + Role Chip + Badges beside Name)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Direct Clickable Profile Picture (opens full Profile Card)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onSenderClick() }
                            .testTag("global_chat_pfp_${message.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        ProfileAvatarImage(
                            avatarId = resolvedAvatarId,
                            customPfpUri = resolvedCustomPfpUri,
                            role = resolvedRole,
                            contentDescription = "$resolvedIgn প্রোফাইল দেখুন",
                            showGifBadge = false,
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.8.dp,
                                    color = when (resolvedRole) {
                                        UserRole.OWNER -> FlameYellow
                                        UserRole.ADMIN -> FlameOrange
                                        UserRole.PLAYER -> FlameCyan
                                    },
                                    shape = CircleShape
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSenderClick() }
                            .padding(vertical = 2.dp)
                            .testTag("global_chat_sender_${message.id}"),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        // IGN + Role (Icon + Name) + Badges right beside the Name
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = resolvedIgn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = when (resolvedRole) {
                                    UserRole.OWNER -> FlameYellow
                                    UserRole.ADMIN -> FlameOrange
                                    UserRole.PLAYER -> Color.White
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )

                            // Role Chip with Official Icon + Role Name
                            GlobalChatRoleTag(
                                role = resolvedRole,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .testTag("global_chat_role_${message.id}")
                            )

                            // All earned badges: V_BADGE & PRO_PLAYER icon-only; all other badges show icon + badge name
                            displayBadges.forEach { badge ->
                                val isIconOnlyBadge = badge == BadgeType.V_BADGE || badge == BadgeType.PRO_PLAYER
                                val drawableRes = getBadgeDrawableRes(badge)
                                val badgeAccent = Color(badge.hexColor)
                                if (isIconOnlyBadge) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = badgeAccent.copy(alpha = 0.16f),
                                        border = BorderStroke(1.dp, badgeAccent.copy(alpha = 0.7f)),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .testTag("global_chat_badge_icon_${message.id}_${badge.name}")
                                    ) {
                                        Box(
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (drawableRes != null) {
                                                Image(
                                                    painter = painterResource(id = drawableRes),
                                                    contentDescription = badge.title,
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = badge.iconEmoji,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = badgeAccent.copy(alpha = 0.16f),
                                        border = BorderStroke(1.dp, badgeAccent.copy(alpha = 0.7f)),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .testTag("global_chat_badge_${message.id}_${badge.name}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (drawableRes != null) {
                                                Image(
                                                    painter = painterResource(id = drawableRes),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            } else {
                                                Text(
                                                    text = badge.iconEmoji,
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Text(
                                                text = badge.title,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = message.timeLabel,
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                            if (message.isPinnedAnnouncement) {
                                Text(
                                    text = "• 📌 পিনড",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FlameYellow
                                )
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    IconButton(
                        onClick = onReplyClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("global_chat_reply_${message.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Reply,
                            contentDescription = "Reply",
                            tint = FlameCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (canModerate) {
                        IconButton(
                            onClick = onPinClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("global_chat_pin_${message.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PushPin,
                                contentDescription = "Pin Message",
                                tint = if (message.isPinnedAnnouncement) FlameYellow else TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                    if (canModerate || isOwnMessage) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("global_chat_delete_${message.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Delete Message",
                                tint = Color(0xFFEF4444).copy(alpha = 0.85f),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // Reply quote block
            if (!message.replyToIgn.isNullOrBlank() && !message.replyToText.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkElevated,
                    border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                        Text(
                            text = "↩️ ${message.replyToIgn}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = FlameCyan
                        )
                        Text(
                            text = message.replyToText,
                            fontSize = 10.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Message Text
            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                ),
                color = Color.White
            )

            // Reactions Row (Existing counts + quick tap reactions)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                quickReactionEmojis.forEach { emoji ->
                    val count = message.reactions[emoji] ?: 0
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (count > 0) BkashPink.copy(alpha = 0.16f) else DarkElevated,
                        border = BorderStroke(
                            1.dp,
                            if (count > 0) BkashPink.copy(alpha = 0.5f) else DarkBorder
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onReactClick(emoji) }
                            .testTag("global_chat_react_${message.id}_$emoji")
                    ) {
                        Text(
                            text = if (count > 0) "$emoji $count" else emoji,
                            fontSize = 10.sp,
                            fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Normal,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
                message.reactions.entries
                    .filter { it.key !in quickReactionEmojis && it.value > 0 }
                    .forEach { (emoji, count) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = FlameYellow.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onReactClick(emoji) }
                        ) {
                            Text(
                                text = "$emoji $count",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameYellow,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
            }
        }
    }
}

@Composable
private fun GlobalChatRoleTag(
    role: UserRole,
    modifier: Modifier = Modifier
) {
    val bgColor = when (role) {
        UserRole.OWNER -> Color(0xFF062C1A)
        UserRole.ADMIN -> Color(0xFF142442)
        UserRole.PLAYER -> Color(0xFF161E2E)
    }
    val borderColor = when (role) {
        UserRole.OWNER -> VictoryGreen
        UserRole.ADMIN -> FlameOrange
        UserRole.PLAYER -> FlameCyan.copy(alpha = 0.55f)
    }
    val textColor = when (role) {
        UserRole.OWNER -> FlameYellow
        UserRole.ADMIN -> FlameOrange
        UserRole.PLAYER -> FlameCyan
    }
    val roleLabel = when (role) {
        UserRole.OWNER -> "ওনার"
        UserRole.ADMIN -> "অ্যাডমিন"
        UserRole.PLAYER -> "মেম্বার"
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (role) {
                UserRole.OWNER -> {
                    Image(
                        painter = painterResource(id = R.drawable.badge_owner_logo),
                        contentDescription = "ওনার আইকন",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(14.dp)
                    )
                }
                UserRole.ADMIN -> {
                    Image(
                        painter = painterResource(id = R.drawable.badge_admin_logo),
                        contentDescription = "অ্যাডমিন আইকন",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(14.dp)
                    )
                }
                UserRole.PLAYER -> {
                    Text(text = "🎮", fontSize = 10.sp)
                }
            }
            Text(
                text = roleLabel,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = textColor,
                maxLines = 1
            )
        }
    }
}
