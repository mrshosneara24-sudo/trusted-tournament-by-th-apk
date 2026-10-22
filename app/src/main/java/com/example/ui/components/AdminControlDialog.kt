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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GameCategory
import com.example.model.MatchType
import com.example.model.TournamentMatch
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun AdminControlDialog(
    matches: List<TournamentMatch>,
    currentWhatsAppUrl: String,
    onDismiss: () -> Unit,
    onPublishRoom: (matchId: String, roomId: String, password: String) -> Unit,
    onUpdateWhatsAppUrl: (url: String) -> Unit,
    onCreateNewMatch: (
        title: String,
        category: GameCategory,
        matchType: MatchType,
        mapName: String,
        matchDateTime: String,
        entryFee: Int,
        totalPrize: Int,
        perKill: Int,
        booyahPrize: Int,
        totalSlots: Int
    ) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Room Publish, 1: WhatsApp Group, 2: Create Match

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .testTag("admin_control_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.5f))
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(FlameOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = FlameYellow,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TH অ্যাডমিন কন্ট্রোল সেন্টার",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = FlameYellow
                            )
                            Text(
                                text = "১-ট্যাপে রুম আইডি, পাসওয়ার্ড ও WhatsApp লিঙ্ক কন্ট্রোল",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkCard,
                    contentColor = FlameYellow,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = FlameYellow,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = "🚀 রুম প্রকাশ",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 0) FlameYellow else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = "🟢 WhatsApp",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 1) VictoryGreen else Color.White
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = "➕ নতুন ম্যাচ",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == 2) FlameYellow else Color.White
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        // Section 1: 1-Tap Room Publish
                        PublishRoomSection(
                            matches = matches,
                            onPublish = { matchId, roomId, pass ->
                                onPublishRoom(matchId, roomId, pass)
                            }
                        )
                    }
                    1 -> {
                        // Section 2: 1-Tap WhatsApp Group Link
                        WhatsAppGroupSection(
                            currentUrl = currentWhatsAppUrl,
                            onUpdateUrl = onUpdateWhatsAppUrl
                        )
                    }
                    2 -> {
                        // Section 3: Create New Match
                        CreateMatchSection(
                            onCreateMatch = onCreateNewMatch
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PublishRoomSection(
    matches: List<TournamentMatch>,
    onPublish: (matchId: String, roomId: String, password: String) -> Unit
) {
    var selectedMatchId by remember { mutableStateOf(matches.firstOrNull()?.id ?: "") }
    var roomIdInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("TH77") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val currentMatch = matches.find { it.id == selectedMatchId }

    Column {
        Text(
            text = "১-ট্যাপে রুম আইডি ও পাসওয়ার্ড পাঠান:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = FlameYellow
        )
        Text(
            text = "এখানে রুম নম্বর ও পাসওয়ার্ড দিয়ে বাটনে চাপলে, যে প্লেয়াররা স্লট কিনেছে সবাই এক নিমিষে তাদের অ্যাপে কোড দেখতে পাবে।",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "ম্যাচ নির্বাচন করুন:",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Match Select Buttons
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            matches.take(4).forEach { match ->
                val isSelected = match.id == selectedMatchId
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else DarkCard,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) FlameYellow else DarkBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedMatchId = match.id
                            if (!match.roomId.isNullOrBlank()) {
                                roomIdInput = match.roomId
                            }
                            if (!match.roomPassword.isNullOrBlank()) {
                                passwordInput = match.roomPassword
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = match.title,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) FlameYellow else Color.White
                            )
                            Text(
                                text = "${match.matchDateTime} • স্লট: ${match.bookedSlots}/${match.totalSlots}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (match.roomId != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = VictoryGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "রুম ওপেন ✓",
                                    color = VictoryGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Room ID Input
        OutlinedTextField(
            value = roomIdInput,
            onValueChange = { roomIdInput = it },
            label = { Text("ফ্রি ফায়ার রুম আইডি (Room ID)") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Key, contentDescription = null, tint = FlameYellow)
            },
            placeholder = { Text("যেমন: 8849102") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_room_id_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlameYellow,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Room Password Input
        OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it },
            label = { Text("রুম পাসওয়ার্ড (Password)") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = FlameYellow)
            },
            placeholder = { Text("যেমন: TH77") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_room_pass_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlameYellow,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Publish Button
        Button(
            onClick = {
                if (roomIdInput.isBlank()) {
                    statusMessage = "অনুগ্রহ করে রুম আইডি লিখুন"
                    return@Button
                }
                if (passwordInput.isBlank()) {
                    statusMessage = "অনুগ্রহ করে রুম পাসওয়ার্ড লিখুন"
                    return@Button
                }
                onPublish(selectedMatchId, roomIdInput.trim(), passwordInput.trim())
                statusMessage = "রুম আইডি সফলভাবে সকল প্লেয়ারদের কাছে প্রকাশিত হয়েছে!"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("admin_publish_room_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Filled.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🚀 ১-ট্যাপে সব প্লেয়ারদের কাছে পাঠান",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        statusMessage?.let { msg ->
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = msg,
                color = VictoryGreen,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun WhatsAppGroupSection(
    currentUrl: String,
    onUpdateUrl: (url: String) -> Unit
) {
    var urlInput by remember { mutableStateOf(currentUrl) }
    var isSaved by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "অফিসিয়াল WhatsApp টুর্নামেন্ট গ্রুপ লিঙ্ক:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = VictoryGreen
        )
        Text(
            text = "এখানে আপনি আপনার WhatsApp গ্রুপের লিঙ্ক দিন। যে প্লেয়াররা স্লট কিনবে তারা অ্যাপে ১-ট্যাপে সরাসরি এই গ্রুপে জয়েন করতে পারবে।",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = urlInput,
            onValueChange = {
                urlInput = it
                isSaved = false
            },
            label = { Text("WhatsApp গ্রুপ ইনভাইট লিঙ্ক") },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Link, contentDescription = null, tint = VictoryGreen)
            },
            placeholder = { Text("https://chat.whatsapp.com/...") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_whatsapp_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VictoryGreen,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onUpdateUrl(urlInput.trim())
                isSaved = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("admin_save_whatsapp_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
        ) {
            Icon(imageVector = Icons.Filled.Chat, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "🟢 লিঙ্ক ১-ট্যাপে আপডেট ও শেয়ার করুন",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        if (isSaved) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "✓ WhatsApp গ্রুপ লিঙ্ক সফলভাবে আপডেট হয়েছে! প্লেয়াররা এখন এই লিঙ্কে জয়েন করতে পারবে।",
                color = VictoryGreen,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun CreateMatchSection(
    onCreateMatch: (
        title: String,
        category: GameCategory,
        matchType: MatchType,
        mapName: String,
        matchDateTime: String,
        entryFee: Int,
        totalPrize: Int,
        perKill: Int,
        booyahPrize: Int,
        totalSlots: Int
    ) -> Unit
) {
    var title by remember { mutableStateOf("বিআর র‍্যাঙ্ক - বারমুডা সোলো #০২") }
    var selectedCategory by remember { mutableStateOf(GameCategory.BR_RANK) }
    var matchType by remember { mutableStateOf(MatchType.SOLO) }
    var mapName by remember { mutableStateOf("বারমুডা (Bermuda)") }
    var matchDateTime by remember { mutableStateOf("আজ রাত ৯:৪৫ PM") }
    var entryFee by remember { mutableStateOf("10") }
    var totalPrize by remember { mutableStateOf("300") }
    var perKill by remember { mutableStateOf("3") }
    var booyahPrize by remember { mutableStateOf("80") }
    var totalSlots by remember { mutableStateOf("48") }

    Column {
        Text(
            text = "নতুন টুর্নামেন্ট ম্যাচ তৈরি করুন:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = FlameYellow
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Match Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("ম্যাচ টাইটেল") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FlameYellow,
                unfocusedBorderColor = DarkBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(GameCategory.BR_RANK, GameCategory.LONE_WOLF, GameCategory.CS_RANK).forEach { cat ->
                val isSelected = cat == selectedCategory
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) FlameOrange else DarkCard,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedCategory = cat
                            if (cat == GameCategory.LONE_WOLF) {
                                totalSlots = "2"
                                title = "লোন উলফ - ১ বনাম ১ কিং ডুয়েল #১০"
                                mapName = "আয়রন কেজ (Iron Cage)"
                                totalPrize = "25"
                                perKill = "0"
                                booyahPrize = "25"
                            } else if (cat == GameCategory.CS_RANK) {
                                totalSlots = "8"
                                title = "সিএস র‍্যাঙ্ক - ৪ বনাম ৪ ক্ল্যাশ স্কোয়াড #১৩"
                                mapName = "ক্লক টাওয়ার"
                                totalPrize = "70"
                                perKill = "0"
                                booyahPrize = "70"
                            } else {
                                totalSlots = "48"
                                title = "বিআর র‍্যাঙ্ক - বারমুডা সোলো #০২"
                                mapName = "বারমুডা (Bermuda)"
                                totalPrize = "300"
                                perKill = "3"
                                booyahPrize = "80"
                            }
                        }
                ) {
                    Text(
                        text = cat.banglaTitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Time and Entry Fee
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = matchDateTime,
                onValueChange = { matchDateTime = it },
                label = { Text("ম্যাচের সময়") },
                modifier = Modifier.weight(1.5f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = entryFee,
                onValueChange = { entryFee = it },
                label = { Text("এন্ট্রি ফি ৳") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Prize and Slots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = totalPrize,
                onValueChange = { totalPrize = it },
                label = { Text("মোট প্রাইজ ৳") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
            OutlinedTextField(
                value = totalSlots,
                onValueChange = { totalSlots = it },
                label = { Text("মোট স্লট") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onCreateMatch(
                    title,
                    selectedCategory,
                    matchType,
                    mapName,
                    matchDateTime,
                    entryFee.toIntOrNull() ?: 10,
                    totalPrize.toIntOrNull() ?: 200,
                    perKill.toIntOrNull() ?: 0,
                    booyahPrize.toIntOrNull() ?: 50,
                    totalSlots.toIntOrNull() ?: 48
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("admin_create_match_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ম্যাচ পাবলিশ করুন (Publish Match)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }
    }
}
