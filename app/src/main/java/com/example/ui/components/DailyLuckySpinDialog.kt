package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.model.SpinHistoryRecord
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DailyLuckySpinDialog(
    hasSpunToday: Boolean,
    onDismiss: () -> Unit,
    onSpinWheel: () -> Int,
    spinHistory: List<SpinHistoryRecord> = emptyList()
) {
    WeeklyLuckySpinDialog(
        hasSpunThisWeek = hasSpunToday,
        onDismiss = onDismiss,
        onSpinWheel = onSpinWheel,
        spinHistory = spinHistory
    )
}

@Composable
fun WeeklyLuckySpinDialog(
    hasSpunThisWeek: Boolean,
    onDismiss: () -> Unit,
    onSpinWheel: () -> Int,
    spinHistory: List<SpinHistoryRecord> = emptyList()
) {
    val scope = rememberCoroutineScope()
    var isSpinning by remember { mutableStateOf(false) }
    var wonAmount by remember { mutableIntStateOf(0) }
    var showOddsBreakdown by remember { mutableStateOf(false) }
    val rotation = remember { Animatable(0f) }

    // Slices for the visual wheel: mostly 3, 4, 5 taka, with ultra-rare ৳30 jackpot
    val prizes = listOf("৳৩", "৳৪", "৳৫", "৳৩", "৳৪", "৳৫", "৳৮", "৳৩০ 👑")
    val sliceColors = listOf(
        Color(0xFF3B82F6), // Blue ৳3
        Color(0xFF10B981), // Green ৳4
        Color(0xFFFF9100), // Amber ৳5
        Color(0xFF8B5CF6), // Purple ৳3
        Color(0xFF00E5FF), // Cyan ৳4
        Color(0xFFFF5722), // Orange ৳5
        Color(0xFFE2136E), // Bkash Pink ৳8
        Color(0xFFFFD700)  // Gold for ৳30 Jackpot
    )

    val slicePaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 34f
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
    }
    val jackpotPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#FFD700")
            textSize = 36f
            isFakeBoldText = true
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, FlameYellow, RoundedCornerShape(24.dp))
                .testTag("lucky_spin_dialog"),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                                .background(FlameYellow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Spin",
                                tint = FlameYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🎡 সাপ্তাহিক লাকি স্পিন",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = FlameYellow
                                )
                            )
                            Text(
                                text = "প্রতি সপ্তাহে ১টি ফ্রি স্পিন • সর্বোচ্চ ৩০ টাকা",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Weekly Rules Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Stars, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "স্পিন সুযোগ:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = if (hasSpunThisWeek) "০/১ টি (এই সপ্তাহ শেষ)" else "১/১ টি ফ্রি স্পিন বাকি",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (hasSpunThisWeek) BkashPink else VictoryGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Live Community Activity Ticker
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF141424),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282845)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "লাইভ উইনার্স: Shakib (৳৪) • Tanvir (৳৩) • Miraz (৳৫) • ALVI (৳৩) • 👑 শেষ ৩০৳: DEVIL (২ মাস আগে)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wheel Visual with Needle Pointer
                Box(
                    modifier = Modifier.size(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating Wheel
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(sliceColors)
                            )
                            .border(4.dp, FlameYellow, CircleShape)
                            .rotate(rotation.value),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(200.dp)) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = size.width / 2f
                            val sliceAngle = 360f / prizes.size

                            for (i in prizes.indices) {
                                val angleRad = Math.toRadians((i * sliceAngle).toDouble())
                                val endX = center.x + radius * Math.cos(angleRad).toFloat()
                                val endY = center.y + radius * Math.sin(angleRad).toFloat()
                                drawLine(
                                    color = Color.White.copy(alpha = 0.4f),
                                    start = center,
                                    end = Offset(endX, endY),
                                    strokeWidth = 2.dp.toPx()
                                )

                                // Draw text for each slice on the wheel
                                val midAngleRad = Math.toRadians((i * sliceAngle + sliceAngle / 2f).toDouble())
                                val textRadius = radius * 0.68f
                                val textX = center.x + textRadius * Math.cos(midAngleRad).toFloat()
                                val textY = center.y + textRadius * Math.sin(midAngleRad).toFloat() + 10f
                                drawContext.canvas.nativeCanvas.drawText(
                                    prizes[i],
                                    textX,
                                    textY,
                                    if (i == 7) jackpotPaint else slicePaint
                                )
                            }

                            // Outer gold ring
                            drawCircle(
                                color = Color(0xFFFFD700),
                                radius = radius,
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }

                        // Center Hub
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F0F1B))
                                .border(2.dp, FlameYellow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = "TH",
                                    tint = FlameYellow,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "TH 30৳",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    ),
                                    color = FlameYellow
                                )
                            }
                        }
                    }

                    // Top Pointer Arrow (Always fixed at top pointing down)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 2.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(FlameYellow)
                            .border(1.5.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▼", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Highest prize highlight & realistic odds notice (Expandable)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FlameYellow.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showOddsBreakdown = !showOddsBreakdown }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "জ্যাকপট: ৳৩০ (আল্ট্রা রেয়ার • ০.০৫%)",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = FlameYellow
                                )
                            }
                            Text(
                                text = if (showOddsBreakdown) "লুকান ▲" else "সম্ভাবনা চার্ট ▼",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = FlameYellow
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 ৯৯% ক্ষেত্রে নিশ্চিতভাবে ৩৳, ৪৳ ও ৫৳ নগদ উপহার পাবেন। ৩০৳ মেগা জ্যাকপট জেতা চরম কঠিন!",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            textAlign = TextAlign.Center
                        )

                        if (showOddsBreakdown) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F0F1A),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("📊 সম্পূর্ণ স্বচ্ছ স্পিন সম্ভাবনা হার:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = FlameYellow)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("• ৳৩ : ৫৫% (সবচেয়ে বেশি বার পাবেন)", fontSize = 10.sp, color = Color.White)
                                    Text("• ৳৪ : ৩০% (উচ্চ সম্ভাবনা)", fontSize = 10.sp, color = Color.White)
                                    Text("• ৳৫ : ১৪% (মাঝারি সম্ভাবনা)", fontSize = 10.sp, color = Color.White)
                                    Text("• ৳৮ : ০.৯৫% (বিরল পুরস্কার)", fontSize = 10.sp, color = Color.White)
                                    Text("• ৳৩০ : ০.০৫% (চরম দুর্লভ মেগা জ্যাকপট - ২০০০ জনে মাত্র ১ জন!)", fontSize = 10.sp, color = FlameYellow, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Result or Notice Box
                if (wonAmount > 0) {
                    if (wonAmount == 30) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF2E2405),
                            border = androidx.compose.foundation.BorderStroke(2.dp, FlameYellow),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "👑 অলৌকিক মেগা জ্যাকপট! আপনি ৳৩০ জিতেছেন!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = FlameYellow
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "অবিশ্বাস্য! আপনি ০.০৫% ভাগ্যের চরম দুর্লভ ৳৩০ মেগা জ্যাকপট জিতেছেন!\nপুরস্কারের পুরো টাকা সরাসরি আপনার বিকাশ ওয়ালেটে যোগ হয়েছে।",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = VictoryGreen.copy(alpha = 0.18f),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, VictoryGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🎉 অভিনন্দন! আপনি জিতেছেন ৳$wonAmount!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = VictoryGreen
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "নগদ ৳$wonAmount সরাসরি আপনার বিকাশ ওয়ালেটে যুক্ত হয়েছে।\nপরবর্তী ফ্রি স্পিন আগামী সপ্তাহে পাবেন।",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                } else if (hasSpunThisWeek) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, BkashPink.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Schedule, contentDescription = null, tint = BkashPink, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "এই সপ্তাহের স্পিন সম্পন্ন হয়েছে!",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BkashPink
                                )
                                Text(
                                    text = "প্রতি সপ্তাহে ১ বার ফ্রি স্পিন পাওয়া যায়। পরবর্তী স্পিন আগামী সপ্তাহে আনলক হবে (সর্বোচ্চ ৩০ টাকা)।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // User's Recent Spin History Preview (if any)
                if (spinHistory.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkCard.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "📜 আপনার সাম্প্রতিক স্পিন হিস্ট্রি:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                spinHistory.take(3).forEach { record ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (record.isJackpot) Color(0xFF2E2405) else Color(0xFF1B2333),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (record.isJackpot) FlameYellow else DarkBorder)
                                    ) {
                                        Text(
                                            text = if (record.isJackpot) "👑 ৳${record.amount} (জ্যাকপট)" else "৳${record.amount} (${record.date})",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (record.isJackpot) FlameYellow else VictoryGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Button
                Button(
                    onClick = {
                        if (isSpinning || hasSpunThisWeek) return@Button
                        isSpinning = true
                        scope.launch {
                            val prize = onSpinWheel()

                            // Match the won prize to the corresponding slice index on the wheel
                            val matchingIndices = prizes.indices.filter { idx ->
                                val clean = prizes[idx].replace("৳", "").replace("★", "").replace("👑", "").trim()
                                clean.toIntOrNull() == prize
                            }
                            val targetIndex = matchingIndices.randomOrNull() ?: 0
                            val sliceAngle = 360f / prizes.size
                            val sliceCenter = targetIndex * sliceAngle + sliceAngle / 2f
                            val targetAngle = (270f - sliceCenter + 360f) % 360f
                            val currentTurns = (rotation.value / 360f).toInt()
                            val finalTarget = (currentTurns + 6) * 360f + targetAngle

                            rotation.animateTo(
                                targetValue = finalTarget,
                                animationSpec = tween(durationMillis = 2800, easing = FastOutSlowInEasing)
                            )
                            wonAmount = prize
                            isSpinning = false
                        }
                    },
                    enabled = !hasSpunThisWeek && !isSpinning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("spin_wheel_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlameYellow,
                        disabledContainerColor = DarkCard
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = if (!hasSpunThisWeek && !isSpinning) Color.Black else Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSpinning) "চাকা ঘুরছে..."
                        else if (hasSpunThisWeek) "এই সপ্তাহের স্পিন শেষ (আগামী সপ্তাহে আসুন)"
                        else "সাপ্তাহিক ফ্রি স্পিন করুন 🎯 (জিতুন ৩-৩০৳)",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = if (!hasSpunThisWeek && !isSpinning) Color.Black else Color.White.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
