package com.example.ui.screens

import com.example.R
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextOverflow
import com.example.model.AppCustomization
import com.example.model.AppUser
import com.example.model.BadgeAppeal
import com.example.model.BadgeType
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.BadgePill
import com.example.ui.components.BadgeShowcaseDialog
import com.example.ui.components.PlayerProfileCardData
import com.example.ui.components.PlayerProfileCardDialog
import com.example.ui.components.ForgotPasswordDialog
import com.example.ui.components.InteractiveProfileBadgeTile
import com.example.ui.components.getAvailableAvatars
import com.example.ui.components.getAvatarDrawableRes
import com.example.ui.components.getLogoDrawable
import com.example.ui.components.toPlayerProfileCard
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.NeonPink
import com.example.ui.theme.VictoryGreen

@Composable
fun AccountScreen(
    userProfile: UserProfile,
    walletBalance: Int,
    onEditProfileClick: () -> Unit,
    onLogin: (phoneOrEmail: String, password: String) -> Boolean,
    onRegister: (name: String, ign: String, uid: String, phone: String, bkash: String, avatarId: String, password: String) -> Unit,
    onLogout: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToMyMatches: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenSupportChat: () -> Unit,
    onOpenOwnerPanel: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    pendingRequestsCount: Int = 0,
    onRequestOtp: (phoneOrUid: String) -> Pair<Boolean, String> = { Pair(false, "") },
    onVerifyOtp: (phoneOrUid: String, enteredOtp: String) -> Boolean = { _, _ -> false },
    onResetPassword: (phoneOrUid: String, newPass: String) -> Boolean = { _, _ -> false },
    appCustomization: AppCustomization = AppCustomization(),
    registeredUsers: List<AppUser> = emptyList(),
    onOpenPlayerProfile: ((PlayerProfileCardData) -> Unit)? = null,
    onGiveKudos: ((String) -> Int)? = null,
    badgeAppeals: List<BadgeAppeal> = emptyList(),
    onSubmitBadgeAppeal: ((BadgeType, String) -> Pair<Boolean, String>)? = null,
    onSendAppealChat: ((String, String, String?) -> Pair<Boolean, String>)? = null,
    onGrantBadge: ((String, BadgeType) -> Pair<Boolean, String>)? = null,
    onRevokeBadge: ((String, BadgeType) -> Pair<Boolean, String>)? = null,
    modifier: Modifier = Modifier
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (!userProfile.isLoggedIn) {
        // Sign In / Log In View
        AuthView(
            onLogin = onLogin,
            onRegister = onRegister,
            onRequestOtp = onRequestOtp,
            onVerifyOtp = onVerifyOtp,
            onResetPassword = onResetPassword,
            modifier = modifier
        )
    } else {
        // Logged In Account Dashboard View
        LoggedInAccountView(
            userProfile = userProfile,
            walletBalance = walletBalance,
            onEditProfileClick = onEditProfileClick,
            onShowLogoutConfirmation = { showLogoutDialog = true },
            onNavigateToWallet = onNavigateToWallet,
            onNavigateToMyMatches = onNavigateToMyMatches,
            onOpenRules = onOpenRules,
            onOpenSupportChat = onOpenSupportChat,
            onOpenOwnerPanel = onOpenOwnerPanel,
            onOpenSettings = onOpenSettings,
            pendingRequestsCount = pendingRequestsCount,
            appCustomization = appCustomization,
            registeredUsers = registeredUsers,
            onOpenPlayerProfile = onOpenPlayerProfile,
            onGiveKudos = onGiveKudos,
            badgeAppeals = badgeAppeals,
            onSubmitBadgeAppeal = onSubmitBadgeAppeal,
            onSendAppealChat = onSendAppealChat,
            onGrantBadge = onGrantBadge,
            onRevokeBadge = onRevokeBadge,
            modifier = modifier
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "লগআউট নিশ্চিতকরণ",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিতভাবে আপনার একাউন্ট থেকে লগআউট করতে চান?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("হ্যাঁ, লগআউট করুন", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("বাতিল", color = Color.White)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun LoggedInAccountView(
    userProfile: UserProfile,
    walletBalance: Int,
    onEditProfileClick: () -> Unit,
    onShowLogoutConfirmation: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToMyMatches: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenSupportChat: () -> Unit,
    onOpenOwnerPanel: () -> Unit,
    onOpenSettings: () -> Unit = {},
    pendingRequestsCount: Int = 0,
    appCustomization: AppCustomization = AppCustomization(),
    registeredUsers: List<AppUser> = emptyList(),
    onOpenPlayerProfile: ((PlayerProfileCardData) -> Unit)? = null,
    onGiveKudos: ((String) -> Int)? = null,
    badgeAppeals: List<BadgeAppeal> = emptyList(),
    onSubmitBadgeAppeal: ((BadgeType, String) -> Pair<Boolean, String>)? = null,
    onSendAppealChat: ((String, String, String?) -> Pair<Boolean, String>)? = null,
    onGrantBadge: ((String, BadgeType) -> Pair<Boolean, String>)? = null,
    onRevokeBadge: ((String, BadgeType) -> Pair<Boolean, String>)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showBadgeDialog by remember { mutableStateOf(false) }
    var highlightedProfileBadge by remember { mutableStateOf<BadgeType?>(null) }
    var localPlayerProfileCard by remember { mutableStateOf<PlayerProfileCardData?>(null) }

    val openPlayerProfileCard: (PlayerProfileCardData) -> Unit = { data ->
        if (onOpenPlayerProfile != null) {
            onOpenPlayerProfile(data)
        } else {
            localPlayerProfileCard = data
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("account_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Owner Status Banner
        if (userProfile.role == com.example.model.UserRole.OWNER) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = FlameYellow.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, FlameYellow),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenOwnerPanel() }
                        .testTag("owner_banner_card")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(FlameYellow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👑", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "👑 সুপ্রিম ওনার (Supreme Owner)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                color = FlameYellow
                            )
                            Text(
                                text = "ট্যাপ করে ওনার প্যানেল খুলুন • ফ্রড ব্যান ও অ্যাডমিন",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = FlameYellow,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Banned Notice Banner (if user is banned) or Active Warnings Notice
        if (userProfile.isBanned) {
            item {
                val banExpiry = userProfile.banExpiresAtMillis
                val remainingText = if (banExpiry != null) {
                    val remainingMs = (banExpiry - System.currentTimeMillis()).coerceAtLeast(0L)
                    val remainingDays = ((remainingMs + 86_399_999L) / 86_400_000L).toInt().coerceAtLeast(1)
                    "⏳ ৭ দিনের অস্থায়ী অ্যাপ ব্যান — আনব্যান হতে বাকি: প্রায় $remainingDays দিন"
                } else {
                    null
                }
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BkashPink.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BkashPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_banned_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = BkashPink,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "অ্যাকাউন্ট ব্যান করা হয়েছে 🔴",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = BkashPink
                            )
                            Text(
                                text = "কারণ: ${userProfile.banReason ?: "প্রতারণা বা টুর্নামেন্ট নীতি লঙ্ঘন"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            if (remainingText != null) {
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = remainingText,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = FlameYellow
                                )
                            }
                        }
                    }
                }
            }
        } else if (userProfile.warningsCount > 0) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = FlameOrange.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, FlameOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_warnings_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Warnings",
                            tint = FlameYellow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "⚠️ অ্যাকাউন্ট সতর্কতা (ওয়ার্নিং): ${userProfile.warningsCount}/৩টি",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black),
                                color = FlameYellow
                            )
                            Text(
                                text = "৩টির বেশি ওয়ার্নিং পেলে ৭ দিনের জন্য অ্যাপ থেকে ব্যান করা হবে।",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // Profile Hero Header Card (Esports Banner + Overlapping Circular PFP)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("account_profile_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Top Profile Banner (Animated GIF for Owner/Admin, Picture/Color for Prime 8, Locked App Color for normal players)
                    com.example.ui.components.ProfileBannerBackground(
                        role = userProfile.role,
                        badges = userProfile.earnedBadges,
                        customBannerUri = userProfile.customBannerUri,
                        bannerColorId = userProfile.bannerColorId,
                        isBanned = userProfile.isBanned,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(106.dp)
                            .testTag("account_profile_banner")
                    ) {
                        val canUseGif = userProfile.role.canUseAnimatedGifProfileMedia()
                        val canCustomBanner = com.example.model.canCustomizeProfileBanner(userProfile.role, userProfile.earnedBadges)
                        val bannerLabel = when {
                            userProfile.customBannerUri != null && canUseGif && com.example.model.isAnimatedGifPathOrUri(userProfile.customBannerUri) -> "✨ GIF BANNER"
                            userProfile.customBannerUri != null && canCustomBanner -> "🖼️ PRIME BANNER"
                            canCustomBanner -> "🎨 CUSTOM BANNER"
                            else -> "🎨 APP COLOUR"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = bannerLabel,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (canCustomBanner) FlameYellow else Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onEditProfileClick() }
                                    .testTag("account_banner_edit_pill")
                            ) {
                                Text(
                                    text = if (canUseGif) "GIF / PFP এডিট ✎" else "PFP এডিট ✎",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameYellow,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 58.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar with edit photo button and rank badge
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurface)
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 3.dp,
                                        brush = Brush.linearGradient(
                                            listOf(FlameYellow, FlameOrange, BkashPink)
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable { onEditProfileClick() }
                            ) {
                                com.example.ui.components.ProfileAvatarImage(
                                    avatarId = userProfile.avatarId,
                                    customPfpUri = userProfile.customPfpUri,
                                    role = userProfile.role,
                                    contentDescription = "Profile Photo",
                                    showGifBadge = true,
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(CircleShape)
                                )
                            }

                            // Camera / Edit badge
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .border(1.5.dp, Color.White, CircleShape)
                                    .clickable { onEditProfileClick() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CameraAlt,
                                    contentDescription = "Change Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Account Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userProfile.accountName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = "Verified Player",
                            tint = VictoryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Role / Rank Pill (clean official identity for Owner/Admin; level+rank for Players)
                    when (userProfile.role) {
                        UserRole.OWNER -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = VictoryGreen.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.7f)),
                                modifier = Modifier.testTag("owner_clean_rank_pill")
                            ) {
                                Text(
                                    text = "👑 অফিসিয়াল অ্যাপ ওনার • ভেরিফাইড",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = FlameYellow,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                                )
                            }
                        }
                        UserRole.ADMIN -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FlameCyan.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.55f))
                            ) {
                                Text(
                                    text = "🛡️ অফিসিয়াল অ্যাডমিন • ভেরিফাইড",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = FlameCyan,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                                )
                            }
                        }
                        UserRole.PLAYER -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FlameOrange.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "লেভেল ${userProfile.level}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = FlameYellow
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "•",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = userProfile.rankBadge,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Active Player Badges Showcase Row with Subtle Scale-Up Animations
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        userProfile.earnedBadges.forEachIndexed { idx, badge ->
                            BadgePill(
                                badge = badge,
                                compact = true,
                                animateEntrance = true,
                                staggerIndex = idx,
                                onClick = {
                                    highlightedProfileBadge = if (highlightedProfileBadge == badge) null else badge
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = FlameYellow.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { showBadgeDialog = true }
                                .testTag("account_view_all_badges_chip")
                        ) {
                            Text(
                                text = "🏆 সব ব্যাজ",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = FlameYellow,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // Interactive Animated Badge Emblems Strip in User Profile Area
                    if (userProfile.earnedBadges.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkCard,
                            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_animated_badges_strip")
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✨ অর্জিত ব্যাজ (ট্যাপে প্রিভিউ)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = FlameYellow
                                    )
                                    Text(
                                        text = "${userProfile.earnedBadges.size}টি সক্রিয়",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VictoryGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    userProfile.earnedBadges.forEachIndexed { idx, badge ->
                                        InteractiveProfileBadgeTile(
                                            badge = badge,
                                            staggerIndex = idx,
                                            isHighlighted = highlightedProfileBadge == badge,
                                            onClick = {
                                                highlightedProfileBadge =
                                                    if (highlightedProfileBadge == badge) null else badge
                                            }
                                        )
                                    }
                                }
                                highlightedProfileBadge?.let { selected ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(selected.hexColor).copy(alpha = 0.14f),
                                        border = BorderStroke(1.dp, Color(selected.hexColor).copy(alpha = 0.45f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { showBadgeDialog = true }
                                    ) {
                                        Text(
                                            text = "🏅 ${selected.title} — ${selected.description}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bio, Join Date & Quick Player Profile Card Trigger
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                val ownKudos = registeredUsers.firstOrNull {
                                    it.phone == userProfile.phone || it.uid == userProfile.uid
                                }?.kudosCount ?: 0
                                openPlayerProfileCard(userProfile.toPlayerProfileCard(ownKudos))
                            }
                            .testTag("open_own_player_profile_btn")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "📝 বায়ো: ${userProfile.battleTagline}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "📅 জয়েন: ${userProfile.joinedDate} • 💸 মোট খরচ: ৳${userProfile.totalSpent}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FlameCyan
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FlameCyan.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "প্রোফাইল কার্ড",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FlameCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Free Fire IGN & UID Info Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = DarkCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Free Fire IGN
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.SportsEsports,
                                        contentDescription = null,
                                        tint = FlameYellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "গেম IGN:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userProfile.ign,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(userProfile.ign))
                                            Toast.makeText(context, "গেম IGN কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ContentCopy,
                                            contentDescription = "Copy IGN",
                                            tint = FlameYellow,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                color = DarkBorder,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // Free Fire UID
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Badge,
                                        contentDescription = null,
                                        tint = FlameOrange,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "প্লেয়ার UID:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userProfile.uid,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(userProfile.uid))
                                            Toast.makeText(context, "ফ্রি ফায়ার UID কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ContentCopy,
                                            contentDescription = "Copy UID",
                                            tint = FlameOrange,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                color = DarkBorder,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // bKash Number
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "৳",
                                        fontWeight = FontWeight.Black,
                                        color = BkashPink,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "বিকাশ নম্বর:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userProfile.bkashNumber,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = BkashPink
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(userProfile.bkashNumber))
                                            Toast.makeText(context, "বিকাশ নম্বর কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ContentCopy,
                                            contentDescription = "Copy bKash",
                                            tint = BkashPink,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Edit Profile Button
                    Button(
                        onClick = onEditProfileClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("edit_profile_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "একাউন্ট তথ্য ও ছবি এডিট করুন",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                }
            }
        }

        // Free Fire Pro Esports ID / Clean Official Owner Card
        item {
            val isOwnerAccount = userProfile.role == UserRole.OWNER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ff_esports_id_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.linearGradient(
                        if (isOwnerAccount) listOf(FlameYellow, VictoryGreen, FlameYellow)
                        else listOf(FlameYellow, FlameCyan, FlameOrange)
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header with Esports/Owner Tag & Guild
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isOwnerAccount) VictoryGreen.copy(alpha = 0.2f) else FlameYellow.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isOwnerAccount) VictoryGreen else FlameYellow)
                            ) {
                                Text(
                                    text = if (isOwnerAccount) "👑 OFFICIAL OWNER ID" else "⚡ FF ESPORTS ID",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isOwnerAccount) VictoryGreen else FlameYellow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = userProfile.guildName.ifBlank { "TRUSTED TH" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameCyan
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isOwnerAccount) FlameYellow.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isOwnerAccount) "অফিসিয়াল ওনার" else userProfile.playerRole,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOwnerAccount) FlameYellow else Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Player IGN & Active Primary Badge Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = userProfile.ign,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            ),
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        val primaryEarnedBadge = userProfile.earnedBadges.firstOrNull()
                        if (primaryEarnedBadge != null) {
                            BadgePill(
                                badge = primaryEarnedBadge,
                                compact = true,
                                animateEntrance = true,
                                onClick = { showBadgeDialog = true }
                            )
                        }
                    }

                    Text(
                        text = if (isOwnerAccount) {
                            "UID: ${userProfile.uid} • অফিসিয়াল ওনার 👑"
                        } else {
                            "UID: ${userProfile.uid} • ${userProfile.rankBadge}"
                        },
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.78f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isOwnerAccount) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurface,
                                border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.45f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("প্ল্যাটফর্ম", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("TRUSTED TOURNAMENT", fontSize = 11.sp, fontWeight = FontWeight.Black, color = FlameYellow)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = VictoryGreen.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.45f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("অ্যাকাউন্ট স্ট্যাটাস", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("ভেরিফাইড ওনার 🟢", fontSize = 11.sp, fontWeight = FontWeight.Black, color = VictoryGreen)
                                }
                            }
                        }
                    } else {
                        // Weapon & Character chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("ফেভারিট গান / স্কিন", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(userProfile.favoriteWeapon, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FlameYellow)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("অ্যাক্টিভ ক্যারেক্টার", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(userProfile.activeCharacter, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FlameCyan)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Pro Stats: Headshot Rate & KD Ratio
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = VictoryGreen.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.4f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("হেডশট রেট:", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                    Text("${userProfile.headshotRate}% 🎯", fontSize = 12.sp, fontWeight = FontWeight.Black, color = VictoryGreen)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = FlameOrange.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange.copy(alpha = 0.4f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("K/D রেশিও:", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                    Text("${userProfile.kdRatio} ⚔️", fontSize = 12.sp, fontWeight = FontWeight.Black, color = FlameOrange)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF240B1B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎡", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("লাকি স্পিন পরিসংখ্যান:", fontSize = 10.sp, color = FlameYellow, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = "মোট ${userProfile.totalSpinsDone} টি • জয় ৳${userProfile.totalSpinEarnings}",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\"${userProfile.battleTagline}\"",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.White.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Copy & Share Card Button
                    Button(
                        onClick = {
                            val cardText = if (isOwnerAccount) {
                                """
                                    👑 TRUSTED TOURNAMENT BY TH - OFFICIAL OWNER
                                    ━━━━━━━━━━━━━━━━━━━━
                                    👤 IGN: ${userProfile.ign}
                                    🆔 UID: ${userProfile.uid}
                                    🔰 প্ল্যাটফর্ম: ${userProfile.guildName}
                                    🛡️ স্ট্যাটাস: অফিসিয়াল ওনার (ভেরিফাইড)
                                    ━━━━━━━━━━━━━━━━━━━━
                                """.trimIndent()
                            } else {
                                """
                                    🎮 TRUSTED TOURNAMENT - FREE FIRE ESPORTS CARD
                                    ━━━━━━━━━━━━━━━━━━━━
                                    👤 IGN: ${userProfile.ign}
                                    🆔 UID: ${userProfile.uid}
                                    🔰 গিল্ড: ${userProfile.guildName}
                                    🎖️ রোল: ${userProfile.playerRole}
                                    🔫 ফেভারিট গান: ${userProfile.favoriteWeapon}
                                    ⚡ ক্যারেক্টার: ${userProfile.activeCharacter}
                                    🎯 হেডশট রেট: ${userProfile.headshotRate}%
                                    ⚔️ K/D রেশিও: ${userProfile.kdRatio}
                                    🏆 বুইয়া: ${userProfile.totalWins} | কিল: ${userProfile.totalKills}
                                    ━━━━━━━━━━━━━━━━━━━━
                                    📱 TRUSTED TOURNAMENT BY TH তে খেলুন ও নগদ টাকা জিতুন!
                                """.trimIndent()
                            }
                            clipboardManager.setText(AnnotatedString(cardText))
                            Toast.makeText(context, "আইডি কার্ড ক্লিপবোর্ডে কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("copy_player_card_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.6f))
                    ) {
                        Icon(imageVector = Icons.Filled.ContentCopy, contentDescription = null, tint = FlameYellow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOwnerAccount) "অফিসিয়াল ওনার কার্ড কপি করুন" else "প্লেয়ার আইডি কার্ড কপি ও শেয়ার করুন",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = FlameYellow
                        )
                    }
                }
            }
        }

        // Tournament Performance Stats Card (Shown for Players/Admins; kept clean for Owner)
        if (userProfile.role != UserRole.OWNER) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MilitaryTech,
                                contentDescription = null,
                                tint = FlameYellow,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "টুর্নামেন্ট ক্যারিয়ার পরিসংখ্যান",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatBox(
                                title = "মোট ম্যাচ",
                                value = "${userProfile.matchesPlayed}",
                                subtitle = "খেলা হয়েছে",
                                color = FlameOrange,
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "মোট বুইয়া",
                                value = "${userProfile.totalWins}",
                                subtitle = "চ্যাম্পিয়ন",
                                color = VictoryGreen,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatBox(
                                title = "মোট কিল",
                                value = "${userProfile.totalKills}",
                                subtitle = "শত্রু খতম",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            StatBox(
                                title = "মোট প্রাইজ আর্ন",
                                value = "৳${userProfile.totalEarnings}",
                                subtitle = "ক্যাশ প্রাইজ",
                                color = BkashPink,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Community Players Clickable Profiles Strip
        if (registeredUsers.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("account_community_players_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.45f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "👥 কমিউনিটি প্লেয়ার প্রোফাইল (গেমার কার্ড)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                                    color = FlameCyan
                                )
                                Text(
                                    text = "যেকোনো প্লেয়ারে ট্যাপ করে ম্যাচ, খরচ, জয়েন ও বায়ো দেখুন",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FlameCyan.copy(alpha = 0.14f)
                            ) {
                                Text(
                                    text = "${registeredUsers.size} জন",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(registeredUsers, key = { it.id }) { member ->
                                val isSelf = (userProfile.phone.isNotBlank() && member.phone == userProfile.phone) ||
                                    (userProfile.uid.isNotBlank() && member.uid == userProfile.uid)
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = DarkCard,
                                    border = BorderStroke(
                                        1.dp,
                                        if (member.role == com.example.model.UserRole.OWNER) FlameYellow.copy(alpha = 0.7f) else DarkBorder
                                    ),
                                    modifier = Modifier
                                        .width(136.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            openPlayerProfileCard(member.toPlayerProfileCard(isSelf = isSelf))
                                        }
                                        .testTag("community_player_chip_${member.uid}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(modifier = Modifier.size(46.dp)) {
                                            com.example.ui.components.ProfileAvatarImage(
                                                avatarId = member.avatarId,
                                                customPfpUri = member.customPfpUri,
                                                role = member.role,
                                                contentDescription = member.ign,
                                                showGifBadge = true,
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(CircleShape)
                                                    .border(
                                                        1.5.dp,
                                                        if (member.role == com.example.model.UserRole.OWNER) FlameYellow else FlameCyan,
                                                        CircleShape
                                                    )
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(DarkCard)
                                                    .padding(2.dp)
                                                    .clip(CircleShape)
                                                    .background(if (member.isBanned) Color(0xFFEF4444) else Color(0xFF23A55A))
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = member.ign,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${member.role.badge} ${member.matchesPlayed} ম্যাচ • ৳${member.totalSpent}",
                                            fontSize = 9.sp,
                                            color = FlameYellow,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Navigation Actions List (Wallet, Bookings, Rules, Support, Logo)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    AccountMenuRow(
                        icon = Icons.Filled.AccountBalanceWallet,
                        iconTint = BkashPink,
                        title = "বিকাশ ওয়ালেট ও লেনদেন হিস্ট্রি",
                        subtitle = "বর্তমান ব্যালেন্স: ৳$walletBalance",
                        onClick = onNavigateToWallet
                    )

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(horizontal = 16.dp))

                    AccountMenuRow(
                        icon = Icons.Filled.Verified,
                        iconTint = FlameYellow,
                        title = "💳 ডিপোজিট ও উইথড্র রিকোয়েস্ট (সেটিংস)",
                        subtitle = if (pendingRequestsCount > 0) "অপেক্ষমাণ রিকোয়েস্ট: $pendingRequestsCount টি • ম্যানেজ ও ট্র্যাক করুন" else "রিকোয়েস্ট পাঠান, স্ট্যাটাস দেখুন ও সেটিংস",
                        onClick = onOpenSettings,
                        rowTag = "account_deposit_withdraw_row"
                    )

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(horizontal = 16.dp))

                    AccountMenuRow(
                        icon = Icons.Filled.Stars,
                        iconTint = FlameYellow,
                        title = "আমার টুর্নামেন্ট স্লট রেকর্ড",
                        subtitle = "ক্রয়কৃত স্লট ও রুম আইডি দেখুন",
                        onClick = onNavigateToMyMatches
                    )

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(horizontal = 16.dp))

                    AccountMenuRow(
                        icon = Icons.Filled.Gavel,
                        iconTint = MaterialTheme.colorScheme.primary,
                        title = "টুর্নামেন্টের নিয়মাবলী ও রুলস",
                        subtitle = "হ্যাক, ইমুলেটর ও ফেয়ার-প্লে পলিসি",
                        onClick = onOpenRules
                    )

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(horizontal = 16.dp))

                    AccountMenuRow(
                        icon = Icons.Filled.SmartToy,
                        iconTint = FlameCyan,
                        title = "🤖 Talk with AI (এআই সহকারী)",
                        subtitle = "২৪/৭ যেকোনো সমস্যা ও পেমেন্ট সমাধান",
                        onClick = onOpenSupportChat
                    )

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(horizontal = 16.dp))

                    AccountMenuRow(
                        icon = Icons.Filled.HeadsetMic,
                        iconTint = VictoryGreen,
                        title = "লাইভ অ্যাডমিন সাপোর্ট চ্যাট",
                        subtitle = "বিকাশ সমস্যা বা যেকোনো সাহায্য",
                        onClick = onOpenSupportChat
                    )

                    if (userProfile.role == com.example.model.UserRole.OWNER) {
                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(horizontal = 16.dp))

                        AccountMenuRow(
                            icon = Icons.Filled.Shield,
                            iconTint = FlameYellow,
                            title = "👑 ওনার কন্ট্রোল প্যানেল (Owner Only)",
                            subtitle = "ব্যালেন্স, ডিপোজিট/উইথড্র, ফ্রড ব্যান ও অ্যাডমিন নিয়োগ",
                            onClick = onOpenOwnerPanel
                        )
                    }

                    HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(horizontal = 16.dp))

                    AccountMenuRow(
                        icon = Icons.Filled.MilitaryTech,
                        iconTint = FlameYellow,
                        title = "🏆 অফিসিয়াল ব্যাজ সিস্টেম ও গাইড",
                        subtitle = "ভি-ব্যাজ, গ্রিন ওনার, মডারেটর ও প্রো ব্যাজ",
                        onClick = { showBadgeDialog = true }
                    )
                }
            }
        }

        // Official "TH" Logo Branding Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.45f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(2.dp, NeonPink, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = getLogoDrawable(appCustomization.selectedLogo)),
                            contentDescription = "TH Logo",
                            modifier = Modifier.size(56.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = appCustomization.appName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = FlameYellow
                        )
                        Text(
                            text = appCustomization.appTagline,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "অফিসিয়াল বিকাশ: ${appCustomization.officialBkashNumber}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BkashPink
                        )
                    }
                }
            }
        }

        // Log Out Button
        item {
            OutlinedButton(
                onClick = onShowLogoutConfirmation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("logout_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "একাউন্ট থেকে লগআউট করুন (Log Out)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showBadgeDialog) {
        BadgeShowcaseDialog(
            currentBadges = userProfile.earnedBadges,
            onDismiss = { showBadgeDialog = false },
            userProfile = userProfile,
            badgeAppeals = badgeAppeals,
            onSubmitBadgeAppeal = onSubmitBadgeAppeal,
            onSendAppealChat = onSendAppealChat
        )
    }

    localPlayerProfileCard?.let { profileData ->
        PlayerProfileCardDialog(
            profile = profileData,
            onDismiss = { localPlayerProfileCard = null },
            onEditOwnProfile = if (profileData.isSelf) {
                {
                    localPlayerProfileCard = null
                    onEditProfileClick()
                }
            } else null,
            onGiveKudos = onGiveKudos,
            onViewAllBadges = {
                localPlayerProfileCard = null
                showBadgeDialog = true
            },
            viewerRole = userProfile.role,
            onGrantBadge = onGrantBadge,
            onRevokeBadge = onRevokeBadge
        )
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = DarkCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                color = color
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccountMenuRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    rowTag: String? = null
) {
    val baseModifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(horizontal = 16.dp, vertical = 14.dp)
    Row(
        modifier = if (rowTag != null) baseModifier.testTag(rowTag) else baseModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun AuthView(
    onLogin: (phoneOrEmail: String, password: String) -> Boolean,
    onRegister: (name: String, ign: String, uid: String, phone: String, bkash: String, avatarId: String, password: String) -> Unit,
    onRequestOtp: (phoneOrUid: String) -> Pair<Boolean, String>,
    onVerifyOtp: (phoneOrUid: String, enteredOtp: String) -> Boolean,
    onResetPassword: (phoneOrUid: String, newPass: String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Sign Up

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("auth_screen"),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Branding Header
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(3.dp, FlameYellow, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.th_logo),
                        contentDescription = "TH Logo",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "TRUSTED TOURNAMENT BY TH",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    ),
                    color = FlameYellow
                )
                Text(
                    text = "বাংলাদেশের ১ নম্বর বিশ্বস্ত ফ্রি ফায়ার টুর্নামেন্ট",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tab Row: Login / Sign Up
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                TabRow(
                    selectedTabIndex = selectedAuthTab,
                    containerColor = DarkSurface,
                    contentColor = FlameYellow,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedAuthTab]),
                            color = FlameYellow,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedAuthTab == 0,
                        onClick = { selectedAuthTab = 0 },
                        text = {
                            Text(
                                text = "লগইন (Log In)",
                                fontWeight = if (selectedAuthTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedAuthTab == 0) FlameYellow else Color.White
                            )
                        },
                        modifier = Modifier.testTag("auth_tab_login")
                    )
                    Tab(
                        selected = selectedAuthTab == 1,
                        onClick = { selectedAuthTab = 1 },
                        text = {
                            Text(
                                text = "নতুন একাউন্ট (Sign Up)",
                                fontWeight = if (selectedAuthTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedAuthTab == 1) FlameYellow else Color.White
                            )
                        },
                        modifier = Modifier.testTag("auth_tab_signup")
                    )
                }
            }
        }

        if (selectedAuthTab == 0) {
            item {
                LoginFormCard(
                    onLogin = onLogin,
                    onRequestOtp = onRequestOtp,
                    onVerifyOtp = onVerifyOtp,
                    onResetPassword = onResetPassword
                )
            }
        } else {
            item {
                SignUpFormCard(onRegister = onRegister)
            }
        }
    }
}

