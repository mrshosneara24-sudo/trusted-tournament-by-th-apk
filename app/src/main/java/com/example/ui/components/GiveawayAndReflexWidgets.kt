package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.GiveawayEvent
import com.example.model.GiveawayStatus
import com.example.model.ReflexGameStats
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun LiveMegaGiveawayCard(
    giveaway: GiveawayEvent,
    currentActorKeys: Set<String>,
    onJoinGiveaway: (String) -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    val hasJoined = remember(giveaway.entries, currentActorKeys) {
        giveaway.entries.any { it.actorKey in currentActorKeys || it.uid in currentActorKeys }
    }
    var feedback by remember(giveaway.id) { mutableStateOf<Pair<Boolean, String>?>(null) }
    val progress = if (giveaway.maxEntries > 0) {
        (giveaway.entries.size.toFloat() / giveaway.maxEntries.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("mega_giveaway_card_${giveaway.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(
            1.4.dp,
            Brush.horizontalGradient(
                listOf(FlameYellow, NeonPink, FlameCyan)
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF28121E), DarkCard, Color(0xFF0A1D2B))
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Header & Prize Pill
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
                            .background(Brush.linearGradient(listOf(FlameOrange, NeonPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Celebration,
                            contentDescription = "Mega Giveaway",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (giveaway.status) {
                                    GiveawayStatus.LIVE -> FireRed
                                    GiveawayStatus.DRAWN -> VictoryGreen.copy(alpha = 0.22f)
                                    GiveawayStatus.CLOSED -> DarkElevated
                                }
                            ) {
                                Text(
                                    text = when (giveaway.status) {
                                        GiveawayStatus.LIVE -> "🔴 LIVE GIVEAWAY"
                                        GiveawayStatus.DRAWN -> "🏆 WINNER DRAWN"
                                        GiveawayStatus.CLOSED -> "বন্ধ"
                                    },
                                    fontFamily = MonoFontFamily,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (giveaway.status == GiveawayStatus.DRAWN) VictoryGreen else Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${giveaway.winnersCount} জন বিজয়ী",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = giveaway.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = FlameYellow,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FlameYellow.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, FlameYellow)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = giveaway.prizeType.emoji, fontSize = 14.sp)
                        Text(
                            text = giveaway.prizeLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = FlameYellow,
                            maxLines = 1
                        )
                    }
                }
            }

            if (giveaway.description.isNotBlank()) {
                Text(
                    text = giveaway.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            // Live Ticket Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🎟️ অংশগ্রহণ: ${giveaway.entries.size}/${giveaway.maxEntries} জন",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "বাকি টিকিট: ${giveaway.spotsRemaining} টি",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (giveaway.isFull) FireRed else VictoryGreen
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (giveaway.status == GiveawayStatus.DRAWN) VictoryGreen else FlameYellow,
                    trackColor = DarkSurface
                )
            }

            // Recent Joined Player Chips
            if (giveaway.entries.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    giveaway.entries.takeLast(12).reversed().forEach { entry ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkBorder)
                        ) {
                            Text(
                                text = "🎟️ ${entry.ign}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FlameCyan,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Winners Showcase (when Drawn)
            if (giveaway.status == GiveawayStatus.DRAWN && giveaway.winners.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = VictoryGreen.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, VictoryGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("giveaway_winners_box_${giveaway.id}")
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = "Giveaway Winners",
                                tint = FlameYellow,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🏆 মেগা গিভওয়ে বিজয়ী তালিকা",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = FlameYellow
                            )
                        }
                        giveaway.winners.forEachIndexed { idx, w ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${idx + 1}. 👑 ${w.ign} (UID: ${w.uid})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = w.prizeLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = VictoryGreen
                                )
                            }
                        }
                    }
                }
            } else {
                // Join Free Ticket CTA
                Button(
                    onClick = { feedback = onJoinGiveaway(giveaway.id) },
                    enabled = giveaway.status == GiveawayStatus.LIVE && !hasJoined && !giveaway.isFull,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("join_giveaway_btn_${giveaway.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasJoined || giveaway.isFull) DarkElevated else VictoryGreen
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ConfirmationNumber,
                        contentDescription = "Join Giveaway",
                        tint = if (hasJoined || giveaway.isFull) TextSecondary else Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            hasJoined -> "ফ্রি টিকিট কনফার্মড ✓ (ড্র-এর অপেক্ষায়)"
                            giveaway.isFull -> "টিকিট পূর্ণ হয়ে গেছে"
                            giveaway.status != GiveawayStatus.LIVE -> "গিভওয়ে সমাপ্ত"
                            else -> "🎁 ফ্রি জয়েন করুন (টিকিট নিন)"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = if (hasJoined || giveaway.isFull) TextSecondary else Color.Black
                    )
                }
            }

            feedback?.let { (ok, msg) ->
                Text(
                    text = msg,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (ok) VictoryGreen else FireRed
                )
            }
        }
    }
}

