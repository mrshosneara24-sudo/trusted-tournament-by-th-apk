package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.NeonPink
import com.example.ui.theme.VictoryGreen

private data class SensitivityPreset(
    val general: Int,
    val redDot: Int,
    val scope2x: Int,
    val scope4x: Int,
    val sniperScope: Int,
    val freeLook: Int,
    val fireButtonSize: Int,
    val proTip: String
)

private fun computeSensitivityPreset(
    ramTierIndex: Int,
    playStyleIndex: Int,
    highDpiEnabled: Boolean
): SensitivityPreset {
    val base = when (ramTierIndex) {
        0 -> SensitivityPreset(192, 186, 178, 168, 120, 145, 44, "লো-র‍্যাম ফোনে হালকা ড্র্যাগ ও ৪৪% ফায়ার বাটনে রেড নম্বর সহজে আসবে।")
        1 -> SensitivityPreset(182, 176, 165, 158, 115, 140, 48, "ব্যালেন্সড ৬-৮ জিবি ডিভাইসের জন্য স্মুথ 'J-Drag' মুভমেন্ট সেরা কাজ করে।")
        else -> SensitivityPreset(168, 164, 152, 144, 108, 130, 52, "হাই রিফ্রেশ-রেট ও প্রো ডিভাইসে ওভার-ড্র্যাগ এড়াতে মাঝারি জেনারেল বেস্ট।")
    }

    val styleAdjusted = when (playStyleIndex) {
        0 -> base.copy(
            general = (base.general + 6).coerceAtMost(200),
            redDot = (base.redDot + 8).coerceAtMost(200),
            fireButtonSize = (base.fireButtonSize - 2).coerceIn(36, 65),
            proTip = "ওয়ান-ট্যাপে শত্রুর বুকের কাছ থেকে দ্রুত উপরের দিকে 'Rotation Drag' করুন।"
        )
        1 -> base.copy(
            general = (base.general + 4).coerceAtMost(200),
            scope2x = (base.scope2x + 10).coerceAtMost(200),
            fireButtonSize = (base.fireButtonSize + 2).coerceIn(36, 65),
            proTip = "MP40/SMG রাশে ক্রসহেয়ার সবসময় চেস্ট-লেভেলে রেখে হালকা আপ-ড্র্যাগ করুন।"
        )
        else -> base.copy(
            sniperScope = (base.sniperScope + 25).coerceAtMost(200),
            scope4x = (base.scope4x + 12).coerceAtMost(200),
            proTip = "ডাবল স্নাইপার কুইক-সুইচে রি-লোড স্কিপ করে স্থির এইম ধরে রাখুন।"
        )
    }

    return if (highDpiEnabled) {
        styleAdjusted.copy(
            general = (styleAdjusted.general - 10).coerceIn(100, 200),
            redDot = (styleAdjusted.redDot - 8).coerceIn(95, 200),
            scope2x = (styleAdjusted.scope2x - 6).coerceIn(90, 200)
        )
    } else {
        styleAdjusted
    }
}