@Composable
private fun LoginFormCard(
    onLogin: (phoneOrEmail: String, password: String) -> Boolean,
    onRequestOtp: (phoneOrUid: String) -> Pair<Boolean, String>,
    onVerifyOtp: (phoneOrUid: String, enteredOtp: String) -> Boolean,
    onResetPassword: (phoneOrUid: String, newPass: String) -> Boolean
) {
    var phoneOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "আপনার একাউন্টে প্রবেশ করুন",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "আপনার মোবাইল নম্বর বা ইমেইল দিয়ে লগইন করুন",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Phone or Email Field
            OutlinedTextField(
                value = phoneOrEmail,
                onValueChange = {
                    phoneOrEmail = it
                    errorMessage = null
                },
                label = { Text("মোবাইল নম্বর অথবা ইমেইল") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Phone,
                        contentDescription = null,
                        tint = FlameYellow
                    )
                },
                modifier = Modifier.fillMaxWidth().testTag("login_phone_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = FlameYellow,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("পাসওয়ার্ড (Password)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = FlameYellow
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().testTag("login_password_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = FlameYellow,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { showForgotPasswordDialog = true },
                    modifier = Modifier.testTag("account_forgot_password_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.LockReset,
                        contentDescription = null,
                        tint = FlameYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "পাসওয়ার্ড ভুলে গেছেন? (Forgot Password)",
                        color = FlameYellow,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Login Button
            Button(
                onClick = {
                    if (phoneOrEmail.isBlank()) {
                        errorMessage = "মোবাইল নম্বর বা ইমেইল লিখুন"
                        return@Button
                    }
                    if (password.isBlank()) {
                        errorMessage = "আপনার পাসওয়ার্ড লিখুন"
                        return@Button
                    }
                    val ok = onLogin(phoneOrEmail, password)
                    if (!ok) {
                        errorMessage = "ভুল নম্বর/UID অথবা পাসওয়ার্ড! পাসওয়ার্ড ভুলে গেলে Forgot Password এ চাপুন।"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_login_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "লগইন করুন (Log In)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            initialPhoneOrUid = phoneOrEmail,
            onDismiss = { showForgotPasswordDialog = false },
            onRequestOtp = onRequestOtp,
            onVerifyOtp = onVerifyOtp,
            onResetPassword = onResetPassword,
            onPasswordResetSuccess = { updatedPhone, updatedPass ->
                phoneOrEmail = updatedPhone
                password = updatedPass
                errorMessage = null
            }
        )
    }
}

@Composable
private fun SignUpFormCard(
    onRegister: (name: String, ign: String, uid: String, phone: String, bkash: String, avatarId: String, password: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ign by remember { mutableStateOf("") }
    var uid by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bkash by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableStateOf("th_logo") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val avatars = remember { getAvailableAvatars() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "নতুন একাউন্ট নিবন্ধন করুন",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "ফ্রি ফায়ার টুর্নামেন্টে যোগ দিন • অ্যাকাউন্ট খুললেই ফ্রি ৳৫ বোনাস!",
                style = MaterialTheme.typography.bodySmall,
                color = VictoryGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Avatar Selection
            Text(
                text = "প্রোফাইল ছবি নির্বাচন:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(avatars) { item ->
                    val isSelected = item.id == selectedAvatarId
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) FlameYellow else DarkBorder,
                                shape = CircleShape
                            )
                            .clickable { selectedAvatarId = item.id }
                    ) {
                        Image(
                            painter = painterResource(id = item.drawableRes),
                            contentDescription = item.title,
                            modifier = Modifier.size(54.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Full Name
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorMessage = null
                },
                label = { Text("আপনার নাম (Full Name)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = null, tint = FlameYellow)
                },
                modifier = Modifier.fillMaxWidth().testTag("signup_name_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Free Fire IGN
            OutlinedTextField(
                value = ign,
                onValueChange = {
                    ign = it
                    errorMessage = null
                },
                label = { Text("ফ্রি ফায়ার গেমের নাম (IGN)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.SportsEsports, contentDescription = null, tint = FlameYellow)
                },
                modifier = Modifier.fillMaxWidth().testTag("signup_ign_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Free Fire UID
            OutlinedTextField(
                value = uid,
                onValueChange = {
                    uid = it
                    errorMessage = null
                },
                label = { Text("ফ্রি ফায়ার প্লেয়ার UID") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Badge, contentDescription = null, tint = FlameYellow)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().testTag("signup_uid_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    if (bkash.isEmpty()) bkash = it
                    errorMessage = null
                },
                label = { Text("মোবাইল নম্বর (Phone)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = null, tint = FlameYellow)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().testTag("signup_phone_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // bKash Number
            OutlinedTextField(
                value = bkash,
                onValueChange = {
                    bkash = it
                    errorMessage = null
                },
                label = { Text("বিকাশ নম্বর (টাকা তোলার জন্য)") },
                leadingIcon = {
                    Text(
                        text = "৳",
                        color = BkashPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().testTag("signup_bkash_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BkashPink,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = null
                },
                label = { Text("পাসওয়ার্ড (কমপক্ষে ৪ সংখ্যা)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = FlameYellow)
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().testTag("signup_password_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameYellow,
                    unfocusedBorderColor = DarkBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "আপনার নাম লিখুন"
                        return@Button
                    }
                    if (ign.isBlank()) {
                        errorMessage = "ফ্রি ফায়ার গেমের নাম (IGN) দিন"
                        return@Button
                    }
                    if (uid.isBlank()) {
                        errorMessage = "ফ্রি ফায়ার প্লেয়ার UID দিন"
                        return@Button
                    }
                    if (phone.isBlank()) {
                        errorMessage = "মোবাইল নম্বর দিন"
                        return@Button
                    }
                    if (password.trim().length < 4) {
                        errorMessage = "কমপক্ষে ৪ অক্ষরের পাসওয়ার্ড দিন"
                        return@Button
                    }
                    onRegister(
                        name.trim(),
                        ign.trim(),
                        uid.trim(),
                        phone.trim(),
                        if (bkash.isBlank()) phone.trim() else bkash.trim(),
                        selectedAvatarId,
                        password.trim()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_signup_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "সাইন আপ করুন (৳৫ ফ্রি বোনাস)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}