@Composable
fun ReflexSpeedEntryCard(
    stats: ReflexGameStats,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onPlayClick() }
            .testTag("reflex_speed_entry_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(FlameCyan, NeonPink)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF061F2D), DarkCard, Color(0xFF260C22)))
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FlameCyan.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Bolt,
                        contentDescription = "One-Tap Reflex Lab",
                        tint = FlameCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "⚡ FF ওয়ান-ট্যাপ রিফ্লেক্স স্পিড ল্যাব",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = FlameCyan
                    )
                    Text(
                        text = if (stats.gamesPlayed > 0) {
                            "${stats.speedRankTitle} • বেস্ট: ${stats.bestScore} পয়েন্ট (${stats.bestReactionMs}ms)"
                        } else {
                            "১৫ সেকেন্ডে আপনার হেডশট রিফ্লেক্স ও স্পিড টেস্ট করুন!"
                        },
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = NeonPink,
                modifier = Modifier.testTag("open_reflex_game_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Play Reflex Mini-Game",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("খেলুন", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ReflexTapChallengeDialog(
    stats: ReflexGameStats,
    onRecordResult: (score: Int, bestReactionMs: Int, maxCombo: Int) -> ReflexGameStats,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var phase by remember { mutableStateOf("READY") } // READY, PLAYING, FINISHED
    var timeLeftSec by remember { mutableIntStateOf(15) }
    var score by remember { mutableIntStateOf(0) }
    var hits by remember { mutableIntStateOf(0) }
    var combo by remember { mutableIntStateOf(0) }
    var maxCombo by remember { mutableIntStateOf(0) }
    var bestReactionMs by remember { mutableIntStateOf(0) }
    var lastTapMs by remember { mutableIntStateOf(0) }
    var targetXFraction by remember { mutableFloatStateOf(0.5f) }
    var targetYFraction by remember { mutableFloatStateOf(0.5f) }
    var targetAppearedAtMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var latestSavedStats by remember(stats) { mutableStateOf(stats) }

    fun spawnNextTarget() {
        targetXFraction = Random.nextFloat().coerceIn(0.12f, 0.88f)
        targetYFraction = Random.nextFloat().coerceIn(0.12f, 0.88f)
        targetAppearedAtMillis = System.currentTimeMillis()
    }

    LaunchedEffect(phase) {
        if (phase == "PLAYING") {
            score = 0
            hits = 0
            combo = 0
            maxCombo = 0
            bestReactionMs = 0
            lastTapMs = 0
            timeLeftSec = 15
            spawnNextTarget()
            while (timeLeftSec > 0 && phase == "PLAYING") {
                kotlinx.coroutines.delay(1000L)
                timeLeftSec -= 1
            }
            if (phase == "PLAYING") {
                phase = "FINISHED"
                latestSavedStats = onRecordResult(score, bestReactionMs, maxCombo)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("reflex_challenge_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(FlameCyan, NeonPink, FlameYellow)))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚡ FF ওয়ান-ট্যাপ রিফ্লেক্স ল্যাব",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = FlameCyan
                        )
                        Text(
                            text = "ফান ও প্র্যাকটিস মোড • সবচেয়ে দ্রুত টার্গেটে হিট করুন!",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Reflex Lab", tint = Color.White)
                    }
                }

                // Live Stats Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkCard, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("সময়", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                "${timeLeftSec}s",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = if (timeLeftSec <= 5 && phase == "PLAYING") FireRed else FlameYellow
                            )
                        }
                    }
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkCard, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("স্কোর", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                "$score",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = VictoryGreen
                            )
                        }
                    }
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkCard, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("কম্বো", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                "x${combo.coerceAtLeast(1)}",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = NeonPink
                            )
                        }
                    }
                    Surface(shape = RoundedCornerShape(10.dp), color = DarkCard, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("রিফ্লেক্স", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                if (lastTapMs > 0) "${lastTapMs}ms" else "--",
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = FlameCyan
                            )
                        }
                    }
                }

                // Cyber Target Arena
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkBackground)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .clickable(enabled = phase == "PLAYING") {
                            // Missed target -> reset combo
                            combo = 0
                        }
                        .testTag("reflex_arena_box")
                ) {
                    val arenaWidthPx = constraints.maxWidth.toFloat()
                    val arenaHeightPx = constraints.maxHeight.toFloat()

                    when (phase) {
                        "READY" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.GpsFixed,
                                    contentDescription = "Target Preview",
                                    tint = FlameYellow,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "১৫ সেকেন্ডের ওয়ান-ট্যাপ চ্যালেঞ্জ!",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "বর্তমান র‍্যাঙ্ক: ${latestSavedStats.speedRankTitle}\nসর্বোচ্চ স্কোর: ${latestSavedStats.bestScore} পয়েন্ট",
                                    fontSize = 11.sp,
                                    color = FlameCyan,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { phase = "PLAYING" },
                                    modifier = Modifier
                                        .heightIn(min = 48.dp)
                                        .testTag("start_reflex_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                                ) {
                                    Text("🎯 শুরু করুন", color = Color.Black, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        "PLAYING" -> {
                            val targetSizePx = 148f
                            val xPx = ((arenaWidthPx - targetSizePx) * targetXFraction).coerceAtLeast(0f).roundToInt()
                            val yPx = ((arenaHeightPx - targetSizePx) * targetYFraction).coerceAtLeast(0f).roundToInt()

                            Box(
                                modifier = Modifier
                                    .offset { IntOffset(xPx, yPx) }
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(FlameYellow, FireOrange, FireRed)
                                        )
                                    )
                                    .border(2.dp, Color.White, CircleShape)
                                    .clickable {
                                        val now = System.currentTimeMillis()
                                        val reaction = (now - targetAppearedAtMillis).toInt().coerceIn(50, 3000)
                                        lastTapMs = reaction
                                        if (bestReactionMs == 0 || reaction < bestReactionMs) {
                                            bestReactionMs = reaction
                                        }
                                        combo += 1
                                        if (combo > maxCombo) maxCombo = combo
                                        val mult = combo.coerceAtMost(5)
                                        val speedBonus = if (reaction <= 320) 10 else 0
                                        score += (10 * mult) + speedBonus
                                        hits += 1
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        spawnNextTarget()
                                    }
                                    .testTag("reflex_target_dot"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.GpsFixed,
                                    contentDescription = "Hit Target",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        "FINISHED" -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("🏁 চ্যালেঞ্জ সমাপ্ত!", fontSize = 16.sp, fontWeight = FontWeight.Black, color = FlameYellow)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "স্কোর: $score পয়েন্ট • হিট: $hits বার • ম্যাক্স কম্বো: x$maxCombo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "দ্রুততম শট: ${if (bestReactionMs > 0) "${bestReactionMs}ms" else "--"} | র‍্যাঙ্ক: ${latestSavedStats.speedRankTitle}",
                                    fontSize = 12.sp,
                                    color = FlameCyan,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(
                                        onClick = onDismiss,
                                        modifier = Modifier.heightIn(min = 48.dp)
                                    ) {
                                        Text("বন্ধ করুন", color = TextPrimary)
                                    }
                                    Button(
                                        onClick = { phase = "PLAYING" },
                                        modifier = Modifier
                                            .heightIn(min = 48.dp)
                                            .testTag("replay_reflex_btn"),
                                        colors = ButtonDefaults.buttonColors(containerColor = FlameCyan)
                                    ) {
                                        Icon(Icons.Filled.Refresh, contentDescription = "Replay", tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("আবার খেলুন", color = Color.Black, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
