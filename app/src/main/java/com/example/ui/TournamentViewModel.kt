package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.TournamentRepository
import com.example.model.AppCustomization
import com.example.model.AppLogoOption
import com.example.model.AppUser
import com.example.model.BadgeAppeal
import com.example.model.BadgeType
import com.example.model.ChatMessage
import com.example.model.DailyGamerMission
import com.example.model.DailyStreakInfo
import com.example.model.DepositRequest
import com.example.model.GameCategory
import com.example.model.GlobalChatMessage
import com.example.model.LeaderboardPlayer
import com.example.model.MatchResult
import com.example.model.MatchType
import com.example.model.SlotBooking
import com.example.model.SpinHistoryRecord
import com.example.model.TournamentMatch
import com.example.model.TournamentRuleItem
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.WalletPaymentMethod
import com.example.model.WalletTransaction
import com.example.model.WithdrawRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class TournamentTab(val banglaLabel: String) {
    MATCHES("ম্যাচ"),
    MY_MATCHES("বুকিং"),
    WALLET("ওয়ালেট"),
    LEADERBOARD("র‍্যাঙ্ক"),
    GLOBAL_CHAT("গ্লোবাল"),
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

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TournamentViewModel(TournamentRepository.getInstance(appContext)) as T
                }
            }
        }
    }

    val officialBkashNumber: String
        get() = appCustomization.value.officialBkashNumber

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
    val depositRequests: StateFlow<List<DepositRequest>> = repository.depositRequests
    val withdrawRequests: StateFlow<List<WithdrawRequest>> = repository.withdrawRequests
    val leaderboard: StateFlow<List<LeaderboardPlayer>> = repository.leaderboard
    val results: StateFlow<List<MatchResult>> = repository.results
    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatMessages
    val globalChatMessages: StateFlow<List<GlobalChatMessage>> = repository.globalChatMessages
    val hasSpunToday: StateFlow<Boolean> = repository.hasSpunToday
    val hasSpunThisWeek: StateFlow<Boolean> = repository.hasSpunThisWeek
    val spinHistory: StateFlow<List<SpinHistoryRecord>> = repository.spinHistory
    val dailyMissions: StateFlow<List<DailyGamerMission>> = repository.dailyMissions
    val appCustomization: StateFlow<AppCustomization> = repository.appCustomization
    val strictRules: StateFlow<List<TournamentRuleItem>> = repository.strictRules
    val badgeAppeals: StateFlow<List<BadgeAppeal>> = repository.badgeAppeals

    // Dialog States (derived reactively from repository.matches so live edits/refunds reflect immediately)
    private val _bookingMatchId = MutableStateFlow<String?>(null)
    val bookingMatch: StateFlow<TournamentMatch?> = combine(_bookingMatchId, repository.matches) { id, list ->
        if (id == null) null else list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _preselectedSlotNumber = MutableStateFlow<Int?>(null)
    val preselectedSlotNumber: StateFlow<Int?> = _preselectedSlotNumber.asStateFlow()

    private val _roomSlotGridMatchId = MutableStateFlow<String?>(null)
    val roomSlotGridMatch: StateFlow<TournamentMatch?> = combine(_roomSlotGridMatchId, repository.matches) { id, list ->
        if (id == null) null else list.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

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
        _bookingMatchId.value = match.id
        _preselectedSlotNumber.value = slot
    }

    fun closeSlotBooking() {
        _bookingMatchId.value = null
        _preselectedSlotNumber.value = null
    }

    fun openSlotGrid(match: TournamentMatch) {
        _roomSlotGridMatchId.value = match.id
        repository.markMissionCompleted("MATCH")
    }

    fun closeSlotGrid() {
        _roomSlotGridMatchId.value = null
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

    fun sendGlobalChatMessage(
        text: String,
        replyToId: String? = null,
        pinAnnouncement: Boolean = false
    ): Pair<Boolean, String> {
        val res = repository.sendGlobalChatMessage(text, replyToId, pinAnnouncement)
        if (!res.first || pinAnnouncement) {
            _snackbarMessage.value = res.second
        }
        return res
    }

    fun togglePinGlobalChatMessage(messageId: String): Pair<Boolean, String> {
        val res = repository.togglePinGlobalChatMessage(messageId)
        _snackbarMessage.value = res.second
        return res
    }

    fun deleteGlobalChatMessage(messageId: String): Pair<Boolean, String> {
        val res = repository.deleteGlobalChatMessage(messageId)
        _snackbarMessage.value = res.second
        return res
    }

    fun reactToGlobalChatMessage(messageId: String, emoji: String): Boolean {
        return repository.reactToGlobalChatMessage(messageId, emoji)
    }

    fun sendAiMessage(query: String) {
        repository.sendAiAssistantMessage(query)
        repository.markMissionCompleted("AI_CHAT")
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
        val match = bookingMatch.value ?: return
        val success = repository.bookSlotWithBkash(
            match = match,
            slotNumber = slotNumber,
            playerIgn = playerIgn,
            playerUid = playerUid,
            bkashNumber = senderBkash,
            trxId = trxId
        )
        if (success) {
            _bookingMatchId.value = null
            _roomSlotGridMatchId.value = null
            _preselectedSlotNumber.value = null
            _snackbarMessage.value = "পেমেন্ট সনাক্ত হয়েছে! স্লট #$slotNumber নিশ্চিত করা হয়েছে।"
            _currentTab.value = TournamentTab.MY_MATCHES
        } else {
            _snackbarMessage.value = "স্লট বুকিং ব্যর্থ হয়েছে! এই স্লটটি ইতিমধ্যেই পূর্ণ বা ম্যাচটি বন্ধ আছে।"
        }
    }

    fun confirmSlotBookingWithWallet(
        slotNumber: Int,
        playerIgn: String,
        playerUid: String
    ) {
        val match = bookingMatch.value ?: return
        val (success, msg) = repository.bookSlotWithWallet(
            match = match,
            slotNumber = slotNumber,
            playerIgn = playerIgn,
            playerUid = playerUid
        )
        if (success) {
            _bookingMatchId.value = null
            _roomSlotGridMatchId.value = null
            _preselectedSlotNumber.value = null
            _snackbarMessage.value = msg
            _currentTab.value = TournamentTab.MY_MATCHES
        } else {
            _snackbarMessage.value = msg
        }
    }

    fun submitDepositRequest(
        amount: Int,
        paymentMethod: WalletPaymentMethod,
        senderNumber: String,
        trxId: String
    ): Boolean {
        val (success, msg) = repository.submitDepositRequest(amount, paymentMethod, senderNumber, trxId)
        _snackbarMessage.value = msg
        if (success) {
            _showAddMoneyDialog.value = false
        }
        return success
    }

    fun confirmAddMoney(amount: Int, bkashNumber: String, trxId: String) {
        submitDepositRequest(amount, WalletPaymentMethod.BKASH, bkashNumber, trxId)
    }

    fun submitWithdrawRequest(
        amount: Int,
        paymentMethod: WalletPaymentMethod,
        targetNumber: String
    ): Boolean {
        val (success, msg) = repository.submitWithdrawRequest(amount, paymentMethod, targetNumber)
        _snackbarMessage.value = msg
        if (success) {
            _showWithdrawDialog.value = false
        }
        return success
    }

    fun confirmWithdraw(amount: Int, bkashNumber: String) {
        submitWithdrawRequest(amount, WalletPaymentMethod.BKASH, bkashNumber)
    }

    fun adjustUserBalance(
        targetPhoneOrUidOrId: String,
        amount: Int,
        isAdd: Boolean,
        reason: String = "",
        performedBy: String = if (userProfile.value.role == UserRole.OWNER) "ওনার" else "অ্যাডমিন"
    ): Boolean {
        val (success, msg) = repository.adjustUserBalance(
            targetPhoneOrUidOrId = targetPhoneOrUidOrId,
            amount = amount,
            isAdd = isAdd,
            reason = reason,
            performedBy = performedBy
        )
        _snackbarMessage.value = msg
        return success
    }

    fun incrementUserMatchesPlayed(targetPhoneOrUidOrId: String, delta: Int = 1): Boolean {
        val (success, msg) = repository.incrementUserMatchesPlayed(targetPhoneOrUidOrId, delta)
        _snackbarMessage.value = msg
        return success
    }

    fun approveDepositRequest(requestId: String): Boolean {
        val (success, msg) = repository.approveDepositRequest(requestId)
        _snackbarMessage.value = msg
        return success
    }

    fun rejectDepositRequest(requestId: String, reason: String = "ভুয়া বা ভুল TrxID"): Boolean {
        val (success, msg) = repository.rejectDepositRequest(requestId, reason)
        _snackbarMessage.value = msg
        return success
    }

    fun approveWithdrawRequest(requestId: String): Boolean {
        val (success, msg) = repository.approveWithdrawRequest(requestId)
        _snackbarMessage.value = msg
        return success
    }

    fun rejectWithdrawRequest(requestId: String, reason: String = "নম্বর বা তথ্যে ত্রুটি"): Boolean {
        val (success, msg) = repository.rejectWithdrawRequest(requestId, reason)
        _snackbarMessage.value = msg
        return success
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
        } else {
            _snackbarMessage.value = "❌ ভুল নম্বর/UID বা পাসওয়ার্ড! পাসওয়ার্ড ভুলে গেলে Forgot Password ব্যবহার করুন।"
        }
        return success
    }

    fun register(
        accountName: String,
        ign: String,
        uid: String,
        phone: String,
        bkash: String,
        avatarId: String,
        password: String = "123456"
    ): Boolean {
        val success = repository.register(accountName, ign, uid, phone, bkash, avatarId, password = password)
        if (success) {
            _snackbarMessage.value = "🎉 একাউন্ট সফলভাবে তৈরি হয়েছে! ফ্রি ৳${TournamentRepository.SIGNUP_WELCOME_BONUS} ওয়েলকাম বোনাস ওয়ালেটে যুক্ত হয়েছে।"
        } else {
            _snackbarMessage.value = "নিবন্ধন ব্যর্থ হয়েছে! এই নম্বরটি ইতিমধ্যেই নিবন্ধিত অথবা তথ্য অসম্পূর্ণ।"
        }
        return success
    }

    fun requestPasswordResetOtp(phoneOrUid: String): Pair<Boolean, String> {
        val result = repository.requestPasswordResetOtp(phoneOrUid)
        if (result.first) {
            _snackbarMessage.value = "📲 ভেরিফিকেশন কোড (OTP): ${result.second}"
        } else {
            _snackbarMessage.value = result.second
        }
        return result
    }

    fun verifyPasswordResetOtp(phoneOrUid: String, enteredOtp: String): Boolean {
        val ok = repository.verifyPasswordResetOtp(phoneOrUid, enteredOtp)
        if (!ok) {
            _snackbarMessage.value = "ভুল ভেরিফিকেশন কোড! আবার চেষ্টা করুন।"
        }
        return ok
    }

    fun resetPassword(phoneOrUid: String, newPass: String): Boolean {
        val ok = repository.resetPassword(phoneOrUid, newPass)
        if (ok) {
            _snackbarMessage.value = "🎉 পাসওয়ার্ড সফলভাবে রিসেট করা হয়েছে! নতুন পাসওয়ার্ড দিয়ে লগইন করুন।"
        } else {
            _snackbarMessage.value = "পাসওয়ার্ড রিসেট ব্যর্থ হয়েছে! কমপক্ষে ৪ অক্ষরের পাসওয়ার্ড দিন।"
        }
        return ok
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

    fun cancelLegacySlotByNumber(matchId: String, slotNumber: Int): Boolean {
        val success = repository.cancelLegacySlotByNumber(matchId, slotNumber)
        if (success) {
            _snackbarMessage.value = "✅ স্লট #$slotNumber সফলভাবে বাতিল এবং ওয়ালেটে রিফান্ড করা হয়েছে!"
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
        newAvatarId: String,
        newBio: String = userProfile.value.battleTagline,
        newCustomPfpUri: String? = userProfile.value.customPfpUri,
        newCustomBannerUri: String? = userProfile.value.customBannerUri,
        newBannerColorId: String = userProfile.value.bannerColorId
    ) {
        repository.updateProfile(
            accountName = accountName,
            newIgn = newIgn,
            newUid = newUid,
            newPhone = newPhone,
            newBkash = newBkash,
            newAvatarId = newAvatarId,
            newBio = newBio,
            newCustomPfpUri = newCustomPfpUri,
            newCustomBannerUri = newCustomBannerUri,
            newBannerColorId = newBannerColorId
        )
        _showEditProfileDialog.value = false
        _snackbarMessage.value = "প্রোফাইল তথ্য ও ব্যানার সফলভাবে আপডেট হয়েছে!"
    }

    fun updateProfileAppearance(
        customPfpUri: String?,
        customBannerUri: String?,
        bannerColorId: String
    ): Pair<Boolean, String> {
        val result = repository.updateProfileAppearance(customPfpUri, customBannerUri, bannerColorId)
        _snackbarMessage.value = result.second
        return result
    }

    fun updateBio(newBio: String) {
        repository.updateBio(newBio)
        _snackbarMessage.value = "প্রোফাইল বায়ো আপডেট হয়েছে!"
    }

    fun giveProfileKudos(targetKey: String): Int {
        val count = repository.giveProfileKudos(targetKey)
        if (count > 0) {
            _snackbarMessage.value = "🔥 প্লেয়ারকে রেস্পেক্ট (GG) পাঠানো হয়েছে!"
        }
        return count
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

    fun grantBadgeToUser(targetPhoneOrUidOrId: String, badge: BadgeType): Boolean {
        val (success, msg) = repository.grantBadgeToUser(targetPhoneOrUidOrId, badge)
        _snackbarMessage.value = msg
        return success
    }

    fun revokeBadgeFromUser(targetPhoneOrUidOrId: String, badge: BadgeType): Boolean {
        val (success, msg) = repository.revokeBadgeFromUser(targetPhoneOrUidOrId, badge)
        _snackbarMessage.value = msg
        return success
    }

    fun submitBadgeAppeal(requestedBadge: BadgeType, reasonNote: String): Pair<Boolean, String> {
        val result = repository.submitBadgeAppeal(requestedBadge, reasonNote)
        _snackbarMessage.value = result.second
        return result
    }

    fun approveBadgeAppeal(appealId: String, selectedBadgeToGrant: BadgeType): Pair<Boolean, String> {
        val reviewer = if (userProfile.value.role == UserRole.OWNER) "ওনার" else "অ্যাডমিন"
        val result = repository.approveBadgeAppeal(appealId, selectedBadgeToGrant, reviewer)
        _snackbarMessage.value = result.second
        return result
    }

    fun denyBadgeAppeal(
        appealId: String,
        reason: String = "ব্যাজের নির্ধারিত শর্ত পূরণ হয়নি (অসম্পূর্ণ বা মিথ্যা আবেদন)"
    ): Pair<Boolean, String> {
        val reviewer = if (userProfile.value.role == UserRole.OWNER) "ওনার" else "অ্যাডমিন"
        val result = repository.denyBadgeAppeal(appealId, reason, reviewer)
        _snackbarMessage.value = result.second
        return result
    }

    fun sendAppealChatMessage(
        appealId: String,
        text: String,
        proofImageUri: String? = null
    ): Pair<Boolean, String> {
        val result = repository.sendAppealChatMessage(appealId, text, proofImageUri)
        if (!result.first) {
            _snackbarMessage.value = result.second
        }
        return result
    }

    // ==================== A TO Z OWNER APP MANAGEMENT ====================

    fun updateAppBranding(
        appName: String,
        appTagline: String,
        logo: AppLogoOption,
        noticeTicker: String,
        announcement: String
    ) {
        repository.updateAppBranding(appName, appTagline, logo, noticeTicker, announcement)
        _snackbarMessage.value = "👑 অ্যাপের নাম, লোগো ও নোটিশ সফলভাবে আপডেট করা হয়েছে!"
    }

    fun updateFinancialSettings(
        bkash: String,
        nagad: String,
        whatsAppUrl: String,
        minWithdraw: Int,
        maxWithdraw: Int
    ) {
        repository.updateFinancialSettings(bkash, nagad, whatsAppUrl, minWithdraw, maxWithdraw)
        _snackbarMessage.value = "💰 বিকাশ, নগদ ও উইথড্র সেটিংস সফলভাবে সংরক্ষিত হয়েছে!"
    }

    fun toggleMaintenanceMode(enabled: Boolean, message: String) {
        repository.toggleMaintenanceMode(enabled, message)
        _snackbarMessage.value = if (enabled) "⚠️ মেইনটেন্যান্স মোড চালু করা হয়েছে!" else "✅ মেইনটেন্যান্স মোড বন্ধ করা হয়েছে।"
    }

    fun updateExistingMatch(match: TournamentMatch): Boolean {
        val success = repository.updateExistingMatch(match)
        if (success) {
            _snackbarMessage.value = "⚔️ ম্যাচ তথ্য সফলভাবে এডিট ও সেভ করা হয়েছে!"
        }
        return success
    }

    fun deleteMatch(matchId: String): Boolean {
        val success = repository.deleteMatch(matchId)
        if (success) {
            if (_bookingMatchId.value == matchId) _bookingMatchId.value = null
            if (_roomSlotGridMatchId.value == matchId) _roomSlotGridMatchId.value = null
            _snackbarMessage.value = "🗑️ ম্যাচটি সম্পূর্ণ মুছে ফেলা হয়েছে ও স্লট ফি রিফান্ড করা হয়েছে!"
        }
        return success
    }

    fun updateMatchSlotCapacity(matchId: String, newCapacity: Int): Boolean {
        val success = repository.updateMatchSlotCapacity(matchId, newCapacity)
        if (success) {
            _snackbarMessage.value = "🎟️ ম্যাচের মোট স্লট সংখ্যা $newCapacity এ পরিবর্তন করা হয়েছে!"
        }
        return success
    }

    fun createManualSlotBooking(
        matchId: String,
        slotNumber: Int,
        playerIgn: String,
        playerUid: String,
        phoneOrBkash: String,
        feePaid: Int
    ): Boolean {
        val success = repository.createManualSlotBooking(matchId, slotNumber, playerIgn, playerUid, phoneOrBkash, feePaid)
        if (success) {
            _snackbarMessage.value = "✅ স্লট #$slotNumber এ $playerIgn এর জন্য সফলভাবে স্লট ক্রিয়েট করা হয়েছে!"
        } else {
            _snackbarMessage.value = "স্লট #$slotNumber ইতিমধ্যেই পূর্ণ অথবা অবৈধ!"
        }
        return success
    }

    fun addStrictRule(title: String, description: String, penalty: String, emoji: String): Boolean {
        val success = repository.addStrictRule(title, description, penalty, emoji)
        if (success) {
            _snackbarMessage.value = "📜 নতুন টুর্নামেন্ট নিয়ম যুক্ত হয়েছে!"
        }
        return success
    }

    fun updateStrictRule(rule: TournamentRuleItem): Boolean {
        val success = repository.updateStrictRule(rule)
        if (success) {
            _snackbarMessage.value = "📜 নিয়মটি সফলভাবে আপডেট করা হয়েছে!"
        }
        return success
    }

    fun deleteStrictRule(ruleId: String): Boolean {
        val success = repository.deleteStrictRule(ruleId)
        if (success) {
            _snackbarMessage.value = "🗑️ নিয়মটি মুছে ফেলা হয়েছে!"
        }
        return success
    }

    fun resetDefaultRules() {
        repository.resetDefaultRules()
        _snackbarMessage.value = "🔄 ডিফল্ট ৮টি কঠোর টুর্নামেন্ট নিয়মে রিসেট করা হয়েছে!"
    }

    fun resetAppForFreshDownload() {
        repository.resetAppForFreshDownload()
        _bookingMatchId.value = null
        _roomSlotGridMatchId.value = null
        _preselectedSlotNumber.value = null
        _roomDetails.value = null
        _showAddMoneyDialog.value = false
        _showWithdrawDialog.value = false
        _showSettingsDialog.value = false
        _showAdminDialog.value = false
        _showOwnerDialog.value = false
        _currentTab.value = TournamentTab.MATCHES
        _snackbarMessage.value = "🔄 অ্যাপ সম্পূর্ণ ফ্রেশ ডাউনলোড অবস্থায় রিসেট করা হয়েছে!"
    }
}

