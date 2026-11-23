package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BonusGiftDrop
import com.example.model.DailyTriviaProgress
import com.example.model.FreeFireTriviaBank
import com.example.model.LiveCommunityPoll
import com.example.model.PromoCodeItem
import com.example.ui.theme.BkashPink
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WaterBlue

@Composable
fun PromoCodeRedeemCard(
    activePromoCount: Int,
    onRedeemCode: (String) -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    var codeInput by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wallet_promo_redeem_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(FlameYellow.copy(alpha = 0.6f), WaterBlue.copy(alpha = 0.5f)))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(FlameYellow.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ConfirmationNumber,
                            contentDescription = "Promo Redeem",
                            tint = FlameYellow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🎟️ প্রোমো / গিফট রিডিম কোড",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = FlameYellow
                        )
                        Text(
                            text = "অফিসিয়াল কোড বসিয়ে তাৎক্ষণিক ওয়ালেট বোনাস পান",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
                if (activePromoCount > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VictoryGreen.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "$activePromoCount সক্রিয়",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = VictoryGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        codeInput = it.uppercase()
                        feedback = null
                    },
                    placeholder = { Text("যেমন: THBONUS50", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("promo_code_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                Button(
                    onClick = {
                        val res = onRedeemCode(codeInput.trim())
                        feedback = res
                        if (res.first) codeInput = ""
                    },
                    modifier = Modifier
                        .heightIn(min = 52.dp)
                        .testTag("redeem_promo_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Redeem,
                        contentDescription = "Redeem Code",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("রিডিম", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color.Black)
                }
            }

            AnimatedVisibility(visible = feedback != null) {
                feedback?.let { (ok, msg) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (ok) VictoryGreen.copy(alpha = 0.14f) else FireRed.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, if (ok) VictoryGreen.copy(alpha = 0.5f) else FireRed.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = msg,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (ok) VictoryGreen else Color(0xFFFCA5A5),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveBonusDropBannerCard(
    drop: BonusGiftDrop,
    hasClaimed: Boolean,
    onClaimClick: (String) -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    var localMsg by remember(drop.id) { mutableStateOf<Pair<Boolean, String>?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("bonus_drop_card_${drop.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(listOf(FireOrange, FlameYellow, FlameCyan))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF281207), DarkCard, Color(0xFF072133))
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FlameYellow.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = "Bonus Gift Drop",
                            tint = FlameYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = drop.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = DisplayFontFamily,
                                fontWeight = FontWeight.Black
                            ),
                            color = FlameYellow,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${drop.subtitle} • প্রেরক: ${drop.createdBy}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 2
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BkashPink.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.7f))
                ) {
                    Text(
                        text = "+৳${drop.amountPerClaim}",
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = FlameYellow,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎁 বাকি বক্স: ${drop.remainingBoxes}/${drop.totalBoxes} টি",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (drop.isSoldOut) FireRed else FlameCyan
                )
                Button(
                    onClick = { localMsg = onClaimClick(drop.id) },
                    enabled = !hasClaimed && !drop.isSoldOut && drop.active,
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("claim_bonus_drop_btn_${drop.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (hasClaimed || drop.isSoldOut) DarkElevated else VictoryGreen
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp)
                ) {
                    Text(
                        text = when {
                            hasClaimed -> "ক্লেইম হয়েছে ✓"
                            drop.isSoldOut -> "বক্স শেষ"
                            else -> "দ্রুত ক্লেইম করুন"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (hasClaimed || drop.isSoldOut) TextSecondary else Color.Black
                    )
                }
            }

            localMsg?.let { (ok, msg) ->
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
fun LiveCommunityPollCard(
    poll: LiveCommunityPoll,
    hasVoted: Boolean,
    onVote: (pollId: String, optionId: String) -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    val totalVotes = poll.totalVotes.coerceAtLeast(0)
    var localFeedback by remember(poll.id) { mutableStateOf<String?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag("community_poll_card_${poll.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Filled.Poll,
                        contentDescription = "Live Community Poll",
                        tint = FlameCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "📊 ${poll.question}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = WaterBlue.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "$totalVotes ভোট",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlameCyan,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            poll.options.forEach { option ->
                val pct = if (totalVotes > 0) ((option.votes * 100f) / totalVotes).toInt() else 0
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, if (hasVoted) FlameCyan.copy(alpha = 0.4f) else DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = !hasVoted && poll.active) {
                            val res = onVote(poll.id, option.id)
                            localFeedback = res.second
                        }
                        .testTag("poll_option_${poll.id}_${option.id}")
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (hasVoted && totalVotes > 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(pct / 100f)
                                    .height(48.dp)
                                    .background(WaterBlue.copy(alpha = 0.22f))
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = if (hasVoted) "$pct% (${option.votes})" else "ভোট দিন",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hasVoted) FlameCyan else FlameYellow
                            )
                        }
                    }
                }
            }

            localFeedback?.let {
                Text(text = it, fontSize = 11.sp, color = VictoryGreen, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun DailyTriviaChallengeCard(
    progress: DailyTriviaProgress,
    onAnswerQuestion: (selectedIndex: Int) -> Pair<Boolean, String>,
    modifier: Modifier = Modifier
) {
    val todayKey = remember { FreeFireTriviaBank.todayDateKey() }
    val alreadyAttempted = progress.lastAttemptDateKey == todayKey
    val question = remember(progress.currentQuestionIndex) {
        FreeFireTriviaBank.questionForDay(progress.currentQuestionIndex)
    }
    var lastResultMsg by remember(todayKey, alreadyAttempted) { mutableStateOf<Pair<Boolean, String>?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_trivia_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, if (alreadyAttempted) VictoryGreen.copy(alpha = 0.45f) else FlameYellow.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(FlameYellow.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Quiz,
                            contentDescription = "Daily Trivia Challenge",
                            tint = FlameYellow,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🧠 দৈনিক ফ্রি ফায়ার কুইজ চ্যালেঞ্জ",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                            color = FlameYellow
                        )
                        Text(
                            text = "ফ্রি ফায়ার জ্ঞান যাচাই করুন • ফান চ্যালেঞ্জ (সঠিক: ${progress.totalCorrectCount}টি)",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
                if (alreadyAttempted) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Attempted Today",
                        tint = if (progress.lastAnsweredCorrectly) VictoryGreen else FlameOrange,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            question.options.forEachIndexed { idx, optLabel ->
                val isSelected = alreadyAttempted && progress.lastSelectedOption == idx
                val isCorrectOption = idx == question.correctIndex
                val borderCol = when {
                    alreadyAttempted && isCorrectOption -> VictoryGreen
                    alreadyAttempted && isSelected && !isCorrectOption -> FireRed
                    else -> DarkBorder
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when {
                        alreadyAttempted && isCorrectOption -> VictoryGreen.copy(alpha = 0.16f)
                        alreadyAttempted && isSelected && !isCorrectOption -> FireRed.copy(alpha = 0.16f)
                        else -> DarkSurface
                    },
                    border = BorderStroke(1.dp, borderCol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(enabled = !alreadyAttempted) {
                            lastResultMsg = onAnswerQuestion(idx)
                        }
                        .testTag("trivia_option_${question.id}_$idx")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${('A' + idx)}. $optLabel",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (alreadyAttempted && isCorrectOption) {
                            Text("✓ সঠিক", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VictoryGreen)
                        }
                    }
                }
            }

            if (alreadyAttempted) {
                Text(
                    text = lastResultMsg?.second
                        ?: if (progress.lastAnsweredCorrectly) "✅ আজকের কুইজ জিতেছেন! আগামীকাল নতুন প্রশ্ন আসবে।"
                        else "💡 উত্তর: ${question.options.getOrNull(question.correctIndex).orEmpty()} • আগামীকাল আবার চেষ্টা করুন!",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (progress.lastAnsweredCorrectly) VictoryGreen else FlameCyan
                )
            }
        }
    }
}
