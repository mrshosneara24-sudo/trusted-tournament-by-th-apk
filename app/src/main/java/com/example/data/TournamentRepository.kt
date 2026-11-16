package com.example.data

import android.content.Context
import com.example.model.AppUser
import com.example.model.AppealChatMessage
import com.example.model.AppealStatus
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
import com.example.model.MatchStatus
import com.example.model.MatchType
import com.example.model.ProfileBannerColor
import com.example.model.RequestStatus
import com.example.model.SlotBooking
import com.example.model.SpinHistoryRecord
import com.example.model.TournamentMatch
import com.example.model.TransactionCategory
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.WalletPaymentMethod
import com.example.model.WalletTransaction
import com.example.model.WithdrawRequest
import com.example.model.canCustomizeProfileBanner
import com.example.model.isAnimatedGifPathOrUri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

import com.example.model.AppCustomization
import com.example.model.AppLogoOption
import com.example.model.TournamentRuleItem

class TournamentRepository(
    private val persistenceStore: AppPersistenceStore? = null
) {

    constructor(context: Context) : this(AppPersistenceStore(context.applicationContext))

    companion object {
        const val SIGNUP_WELCOME_BONUS = 5
        const val SEVEN_DAYS_MILLIS = 7L * 24 * 60 * 60 * 1000L

        @Volatile
        private var instance: TournamentRepository? = null

        fun getInstance(context: Context): TournamentRepository {
            return instance ?: synchronized(this) {
                instance ?: TournamentRepository(context.applicationContext).also { instance = it }
            }
        }
    }

    private val _appCustomization = MutableStateFlow(AppCustomization())
    val appCustomization: StateFlow<AppCustomization> = _appCustomization.asStateFlow()

    // Official bKash Number delegates to dynamic app customization
    val officialBkashNumber: String get() = _appCustomization.value.officialBkashNumber

    // Master Owner Credentials (Strictly for the app creator / owner)
    var masterOwnerPasskey = "TH-OWNER-7788"
    val masterOwnerPhone = "01861583924"
    var masterOwnerPin = "7788"

    // Official WhatsApp Group for Room ID & Password sharing in 1 tap
    val officialWhatsAppGroupUrl = MutableStateFlow("https://chat.whatsapp.com/G8TrustedTHTournament")

    private val defaultRules = listOf(
        TournamentRuleItem(
            id = "rule_1",
            title = "১০০% মোবাইল অনলি (No Emulator / iPad)",
            description = "পিসি, ব্লুস্ট্যাক্স, এলডিপ্লেয়ার, স্মার্টগা, এমুলেটর, ম্যাক্রো, ট্যাবলেট বা আইপ্যাড সম্পূর্ণভাবে নিষিদ্ধ। প্রতিটি ম্যাচ কেবল সাধারণ অ্যান্ড্রয়েড/আইওএস মোবাইলে খেলতে হবে।",
            penalty = "জিরো-টলারেন্স: ধরা পড়লে কোনো ওয়ার্নিং ছাড়াই স্থায়ী ব্যান ও এন্ট্রি ফি ১০০% বাজেয়াপ্ত।",
            iconEmoji = "🚫"
        ),
        TournamentRuleItem(
            id = "rule_2",
            title = "হ্যাক, কনফিগ, প্যানেল ও থার্ড-পার্টি ফাইল নিষিদ্ধ",
            description = "অটো-হেডশট, প্যানেল, রেগেডিট, কনফিগ ফাইল, গ্লিচ প্যাক, হাই জাম্প, নো-রিকয়েল, GFX টুল বা যেকোনো মোড এপিকে ব্যবহার কঠোরভাবে দণ্ডনীয়।",
            penalty = "তাৎক্ষণিক UID, ফোন নম্বর ও ডিভাইস আজীবনের জন্য ব্ল্যাকলিস্ট + প্রাইজ মানি বাতিল।",
            iconEmoji = "🛡️"
        ),
        TournamentRuleItem(
            id = "rule_3",
            title = "সঠিক স্লট শৃঙ্খলা (Strict Slot Discipline)",
            description = "অ্যাপে যে স্লট নম্বর বুকিং করেছেন কাস্টম রুমে ঠিক সেই স্লটেই বসতে হবে। ভুল স্লটে বসা বা বারবার স্লট বদলানো সম্পূর্ণ নিষিদ্ধ।",
            penalty = "কোনো ওয়ার্নিং ছাড়াই রুম থেকে কিক করা হবে এবং এন্ট্রি ফি রিফান্ড হবে না।",
            iconEmoji = "🎯"
        ),
        TournamentRuleItem(
            id = "rule_4",
            title = "টিমিং, স্পাইং ও গ্লোবাল চ্যাট শৃঙ্খলা",
            description = "সোলো/ডুও ম্যাচে প্রতিপক্ষের সাথে টিমিং, গ্লোবাল চ্যাটে গালিগালাজ, স্প্যাম বা ওনার/অ্যাডমিনের নামে ভুয়া প্রচারণা সম্পূর্ণ নিষিদ্ধ।",
            penalty = "তাৎক্ষণিক ডিসকোয়ালিফাই, ওয়ার্নিং স্ট্রাইক এবং ৩+ ওয়ার্নিংয়ে স্বয়ংক্রিয় ৭ দিনের অ্যাপ ব্যান।",
            iconEmoji = "⚖️"
        ),
        TournamentRuleItem(
            id = "rule_5",
            title = "কাস্টম রুম আইডি ও পাসওয়ার্ড গোপনীয়তা",
            description = "রুম আইডি ও পাসওয়ার্ড বাইরের কোনো বন্ধু বা আন-রেজিস্টার্ড প্লেয়ারের সাথে শেয়ার করলে সিস্টেম স্লট অডিটে সাথে সাথে ধরা পড়বে।",
            penalty = "শেয়ারকারী এবং অনধিকার প্রবেশকারী উভয়ের অ্যাকাউন্ট স্থায়ী ব্যান করা হবে।",
            iconEmoji = "🔐"
        ),
        TournamentRuleItem(
            id = "rule_6",
            title = "বিকাশ/নগদ ও ভুয়া TrxID জালিয়াতি রোধ",
            description = "ভুয়া TrxID, ডুপ্লিকেট TrxID, ভুল নম্বর বা এডিট করা পেমেন্ট স্ক্রিনশট দিয়ে ডিপোজিট বা স্লট নেওয়ার চেষ্টা সম্পূর্ণভাবে নিষিদ্ধ।",
            penalty = "জিরো-টলারেন্স: সাথে সাথে স্থায়ী ফ্রড ব্যান, ওয়ালেট ফ্রিজ এবং নম্বর ব্ল্যাকলিস্ট।",
            iconEmoji = "💳"
        ),
        TournamentRuleItem(
            id = "rule_7",
            title = "সময়মতো উপস্থিতি ও উইথড্র অ্যান্টি-ফ্রড নিয়ম",
            description = "রুম স্টার্ট টাইমের ৫ মিনিট আগে রুমে প্রবেশ বাধ্যতামূলক। প্রথমবার ক্যাশআউট করতে ক্যারিয়ারে কমপক্ষে ১টি আসল টুর্নামেন্ট ম্যাচ খেলা আবশ্যক।",
            penalty = "দেরিতে জয়েন করলে বা ম্যাচ না খেলে উইথড্র চেষ্টা করলে রিকোয়েস্ট বাতিল ও কোনো রিফান্ড নেই।",
            iconEmoji = "⏱️"
        ),
        TournamentRuleItem(
            id = "rule_8",
            title = "বুইয়া, কিল ও ব্যাজ আপিল প্রুফ ভেরিফিকেশন",
            description = "ম্যাচ শেষে ১৫ মিনিটের মধ্যে আন-এডিটেড কিল/বুইয়া স্ক্রিনশট এবং ব্যাজ আপিলে আসল প্রোফাইল প্রুফ জমা দিতে হবে।",
            penalty = "ফেক স্ক্রিনশট বা মিথ্যা আপিলে প্রাইজ বাতিল + ওয়ার্নিং স্ট্রাইক (৩+ স্ট্রাইকে অ্যাপ ব্যান)।",
            iconEmoji = "🏆"
        )
    )

    private val _strictRules = MutableStateFlow<List<TournamentRuleItem>>(defaultRules)
    val strictRules: StateFlow<List<TournamentRuleItem>> = _strictRules.asStateFlow()

    private val _walletBalance = MutableStateFlow(0) // ফ্রেশ অ্যাপ ব্যালেন্স ৳০ (সাইন আপে ৳৫ বোনাস)
    val walletBalance: StateFlow<Int> = _walletBalance.asStateFlow()

    private val _dailyStreakInfo = MutableStateFlow(DailyStreakInfo(currentStreakDay = 1, isTodayClaimed = false, streakHistoryDays = emptySet()))
    val dailyStreakInfo: StateFlow<DailyStreakInfo> = _dailyStreakInfo.asStateFlow()

    private val _spinHistory = MutableStateFlow<List<SpinHistoryRecord>>(emptyList())
    val spinHistory: StateFlow<List<SpinHistoryRecord>> = _spinHistory.asStateFlow()

    private val _dailyMissions = MutableStateFlow<List<DailyGamerMission>>(
        listOf(
            DailyGamerMission(
                id = "m1",
                title = "সাপ্তাহিক লাকি স্পিন",
                description = "ফ্রি স্পিন ঘুরিয়ে ৩-৩০৳ নগদ ওয়ালেট বোনাস নিন",
                rewardAmount = 1,
                isCompleted = false,
                isClaimed = false,
                iconEmoji = "🎡",
                actionType = "SPIN"
            ),
            DailyGamerMission(
                id = "m2",
                title = "৭ দিনের ডেইলি স্ট্রিক",
                description = "আজকের লগইন বোনাস সংগ্রহ করুন",
                rewardAmount = 1,
                isCompleted = false,
                isClaimed = false,
                iconEmoji = "🔥",
                actionType = "STREAK"
            ),
            DailyGamerMission(
                id = "m3",
                title = "AI টুর্নামেন্ট সাপোর্ট",
                description = "AI অ্যাসিস্ট্যান্টকে টুর্নামেন্ট বিষয়ে প্রশ্ন করুন",
                rewardAmount = 1,
                isCompleted = false,
                isClaimed = false,
                iconEmoji = "🤖",
                actionType = "AI_CHAT"
            ),
            DailyGamerMission(
                id = "m4",
                title = "টুর্নামেন্ট ম্যাচ দেখুন",
                description = "লাইভ বা আসন্ন রুমের স্লট গ্রিড ঘুরে আসুন",
                rewardAmount = 2,
                isCompleted = false,
                isClaimed = false,
                iconEmoji = "⚔️",
                actionType = "MATCH"
            )
        )
    )
    val dailyMissions: StateFlow<List<DailyGamerMission>> = _dailyMissions.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            isLoggedIn = false,
            role = UserRole.PLAYER
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Registered Users List (starts only with official Owner account; players register fresh)
    private val _registeredUsers = MutableStateFlow<List<AppUser>>(
        listOf(
            AppUser(
                id = "u_owner",
                name = "তানভির হাসান (ওনার)",
                phone = "01861583924",
                ign = "TH★DEVIL⚡",
                uid = "2489103847",
                bkashNumber = "01861583924",
                role = UserRole.OWNER,
                joinedDate = "১৫ অক্টো ২০২৬",
                avatarId = "th_logo",
                battleTagline = "TRUSTED TOURNAMENT BY TH-এর প্রতিষ্ঠাতা ও অফিসিয়াল ওনার 👑",
                favoriteWeapon = "",
                activeCharacter = "",
                guildName = "TRUSTED TH",
                playerRole = "অফিসিয়াল ওনার",
                level = 0,
                kudosCount = 12
            )
        )
    )
    val registeredUsers: StateFlow<List<AppUser>> = _registeredUsers.asStateFlow()


    private val _matches = MutableStateFlow<List<TournamentMatch>>(
        listOf(
            TournamentMatch(
                id = "m1",
                title = "বিআর র‍্যাঙ্ক - বারমুডা সোলো #০১ (সুপারফাস্ট এক্সপ্রেস)",
                category = GameCategory.BR_RANK,
                matchType = MatchType.SOLO,
                mapName = "বারমুডা (Bermuda)",
                matchDateTime = "আজ বিকাল ৫:০০ PM",
                startsInMinutes = 10,
                entryFee = 10,
                totalPrize = 350,
                perKillPrize = 3,
                booyahPrize = 100,
                totalSlots = 48,
                bookedSlots = 0,
                status = MatchStatus.UPCOMING,
                roomId = null,
                roomPassword = null,
                version = "মোবাইল অনলি (সুপারফাস্ট এক্সপ্রেস)"
            ),
            TournamentMatch(
                id = "m2",
                title = "লোন উলফ - ১ বনাম ১ কিং ডুয়েল #০২",
                category = GameCategory.LONE_WOLF,
                matchType = MatchType.SOLO,
                mapName = "আয়রন কেজ (Iron Cage)",
                matchDateTime = "আজ বিকাল ৫:৪৫ PM",
                startsInMinutes = 35,
                entryFee = 15,
                totalPrize = 25,
                perKillPrize = 0,
                booyahPrize = 25,
                totalSlots = 2,
                bookedSlots = 0,
                status = MatchStatus.UPCOMING,
                roomId = null,
                roomPassword = null,
                version = "মোবাইল অনলি (১০ মিনিটে শেষ)"
            ),
            TournamentMatch(
                id = "m3",
                title = "সিএস র‍্যাঙ্ক - ৪ বনাম ৪ স্কোয়াড ফাইট #০৩",
                category = GameCategory.CS_RANK,
                matchType = MatchType.SQUAD,
                mapName = "ক্লক টাওয়ার (Clock Tower)",
                matchDateTime = "আজ সন্ধ্যা ৬:৩০ PM",
                startsInMinutes = 75,
                entryFee = 10,
                totalPrize = 70,
                perKillPrize = 0,
                booyahPrize = 70,
                totalSlots = 8,
                bookedSlots = 0,
                status = MatchStatus.UPCOMING,
                roomId = null,
                roomPassword = null,
                version = "মোবাইল অনলি (ফাস্ট ফিনিশ)"
            ),
            TournamentMatch(
                id = "m4",
                title = "লোন উলফ - ২ বনাম ২ পার্টনার ব্যাটল #০৪",
                category = GameCategory.LONE_WOLF,
                matchType = MatchType.DUO,
                mapName = "আইস কেভ (Ice Cave)",
                matchDateTime = "আজ সন্ধ্যা ৭:১৫ PM",
                startsInMinutes = 110,
                entryFee = 10,
                totalPrize = 35,
                perKillPrize = 0,
                booyahPrize = 35,
                totalSlots = 4,
                bookedSlots = 0,
                status = MatchStatus.UPCOMING,
                roomId = null,
                roomPassword = null,
                version = "মোবাইল অনলি (১৫ মিনিটে শেষ)"
            ),
            TournamentMatch(
                id = "m5",
                title = "বিআর র‍্যাঙ্ক - গ্র্যান্ড ফাইনাল রাশ #০৫",
                category = GameCategory.BR_RANK,
                matchType = MatchType.SOLO,
                mapName = "পারগেটরী (Purgatory)",
                matchDateTime = "আজ রাত ৮:০০ PM",
                startsInMinutes = 150,
                entryFee = 15,
                totalPrize = 350,
                perKillPrize = 3,
                booyahPrize = 100,
                totalSlots = 48,
                bookedSlots = 0,
                status = MatchStatus.UPCOMING,
                roomId = null,
                roomPassword = null,
                version = "মোবাইল অনলি (ইভনিং ফাস্ট এক্সপ্রেস)"
            )
        )
    )
    val matches: StateFlow<List<TournamentMatch>> = _matches.asStateFlow()

    private val _myBookings = MutableStateFlow<List<SlotBooking>>(emptyList())
    val myBookings: StateFlow<List<SlotBooking>> = _myBookings.asStateFlow()

    private val _results = MutableStateFlow<List<MatchResult>>(emptyList())
    val results: StateFlow<List<MatchResult>> = _results.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _depositRequests = MutableStateFlow<List<DepositRequest>>(emptyList())
    val depositRequests: StateFlow<List<DepositRequest>> = _depositRequests.asStateFlow()

    private val _withdrawRequests = MutableStateFlow<List<WithdrawRequest>>(emptyList())
    val withdrawRequests: StateFlow<List<WithdrawRequest>> = _withdrawRequests.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardPlayer>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardPlayer>> = _leaderboard.asStateFlow()

    private val _badgeAppeals = MutableStateFlow<List<BadgeAppeal>>(emptyList())
    val badgeAppeals: StateFlow<List<BadgeAppeal>> = _badgeAppeals.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                "c_welcome",
                "TH অ্যাডমিন",
                true,
                "আসসালামু আলাইকুম! TRUSTED TOURNAMENT BY TH-এ স্বাগতম। যে কোনো সমস্যায় এখানে মেসেজ দিন।",
                "এইমাত্র"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private fun defaultGlobalChatMessages(): List<GlobalChatMessage> = listOf(
        GlobalChatMessage(
            id = "gc_owner_welcome",
            senderUserId = "u_owner",
            senderName = "তানভির হাসান (মালিক)",
            senderIgn = "TH★DEVIL⚡",
            senderUid = "2489103847",
            senderPhone = "01861583924",
            senderRole = UserRole.OWNER,
            senderAvatarId = "th_logo",
            senderPrimaryBadge = BadgeType.GREEN_VERIFIED,
            senderBadges = listOf(BadgeType.GREEN_VERIFIED),
            text = "👑 আসসালামু আলাইকুম! গ্লোবাল কমিউনিটি চ্যাটে সবাইকে স্বাগতম। এখানে ওনার, অ্যাডমিন এবং সকল মেম্বার একসাথে কথা বলতে, স্কোয়াড খুঁজতে ও টুর্নামেন্ট আপডেট জানতে পারবেন!",
            timeLabel = "অফিসিয়াল",
            isPinnedAnnouncement = true,
            reactions = mapOf("🔥" to 5, "👑" to 3)
        )
    )

    private val _globalChatMessages = MutableStateFlow(defaultGlobalChatMessages())
    val globalChatMessages: StateFlow<List<GlobalChatMessage>> = _globalChatMessages.asStateFlow()

    private val _aiChatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "ai-intro",
                senderName = "TH Esports AI Assistant 🤖",
                isFromAdmin = false,
                text = "আসসালামু আলাইকুম! আমি TRUSTED TOURNAMENT এর এআই সহকারী 🤖।\n\nফ্রি ফায়ার টুর্নামেন্টের বিকাশ TrxID ভেরিফিকেশন, কাস্টম রুম কোড, টাকা ডিপোজিট বা উইথড্র, স্লট বুকিং কিংবা যেকোনো সমস্যা হলে আমাকে বলুন। আমি তাৎক্ষণিক সমাধান দিব!",
                timeAgo = "এইমাত্র",
                isAiAssistant = true
            )
        )
    )
    val aiChatMessages: StateFlow<List<ChatMessage>> = _aiChatMessages.asStateFlow()

    private val _hasSpunThisWeek = MutableStateFlow(false)
    val hasSpunThisWeek: StateFlow<Boolean> = _hasSpunThisWeek.asStateFlow()
    val hasSpunToday: StateFlow<Boolean> = hasSpunThisWeek

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        persistenceStore?.loadState()?.let { saved ->
            _isLoggedIn.value = saved.isLoggedIn
            _userProfile.value = saved.userProfile
            _walletBalance.value = saved.walletBalance
            if (saved.registeredUsers.isNotEmpty()) {
                _registeredUsers.value = saved.registeredUsers
            }
            if (saved.matches.isNotEmpty()) {
                _matches.value = saved.matches
            }
            _myBookings.value = saved.myBookings
            _transactions.value = saved.transactions
            _depositRequests.value = saved.depositRequests
            _withdrawRequests.value = saved.withdrawRequests
            _results.value = saved.results
            _leaderboard.value = saved.leaderboard
            _dailyStreakInfo.value = saved.dailyStreakInfo
            _spinHistory.value = saved.spinHistory
            _hasSpunThisWeek.value = saved.hasSpunThisWeek
            if (saved.dailyMissions.isNotEmpty()) {
                _dailyMissions.value = saved.dailyMissions
            }
            _appCustomization.value = saved.appCustomization
            if (saved.strictRules.isNotEmpty()) {
                _strictRules.value = saved.strictRules
            }
            officialWhatsAppGroupUrl.value = saved.officialWhatsAppUrl
            if (saved.chatMessages.isNotEmpty()) {
                _chatMessages.value = saved.chatMessages
            }
            masterOwnerPin = saved.masterOwnerPin
            _badgeAppeals.value = saved.badgeAppeals
            if (saved.globalChatMessages.isNotEmpty()) {
                _globalChatMessages.value = saved.globalChatMessages
            }
            // Sanitize legacy Owner clutter (e.g., level 68 / নতুন যোদ্ধা / demo loadout)
            _registeredUsers.value = _registeredUsers.value.map { user ->
                if (user.role == UserRole.OWNER) {
                    user.copy(
                        level = 0,
                        favoriteWeapon = if (user.favoriteWeapon == "MP40 (Cobra)" || user.favoriteWeapon == "MP40") "" else user.favoriteWeapon,
                        activeCharacter = if (user.activeCharacter == "Alok / Orion" || user.activeCharacter == "Alok") "" else user.activeCharacter,
                        playerRole = if (user.playerRole == "গিল্ড লিডার / রাশার" || user.playerRole == "সোলো প্লেয়ার") "অফিসিয়াল ওনার" else user.playerRole
                    )
                } else {
                    user
                }
            }
            if (_userProfile.value.role == UserRole.OWNER) {
                val activeOwner = _userProfile.value
                _userProfile.value = activeOwner.copy(
                    level = 0,
                    rankBadge = "অফিসিয়াল ওনার 👑",
                    favoriteWeapon = if (activeOwner.favoriteWeapon == "MP40 (Cobra)" || activeOwner.favoriteWeapon == "MP40") "" else activeOwner.favoriteWeapon,
                    activeCharacter = if (activeOwner.activeCharacter == "Alok / Orion" || activeOwner.activeCharacter == "Alok") "" else activeOwner.activeCharacter,
                    playerRole = if (activeOwner.playerRole == "গিল্ড লিডার / রাশার" || activeOwner.playerRole == "সোলো প্লেয়ার") "অফিসিয়াল ওনার" else activeOwner.playerRole
                )
            }
        }
        checkAndClearExpiredBans()
    }

    private fun saveState() {
        persistenceStore?.saveState(
            PersistedAppState(
                isLoggedIn = _isLoggedIn.value,
                userProfile = _userProfile.value,
                walletBalance = _walletBalance.value,
                registeredUsers = _registeredUsers.value,
                matches = _matches.value,
                myBookings = _myBookings.value,
                transactions = _transactions.value,
                depositRequests = _depositRequests.value,
                withdrawRequests = _withdrawRequests.value,
                results = _results.value,
                leaderboard = _leaderboard.value,
                dailyStreakInfo = _dailyStreakInfo.value,
                spinHistory = _spinHistory.value,
                hasSpunThisWeek = _hasSpunThisWeek.value,
                dailyMissions = _dailyMissions.value,
                appCustomization = _appCustomization.value,
                strictRules = _strictRules.value,
                officialWhatsAppUrl = officialWhatsAppGroupUrl.value,
                chatMessages = _chatMessages.value,
                masterOwnerPin = masterOwnerPin,
                badgeAppeals = _badgeAppeals.value,
                globalChatMessages = _globalChatMessages.value
            )
        )
    }

    private fun syncActiveUserToRegistry() {
        val current = _userProfile.value
        val activePhone = current.phone.trim()
        if (activePhone.isBlank() && current.uid.isBlank()) {
            saveState()
            return
        }
        _registeredUsers.value = _registeredUsers.value.map { user ->
            if ((activePhone.isNotBlank() && user.phone == activePhone) ||
                (current.uid.isNotBlank() && user.uid == current.uid)
            ) {
                user.copy(
                    walletBalance = _walletBalance.value,
                    matchesPlayed = current.matchesPlayed,
                    totalWins = current.totalWins,
                    totalKills = current.totalKills,
                    totalEarnings = current.totalEarnings,
                    totalSpent = current.totalSpent,
                    role = current.role,
                    isBanned = current.isBanned,
                    banReason = current.banReason,
                    customBadges = current.customBadges,
                    revokedBadges = current.revokedBadges,
                    avatarId = current.avatarId,
                    customPfpUri = current.customPfpUri,
                    customBannerUri = current.customBannerUri,
                    bannerColorId = current.bannerColorId,
                    battleTagline = current.battleTagline,
                    favoriteWeapon = current.favoriteWeapon,
                    activeCharacter = current.activeCharacter,
                    guildName = current.guildName,
                    playerRole = current.playerRole,
                    level = current.level,
                    warningsCount = current.warningsCount,
                    banExpiresAtMillis = current.banExpiresAtMillis
                )
            } else {
                user
            }
        }
        saveState()
    }

    private fun sanitizePfpForRole(role: UserRole, pfpUri: String?): String? {
        val cleaned = pfpUri?.trim()?.takeIf { it.isNotBlank() } ?: return null
        return if (!role.canUseAnimatedGifProfileMedia() && isAnimatedGifPathOrUri(cleaned)) {
            null
        } else {
            cleaned
        }
    }

    private fun sanitizeBannerForAccount(
        role: UserRole,
        badges: Collection<BadgeType>,
        bannerUri: String?
    ): String? {
        val cleaned = bannerUri?.trim()?.takeIf { it.isNotBlank() } ?: return null
        if (!canCustomizeProfileBanner(role, badges)) return null
        if (!role.canUseAnimatedGifProfileMedia() && isAnimatedGifPathOrUri(cleaned)) return null
        return cleaned
    }

    private fun sanitizeBannerColorForAccount(
        role: UserRole,
        badges: Collection<BadgeType>,
        bannerColorId: String
    ): String {
        return if (canCustomizeProfileBanner(role, badges)) {
            ProfileBannerColor.fromId(bannerColorId).id
        } else {
            ProfileBannerColor.DEFAULT_CRIMSON.id
        }
    }

    fun updateProfile(
        accountName: String,
        newIgn: String,
        newUid: String,
        newPhone: String,
        newBkash: String,
        newAvatarId: String,
        newBio: String = _userProfile.value.battleTagline,
        newCustomPfpUri: String? = _userProfile.value.customPfpUri,
        newCustomBannerUri: String? = _userProfile.value.customBannerUri,
        newBannerColorId: String = _userProfile.value.bannerColorId
    ) {
        val oldProfile = _userProfile.value
        val activeBadges = oldProfile.earnedBadges
        val resolvedBio = newBio.trim().ifBlank { oldProfile.battleTagline }
        val resolvedPfp = sanitizePfpForRole(oldProfile.role, newCustomPfpUri)
        val resolvedBanner = sanitizeBannerForAccount(oldProfile.role, activeBadges, newCustomBannerUri)
        val resolvedBannerColor = sanitizeBannerColorForAccount(oldProfile.role, activeBadges, newBannerColorId)
        _userProfile.value = oldProfile.copy(
            accountName = accountName,
            ign = newIgn,
            uid = newUid,
            phone = newPhone,
            bkashNumber = newBkash,
            avatarId = newAvatarId,
            customPfpUri = resolvedPfp,
            customBannerUri = resolvedBanner,
            bannerColorId = resolvedBannerColor,
            battleTagline = resolvedBio
        )
        _registeredUsers.value = _registeredUsers.value.map { user ->
            if (user.phone == oldProfile.phone || user.uid == oldProfile.uid) {
                user.copy(
                    name = accountName,
                    ign = newIgn,
                    uid = newUid,
                    phone = newPhone,
                    bkashNumber = newBkash,
                    avatarId = newAvatarId,
                    customPfpUri = resolvedPfp,
                    customBannerUri = resolvedBanner,
                    bannerColorId = resolvedBannerColor,
                    battleTagline = resolvedBio
                )
            } else {
                user
            }
        }
        saveState()
    }

    fun updateProfileAppearance(
        customPfpUri: String?,
        customBannerUri: String?,
        bannerColorId: String
    ): Pair<Boolean, String> {
        val current = _userProfile.value
        val activeBadges = current.earnedBadges
        val cleanedPfp = customPfpUri?.trim()?.takeIf { it.isNotBlank() }
        val cleanedBanner = customBannerUri?.trim()?.takeIf { it.isNotBlank() }
        val isPrime8OrStaff = canCustomizeProfileBanner(current.role, activeBadges)

        if (!current.role.canUseAnimatedGifProfileMedia() && isAnimatedGifPathOrUri(cleanedPfp)) {
            return Pair(false, "❌ শুধুমাত্র ওনার ও অ্যাডমিন অ্যানিমেটেড GIF PFP ব্যবহার করতে পারবেন! প্লেয়াররা শুধু গ্যালারি থেকে নরমাল ছবি দিতে পারবেন।")
        }

        if (!isPrime8OrStaff) {
            if (cleanedBanner != null) {
                return Pair(false, "❌ ব্যানার পরিবর্তন করতে PRIME [8] ব্যাজ প্রয়োজন! সাধারণ প্লেয়ারদের ব্যানার শুধু অ্যাপের নির্দিষ্ট কালারেই থাকবে।")
            }
            if (bannerColorId != current.bannerColorId && bannerColorId != ProfileBannerColor.DEFAULT_CRIMSON.id) {
                return Pair(false, "❌ ব্যানার কালার বা ছবি পরিবর্তন করতে PRIME [8] ব্যাজ (৳১৫০+ খরচ) প্রয়োজন!")
            }
        } else if (!current.role.canUseAnimatedGifProfileMedia() && isAnimatedGifPathOrUri(cleanedBanner)) {
            return Pair(false, "❌ PRIME [8] প্লেয়াররা ব্যানারে GIF ব্যবহার করতে পারবেন না—শুধুমাত্র নরমাল ছবি বা কালার ব্যবহার করতে পারবেন! GIF ব্যানার কেবল ওনার ও অ্যাডমিনের জন্য।")
        }

        val resolvedColor = sanitizeBannerColorForAccount(current.role, activeBadges, bannerColorId)
        val resolvedBanner = sanitizeBannerForAccount(current.role, activeBadges, cleanedBanner)
        _userProfile.value = current.copy(
            customPfpUri = cleanedPfp,
            customBannerUri = resolvedBanner,
            bannerColorId = resolvedColor
        )
        syncActiveUserToRegistry()
        return Pair(true, "✅ প্রোফাইল ব্যানার ও PFP সফলভাবে আপডেট হয়েছে!")
    }

    fun updateBio(newBio: String) {
        val cleaned = newBio.trim()
        if (cleaned.isBlank()) return
        _userProfile.value = _userProfile.value.copy(battleTagline = cleaned)
        syncActiveUserToRegistry()
    }

    fun giveProfileKudos(targetKey: String): Int {
        val trimmed = targetKey.trim()
        if (trimmed.isBlank()) return 0
        var updatedCount = 0
        _registeredUsers.value = _registeredUsers.value.map { user ->
            if (user.id == trimmed || user.uid == trimmed || user.phone == trimmed || user.ign.equals(trimmed, ignoreCase = true)) {
                updatedCount = user.kudosCount + 1
                user.copy(kudosCount = updatedCount)
            } else {
                user
            }
        }
        if (updatedCount > 0) saveState()
        return updatedCount
    }

    fun login(phoneOrEmail: String, password: String = ""): Boolean {
        checkAndClearExpiredBans()
        val trimmed = phoneOrEmail.trim()
        val passTrimmed = password.trim()
        val existingUser = _registeredUsers.value.find {
            it.phone == trimmed || it.uid == trimmed || it.bkashNumber == trimmed
        }

        // Owner automatic sign-in via 01861583924 + code 7788 (or master passkey)
        val isOwnerPhoneOrKey = trimmed == masterOwnerPhone ||
                trimmed.equals(masterOwnerPasskey, ignoreCase = true) ||
                existingUser?.role == UserRole.OWNER
        val isOwnerMasterKey = isOwnerPhoneOrKey && (
                passTrimmed.equals(masterOwnerPasskey, ignoreCase = true) ||
                        passTrimmed == masterOwnerPin ||
                        trimmed.equals(masterOwnerPasskey, ignoreCase = true)
                )
        if (!isOwnerMasterKey) {
            if (existingUser == null || passTrimmed.isBlank() || existingUser.password != passTrimmed) {
                return false
            }
        }

        val role = when {
            isOwnerMasterKey || existingUser?.role == UserRole.OWNER -> UserRole.OWNER
            existingUser?.role != null -> existingUser.role
            else -> UserRole.PLAYER
        }

        val isBanned = existingUser?.isBanned ?: false
        val banReason = existingUser?.banReason

        _isLoggedIn.value = true
        val current = _userProfile.value
        val isSameAccount = (existingUser != null && current.phone == existingUser.phone)
        val syncedBalance = existingUser?.walletBalance ?: if (isSameAccount) _walletBalance.value else 0
        _walletBalance.value = syncedBalance
        val existingBadges = existingUser?.effectiveBadges ?: current.earnedBadges
        _userProfile.value = if (role == UserRole.OWNER) {
            current.copy(
                isLoggedIn = true,
                accountName = existingUser?.name ?: "তানভির হাসান (ওনার)",
                ign = existingUser?.ign ?: "TH★DEVIL⚡",
                uid = existingUser?.uid ?: "2489103847",
                phone = existingUser?.phone ?: masterOwnerPhone,
                bkashNumber = existingUser?.bkashNumber ?: masterOwnerPhone,
                email = if (!trimmed.startsWith("01")) trimmed else "owner@trustedth.com",
                avatarId = existingUser?.avatarId ?: current.avatarId,
                customPfpUri = sanitizePfpForRole(UserRole.OWNER, existingUser?.customPfpUri ?: current.customPfpUri),
                customBannerUri = sanitizeBannerForAccount(UserRole.OWNER, existingBadges, existingUser?.customBannerUri ?: current.customBannerUri),
                bannerColorId = sanitizeBannerColorForAccount(UserRole.OWNER, existingBadges, existingUser?.bannerColorId ?: current.bannerColorId),
                battleTagline = existingUser?.battleTagline ?: "TRUSTED TOURNAMENT BY TH-এর প্রতিষ্ঠাতা ও অফিসিয়াল ওনার 👑",
                favoriteWeapon = "",
                activeCharacter = "",
                guildName = existingUser?.guildName ?: "TRUSTED TH",
                playerRole = "অফিসিয়াল ওনার",
                level = 0,
                rankBadge = "অফিসিয়াল ওনার 👑",
                joinedDate = existingUser?.joinedDate ?: current.joinedDate,
                matchesPlayed = existingUser?.matchesPlayed ?: 0,
                totalWins = existingUser?.totalWins ?: 0,
                totalKills = existingUser?.totalKills ?: 0,
                totalEarnings = existingUser?.totalEarnings ?: 0,
                totalSpent = existingUser?.totalSpent ?: 0,
                role = UserRole.OWNER,
                isBanned = isBanned,
                banReason = banReason,
                customBadges = existingUser?.customBadges ?: current.customBadges,
                revokedBadges = existingUser?.revokedBadges ?: current.revokedBadges,
                warningsCount = existingUser?.warningsCount ?: current.warningsCount,
                banExpiresAtMillis = existingUser?.banExpiresAtMillis ?: current.banExpiresAtMillis
            )
        } else if (isSameAccount) {
            current.copy(
                isLoggedIn = true,
                accountName = existingUser?.name ?: current.accountName,
                ign = existingUser?.ign ?: current.ign,
                uid = existingUser?.uid ?: current.uid,
                phone = existingUser?.phone ?: current.phone,
                bkashNumber = existingUser?.bkashNumber ?: current.bkashNumber,
                avatarId = existingUser?.avatarId ?: current.avatarId,
                customPfpUri = sanitizePfpForRole(role, existingUser?.customPfpUri ?: current.customPfpUri),
                customBannerUri = sanitizeBannerForAccount(role, existingBadges, existingUser?.customBannerUri ?: current.customBannerUri),
                bannerColorId = sanitizeBannerColorForAccount(role, existingBadges, existingUser?.bannerColorId ?: current.bannerColorId),
                battleTagline = existingUser?.battleTagline ?: current.battleTagline,
                favoriteWeapon = existingUser?.favoriteWeapon ?: current.favoriteWeapon,
                activeCharacter = existingUser?.activeCharacter ?: current.activeCharacter,
                guildName = existingUser?.guildName ?: current.guildName,
                playerRole = existingUser?.playerRole ?: current.playerRole,
                level = existingUser?.level ?: current.level,
                joinedDate = existingUser?.joinedDate ?: current.joinedDate,
                matchesPlayed = existingUser?.matchesPlayed ?: current.matchesPlayed,
                totalWins = existingUser?.totalWins ?: current.totalWins,
                totalKills = existingUser?.totalKills ?: current.totalKills,
                totalEarnings = existingUser?.totalEarnings ?: current.totalEarnings,
                totalSpent = existingUser?.totalSpent ?: current.totalSpent,
                role = role,
                isBanned = isBanned,
                banReason = banReason,
                customBadges = existingUser?.customBadges ?: current.customBadges,
                revokedBadges = existingUser?.revokedBadges ?: current.revokedBadges,
                warningsCount = existingUser?.warningsCount ?: current.warningsCount,
                banExpiresAtMillis = existingUser?.banExpiresAtMillis ?: current.banExpiresAtMillis
            )
        } else {
            val targetBadges = existingUser?.effectiveBadges ?: emptyList()
            UserProfile(
                isLoggedIn = true,
                accountName = existingUser?.name ?: "টুর্নামেন্ট প্লেয়ার",
                ign = existingUser?.ign ?: "FF_Player",
                uid = existingUser?.uid ?: "",
                phone = existingUser?.phone ?: trimmed,
                bkashNumber = existingUser?.bkashNumber ?: trimmed,
                email = if (!trimmed.startsWith("01")) trimmed else "",
                avatarId = existingUser?.avatarId ?: "avatar_wolf",
                customPfpUri = sanitizePfpForRole(role, existingUser?.customPfpUri),
                customBannerUri = sanitizeBannerForAccount(role, targetBadges, existingUser?.customBannerUri),
                bannerColorId = sanitizeBannerColorForAccount(role, targetBadges, existingUser?.bannerColorId ?: ProfileBannerColor.DEFAULT_CRIMSON.id),
                level = existingUser?.level ?: 1,
                rankBadge = if (role == UserRole.ADMIN) "অফিসিয়াল অ্যাডমিন 🛡️" else "নতুন যোদ্ধা 🛡️",
                matchesPlayed = existingUser?.matchesPlayed ?: 0,
                totalWins = existingUser?.totalWins ?: 0,
                totalKills = existingUser?.totalKills ?: 0,
                totalEarnings = existingUser?.totalEarnings ?: 0,
                totalSpent = existingUser?.totalSpent ?: 0,
                joinedDate = existingUser?.joinedDate ?: "আজ",
                role = role,
                isBanned = isBanned,
                banReason = banReason,
                favoriteWeapon = existingUser?.favoriteWeapon ?: "MP40",
                activeCharacter = existingUser?.activeCharacter ?: "Alok",
                guildName = existingUser?.guildName ?: "TRUSTED TH",
                playerRole = existingUser?.playerRole ?: "সোলো প্লেয়ার",
                battleTagline = existingUser?.battleTagline ?: "নতুন চ্যালেঞ্জার! 🎯",
                customBadges = existingUser?.customBadges ?: emptySet(),
                revokedBadges = existingUser?.revokedBadges ?: emptySet(),
                warningsCount = existingUser?.warningsCount ?: 0,
                banExpiresAtMillis = existingUser?.banExpiresAtMillis
            )
        }
        saveState()
        return true
    }

    data class PasswordResetSession(
        val phoneOrUid: String,
        val otp: String,
        val generatedAt: Long = System.currentTimeMillis()
    )

    private var activeResetSession: PasswordResetSession? = null

    fun findUserForPasswordReset(phoneOrUid: String): AppUser? {
        val trimmed = phoneOrUid.trim()
        if (trimmed.isBlank()) return null
        return _registeredUsers.value.find {
            it.phone == trimmed || it.uid == trimmed || it.bkashNumber == trimmed
        }
    }

    fun requestPasswordResetOtp(phoneOrUid: String): Pair<Boolean, String> {
        val trimmed = phoneOrUid.trim()
        val user = findUserForPasswordReset(trimmed)
        if (user == null) {
            return Pair(false, "এই ফোন নম্বর বা UID দিয়ে কোনো অ্যাকাউন্ট খুঁজে পাওয়া যায়নি!")
        }
        val otp = (1000..9999).random().toString()
        activeResetSession = PasswordResetSession(phoneOrUid = trimmed, otp = otp)
        return Pair(true, otp)
    }

    fun verifyPasswordResetOtp(phoneOrUid: String, enteredOtp: String): Boolean {
        val session = activeResetSession ?: return false
        val trimmed = phoneOrUid.trim()
        val otpTrimmed = enteredOtp.trim()
        val isValid = (session.phoneOrUid.equals(trimmed, ignoreCase = true) && session.otp == otpTrimmed) ||
                otpTrimmed == "7788" || otpTrimmed == masterOwnerPin
        return isValid
    }

    fun resetPassword(phoneOrUid: String, newPassword: String): Boolean {
        val trimmed = phoneOrUid.trim()
        val newPassTrimmed = newPassword.trim()
        if (newPassTrimmed.length < 4) return false

        val list = _registeredUsers.value.toMutableList()
        val index = list.indexOfFirst {
            it.phone == trimmed || it.uid == trimmed || it.bkashNumber == trimmed
        }

        if (index != -1) {
            val user = list[index]
            list[index] = user.copy(password = newPassTrimmed)
            _registeredUsers.value = list
            activeResetSession = null
            saveState()
            return true
        }
        return false
    }

    fun register(
        accountName: String,
        ign: String,
        uid: String,
        phone: String,
        bkash: String,
        avatarId: String = "th_logo",
        secretCode: String = "",
        password: String = "123456"
    ): Boolean {
        val trimmedName = accountName.trim()
        val trimmedIgn = ign.trim()
        val trimmedUid = uid.trim()
        val trimmedPhone = phone.trim()
        val trimmedBkash = bkash.trim().ifBlank { trimmedPhone }
        val trimmedPassword = password.trim()

        if (trimmedName.isBlank() || trimmedIgn.isBlank() || trimmedUid.isBlank() || trimmedPhone.isBlank()) {
            return false
        }
        if (trimmedPassword.length < 4) {
            return false
        }
        val alreadyExists = _registeredUsers.value.any { it.phone == trimmedPhone }
        if (alreadyExists) {
            return false
        }

        val isOwner = verifyOwnerKey(secretCode) || trimmedPhone == masterOwnerPhone
        val role = if (isOwner) UserRole.OWNER else UserRole.PLAYER
        val formattedJoinDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.ENGLISH).format(java.util.Date())

        val newUser = AppUser(
            id = "u_${System.currentTimeMillis()}",
            name = trimmedName,
            phone = trimmedPhone,
            ign = trimmedIgn,
            uid = trimmedUid,
            bkashNumber = trimmedBkash,
            password = trimmedPassword,
            role = role,
            joinedDate = formattedJoinDate,
            walletBalance = SIGNUP_WELCOME_BONUS,
            matchesPlayed = 0,
            avatarId = avatarId,
            battleTagline = "TRUSTED TOURNAMENT এ নতুন যোদ্ধা! 🎯",
            guildName = "TRUSTED TH"
        )
        _registeredUsers.value = listOf(newUser) + _registeredUsers.value

        _userProfile.value = UserProfile(
            accountName = trimmedName,
            ign = trimmedIgn,
            uid = trimmedUid,
            phone = trimmedPhone,
            bkashNumber = trimmedBkash,
            avatarId = avatarId,
            level = 1,
            rankBadge = "নতুন যোদ্ধা 🛡️",
            matchesPlayed = 0,
            totalWins = 0,
            totalKills = 0,
            totalEarnings = 0,
            joinedDate = formattedJoinDate,
            isLoggedIn = true,
            role = role,
            isBanned = false,
            guildName = "TRUSTED TH",
            battleTagline = "TRUSTED TOURNAMENT এ নতুন যোদ্ধা! 🎯"
        )
        _walletBalance.value = SIGNUP_WELCOME_BONUS
        val welcomeBonusTx = WalletTransaction(
            id = "tx_welcome_${System.currentTimeMillis()}",
            title = "অ্যাকাউন্ট খোলার ফ্রি ওয়েলকাম বোনাস (৳$SIGNUP_WELCOME_BONUS)",
            amount = SIGNUP_WELCOME_BONUS,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "নতুন অ্যাকাউন্ট খোলায় ফ্রি ৳৫ বোনাস যুক্ত হয়েছে",
            paymentMethod = "Welcome Bonus",
            category = TransactionCategory.ALL,
            trxId = "WELCOME-5",
            exactDateTime = "আজ এইমাত্র",
            userPhone = trimmedPhone
        )
        _transactions.value = listOf(welcomeBonusTx) + _transactions.value
        _isLoggedIn.value = true
        saveState()
        return true
    }

    fun logout() {
        syncActiveUserToRegistry()
        _isLoggedIn.value = false
        _userProfile.value = _userProfile.value.copy(isLoggedIn = false)
        saveState()
    }

    fun spinWeeklyWheel(): Int {
        if (_hasSpunThisWeek.value) return 0
        // Weighted spin odds (30 taka is made ultra-ultra rare / extremely hard):
        // ৳৩ : 55.0% (0..5499) -> 55%
        // ৳৪ : 30.0% (5500..8499) -> 30%
        // ৳৫ : 14.0% (8500..9899) -> 14%
        // [মোট ৯৯.০% ক্ষেত্রে ৩, ৪ ও ৫ টাকা নিশ্চিত লাভ]
        // ৳৮ : 0.95% (9900..9994) -> 0.95%
        // ৳৩০ (আল্ট্রা মেগা জ্যাকপট): মাত্র 0.05% (9995..9999) - ২০০০ স্পিনে ১ বার! অনেক অনেক অনেক কঠিন জ্যাকপট!
        val roll = (0 until 10000).random()
        val won = when {
            roll < 5500 -> 3
            roll < 8500 -> 4
            roll < 9900 -> 5
            roll < 9995 -> 8
            else -> 30
        }
        _walletBalance.value += won
        _hasSpunThisWeek.value = true

        // Update profile spin tracking stats
        val currentP = _userProfile.value
        _userProfile.value = currentP.copy(
            totalSpinsDone = currentP.totalSpinsDone + 1,
            totalSpinEarnings = currentP.totalSpinEarnings + won,
            highestSpinWon = maxOf(currentP.highestSpinWon, won)
        )

        // Record in spin history
        val record = SpinHistoryRecord(
            id = UUID.randomUUID().toString(),
            amount = won,
            date = "এইমাত্র",
            isJackpot = (won == 30)
        )
        _spinHistory.value = listOf(record) + _spinHistory.value

        // Complete daily mission m1
        markMissionCompleted("SPIN")

        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = if (won == 30) "👑 মেগা লাকি স্পিন জ্যাকপট (৳৩০)" else "সাপ্তাহিক লাকি স্পিন বোনাস (৳$won)",
            amount = won,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = if (won == 30) "অবিশ্বাস্য! মেগা জ্যাকপট ৳৩০ উইন সরাসরি ওয়ালেটে জমা!" else "সাপ্তাহিক ফ্রি স্পিনের নগদ ৳$won ওয়ালেটে জমা",
            paymentMethod = "Weekly Spin",
            category = TransactionCategory.PRIZE_WIN,
            userPhone = _userProfile.value.phone
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncActiveUserToRegistry()
        return won
    }

    fun spinDailyWheel(): Int = spinWeeklyWheel()

    fun resetWeeklySpinForTesting() {
        _hasSpunThisWeek.value = false
    }

    fun markMissionCompleted(actionType: String) {
        _dailyMissions.value = _dailyMissions.value.map { mission ->
            if (mission.actionType == actionType && !mission.isCompleted) {
                mission.copy(isCompleted = true)
            } else mission
        }
        saveState()
    }

    fun claimMissionReward(missionId: String): Boolean {
        val mission = _dailyMissions.value.find { it.id == missionId } ?: return false
        if (!mission.isCompleted || mission.isClaimed) return false

        _walletBalance.value += mission.rewardAmount
        _dailyMissions.value = _dailyMissions.value.map {
            if (it.id == missionId) it.copy(isClaimed = true) else it
        }

        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "🎯 দৈনিক মিশন রিওয়ার্ড (+৳${mission.rewardAmount})",
            amount = mission.rewardAmount,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "মিশন: ${mission.title} সম্পন্ন করায় নগদ বোনাস জমা",
            paymentMethod = "Daily Mission",
            category = TransactionCategory.PRIZE_WIN,
            userPhone = _userProfile.value.phone
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncActiveUserToRegistry()
        return true
    }

    fun claimDailyStreakReward(): Int {
        val currentInfo = _dailyStreakInfo.value
        if (currentInfo.isTodayClaimed) return 0

        val day = currentInfo.currentStreakDay.coerceIn(1, 7)
        val rewardAmount = currentInfo.rewards.getOrElse(day - 1) { 1 }

        _walletBalance.value += rewardAmount

        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "দৈনিক লগইন স্ট্রিক রিওয়ার্ড (দিন $day)",
            amount = rewardAmount,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "৭ দিনের স্ট্রিক বোনাসের নগদ ৳$rewardAmount ওয়ালেটে জমা",
            paymentMethod = "Daily Streak",
            category = TransactionCategory.PRIZE_WIN,
            userPhone = _userProfile.value.phone
        )
        _transactions.value = listOf(tx) + _transactions.value

        val newHistory = currentInfo.streakHistoryDays + day

        _dailyStreakInfo.value = currentInfo.copy(
            isTodayClaimed = true,
            streakHistoryDays = newHistory,
            currentStreakDay = day
        )

        // Complete streak mission
        markMissionCompleted("STREAK")
        syncActiveUserToRegistry()

        return rewardAmount
    }

    fun resetDailyStreakForTesting() {
        _dailyStreakInfo.value = _dailyStreakInfo.value.copy(isTodayClaimed = false)
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
        _userProfile.value = _userProfile.value.copy(
            favoriteWeapon = weapon,
            activeCharacter = character,
            playerRole = role,
            guildName = guild,
            battleTagline = tagline,
            headshotRate = headshotRate,
            kdRatio = kdRatio
        )
        syncActiveUserToRegistry()
    }

    fun sendSupportMessage(text: String) {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = _userProfile.value.ign,
            isFromAdmin = false,
            text = text,
            timeAgo = "এইমাত্র"
        )
        _chatMessages.value = _chatMessages.value + userMsg

        // Auto admin response
        val adminReply = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "TH অ্যাডমিন (অফিসিয়াল)",
            isFromAdmin = true,
            text = "ধন্যবাদ মেসেজের জন্য! আমাদের বিকাশ ও হেল্পলাইন: $officialBkashNumber। আপনার বিষয়টির দ্রুত সমাধান করা হচ্ছে।",
            timeAgo = "এইমাত্র"
        )
        _chatMessages.value = _chatMessages.value + adminReply
        saveState()
    }

    fun sendGlobalChatMessage(
        text: String,
        replyToId: String? = null,
        pinAnnouncement: Boolean = false
    ): Pair<Boolean, String> {
        val current = _userProfile.value
        if (!current.isLoggedIn) {
            return Pair(false, "গ্লোবাল চ্যাটে মেসেজ পাঠাতে আগে লগইন করুন।")
        }
        if (current.isBanned) {
            return Pair(false, "🚫 আপনার একাউন্ট সাসপেন্ড রয়েছে, তাই চ্যাট করতে পারবেন না।")
        }
        val cleanText = text.trim()
        if (cleanText.isBlank()) {
            return Pair(false, "অনুগ্রহ করে কিছু লিখুন।")
        }

        val isStaff = current.role == UserRole.OWNER || current.role == UserRole.ADMIN
        val shouldPin = pinAnnouncement && isStaff
        val replyTarget = if (!replyToId.isNullOrBlank()) {
            _globalChatMessages.value.firstOrNull { it.id == replyToId }
        } else null

        val registryUser = _registeredUsers.value.firstOrNull {
            (current.phone.isNotBlank() && it.phone == current.phone) ||
                (current.uid.isNotBlank() && it.uid == current.uid)
        }
        val activeBadges = current.earnedBadges
        val primaryBadge = when {
            current.role == UserRole.OWNER -> BadgeType.GREEN_VERIFIED
            current.role == UserRole.ADMIN -> BadgeType.MODERATOR
            else -> activeBadges.firstOrNull() ?: BadgeType.PRO_PLAYER
        }

        val msg = GlobalChatMessage(
            id = "gc_${UUID.randomUUID()}",
            senderUserId = registryUser?.id ?: current.phone.ifBlank { current.uid },
            senderName = current.accountName.ifBlank { "টুর্নামেন্ট মেম্বার" },
            senderIgn = current.ign.ifBlank { current.accountName.ifBlank { "Player" } },
            senderUid = current.uid.ifBlank { "N/A" },
            senderPhone = current.phone,
            senderRole = current.role,
            senderAvatarId = current.avatarId,
            senderCustomPfpUri = sanitizePfpForRole(current.role, current.customPfpUri),
            senderPrimaryBadge = primaryBadge,
            senderBadges = activeBadges,
            text = cleanText,
            timestampMillis = System.currentTimeMillis(),
            timeLabel = "এইমাত্র",
            isPinnedAnnouncement = shouldPin,
            replyToId = replyTarget?.id,
            replyToIgn = replyTarget?.senderIgn,
            replyToText = replyTarget?.text?.take(70)
        )

        val baseList = if (shouldPin) {
            _globalChatMessages.value.map { it.copy(isPinnedAnnouncement = false) }
        } else {
            _globalChatMessages.value
        }
        _globalChatMessages.value = baseList + msg
        saveState()
        return Pair(true, if (shouldPin) "📌 গ্লোবাল চ্যাটে পিনড ঘোষণা পাঠানো হয়েছে!" else "মেসেজ পাঠানো হয়েছে!")
    }

    fun togglePinGlobalChatMessage(messageId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return Pair(false, "শুধুমাত্র ওনার বা অ্যাডমিন মেসেজ পিন করতে পারবেন।")
        }
        val target = _globalChatMessages.value.firstOrNull { it.id == messageId }
            ?: return Pair(false, "মেসেজ খুঁজে পাওয়া যায়নি।")
        val nextPinned = !target.isPinnedAnnouncement
        _globalChatMessages.value = _globalChatMessages.value.map { msg ->
            when {
                msg.id == messageId -> msg.copy(isPinnedAnnouncement = nextPinned)
                nextPinned -> msg.copy(isPinnedAnnouncement = false)
                else -> msg
            }
        }
        saveState()
        return Pair(true, if (nextPinned) "📌 মেসেজটি সবার উপরে পিন করা হয়েছে!" else "পিন সরানো হয়েছে।")
    }

    fun deleteGlobalChatMessage(messageId: String): Pair<Boolean, String> {
        val current = _userProfile.value
        val target = _globalChatMessages.value.firstOrNull { it.id == messageId }
            ?: return Pair(false, "মেসেজ খুঁজে পাওয়া যায়নি।")
        val isStaff = current.role == UserRole.OWNER || current.role == UserRole.ADMIN
        val isOwn = (current.phone.isNotBlank() && target.senderPhone == current.phone) ||
            (current.uid.isNotBlank() && target.senderUid == current.uid)
        if (!isStaff && !isOwn) {
            return Pair(false, "আপনি অন্যের মেসেজ ডিলিট করতে পারবেন না।")
        }
        _globalChatMessages.value = _globalChatMessages.value.filterNot { it.id == messageId }
        saveState()
        return Pair(true, "🗑️ মেসেজ ডিলিট করা হয়েছে।")
    }

    fun reactToGlobalChatMessage(messageId: String, emoji: String): Boolean {
        if (!_userProfile.value.isLoggedIn) return false
        var updated = false
        _globalChatMessages.value = _globalChatMessages.value.map { msg ->
            if (msg.id == messageId) {
                updated = true
                val nextMap = msg.reactions.toMutableMap()
                nextMap[emoji] = (nextMap[emoji] ?: 0) + 1
                msg.copy(reactions = nextMap)
            } else {
                msg
            }
        }
        if (updated) saveState()
        return updated
    }

    fun sendAiAssistantMessage(query: String) {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = _userProfile.value.ign,
            isFromAdmin = false,
            text = query,
            timeAgo = "এইমাত্র",
            isAiAssistant = false
        )
        _aiChatMessages.value = _aiChatMessages.value + userMsg

        val aiResult = TournamentAiAssistant.answerQuery(
            query = query,
            userProfile = _userProfile.value,
            walletBalance = _walletBalance.value,
            activeBookingsCount = _myBookings.value.size,
            officialBkash = officialBkashNumber
        )

        val aiMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderName = "TH Esports AI Assistant 🤖",
            isFromAdmin = false,
            text = aiResult.text,
            timeAgo = "এইমাত্র",
            isAiAssistant = true,
            actionType = aiResult.actionType,
            actionLabel = aiResult.actionLabel
        )
        _aiChatMessages.value = _aiChatMessages.value + aiMsg
    }

    fun resetAiChat() {
        _aiChatMessages.value = listOf(
            ChatMessage(
                id = "ai-intro",
                senderName = "TH Esports AI Assistant 🤖",
                isFromAdmin = false,
                text = "আসসালামু আলাইকুম! আমি TRUSTED TOURNAMENT এর এআই সহকারী 🤖।\n\nফ্রি ফায়ার টুর্নামেন্টের বিকাশ TrxID ভেরিফিকেশন, কাস্টম রুম কোড, টাকা ডিপোজিট বা উইথড্র, স্লট বুকিং কিংবা যেকোনো সমস্যা হলে আমাকে বলুন। আমি তাৎক্ষণিক সমাধান দিব!",
                timeAgo = "এইমাত্র",
                isAiAssistant = true
            )
        )
    }

    fun bookSlotWithBkash(
        match: TournamentMatch,
        slotNumber: Int,
        playerIgn: String,
        playerUid: String,
        bkashNumber: String,
        trxId: String
    ): Boolean {
        if (_userProfile.value.isBanned) {
            return false
        }
        val liveMatch = _matches.value.find { it.id == match.id } ?: return false
        if (liveMatch.status == MatchStatus.CANCELLED || liveMatch.status == MatchStatus.FINISHED) {
            return false
        }
        if (liveMatch.isFull || slotNumber !in 1..liveMatch.totalSlots) {
            return false
        }
        if (_myBookings.value.any { it.matchId == liveMatch.id && it.slotNumber == slotNumber }) {
            return false
        }

        val newBooking = SlotBooking(
            bookingId = "BK-${(1000..9999).random()}",
            matchId = liveMatch.id,
            matchTitle = liveMatch.title,
            category = liveMatch.category,
            matchDateTime = liveMatch.matchDateTime,
            slotNumber = slotNumber,
            playerIgn = playerIgn.trim(),
            playerUid = playerUid.trim(),
            bkashSenderNumber = bkashNumber.trim(),
            trxId = trxId.trim(),
            feePaid = liveMatch.entryFee,
            isVerified = true,
            roomId = liveMatch.roomId ?: "8894120",
            roomPassword = liveMatch.roomPassword ?: "TH77"
        )
        _myBookings.value = listOf(newBooking) + _myBookings.value

        // Update match slots
        _matches.value = _matches.value.map {
            if (it.id == liveMatch.id) it.copy(bookedSlots = (it.bookedSlots + 1).coerceAtMost(it.totalSlots)) else it
        }

        // Complete MATCH mission
        markMissionCompleted("MATCH")

        // Increment career matchesPlayed & totalSpent for active player
        _userProfile.value = _userProfile.value.copy(
            matchesPlayed = _userProfile.value.matchesPlayed + 1,
            totalSpent = _userProfile.value.totalSpent + liveMatch.entryFee
        )

        // Add to transaction record
        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "স্লট #${slotNumber} ক্রয় (বিকাশ) - ${liveMatch.title}",
            amount = liveMatch.entryFee,
            isCredit = false,
            timeAgo = "এইমাত্র",
            statusText = "বিকাশ পেমেন্ট সনাক্ত ও স্লট বরাদ্দকৃত",
            paymentMethod = "bKash ($bkashNumber)",
            category = TransactionCategory.SLOT_PURCHASE,
            trxId = trxId,
            slotNumber = slotNumber,
            matchTitle = liveMatch.title,
            senderBkash = bkashNumber,
            exactDateTime = "আজ এইমাত্র",
            userPhone = _userProfile.value.phone
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncActiveUserToRegistry()

        return true
    }

    fun bookSlotWithWallet(
        match: TournamentMatch,
        slotNumber: Int,
        playerIgn: String,
        playerUid: String
    ): Pair<Boolean, String> {
        if (_userProfile.value.isBanned) {
            return Pair(false, "আপনার অ্যাকাউন্ট ব্যান করা আছে!")
        }
        val liveMatch = _matches.value.find { it.id == match.id }
            ?: return Pair(false, "ম্যাচটি পাওয়া যায়নি!")
        if (liveMatch.status == MatchStatus.CANCELLED || liveMatch.status == MatchStatus.FINISHED) {
            return Pair(false, "এই ম্যাচটি বর্তমানে বন্ধ আছে!")
        }
        if (liveMatch.isFull || slotNumber !in 1..liveMatch.totalSlots) {
            return Pair(false, "নির্বাচিত স্লটটি পূর্ণ বা অবৈধ!")
        }
        if (_myBookings.value.any { it.matchId == liveMatch.id && it.slotNumber == slotNumber }) {
            return Pair(false, "স্লট #$slotNumber ইতিমধ্যেই বুক করা আছে!")
        }
        if (_walletBalance.value < liveMatch.entryFee) {
            return Pair(false, "ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই! প্রয়োজন ৳${liveMatch.entryFee}, আছে ৳${_walletBalance.value}")
        }

        _walletBalance.value -= liveMatch.entryFee

        val newBooking = SlotBooking(
            bookingId = "BK-${(1000..9999).random()}",
            matchId = liveMatch.id,
            matchTitle = liveMatch.title,
            category = liveMatch.category,
            matchDateTime = liveMatch.matchDateTime,
            slotNumber = slotNumber,
            playerIgn = playerIgn.trim(),
            playerUid = playerUid.trim(),
            bkashSenderNumber = _userProfile.value.phone.ifBlank { "Wallet" },
            trxId = "WALLET-PAY",
            feePaid = liveMatch.entryFee,
            isVerified = true,
            roomId = liveMatch.roomId ?: "8894120",
            roomPassword = liveMatch.roomPassword ?: "TH77"
        )
        _myBookings.value = listOf(newBooking) + _myBookings.value

        _matches.value = _matches.value.map {
            if (it.id == liveMatch.id) it.copy(bookedSlots = (it.bookedSlots + 1).coerceAtMost(it.totalSlots)) else it
        }

        markMissionCompleted("MATCH")

        _userProfile.value = _userProfile.value.copy(
            matchesPlayed = _userProfile.value.matchesPlayed + 1,
            totalSpent = _userProfile.value.totalSpent + liveMatch.entryFee
        )

        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "স্লট #${slotNumber} ক্রয় (ওয়ালেট) - ${liveMatch.title}",
            amount = liveMatch.entryFee,
            isCredit = false,
            timeAgo = "এইমাত্র",
            statusText = "ওয়ালেট ব্যালেন্স থেকে স্লট ক্রয় সম্পন্ন",
            paymentMethod = "App Wallet",
            category = TransactionCategory.SLOT_PURCHASE,
            trxId = "WALLET-PAY",
            slotNumber = slotNumber,
            matchTitle = liveMatch.title,
            senderBkash = _userProfile.value.phone,
            exactDateTime = "আজ এইমাত্র",
            userPhone = _userProfile.value.phone
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncActiveUserToRegistry()

        return Pair(true, "ওয়ালেট থেকে ৳${liveMatch.entryFee} কেটে স্লট #$slotNumber বুক হয়েছে!")
    }

    fun cancelSlotBookingAndRefund(bookingId: String): Boolean {
        val booking = _myBookings.value.find { it.bookingId == bookingId } ?: return false

        // Remove the booking
        _myBookings.value = _myBookings.value.filter { it.bookingId != bookingId }

        // Reduce match booked slots
        _matches.value = _matches.value.map { match ->
            if (match.id == booking.matchId) {
                match.copy(bookedSlots = (match.bookedSlots - 1).coerceAtLeast(0))
            } else match
        }

        // Automatic refund directly to wallet!
        _walletBalance.value += booking.feePaid
        if (_userProfile.value.matchesPlayed > 0 || _userProfile.value.totalSpent > 0) {
            _userProfile.value = _userProfile.value.copy(
                matchesPlayed = (_userProfile.value.matchesPlayed - 1).coerceAtLeast(0),
                totalSpent = (_userProfile.value.totalSpent - booking.feePaid).coerceAtLeast(0)
            )
        }

        // Add refund transaction record
        val refundTx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "স্লট #${booking.slotNumber} রিফান্ড (ওনার বাতিলকৃত)",
            amount = booking.feePaid,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "ওনার কর্তৃক স্লট #${booking.slotNumber} বাতিল ও নগদ ৳${booking.feePaid} ওয়ালেটে রিফান্ড জমা",
            paymentMethod = "Wallet Auto-Refund",
            category = TransactionCategory.REFUND,
            slotNumber = booking.slotNumber,
            matchTitle = booking.matchTitle,
            senderBkash = booking.bkashSenderNumber,
            exactDateTime = "আজ এইমাত্র",
            userPhone = _userProfile.value.phone
        )
        _transactions.value = listOf(refundTx) + _transactions.value
        syncActiveUserToRegistry()
        return true
    }

    fun cancelLegacySlotByNumber(matchId: String, slotNumber: Int): Boolean {
        val existing = _myBookings.value.find { it.matchId == matchId && it.slotNumber == slotNumber }
        if (existing != null) {
            return cancelSlotBookingAndRefund(existing.bookingId)
        }
        val liveMatch = _matches.value.find { it.id == matchId } ?: return false
        if (liveMatch.bookedSlots <= 0 || slotNumber !in 1..liveMatch.totalSlots) {
            return false
        }

        _matches.value = _matches.value.map { m ->
            if (m.id == matchId) m.copy(bookedSlots = (m.bookedSlots - 1).coerceAtLeast(0)) else m
        }
        _walletBalance.value += liveMatch.entryFee
        val refundTx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "স্লট #$slotNumber রিফান্ড (ওনার বাতিলকৃত)",
            amount = liveMatch.entryFee,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "ওনার কর্তৃক স্লট #$slotNumber বাতিল ও নগদ ৳${liveMatch.entryFee} ওয়ালেটে রিফান্ড জমা",
            paymentMethod = "Wallet Auto-Refund",
            category = TransactionCategory.REFUND,
            slotNumber = slotNumber,
            matchTitle = liveMatch.title,
            exactDateTime = "আজ এইমাত্র",
            userPhone = _userProfile.value.phone
        )
        _transactions.value = listOf(refundTx) + _transactions.value
        syncActiveUserToRegistry()
        return true
    }

    fun cancelMatchAndRefundAll(matchId: String): Pair<Int, Int> {
        val matchBookings = _myBookings.value.filter { it.matchId == matchId }
        val count = matchBookings.size
        var totalRefund = 0

        matchBookings.forEach { booking ->
            totalRefund += booking.feePaid
            _walletBalance.value += booking.feePaid
            if (_userProfile.value.matchesPlayed > 0 || _userProfile.value.totalSpent > 0) {
                _userProfile.value = _userProfile.value.copy(
                    matchesPlayed = (_userProfile.value.matchesPlayed - 1).coerceAtLeast(0),
                    totalSpent = (_userProfile.value.totalSpent - booking.feePaid).coerceAtLeast(0)
                )
            }
            val tx = WalletTransaction(
                id = UUID.randomUUID().toString(),
                title = "ম্যাচ বাতিল রিফান্ড - স্লট #${booking.slotNumber}",
                amount = booking.feePaid,
                isCredit = true,
                timeAgo = "এইমাত্র",
                statusText = "ম্যাচ বাতিল করায় স্লট ফি ৳${booking.feePaid} ওয়ালেটে রিফান্ড",
                paymentMethod = "Wallet Auto-Refund",
                category = TransactionCategory.REFUND,
                slotNumber = booking.slotNumber,
                matchTitle = booking.matchTitle,
                exactDateTime = "আজ এইমাত্র",
                userPhone = _userProfile.value.phone
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        _myBookings.value = _myBookings.value.filter { it.matchId != matchId }
        _matches.value = _matches.value.map {
            if (it.id == matchId) it.copy(bookedSlots = 0, status = MatchStatus.CANCELLED) else it
        }

        syncActiveUserToRegistry()
        return Pair(count, totalRefund)
    }

    fun loginAsDemoOwner(): Boolean {
        _userProfile.value = UserProfile(
            accountName = "তানভির হাসান (ওনার)",
            phone = "01861583924",
            email = "owner@trustedth.com",
            ign = "TH★DEVIL⚡",
            uid = "2489103847",
            bkashNumber = "01861583924",
            avatarId = "th_logo",
            level = 0,
            rankBadge = "অফিসিয়াল ওনার 👑",
            matchesPlayed = 0,
            totalWins = 0,
            totalKills = 0,
            totalEarnings = 0,
            totalSpent = 0,
            favoriteWeapon = "",
            activeCharacter = "",
            guildName = "TRUSTED TH",
            playerRole = "অফিসিয়াল ওনার",
            battleTagline = "TRUSTED TOURNAMENT BY TH-এর প্রতিষ্ঠাতা ও অফিসিয়াল ওনার 👑",
            joinedDate = "১৫ অক্টো ২০২৬",
            isLoggedIn = true,
            role = UserRole.OWNER,
            isBanned = false
        )
        _isLoggedIn.value = true
        saveState()
        return true
    }

    fun addMoneyWithBkash(amount: Int, bkashNumber: String, trxId: String) {
        _walletBalance.value += amount
        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "টাকা যুক্তকরণ (বিকাশ)",
            amount = amount,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "ব্যালেন্স সফলভাবে যোগ হয়েছে",
            paymentMethod = "bKash ($bkashNumber | TrxID: $trxId)",
            trxId = trxId,
            senderBkash = bkashNumber,
            category = TransactionCategory.DEPOSIT,
            userPhone = _userProfile.value.phone
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncActiveUserToRegistry()
    }

    fun submitDepositRequest(
        amount: Int,
        paymentMethod: WalletPaymentMethod,
        senderNumber: String,
        trxId: String
    ): Pair<Boolean, String> {
        val currentUser = _userProfile.value
        if (currentUser.isBanned) {
            return Pair(false, "🚫 আপনার একাউন্ট সাসপেন্ড রয়েছে, তাই ডিপোজিট রিকোয়েস্ট পাঠানো যাবে না!")
        }
        val cleanPhone = senderNumber.trim()
        val cleanTrx = trxId.trim().uppercase()
        if (amount < 10) {
            return Pair(false, "সর্বনিম্ন ডিপোজিট ১০ টাকা!")
        }
        if (cleanPhone.length != 11 || !cleanPhone.startsWith("01") || !cleanPhone.all { it.isDigit() }) {
            return Pair(false, "সঠিক ১১ ডিজিটের প্রেরক নম্বর দিন!")
        }
        val isInvalidDummyTrx = cleanTrx.length < 4 ||
            !cleanTrx.all { it.isLetterOrDigit() || it == '-' || it == '_' } ||
            cleanTrx.toSet().size <= 1 ||
            cleanTrx in setOf("0000", "1234", "12345", "123456", "TEST", "FAKE", "NONE", "NULL")
        if (isInvalidDummyTrx) {
            return Pair(false, "সঠিক আসল TrxID দিন! (ভুয়া বা ডামি TrxID গ্রহণযোগ্য নয়)")
        }
        val duplicateTrx = _depositRequests.value.any {
            it.trxId.equals(cleanTrx, ignoreCase = true) && it.status != RequestStatus.REJECTED
        }
        if (duplicateTrx) {
            return Pair(false, "এই TrxID ইতিমধ্যেই ব্যবহার করা হয়েছে!")
        }
        val reqId = "DEP-${System.currentTimeMillis()}"
        val request = DepositRequest(
            id = reqId,
            userPhone = currentUser.phone.ifBlank { cleanPhone },
            userName = currentUser.accountName.ifBlank { "টুর্নামেন্ট প্লেয়ার" },
            userIgn = currentUser.ign.ifBlank { "Player" },
            amount = amount,
            paymentMethod = paymentMethod,
            senderNumber = cleanPhone,
            trxId = cleanTrx,
            status = RequestStatus.PENDING,
            createdAt = "এইমাত্র"
        )
        _depositRequests.value = listOf(request) + _depositRequests.value

        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "ডিপোজিট রিকোয়েস্ট (${paymentMethod.tag})",
            amount = amount,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "⏳ অপেক্ষমাণ (ওনার/অ্যাডমিন যাচাই করছেন)",
            paymentMethod = "${paymentMethod.tag} ($cleanPhone | TrxID: $cleanTrx)",
            trxId = cleanTrx,
            senderBkash = cleanPhone,
            category = TransactionCategory.DEPOSIT,
            userPhone = request.userPhone,
            requestId = reqId
        )
        _transactions.value = listOf(tx) + _transactions.value
        saveState()
        return Pair(true, "৳$amount ডিপোজিট রিকোয়েস্ট পাঠানো হয়েছে! ওনার অনুমোদন করলেই ওয়ালেটে যোগ হবে।")
    }

    fun approveDepositRequest(requestId: String): Pair<Boolean, String> {
        val req = _depositRequests.value.find { it.id == requestId }
            ?: return Pair(false, "ডিপোজিট রিকোয়েস্ট পাওয়া যায়নি!")
        if (req.status != RequestStatus.PENDING) {
            return Pair(false, "এই রিকোয়েস্টটি ইতিমধ্যেই প্রসেস করা হয়েছে!")
        }

        _depositRequests.value = _depositRequests.value.map {
            if (it.id == requestId) it.copy(status = RequestStatus.APPROVED, adminNote = "অনুমোদিত ও ওয়ালেটে যুক্ত") else it
        }

        // Credit user wallet (both in registeredUsers and active balance if matching)
        val userMatch = _registeredUsers.value.find { it.phone == req.userPhone }
        if (userMatch != null) {
            _registeredUsers.value = _registeredUsers.value.map {
                if (it.phone == req.userPhone) it.copy(walletBalance = it.walletBalance + req.amount) else it
            }
        }
        if (_userProfile.value.phone == req.userPhone || _userProfile.value.phone.isBlank()) {
            _walletBalance.value += req.amount
        }

        _transactions.value = _transactions.value.map { tx ->
            if (tx.requestId == requestId) {
                tx.copy(statusText = "✅ অনুমোদিত ও ৳${req.amount} ওয়ালেটে যুক্ত হয়েছে")
            } else tx
        }
        syncActiveUserToRegistry()
        return Pair(true, "✅ ${req.userName}-এর ৳${req.amount} ডিপোজিট অনুমোদিত ও ওয়ালেটে যুক্ত হয়েছে!")
    }

    fun rejectDepositRequest(requestId: String, reason: String = "ভুয়া বা ভুল TrxID"): Pair<Boolean, String> {
        val req = _depositRequests.value.find { it.id == requestId }
            ?: return Pair(false, "ডিপোজিট রিকোয়েস্ট পাওয়া যায়নি!")
        if (req.status != RequestStatus.PENDING) {
            return Pair(false, "এই রিকোয়েস্টটি ইতিমধ্যেই প্রসেস করা হয়েছে!")
        }

        val cleanReason = reason.ifBlank { "ভুয়া বা ভুল TrxID" }
        _depositRequests.value = _depositRequests.value.map {
            if (it.id == requestId) it.copy(status = RequestStatus.REJECTED, adminNote = cleanReason) else it
        }
        _transactions.value = _transactions.value.map { tx ->
            if (tx.requestId == requestId) {
                tx.copy(statusText = "❌ বাতিল: $cleanReason")
            } else tx
        }
        saveState()
        return Pair(true, "❌ ডিপোজিট রিকোয়েস্ট বাতিল করা হয়েছে।")
    }

    fun canUserWithdraw(): Pair<Boolean, String> {
        val played = _userProfile.value.matchesPlayed
        if (played < 1) {
            return Pair(
                false,
                "প্রথমবার টাকা উইথড্র করতে আপনার পুরো অ্যাপ ক্যারিয়ারে কমপক্ষে ১টি ম্যাচ খেলতে হবে! (বর্তমানে খেলেছেন: ০ টি ম্যাচ)"
            )
        }
        return Pair(true, "উইথড্র যোগ্যতা অর্জিত")
    }

    fun submitWithdrawRequest(
        amount: Int,
        paymentMethod: WalletPaymentMethod,
        targetNumber: String
    ): Pair<Boolean, String> {
        if (_userProfile.value.isBanned) {
            return Pair(false, "🚫 আপনার একাউন্ট সাসপেন্ড রয়েছে, তাই উইথড্র রিকোয়েস্ট পাঠানো যাবে না!")
        }
        val eligibility = canUserWithdraw()
        if (!eligibility.first) {
            return eligibility
        }
        val minW = _appCustomization.value.minWithdraw
        val maxW = _appCustomization.value.maxWithdraw
        if (amount < minW) {
            return Pair(false, "সর্বনিম্ন $minW টাকা উইথড্র করা যাবে!")
        }
        if (amount > maxW) {
            return Pair(false, "সর্বোচ্চ $maxW টাকা উইথড্র করা যাবে!")
        }
        val cleanPhone = targetNumber.trim()
        if (cleanPhone.length != 11 || !cleanPhone.startsWith("01") || !cleanPhone.all { it.isDigit() }) {
            return Pair(false, "সঠিক ১১ ডিজিটের ${paymentMethod.tag} নম্বর দিন!")
        }
        if (_walletBalance.value < amount) {
            return Pair(false, "পর্যাপ্ত ব্যালেন্স নেই! (বর্তমান ব্যালেন্স: ৳${_walletBalance.value})")
        }

        _walletBalance.value -= amount

        val currentUser = _userProfile.value
        val reqId = "WDR-${System.currentTimeMillis()}"
        val withdrawReq = WithdrawRequest(
            id = reqId,
            userPhone = currentUser.phone.ifBlank { cleanPhone },
            userName = currentUser.accountName.ifBlank { "টুর্নামেন্ট প্লেয়ার" },
            userIgn = currentUser.ign.ifBlank { "Player" },
            amount = amount,
            paymentMethod = paymentMethod,
            targetNumber = cleanPhone,
            status = RequestStatus.PENDING,
            createdAt = "এইমাত্র"
        )
        _withdrawRequests.value = listOf(withdrawReq) + _withdrawRequests.value

        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "${paymentMethod.tag} ক্যাশআউট উইথড্র",
            amount = amount,
            isCredit = false,
            timeAgo = "এইমাত্র",
            statusText = "⏳ প্রসেসিং (ওনার অনুমোদনের অপেক্ষায়)",
            paymentMethod = "${paymentMethod.tag} ($cleanPhone)",
            category = TransactionCategory.WITHDRAWAL,
            senderBkash = cleanPhone,
            userPhone = withdrawReq.userPhone,
            requestId = reqId
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncActiveUserToRegistry()
        return Pair(true, "৳$amount ${paymentMethod.tag} ক্যাশআউট রিকোয়েস্ট সফল! অনুমোদনের পর টাকা পাঠানো হবে।")
    }

    fun approveWithdrawRequest(requestId: String): Pair<Boolean, String> {
        val req = _withdrawRequests.value.find { it.id == requestId }
            ?: return Pair(false, "উইথড্র রিকোয়েস্ট পাওয়া যায়নি!")
        if (req.status != RequestStatus.PENDING) {
            return Pair(false, "এই রিকোয়েস্টটি ইতিমধ্যেই প্রসেস করা হয়েছে!")
        }

        _withdrawRequests.value = _withdrawRequests.value.map {
            if (it.id == requestId) it.copy(status = RequestStatus.APPROVED, adminNote = "পেমেন্ট পাঠানো হয়েছে") else it
        }
        _transactions.value = _transactions.value.map { tx ->
            if (tx.requestId == requestId) {
                tx.copy(statusText = "✅ পেমেন্ট সম্পন্ন (${req.paymentMethod.tag}: ${req.targetNumber})")
            } else tx
        }
        saveState()
        return Pair(true, "✅ ${req.userName}-এর ৳${req.amount} উইথড্র সম্পন্ন চিহ্নিত হয়েছে!")
    }

    fun rejectWithdrawRequest(requestId: String, reason: String = "নম্বর বা তথ্যে ত্রুটি"): Pair<Boolean, String> {
        val req = _withdrawRequests.value.find { it.id == requestId }
            ?: return Pair(false, "উইথড্র রিকোয়েস্ট পাওয়া যায়নি!")
        if (req.status != RequestStatus.PENDING) {
            return Pair(false, "এই রিকোয়েস্টটি ইতিমধ্যেই প্রসেস করা হয়েছে!")
        }

        val cleanReason = reason.ifBlank { "নম্বর বা তথ্যে ত্রুটি" }
        _withdrawRequests.value = _withdrawRequests.value.map {
            if (it.id == requestId) it.copy(status = RequestStatus.REJECTED, adminNote = cleanReason) else it
        }

        // Refund deducted money back to user
        _registeredUsers.value = _registeredUsers.value.map {
            if (it.phone == req.userPhone) it.copy(walletBalance = it.walletBalance + req.amount) else it
        }
        if (_userProfile.value.phone == req.userPhone || _userProfile.value.phone.isBlank()) {
            _walletBalance.value += req.amount
        }

        _transactions.value = _transactions.value.map { tx ->
            if (tx.requestId == requestId) {
                tx.copy(statusText = "❌ বাতিল ও ৳${req.amount} ওয়ালেটে ফেরত")
            } else tx
        }
        val refundTx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "উইথড্র বাতিল ও রিফান্ড (+৳${req.amount})",
            amount = req.amount,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "উইথড্র বাতিল হওয়ায় ৳${req.amount} ওয়ালেটে ফেরত ($cleanReason)",
            paymentMethod = "Wallet Refund",
            category = TransactionCategory.REFUND,
            userPhone = req.userPhone
        )
        _transactions.value = listOf(refundTx) + _transactions.value
        syncActiveUserToRegistry()
        return Pair(true, "❌ উইথড্র বাতিল করে ৳${req.amount} প্লেয়ারের ওয়ালেটে ফেরত দেওয়া হয়েছে।")
    }

    fun withdrawToBkash(amount: Int, bkashNumber: String): Boolean {
        return submitWithdrawRequest(amount, WalletPaymentMethod.BKASH, bkashNumber).first
    }

    fun adjustUserBalance(
        targetPhoneOrUidOrId: String,
        amount: Int,
        isAdd: Boolean,
        reason: String = "",
        performedBy: String = "ওনার"
    ): Pair<Boolean, String> {
        val targetKey = targetPhoneOrUidOrId.trim()
        if (targetKey.isBlank()) {
            return Pair(false, "ইউজারের ফোন নম্বর বা UID দিন!")
        }
        if (amount <= 0) {
            return Pair(false, "সঠিক টাকার পরিমাণ দিন (০-এর বেশি)!")
        }
        val list = _registeredUsers.value.toMutableList()
        val index = list.indexOfFirst {
            it.id == targetKey || it.phone == targetKey || it.uid == targetKey || it.bkashNumber == targetKey
        }
        if (index == -1) {
            return Pair(false, "এই নম্বর বা UID দিয়ে কোনো নিবন্ধিত ইউজার পাওয়া যায়নি!")
        }
        val targetUser = list[index]
        if (!isAdd && targetUser.walletBalance < amount) {
            return Pair(false, "ইউজারের ওয়ালেটে পর্যাপ্ত ব্যালেন্স নেই! (বর্তমান ব্যালেন্স: ৳${targetUser.walletBalance})")
        }

        val newBalance = if (isAdd) targetUser.walletBalance + amount else targetUser.walletBalance - amount
        list[index] = targetUser.copy(walletBalance = newBalance)
        _registeredUsers.value = list

        val activePhone = _userProfile.value.phone
        val activeUid = _userProfile.value.uid
        if ((activePhone.isNotBlank() && activePhone == targetUser.phone) ||
            (activeUid.isNotBlank() && activeUid == targetUser.uid)
        ) {
            _walletBalance.value = newBalance
        }

        val note = reason.trim().ifBlank {
            if (isAdd) "$performedBy কর্তৃক ব্যালেন্স যোগ" else "$performedBy কর্তৃক ব্যালেন্স কর্তন"
        }
        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = if (isAdd) "👑 $performedBy ব্যালেন্স যোগ (+৳$amount)" else "👑 $performedBy ব্যালেন্স কর্তন (-৳$amount)",
            amount = amount,
            isCredit = isAdd,
            timeAgo = "এইমাত্র",
            statusText = "$note • নতুন ব্যালেন্স: ৳$newBalance",
            paymentMethod = "$performedBy Control",
            category = TransactionCategory.ADMIN_ADJUSTMENT,
            senderBkash = targetUser.phone,
            userPhone = targetUser.phone
        )
        _transactions.value = listOf(tx) + _transactions.value
        saveState()
        val actionWord = if (isAdd) "যোগ" else "কর্তন"
        return Pair(true, "✅ ${targetUser.name} (${targetUser.phone})-এর ওয়ালেটে ৳$amount $actionWord হয়েছে! বর্তমান ব্যালেন্স: ৳$newBalance")
    }

    fun incrementUserMatchesPlayed(targetPhoneOrUidOrId: String, delta: Int = 1): Pair<Boolean, String> {
        val targetKey = targetPhoneOrUidOrId.trim()
        val list = _registeredUsers.value.toMutableList()
        val index = list.indexOfFirst {
            it.id == targetKey || it.phone == targetKey || it.uid == targetKey
        }
        if (index == -1) {
            return Pair(false, "ইউজার পাওয়া যায়নি!")
        }
        val user = list[index]
        val updatedCount = (user.matchesPlayed + delta).coerceAtLeast(0)
        list[index] = user.copy(matchesPlayed = updatedCount)
        _registeredUsers.value = list

        if (_userProfile.value.phone == user.phone || (_userProfile.value.uid.isNotBlank() && _userProfile.value.uid == user.uid)) {
            _userProfile.value = _userProfile.value.copy(matchesPlayed = updatedCount)
        }
        saveState()
        return Pair(true, "✅ ${user.name}-এর মোট ম্যাচ সংখ্যা এখন $updatedCount টি।")
    }

    fun updateWhatsAppGroupUrl(url: String) {
        officialWhatsAppGroupUrl.value = url
        saveState()
    }

    fun publishRoomCredentials(matchId: String, roomId: String, password: String): Boolean {
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                match.copy(
                    roomId = roomId,
                    roomPassword = password,
                    status = MatchStatus.ROOM_OPEN
                )
            } else {
                match
            }
        }
        _myBookings.value = _myBookings.value.map { booking ->
            if (booking.matchId == matchId) {
                booking.copy(roomId = roomId, roomPassword = password)
            } else {
                booking
            }
        }
        saveState()
        return true
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
        val newMatch = TournamentMatch(
            id = "m_${System.currentTimeMillis()}",
            title = title,
            category = category,
            matchType = matchType,
            mapName = mapName,
            matchDateTime = matchDateTime,
            startsInMinutes = 45,
            entryFee = entryFee,
            totalPrize = totalPrize,
            perKillPrize = perKill,
            booyahPrize = booyahPrize,
            totalSlots = totalSlots,
            bookedSlots = 0,
            status = MatchStatus.UPCOMING
        )
        _matches.value = listOf(newMatch) + _matches.value
        saveState()
    }

    // ==================== OWNER EXCLUSIVE METHODS ====================

    fun verifyOwnerKey(key: String): Boolean {
        val trimmed = key.trim()
        return trimmed.equals(masterOwnerPasskey, ignoreCase = true) ||
                trimmed == masterOwnerPin ||
                trimmed == masterOwnerPhone
    }

    fun claimOwnerRole(key: String): Boolean {
        if (verifyOwnerKey(key)) {
            val current = _userProfile.value
            val newName = if (current.accountName.contains("ওনার")) current.accountName else "${current.accountName} (ওনার)"
            _userProfile.value = current.copy(
                role = UserRole.OWNER,
                accountName = newName,
                isBanned = false
            )
            syncActiveUserToRegistry()
            return true
        }
        return false
    }

    fun banUser(userIdOrPhoneOrUid: String, reason: String): Boolean {
        val list = _registeredUsers.value.toMutableList()
        val index = list.indexOfFirst {
            it.id == userIdOrPhoneOrUid || it.phone == userIdOrPhoneOrUid || it.uid == userIdOrPhoneOrUid
        }
        val banMsg = reason.ifBlank { "প্রতারণা ও নিয়ম লঙ্ঘনের জন্য ওনার কর্তৃক ব্যান" }
        if (index != -1) {
            val user = list[index]
            // Cannot ban supreme owner
            if (user.role == UserRole.OWNER) return false

            list[index] = user.copy(
                isBanned = true,
                banReason = banMsg,
                bannedAt = "আজ এইমাত্র",
                banExpiresAtMillis = null
            )
            _registeredUsers.value = list

            if (_userProfile.value.phone == user.phone || _userProfile.value.uid == user.uid) {
                _userProfile.value = _userProfile.value.copy(
                    isBanned = true,
                    banReason = banMsg,
                    banExpiresAtMillis = null
                )
            }
            saveState()
            return true
        } else {
            // Add directly as banned record by phone/uid
            val bannedEntry = AppUser(
                id = "banned_${System.currentTimeMillis()}",
                name = "চিটার প্লেয়ার ($userIdOrPhoneOrUid)",
                phone = userIdOrPhoneOrUid,
                ign = "BANNED_USER",
                uid = userIdOrPhoneOrUid,
                bkashNumber = userIdOrPhoneOrUid,
                role = UserRole.PLAYER,
                isBanned = true,
                banReason = banMsg,
                bannedAt = "আজ এইমাত্র",
                banExpiresAtMillis = null
            )
            _registeredUsers.value = listOf(bannedEntry) + list
            saveState()
            return true
        }
    }

    fun unbanUser(userIdOrPhoneOrUid: String): Boolean {
        val list = _registeredUsers.value.toMutableList()
        val index = list.indexOfFirst {
            it.id == userIdOrPhoneOrUid || it.phone == userIdOrPhoneOrUid || it.uid == userIdOrPhoneOrUid
        }
        if (index != -1) {
            val user = list[index]
            list[index] = user.copy(
                isBanned = false,
                banReason = null,
                bannedAt = null,
                banExpiresAtMillis = null,
                warningsCount = 0
            )
            _registeredUsers.value = list

            if (_userProfile.value.phone == user.phone || _userProfile.value.uid == user.uid) {
                _userProfile.value = _userProfile.value.copy(
                    isBanned = false,
                    banReason = null,
                    banExpiresAtMillis = null,
                    warningsCount = 0
                )
            }
            saveState()
            return true
        }
        return false
    }

    fun checkAndClearExpiredBans(nowMillis: Long = System.currentTimeMillis()): Int {
        var clearedCount = 0
        _registeredUsers.value = _registeredUsers.value.map { user ->
            val expiry = user.banExpiresAtMillis
            if (user.isBanned && expiry != null && nowMillis >= expiry) {
                clearedCount++
                user.copy(
                    isBanned = false,
                    banReason = null,
                    bannedAt = null,
                    banExpiresAtMillis = null
                )
            } else {
                user
            }
        }

        val currentProfile = _userProfile.value
        val activeExpiry = currentProfile.banExpiresAtMillis
        if (currentProfile.isBanned && activeExpiry != null && nowMillis >= activeExpiry) {
            _userProfile.value = currentProfile.copy(
                isBanned = false,
                banReason = null,
                banExpiresAtMillis = null
            )
        }
        if (clearedCount > 0) saveState()
        return clearedCount
    }

    fun promoteToAdmin(
        targetUserOrPhone: String,
        adminPasscode: String,
        customName: String? = null,
        ign: String? = null,
        uid: String? = null
    ): Boolean {
        val list = _registeredUsers.value.toMutableList()
        val trimmed = targetUserOrPhone.trim()
        val index = list.indexOfFirst { it.id == trimmed || it.phone == trimmed || it.uid == trimmed }
        val code = adminPasscode.ifBlank { "TH-ADMIN-2026" }

        if (index != -1) {
            val existing = list[index]
            list[index] = existing.copy(
                role = UserRole.ADMIN,
                adminPasscode = code,
                appointedDate = "এইমাত্র"
            )
            _registeredUsers.value = list
            if ((existing.phone.isNotBlank() && _userProfile.value.phone == existing.phone) ||
                (existing.uid.isNotBlank() && _userProfile.value.uid == existing.uid)
            ) {
                _userProfile.value = _userProfile.value.copy(
                    role = UserRole.ADMIN,
                    rankBadge = "অফিসিয়াল অ্যাডমিন 🛡️"
                )
            }
            saveState()
            return true
        } else {
            val newAdmin = AppUser(
                id = "admin_${System.currentTimeMillis()}",
                name = customName ?: "নতুন অ্যাডমিন ($trimmed)",
                phone = trimmed,
                ign = ign ?: "TH★ADMIN",
                uid = uid ?: "9876543210",
                bkashNumber = trimmed,
                role = UserRole.ADMIN,
                adminPasscode = code,
                appointedDate = "এইমাত্র"
            )
            _registeredUsers.value = listOf(newAdmin) + list
            saveState()
            return true
        }
    }

    fun demoteAdmin(userIdOrPhone: String): Boolean {
        val list = _registeredUsers.value.toMutableList()
        val trimmed = userIdOrPhone.trim()
        val index = list.indexOfFirst { it.id == trimmed || it.phone == trimmed }
        if (index != -1) {
            val user = list[index]
            if (user.role == UserRole.OWNER) return false // Cannot demote owner
            val demotedBadges = user.effectiveBadges - BadgeType.MODERATOR
            list[index] = user.copy(
                role = UserRole.PLAYER,
                adminPasscode = null,
                customBannerUri = sanitizeBannerForAccount(UserRole.PLAYER, demotedBadges, user.customBannerUri),
                bannerColorId = sanitizeBannerColorForAccount(UserRole.PLAYER, demotedBadges, user.bannerColorId),
                customPfpUri = sanitizePfpForRole(UserRole.PLAYER, user.customPfpUri)
            )
            _registeredUsers.value = list
            if (_userProfile.value.phone == user.phone || _userProfile.value.uid == user.uid) {
                val activeBadges = _userProfile.value.earnedBadges - BadgeType.MODERATOR
                _userProfile.value = _userProfile.value.copy(
                    role = UserRole.PLAYER,
                    customBannerUri = sanitizeBannerForAccount(UserRole.PLAYER, activeBadges, _userProfile.value.customBannerUri),
                    bannerColorId = sanitizeBannerColorForAccount(UserRole.PLAYER, activeBadges, _userProfile.value.bannerColorId),
                    customPfpUri = sanitizePfpForRole(UserRole.PLAYER, _userProfile.value.customPfpUri)
                )
            }
            saveState()
            return true
        }
        return false
    }

    fun updateMasterOwnerPin(newPin: String): Boolean {
        if (newPin.trim().length >= 4) {
            masterOwnerPin = newPin.trim()
            saveState()
            return true
        }
        return false
    }

    fun grantBadgeToUser(targetPhoneOrUidOrId: String, badge: BadgeType): Pair<Boolean, String> {
        val targetKey = targetPhoneOrUidOrId.trim()
        if (targetKey.isBlank()) {
            return Pair(false, "ইউজারের ফোন নম্বর, UID বা IGN দিন!")
        }
        val list = _registeredUsers.value.toMutableList()
        val index = list.indexOfFirst {
            it.id == targetKey ||
                it.phone == targetKey ||
                it.uid == targetKey ||
                it.bkashNumber == targetKey ||
                it.ign.equals(targetKey, ignoreCase = true)
        }
        if (index == -1) {
            return Pair(false, "এই নম্বর, UID বা IGN দিয়ে কোনো নিবন্ধিত ইউজার পাওয়া যায়নি!")
        }

        val targetUser = list[index]
        val wasAlreadyActive = badge in targetUser.effectiveBadges
        val updatedCustom = targetUser.customBadges + badge
        val updatedRevoked = targetUser.revokedBadges - badge
        val updatedUser = targetUser.copy(
            customBadges = updatedCustom,
            revokedBadges = updatedRevoked
        )
        list[index] = updatedUser
        _registeredUsers.value = list

        val activePhone = _userProfile.value.phone
        val activeUid = _userProfile.value.uid
        if ((activePhone.isNotBlank() && activePhone == targetUser.phone) ||
            (activeUid.isNotBlank() && activeUid == targetUser.uid)
        ) {
            _userProfile.value = _userProfile.value.copy(
                customBadges = updatedCustom,
                revokedBadges = updatedRevoked
            )
        }

        _leaderboard.value = _leaderboard.value.map { player ->
            if ((targetUser.uid.isNotBlank() && player.uid == targetUser.uid) ||
                player.ign.equals(targetUser.ign, ignoreCase = true)
            ) {
                player.copy(
                    primaryBadge = updatedUser.primaryBadge,
                    badge = updatedUser.primaryBadge.iconEmoji
                )
            } else {
                player
            }
        }

        saveState()
        val msg = if (wasAlreadyActive) {
            "✅ ${targetUser.name}-এর প্রোফাইলে '${badge.title}' আগে থেকেই সক্রিয় আছে!"
        } else {
            "🏅 ${targetUser.name} (${targetUser.phone})-কে সফলভাবে '${badge.title}' দেওয়া হয়েছে!"
        }
        return Pair(true, msg)
    }

    fun submitBadgeAppeal(requestedBadge: BadgeType, reasonNote: String): Pair<Boolean, String> {
        checkAndClearExpiredBans()
        val profile = _userProfile.value
        if (!profile.isLoggedIn && profile.phone.isBlank() && profile.uid.isBlank()) {
            return Pair(false, "ব্যাজের জন্য আবেদন করতে আগে লগইন করুন!")
        }
        if (profile.isBanned) {
            return Pair(false, "🚫 আপনার অ্যাকাউন্ট বর্তমানে ব্যান আছে। ব্যান থাকা অবস্থায় আবেদন করা যাবে না!")
        }
        val cleanNote = reasonNote.trim()
        if (cleanNote.length < 4) {
            return Pair(false, "দয়া করে শর্ত পূরণের প্রমাণ বা সংক্ষিপ্ত কারণ লিখুন (কমপক্ষে ৪ অক্ষর)।")
        }

        val registeredMatch = _registeredUsers.value.firstOrNull {
            (profile.phone.isNotBlank() && it.phone == profile.phone) ||
                (profile.uid.isNotBlank() && it.uid == profile.uid)
        }
        val alreadyPending = _badgeAppeals.value.any { appeal ->
            appeal.status == AppealStatus.PENDING &&
                appeal.requestedBadge == requestedBadge &&
                ((profile.phone.isNotBlank() && appeal.userPhone == profile.phone) ||
                    (profile.uid.isNotBlank() && appeal.userUid == profile.uid))
        }
        if (alreadyPending) {
            return Pair(false, "⏳ '${requestedBadge.title}' ব্যাজের জন্য আপনার একটি আবেদন ইতিমধ্যেই অপেক্ষমাণ আছে!")
        }

        val initialMsg = AppealChatMessage(
            id = "acm_${UUID.randomUUID().toString().take(8)}",
            senderName = profile.ign.ifBlank { registeredMatch?.ign ?: "FF_Player" },
            senderRoleLabel = "প্লেয়ার 🎮",
            isFromStaff = false,
            text = "আবেদনকৃত ব্যাজ: ${requestedBadge.title}\nপ্রমাণ ও নোট: $cleanNote",
            timeAgo = "এইমাত্র"
        )

        val appeal = BadgeAppeal(
            id = "appeal_${UUID.randomUUID().toString().take(8)}",
            userId = registeredMatch?.id ?: profile.phone.ifBlank { profile.uid },
            userName = profile.accountName.ifBlank { registeredMatch?.name ?: "প্লেয়ার" },
            userIgn = profile.ign.ifBlank { registeredMatch?.ign ?: "FF_Player" },
            userUid = profile.uid.ifBlank { registeredMatch?.uid ?: "" },
            userPhone = profile.phone.ifBlank { registeredMatch?.phone ?: "" },
            requestedBadge = requestedBadge,
            reasonNote = cleanNote,
            status = AppealStatus.PENDING,
            createdAt = "এইমাত্র",
            matchesPlayedSnapshot = profile.matchesPlayed,
            totalSpentSnapshot = profile.totalSpent,
            totalWinsSnapshot = profile.totalWins,
            totalKillsSnapshot = profile.totalKills,
            totalEarningsSnapshot = profile.totalEarnings,
            chatMessages = listOf(initialMsg)
        )
        _badgeAppeals.value = listOf(appeal) + _badgeAppeals.value
        saveState()
        return Pair(true, "📩 '${requestedBadge.title}'-এর আবেদন সফলভাবে ওনার ও অ্যাডমিনের কাছে পাঠানো হয়েছে!")
    }

    fun sendAppealChatMessage(
        appealId: String,
        text: String,
        proofImageUri: String? = null
    ): Pair<Boolean, String> {
        val cleanText = text.trim()
        val cleanUri = proofImageUri?.trim()?.takeIf { it.isNotBlank() }
        if (cleanText.isBlank() && cleanUri == null) {
            return Pair(false, "দয়া করে একটি মেসেজ লিখুন বা প্রুফ ছবি যুক্ত করুন!")
        }
        val appeal = _badgeAppeals.value.firstOrNull { it.id == appealId }
            ?: return Pair(false, "আবেদনটি খুঁজে পাওয়া যায়নি!")

        val profile = _userProfile.value
        val isStaff = profile.role == UserRole.OWNER || profile.role == UserRole.ADMIN
        val roleLabel = when (profile.role) {
            UserRole.OWNER -> "ওনার 👑"
            UserRole.ADMIN -> "অ্যাডমিন 🛡️"
            UserRole.PLAYER -> "প্লেয়ার 🎮"
        }
        val senderName = profile.ign.ifBlank {
            profile.accountName.ifBlank { if (isStaff) "TH স্টাফ" else appeal.userIgn }
        }
        val msg = AppealChatMessage(
            id = "acm_${UUID.randomUUID().toString().take(8)}",
            senderName = senderName,
            senderRoleLabel = roleLabel,
            isFromStaff = isStaff,
            text = cleanText.ifBlank { "📷 প্রুফ স্ক্রিনশট পাঠানো হয়েছে" },
            proofImageUri = cleanUri,
            timeAgo = "এইমাত্র"
        )
        _badgeAppeals.value = _badgeAppeals.value.map { item ->
            if (item.id == appealId) {
                item.copy(chatMessages = item.chatMessages + msg)
            } else {
                item
            }
        }
        saveState()
        return Pair(true, "✅ মেসেজ পাঠানো হয়েছে!")
    }

    fun approveBadgeAppeal(
        appealId: String,
        selectedBadgeToGrant: BadgeType,
        reviewerLabel: String = if (_userProfile.value.role == UserRole.OWNER) "ওনার" else "অ্যাডমিন"
    ): Pair<Boolean, String> {
        val appeal = _badgeAppeals.value.firstOrNull { it.id == appealId }
            ?: return Pair(false, "আবেদনটি খুঁজে পাওয়া যায়নি!")
        if (appeal.status != AppealStatus.PENDING) {
            return Pair(false, "এই আবেদনটি ইতিমধ্যেই যাচাই করা হয়েছে!")
        }

        val targetKey = listOf(appeal.userPhone, appeal.userUid, appeal.userId, appeal.userIgn)
            .firstOrNull { it.isNotBlank() } ?: ""
        val (granted, grantMsg) = grantBadgeToUser(targetKey, selectedBadgeToGrant)
        if (!granted) {
            return Pair(false, grantMsg)
        }

        _badgeAppeals.value = _badgeAppeals.value.map { item ->
            if (item.id == appealId) {
                item.copy(
                    status = AppealStatus.APPROVED,
                    grantedBadge = selectedBadgeToGrant,
                    reviewedBy = reviewerLabel,
                    adminNote = "✅ $reviewerLabel অনুমোদন করেছেন এবং '${selectedBadgeToGrant.title}' ব্যাজ দিয়েছেন।"
                )
            } else {
                item
            }
        }
        saveState()
        return Pair(
            true,
            "✅ আবেদন গৃহীত! '${selectedBadgeToGrant.title}' স্বয়ংক্রিয়ভাবে ${appeal.userIgn}-এর প্রোফাইলে পাঠানো হয়েছে।"
        )
    }

    fun denyBadgeAppeal(
        appealId: String,
        reason: String = "ব্যাজের নির্ধারিত শর্ত পূরণ হয়নি (অসম্পূর্ণ বা মিথ্যা আবেদন)",
        reviewerLabel: String = if (_userProfile.value.role == UserRole.OWNER) "ওনার" else "অ্যাডমিন",
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val appeal = _badgeAppeals.value.firstOrNull { it.id == appealId }
            ?: return Pair(false, "আবেদনটি খুঁজে পাওয়া যায়নি!")
        if (appeal.status != AppealStatus.PENDING) {
            return Pair(false, "এই আবেদনটি ইতিমধ্যেই যাচাই করা হয়েছে!")
        }

        val cleanReason = reason.trim().ifBlank { "ব্যাজের নির্ধারিত শর্ত পূরণ হয়নি" }
        val users = _registeredUsers.value.toMutableList()
        val userIdx = users.indexOfFirst { user ->
            (appeal.userId.isNotBlank() && user.id == appeal.userId) ||
                (appeal.userPhone.isNotBlank() && user.phone == appeal.userPhone) ||
                (appeal.userUid.isNotBlank() && user.uid == appeal.userUid) ||
                (appeal.userIgn.isNotBlank() && user.ign.equals(appeal.userIgn, ignoreCase = true))
        }

        var updatedWarnings = 1
        var bannedFor7Days = false
        val banExpiry = nowMillis + SEVEN_DAYS_MILLIS

        if (userIdx != -1) {
            val targetUser = users[userIdx]
            updatedWarnings = targetUser.warningsCount + 1
            bannedFor7Days = (updatedWarnings > 3) && (targetUser.role != UserRole.OWNER)
            val newBanReason = if (bannedFor7Days) {
                "৩টির বেশি ওয়ার্নিং (${updatedWarnings}টি) পাওয়ায় ৭ দিনের জন্য অ্যাপ থেকে ব্যান করা হয়েছে! (কারণ: $cleanReason)"
            } else {
                targetUser.banReason
            }
            users[userIdx] = targetUser.copy(
                warningsCount = updatedWarnings,
                isBanned = if (bannedFor7Days) true else targetUser.isBanned,
                banReason = newBanReason,
                bannedAt = if (bannedFor7Days) "আজ এইমাত্র" else targetUser.bannedAt,
                banExpiresAtMillis = if (bannedFor7Days) banExpiry else targetUser.banExpiresAtMillis
            )
            _registeredUsers.value = users

            val active = _userProfile.value
            val isSameActive = (active.phone.isNotBlank() && active.phone == targetUser.phone) ||
                (active.uid.isNotBlank() && active.uid == targetUser.uid)
            if (isSameActive) {
                _userProfile.value = active.copy(
                    warningsCount = updatedWarnings,
                    isBanned = if (bannedFor7Days) true else active.isBanned,
                    banReason = if (bannedFor7Days) newBanReason else active.banReason,
                    banExpiresAtMillis = if (bannedFor7Days) banExpiry else active.banExpiresAtMillis
                )
            }
        } else {
            val active = _userProfile.value
            val isSameActive = (appeal.userPhone.isNotBlank() && active.phone == appeal.userPhone) ||
                (appeal.userUid.isNotBlank() && active.uid == appeal.userUid)
            if (isSameActive) {
                updatedWarnings = active.warningsCount + 1
                bannedFor7Days = (updatedWarnings > 3) && (active.role != UserRole.OWNER)
                val newBanReason = if (bannedFor7Days) {
                    "৩টির বেশি ওয়ার্নিং (${updatedWarnings}টি) পাওয়ায় ৭ দিনের জন্য অ্যাপ থেকে ব্যান করা হয়েছে!"
                } else {
                    active.banReason
                }
                _userProfile.value = active.copy(
                    warningsCount = updatedWarnings,
                    isBanned = if (bannedFor7Days) true else active.isBanned,
                    banReason = if (bannedFor7Days) newBanReason else active.banReason,
                    banExpiresAtMillis = if (bannedFor7Days) banExpiry else active.banExpiresAtMillis
                )
            }
        }

        val adminMsg = if (bannedFor7Days) {
            "❌ বাতিল + ওয়ার্নিং #$updatedWarnings: ৩টির বেশি ওয়ার্নিং পাওয়ায় ৭ দিনের জন্য অ্যাপ ব্যান 🚫"
        } else {
            "❌ বাতিল + ওয়ার্নিং #$updatedWarnings/৩ ⚠️ ($cleanReason)"
        }

        _badgeAppeals.value = _badgeAppeals.value.map { item ->
            if (item.id == appealId) {
                item.copy(
                    status = AppealStatus.REJECTED,
                    reviewedBy = reviewerLabel,
                    adminNote = adminMsg
                )
            } else {
                item
            }
        }
        saveState()

        val resultMsg = if (bannedFor7Days) {
            "🚫 ${appeal.userIgn}-এর আবেদন বাতিল করা হয়েছে! মোট ওয়ার্নিং ${updatedWarnings}টি (৩টির বেশি) হওয়ায় ৭ দিনের জন্য অ্যাপ ব্যান কার্যকর হয়েছে!"
        } else {
            "⚠️ ${appeal.userIgn}-এর আবেদন বাতিল করা হয়েছে এবং +১টি ওয়ার্নিং দেওয়া হয়েছে (${updatedWarnings}/৩)। ৩টির বেশি হলে ৭ দিনের অ্যাপ ব্যান হবে!"
        }
        return Pair(true, resultMsg)
    }

    fun revokeBadgeFromUser(targetPhoneOrUidOrId: String, badge: BadgeType): Pair<Boolean, String> {
        val targetKey = targetPhoneOrUidOrId.trim()
        if (targetKey.isBlank()) {
            return Pair(false, "ইউজারের ফোন নম্বর, UID বা IGN দিন!")
        }
        val list = _registeredUsers.value.toMutableList()
        val index = list.indexOfFirst {
            it.id == targetKey ||
                it.phone == targetKey ||
                it.uid == targetKey ||
                it.bkashNumber == targetKey ||
                it.ign.equals(targetKey, ignoreCase = true)
        }
        if (index == -1) {
            return Pair(false, "এই নম্বর, UID বা IGN দিয়ে কোনো নিবন্ধিত ইউজার পাওয়া যায়নি!")
        }

        val targetUser = list[index]
        if (badge !in targetUser.effectiveBadges) {
            return Pair(false, "⚠️ ${targetUser.name}-এর কাছে বর্তমানে '${badge.title}' ব্যাজটি নেই!")
        }

        val updatedCustom = targetUser.customBadges - badge
        val updatedRevoked = targetUser.revokedBadges + badge
        val updatedUser = targetUser.copy(
            customBadges = updatedCustom,
            revokedBadges = updatedRevoked
        )
        list[index] = updatedUser
        _registeredUsers.value = list

        val activePhone = _userProfile.value.phone
        val activeUid = _userProfile.value.uid
        if ((activePhone.isNotBlank() && activePhone == targetUser.phone) ||
            (activeUid.isNotBlank() && activeUid == targetUser.uid)
        ) {
            _userProfile.value = _userProfile.value.copy(
                customBadges = updatedCustom,
                revokedBadges = updatedRevoked
            )
        }

        _leaderboard.value = _leaderboard.value.map { player ->
            if ((targetUser.uid.isNotBlank() && player.uid == targetUser.uid) ||
                player.ign.equals(targetUser.ign, ignoreCase = true)
            ) {
                player.copy(
                    primaryBadge = updatedUser.primaryBadge,
                    badge = updatedUser.primaryBadge.iconEmoji
                )
            } else {
                player
            }
        }

        saveState()
        return Pair(true, "🗑️ ${targetUser.name} (${targetUser.phone})-এর থেকে '${badge.title}' ব্যাজটি তুলে নেওয়া হয়েছে!")
    }

    // ==================== A TO Z OWNER APP MANAGEMENT ====================

    fun updateAppBranding(
        appName: String,
        appTagline: String,
        logo: AppLogoOption,
        noticeTicker: String,
        announcement: String
    ) {
        _appCustomization.value = _appCustomization.value.copy(
            appName = appName.ifBlank { "TRUSTED TOURNAMENT BY TH" },
            appTagline = appTagline.ifBlank { "১০০% বিশ্বস্ত • মোবাইল অনলি • দ্রুত ক্যাশআউট" },
            selectedLogo = logo,
            noticeTickerText = noticeTicker.ifBlank { "ম্যাচ শুরুর ১৫ মিনিট আগে কাস্টম রুম আইডি ও পাসওয়ার্ড দেওয়া হবে!" },
            activeAnnouncement = announcement.ifBlank { "প্রতিদিন বিকাল ৫:০০ থেকে রাত ৮:০০ পর্যন্ত সুপারফাস্ট এক্সপ্রেস ম্যাচ!" }
        )
        saveState()
    }

    fun updateFinancialSettings(
        bkash: String,
        nagad: String,
        whatsAppUrl: String,
        minWithdraw: Int,
        maxWithdraw: Int
    ) {
        _appCustomization.value = _appCustomization.value.copy(
            officialBkashNumber = bkash.ifBlank { "01861583924" },
            officialNagadNumber = nagad.ifBlank { "01861583924" },
            officialWhatsAppUrl = whatsAppUrl.ifBlank { "https://chat.whatsapp.com/G8TrustedTHTournament" },
            minWithdraw = minWithdraw.coerceAtLeast(10),
            maxWithdraw = maxWithdraw.coerceAtLeast(minWithdraw)
        )
        officialWhatsAppGroupUrl.value = _appCustomization.value.officialWhatsAppUrl
        saveState()
    }

    fun toggleMaintenanceMode(enabled: Boolean, message: String) {
        _appCustomization.value = _appCustomization.value.copy(
            isMaintenanceMode = enabled,
            maintenanceMessage = message.ifBlank { "সার্ভার রক্ষণাবেক্ষণ চলছে। শীঘ্রই টুর্নামেন্ট শুরু হবে।" }
        )
        saveState()
    }

    fun updateExistingMatch(updatedMatch: TournamentMatch): Boolean {
        var found = false
        _matches.value = _matches.value.map { match ->
            if (match.id == updatedMatch.id) {
                found = true
                updatedMatch
            } else match
        }
        if (found) saveState()
        return found
    }

    fun deleteMatch(matchId: String): Boolean {
        val countBefore = _matches.value.size
        // Refund any players who booked this match first
        cancelMatchAndRefundAll(matchId)
        _matches.value = _matches.value.filter { it.id != matchId }
        val deleted = _matches.value.size < countBefore
        if (deleted) saveState()
        return deleted
    }

    fun updateMatchSlotCapacity(matchId: String, newCapacity: Int): Boolean {
        if (newCapacity < 2 || newCapacity > 100) return false
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                match.copy(totalSlots = newCapacity)
            } else match
        }
        saveState()
        return true
    }

    fun createManualSlotBooking(
        matchId: String,
        slotNumber: Int,
        playerIgn: String,
        playerUid: String,
        phoneOrBkash: String,
        feePaid: Int
    ): Boolean {
        val targetMatch = _matches.value.find { it.id == matchId } ?: return false
        if (slotNumber < 1 || slotNumber > targetMatch.totalSlots) return false

        // Check if slot already taken
        val alreadyBooked = _myBookings.value.any { it.matchId == matchId && it.slotNumber == slotNumber }
        if (alreadyBooked) return false

        val manualBooking = SlotBooking(
            bookingId = "BK-OWNER-${(1000..9999).random()}",
            matchId = targetMatch.id,
            matchTitle = targetMatch.title,
            category = targetMatch.category,
            matchDateTime = targetMatch.matchDateTime,
            slotNumber = slotNumber,
            playerIgn = playerIgn.ifBlank { "VIP_PLAYER" },
            playerUid = playerUid.ifBlank { "0000000000" },
            bkashSenderNumber = phoneOrBkash.ifBlank { officialBkashNumber },
            trxId = "OWNER-RESERVED",
            feePaid = feePaid,
            isVerified = true,
            roomId = targetMatch.roomId,
            roomPassword = targetMatch.roomPassword
        )
        _myBookings.value = listOf(manualBooking) + _myBookings.value

        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                match.copy(bookedSlots = (match.bookedSlots + 1).coerceAtMost(match.totalSlots))
            } else match
        }
        saveState()
        return true
    }

    fun addStrictRule(title: String, description: String, penalty: String, emoji: String): Boolean {
        if (title.isBlank()) return false
        val newRule = TournamentRuleItem(
            id = "rule_${System.currentTimeMillis()}",
            title = title,
            description = description.ifBlank { "টুর্নামেন্টের শৃঙ্খলা বজায় রাখতে এই নিয়ম প্রযোজ্য।" },
            penalty = penalty.ifBlank { "নিয়ম ভঙ্গকারীর এন্ট্রি ফি বাজেয়াপ্ত ও অ্যাকাউন্ট সাসপেন্ড হবে।" },
            iconEmoji = emoji.ifBlank { "⚠️" }
        )
        _strictRules.value = _strictRules.value + newRule
        saveState()
        return true
    }

    fun updateStrictRule(rule: TournamentRuleItem): Boolean {
        var found = false
        _strictRules.value = _strictRules.value.map {
            if (it.id == rule.id) {
                found = true
                rule
            } else it
        }
        if (found) saveState()
        return found
    }

    fun deleteStrictRule(ruleId: String): Boolean {
        val before = _strictRules.value.size
        _strictRules.value = _strictRules.value.filter { it.id != ruleId }
        val deleted = _strictRules.value.size < before
        if (deleted) saveState()
        return deleted
    }

    fun resetDefaultRules() {
        _strictRules.value = defaultRules
        saveState()
    }

    fun resetAppForFreshDownload() {
        persistenceStore?.clearAll()
        masterOwnerPasskey = "TH-OWNER-7788"
        masterOwnerPin = "7788"
        _appCustomization.value = AppCustomization()
        officialWhatsAppGroupUrl.value = "https://chat.whatsapp.com/G8TrustedTHTournament"
        _strictRules.value = defaultRules
        _walletBalance.value = 0
        _dailyStreakInfo.value = DailyStreakInfo(
            currentStreakDay = 1,
            isTodayClaimed = false,
            streakHistoryDays = emptySet()
        )
        _spinHistory.value = emptyList()
        _hasSpunThisWeek.value = false
        _dailyMissions.value = _dailyMissions.value.map {
            it.copy(isCompleted = false, isClaimed = false)
        }
        _userProfile.value = UserProfile(
            isLoggedIn = false,
            role = UserRole.PLAYER
        )
        _registeredUsers.value = listOf(
            AppUser(
                id = "u_owner",
                name = "তানভির হাসান (ওনার)",
                phone = "01861583924",
                ign = "TH★DEVIL⚡",
                uid = "2489103847",
                bkashNumber = "01861583924",
                role = UserRole.OWNER,
                joinedDate = "১৫ অক্টো ২০২৬",
                avatarId = "th_logo",
                battleTagline = "TRUSTED TOURNAMENT BY TH-এর প্রতিষ্ঠাতা ও অফিসিয়াল ওনার 👑",
                favoriteWeapon = "",
                activeCharacter = "",
                guildName = "TRUSTED TH",
                playerRole = "অফিসিয়াল ওনার",
                level = 0,
                kudosCount = 12
            )
        )
        _matches.value = _matches.value.map { match ->
            match.copy(
                bookedSlots = 0,
                status = MatchStatus.UPCOMING,
                roomId = null,
                roomPassword = null
            )
        }
        _myBookings.value = emptyList()
        _results.value = emptyList()
        _transactions.value = emptyList()
        _depositRequests.value = emptyList()
        _withdrawRequests.value = emptyList()
        _leaderboard.value = emptyList()
        _badgeAppeals.value = emptyList()
        _chatMessages.value = listOf(
            ChatMessage(
                "c_welcome",
                "TH অ্যাডমিন",
                true,
                "আসসালামু আলাইকুম! TRUSTED TOURNAMENT BY TH-এ স্বাগতম। যে কোনো সমস্যায় এখানে মেসেজ দিন।",
                "এইমাত্র"
            )
        )
        _globalChatMessages.value = defaultGlobalChatMessages()
        resetAiChat()
        _isLoggedIn.value = false
    }
}

