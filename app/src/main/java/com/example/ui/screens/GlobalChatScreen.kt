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
import androidx.compose.material.icons.filled.Warning
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
import com.example.model.ChatChannel
import com.example.model.GlobalChatMessage
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.PlayerProfileCardData
import com.example.ui.components.ProfileAvatarImage
import com.example.ui.components.getBadgeDrawableRes
import com.example.ui.components.toPlayerProfileCard
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.MonoFontFamily

private val DiscordCanvas = Color(0xFF16181D)
private val DiscordHeaderBg = Color(0xFF202228)
private val DiscordChannelBarBg = Color(0xFF272A32)
private val DiscordDivider = Color(0xFF2F323B)
private val DiscordComposerBg = Color(0xFF2B2E36)
private val DiscordMutedText = Color(0xFF9BA1AC)

private val OwnerRed = Color(0xFFEF4444)
private val OwnerBlack = Color(0xFF050507)
private val AdminRed = Color(0xFFDC2626)

private val quickChatPhrases = listOf(
    "GG",
    "রুম কোড কখন দিবে?",
    "কেউ ডুও বা স্কোয়াড খেলবে?",
    "হ্যালো সবাই",
    "বেস্ট অব লাক"
)

private val reactionOptions = listOf("GG", "WIN", "+1")

