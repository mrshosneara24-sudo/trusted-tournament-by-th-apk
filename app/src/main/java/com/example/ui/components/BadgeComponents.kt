package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import com.example.R
import com.example.model.AppealStatus
import com.example.model.BadgeAppeal
import com.example.model.BadgeType
import com.example.model.UserProfile
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@DrawableRes
fun getBadgeDrawableRes(badge: BadgeType): Int? = when (badge) {
    BadgeType.V_BADGE -> R.drawable.badge_v_logo
    BadgeType.PRO_PLAYER -> R.drawable.badge_pro_logo
    BadgeType.PRIME_SPENDER -> R.drawable.badge_spender_logo
    BadgeType.MODERATOR -> R.drawable.badge_admin_logo
    BadgeType.GREEN_VERIFIED -> R.drawable.badge_owner_logo
    BadgeType.BOOYAH_MASTER,
    BadgeType.HEADSHOT_KING,
    BadgeType.KILL_MACHINE,
    BadgeType.CHAMPION_LEGEND -> null
}

private data class BadgeVisualStyle(
    val gradientColors: List<Color>,
    val borderColor: Color,
    val textColor: Color,
    val glowColor: Color
)

private fun getBadgeVisualStyle(badge: BadgeType): BadgeVisualStyle = when (badge) {
    BadgeType.V_BADGE -> BadgeVisualStyle(
        gradientColors = listOf(Color(0xFF451A03), Color(0xFFB45309), Color(0xFFFFC107)),
        borderColor = Color(0xFFFFE082),
        textColor = Color(0xFFFFFBEB),
        glowColor = Color(0xFFFFD700)
    )
    BadgeType.GREEN_VERIFIED -> BadgeVisualStyle(
        gradientColors = listOf(Color(0xFF052E16), Color(0xFF059669), Color(0xFF22C55E)),
        borderColor = Color(0xFF86EFAC),
        textColor = Color(0xFFECFDF5),
        glowColor = Color(0xFF22C55E)
    )
    BadgeType.MODERATOR -> BadgeVisualStyle(
        gradientColors = listOf(Color(0xFF0A192F), Color(0xFF1D3B72), Color(0xFFD97706)),
        borderColor = Color(0xFFFBBF24),
        textColor = Color(0xFFFEF3C7),
        glowColor = Color(0xFFF59E0B)
    )
    BadgeType.PRO_PLAYER -> BadgeVisualStyle(
        gradientColors = listOf(Color(0xFF2E1A00), Color(0xFF9A6700), Color(0xFFEAB308)),
        borderColor = Color(0xFFFDE047),
        textColor = Color(0xFFFEFCE8),
        glowColor = Color(0xFFEAB308)
    )
    BadgeType.PRIME_SPENDER -> BadgeVisualStyle(
        gradientColors = listOf(Color(0xFF3F1600), Color(0xFFC2410C), Color(0xFFFF9800)),
        borderColor = Color(0xFFFFD54F),
        textColor = Color(0xFFFFF8E1),
        glowColor = Color(0xFFFF9800)
    )
    BadgeType.BOOYAH_MASTER -> BadgeVisualStyle(
        gradientColors = listOf(Color(0xFF431407), Color(0xFFEA580C), Color(0xFFF59E0B)),
        borderColor = Color(0xFFFDBA74),
        textColor = Color(0xFFFFF7ED),
        glowColor = Color(0xFFFF9100)
    )
    BadgeType.HEADSHOT_KING -> BadgeVisualStyle(
        gradientColors = listOf(Color(0xFF042F40), Color(0xFF0284C7), Color(0xFF00E5FF)),
        borderColor = Color(0xFF67E8F9),
        textColor = Color(0xFFECFEFF),
        glowColor = Color(0xFF00E5FF)
    )
    BadgeType.KILL_MACHINE -> BadgeVisualStyle(
        gradientColors = listOf(Color(0xFF450A0A), Color(0xFFB91C1C), Color(0xFFEF4444)),
        borderColor = Color(0xFFFCA5A5),
        textColor = Color(0xFFFEF2F2),
        glowColor = Color(0xFFEF4444)
    )
    BadgeType.CHAMPION_LEGEND -> BadgeVisualStyle(
        gradientColors = listOf(Color(0xFF2E1065), Color(0xFF7E22CE), Color(0xFFA855F7)),
        borderColor = Color(0xFFD8B4FE),
        textColor = Color(0xFFFAF5FF),
        glowColor = Color(0xFFA855F7)
    )
}

@Composable
fun BadgeEmblemArt(
    badge: BadgeType,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val style = getBadgeVisualStyle(badge)
    val drawableRes = getBadgeDrawableRes(badge)
    val shape = if (badge == BadgeType.GREEN_VERIFIED || drawableRes == null) {
        CircleShape
    } else {
        RoundedCornerShape((size.value * 0.24f).dp)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        style.glowColor.copy(alpha = 0.35f),
                        Color(0xFF0B0F19)
                    )
                )
            )
            .border(
                width = if (size >= 40.dp) 1.5.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(style.borderColor, style.glowColor.copy(alpha = 0.6f))
                ),
                shape = shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (drawableRes != null) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = badge.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding((size.value * 0.04f).dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(style.gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badge.iconEmoji,
                    fontSize = (size.value * 0.44f).sp
                )
            }
        }
    }
}

