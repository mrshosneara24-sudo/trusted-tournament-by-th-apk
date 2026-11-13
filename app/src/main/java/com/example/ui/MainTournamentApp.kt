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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.UserRole
import com.example.ui.components.AddMoneyDialog
import com.example.ui.components.AdminControlDialog
import com.example.ui.components.DailyLuckySpinDialog
import com.example.ui.components.DailyStreakDialog
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
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MatchesScreen
import com.example.ui.screens.MyMatchesScreen
import com.example.ui.screens.RulesAndSupportScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.NeonPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTournamentApp(
    viewModel: TournamentViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val matches by viewModel.filteredMatches.collectAsStateWithLifecycle()
    val myBookings by viewModel.myBookings.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val walletBalance by viewModel.walletBalance.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val hasSpunToday by viewModel.hasSpunToday.collectAsStateWithLifecycle()
    val preselectedSlot by viewModel.preselectedSlotNumber.collectAsStateWithLifecycle()

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val officialWhatsAppUrl by viewModel.officialWhatsAppGroupUrl.collectAsStateWithLifecycle()
    val allMatches by viewModel.allMatches.collectAsStateWithLifecycle()
    val registeredUsers by viewModel.registeredUsers.collectAsStateWithLifecycle()
    val appCustomization by viewModel.appCustomization.collectAsStateWithLifecycle()
    val strictRules by viewModel.strictRules.collectAsStateWithLifecycle()
    var showMasterAppEditor by remember { mutableStateOf(false) }

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

    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Discord-inspired Onboarding & Captcha Screen for new downloads / logged-out users
    if (!isLoggedIn) {
        AuthOnboardingScreen(
            onRegister = { name, ign, uid, phone, bkash, pass ->
                viewModel.register(name, ign, uid, phone, bkash, "avatar_1", pass)
            },
            onLogin = { phoneOrEmail, pass ->
                viewModel.login(phoneOrEmail, pass)
            },
            onDemoOwnerLogin = {
                viewModel.loginAsDemoOwner()
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.selectTab(TournamentTab.MATCHES) }
                        ) {
                            Image(
                                painter = painterResource(id = getLogoDrawable(appCustomization.selectedLogo)),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = appCustomization.appName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.4.sp
                                ),
                                color = Color.White,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        // Supreme Owner A-to-Z Master Editor Button (Crown)
                        IconButton(
                            onClick = { showMasterAppEditor = true },
                            modifier = Modifier.size(36.dp).testTag("appbar_owner_btn")
                        ) {
                            Text("👑", fontSize = 16.sp)
                        }

                        // Admin Control Button
                        IconButton(
                            onClick = { viewModel.openAdminPanel() },
                            modifier = Modifier.size(36.dp).testTag("appbar_admin_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AdminPanelSettings,
                                contentDescription = "Admin Control",
                                tint = FlameYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Daily Streak Button
                        IconButton(
                            onClick = { viewModel.openDailyStreak() },
                            modifier = Modifier.size(36.dp).testTag("appbar_daily_streak_btn")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.LocalFireDepartment,
                                    contentDescription = "Daily Streak",
                                    tint = NeonPink,
                                    modifier = Modifier.size(20.dp)
                                )
                                if (!dailyStreakInfo.isTodayClaimed) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .align(Alignment.TopEnd)
                                            .clip(CircleShape)
                                            .background(FlameYellow)
                                    )
                                }
                            }
                        }

                        // Talk with AI Button
                        IconButton(
                            onClick = { viewModel.openAiAssistant() },
                            modifier = Modifier.size(36.dp).testTag("appbar_ai_chat_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SmartToy,
                                contentDescription = "Talk with AI",
                                tint = FlameCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Settings Button
                        IconButton(
                            onClick = { viewModel.openSettings() },
                            modifier = Modifier.size(36.dp).testTag("appbar_settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // bKash Wallet Balance Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = BkashPink,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .padding(end = 8.dp, start = 2.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectTab(TournamentTab.WALLET) }
                                .testTag("appbar_wallet_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccountBalanceWallet,
                                    contentDescription = "Wallet",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "৳$walletBalance",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                                    color = Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkSurface
                    )
                )
                HorizontalDivider(color = FlameYellow.copy(alpha = 0.28f), thickness = 1.dp)
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = Color.White.copy(alpha = 0.14f), thickness = 1.dp)
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 8.dp
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
                    label = { Text(TournamentTab.MATCHES.banglaLabel, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                    label = { Text(TournamentTab.MY_MATCHES.banglaLabel, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                    label = { Text(TournamentTab.WALLET.banglaLabel, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = BkashPink,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                    label = { Text(TournamentTab.LEADERBOARD.banglaLabel, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.testTag("tab_leaderboard")
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
                    label = { Text(TournamentTab.ACCOUNT.banglaLabel, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = FlameYellow,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                                onWithdrawClick = { viewModel.openWithdraw() }
                            )
                        }

                        TournamentTab.LEADERBOARD -> {
                            LeaderboardScreen(
                                players = leaderboard
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
                                appCustomization = appCustomization
                            )
                        }
                    }
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
            initialSlotNumber = preselectedSlot,
            defaultIgn = userProfile.ign,
            defaultUid = userProfile.uid,
            defaultBkash = userProfile.bkashNumber,
            takenSlots = takenSlots,
            onDismiss = { viewModel.closeSlotBooking() },
            onConfirmBooking = { slot, ign, uid, senderBkash, trxId ->
                viewModel.confirmSlotBooking(slot, ign, uid, senderBkash, trxId)
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
            }
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

    // Add Money Dialog
    if (showAddMoney) {
        AddMoneyDialog(
            officialBkashNumber = appCustomization.officialBkashNumber,
            onDismiss = { viewModel.closeAddMoney() },
            onConfirmDeposit = { amount, senderBkash, trxId ->
                viewModel.confirmAddMoney(amount, senderBkash, trxId)
            }
        )
    }

    // Withdraw Dialog
    if (showWithdraw) {
        WithdrawDialog(
            currentBalance = walletBalance,
            minWithdraw = appCustomization.minWithdraw,
            maxWithdraw = appCustomization.maxWithdraw,
            defaultBkashNumber = userProfile.bkashNumber,
            onDismiss = { viewModel.closeWithdraw() },
            onConfirmWithdraw = { amount, bkashNumber ->
                viewModel.confirmWithdraw(amount, bkashNumber)
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
            onAiActionClick = { actionType -> viewModel.handleAiAction(actionType) }
        )
    }

    // Edit Account Dialog
    if (showEditProfile) {
        EditAccountDialog(
            userProfile = userProfile,
            onDismiss = { viewModel.closeEditProfile() },
            onSaveProfile = { name, ign, uid, phone, bkash, avatarId ->
                viewModel.updateProfile(name, ign, uid, phone, bkash, avatarId)
            }
        )
    }

    // Admin Control Dialog
    if (showAdminDialog) {
        AdminControlDialog(
            matches = allMatches,
            currentWhatsAppUrl = officialWhatsAppUrl,
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
            }
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            officialBkashNumber = appCustomization.officialBkashNumber,
            officialWhatsAppUrl = officialWhatsAppUrl,
            appCustomization = appCustomization,
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
            }
        )
    }

    // Owner Control Dialog (Owner Only: Ban fraud users, create admins & cancel slots with auto-refund)
    if (showOwnerDialog) {
        OwnerControlDialog(
            userProfile = userProfile,
            allUsers = registeredUsers,
            allBookings = myBookings,
            matches = allMatches,
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
            }
        )
    }

    // Master App Editor Dialog (A-to-Z control requested by user)
    if (showMasterAppEditor) {
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
}