@Composable
fun GlobalChatScreen(
    messages: List<GlobalChatMessage>,
    currentUser: UserProfile,
    registeredUsers: List<AppUser>,
    currentActorKeys: Set<String> = emptySet(),
    onSendMessage: (text: String, replyToId: String?, channelId: String) -> Pair<Boolean, String>,
    onDeleteMessage: (messageId: String) -> Pair<Boolean, String>,
    onReactMessage: (messageId: String, reactKey: String) -> Boolean = { _, _ -> false },
    onPlayerProfileClick: (PlayerProfileCardData) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var selectedChannel by remember { mutableStateOf(ChatChannel.GLOBAL) }
    var replyingTo by remember { mutableStateOf<GlobalChatMessage?>(null) }
    val listState = rememberLazyListState()

    val isStaff = currentUser.role == UserRole.OWNER || currentUser.role == UserRole.ADMIN

    val channelMessages = remember(messages, selectedChannel) {
        messages.filter { msg ->
            val msgChannel = ChatChannel.entries.firstOrNull {
                it.id.equals(msg.channelId, ignoreCase = true)
            } ?: ChatChannel.GLOBAL
            msgChannel == selectedChannel
        }
    }

    LaunchedEffect(channelMessages.size, selectedChannel) {
        if (channelMessages.isNotEmpty()) {
            listState.animateScrollToItem(channelMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DiscordCanvas)
            .imePadding()
            .testTag("global_chat_screen")
    ) {
        // 1. Upgraded Discord-Style Server Header + Category Channel Switcher
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DiscordHeaderBg)
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DiscordChannelBarBg,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
                    ) {
                        Text(
                            text = "#",
                            fontFamily = MonoFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedChannel.channelName.removePrefix("# ").trim(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = selectedChannel.description,
                            fontSize = 11.sp,
                            color = DiscordMutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DiscordChannelBarBg,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Text(
                            text = "${registeredUsers.size.coerceAtLeast(1)} অনলাইন",
                            fontSize = 10.sp,
                            fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Global + Category Channel Tabs (Everyone can chat; zero emojis; zero pinned tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                ChatChannel.entries.forEach { channel ->
                    val isSelected = selectedChannel == channel
                    val msgCount = messages.count { msg ->
                        (ChatChannel.entries.firstOrNull { it.id.equals(msg.channelId, ignoreCase = true) } ?: ChatChannel.GLOBAL) == channel
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF343844) else DiscordChannelBarBg.copy(alpha = 0.65f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color.White.copy(alpha = 0.6f) else DiscordDivider
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                selectedChannel = channel
                                replyingTo = null
                            }
                            .testTag("chat_channel_${channel.id.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = channel.channelName,
                                fontSize = 11.sp,
                                fontFamily = MonoFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else DiscordMutedText
                            )
                            if (msgCount > 0) {
                                Text(
                                    text = "$msgCount",
                                    fontSize = 9.sp,
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else DiscordMutedText
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = DiscordDivider, thickness = 1.dp)

        // 2. Plain Text Discord Messages Feed (no card embeds, no emojis, no pinned messages)
        if (channelMessages.isEmpty()) {
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
                        tint = DiscordMutedText,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${selectedChannel.channelName} চ্যানেলে স্বাগতম",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "এই চ্যানেলে প্রথম মেসেজ পাঠিয়ে আলোচনা শুরু করুন।",
                        fontSize = 12.sp,
                        color = DiscordMutedText
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
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(channelMessages, key = { it.id }) { msg ->
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
                    val userSelectedReact = currentActorKeys.firstNotNullOfOrNull { key ->
                        msg.reactedByUsers[key]
                    }

                    GlobalChatDiscordMessageRow(
                        message = msg,
                        resolvedRole = resolvedRole,
                        resolvedIgn = resolvedIgn,
                        resolvedAvatarId = resolvedAvatarId,
                        resolvedCustomPfpUri = resolvedCustomPfp,
                        resolvedBadges = resolvedBadges,
                        isOwnMessage = isOwnMessage,
                        canModerate = isStaff,
                        userSelectedReact = userSelectedReact,
                        onSenderClick = {
                            onPlayerProfileClick(msg.toPlayerProfileCard(registeredUsers, currentUser))
                        },
                        onReplyClick = { replyingTo = msg },
                        onDeleteClick = { onDeleteMessage(msg.id) },
                        onReactClick = { reactKey -> onReactMessage(msg.id, reactKey) }
                    )
                }
            }
        }

        // 3. Upgraded Discord-Style Composer Bar
        HorizontalDivider(color = DiscordDivider, thickness = 1.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DiscordHeaderBg)
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            if (currentUser.isBanned) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = OwnerRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "আপনার একাউন্ট ব্যান করা আছে, তাই চ্যাটে মেসেজ পাঠানো বন্ধ।",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else if (currentUser.isChatMuted && currentUser.role != UserRole.OWNER) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .testTag("global_chat_muted_banner"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Chat Muted",
                        tint = OwnerRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "আপনার গ্লোবাল চ্যাট সাময়িকভাবে মিউট করা হয়েছে! (${currentUser.chatMuteReason ?: "চ্যাট নীতিমালা ভঙ্গ"})",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                // Plain Reply Preview Strip
                AnimatedVisibility(visible = replyingTo != null) {
                    replyingTo?.let { reply ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DiscordChannelBarBg, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Reply,
                                contentDescription = null,
                                tint = DiscordMutedText,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Reply @${reply.senderIgn}: ${reply.text}",
                                fontSize = 10.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
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

                // Quick Text Phrases (plain text, zero emojis, zero pin toggles)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(quickChatPhrases) { phrase ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DiscordChannelBarBg,
                            border = BorderStroke(1.dp, DiscordDivider),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    val (ok, _) = onSendMessage(
                                        phrase,
                                        replyingTo?.id,
                                        selectedChannel.id
                                    )
                                    if (ok) {
                                        replyingTo = null
                                    }
                                }
                        ) {
                            Text(
                                text = phrase,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                // Input Field + Send Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Message ${selectedChannel.channelName}",
                                fontSize = 12.sp,
                                color = DiscordMutedText
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("global_chat_input"),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedBorderColor = DiscordDivider,
                            focusedContainerColor = DiscordComposerBg,
                            unfocusedContainerColor = DiscordComposerBg
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val msgToSend = inputText
                                val (ok, _) = onSendMessage(
                                    msgToSend,
                                    replyingTo?.id,
                                    selectedChannel.id
                                )
                                if (ok) {
                                    inputText = ""
                                    replyingTo = null
                                }
                            }
                        },
                        shape = CircleShape,
                        color = when (currentUser.role) {
                            UserRole.OWNER -> OwnerRed
                            UserRole.ADMIN -> AdminRed
                            UserRole.PLAYER -> Color.White
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("global_chat_send_btn")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Global Chat Message",
                                tint = when (currentUser.role) {
                                    UserRole.OWNER -> OwnerBlack
                                    UserRole.ADMIN -> Color.White
                                    UserRole.PLAYER -> OwnerBlack
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GlobalChatDiscordMessageRow(
    message: GlobalChatMessage,
    resolvedRole: UserRole,
    resolvedIgn: String,
    resolvedAvatarId: String,
    resolvedCustomPfpUri: String?,
    resolvedBadges: List<BadgeType>,
    isOwnMessage: Boolean,
    canModerate: Boolean,
    userSelectedReact: String?,
    onSenderClick: () -> Unit,
    onReplyClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReactClick: (String) -> Unit
) {
    val displayBadges = remember(resolvedBadges, resolvedRole) {
        resolvedBadges.filterNot { badge ->
            (resolvedRole == UserRole.OWNER && badge == BadgeType.GREEN_VERIFIED) ||
                (resolvedRole == UserRole.ADMIN && badge == BadgeType.MODERATOR)
        }
    }

    // Strict role-based colors:
    // Owner = Red and Black | Admin = Red and White | Player = White only
    val nameColor = when (resolvedRole) {
        UserRole.OWNER -> OwnerRed
        UserRole.ADMIN -> Color.White
        UserRole.PLAYER -> Color.White
    }
    val messageColor = when (resolvedRole) {
        UserRole.OWNER -> Color(0xFFFCA5A5)
        UserRole.ADMIN -> Color.White
        UserRole.PLAYER -> Color.White
    }
    val avatarBorderColor = when (resolvedRole) {
        UserRole.OWNER -> OwnerRed
        UserRole.ADMIN -> AdminRed
        UserRole.PLAYER -> Color.White.copy(alpha = 0.65f)
    }
    val rowAccentStripe = when (resolvedRole) {
        UserRole.OWNER -> OwnerRed
        UserRole.ADMIN -> AdminRed
        UserRole.PLAYER -> Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                when (resolvedRole) {
                    UserRole.OWNER -> OwnerBlack.copy(alpha = 0.6f)
                    UserRole.ADMIN -> Color(0xFF1F1619)
                    UserRole.PLAYER -> Color.Transparent
                }
            )
            .testTag("global_chat_msg_${message.id}"),
        verticalAlignment = Alignment.Top
    ) {
        if (resolvedRole != UserRole.PLAYER) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(64.dp)
                    .background(rowAccentStripe)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Optional plain-text Discord reply line (no embed box, no emoji)
            if (!message.replyToIgn.isNullOrBlank() && !message.replyToText.isNullOrBlank()) {
                Text(
                    text = "Reply @${message.replyToIgn}: ${message.replyToText}",
                    fontSize = 10.sp,
                    color = DiscordMutedText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 46.dp, bottom = 3.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Clickable Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, avatarBorderColor, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Header: Sender Name + Role Pill + Colored Badges beside name + Timestamp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        FlowRow(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onSenderClick() }
                                .testTag("global_chat_sender_${message.id}"),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = resolvedIgn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = nameColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )

                            GlobalChatRoleTag(
                                role = resolvedRole,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .testTag("global_chat_role_${message.id}")
                            )

                            // Discord-style Badges beside Name (zero emojis; uses badge hexColor)
                            displayBadges.forEach { badge ->
                                val isIconOnlyBadge = badge == BadgeType.V_BADGE || badge == BadgeType.PRO_PLAYER
                                val drawableRes = getBadgeDrawableRes(badge)
                                val badgeAccent = Color(badge.hexColor)
                                if (isIconOnlyBadge) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = badgeAccent.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, badgeAccent),
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
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(7.dp)
                                                        .clip(CircleShape)
                                                        .background(badgeAccent)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = badgeAccent.copy(alpha = 0.22f),
                                        border = BorderStroke(1.dp, badgeAccent),
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .testTag("global_chat_badge_${message.id}_${badge.name}")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (drawableRes != null) {
                                                Image(
                                                    painter = painterResource(id = drawableRes),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.Fit,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .size(6.dp)
                                                        .clip(CircleShape)
                                                        .background(badgeAccent)
                                                )
                                            }
                                            Text(
                                                text = badge.title,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = badgeAccent,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = message.timeLabel,
                                fontSize = 9.sp,
                                color = DiscordMutedText,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }

                        // Compact Actions (Reply, Delete — NO Pin button)
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            IconButton(
                                onClick = onReplyClick,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("global_chat_reply_${message.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Reply,
                                    contentDescription = "Reply",
                                    tint = DiscordMutedText,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            if (canModerate || isOwnMessage) {
                                IconButton(
                                    onClick = onDeleteClick,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("global_chat_delete_${message.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = "Delete Message",
                                        tint = OwnerRed,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Plain Discord message body text (Owner = Red/Black, Admin = Red/White, Player = White only)
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        ),
                        color = messageColor
                    )

                    // Single-use toggleable plain-text reactions (no emojis, 1 react per user per message)
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        reactionOptions.forEach { reactKey ->
                            val count = if (message.reactedByUsers.isNotEmpty()) {
                                message.reactedByUsers.values.count { it == reactKey }
                            } else {
                                message.reactions[reactKey] ?: 0
                            }
                            val isSelectedByMe = userSelectedReact == reactKey
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelectedByMe) Color.White else DiscordChannelBarBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelectedByMe) Color.White else DiscordDivider
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onReactClick(reactKey) }
                                    .testTag("global_chat_react_${message.id}_$reactKey")
                            ) {
                                Text(
                                    text = if (count > 0) "$reactKey $count" else reactKey,
                                    fontSize = 10.sp,
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelectedByMe) OwnerBlack else DiscordMutedText,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    HorizontalDivider(color = DiscordDivider, thickness = 0.5.dp)
}

@Composable
private fun GlobalChatRoleTag(
    role: UserRole,
    modifier: Modifier = Modifier
) {
    // Owner: Red + Black | Admin: Red + White | Player: White only
    val bgColor = when (role) {
        UserRole.OWNER -> OwnerBlack
        UserRole.ADMIN -> AdminRed
        UserRole.PLAYER -> Color.Transparent
    }
    val borderColor = when (role) {
        UserRole.OWNER -> OwnerRed
        UserRole.ADMIN -> Color.White
        UserRole.PLAYER -> Color.White.copy(alpha = 0.45f)
    }
    val textColor = when (role) {
        UserRole.OWNER -> OwnerRed
        UserRole.ADMIN -> Color.White
        UserRole.PLAYER -> Color.White
    }
    val roleLabel = when (role) {
        UserRole.OWNER -> "ওনার"
        UserRole.ADMIN -> "অ্যাডমিন"
        UserRole.PLAYER -> "মেম্বার"
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            when (role) {
                UserRole.OWNER -> {
                    Image(
                        painter = painterResource(id = R.drawable.badge_owner_logo),
                        contentDescription = "ওনার আইকন",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(12.dp)
                    )
                }
                UserRole.ADMIN -> {
                    Image(
                        painter = painterResource(id = R.drawable.badge_admin_logo),
                        contentDescription = "অ্যাডমিন আইকন",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(12.dp)
                    )
                }
                UserRole.PLAYER -> Unit
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
