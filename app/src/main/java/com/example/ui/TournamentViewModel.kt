package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TournamentRepository
import com.example.model.AppUser
import com.example.model.ChatMessage
import com.example.model.DailyGamerMission
import com.example.model.DailyStreakInfo
import com.example.model.GameCategory
import com.example.model.LeaderboardPlayer
import com.example.model.MatchResult
import com.example.model.SlotBooking
import com.example.model.SpinHistoryRecord
import com.example.model.MatchType
import com.example.model.TournamentMatch
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.WalletTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class TournamentTab(val banglaLabel: String) {
    MATCHES("টুর্নামেন্ট"),
    MY_MATCHES("আমার ম্যাচ"),
    WALLET("ওয়ালেট"),
    LEADERBOARD("লিডারবোর্ড"),
    ACCOUNT("প্রোফাইল")
}

data class RoomDetailsData(
    val title: String,
    val slotNumber: Int,
    val roomId: String?,
    val roomPassword: String?
)

enum class SupportTabType {
    AI_ASSISTANT,
    LIVE_ADMIN
}

class TournamentViewModel(
    private val repository: TournamentRepository = TournamentRepository()
) : ViewModel() {

    val officialBkashNumber: String = repository.officialBkashNumber

    private val _currentTab = MutableStateFlow(TournamentTab.MATCHES)
    val currentTab: StateFlow<TournamentTab> = _currentTab.asStateFlow()

    private val _selectedCategory = MutableStateFlow(GameCategory.ALL)
    val selectedCategory: StateFlow<GameCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val walletBalance: StateFlow<Int> = repository.walletBalance
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val myBookings: StateFlow<List<SlotBooking>> = repository.myBookings
    val transactions: StateFlow<List<WalletTransaction>> = repository.transactions
    val leaderboard: StateFlow<List<LeaderboardPlayer>> = repository.leaderboard
    val results: StateFlow<List<MatchResult>> = repository.results
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val hasSpunToday: StateFlow<Boolean> = repository.hasSpunToday
    val hasSpunThisWeek: StateFlow<Boolean> = repository.hasSpunThisWeek
    val spinHistory: StateFlow<List<SpinHistoryRecord>> = repository.spinHistory
    val dailyMissions: StateFlow<List<DailyGamerMission>> = repository.dailyMissions

    // Dialog States
    private val _bookingMatch = MutableStateFlow<TournamentMatch?>(null)
    val bookingMatch: StateFlow<TournamentMatch?> = _bookingMatch.asStateFlow()

    private val _preselectedSlotNumber = MutableStateFlow<Int?>(null)
    val preselectedSlotNumber: StateFlow<Int?> = _preselectedSlotNumber.asStateFlow()

    private val _roomSlotGridMatch = MutableStateFlow<TournamentMatch?>(null)
    val roomSlotGridMatch: StateFlow<TournamentMatch?> = _roomSlotGridMatch.asStateFlow()

    private val _roomDetails = MutableStateFlow<RoomDetailsData?>(null)
    val roomDetails: StateFlow<RoomDetailsData?> = _roomDetails.asStateFlow()

    private val _showAddMoneyDialog = MutableStateFlow(false)
    val showAddMoneyDialog: StateFlow<Boolean> = _showAddMoneyDialog.asStateFlow()

    private val _showWithdrawDialog = MutableStateFlow(false)
    val showWithdrawDialog: StateFlow<Boolean> = _showWithdrawDialog.asStateFlow()

    private val _showLuckySpinDialog = MutableStateFlow(false)
    val showLuckySpinDialog: StateFlow<Boolean> = _showLuckySpinDialog.asStateFlow()

    val dailyStreakInfo: StateFlow<DailyStreakInfo> = repository.dailyStreakInfo

    private val _showDailyStreakDialog = MutableStateFlow(false)
    val showDailyStreakDialog: StateFlow<Boolean> = _showDailyStreakDialog.asStateFlow()

    private val _showSupportChatDialog = MutableStateFlow(false)
    val showSupportChatDialog: StateFlow<Boolean> = _showSupportChatDialog.asStateFlow()

    val aiChatMessages: StateFlow<List<ChatMessage>> = repository.aiChatMessages

    private val _supportDialogTab = MutableStateFlow(SupportTabType.AI_ASSISTANT)
    val supportDialogTab: StateFlow<SupportTabType> = _supportDialogTab.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = repository.isLoggedIn
    val officialWhatsAppGroupUrl: StateFlow<String> = repository.officialWhatsAppGroupUrl
    val allMatches: StateFlow<List<TournamentMatch>> = repository.matches
    val registeredUsers: StateFlow<List<AppUser>> = repository.registeredUsers

    private val _showEditProfileDialog = MutableStateFlow(false)
    val showEditProfileDialog: StateFlow<Boolean> = _showEditProfileDialog.asStateFlow()

    private val _showRulesDialog = MutableStateFlow(false)
    val showRulesDialog: StateFlow<Boolean> = _showRulesDialog.asStateFlow()

    private val _showAdminDialog = MutableStateFlow(false)
    val showAdminDialog: StateFlow<Boolean> = _showAdminDialog.asStateFlow()

    private val _showOwnerDialog = MutableStateFlow(false)
    val showOwnerDialog: StateFlow<Boolean> = _showOwnerDialog.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Filtered matches stream
    val filteredMatches: StateFlow<List<TournamentMatch>> = combine(
        repository.matches,
        _selectedCategory,
        _searchQuery
    ) { allMatches, category, query ->
        allMatches.filter { match ->
            val matchesCategory = (category == GameCategory.ALL || match.category == category)
            val matchesQuery = query.isBlank() ||
                    match.title.contains(query, ignoreCase = true) ||
                    match.mapName.contains(query, ignoreCase = true) ||
                    match.category.banglaTitle.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tab: TournamentTab) {
        _currentTab.value = tab
    }

    fun selectCategory(category: GameCategory) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun openSlotBooking(match: TournamentMatch, slot: Int? = null) {
        _bookingMatch.value = match
        _preselectedSlotNumber.value = slot
    }

    fun closeSlotBooking() {
        _bookingMatch.value = null
        _preselectedSlotNumber.value = null
    }

    fun openSlotGrid(match: TournamentMatch) {
        _roomSlotGridMatch.value = match
    }

    fun closeSlotGrid() {
        _roomSlotGridMatch.value = null
    }

    fun openRoomDetails(match: TournamentMatch) {
        val booking = myBookings.value.firstOrNull { it.matchId == match.id }
        _roomDetails.value = RoomDetailsData(
            title = match.title,
            slotNumber = booking?.slotNumber ?: 1,
            roomId = match.roomId,
            roomPassword = match.roomPassword
        )
    }

    fun openRoomDetailsForBooking(booking: SlotBooking) {
        _roomDetails.value = RoomDetailsData(
            title = booking.matchTitle,
            slotNumber = booking.slotNumber,
            roomId = booking.roomId,
            roomPassword = booking.roomPassword
        )
    }

    fun closeRoomDetails() {
        _roomDetails.value = null
    }

    fun openAddMoney() {
        _showAddMoneyDialog.value = true
    }

    fun closeAddMoney() {
        _showAddMoneyDialog.value = false
    }

    fun openWithdraw() {
        _showWithdrawDialog.value = true
    }

    fun closeWithdraw() {
        _showWithdrawDialog.value = false
    }

    fun openLuckySpin() {
        _showLuckySpinDialog.value = true
    }

    fun closeLuckySpin() {
        _showLuckySpinDialog.value = false
    }

    fun spinWheel(): Int {
        val prize = repository.spinWeeklyWheel()
        if (prize > 0) {
            _snackbarMessage.value = if (prize == 30) {
                "👑 অবিশ্বাস্য অলৌকিক জয়! আপনি ০.০৫% ভাগ্যে ৳৩০ মেগা জ্যাকপট জিতেছেন!"
            } else {
                "🎉 অভিনন্দন! আপনি সাপ্তাহিক স্পিনে ৳$prize জিতেছেন!"
            }
        }
        return prize
    }

    fun claimMission(missionId: String) {
        val success = repository.claimMissionReward(missionId)
        if (success) {
            _snackbarMessage.value = "🎯 দৈনিক মিশন রিওয়ার্ড নগদ বোনাস ওয়ালেটে যুক্ত হয়েছে!"
        }
    }

    fun openDailyStreak() {
        _showDailyStreakDialog.value = true
    }

    fun closeDailyStreak() {
        _showDailyStreakDialog.value = false
    }

    fun claimDailyStreak(): Int {
        val reward = repository.claimDailyStreakReward()
        if (reward > 0) {
            _snackbarMessage.value = "🎉 অভিনন্দন! ৳$reward দৈনিক লগইন বোনাস ওয়ালেটে জমা হয়েছে!"
        }
        return reward
    }

    fun updatePlayerLoadout(
        weapon: String,
        character: String,
        role: String,
        guild: String,
        tagline: String,
        headshotRate: Int,
        kdRatio: Double
    ) {
        repository.updatePlayerLoadout(weapon, character, role, guild, tagline, headshotRate, kdRatio)
        _snackbarMessage.value = "ব্যাটল লোডআউট সফলভাবে সংরক্ষিত হয়েছে! 🎮"
    }

    fun openSupportChat(tab: SupportTabType = SupportTabType.AI_ASSISTANT) {
        _supportDialogTab.value = tab
        _showSupportChatDialog.value = true
    }

    fun openAiAssistant() {
        openSupportChat(SupportTabType.AI_ASSISTANT)
    }

    fun setSupportDialogTab(tab: SupportTabType) {
        _supportDialogTab.value = tab
    }

    fun closeSupportChat() {
        _showSupportChatDialog.value = false
    }

    fun sendChatMessage(text: String) {
        repository.sendSupportMessage(text)
    }

    fun sendAiMessage(query: String) {
        repository.sendAiAssistantMessage(query)
    }

    fun resetAiChat() {
        repository.resetAiChat()
    }

    fun handleAiAction(actionType: String) {
        when (actionType) {
            "ACTION_OPEN_WALLET" -> {
                closeSupportChat()
                openAddMoney()
            }
            "ACTION_OPEN_MY_MATCHES" -> {
                closeSupportChat()
                selectTab(TournamentTab.MY_MATCHES)
            }
            "ACTION_OPEN_MATCHES" -> {
                closeSupportChat()
                selectTab(TournamentTab.MATCHES)
            }
            "ACTION_OPEN_ACCOUNT" -> {
                closeSupportChat()
                selectTab(TournamentTab.ACCOUNT)
            }
            "ACTION_OPEN_SPIN" -> {
                closeSupportChat()
                openLuckySpin()
            }
            "ACTION_OPEN_STREAK" -> {
                closeSupportChat()
                openDailyStreak()
            }
            "ACTION_OPEN_RULES" -> {
                closeSupportChat()
                openRules()
            }
            "ACTION_OPEN_ADMIN_CHAT" -> {
                setSupportDialogTab(SupportTabType.LIVE_ADMIN)
            }
            "ACTION_CALL_ADMIN" -> {
                _snackbarMessage.value = "বিকাশ ও এডমিন হেল্পলাইন: $officialBkashNumber"
            }
        }
    }

    fun confirmSlotBooking(
        slotNumber: Int,
        playerIgn: String,
        playerUid: String,
        senderBkash: String,
        trxId: String
    ) {
        val match = _bookingMatch.value ?: return
        val success = repository.bookSlotWithBkash(
            match = match,
            slotNumber = slotNumber,
            playerIgn = playerIgn,
            playerUid = playerUid,
            bkashNumber = senderBkash,
            trxId = trxId
        )
        if (success) {
            _bookingMatch.value = null
            _roomSlotGridMatch.value = null
            _snackbarMessage.value = "পেমেন্ট সনাক্ত হয়েছে! স্লট #$slotNumber নিশ্চিত করা হয়েছে।"
            _currentTab.value = TournamentTab.MY_MATCHES
        }
    }

    fun confirmAddMoney(amount: Int, bkashNumber: String, trxId: String) {
        repository.addMoneyWithBkash(amount, bkashNumber, trxId)
        _showAddMoneyDialog.value = false
        _snackbarMessage.value = "৳$amount সফলভাবে ওয়ালেটে যোগ হয়েছে!"
    }

    fun confirmWithdraw(amount: Int, bkashNumber: String) {
        if (amount < 50) {
            _snackbarMessage.value = "সর্বনিম্ন ৫০ টাকা উইথড্র করা যাবে!"
            return
        }
        if (amount > 200) {
            _snackbarMessage.value = "সর্বোচ্চ ২০০ টাকা উইথড্র করা যাবে!"
            return
        }
        val success = repository.withdrawToBkash(amount, bkashNumber)
        _showWithdrawDialog.value = false
        if (success) {
            _snackbarMessage.value = "৳$amount ক্যাশআউট রিকোয়েস্ট সফল! ৫-১০ মিনিটের মধ্যে বিকাশে পাঠানো হবে।"
        } else {
            _snackbarMessage.value = "পর্যাপ্ত ব্যালেন্স নেই!"
        }
    }

    fun openEditProfile() {
        _showEditProfileDialog.value = true
    }

    fun closeEditProfile() {
        _showEditProfileDialog.value = false
    }

    fun openRules() {
        _showRulesDialog.value = true
    }

    fun closeRules() {
        _showRulesDialog.value = false
    }

    fun openSettings() {
        _showSettingsDialog.value = true
    }

    fun closeSettings() {
        _showSettingsDialog.value = false
    }

    fun login(phoneOrEmail: String, password: String): Boolean {
        val success = repository.login(phoneOrEmail, password)
        if (success) {
            _snackbarMessage.value = "সফলভাবে লগইন হয়েছে! স্বাগতম ${userProfile.value.accountName}"
        }
        return success
    }

    fun register(
        accountName: String,
        ign: String,
        uid: String,
        phone: String,
        bkash: String,
        avatarId: String
    ): Boolean {
        val success = repository.register(accountName, ign, uid, phone, bkash, avatarId)
        if (success) {
            _snackbarMessage.value = "একাউন্ট সফলভাবে তৈরি হয়েছে! স্বাগতম $accountName"
        }
        return success
    }

    fun logout() {
        repository.logout()
        _snackbarMessage.value = "লগআউট সফল হয়েছে।"
    }

    fun cancelSlotBookingAndRefund(bookingId: String): Boolean {
        val success = repository.cancelSlotBookingAndRefund(bookingId)
        if (success) {
            _snackbarMessage.value = "✅ স্লট সফলভাবে বাতিল এবং প্লেয়ারের ওয়ালেটে সম্পূর্ণ টাকা রিফান্ড করা হয়েছে!"
        }
        return success
    }

    fun cancelMatchAndRefundAll(matchId: String): Pair<Int, Int> {
        val result = repository.cancelMatchAndRefundAll(matchId)
        _snackbarMessage.value = "⚠️ ম্যাচ বাতিল! মোট ${result.first} টি স্লটের ৳${result.second} স্বয়ংক্রিয়ভাবে রিফান্ড হয়েছে।"
        return result
    }

    fun loginAsDemoOwner(): Boolean {
        val success = repository.loginAsDemoOwner()
        if (success) {
            _snackbarMessage.value = "👑 ওনার তানভির হাসান হিসেবে সফলভাবে লগইন হয়েছে!"
        }
        return success
    }

    fun updateProfile(
        accountName: String,
        newIgn: String,
        newUid: String,
        newPhone: String,
        newBkash: String,
        newAvatarId: String
    ) {
        repository.updateProfile(accountName, newIgn, newUid, newPhone, newBkash, newAvatarId)
        _showEditProfileDialog.value = false
        _snackbarMessage.value = "প্রোফাইল তথ্য সফলভাবে আপডেট হয়েছে!"
    }

    fun openAdminPanel() {
        _showAdminDialog.value = true
    }

    fun closeAdminPanel() {
        _showAdminDialog.value = false
    }

    fun publishRoomCredentials(matchId: String, roomId: String, password: String) {
        repository.publishRoomCredentials(matchId, roomId, password)
        _snackbarMessage.value = "🚀 এক ট্যাপে সব প্লেয়ারদের কাছে রুম আইডি ($roomId) ও পাসওয়ার্ড ($password) পাঠানো হয়েছে!"
    }

    fun updateWhatsAppGroupUrl(url: String) {
        repository.updateWhatsAppGroupUrl(url)
        _snackbarMessage.value = "🟢 অফিসিয়াল WhatsApp টুর্নামেন্ট লিঙ্ক আপডেট হয়েছে!"
    }

    fun createNewMatch(
        title: String,
        category: GameCategory,
        matchType: MatchType,
        mapName: String,
        matchDateTime: String,
        entryFee: Int,
        totalPrize: Int,
        perKill: Int,
        booyahPrize: Int,
        totalSlots: Int
    ) {
        repository.createNewMatch(
            title, category, matchType, mapName, matchDateTime,
            entryFee, totalPrize, perKill, booyahPrize, totalSlots
        )
        _snackbarMessage.value = "নতুন টুর্নামেন্ট ম্যাচ সফলভাবে তৈরি হয়েছে!"
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // ==================== OWNER METHODS ====================

    fun openOwnerPanel() {
        _showOwnerDialog.value = true
    }

    fun closeOwnerPanel() {
        _showOwnerDialog.value = false
    }

    fun claimOwnerRole(key: String): Boolean {
        val success = repository.claimOwnerRole(key)
        if (success) {
            _snackbarMessage.value = "👑 অভিনন্দন! আপনি সুপ্রিম ওনার (Owner) হিসেবে সফলভাবে ভেরিফাইড হয়েছেন!"
        } else {
            _snackbarMessage.value = "ভুল ওনার মাস্টার কী! অ্যাক্সেস প্রত্যাখ্যাত।"
        }
        return success
    }

    fun banUser(userIdOrPhone: String, reason: String) {
        val success = repository.banUser(userIdOrPhone, reason)
        if (success) {
            _snackbarMessage.value = "🚫 ইউজারকে সফলভাবে টুর্নামেন্ট অ্যাপ থেকে ব্যান করা হয়েছে!"
        } else {
            _snackbarMessage.value = "ওনার ছাড়া এই অ্যাকশন অনুমোদনযোগ্য নয়।"
        }
    }

    fun unbanUser(userIdOrPhone: String) {
        val success = repository.unbanUser(userIdOrPhone)
        if (success) {
            _snackbarMessage.value = "✅ ইউজারের ব্যান সফলভাবে প্রত্যাহার করা হয়েছে।"
        }
    }

    fun promoteToAdmin(
        targetUserOrPhone: String,
        adminPasscode: String,
        customName: String? = null,
        ign: String? = null,
        uid: String? = null
    ) {
        val success = repository.promoteToAdmin(targetUserOrPhone, adminPasscode, customName, ign, uid)
        if (success) {
            _snackbarMessage.value = "🛡️ নতুন টুর্নামেন্ট অ্যাডমিন সফলভাবে নিয়োগ করা হয়েছে!"
        }
    }

    fun demoteAdmin(userIdOrPhone: String) {
        val success = repository.demoteAdmin(userIdOrPhone)
        if (success) {
            _snackbarMessage.value = "অ্যাডমিন পদ বাতিল করে সাধারণ প্লেয়ার করা হয়েছে।"
        }
    }

    fun updateMasterOwnerPin(newPin: String): Boolean {
        val success = repository.updateMasterOwnerPin(newPin)
        if (success) {
            _snackbarMessage.value = "ওনার মাস্টার পিন সফলভাবে পরিবর্তন করা হয়েছে!"
        } else {
            _snackbarMessage.value = "পিন কমপক্ষে ৪ অক্ষরের হতে হবে।"
        }
        return success
    }
}