@Composable
fun BadgePill(
    badge: BadgeType,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true,
    compact: Boolean = false,
    animateEntrance: Boolean = false,
    staggerIndex: Int = 0,
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 1.09f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "badge_pill_press_scale"
    )
    val entranceScale = remember(badge, animateEntrance) {
        Animatable(if (animateEntrance) 0.84f else 1f)
    }
    LaunchedEffect(badge, animateEntrance, staggerIndex) {
        if (animateEntrance) {
            entranceScale.snapTo(0.84f)
            if (staggerIndex > 0) delay((staggerIndex.coerceAtMost(8) * 45L))
            entranceScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        } else {
            entranceScale.snapTo(1f)
        }
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        Modifier
    }
    val style = getBadgeVisualStyle(badge)
    val drawableRes = getBadgeDrawableRes(badge)

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, style.borderColor.copy(alpha = 0.85f)),
        modifier = modifier
            .graphicsLayer {
                val combined = pressScale * entranceScale.value
                scaleX = combined
                scaleY = combined
            }
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.horizontalGradient(style.gradientColors))
            .then(clickModifier)
            .testTag("badge_pill_${badge.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 7.dp else 10.dp,
                vertical = if (compact) 3.dp else 5.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                if (drawableRes != null) {
                    val iconBoxSize = if (compact) 16.dp else 20.dp
                    Box(
                        modifier = Modifier
                            .size(iconBoxSize)
                            .clip(if (badge == BadgeType.GREEN_VERIFIED) CircleShape else RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = drawableRes),
                            contentDescription = badge.title,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(1.dp)
                        )
                    }
                } else {
                    Text(
                        text = badge.iconEmoji,
                        fontSize = if (compact) 11.sp else 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(5.dp))
            }
            Text(
                text = badge.tagText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    fontSize = if (compact) 10.sp else 11.sp
                ),
                color = style.textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!compact) {
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.35f)
                ) {
                    Text(
                        text = badge.tierLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            letterSpacing = 0.4.sp
                        ),
                        color = style.borderColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveProfileBadgeTile(
    badge: BadgeType,
    staggerIndex: Int = 0,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    val style = getBadgeVisualStyle(badge)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val entranceScale = remember(badge) { Animatable(0.82f) }
    LaunchedEffect(badge, staggerIndex) {
        if (staggerIndex > 0) delay((staggerIndex.coerceAtMost(8) * 55L))
        entranceScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    val interactiveScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 1.11f
            isHighlighted -> 1.07f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "profile_badge_tile_scale"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isHighlighted) style.glowColor.copy(alpha = 0.22f) else DarkCard,
        border = BorderStroke(
            width = if (isHighlighted) 1.5.dp else 1.dp,
            color = if (isHighlighted) style.borderColor else style.borderColor.copy(alpha = 0.55f)
        ),
        modifier = Modifier
            .graphicsLayer {
                val s = entranceScale.value * interactiveScale
                scaleX = s
                scaleY = s
            }
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("profile_animated_badge_${badge.name.lowercase()}")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BadgeEmblemArt(
                badge = badge,
                size = 38.dp
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = badge.tagText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp
                ),
                color = style.borderColor,
                maxLines = 1
            )
            Text(
                text = badge.tierLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}

@Composable
fun BadgeShowcaseDialog(
    currentBadges: List<BadgeType>,
    onDismiss: () -> Unit,
    userProfile: UserProfile? = null,
    badgeAppeals: List<BadgeAppeal> = emptyList(),
    onSubmitBadgeAppeal: ((BadgeType, String) -> Pair<Boolean, String>)? = null,
    onSendAppealChat: ((String, String, String?) -> Pair<Boolean, String>)? = null
) {
    val allBadges = BadgeType.values().toList()
    var showAppealForm by remember { mutableStateOf(false) }
    var appealInitialBadge by remember { mutableStateOf(BadgeType.PRO_PLAYER) }
    val myAppeals = remember(badgeAppeals, userProfile) {
        if (userProfile == null) emptyList()
        else badgeAppeals.filter { appeal ->
            (userProfile.phone.isNotBlank() && appeal.userPhone == userProfile.phone) ||
                (userProfile.uid.isNotBlank() && appeal.userUid == userProfile.uid)
        }
    }
    val pendingMyAppeals = myAppeals.count { it.status == AppealStatus.PENDING }
    val spotlightBadges = listOf(
        BadgeType.V_BADGE to "ভি-ব্যাজ",
        BadgeType.PRO_PLAYER to "প্রো",
        BadgeType.PRIME_SPENDER to "স্পেন্ডার ৮",
        BadgeType.MODERATOR to "অ্যাডমিন",
        BadgeType.GREEN_VERIFIED to "ওনার"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(FlameYellow.copy(alpha = 0.35f), FlameYellow.copy(alpha = 0.08f))
                                    )
                                )
                                .border(1.dp, FlameYellow.copy(alpha = 0.6f), CircleShape),
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
                                text = "অফিসিয়াল ফ্রি ফায়ার ব্যাজ",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = FlameYellow
                            )
                            Text(
                                text = "মোট ${allBadges.size}টি প্রিমিয়াম ব্যাজ (${currentBadges.size}/${allBadges.size} অর্জিত)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 5 Custom Emblem Official Spotlight Strip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF1A1407),
                                        Color(0xFF111827),
                                        Color(0xFF0A1E15)
                                    )
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "অফিসিয়াল কাস্টম এমব্লেম গ্যালারি",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = FlameYellow,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            spotlightBadges.forEach { (badge, shortLabel) ->
                                val unlocked = currentBadges.contains(badge)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    BadgeEmblemArt(
                                        badge = badge,
                                        size = 46.dp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = shortLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.sp
                                        ),
                                        color = if (unlocked) Color(badge.hexColor) else Color.White.copy(alpha = 0.85f),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // User's Active Badges Bar
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, DarkBorder),
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
                                text = "এখনো কোনো বিশেষ ব্যাজ অর্জিত হয়নি। ম্যাচ খেলুন বা স্লট বুকিং করুন!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                currentBadges.forEach { badge ->
                                    BadgePill(badge = badge)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Appeal for Badge Action Bar (for players completing requirements)
                if (onSubmitBadgeAppeal != null && userProfile != null) {
                    val metAnyBadge = allBadges.firstOrNull {
                        it !in currentBadges && it.isRequirementMet(userProfile)
                    } ?: BadgeType.PRO_PLAYER
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = FlameCyan.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.65f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "শর্ত পূরণ করেছেন? ব্যাজের আবেদন করুন!",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                        color = FlameCyan
                                    )
                                    val statusText = when {
                                        userProfile.isBanned -> "🚫 ব্যান সক্রিয় (ওয়ার্নিং: ${userProfile.warningsCount})"
                                        userProfile.warningsCount > 0 -> "⚠️ ওয়ার্নিং: ${userProfile.warningsCount}/৩ (৩+ এ ৭ দিন ব্যান)"
                                        pendingMyAppeals > 0 -> "⏳ আপনার $pendingMyAppeals টি আবেদন যাচাই চলছে"
                                        else -> "মিথ্যা আবেদনে +১ ওয়ার্নিং (৩+ ওয়ার্নে ৭ দিন অ্যাপ ব্যান)"
                                    }
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (userProfile.isBanned || userProfile.warningsCount > 0) BkashPink else Color.White.copy(alpha = 0.78f),
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        appealInitialBadge = metAnyBadge
                                        showAppealForm = true
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = FlameCyan,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .testTag("appeal_for_badge_btn")
                                ) {
                                    Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "Appeal for Badge",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = "সকল ব্যাজ ও অর্জনের শর্তাবলী:",
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
                    items(allBadges, key = { it.name }) { badge ->
                        val isUnlocked = currentBadges.contains(badge)
                        val badgeColor = Color(badge.hexColor)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("showcase_badge_item_${badge.name.lowercase()}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnlocked) badgeColor.copy(alpha = 0.13f) else DarkCard
                            ),
                            border = BorderStroke(
                                width = if (isUnlocked) 1.5.dp else 1.dp,
                                color = if (isUnlocked) badgeColor else DarkBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BadgeEmblemArt(
                                    badge = badge,
                                    size = 54.dp
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = badge.title,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = badgeColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = badgeColor.copy(alpha = 0.18f),
                                            border = BorderStroke(0.8.dp, badgeColor.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = badge.tierLabel,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black
                                                ),
                                                color = badgeColor,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    BadgePill(badge = badge, compact = true)
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = badge.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🎯 শর্ত: ${badge.criteria}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isUnlocked) Color(0xFF10B981) else Color(0xFFFFC107),
                                        fontSize = 10.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isUnlocked) Color(0xFF10B981) else Color.White.copy(alpha = 0.08f),
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isUnlocked) Icons.Filled.Check else Icons.Filled.Lock,
                                                contentDescription = if (isUnlocked) "Unlocked" else "Locked",
                                                tint = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.45f),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                    if (!isUnlocked && onSubmitBadgeAppeal != null && userProfile != null) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = FlameCyan.copy(alpha = 0.16f),
                                            border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.65f)),
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    appealInitialBadge = badge
                                                    showAppealForm = true
                                                }
                                                .testTag("appeal_badge_item_${badge.name.lowercase()}")
                                        ) {
                                            Text(
                                                text = "আবেদন",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 9.sp
                                                ),
                                                color = FlameCyan,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
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

    if (showAppealForm && userProfile != null && onSubmitBadgeAppeal != null) {
        BadgeAppealRequestDialog(
            userProfile = userProfile,
            myAppeals = myAppeals,
            initialBadge = appealInitialBadge,
            onDismiss = { showAppealForm = false },
            onSubmitAppeal = onSubmitBadgeAppeal,
            onSendAppealChat = onSendAppealChat ?: { _, _, _ -> false to "" }
        )
    }
}
