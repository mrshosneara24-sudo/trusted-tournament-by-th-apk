package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.TournamentAiAssistant
import com.example.model.ChatMessage
import com.example.ui.SupportTabType
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SupportChatDialog(
    currentTab: SupportTabType,
    onTabSelected: (SupportTabType) -> Unit,
    adminMessages: List<ChatMessage>,
    aiMessages: List<ChatMessage>,
    officialBkashNumber: String,
    onDismiss: () -> Unit,
    onSendAdminMessage: (String) -> Unit,
    onSendAiMessage: (String) -> Unit,
    onResetAiChat: () -> Unit,
    onAiActionClick: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    var isAiTyping by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val aiListState = rememberLazyListState()
    val adminListState = rememberLazyListState()

    // Auto scroll when messages change
    LaunchedEffect(aiMessages.size, isAiTyping) {
        if (aiMessages.isNotEmpty()) {
            aiListState.animateScrollToItem(aiMessages.size - 1)
        }
    }

    LaunchedEffect(adminMessages.size) {
        if (adminMessages.isNotEmpty()) {
            adminListState.animateScrollToItem(adminMessages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
                .testTag("support_chat_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    if (currentTab == SupportTabType.AI_ASSISTANT) {
                                        Brush.linearGradient(listOf(FlameCyan, Color(0xFF3B82F6)))
                                    } else {
                                        Brush.linearGradient(listOf(BkashPink, Color(0xFFD81B60)))
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentTab == SupportTabType.AI_ASSISTANT) {
                                    Icons.Filled.SmartToy
                                } else {
                                    Icons.Filled.HeadsetMic
                                },
                                contentDescription = "Support Icon",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentTab == SupportTabType.AI_ASSISTANT) {
                                        "Talk with AI (এআই সহকারী)"
                                    } else {
                                        "লাইভ এডমিন সাপোর্ট"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (currentTab == SupportTabType.AI_ASSISTANT) FlameCyan.copy(alpha = 0.2f) else FlameYellow.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (currentTab == SupportTabType.AI_ASSISTANT) "24/7 AI" else "Live",
                                        color = if (currentTab == SupportTabType.AI_ASSISTANT) FlameCyan else FlameYellow,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (currentTab == SupportTabType.AI_ASSISTANT) {
                                    "যেকোনো সমস্যায় তাৎক্ষণিক সমাধান ও গাইড"
                                } else {
                                    "বিকাশ ও হেল্পলাইন: $officialBkashNumber"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (currentTab == SupportTabType.AI_ASSISTANT) Color(0xFF93C5FD) else FlameYellow
                            )
                        }
                    }

                    Row {
                        if (currentTab == SupportTabType.AI_ASSISTANT) {
                            IconButton(
                                onClick = onResetAiChat,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Reset Chat",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
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

                // Switcher Tabs
                TabRow(
                    selectedTabIndex = if (currentTab == SupportTabType.AI_ASSISTANT) 0 else 1,
                    containerColor = DarkCard,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[if (currentTab == SupportTabType.AI_ASSISTANT) 0 else 1]),
                            color = if (currentTab == SupportTabType.AI_ASSISTANT) FlameCyan else BkashPink,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = currentTab == SupportTabType.AI_ASSISTANT,
                        onClick = { onTabSelected(SupportTabType.AI_ASSISTANT) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (currentTab == SupportTabType.AI_ASSISTANT) FlameCyan else Color.Gray
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "🤖 Talk with AI",
                                    fontWeight = if (currentTab == SupportTabType.AI_ASSISTANT) FontWeight.Bold else FontWeight.Normal,
                                    color = if (currentTab == SupportTabType.AI_ASSISTANT) Color.White else Color.Gray
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_talk_with_ai")
                    )

                    Tab(
                        selected = currentTab == SupportTabType.LIVE_ADMIN,
                        onClick = { onTabSelected(SupportTabType.LIVE_ADMIN) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.HeadsetMic,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (currentTab == SupportTabType.LIVE_ADMIN) BkashPink else Color.Gray
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "👥 লাইভ এডমিন",
                                    fontWeight = if (currentTab == SupportTabType.LIVE_ADMIN) FontWeight.Bold else FontWeight.Normal,
                                    color = if (currentTab == SupportTabType.LIVE_ADMIN) Color.White else Color.Gray
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_live_admin")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // If in AI mode: Quick suggestions horizontal chips
                if (currentTab == SupportTabType.AI_ASSISTANT) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(TournamentAiAssistant.quickQuestions) { q ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF1E293B),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FlameCyan.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .clickable {
                                        coroutineScope.launch {
                                            isAiTyping = true
                                            delay(400)
                                            onSendAiMessage(q)
                                            isAiTyping = false
                                        }
                                    }
                            ) {
                                Text(
                                    text = q,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Chat Messages Feed
                if (currentTab == SupportTabType.AI_ASSISTANT) {
                    LazyColumn(
                        state = aiListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(aiMessages, key = { it.id }) { msg ->
                            AiMessageBubble(
                                message = msg,
                                onActionClick = onAiActionClick
                            )
                        }

                        if (isAiTyping) {
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = FlameCyan
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "এআই সহকারী সমাধান খুঁজছে...",
                                        fontSize = 11.sp,
                                        color = FlameCyan
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Admin Live Messages Feed
                    LazyColumn(
                        state = adminListState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(adminMessages, key = { it.id }) { msg ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (msg.isFromAdmin) Alignment.Start else Alignment.End
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (msg.isFromAdmin) 2.dp else 14.dp,
                                        bottomEnd = if (msg.isFromAdmin) 14.dp else 2.dp
                                    ),
                                    color = if (msg.isFromAdmin) MaterialTheme.colorScheme.surfaceVariant else BkashPink,
                                    modifier = Modifier.fillMaxWidth(0.85f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = msg.senderName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (msg.isFromAdmin) FlameYellow else Color.White
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.text,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.timeAgo,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = if (currentTab == SupportTabType.AI_ASSISTANT) {
                                    "যেকোনো সমস্যা বা প্রশ্ন লিখুন..."
                                } else {
                                    "এডমিনকে মেসেজ লিখুন..."
                                },
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = if (currentTab == SupportTabType.AI_ASSISTANT) FlameCyan else MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = DarkBorder
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val text = inputText.trim()
                                inputText = ""
                                if (currentTab == SupportTabType.AI_ASSISTANT) {
                                    coroutineScope.launch {
                                        isAiTyping = true
                                        delay(400)
                                        onSendAiMessage(text)
                                        isAiTyping = false
                                    }
                                } else {
                                    onSendAdminMessage(text)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (currentTab == SupportTabType.AI_ASSISTANT) FlameCyan else BkashPink)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = if (currentTab == SupportTabType.AI_ASSISTANT) Color.Black else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AiMessageBubble(
    message: ChatMessage,
    onActionClick: (String) -> Unit
) {
    val isAi = message.isAiAssistant

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isAi) Alignment.Start else Alignment.End
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isAi) 2.dp else 16.dp,
                bottomEnd = if (isAi) 16.dp else 2.dp
            ),
            color = if (isAi) Color(0xFF131E2E) else BkashPink,
            border = if (isAi) androidx.compose.foundation.BorderStroke(1.dp, FlameCyan.copy(alpha = 0.3f)) else null,
            modifier = Modifier.fillMaxWidth(if (isAi) 0.92f else 0.85f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isAi) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(FlameCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = message.senderName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isAi) FlameCyan else Color.White
                            )
                        )
                    }
                    Text(
                        text = message.timeAgo,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp),
                    color = Color.White
                )

                // Optional Action Button inside AI message
                if (isAi && message.actionType != null && message.actionLabel != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onActionClick(message.actionType) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FlameCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("ai_action_btn_${message.actionType}")
                    ) {
                        Text(
                            text = message.actionLabel,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
