package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.BadgeType
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameYellow

@Composable
fun BadgePill(
    badge: BadgeType,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    compact: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier

    // Authentic Free Fire visual gradients and styling per badge type
    val (gradientColors, borderColor, textColor) = when (badge) {
        BadgeType.V_BADGE -> Triple(
            listOf(Color(0xFF6B4400), Color(0xFFD48800), Color(0xFFFFC107)),
            Color(0xFFFFE082),
            Color(0xFFFFF8E1)
        )
        BadgeType.PRO_PLAYER -> Triple(
            listOf(Color(0xFF003056), Color(0xFF007799), Color(0xFF00E5FF)),
            Color(0xFF80D8FF),
            Color(0xFFE0F7FA)
        )
        BadgeType.GREEN_VERIFIED -> Triple(
            listOf(Color(0xFF064E3B), Color(0xFF059669), Color(0xFF10B981)),
            Color(0xFFA7F3D0),
            Color(0xFFECFDF5)
        )
        BadgeType.MODERATOR -> Triple(
            listOf(Color(0xFF7F1D1D), Color(0xFFC2410C), Color(0xFFFF5722)),
            Color(0xFFFFCCBC),
            Color(0xFFFBE9E7)
        )
        BadgeType.BOOYAH_MASTER -> Triple(
            listOf(Color(0xFF78350F), Color(0xFFD97706), Color(0xFFF59E0B)),
            Color(0xFFFDE68A),
            Color(0xFFFFFBEB)
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .then(clickModifier)
            .testTag("badge_pill_${badge.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 7.dp else 10.dp,
                vertical = if (compact) 2.dp else 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                if (badge == BadgeType.V_BADGE) {
                    Text(
                        text = "Ⓥ",
                        fontSize = if (compact) 11.sp else 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFD700)
                    )
                } else if (badge == BadgeType.PRO_PLAYER) {
                    Text(
                        text = "⚡",
                        fontSize = if (compact) 10.sp else 12.sp
                    )
                } else {
                    Text(
                        text = badge.iconEmoji,
                        fontSize = if (compact) 10.sp else 12.sp
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = badge.tagText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    fontSize = if (compact) 10.sp else 11.sp
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun BadgeShowcaseDialog(
    currentBadges: List<BadgeType>,
    onDismiss: () -> Unit
) {
    val allBadges = BadgeType.values().toList()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 700.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, FlameYellow, RoundedCornerShape(24.dp))
                .testTag("badge_showcase_dialog"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(FlameYellow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MilitaryTech,
                                contentDescription = null,
                                tint = FlameYellow,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "🏆 অফিসিয়াল ফ্রি ফায়ার ব্যাজ",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = FlameYellow
                            )
                            Text(
                                text = "রিয়েল Free Fire V-Badge ও PRO ব্যাজ সিস্টেম",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real Free Fire 3D Showcase Banner (V-Badge & Pro-Badge Spotlight)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // V-Badge Spotlight Card
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.5.dp, Color(0xFFFFD700), RoundedCornerShape(14.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_ff_v_badge),
                                    contentDescription = "Real Free Fire V Badge",
                                    modifier = Modifier.size(64.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Ⓥ [V] VIP ব্যাজ",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                color = Color(0xFFFFD700),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "টাকা লাগালে পাবেন",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 10.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(60.dp)
                                .background(DarkBorder)
                        )

                        // PRO-Badge Spotlight Card
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(14.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_ff_pro_badge),
                                    contentDescription = "Real Free Fire PRO Badge",
                                    modifier = Modifier.size(64.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "⚡ PRO PLAYER",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                color = Color(0xFF00E5FF),
                                fontSize = 11.sp
                            )
                            Text(
                                text = "বেশি ম্যাচ খেললে পাবেন",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // User's Active Badges Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "আপনার সক্রিয় ব্যাজসমূহ (${currentBadges.size}টি):",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = FlameYellow
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (currentBadges.isEmpty()) {
                            Text(
                                text = "এখনো কোনো বিশেষ ব্যাজ অর্জিত হয়নি। ম্যাচ খেলুন বা ডিপোজিট করুন!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentBadges.forEach { badge ->
                                    BadgePill(badge = badge)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ব্যাজ ও অর্জনের শর্তাবলী:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allBadges) { badge ->
                        val isUnlocked = currentBadges.contains(badge)
                        val badgeColor = Color(badge.hexColor)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnlocked) badgeColor.copy(alpha = 0.12f) else DarkCard
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isUnlocked) badgeColor else DarkBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Real Free Fire 3D Icon or vector
                                if (badge == BadgeType.V_BADGE) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.5.dp, Color(0xFFFFD700), RoundedCornerShape(10.dp))
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_ff_v_badge),
                                            contentDescription = "FF V Badge",
                                            modifier = Modifier.size(48.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                } else if (badge == BadgeType.PRO_PLAYER) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.5.dp, Color(0xFF00E5FF), RoundedCornerShape(10.dp))
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_ff_pro_badge),
                                            contentDescription = "FF PRO Badge",
                                            modifier = Modifier.size(48.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(badgeColor.copy(alpha = 0.2f))
                                            .border(1.5.dp, badgeColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(badge.iconEmoji, fontSize = 20.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = badge.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = badgeColor
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        BadgePill(badge = badge, compact = true)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = badge.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "🎯 শর্ত: ${badge.criteria}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isUnlocked) Color(0xFF10B981) else Color(0xFFFFC107),
                                        fontSize = 10.sp
                                    )
                                }

                                if (isUnlocked) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF10B981),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = "Unlocked",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
                ) {
                    Text("ঠিক আছে", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
