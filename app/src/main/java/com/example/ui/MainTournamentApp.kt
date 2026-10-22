package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.components.AddMoneyDialog
import com.example.ui.components.AdminControlDialog
import com.example.ui.components.DailyLuckySpinDialog
import com.example.ui.components.DailyStreakDialog
import com.example.ui.components.EditAccountDialog
import com.example.ui.components.OwnerControlDialog
import com.example.ui.components.RoomDetailsDialog
import com.example.ui.components.RoomSlotGridDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SlotBookingDialog
import com.example.ui.components.SupportChatDialog
import com.example.ui.components.WithdrawDialog
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
            onRegister = { name, ign, uid, phone, bkash ->
                viewModel.register(name, ign, uid, phone, bkash, "avatar_1")
            },
            onLogin = { phoneOrEmail, pass ->
                viewModel.login(phoneOrEmail, pass)
            },
            onDemoOwnerLogin = {
                viewModel.loginAsDemoOwner()
            }
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
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.selectTab(TournamentTab.MATCHES) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.th_logo),
                            contentDescription = "TH Logo",
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TRUSTED TOURNAMENT",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White
                        )
                    }
                },
                actions = {
                    // Supreme Owner Button (Crown)
                    IconButton(
                        onClick = { viewModel.openOwnerPanel() },
                        modifier = Modifier.testTag("appbar_owner_btn")
                    ) {
                        Text("👑", fontSize = 18.sp)
                    }

                    // Admin Control Button (One-tap Room publishing and WhatsApp link setup)
                    IconButton(
                        onClick = { viewModel.openAdminPanel() },
                        modifier = Modifier.testTag("appbar_admin_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AdminPanelSettings,
                            contentDescription = "Admin Control",
                            tint = FlameYellow
                        )
                    }

                    // Daily Streak Button (Fire Icon)
                    IconButton(
                        onClick = { viewModel.openDailyStreak() },
                        modifier = Modifier.testTag("appbar_daily_streak_btn")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.LocalFireDepartment,
                                contentDescription = "Daily Streak",
                                tint = FlameOrange
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

                    // Talk with AI Button (Robot Icon)
                    IconButton(
                        onClick = { viewModel.openAiAssistant() },
                        modifier = Modifier.testTag("appbar_ai_chat_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = "Talk with AI",
                            tint = FlameCyan
                        )
                    }

                    // Settings Button
                    IconButton(
                        onClick = { viewModel.openSettings() },
                        modifier = Modifier.testTag("appbar_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }

                    // bKash Wallet Balance Pill
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BkashPink,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectTab(TournamentTab.WALLET) }
                            .testTag("appbar_wallet_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                // Matches Tab
                NavigationBarItem(
                    selected = currentTab == TournamentTab.MATCHES,
                    onClick = { viewModel.selectTab(TournamentTab.MATCHES) },
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
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
                        onClaimMission = { viewModel.claimMission(it) }
                    )
                }

                TournamentTab.MY_MATCHES -> {
                    MyMatchesScreen(
                        bookings = myBookings,
                        onViewRoomDetails = { viewModel.openRoomDetailsForBooking(it) },
                        onExploreMatchesClick = { viewModel.selectTab(TournamentTab.MATCHES) }
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
                        onRegister = { name, ign, uid, phone, bkash, avatarId ->
                            viewModel.register(name, ign, uid, phone, bkash, avatarId)
                        },
                        onLogout = { viewModel.logout() },
                        onNavigateToWallet = { viewModel.selectTab(TournamentTab.WALLET) },
                        onNavigateToMyMatches = { viewModel.selectTab(TournamentTab.MY_MATCHES) },
                        onOpenRules = { viewModel.openRules() },
                        onOpenSupportChat = { viewModel.openSupportChat() },
                        onOpenOwnerPanel = { viewModel.openOwnerPanel() }
                    )
                }
            }
        }
    }

    // Slot Booking Dialog
    bookingMatch?.let { match ->
        SlotBookingDialog(
            match = match,
            officialBkashNumber = viewModel.officialBkashNumber,
            initialSlotNumber = preselectedSlot,
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
            officialBkashNumber = viewModel.officialBkashNumber,
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
            officialBkashNumber = viewModel.officialBkashNumber,
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
            officialBkashNumber = viewModel.officialBkashNumber,
            officialWhatsAppUrl = officialWhatsAppUrl,
            onDismiss = { viewModel.closeSettings() },
            onOpenAdminPanel = {
                viewModel.closeSettings()
                viewModel.openAdminPanel()
            },
            onOpenOwnerPanel = {
                viewModel.closeSettings()
                viewModel.openOwnerPanel()
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

    // Rules Dialog
    if (showRulesDialog) {
        Dialog(onDismissRequest = { viewModel.closeRules() }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                RulesAndSupportScreen(
                    officialBkashNumber = viewModel.officialBkashNumber,
                    onOpenSupportChat = {
                        viewModel.closeRules()
                        viewModel.openSupportChat()
                    }
                )
            }
        }
    }
}
