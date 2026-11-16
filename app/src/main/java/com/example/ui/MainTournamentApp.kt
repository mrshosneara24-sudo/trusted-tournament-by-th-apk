package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.RequestStatus
import com.example.model.UserRole
import com.example.ui.components.AddMoneyDialog
import com.example.ui.components.AdminControlDialog
import com.example.ui.components.DailyLuckySpinDialog
import com.example.ui.components.DailyStreakDialog
import com.example.ui.components.PlayerProfileCardData
import com.example.ui.components.PlayerProfileCardDialog
import com.example.ui.components.EditAccountDialog
import com.example.ui.components.MasterAppEditorDialog
import com.example.ui.components.OwnerControlDialog
import com.example.ui.components.RoomDetailsDialog
import com.example.ui.components.RoomSlotGridDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SlotBookingDialog
import com.example.ui.components.SupportChatDialog
import com.example.ui.components.WithdrawDialog
import com.example.ui.components.getLogoDrawable
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AuthOnboardingScreen
import com.example.ui.screens.GlobalChatScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.MyMatchesScreen
import com.example.ui.screens.RulesAndSupportScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PinkBlackButtonGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTournamentApp(
    viewModel: TournamentViewModel = viewModel(
        factory = TournamentViewModel.factory(LocalContext.current.applicationContext)
    )
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val matches by viewModel.filteredMatches.collectAsStateWithLifecycle()
    val myBookings by viewModel.myBookings.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val walletBalance by viewModel.walletBalance.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val depositRequests by viewModel.depositRequests.collectAsStateWithLifecycle()
    val withdrawRequests by viewModel.withdrawRequests.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val globalChatMessages by viewModel.globalChatMessages.collectAsStateWithLifecycle()
    val hasSpunToday by viewModel.hasSpunToday.collectAsStateWithLifecycle()
    val preselectedSlot by viewModel.preselectedSlotNumber.collectAsStateWithLifecycle()

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val officialWhatsAppUrl by viewModel.officialWhatsAppGroupUrl.collectAsStateWithLifecycle()
    val allMatches by viewModel.allMatches.collectAsStateWithLifecycle()
    val registeredUsers by viewModel.registeredUsers.collectAsStateWithLifecycle()
    val appCustomization by viewModel.appCustomization.collectAsStateWithLifecycle()
    val strictRules by viewModel.strictRules.collectAsStateWithLifecycle()
    var showMasterAppEditor by remember { mutableStateOf(false) }
    var selectedPlayerProfileCard by remember { mutableStateOf<PlayerProfileCardData?>(null) }

    val bookingMatch by viewModel.bookingMatch.collectAsStateWithLifecycle()
    val roomSlotGridMatch by viewModel.roomSlotGridMatch.collectAsStateWithLifecycle()
    val roomDetails by viewModel.roomDetails.collectAsStateWithLifecycle()
    val showAddMoney by viewModel.showAddMoneyDialog.collectAsStateWithLifecycle()
    val showWithdraw by viewModel.showWithdrawDialog.collectAsStateWithLifecycle()
    val showLuckySpin by viewModel.showLuckySpinDialog.collectAsStateWithLifecycle()
    val spinHistory by viewModel.spinHistory.collectAsStateWithLifecycle()
    val dailyMissions by viewModel.dailyMissions.collectAsStateWithLifecycle()
    val dailyStreakInfo by viewModel.dailyStreakInfo.collectAsStateWithLifecycle()
    val showDailyStreak by viewModel.showDailyStreakDialog.collectAsStateWithLifecycle()
    val showSupportChat by viewModel.showSupportChatDialog.collectAsStateWithLifecycle()
    val aiChatMessages by viewModel.aiChatMessages.collectAsStateWithLifecycle()
    val supportDialogTab by viewModel.supportDialogTab.collectAsStateWithLifecycle()
    val showEditProfile by viewModel.showEditProfileDialog.collectAsStateWithLifecycle()
    val showAdminDialog by viewModel.showAdminDialog.collectAsStateWithLifecycle()
    val showOwnerDialog by viewModel.showOwnerDialog.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showRulesDialog by viewModel.showRulesDialog.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val badgeAppeals by viewModel.badgeAppeals.collectAsStateWithLifecycle()

    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Esports Onboarding & Captcha Screen for new downloads / logged-out users
    if (!isLoggedIn) {
        AuthOnboardingScreen(
            onRegister = { name, ign, uid, phone, bkash, pass ->
                viewModel.register(name, ign, uid, phone, bkash, "avatar_1", pass)
            },
            onLogin = { phoneOrEmail, pass ->
                viewModel.login(phoneOrEmail, pass)
            },
            onRequestOtp = { phoneOrUid ->
                viewModel.requestPasswordResetOtp(phoneOrUid)
            },
            onVerifyOtp = { phoneOrUid, otp ->
                viewModel.verifyPasswordResetOtp(phoneOrUid, otp)
            },
            onResetPassword = { phoneOrUid, newPass ->
                viewModel.resetPassword(phoneOrUid, newPass)
            },
            appCustomization = appCustomization
        )
        return
    }

    // Handle back button on sub-tabs to go back to Matches
    BackHandler(enabled = currentTab != TournamentTab.MATCHES) {
        viewModel.selectTab(TournamentTab.MATCHES)
    }

    val pendingRequestsCount = remember(depositRequests, withdrawRequests, userProfile) {
        val isStaff = userProfile.role == UserRole.OWNER || userProfile.role == UserRole.ADMIN
        val d = if (isStaff) depositRequests else depositRequests.filter { it.userPhone == userProfile.phone }
        val w = if (isStaff) withdrawRequests else withdrawRequests.filter { it.userPhone == userProfile.phone }
        d.count { it.status == RequestStatus.PENDING } + w.count { it.status == RequestStatus.PENDING }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = {
            if (currentTab != TournamentTab.GLOBAL_CHAT) {
                SnackbarHost(snackbarHostState)
            }
        },
        topBar = {
            Surface(
                color = DarkSurface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF170D1D), DarkSurface)
                            )
                        )
                        .statusBarsPadding()
                ) {
                    // Tier 1: Full App Identity + Wallet Balance Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectTab(TournamentTab.MATCHES) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.5.dp,
                                        Brush.linearGradient(listOf(FlameYellow, NeonPink)),
                                        CircleShape
                                    )
                                    .padding(2.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = getLogoDrawable(appCustomization.selectedLogo)),
                                    contentDescription = "App Logo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = appCustomization.appName,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.4.sp
                                    ),
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "অফিসিয়াল ফ্রি ফায়ার টুর্নামেন্ট",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = FlameYellow.copy(alpha = 0.9f),
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Sleek Gradient bKash Wallet Pill
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(PinkBlackButtonGradient)
                                .clickable { viewModel.selectTab(TournamentTab.WALLET) }
                                .testTag("appbar_wallet_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccountBalanceWallet,
                                    contentDescription = "Wallet",
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "৳$walletBalance",
                                    fontFamily = MonoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Tier 2: Clean Glass Quick Action Strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(start = 14.dp, end = 14.dp, bottom = 9.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Daily Streak Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkCard,
                            border = BorderStroke(1.dp, if (!dailyStreakInfo.isTodayClaimed) NeonPink.copy(alpha = 0.65f) else DarkBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.openDailyStreak() }
                                .testTag("appbar_daily_streak_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = "Daily Streak",
                                    tint = NeonPink,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("স্ট্রিক", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                if (!dailyStreakInfo.isTodayClaimed) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(FlameYellow)
                                    )
                                }
                            }
                        }

                        // Talk with AI Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkCard,
                            border = BorderStroke(1.dp, FlameCyan.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.openAiAssistant() }
                                .testTag("appbar_ai_chat_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SmartToy,
                                    contentDescription = "Talk with AI",
                                    tint = FlameCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("AI হেল্প", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            }
                        }

                        // Settings Pill (with pending request indicator)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkCard,
                            border = BorderStroke(1.dp, if (pendingRequestsCount > 0) FlameYellow.copy(alpha = 0.65f) else DarkBorder),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.openSettings() }
                                .testTag("appbar_settings_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Settings",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text("সেটিংস", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                if (pendingRequestsCount > 0) {
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Surface(shape = CircleShape, color = FlameYellow) {
                                        Text(
                                            text = "$pendingRequestsCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Admin Control Pill (Owner & Admin Only)
                        if (userProfile.role == UserRole.OWNER || userProfile.role == UserRole.ADMIN) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkElevated,
                                border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.55f)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.openAdminPanel() }
                                    .testTag("appbar_admin_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AdminPanelSettings,
                                        contentDescription = "Admin Control",
                                        tint = FlameYellow,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text("অ্যাডমিন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FlameYellow)
                                }
                            }
                        }

                        // Owner A-to-Z Editor Pill (Owner Only)
                        if (userProfile.role == UserRole.OWNER) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF2B1124),
                                border = BorderStroke(1.dp, FlameYellow),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { showMasterAppEditor = true }
                                    .testTag("appbar_owner_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("👑", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ওনার", fontSize = 11.sp, fontWeight = FontWeight.Black, color = FlameYellow)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                }
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                // Matches Tab
                NavigationBarItem(
                    selected = currentTab == TournamentTab.MATCHES,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.selectTab(TournamentTab.MATCHES)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.SportsEsports,
                            contentDescription = "Tournament Matches"
                        )
                    },
                    label = { Text(TournamentTab.MATCHES.banglaLabel, fontSize = 10.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_matches")
                )

                // My Matches Tab
                NavigationBarItem(
                    selected = currentTab == TournamentTab.MY_MATCHES,
                    onClick = { viewModel.selectTab(TournamentTab.MY_MATCHES) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Stars,
                            contentDescription = "My Matches"
                        )
                    },
                    label = { Text(TournamentTab.MY_MATCHES.banglaLabel, fontSize = 10.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_my_matches")
                )

                // Wallet Tab
                NavigationBarItem(
                    selected = currentTab == TournamentTab.WALLET,
                    onClick = { viewModel.selectTab(TournamentTab.WALLET) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.AccountBalanceWallet,
                            contentDescription = "bKash Wallet"
                        )
                    },
                    label = { Text(TournamentTab.WALLET.banglaLabel, fontSize = 10.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = BkashPink,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_wallet")
                )

                // Leaderboard Tab
                NavigationBarItem(
                    selected = currentTab == TournamentTab.LEADERBOARD,
                    onClick = { viewModel.selectTab(TournamentTab.LEADERBOARD) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = "Leaderboard"
                        )
                    },
                    label = { Text(TournamentTab.LEADERBOARD.banglaLabel, fontSize = 10.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_leaderboard")
                )

                // Global Chat Tab (Alongside Leaderboard)
                NavigationBarItem(
                    selected = currentTab == TournamentTab.GLOBAL_CHAT,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.selectTab(TournamentTab.GLOBAL_CHAT)
                    },
                    icon = {
                        Box {
                            Icon(
                                imageVector = Icons.Filled.Forum,
                                contentDescription = "Global Chat"
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(VictoryGreen)
                            )
                        }
                    },
                    label = { Text(TournamentTab.GLOBAL_CHAT.banglaLabel, fontSize = 10.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = BkashPink,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_global_chat")
                )

                // Account Tab
                NavigationBarItem(
                    selected = currentTab == TournamentTab.ACCOUNT,
                    onClick = { viewModel.selectTab(TournamentTab.ACCOUNT) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "User Account"
                        )
                    },
                    label = { Text(TournamentTab.ACCOUNT.banglaLabel, fontSize = 10.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_account")
                )
            }
        }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (appCustomization.isMaintenanceMode) {
                Surface(
                    color = Color(0xFF3A0919),
                    border = BorderStroke(1.dp, NeonPink),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("maintenance_banner"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Maintenance Notice",
                            tint = NeonPink,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = appCustomization.maintenanceMessage,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220)) +
                            slideInVertically(animationSpec = tween(220)) { it / 28 })
                            .togetherWith(fadeOut(animationSpec = tween(160)))
                    },
                    label = "tab_content_transition"
                ) { activeTab ->
                    when (activeTab) {
                        TournamentTab.MATCHES -> {
                            MatchesScreen(
                                matches = matches,
                                myBookings = myBookings,
                                selectedCategory = selectedCategory,
                                searchQuery = searchQuery,
                                walletBalance = walletBalance,
                                onCategorySelected = { viewModel.selectCategory(it) },
                                onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                                onWalletClick = { viewModel.selectTab(TournamentTab.WALLET) },
                                onBookSlotClick = { viewModel.openSlotBooking(it) },
                                onViewRoomClick = { viewModel.openRoomDetails(it) },
                                onViewSlotGridClick = { viewModel.openSlotGrid(it) },
                                onDailySpinClick = { viewModel.openLuckySpin() },
                                onSupportChatClick = { viewModel.openSupportChat() },
                                onDailyStreakClick = { viewModel.openDailyStreak() },
                                dailyMissions = dailyMissions,
                                onClaimMission = { viewModel.claimMission(it) },
                                appCustomization = appCustomization
                            )
                        }

                        TournamentTab.MY_MATCHES -> {
                            MyMatchesScreen(
                                bookings = myBookings,
                                onViewRoomDetails = { viewModel.openRoomDetailsForBooking(it) },
                                onExploreMatchesClick = { viewModel.selectTab(TournamentTab.MATCHES) },
                                onViewTransactionHistoryClick = { viewModel.selectTab(TournamentTab.WALLET) }
                            )
                        }

                        TournamentTab.WALLET -> {
                            WalletScreen(
                                balance = walletBalance,
                                transactions = transactions,
                                onAddMoneyClick = { viewModel.openAddMoney() },
                                onWithdrawClick = { viewModel.openWithdraw() },
                                matchesPlayed = userProfile.matchesPlayed,
                                depositRequests = depositRequests,
                                withdrawRequests = withdrawRequests,
                                currentUserPhone = userProfile.phone
                            )
                        }

                        TournamentTab.LEADERBOARD -> {
                            LeaderboardScreen(
                                players = leaderboard,
                                currentBadges = userProfile.earnedBadges,
                                registeredUsers = registeredUsers,
                                currentUser = userProfile,
                                onPlayerProfileClick = { selectedPlayerProfileCard = it },
                                onGiveKudos = { viewModel.giveProfileKudos(it) },
                                onOpenGlobalChat = { viewModel.selectTab(TournamentTab.GLOBAL_CHAT) },
                                badgeAppeals = badgeAppeals,
                                onSubmitBadgeAppeal = { badge, note -> viewModel.submitBadgeAppeal(badge, note) },
                                onSendAppealChat = { appealId, text, uri -> viewModel.sendAppealChatMessage(appealId, text, uri) }
                            )
                        }

                        TournamentTab.GLOBAL_CHAT -> {
                            GlobalChatScreen(
                                messages = globalChatMessages,
                                currentUser = userProfile,
                                registeredUsers = registeredUsers,
                                onSendMessage = { text, replyToId, pin ->
                                    viewModel.sendGlobalChatMessage(text, replyToId, pin)
                                },
                                onTogglePin = { msgId -> viewModel.togglePinGlobalChatMessage(msgId) },
                                onDeleteMessage = { msgId -> viewModel.deleteGlobalChatMessage(msgId) },
                                onReactMessage = { msgId, emoji -> viewModel.reactToGlobalChatMessage(msgId, emoji) },
                                onPlayerProfileClick = { selectedPlayerProfileCard = it }
                            )
                        }

                        TournamentTab.ACCOUNT -> {
                            AccountScreen(
                                userProfile = userProfile,
                                walletBalance = walletBalance,
                                onEditProfileClick = { viewModel.openEditProfile() },
                                onLogin = { phoneOrEmail, password ->
                                    viewModel.login(phoneOrEmail, password)
                                },
                                onRegister = { name, ign, uid, phone, bkash, avatarId, pass ->
                                    viewModel.register(name, ign, uid, phone, bkash, avatarId, pass)
                                },
                                onRequestOtp = { phoneOrUid ->
                                    viewModel.requestPasswordResetOtp(phoneOrUid)
                                },
                                onVerifyOtp = { phoneOrUid, otp ->
                                    viewModel.verifyPasswordResetOtp(phoneOrUid, otp)
                                },
                                onResetPassword = { phoneOrUid, newPass ->
                                    viewModel.resetPassword(phoneOrUid, newPass)
                                },
                                onLogout = { viewModel.logout() },
                                onNavigateToWallet = { viewModel.selectTab(TournamentTab.WALLET) },
                                onNavigateToMyMatches = { viewModel.selectTab(TournamentTab.MY_MATCHES) },
                                onOpenRules = { viewModel.openRules() },
                                onOpenSupportChat = { viewModel.openSupportChat() },
                                onOpenOwnerPanel = { viewModel.openOwnerPanel() },
                                onOpenSettings = { viewModel.openSettings() },
                                pendingRequestsCount = pendingRequestsCount,
                                appCustomization = appCustomization,
                                registeredUsers = registeredUsers,
                                onOpenPlayerProfile = { selectedPlayerProfileCard = it },
                                onGiveKudos = { viewModel.giveProfileKudos(it) },
                                badgeAppeals = badgeAppeals,
                                onSubmitBadgeAppeal = { badge, note -> viewModel.submitBadgeAppeal(badge, note) },
                                onSendAppealChat = { appealId, text, uri -> viewModel.sendAppealChatMessage(appealId, text, uri) }
                            )
                        }
                    }
                }

                if (currentTab == TournamentTab.GLOBAL_CHAT) {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }

    // Slot Booking Dialog
    bookingMatch?.let { match ->
        val takenSlots = myBookings.filter { it.matchId == match.id }.map { it.slotNumber }.toSet()
        SlotBookingDialog(
            match = match,
            officialBkashNumber = appCustomization.officialBkashNumber,
            walletBalance = walletBalance,
            initialSlotNumber = preselectedSlot,
            defaultIgn = userProfile.ign,
            defaultUid = userProfile.uid,
            defaultBkash = userProfile.bkashNumber,
            takenSlots = takenSlots,
            onDismiss = { viewModel.closeSlotBooking() },
            onConfirmBooking = { slot, ign, uid, senderBkash, trxId ->
                viewModel.confirmSlotBooking(slot, ign, uid, senderBkash, trxId)
            },
            onConfirmWalletBooking = { slot, ign, uid ->
                viewModel.confirmSlotBookingWithWallet(slot, ign, uid)
            }
        )
    }

    // Room Slot Grid Dialog
    roomSlotGridMatch?.let { match ->
        RoomSlotGridDialog(
            match = match,
            isOwner = (userProfile.role == com.example.model.UserRole.OWNER),
            bookings = myBookings,
            onDismiss = { viewModel.closeSlotGrid() },
            onSelectSlotToBook = { chosenSlot ->
                viewModel.closeSlotGrid()
                viewModel.openSlotBooking(match, chosenSlot)
            },
            onCancelSlotBookingAndRefund = { bookingId ->
                viewModel.cancelSlotBookingAndRefund(bookingId)
            },
            onCancelLegacySlot = { slotNumber ->
                viewModel.cancelLegacySlotByNumber(match.id, slotNumber)
            },
            registeredUsers = registeredUsers,
            currentUser = userProfile,
            onPlayerProfileClick = { selectedPlayerProfileCard = it },
            onGiveKudos = { viewModel.giveProfileKudos(it) }
        )
    }

    // Room Details Dialog
    roomDetails?.let { data ->
        RoomDetailsDialog(
            matchTitle = data.title,
            slotNumber = data.slotNumber,
            roomId = data.roomId,
            roomPassword = data.roomPassword,
            whatsAppUrl = officialWhatsAppUrl,
            onDismiss = { viewModel.closeRoomDetails() }
        )
    }

    // Add Money / Deposit Dialog
    if (showAddMoney) {
        AddMoneyDialog(
            officialBkashNumber = appCustomization.officialBkashNumber,
            officialNagadNumber = appCustomization.officialNagadNumber,
            defaultSenderNumber = userProfile.bkashNumber.ifBlank { userProfile.phone },
            onDismiss = { viewModel.closeAddMoney() },
            onConfirmDeposit = { amount, method, senderNumber, trxId ->
                viewModel.submitDepositRequest(amount, method, senderNumber, trxId)
            }
        )
    }

    // Withdraw Dialog
    if (showWithdraw) {
        WithdrawDialog(
            currentBalance = walletBalance,
            matchesPlayed = userProfile.matchesPlayed,
            minWithdraw = appCustomization.minWithdraw,
            maxWithdraw = appCustomization.maxWithdraw,
            defaultBkashNumber = userProfile.bkashNumber.ifBlank { userProfile.phone },
            onDismiss = { viewModel.closeWithdraw() },
            onConfirmWithdraw = { amount, method, targetNumber ->
                viewModel.submitWithdrawRequest(amount, method, targetNumber)
            }
        )
    }

    // Daily Lucky Spin Dialog
    if (showLuckySpin) {
        DailyLuckySpinDialog(
            hasSpunToday = hasSpunToday,
            onDismiss = { viewModel.closeLuckySpin() },
            onSpinWheel = { viewModel.spinWheel() },
            spinHistory = spinHistory
        )
    }

    // Daily Streak Bonus Dialog
    if (showDailyStreak) {
        DailyStreakDialog(
            streakInfo = dailyStreakInfo,
            onClaimReward = { viewModel.claimDailyStreak() },
            onDismiss = { viewModel.closeDailyStreak() }
        )
    }

    // Support Chat & AI Dialog
    if (showSupportChat) {
        SupportChatDialog(
            currentTab = supportDialogTab,
            onTabSelected = { tab -> viewModel.setSupportDialogTab(tab) },
            adminMessages = chatMessages,
            aiMessages = aiChatMessages,
            officialBkashNumber = appCustomization.officialBkashNumber,
            onDismiss = { viewModel.closeSupportChat() },
            onSendAdminMessage = { text -> viewModel.sendChatMessage(text) },
            onSendAiMessage = { text -> viewModel.sendAiMessage(text) },
            onResetAiChat = { viewModel.resetAiChat() },
            onAiActionClick = { actionType -> viewModel.handleAiAction(actionType) },
            registeredUsers = registeredUsers,
            currentUser = userProfile,
            onPlayerProfileClick = { selectedPlayerProfileCard = it },
            onGiveKudos = { viewModel.giveProfileKudos(it) }
        )
    }

    // Edit Account Dialog
    if (showEditProfile) {
        EditAccountDialog(
            userProfile = userProfile,
            onDismiss = { viewModel.closeEditProfile() },
            onSaveProfile = { name, ign, uid, phone, bkash, avatarId, customPfpUri, customBannerUri, bannerColorId ->
                viewModel.updateProfile(
                    accountName = name,
                    newIgn = ign,
                    newUid = uid,
                    newPhone = phone,
                    newBkash = bkash,
                    newAvatarId = avatarId,
                    newCustomPfpUri = customPfpUri,
                    newCustomBannerUri = customBannerUri,
                    newBannerColorId = bannerColorId
                )
            },
            onSaveBio = { newBio ->
                viewModel.updateBio(newBio)
            }
        )
    }

    // Admin Control Dialog (Owner or Admin only)
    if (showAdminDialog && (userProfile.role == UserRole.OWNER || userProfile.role == UserRole.ADMIN)) {
        AdminControlDialog(
            matches = allMatches,
            currentWhatsAppUrl = officialWhatsAppUrl,
            allUsers = registeredUsers,
            depositRequests = depositRequests,
            withdrawRequests = withdrawRequests,
            onDismiss = { viewModel.closeAdminPanel() },
            onPublishRoom = { matchId, roomId, password ->
                viewModel.publishRoomCredentials(matchId, roomId, password)
            },
            onUpdateWhatsAppUrl = { url ->
                viewModel.updateWhatsAppGroupUrl(url)
            },
            onCreateNewMatch = { title, category, matchType, mapName, matchDateTime, entryFee, totalPrize, perKill, booyahPrize, totalSlots ->
                viewModel.createNewMatch(
                    title, category, matchType, mapName, matchDateTime,
                    entryFee, totalPrize, perKill, booyahPrize, totalSlots
                )
            },
            onAdjustUserBalance = { target, amount, isAdd, reason ->
                viewModel.adjustUserBalance(target, amount, isAdd, reason)
            },
            onIncrementUserMatchesPlayed = { target, delta ->
                viewModel.incrementUserMatchesPlayed(target, delta)
            },
            onApproveDeposit = { reqId -> viewModel.approveDepositRequest(reqId) },
            onRejectDeposit = { reqId -> viewModel.rejectDepositRequest(reqId) },
            onApproveWithdraw = { reqId -> viewModel.approveWithdrawRequest(reqId) },
            onRejectWithdraw = { reqId -> viewModel.rejectWithdrawRequest(reqId) },
            badgeAppeals = badgeAppeals,
            onApproveBadgeAppeal = { appealId, badge -> viewModel.approveBadgeAppeal(appealId, badge) },
            onDenyBadgeAppeal = { appealId, reason -> viewModel.denyBadgeAppeal(appealId, reason) },
            onSendAppealChat = { appealId, text, uri -> viewModel.sendAppealChatMessage(appealId, text, uri) }
        )
    }

    // Settings Dialog (includes Deposit/Withdraw Request Section)
    if (showSettingsDialog) {
        SettingsDialog(
            officialBkashNumber = appCustomization.officialBkashNumber,
            officialWhatsAppUrl = officialWhatsAppUrl,
            appCustomization = appCustomization,
            isOwner = (userProfile.role == UserRole.OWNER),
            isAdmin = (userProfile.role == UserRole.OWNER || userProfile.role == UserRole.ADMIN),
            currentBalance = walletBalance,
            matchesPlayed = userProfile.matchesPlayed,
            currentUserPhone = userProfile.phone,
            depositRequests = depositRequests,
            withdrawRequests = withdrawRequests,
            onDismiss = { viewModel.closeSettings() },
            onOpenAdminPanel = {
                viewModel.closeSettings()
                viewModel.openAdminPanel()
            },
            onOpenOwnerPanel = {
                viewModel.closeSettings()
                viewModel.openOwnerPanel()
            },
            onOpenMasterEditor = {
                viewModel.closeSettings()
                showMasterAppEditor = true
            },
            onOpenDepositDialog = { viewModel.openAddMoney() },
            onOpenWithdrawDialog = { viewModel.openWithdraw() },
            onApproveDeposit = { reqId -> viewModel.approveDepositRequest(reqId) },
            onRejectDeposit = { reqId -> viewModel.rejectDepositRequest(reqId) },
            onApproveWithdraw = { reqId -> viewModel.approveWithdrawRequest(reqId) },
            onRejectWithdraw = { reqId -> viewModel.rejectWithdrawRequest(reqId) },
            onResetAppForFreshDownload = { viewModel.resetAppForFreshDownload() }
        )
    }

    // Owner Control Dialog (Owner Only: Ban fraud users, balance control, deposit/withdraw approval & auto-refund)
    if (showOwnerDialog) {
        OwnerControlDialog(
            userProfile = userProfile,
            allUsers = registeredUsers,
            allBookings = myBookings,
            matches = allMatches,
            depositRequests = depositRequests,
            withdrawRequests = withdrawRequests,
            onDismiss = { viewModel.closeOwnerPanel() },
            onClaimOwner = { key -> viewModel.claimOwnerRole(key) },
            onBanUser = { target, reason -> viewModel.banUser(target, reason) },
            onUnbanUser = { target -> viewModel.unbanUser(target) },
            onPromoteToAdmin = { target, passcode, name, ign, uid ->
                viewModel.promoteToAdmin(target, passcode, name, ign, uid)
            },
            onDemoteAdmin = { target -> viewModel.demoteAdmin(target) },
            onUpdateMasterPin = { pin -> viewModel.updateMasterOwnerPin(pin) },
            onCancelSlotAndRefund = { bookingId ->
                viewModel.cancelSlotBookingAndRefund(bookingId)
            },
            onCancelMatchAndRefundAll = { matchId ->
                viewModel.cancelMatchAndRefundAll(matchId)
            },
            onAdjustUserBalance = { target, amount, isAdd, reason ->
                viewModel.adjustUserBalance(target, amount, isAdd, reason)
            },
            onIncrementUserMatchesPlayed = { target, delta ->
                viewModel.incrementUserMatchesPlayed(target, delta)
            },
            onApproveDeposit = { reqId -> viewModel.approveDepositRequest(reqId) },
            onRejectDeposit = { reqId -> viewModel.rejectDepositRequest(reqId) },
            onApproveWithdraw = { reqId -> viewModel.approveWithdrawRequest(reqId) },
            onRejectWithdraw = { reqId -> viewModel.rejectWithdrawRequest(reqId) },
            onGrantBadge = { target, badge -> viewModel.grantBadgeToUser(target, badge) },
            onRevokeBadge = { target, badge -> viewModel.revokeBadgeFromUser(target, badge) },
            badgeAppeals = badgeAppeals,
            onApproveBadgeAppeal = { appealId, badge -> viewModel.approveBadgeAppeal(appealId, badge) },
            onDenyBadgeAppeal = { appealId, reason -> viewModel.denyBadgeAppeal(appealId, reason) },
            onSendAppealChat = { appealId, text, uri -> viewModel.sendAppealChatMessage(appealId, text, uri) }
        )
    }

    // Master App Editor Dialog (Owner only)
    if (showMasterAppEditor && userProfile.role == UserRole.OWNER) {
        MasterAppEditorDialog(
            appCustomization = appCustomization,
            matches = allMatches,
            bookings = myBookings,
            strictRules = strictRules,
            onDismiss = { showMasterAppEditor = false },
            onUpdateAppBranding = { name, tagline, logo, ticker, announcement ->
                viewModel.updateAppBranding(name, tagline, logo, ticker, announcement)
            },
            onUpdateFinancials = { bkash, nagad, whatsApp, minW, maxW ->
                viewModel.updateFinancialSettings(bkash, nagad, whatsApp, minW, maxW)
            },
            onToggleMaintenance = { enabled, msg ->
                viewModel.toggleMaintenanceMode(enabled, msg)
            },
            onCreateMatch = { title, category, matchType, mapName, matchDateTime, entryFee, totalPrize, perKill, booyahPrize, totalSlots ->
                viewModel.createNewMatch(
                    title, category, matchType, mapName, matchDateTime,
                    entryFee, totalPrize, perKill, booyahPrize, totalSlots
                )
            },
            onUpdateExistingMatch = { match ->
                viewModel.updateExistingMatch(match)
            },
            onDeleteMatch = { matchId ->
                viewModel.deleteMatch(matchId)
            },
            onUpdateSlotCapacity = { matchId, newCapacity ->
                viewModel.updateMatchSlotCapacity(matchId, newCapacity)
            },
            onCreateManualSlot = { matchId, slotNumber, ign, uid, phone, fee ->
                viewModel.createManualSlotBooking(matchId, slotNumber, ign, uid, phone, fee)
            },
            onCancelSlotAndRefund = { bookingId ->
                viewModel.cancelSlotBookingAndRefund(bookingId)
            },
            onCancelMatchAndRefundAll = { matchId ->
                viewModel.cancelMatchAndRefundAll(matchId)
            },
            onAddRule = { title, desc, penalty, emoji ->
                viewModel.addStrictRule(title, desc, penalty, emoji)
            },
            onUpdateRule = { rule ->
                viewModel.updateStrictRule(rule)
            },
            onDeleteRule = { ruleId ->
                viewModel.deleteStrictRule(ruleId)
            },
            onResetRules = {
                viewModel.resetDefaultRules()
            }
        )
    }

    // Rules Dialog
    if (showRulesDialog) {
        Dialog(onDismissRequest = { viewModel.closeRules() }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f))
            ) {
                RulesAndSupportScreen(
                    officialBkashNumber = appCustomization.officialBkashNumber,
                    onOpenSupportChat = {
                        viewModel.closeRules()
                        viewModel.openSupportChat()
                    },
                    strictRules = strictRules,
                    appCustomization = appCustomization,
                    isOwner = (userProfile.role == UserRole.OWNER),
                    onOpenOwnerRulesEditor = {
                        viewModel.closeRules()
                        showMasterAppEditor = true
                    }
                )
            }
        }
    }

    // Global Esports Player Profile Card Modal
    selectedPlayerProfileCard?.let { profileData ->
        PlayerProfileCardDialog(
            profile = profileData,
            onDismiss = { selectedPlayerProfileCard = null },
            onEditOwnProfile = if (profileData.isSelf) {
                {
                    selectedPlayerProfileCard = null
                    viewModel.openEditProfile()
                }
            } else null,
            onGiveKudos = { targetKey -> viewModel.giveProfileKudos(targetKey) },
            userProfileForAppeal = userProfile,
            badgeAppeals = badgeAppeals,
            onSubmitBadgeAppeal = { badge, note -> viewModel.submitBadgeAppeal(badge, note) },
            onSendAppealChat = { appealId, text, uri -> viewModel.sendAppealChatMessage(appealId, text, uri) }
        )
    }
}