@Composable
fun SensitivityProDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current

    var ramTierIndex by remember { mutableIntStateOf(1) }
    var playStyleIndex by remember { mutableIntStateOf(0) }
    var highDpiEnabled by remember { mutableStateOf(false) }

    val ramOptions = listOf("2-4 GB", "6-8 GB", "12+ GB")
    val styleOptions = listOf("🎯 One-Tap", "⚡ SMG Rush", "🔭 Sniper")
    val preset = remember(ramTierIndex, playStyleIndex, highDpiEnabled) {
        computeSensitivityPreset(ramTierIndex, playStyleIndex, highDpiEnabled)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.93f)
                .testTag("sensitivity_pro_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(FlameYellow, Color.White.copy(alpha = 0.7f), NeonPink))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
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
                                .background(FlameYellow.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GpsFixed,
                                contentDescription = "Sensitivity Lab",
                                tint = FlameYellow,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "FF প্রো সেনসিটিভিটি ল্যাব",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            Text(
                                text = "হেডশট এবং কাস্টম রুম সেটআপ জেনারেটর",
                                fontSize = 11.sp,
                                color = FlameYellow
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

                Spacer(modifier = Modifier.height(14.dp))

                // Device Tier Selector
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.PhoneAndroid,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "আপনার ফোনের RAM:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ramOptions.forEachIndexed { index, label ->
                        val selected = ramTierIndex == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) FlameYellow else DarkCard,
                            border = BorderStroke(1.dp, if (selected) FlameYellow else DarkBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    ramTierIndex = index
                                }
                                .testTag("sens_ram_$index")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Play Style Selector
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Speed,
                        contentDescription = null,
                        tint = NeonPink,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "খেলার স্টাইল:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    styleOptions.forEachIndexed { index, label ->
                        val selected = playStyleIndex == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) BkashPink else DarkCard,
                            border = BorderStroke(1.dp, if (selected) NeonPink else DarkBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    playStyleIndex = index
                                }
                                .testTag("sens_style_$index")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom DPI Toggle
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "প্রো DPI অপ্টিমাইজেশন (৪৪০-৫২০)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (highDpiEnabled) "DPI অনুযায়ী রিকয়েল ব্যালেন্স অন আছে" else "স্ট্যান্ডার্ড ডিফল্ট ফোন DPI মোড",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = highDpiEnabled,
                            onCheckedChange = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                highDpiEnabled = it
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = FlameYellow
                            ),
                            modifier = Modifier.testTag("sens_dpi_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Animated Stat Meters Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SensitivityMeterRow("জেনারেল (General)", preset.general, FlameYellow)
                        SensitivityMeterRow("রেড ডট (Red Dot)", preset.redDot, NeonPink)
                        SensitivityMeterRow("২এক্স স্কোপ (2x Scope)", preset.scope2x, FlameCyan)
                        SensitivityMeterRow("৪এক্স স্কোপ (4x Scope)", preset.scope4x, Color.White)
                        SensitivityMeterRow("স্নাইপার স্কোপ (Sniper)", preset.sniperScope, FlameYellow)
                        SensitivityMeterRow("ফ্রি লুক (Free Look)", preset.freeLook, VictoryGreen)

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔘 প্রস্তাবিত ফায়ার বাটন সাইজ:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FlameYellow.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, FlameYellow)
                            ) {
                                Text(
                                    text = "${preset.fireButtonSize}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FlameYellow,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Pro Tip
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1A170D),
                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 প্রো টিপস: ${preset.proTip}",
                        fontSize = 11.sp,
                        color = Color.White,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1-Tap Copy Settings Button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val summary = """
                            🎯 FF Pro Sensitivity (${ramOptions[ramTierIndex]} | ${styleOptions[playStyleIndex]})
                            • General: ${preset.general}
                            • Red Dot: ${preset.redDot}
                            • 2x Scope: ${preset.scope2x}
                            • 4x Scope: ${preset.scope4x}
                            • Sniper Scope: ${preset.sniperScope}
                            • Free Look: ${preset.freeLook}
                            • Fire Button Size: ${preset.fireButtonSize}%
                        """.trimIndent()
                        clipboardManager.setText(AnnotatedString(summary))
                        Toast.makeText(context, "সেনসিটিভিটি সেটিংস কপি হয়েছে! 🎮", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("copy_sensitivity_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlameYellow,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Copy Settings",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("১-ট্যাপে সব সেটিংস কপি করুন", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun SensitivityMeterRow(
    label: String,
    value: Int,
    barColor: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (value / 200f).coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = label
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = Color.White.copy(alpha = 0.88f))
            Text(
                text = "$value / 200",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = barColor
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = Color(0xFF282534),
            strokeCap = StrokeCap.Round
        )
    }
}
