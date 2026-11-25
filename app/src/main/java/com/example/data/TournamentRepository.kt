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
import com.example.model.BonusGiftDrop
import com.example.model.DailyTriviaProgress
import com.example.model.FreeFireTriviaBank
import com.example.model.GiveawayEntry
import com.example.model.GiveawayEvent
import com.example.model.GiveawayPrizeType
import com.example.model.GiveawayStatus
import com.example.model.GiveawayWinner
import com.example.model.LiveCommunityPoll
import com.example.model.MatchPresetTemplate
import com.example.model.ModerationActionType
import com.example.model.ModerationLogEntry
import com.example.model.PollOptionItem
import com.example.model.PromoCodeItem
import com.example.model.ReflexGameStats
import com.example.model.TournamentRuleItem
import com.example.model.UserPreferences
import com.example.model.formatMatchClockTimeLabel

class TournamentRepository(
    private val persistenceStore: AppPersistenceStore? = null,
    private val appContext: Context? = null
) {

    constructor(context: Context) : this(
        persistenceStore = AppPersistenceStore(context.applicationContext),
        appContext = context.applicationContext
    )

    companion object {
        const val SIGNUP_WELCOME_BONUS = 5
        const val SEVEN_DAYS_MILLIS = 7L * 24 * 60 * 60 * 1000L
        const val TEN_DAYS_MILLIS = 10L * 24 * 60 * 60 * 1000L
        const val MODERATION_WARN_BAN_THRESHOLD = 7

        @Volatile
        private var instance: TournamentRepository? = null

        fun getInstance(context: Context): TournamentRepository {
            return instance ?: synchronized(this) {
                instance ?: TournamentRepository(context.applicationContext).also { instance = it }
            }
        }
    }

    private val cloudSync: CloudTournamentSync? = appContext?.let { CloudTournamentSync(it) }

    private val _isCloudConnected = MutableStateFlow(cloudSync?.isSignedInCloud() ?: false)
    val isCloudConnected: StateFlow<Boolean> = _isCloudConnected.asStateFlow()

    private val _cloudUserEmail = MutableStateFlow(cloudSync?.currentCloudEmail().orEmpty())
    val cloudUserEmail: StateFlow<String> = _cloudUserEmail.asStateFlow()

    private val _cloudStatusMessage = MutableStateFlow(
        if (cloudSync?.isSignedInCloud() == true) {
            "লাইভ ক্লাউড সিঙ্ক চালু (${cloudSync.currentCloudEmail()})"
        } else {
            "ইমেইল ও পাসওয়ার্ড লগইন সক্রিয়"
        }
    )
    val cloudStatusMessage: StateFlow<String> = _cloudStatusMessage.asStateFlow()

    private val privateCloudAccounts = mutableMapOf<String, Triple<String, String, String>>()

    private val _appCustomization = MutableStateFlow(AppCustomization())
    val appCustomization: StateFlow<AppCustomization> = _appCustomization.asStateFlow()

    // Official bKash Number delegates to dynamic app customization
    val officialBkashNumber: String get() = _appCustomization.value.officialBkashNumber

    // Master Owner Credentials (Strictly for the app creator / owner)
    var masterOwnerPasskey = "TH-OWNER-7788"
    val masterOwnerPhone = "01861583924"
    var masterOwnerPin = "7788"

    // Official WhatsApp Channel / Group for app updates & Room ID sharing in 1 tap
    val officialWhatsAppGroupUrl = MutableStateFlow(com.example.model.OFFICIAL_WHATSAPP_CHANNEL_URL)

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
                email = "owner@trustedth.com",
                ign = "TH★DEVIL⚡",
                uid = "11410365896",
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

    private fun defaultInitialMatches(baseNowMillis: Long = System.currentTimeMillis()): List<TournamentMatch> {
        val lw1Start = baseNowMillis + 15 * 60_000L
        val lw2Start = baseNowMillis + 35 * 60_000L
        return listOf(
            TournamentMatch(
                id = "m_lw_1",
                title = "লোন উলফ - ১ বনাম ১ কিং ডুয়েল #০১",
                category = GameCategory.LONE_WOLF,
                matchType = MatchType.SOLO,
                mapName = "আয়রন কেজ (Iron Cage)",
                matchDateTime = formatMatchClockTimeLabel(lw1Start),
                startsInMinutes = 15,
                entryFee = 10,
                totalPrize = 15,
                perKillPrize = 0,
                booyahPrize = 15,
                totalSlots = 2,
                bookedSlots = 0,
                status = MatchStatus.UPCOMING,
                roomId = null,
                roomPassword = null,
                version = "মোবাইল অনলি (সুপারফাস্ট এক্সপ্রেস)",
                scheduledStartAtMillis = lw1Start
            ),
            TournamentMatch(
                id = "m_lw_2",
                title = "লোন উলফ - ২ বনাম ২ পার্টনার ব্যাটল #০২",
                category = GameCategory.LONE_WOLF,
                matchType = MatchType.DUO,
                mapName = "আইস কেভ (Ice Cave)",
                matchDateTime = formatMatchClockTimeLabel(lw2Start),
                startsInMinutes = 35,
                entryFee = 10,
                totalPrize = 20,
                perKillPrize = 0,
                booyahPrize = 20,
                totalSlots = 4,
                bookedSlots = 0,
                status = MatchStatus.UPCOMING,
                roomId = null,
                roomPassword = null,
                version = "মোবাইল অনলি (১৫ মিনিটে শেষ)",
                scheduledStartAtMillis = lw2Start
            )
        )
    }

    private fun defaultSavedMatchPresets(): List<MatchPresetTemplate> = listOf(
        MatchPresetTemplate(
            id = "preset_br_bermuda",
            presetName = "🏆 BR Rank — বারমুডা সোলো প্রিসেট",
            matchTitle = "বিআর র‍্যাঙ্ক - বারমুডা সোলো এক্সপ্রেস",
            category = GameCategory.BR_RANK,
            matchType = MatchType.SOLO,
            mapName = "বারমুডা (Bermuda)",
            defaultStartsInMinutes = 30,
            entryFee = 10,
            totalPrize = 80,
            perKillPrize = 1,
            booyahPrize = 30,
            totalSlots = 48,
            version = "মোবাইল অনলি (No Emulator)",
            isDefaultSaved = true
        ),
        MatchPresetTemplate(
            id = "preset_cs_squad",
            presetName = "⚔️ CS Rank — ৪v৪ ক্ল্যাশ স্কোয়াড প্রিসেট",
            matchTitle = "সিএস র‍্যাঙ্ক - ৪ বনাম ৪ ক্ল্যাশ স্কোয়াড",
            category = GameCategory.CS_RANK,
            matchType = MatchType.SQUAD,
            mapName = "ক্লক টাওয়ার (Clock Tower)",
            defaultStartsInMinutes = 20,
            entryFee = 10,
            totalPrize = 40,
            perKillPrize = 0,
            booyahPrize = 40,
            totalSlots = 8,
            version = "মোবাইল অনলি (ফাস্ট ফিনিশ)",
            isDefaultSaved = true
        ),
        MatchPresetTemplate(
            id = "preset_lw_1v1",
            presetName = "🐺 Lone Wolf — ১v১ কিং ডুয়েল প্রিসেট",
            matchTitle = "লোন উলফ - ১ বনাম ১ কিং ডুয়েল",
            category = GameCategory.LONE_WOLF,
            matchType = MatchType.SOLO,
            mapName = "আয়রন কেজ (Iron Cage)",
            defaultStartsInMinutes = 15,
            entryFee = 10,
            totalPrize = 15,
            perKillPrize = 0,
            booyahPrize = 15,
            totalSlots = 2,
            version = "মোবাইল অনলি (১০ মিনিটে শেষ)",
            isDefaultSaved = true
        )
    )

    private val _matches = MutableStateFlow<List<TournamentMatch>>(defaultInitialMatches())
    val matches: StateFlow<List<TournamentMatch>> = _matches.asStateFlow()

    private val _matchPresets = MutableStateFlow<List<MatchPresetTemplate>>(defaultSavedMatchPresets())
    val matchPresets: StateFlow<List<MatchPresetTemplate>> = _matchPresets.asStateFlow()

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

    private val _moderationLogs = MutableStateFlow<List<ModerationLogEntry>>(emptyList())
    val moderationLogs: StateFlow<List<ModerationLogEntry>> = _moderationLogs.asStateFlow()

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
            senderUid = "11410365896",
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

    private val _userPreferences = MutableStateFlow(UserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    private val _promoCodes = MutableStateFlow<List<PromoCodeItem>>(emptyList())
    val promoCodes: StateFlow<List<PromoCodeItem>> = _promoCodes.asStateFlow()

    private val _bonusDrops = MutableStateFlow<List<BonusGiftDrop>>(emptyList())
    val bonusDrops: StateFlow<List<BonusGiftDrop>> = _bonusDrops.asStateFlow()

    private val _communityPolls = MutableStateFlow<List<LiveCommunityPoll>>(emptyList())
    val communityPolls: StateFlow<List<LiveCommunityPoll>> = _communityPolls.asStateFlow()

    private val _triviaProgress = MutableStateFlow(DailyTriviaProgress())
    val triviaProgress: StateFlow<DailyTriviaProgress> = _triviaProgress.asStateFlow()

    private val _giveaways = MutableStateFlow<List<GiveawayEvent>>(emptyList())
    val giveaways: StateFlow<List<GiveawayEvent>> = _giveaways.asStateFlow()

    private val _reflexStats = MutableStateFlow(ReflexGameStats())
    val reflexStats: StateFlow<ReflexGameStats> = _reflexStats.asStateFlow()

    init {
        persistenceStore?.loadState()?.let { saved ->
            _isLoggedIn.value = saved.isLoggedIn
            _userProfile.value = saved.userProfile
            _walletBalance.value = saved.walletBalance
            if (saved.registeredUsers.isNotEmpty()) {
                _registeredUsers.value = saved.registeredUsers
            }
            if (saved.matches.isNotEmpty()) {
                val nowMs = System.currentTimeMillis()
                _matches.value = saved.matches.map { m ->
                    if (m.scheduledStartAtMillis <= 0L) {
                        m.copy(scheduledStartAtMillis = nowMs + m.startsInMinutes.coerceAtLeast(0) * 60_000L)
                    } else m
                }
            }
            if (saved.matchPresets.isNotEmpty()) {
                _matchPresets.value = saved.matchPresets
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
            val normalizedCfg = normalizeLegacyAppConfig(saved.appCustomization)
            _appCustomization.value = normalizedCfg
            if (saved.strictRules.isNotEmpty()) {
                _strictRules.value = saved.strictRules
            }
            val savedWa = saved.officialWhatsAppUrl
            officialWhatsAppGroupUrl.value = if (savedWa.isBlank() || savedWa == com.example.model.LEGACY_WHATSAPP_GROUP_URL) {
                com.example.model.OFFICIAL_WHATSAPP_CHANNEL_URL
            } else {
                savedWa
            }
            if (saved.chatMessages.isNotEmpty()) {
                _chatMessages.value = saved.chatMessages
            }
            masterOwnerPin = saved.masterOwnerPin
            _badgeAppeals.value = saved.badgeAppeals
            if (saved.globalChatMessages.isNotEmpty()) {
                _globalChatMessages.value = saved.globalChatMessages
            }
            _moderationLogs.value = saved.moderationLogs
            _userPreferences.value = saved.userPreferences
            _promoCodes.value = saved.promoCodes
            _bonusDrops.value = saved.bonusDrops
            _communityPolls.value = saved.communityPolls
            _triviaProgress.value = saved.triviaProgress
            _giveaways.value = saved.giveaways
            _reflexStats.value = saved.reflexStats
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
        checkAndClearExpiredModeration()
        rebuildLiveLeaderboard()
        if (cloudSync?.isSignedInCloud() == true) {
            startCloudSync()
        }
    }

    private fun rebuildLiveLeaderboard() {
        val rankedUsers = _registeredUsers.value
            .filter { user ->
                !user.isBanned && (
                    user.totalEarnings > 0 ||
                        user.totalWins > 0 ||
                        user.totalKills > 0 ||
                        user.matchesPlayed > 0
                    )
            }
            .sortedWith(
                compareByDescending<AppUser> { it.totalEarnings }
                    .thenByDescending { it.totalWins }
                    .thenByDescending { it.totalKills }
                    .thenByDescending { it.matchesPlayed }
                    .thenByDescending { it.kudosCount }
                    .thenBy { it.ign.lowercase() }
            )

        _leaderboard.value = rankedUsers.mapIndexed { idx, user ->
            val primary = user.primaryBadge
            LeaderboardPlayer(
                rank = idx + 1,
                ign = user.ign.ifBlank { user.name },
                uid = user.uid,
                totalKills = user.totalKills,
                booyahs = user.totalWins,
                totalEarnings = user.totalEarnings,
                badge = primary.iconEmoji,
                primaryBadge = primary,
                totalSpent = user.totalSpent,
                matchesPlayed = user.matchesPlayed
            )
        }
    }

    fun startCloudSync() {
        val sync = cloudSync ?: return
        sync.startRealtimeSync(
            CloudTournamentSync.SyncCallbacks(
                onAppConfigSynced = { cfg, rules ->
                    val normalizedCfg = normalizeLegacyAppConfig(cfg)
                    _appCustomization.value = normalizedCfg
                    officialWhatsAppGroupUrl.value = normalizedCfg.officialWhatsAppUrl
                    if (!rules.isNullOrEmpty()) {
                        _strictRules.value = rules
                    }
                    saveState()
                },
                onMatchesSynced = { cloudMatches ->
                    if (cloudMatches.isNotEmpty()) {
                        _matches.value = cloudMatches.sortedBy { it.effectiveStartMillis() }
                        saveState()
                    }
                },
                onBookingsSynced = { cloudBookings ->
                    val existingPhones = _myBookings.value.associate { it.bookingId to it.bkashSenderNumber }
                    _myBookings.value = cloudBookings.map { b ->
                        val cachedPhone = existingPhones[b.bookingId]
                        if (b.bkashSenderNumber.isBlank() && !cachedPhone.isNullOrBlank()) {
                            b.copy(bkashSenderNumber = cachedPhone)
                        } else b
                    }.sortedBy { it.slotNumber }
                    saveState()
                },
                onPlayersSynced = { cloudPlayers ->
                    val existingById = _registeredUsers.value.associateBy { it.id }
                    val merged = cloudPlayers.map { remote ->
                        val priv = privateCloudAccounts[remote.id]
                        val local = existingById[remote.id]
                            ?: _registeredUsers.value.firstOrNull {
                                (remote.uid.isNotBlank() && it.uid == remote.uid) ||
                                    it.ign.equals(remote.ign, ignoreCase = true)
                            }
                        val resolvedPhone = priv?.first?.takeIf { it.isNotBlank() }
                            ?: local?.phone.orEmpty()
                        val resolvedBkash = priv?.second?.takeIf { it.isNotBlank() }
                            ?: local?.bkashNumber.orEmpty()
                        val resolvedJoin = priv?.third?.takeIf { it.isNotBlank() }
                            ?: local?.joinedDate
                            ?: remote.joinedDate
                        remote.copy(
                            phone = resolvedPhone,
                            bkashNumber = resolvedBkash,
                            joinedDate = resolvedJoin,
                            password = local?.password ?: remote.password
                        )
                    }.toMutableList()

                    // Ensure official owner always exists
                    if (merged.none { it.role == UserRole.OWNER || it.phone == masterOwnerPhone || it.uid == "11410365896" }) {
                        existingById["u_owner"]?.let { merged.add(0, it) }
                    }
                    _registeredUsers.value = merged

                    // Sync active user profile & wallet live if updated remotely (badges, timeout, ban, wallet)
                    val active = _userProfile.value
                    if (active.isLoggedIn) {
                        val updatedSelf = merged.firstOrNull {
                            (active.phone.isNotBlank() && it.phone == active.phone) ||
                                (active.uid.isNotBlank() && it.uid == active.uid) ||
                                (active.ign.isNotBlank() && it.ign.equals(active.ign, ignoreCase = true))
                        }
                        if (updatedSelf != null) {
                            syncTargetToActiveProfile(updatedSelf)
                            if (updatedSelf.walletBalance != _walletBalance.value && updatedSelf.walletBalance >= 0) {
                                _walletBalance.value = updatedSelf.walletBalance
                            }
                            _userProfile.value = _userProfile.value.copy(
                                matchesPlayed = updatedSelf.matchesPlayed,
                                totalWins = updatedSelf.totalWins,
                                totalKills = updatedSelf.totalKills,
                                totalEarnings = updatedSelf.totalEarnings,
                                totalSpent = updatedSelf.totalSpent,
                                role = updatedSelf.role
                            )
                        }
                    }
                    rebuildLiveLeaderboard()
                    saveState()
                },
                onPrivateAccountsSynced = { accountMap ->
                    privateCloudAccounts.putAll(accountMap)
                    if (accountMap.isNotEmpty()) {
                        _registeredUsers.value = _registeredUsers.value.map { user ->
                            val priv = accountMap[user.id]
                            if (priv != null) {
                                user.copy(
                                    phone = priv.first.ifBlank { user.phone },
                                    bkashNumber = priv.second.ifBlank { user.bkashNumber },
                                    joinedDate = priv.third.ifBlank { user.joinedDate }
                                )
                            } else user
                        }
                        saveState()
                    }
                },
                onDepositsSynced = { cloudDeposits ->
                    if (cloudDeposits.isNotEmpty()) {
                        _depositRequests.value = cloudDeposits
                        saveState()
                    }
                },
                onWithdrawsSynced = { cloudWithdraws ->
                    if (cloudWithdraws.isNotEmpty()) {
                        _withdrawRequests.value = cloudWithdraws
                        saveState()
                    }
                },
                onAppealsSynced = { cloudAppeals ->
                    if (cloudAppeals.isNotEmpty()) {
                        _badgeAppeals.value = cloudAppeals
                        saveState()
                    }
                },
                onGlobalChatSynced = { cloudChat ->
                    if (cloudChat.isNotEmpty()) {
                        _globalChatMessages.value = cloudChat
                        saveState()
                    }
                },
                onModerationLogsSynced = { cloudLogs ->
                    if (cloudLogs.isNotEmpty()) {
                        _moderationLogs.value = cloudLogs
                        saveState()
                    }
                },
                onPromoCodesSynced = { cloudPromos ->
                    _promoCodes.value = cloudPromos
                    saveState()
                },
                onBonusDropsSynced = { cloudDrops ->
                    _bonusDrops.value = cloudDrops
                    saveState()
                },
                onCommunityPollsSynced = { cloudPolls ->
                    _communityPolls.value = cloudPolls
                    saveState()
                },
                onGiveawaysSynced = { cloudGiveaways ->
                    _giveaways.value = cloudGiveaways
                    saveState()
                },
                onSyncStateChanged = { connected, email, statusText ->
                    _isCloudConnected.value = connected
                    _cloudUserEmail.value = email
                    _cloudStatusMessage.value = statusText
                    if (connected) {
                        val isStaff = _userProfile.value.role == UserRole.OWNER ||
                            _userProfile.value.role == UserRole.ADMIN ||
                            email.equals("mrshosneara24@gmail.com", ignoreCase = true)
                        if (isStaff) {
                            sync.seedInitialMatchesIfCloudEmpty(
                                _matches.value,
                                _appCustomization.value,
                                _strictRules.value
                            )
                        }
                        if (_isLoggedIn.value) {
                            pushActiveUserToCloud()
                        }
                    }
                }
            )
        )
    }

    suspend fun signOutCloud() {
        cloudSync?.safeSignOutCloud()
        _isCloudConnected.value = false
        _cloudUserEmail.value = ""
        _cloudStatusMessage.value = "ক্লাউড সিঙ্ক সংযোগ বিচ্ছিন্ন"
    }

    private fun pushActiveUserToCloud() {
        val sync = cloudSync ?: return
        val current = _userProfile.value
        if (!current.isLoggedIn) return
        val matched = _registeredUsers.value.firstOrNull {
            (current.phone.isNotBlank() && it.phone == current.phone) ||
                (current.uid.isNotBlank() && it.uid == current.uid)
        }
        if (matched != null) {
            sync.pushUser(matched)
        }
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
                globalChatMessages = _globalChatMessages.value,
                moderationLogs = _moderationLogs.value,
                userPreferences = _userPreferences.value,
                promoCodes = _promoCodes.value,
                bonusDrops = _bonusDrops.value,
                communityPolls = _communityPolls.value,
                triviaProgress = _triviaProgress.value,
                giveaways = _giveaways.value,
                reflexStats = _reflexStats.value,
                matchPresets = _matchPresets.value
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
                    banExpiresAtMillis = current.banExpiresAtMillis,
                    moderationWarningsCount = current.moderationWarningsCount,
                    timeoutUntilMillis = current.timeoutUntilMillis,
                    timeoutReason = current.timeoutReason,
                    timeoutBy = current.timeoutBy,
                    isChatMuted = current.isChatMuted,
                    chatMuteUntilMillis = current.chatMuteUntilMillis,
                    chatMuteReason = current.chatMuteReason,
                    isMatchBanned = current.isMatchBanned,
                    matchBanReason = current.matchBanReason,
                    isWalletFrozen = current.isWalletFrozen,
                    walletFreezeReason = current.walletFreezeReason
                )
            } else {
                user
            }
        }
        rebuildLiveLeaderboard()
        saveState()
        pushActiveUserToCloud()
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
        rebuildLiveLeaderboard()
        saveState()
        pushActiveUserToCloud()
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
        var updatedUserId: String? = null
        _registeredUsers.value = _registeredUsers.value.map { user ->
            if (user.id == trimmed || user.uid == trimmed || user.phone == trimmed || user.ign.equals(trimmed, ignoreCase = true)) {
                updatedCount = user.kudosCount + 1
                updatedUserId = user.id
                user.copy(kudosCount = updatedCount)
            } else {
                user
            }
        }
        if (updatedCount > 0) {
            saveState()
            updatedUserId?.let { cloudSync?.pushKudosUpdate(it, updatedCount) }
        }
        return updatedCount
    }

    fun login(phoneOrEmail: String, password: String = ""): Boolean {
        checkAndClearExpiredBans()
        val trimmed = phoneOrEmail.trim()
        val passTrimmed = password.trim()
        val existingUser = _registeredUsers.value.find {
            (it.email.isNotBlank() && it.email.equals(trimmed, ignoreCase = true)) ||
                it.phone == trimmed ||
                it.uid == trimmed ||
                it.bkashNumber == trimmed
        }

        // Owner sign-in via owner email / 01861583924 + code 7788 (or master passkey)
        val isOwnerPhoneOrKey = trimmed == masterOwnerPhone ||
                trimmed.equals("owner@trustedth.com", ignoreCase = true) ||
                trimmed.equals("mrshosneara24@gmail.com", ignoreCase = true) ||
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
        val resolvedEmail = existingUser?.email?.takeIf { it.isNotBlank() }
            ?: if (trimmed.contains("@")) trimmed else current.email
        _userProfile.value = if (role == UserRole.OWNER) {
            current.copy(
                isLoggedIn = true,
                accountName = existingUser?.name ?: "তানভির হাসান (ওনার)",
                ign = existingUser?.ign ?: "TH★DEVIL⚡",
                uid = existingUser?.uid ?: "11410365896",
                phone = existingUser?.phone ?: masterOwnerPhone,
                bkashNumber = existingUser?.bkashNumber ?: masterOwnerPhone,
                email = resolvedEmail.ifBlank { "owner@trustedth.com" },
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
                email = resolvedEmail,
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
                banExpiresAtMillis = existingUser?.banExpiresAtMillis ?: current.banExpiresAtMillis,
                moderationWarningsCount = existingUser?.moderationWarningsCount ?: current.moderationWarningsCount,
                timeoutUntilMillis = existingUser?.timeoutUntilMillis ?: current.timeoutUntilMillis,
                timeoutReason = existingUser?.timeoutReason ?: current.timeoutReason,
                timeoutBy = existingUser?.timeoutBy ?: current.timeoutBy,
                isChatMuted = existingUser?.isChatMuted ?: current.isChatMuted,
                chatMuteUntilMillis = existingUser?.chatMuteUntilMillis ?: current.chatMuteUntilMillis,
                chatMuteReason = existingUser?.chatMuteReason ?: current.chatMuteReason,
                isMatchBanned = existingUser?.isMatchBanned ?: current.isMatchBanned,
                matchBanReason = existingUser?.matchBanReason ?: current.matchBanReason,
                isWalletFrozen = existingUser?.isWalletFrozen ?: current.isWalletFrozen,
                walletFreezeReason = existingUser?.walletFreezeReason ?: current.walletFreezeReason
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
                email = resolvedEmail,
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
                banExpiresAtMillis = existingUser?.banExpiresAtMillis,
                moderationWarningsCount = existingUser?.moderationWarningsCount ?: 0,
                timeoutUntilMillis = existingUser?.timeoutUntilMillis,
                timeoutReason = existingUser?.timeoutReason,
                timeoutBy = existingUser?.timeoutBy,
                isChatMuted = existingUser?.isChatMuted ?: false,
                chatMuteUntilMillis = existingUser?.chatMuteUntilMillis,
                chatMuteReason = existingUser?.chatMuteReason,
                isMatchBanned = existingUser?.isMatchBanned ?: false,
                matchBanReason = existingUser?.matchBanReason,
                isWalletFrozen = existingUser?.isWalletFrozen ?: false,
                walletFreezeReason = existingUser?.walletFreezeReason
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
            (it.email.isNotBlank() && it.email.equals(trimmed, ignoreCase = true)) ||
                it.phone == trimmed ||
                it.uid == trimmed ||
                it.bkashNumber == trimmed
        }
    }

    fun requestPasswordResetOtp(phoneOrUid: String): Pair<Boolean, String> {
        val trimmed = phoneOrUid.trim()
        val user = findUserForPasswordReset(trimmed)
        if (user == null) {
            return Pair(false, "এই ইমেইল, ফোন নম্বর বা UID দিয়ে কোনো অ্যাকাউন্ট খুঁজে পাওয়া যায়নি!")
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
            (it.email.isNotBlank() && it.email.equals(trimmed, ignoreCase = true)) ||
                it.phone == trimmed ||
                it.uid == trimmed ||
                it.bkashNumber == trimmed
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
        password: String = "123456",
        email: String = ""
    ): Boolean {
        val trimmedName = accountName.trim()
        val trimmedIgn = ign.trim()
        val trimmedUid = uid.trim()
        val trimmedPhone = phone.trim()
        val trimmedBkash = bkash.trim().ifBlank { trimmedPhone }
        val trimmedPassword = password.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isBlank() || trimmedIgn.isBlank() || trimmedUid.isBlank() || trimmedPhone.isBlank()) {
            return false
        }
        if (trimmedPassword.length < 4) {
            return false
        }
        val alreadyExists = _registeredUsers.value.any {
            it.phone == trimmedPhone ||
                (trimmedEmail.isNotBlank() && it.email.equals(trimmedEmail, ignoreCase = true))
        }
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
            email = trimmedEmail,
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
            email = trimmedEmail,
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
        cloudSync?.pushUser(newUser)
        return true
    }

    fun logout() {
        syncActiveUserToRegistry()
        _isLoggedIn.value = false
        _userProfile.value = _userProfile.value.copy(isLoggedIn = false)
        saveState()
    }

    fun spinWeeklyWheel(): Int {
        checkAndClearExpiredModeration()
        if (_hasSpunThisWeek.value || isActiveUserTimedOut() || _userProfile.value.isBanned) return 0
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
        checkAndClearExpiredModeration()
        val currentInfo = _dailyStreakInfo.value
        if (currentInfo.isTodayClaimed || isActiveUserTimedOut() || _userProfile.value.isBanned) return 0

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
        checkAndClearExpiredModeration()
        val current = _userProfile.value
        if (!current.isLoggedIn) {
            return Pair(false, "গ্লোবাল চ্যাটে মেসেজ পাঠাতে আগে লগইন করুন।")
        }
        if (current.isBanned) {
            return Pair(false, "🚫 আপনার একাউন্ট সাসপেন্ড রয়েছে, তাই চ্যাট করতে পারবেন না।")
        }
        if (isActiveUserTimedOut()) {
            return Pair(false, "⏳ আপনি বর্তমানে অ্যাপ টাইমআউটে আছেন! টাইমার শেষ হওয়া পর্যন্ত চ্যাট করা যাবে না।")
        }
        if (current.isChatMuted) {
            return Pair(false, "🔇 আপনার গ্লোবাল চ্যাট মিউট করা আছে! (${current.chatMuteReason ?: "মডারেশন শাস্তি"})")
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
        cloudSync?.pushGlobalChatMessage(msg)
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
                msg.id == messageId -> msg.copy(isPinnedAnnouncement = nextPinned).also { cloudSync?.pushGlobalChatMessage(it) }
                nextPinned -> msg.copy(isPinnedAnnouncement = false).also {
                    if (msg.isPinnedAnnouncement) cloudSync?.pushGlobalChatMessage(it)
                }
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
        cloudSync?.deleteGlobalChatCloud(messageId)
        return Pair(true, "🗑️ মেসেজ ডিলিট করা হয়েছে।")
    }

    fun reactToGlobalChatMessage(messageId: String, emoji: String): Boolean {
        if (!_userProfile.value.isLoggedIn) return false
        var updatedMsg: GlobalChatMessage? = null
        _globalChatMessages.value = _globalChatMessages.value.map { msg ->
            if (msg.id == messageId) {
                val nextMap = msg.reactions.toMutableMap()
                nextMap[emoji] = (nextMap[emoji] ?: 0) + 1
                msg.copy(reactions = nextMap).also { updatedMsg = it }
            } else {
                msg
            }
        }
        updatedMsg?.let {
            saveState()
            cloudSync?.pushGlobalChatMessage(it)
        }
        return updatedMsg != null
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
        checkAndClearExpiredModeration()
        val current = _userProfile.value
        if (current.isBanned || isActiveUserTimedOut() || current.isMatchBanned) {
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
        val updatedBookedCount = (liveMatch.bookedSlots + 1).coerceAtMost(liveMatch.totalSlots)
        _matches.value = _matches.value.map {
            if (it.id == liveMatch.id) it.copy(bookedSlots = updatedBookedCount) else it
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
        cloudSync?.pushBooking(newBooking, updatedBookedCount)

        return true
    }

    fun bookSlotWithWallet(
        match: TournamentMatch,
        slotNumber: Int,
        playerIgn: String,
        playerUid: String
    ): Pair<Boolean, String> {
        checkAndClearExpiredModeration()
        val current = _userProfile.value
        if (current.isBanned) {
            return Pair(false, "আপনার অ্যাকাউন্ট ব্যান করা আছে!")
        }
        if (isActiveUserTimedOut()) {
            return Pair(false, "⏳ আপনি অ্যাপ টাইমআউটে আছেন! টাইমার শেষ না হওয়া পর্যন্ত স্লট বুক করা যাবে না।")
        }
        if (current.isMatchBanned) {
            return Pair(false, "🎯 আপনার ম্যাচ এন্ট্রি সাময়িকভাবে নিষিদ্ধ (${current.matchBanReason ?: "মডারেশন শাস্তি"})!")
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

        val updatedBookedCount = (liveMatch.bookedSlots + 1).coerceAtMost(liveMatch.totalSlots)
        _matches.value = _matches.value.map {
            if (it.id == liveMatch.id) it.copy(bookedSlots = updatedBookedCount) else it
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
        cloudSync?.pushBooking(newBooking, updatedBookedCount)

        return Pair(true, "ওয়ালেট থেকে ৳${liveMatch.entryFee} কেটে স্লট #$slotNumber বুক হয়েছে!")
    }

    private fun refundPlayerForBooking(booking: SlotBooking) {
        val active = _userProfile.value
        val matchedUser = _registeredUsers.value.firstOrNull { user ->
            (booking.bkashSenderNumber.isNotBlank() && (user.phone == booking.bkashSenderNumber || user.bkashNumber == booking.bkashSenderNumber)) ||
                (booking.playerUid.isNotBlank() && booking.playerUid != "0000000000" && user.uid == booking.playerUid) ||
                (booking.playerIgn.isNotBlank() && user.ign.equals(booking.playerIgn, ignoreCase = true))
        }

        if (matchedUser != null) {
            val updatedUser = matchedUser.copy(
                walletBalance = matchedUser.walletBalance + booking.feePaid,
                matchesPlayed = (matchedUser.matchesPlayed - 1).coerceAtLeast(0),
                totalSpent = (matchedUser.totalSpent - booking.feePaid).coerceAtLeast(0)
            )
            _registeredUsers.value = _registeredUsers.value.map {
                if (it.id == updatedUser.id) updatedUser else it
            }
            cloudSync?.pushUser(updatedUser)

            val isSameActive = (active.phone.isNotBlank() && active.phone == updatedUser.phone) ||
                (active.uid.isNotBlank() && active.uid == updatedUser.uid)
            if (isSameActive) {
                _walletBalance.value += booking.feePaid
                _userProfile.value = active.copy(
                    matchesPlayed = (active.matchesPlayed - 1).coerceAtLeast(0),
                    totalSpent = (active.totalSpent - booking.feePaid).coerceAtLeast(0)
                )
            }
        } else {
            // Fallback when owner self-booked or legacy anonymous slot
            _walletBalance.value += booking.feePaid
            if (active.matchesPlayed > 0 || active.totalSpent > 0) {
                _userProfile.value = active.copy(
                    matchesPlayed = (active.matchesPlayed - 1).coerceAtLeast(0),
                    totalSpent = (active.totalSpent - booking.feePaid).coerceAtLeast(0)
                )
            }
        }
        rebuildLiveLeaderboard()
    }

    fun cancelSlotBookingAndRefund(bookingId: String): Boolean {
        val booking = _myBookings.value.find { it.bookingId == bookingId } ?: return false

        // Remove the booking
        _myBookings.value = _myBookings.value.filter { it.bookingId != bookingId }

        // Reduce match booked slots safely
        var nextBookedCount = 0
        val explicitRemaining = _myBookings.value.count { it.matchId == booking.matchId }
        _matches.value = _matches.value.map { match ->
            if (match.id == booking.matchId) {
                nextBookedCount = (match.bookedSlots - 1).coerceAtLeast(explicitRemaining).coerceAtLeast(0)
                match.copy(bookedSlots = nextBookedCount)
            } else match
        }

        // Refund the booked player's wallet (not owner's own wallet unless owner booked)
        refundPlayerForBooking(booking)

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
            userPhone = booking.bkashSenderNumber.ifBlank { _userProfile.value.phone }
        )
        _transactions.value = listOf(refundTx) + _transactions.value
        syncActiveUserToRegistry()
        cloudSync?.deleteBookingCloud(booking.bookingId, booking.matchId, nextBookedCount)
        return true
    }

    fun cancelLegacySlotByNumberResult(matchId: String, slotNumber: Int): Pair<Boolean, String> {
        val existing = _myBookings.value.find { it.matchId == matchId && it.slotNumber == slotNumber }
        if (existing != null) {
            val ok = cancelSlotBookingAndRefund(existing.bookingId)
            return ok to if (ok) {
                "✅ স্লট #$slotNumber (${existing.playerIgn}) রিমুভ এবং ৳${existing.feePaid} প্লেয়ার ওয়ালেটে রিফান্ড হয়েছে!"
            } else {
                "❌ স্লট #$slotNumber বাতিল করা যায়নি!"
            }
        }
        val liveMatch = _matches.value.find { it.id == matchId }
            ?: return false to "ম্যাচটি খুঁজে পাওয়া যায়নি!"
        if (liveMatch.bookedSlots <= 0 || slotNumber !in 1..liveMatch.totalSlots) {
            return false to "স্লট #$slotNumber বর্তমানে খালি আছে।"
        }

        val updatedMatch = liveMatch.copy(bookedSlots = (liveMatch.bookedSlots - 1).coerceAtLeast(0))
        _matches.value = _matches.value.map { m -> if (m.id == matchId) updatedMatch else m }
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
        cloudSync?.pushMatch(updatedMatch)
        return true to "✅ স্লট #$slotNumber সফলভাবে রিমুভ ও ৳${liveMatch.entryFee} রিফান্ড হয়েছে!"
    }

    fun cancelLegacySlotByNumber(matchId: String, slotNumber: Int): Boolean =
        cancelLegacySlotByNumberResult(matchId, slotNumber).first

    fun cancelMatchAndRefundAll(matchId: String): Pair<Int, Int> {
        val matchBookings = _myBookings.value.filter { it.matchId == matchId }
        val count = matchBookings.size
        var totalRefund = 0

        matchBookings.forEach { booking ->
            totalRefund += booking.feePaid
            refundPlayerForBooking(booking)
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
                userPhone = booking.bkashSenderNumber.ifBlank { _userProfile.value.phone }
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        _myBookings.value = _myBookings.value.filter { it.matchId != matchId }
        var updatedCancelledMatch: TournamentMatch? = null
        _matches.value = _matches.value.map {
            if (it.id == matchId) it.copy(bookedSlots = 0, status = MatchStatus.CANCELLED).also { m -> updatedCancelledMatch = m } else it
        }

        syncActiveUserToRegistry()
        matchBookings.forEach { cloudSync?.deleteBookingCloud(it.bookingId, matchId, 0) }
        updatedCancelledMatch?.let { cloudSync?.pushMatch(it) }
        return Pair(count, totalRefund)
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
        checkAndClearExpiredModeration()
        val currentUser = _userProfile.value
        if (currentUser.isBanned) {
            return Pair(false, "🚫 আপনার একাউন্ট সাসপেন্ড রয়েছে, তাই ডিপোজিট রিকোয়েস্ট পাঠানো যাবে না!")
        }
        if (isActiveUserTimedOut()) {
            return Pair(false, "⏳ আপনি অ্যাপ টাইমআউটে আছেন! টাইমার শেষ হওয়ার আগে ডিপোজিট পাঠানো যাবে না।")
        }
        if (currentUser.isWalletFrozen) {
            return Pair(false, "❄️ আপনার ওয়ালেট ফ্রিজ করা আছে (${currentUser.walletFreezeReason ?: "নিরাপত্তা অডিট"})!")
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
            cleanTrx.startsWith("TEST") ||
            cleanTrx.startsWith("FAKE") ||
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
        cloudSync?.pushDepositRequest(request)
        return Pair(true, "৳$amount ডিপোজিট রিকোয়েস্ট পাঠানো হয়েছে! ওনার অনুমোদন করলেই ওয়ালেটে যোগ হবে।")
    }

    fun approveDepositRequest(requestId: String): Pair<Boolean, String> {
        val req = _depositRequests.value.find { it.id == requestId }
            ?: return Pair(false, "ডিপোজিট রিকোয়েস্ট পাওয়া যায়নি!")
        if (req.status != RequestStatus.PENDING) {
            return Pair(false, "এই রিকোয়েস্টটি ইতিমধ্যেই প্রসেস করা হয়েছে!")
        }

        var updatedReq = req
        _depositRequests.value = _depositRequests.value.map {
            if (it.id == requestId) it.copy(status = RequestStatus.APPROVED, adminNote = "অনুমোদিত ও ওয়ালেটে যুক্ত").also { r -> updatedReq = r } else it
        }

        // Credit user wallet (both in registeredUsers and active balance if matching)
        val userMatch = _registeredUsers.value.find { it.phone == req.userPhone }
        if (userMatch != null) {
            val updatedUser = userMatch.copy(walletBalance = userMatch.walletBalance + req.amount)
            _registeredUsers.value = _registeredUsers.value.map {
                if (it.id == updatedUser.id) updatedUser else it
            }
            cloudSync?.pushUser(updatedUser)
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
        cloudSync?.pushDepositRequest(updatedReq)
        return Pair(true, "✅ ${req.userName}-এর ৳${req.amount} ডিপোজিট অনুমোদিত ও ওয়ালেটে যুক্ত হয়েছে!")
    }

    fun rejectDepositRequest(requestId: String, reason: String = "ভুয়া বা ভুল TrxID"): Pair<Boolean, String> {
        val req = _depositRequests.value.find { it.id == requestId }
            ?: return Pair(false, "ডিপোজিট রিকোয়েস্ট পাওয়া যায়নি!")
        if (req.status != RequestStatus.PENDING) {
            return Pair(false, "এই রিকোয়েস্টটি ইতিমধ্যেই প্রসেস করা হয়েছে!")
        }

        val cleanReason = reason.ifBlank { "ভুয়া বা ভুল TrxID" }
        var updatedReq = req
        _depositRequests.value = _depositRequests.value.map {
            if (it.id == requestId) it.copy(status = RequestStatus.REJECTED, adminNote = cleanReason).also { r -> updatedReq = r } else it
        }
        _transactions.value = _transactions.value.map { tx ->
            if (tx.requestId == requestId) {
                tx.copy(statusText = "❌ বাতিল: $cleanReason")
            } else tx
        }
        saveState()
        cloudSync?.pushDepositRequest(updatedReq)
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
        checkAndClearExpiredModeration()
        val currentUser = _userProfile.value
        if (currentUser.isBanned) {
            return Pair(false, "🚫 আপনার একাউন্ট সাসপেন্ড রয়েছে, তাই উইথড্র রিকোয়েস্ট পাঠানো যাবে না!")
        }
        if (isActiveUserTimedOut()) {
            return Pair(false, "⏳ আপনি অ্যাপ টাইমআউটে আছেন! টাইমার শেষ হওয়ার আগে উইথড্র করা যাবে না।")
        }
        if (currentUser.isWalletFrozen) {
            return Pair(false, "❄️ আপনার ওয়ালেট ফ্রিজ করা আছে (${currentUser.walletFreezeReason ?: "নিরাপত্তা অডিট"})! অনুগ্রহ করে সাপোর্টে যোগাযোগ করুন।")
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
        cloudSync?.pushWithdrawRequest(withdrawReq)
        return Pair(true, "৳$amount ${paymentMethod.tag} ক্যাশআউট রিকোয়েস্ট সফল! অনুমোদনের পর টাকা পাঠানো হবে।")
    }

    fun approveWithdrawRequest(requestId: String): Pair<Boolean, String> {
        val req = _withdrawRequests.value.find { it.id == requestId }
            ?: return Pair(false, "উইথড্র রিকোয়েস্ট পাওয়া যায়নি!")
        if (req.status != RequestStatus.PENDING) {
            return Pair(false, "এই রিকোয়েস্টটি ইতিমধ্যেই প্রসেস করা হয়েছে!")
        }

        var updatedReq = req
        _withdrawRequests.value = _withdrawRequests.value.map {
            if (it.id == requestId) it.copy(status = RequestStatus.APPROVED, adminNote = "পেমেন্ট পাঠানো হয়েছে").also { r -> updatedReq = r } else it
        }
        _transactions.value = _transactions.value.map { tx ->
            if (tx.requestId == requestId) {
                tx.copy(statusText = "✅ পেমেন্ট সম্পন্ন (${req.paymentMethod.tag}: ${req.targetNumber})")
            } else tx
        }
        saveState()
        cloudSync?.pushWithdrawRequest(updatedReq)
        return Pair(true, "✅ ${req.userName}-এর ৳${req.amount} উইথড্র সম্পন্ন চিহ্নিত হয়েছে!")
    }

    fun rejectWithdrawRequest(requestId: String, reason: String = "নম্বর বা তথ্যে ত্রুটি"): Pair<Boolean, String> {
        val req = _withdrawRequests.value.find { it.id == requestId }
            ?: return Pair(false, "উইথড্র রিকোয়েস্ট পাওয়া যায়নি!")
        if (req.status != RequestStatus.PENDING) {
            return Pair(false, "এই রিকোয়েস্টটি ইতিমধ্যেই প্রসেস করা হয়েছে!")
        }

        val cleanReason = reason.ifBlank { "নম্বর বা তথ্যে ত্রুটি" }
        var updatedReq = req
        _withdrawRequests.value = _withdrawRequests.value.map {
            if (it.id == requestId) it.copy(status = RequestStatus.REJECTED, adminNote = cleanReason).also { r -> updatedReq = r } else it
        }

        // Refund deducted money back to user
        _registeredUsers.value = _registeredUsers.value.map {
            if (it.phone == req.userPhone) it.copy(walletBalance = it.walletBalance + req.amount).also { u -> cloudSync?.pushUser(u) } else it
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
        cloudSync?.pushWithdrawRequest(updatedReq)
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
        val updatedUser = targetUser.copy(walletBalance = newBalance)
        list[index] = updatedUser
        _registeredUsers.value = list
        cloudSync?.pushUser(updatedUser)

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
        val updatedUser = user.copy(matchesPlayed = updatedCount)
        list[index] = updatedUser
        _registeredUsers.value = list
        cloudSync?.pushUser(updatedUser)

        if (_userProfile.value.phone == user.phone || (_userProfile.value.uid.isNotBlank() && _userProfile.value.uid == user.uid)) {
            _userProfile.value = _userProfile.value.copy(matchesPlayed = updatedCount)
        }
        rebuildLiveLeaderboard()
        saveState()
        return Pair(true, "✅ ${user.name}-এর মোট ম্যাচ সংখ্যা এখন $updatedCount টি।")
    }

    fun updateWhatsAppGroupUrl(url: String) {
        officialWhatsAppGroupUrl.value = url
        _appCustomization.value = _appCustomization.value.copy(officialWhatsAppUrl = url)
        saveState()
        cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
    }

    fun publishRoomCredentials(matchId: String, roomId: String, password: String): Boolean {
        var updatedMatch: TournamentMatch? = null
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                match.copy(
                    roomId = roomId,
                    roomPassword = password,
                    status = MatchStatus.ROOM_OPEN
                ).also { updatedMatch = it }
            } else {
                match
            }
        }
        _myBookings.value = _myBookings.value.map { booking ->
            if (booking.matchId == matchId) {
                booking.copy(roomId = roomId, roomPassword = password).also { cloudSync?.pushBooking(it) }
            } else {
                booking
            }
        }
        saveState()
        updatedMatch?.let { cloudSync?.pushMatch(it) }
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
        totalSlots: Int,
        startsInMinutes: Int = 15,
        scheduledStartAtMillis: Long = System.currentTimeMillis() + startsInMinutes.coerceAtLeast(0) * 60_000L
    ) {
        val safeMinutes = startsInMinutes.coerceIn(1, 100000)
        val targetMillis = if (scheduledStartAtMillis > 0L) {
            scheduledStartAtMillis
        } else {
            System.currentTimeMillis() + safeMinutes * 60_000L
        }
        val resolvedDateTime = matchDateTime.trim().ifBlank { formatMatchClockTimeLabel(targetMillis) }
        val newMatch = TournamentMatch(
            id = "m_${System.currentTimeMillis()}",
            title = title,
            category = category,
            matchType = matchType,
            mapName = mapName,
            matchDateTime = resolvedDateTime,
            startsInMinutes = safeMinutes,
            entryFee = entryFee,
            totalPrize = totalPrize,
            perKillPrize = perKill,
            booyahPrize = booyahPrize,
            totalSlots = totalSlots,
            bookedSlots = 0,
            status = MatchStatus.UPCOMING,
            scheduledStartAtMillis = targetMillis
        )
        _matches.value = (listOf(newMatch) + _matches.value).sortedBy { it.effectiveStartMillis() }
        saveState()
        cloudSync?.pushMatch(newMatch)
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
            pushActiveUserToCloud()
            return true
        }
        return false
    }

    private fun currentActorLabel(role: UserRole = _userProfile.value.role): String {
        return when (role) {
            UserRole.OWNER -> "ওনার (${_userProfile.value.ign.ifBlank { "TH" }})"
            UserRole.ADMIN -> "অ্যাডমিন (${_userProfile.value.ign.ifBlank { "TH" }})"
            UserRole.PLAYER -> "সিস্টেম"
        }
    }

    private fun findUserIndexByKey(targetKey: String): Int {
        val clean = targetKey.trim()
        if (clean.isBlank()) return -1
        return _registeredUsers.value.indexOfFirst {
            it.id == clean ||
                it.phone == clean ||
                it.uid == clean ||
                it.bkashNumber == clean ||
                it.ign.equals(clean, ignoreCase = true)
        }
    }

    private fun canActorModerateTarget(actorRole: UserRole, target: AppUser): Pair<Boolean, String> {
        if (target.role == UserRole.OWNER && actorRole != UserRole.OWNER) {
            return Pair(false, "⛔ অ্যাপের মূল ওনারের বিরুদ্ধে কোনো শাস্তি বা মডারেশন অ্যাকশন নেওয়া যাবে না!")
        }
        if (target.role == UserRole.OWNER) {
            return Pair(false, "👑 সুপ্রিম ওনার অ্যাকাউন্টের উপর শাস্তি কার্যকর করা যাবে না!")
        }
        if (actorRole == UserRole.ADMIN && target.role == UserRole.ADMIN) {
            return Pair(false, "🛡️ একজন অ্যাডমিন আরেকজন অ্যাডমিনকে শাস্তি দিতে পারবেন না! কেবল ওনার এটি করতে পারবেন।")
        }
        return Pair(true, "")
    }

    private fun appendModerationLog(
        target: AppUser,
        actionType: ModerationActionType,
        durationLabel: String = "",
        reason: String = "",
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        timestampMillis: Long = System.currentTimeMillis()
    ) {
        val entry = ModerationLogEntry(
            id = "mod_${UUID.randomUUID().toString().take(8)}",
            targetUserId = target.id,
            targetIgn = target.ign.ifBlank { target.name },
            targetPhone = target.phone,
            actionType = actionType,
            durationLabel = durationLabel,
            reason = reason,
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = timestampMillis,
            timeLabel = "এইমাত্র"
        )
        _moderationLogs.value = (listOf(entry) + _moderationLogs.value).take(80)
        cloudSync?.pushUser(target)
        cloudSync?.pushModerationLog(entry, target.id)
    }

    private fun syncTargetToActiveProfile(updatedUser: AppUser) {
        val active = _userProfile.value
        val matches = (active.phone.isNotBlank() && active.phone == updatedUser.phone) ||
            (active.uid.isNotBlank() && active.uid == updatedUser.uid)
        if (matches) {
            _userProfile.value = active.copy(
                isBanned = updatedUser.isBanned,
                banReason = updatedUser.banReason,
                banExpiresAtMillis = updatedUser.banExpiresAtMillis,
                warningsCount = updatedUser.warningsCount,
                moderationWarningsCount = updatedUser.moderationWarningsCount,
                timeoutUntilMillis = updatedUser.timeoutUntilMillis,
                timeoutReason = updatedUser.timeoutReason,
                timeoutBy = updatedUser.timeoutBy,
                isChatMuted = updatedUser.isChatMuted,
                chatMuteUntilMillis = updatedUser.chatMuteUntilMillis,
                chatMuteReason = updatedUser.chatMuteReason,
                isMatchBanned = updatedUser.isMatchBanned,
                matchBanReason = updatedUser.matchBanReason,
                isWalletFrozen = updatedUser.isWalletFrozen,
                walletFreezeReason = updatedUser.walletFreezeReason,
                customBadges = updatedUser.customBadges,
                revokedBadges = updatedUser.revokedBadges
            )
        }
    }

    fun isActiveUserTimedOut(nowMillis: Long = System.currentTimeMillis()): Boolean {
        val p = _userProfile.value
        if (p.role == UserRole.OWNER) return false
        val until = p.timeoutUntilMillis ?: return false
        return until > nowMillis
    }

    fun formatDurationBangla(minutes: Long): String {
        val safe = minutes.coerceAtLeast(1)
        val days = safe / (24 * 60)
        val remAfterDays = safe % (24 * 60)
        val hours = remAfterDays / 60
        val mins = remAfterDays % 60
        return buildString {
            if (days > 0) append("${days} দিন ")
            if (hours > 0) append("${hours} ঘন্টা ")
            if (mins > 0) append("${mins} মিনিট")
        }.trim().ifBlank { "${safe} মিনিট" }
    }

    fun warnUser(
        targetKey: String,
        reason: String = "টুর্নামেন্ট নীতিমালা লঙ্ঘন",
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "এই নম্বর, UID বা IGN দিয়ে কোনো ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val cleanReason = reason.trim().ifBlank { "টুর্নামেন্টের নিয়ম ভঙ্গ ও অসদাচরণ" }
        val newWarns = target.moderationWarningsCount + 1
        val reachedSevenWarnLimit = newWarns >= MODERATION_WARN_BAN_THRESHOLD
        val tenDayExpiry = nowMillis + TEN_DAYS_MILLIS

        val updated = if (reachedSevenWarnLimit) {
            target.copy(
                moderationWarningsCount = newWarns,
                isBanned = true,
                banReason = "৭টি ওয়ার্নিং পূর্ণ হওয়ায় স্বয়ংক্রিয়ভাবে ১০ দিনের অ্যাপ ব্যান! (সর্বশেষ কারণ: $cleanReason)",
                bannedAt = "আজ এইমাত্র",
                banExpiresAtMillis = tenDayExpiry
            )
        } else {
            target.copy(moderationWarningsCount = newWarns)
        }

        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        appendModerationLog(
            target = updated,
            actionType = ModerationActionType.WARN,
            durationLabel = if (reachedSevenWarnLimit) "১০ দিন অটো-ব্যান ($newWarns/৭)" else "ওয়ার্ন #$newWarns/৭",
            reason = cleanReason,
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()

        val msg = if (reachedSevenWarnLimit) {
            "🚫 ${updated.ign}-এর মোট ৭টি ওয়ার্নিং পূর্ণ হয়েছে! নিয়ম অনুযায়ী স্বয়ংক্রিয়ভাবে ১০ দিনের অ্যাপ ব্যান কার্যকর করা হয়েছে।"
        } else {
            "⚠️ ${updated.ign}-কে ওয়ার্নিং দেওয়া হয়েছে ($newWarns/৭)! ৭টি ওয়ার্নিং পূর্ণ হলে ১০ দিনের অ্যাপ ব্যান হবে।"
        }
        return Pair(true, msg)
    }

    fun clearUserModerationWarnings(
        targetKey: String,
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val updated = target.copy(moderationWarningsCount = 0, warningsCount = 0)
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        appendModerationLog(
            target = updated,
            actionType = ModerationActionType.CLEAR_WARNINGS,
            reason = "স্টাফ কর্তৃক ওয়ার্নিং রিসেট",
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(true, "🧹 ${updated.ign}-এর সকল ওয়ার্নিং রিসেট (০/৭) করা হয়েছে!")
    }

    fun timeoutUser(
        targetKey: String,
        durationMinutes: Long,
        reason: String = "অস্থায়ী অ্যাপ টাইমআউট",
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        if (durationMinutes < 1) {
            return Pair(false, "কমপক্ষে ১ মিনিটের টাইমআউট সময় দিন!")
        }
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val cleanReason = reason.trim().ifBlank { "মডারেশন নীতিমালা ভঙ্গের কারণে সাময়িক টাইমআউট" }
        val expiryMillis = nowMillis + (durationMinutes * 60_000L)
        val durationLabel = formatDurationBangla(durationMinutes)
        val updated = target.copy(
            timeoutUntilMillis = expiryMillis,
            timeoutReason = cleanReason,
            timeoutBy = moderatorName
        )
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        appendModerationLog(
            target = updated,
            actionType = ModerationActionType.TIMEOUT,
            durationLabel = durationLabel,
            reason = cleanReason,
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(true, "⏳ ${updated.ign}-কে $durationLabel সময়ের জন্য অ্যাপ টাইমআউট দেওয়া হয়েছে! টাইমার শেষ না হওয়া পর্যন্ত অ্যাপ লক থাকবে।")
    }

    fun removeTimeout(
        targetKey: String,
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val updated = target.copy(
            timeoutUntilMillis = null,
            timeoutReason = null,
            timeoutBy = null
        )
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        appendModerationLog(
            target = updated,
            actionType = ModerationActionType.REMOVE_TIMEOUT,
            reason = "টাইমআউট তুলে নেওয়া হয়েছে",
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(true, "🔓 ${updated.ign}-এর অ্যাপ টাইমআউট প্রত্যাহার করা হয়েছে!")
    }

    fun muteUserChat(
        targetKey: String,
        durationHours: Long = 24,
        reason: String = "গ্লোবাল চ্যাটে স্প্যাম বা অশোভন আচরণ",
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val cleanReason = reason.trim().ifBlank { "গ্লোবাল চ্যাট নীতিমালা লঙ্ঘন" }
        val until = if (durationHours > 0) nowMillis + (durationHours * 3_600_000L) else null
        val durationLabel = if (durationHours > 0) "$durationHours ঘন্টা" else "অনির্দিষ্টকাল"
        val updated = target.copy(
            isChatMuted = true,
            chatMuteUntilMillis = until,
            chatMuteReason = cleanReason
        )
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        appendModerationLog(
            target = updated,
            actionType = ModerationActionType.CHAT_MUTE,
            durationLabel = durationLabel,
            reason = cleanReason,
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(true, "🔇 ${updated.ign}-কে $durationLabel-এর জন্য চ্যাট মিউট করা হয়েছে!")
    }

    fun unmuteUserChat(
        targetKey: String,
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val updated = target.copy(
            isChatMuted = false,
            chatMuteUntilMillis = null,
            chatMuteReason = null
        )
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        appendModerationLog(
            target = updated,
            actionType = ModerationActionType.UNMUTE_CHAT,
            reason = "চ্যাট মিউট প্রত্যাহার",
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(true, "🔊 ${updated.ign}-এর চ্যাট মিউট খোলা হয়েছে!")
    }

    fun setUserMatchBan(
        targetKey: String,
        banned: Boolean,
        reason: String = "স্লট শৃঙ্খলা ভঙ্গ বা টিমিং",
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val cleanReason = reason.trim().ifBlank { "টুর্নামেন্ট নিয়ম ভঙ্গ" }
        val updated = target.copy(
            isMatchBanned = banned,
            matchBanReason = if (banned) cleanReason else null
        )
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        appendModerationLog(
            target = updated,
            actionType = if (banned) ModerationActionType.MATCH_BAN else ModerationActionType.UNBAN_MATCH,
            reason = if (banned) cleanReason else "ম্যাচ স্লট খুলে দেওয়া হয়েছে",
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(
            true,
            if (banned) "🎯 ${updated.ign}-এর ম্যাচ স্লট এন্ট্রি নিষিদ্ধ (Match Ban) করা হয়েছে!"
            else "✅ ${updated.ign}-এর ম্যাচ ব্যান প্রত্যাহার করা হয়েছে!"
        )
    }

    fun setUserWalletFreeze(
        targetKey: String,
        frozen: Boolean,
        reason: String = "সন্দেহজনক TrxID বা পেমেন্ট অডিট",
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val cleanReason = reason.trim().ifBlank { "পেমেন্ট যাচাই ও অডিট" }
        val updated = target.copy(
            isWalletFrozen = frozen,
            walletFreezeReason = if (frozen) cleanReason else null
        )
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        appendModerationLog(
            target = updated,
            actionType = if (frozen) ModerationActionType.WALLET_FREEZE else ModerationActionType.UNFREEZE_WALLET,
            reason = if (frozen) cleanReason else "ওয়ালেট আনফ্রিজ করা হয়েছে",
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(
            true,
            if (frozen) "❄️ ${updated.ign}-এর ওয়ালেট ও ডিপোজিট/উইথড্র ফ্রিজ করা হয়েছে!"
            else "🔥 ${updated.ign}-এর ওয়ালেট আনফ্রিজ করা হয়েছে!"
        )
    }

    fun tempBanUser(
        targetKey: String,
        days: Int,
        reason: String = "গুরুতর নিয়ম লঙ্ঘন",
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val safeDays = days.coerceAtLeast(1)
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val cleanReason = reason.trim().ifBlank { "$safeDays দিনের অস্থায়ী অ্যাপ ব্যান" }
        val expiry = nowMillis + safeDays * 24L * 60 * 60 * 1000L
        val updated = target.copy(
            isBanned = true,
            banReason = "$cleanReason (${safeDays} দিনের ব্যান)",
            bannedAt = "আজ এইমাত্র",
            banExpiresAtMillis = expiry
        )
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        rebuildLiveLeaderboard()
        appendModerationLog(
            target = updated,
            actionType = ModerationActionType.TEMP_BAN,
            durationLabel = "$safeDays দিন",
            reason = cleanReason,
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(true, "🚫 ${updated.ign}-কে $safeDays দিনের জন্য অ্যাপ ব্যান করা হয়েছে!")
    }

    fun removeAllBadgesFromUser(
        targetKey: String,
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val allRevoked = target.revokedBadges + target.effectiveBadges
        val updated = target.copy(
            customBadges = emptySet(),
            revokedBadges = allRevoked
        )
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        rebuildLiveLeaderboard()
        appendModerationLog(
            target = updated,
            actionType = ModerationActionType.REMOVE_ALL_BADGES,
            reason = "শাস্তিমূলক সকল ব্যাজ বাতিল",
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(true, "💥 ${updated.ign}-এর সকল ব্যাজ তুলে নেওয়া হয়েছে!")
    }

    fun pardonAllPunishments(
        targetKey: String,
        moderatorRole: UserRole = _userProfile.value.role,
        moderatorName: String = currentActorLabel(moderatorRole),
        nowMillis: Long = System.currentTimeMillis()
    ): Pair<Boolean, String> {
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return Pair(false, "ইউজার পাওয়া যায়নি!")
        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val (allowed, denyMsg) = canActorModerateTarget(moderatorRole, target)
        if (!allowed) return Pair(false, denyMsg)

        val updated = target.copy(
            isBanned = false,
            banReason = null,
            bannedAt = null,
            banExpiresAtMillis = null,
            warningsCount = 0,
            moderationWarningsCount = 0,
            timeoutUntilMillis = null,
            timeoutReason = null,
            timeoutBy = null,
            isChatMuted = false,
            chatMuteUntilMillis = null,
            chatMuteReason = null,
            isMatchBanned = false,
            matchBanReason = null,
            isWalletFrozen = false,
            walletFreezeReason = null
        )
        list[idx] = updated
        _registeredUsers.value = list
        syncTargetToActiveProfile(updated)
        rebuildLiveLeaderboard()
        appendModerationLog(
            target = updated,
            actionType = ModerationActionType.PARDON_ALL,
            reason = "সকল শাস্তি, ব্যান, টাইমআউট, মিউট ও ওয়ার্নিং মওকুফ",
            moderatorRole = moderatorRole,
            moderatorName = moderatorName,
            timestampMillis = nowMillis
        )
        saveState()
        return Pair(true, "🌟 ${updated.ign}-এর সকল শাস্তি ও নিষেধাজ্ঞা সম্পূর্ণভাবে মওকুফ করা হয়েছে!")
    }

    fun banUser(userIdOrPhoneOrUid: String, reason: String): Boolean {
        val list = _registeredUsers.value.toMutableList()
        val index = list.indexOfFirst {
            it.id == userIdOrPhoneOrUid || it.phone == userIdOrPhoneOrUid || it.uid == userIdOrPhoneOrUid || it.ign.equals(userIdOrPhoneOrUid, ignoreCase = true)
        }
        val banMsg = reason.ifBlank { "প্রতারণা ও নিয়ম লঙ্ঘনের জন্য ওনার কর্তৃক ব্যান" }
        if (index != -1) {
            val user = list[index]
            // Cannot ban supreme owner
            if (user.role == UserRole.OWNER) return false
            if (_userProfile.value.role == UserRole.ADMIN && user.role == UserRole.ADMIN) return false

            val updated = user.copy(
                isBanned = true,
                banReason = banMsg,
                bannedAt = "আজ এইমাত্র",
                banExpiresAtMillis = null
            )
            list[index] = updated
            _registeredUsers.value = list
            syncTargetToActiveProfile(updated)
            rebuildLiveLeaderboard()
            appendModerationLog(updated, ModerationActionType.PERMANENT_BAN, "আজীবন", banMsg)
            saveState()
            return true
        } else {
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
            appendModerationLog(bannedEntry, ModerationActionType.PERMANENT_BAN, "আজীবন", banMsg)
            saveState()
            return true
        }
    }

    fun unbanUser(userIdOrPhoneOrUid: String): Boolean {
        val list = _registeredUsers.value.toMutableList()
        val index = list.indexOfFirst {
            it.id == userIdOrPhoneOrUid || it.phone == userIdOrPhoneOrUid || it.uid == userIdOrPhoneOrUid || it.ign.equals(userIdOrPhoneOrUid, ignoreCase = true)
        }
        if (index != -1) {
            val user = list[index]
            val updated = user.copy(
                isBanned = false,
                banReason = null,
                bannedAt = null,
                banExpiresAtMillis = null,
                warningsCount = 0,
                moderationWarningsCount = 0
            )
            list[index] = updated
            _registeredUsers.value = list
            syncTargetToActiveProfile(updated)
            rebuildLiveLeaderboard()
            appendModerationLog(updated, ModerationActionType.UNBAN, reason = "ব্যান প্রত্যাহার")
            saveState()
            return true
        }
        return false
    }

    fun checkAndClearExpiredModeration(nowMillis: Long = System.currentTimeMillis()): Int {
        var clearedCount = 0
        val active = _userProfile.value
        var activeClearedInRegistry = false

        _registeredUsers.value = _registeredUsers.value.map { user ->
            val banExpired = user.isBanned && user.banExpiresAtMillis != null && nowMillis >= user.banExpiresAtMillis
            val timeoutExpired = user.timeoutUntilMillis != null && nowMillis >= user.timeoutUntilMillis
            val muteExpired = user.isChatMuted && user.chatMuteUntilMillis != null && nowMillis >= user.chatMuteUntilMillis
            if (banExpired || timeoutExpired || muteExpired) {
                clearedCount++
                val isActiveMatch = (active.phone.isNotBlank() && active.phone == user.phone) ||
                    (active.uid.isNotBlank() && active.uid == user.uid)
                if (isActiveMatch) activeClearedInRegistry = true
                user.copy(
                    isBanned = if (banExpired) false else user.isBanned,
                    banReason = if (banExpired) null else user.banReason,
                    bannedAt = if (banExpired) null else user.bannedAt,
                    banExpiresAtMillis = if (banExpired) null else user.banExpiresAtMillis,
                    moderationWarningsCount = if (banExpired && user.moderationWarningsCount >= MODERATION_WARN_BAN_THRESHOLD) 0 else user.moderationWarningsCount,
                    timeoutUntilMillis = if (timeoutExpired) null else user.timeoutUntilMillis,
                    timeoutReason = if (timeoutExpired) null else user.timeoutReason,
                    timeoutBy = if (timeoutExpired) null else user.timeoutBy,
                    isChatMuted = if (muteExpired) false else user.isChatMuted,
                    chatMuteUntilMillis = if (muteExpired) null else user.chatMuteUntilMillis,
                    chatMuteReason = if (muteExpired) null else user.chatMuteReason
                )
            } else {
                user
            }
        }

        val activeBanExpired = active.isBanned && active.banExpiresAtMillis != null && nowMillis >= active.banExpiresAtMillis
        val activeTimeoutExpired = active.timeoutUntilMillis != null && nowMillis >= active.timeoutUntilMillis
        val activeMuteExpired = active.isChatMuted && active.chatMuteUntilMillis != null && nowMillis >= active.chatMuteUntilMillis
        if (activeBanExpired || activeTimeoutExpired || activeMuteExpired) {
            if (!activeClearedInRegistry) {
                clearedCount++
            }
            _userProfile.value = active.copy(
                isBanned = if (activeBanExpired) false else active.isBanned,
                banReason = if (activeBanExpired) null else active.banReason,
                banExpiresAtMillis = if (activeBanExpired) null else active.banExpiresAtMillis,
                moderationWarningsCount = if (activeBanExpired && active.moderationWarningsCount >= MODERATION_WARN_BAN_THRESHOLD) 0 else active.moderationWarningsCount,
                timeoutUntilMillis = if (activeTimeoutExpired) null else active.timeoutUntilMillis,
                timeoutReason = if (activeTimeoutExpired) null else active.timeoutReason,
                timeoutBy = if (activeTimeoutExpired) null else active.timeoutBy,
                isChatMuted = if (activeMuteExpired) false else active.isChatMuted,
                chatMuteUntilMillis = if (activeMuteExpired) null else active.chatMuteUntilMillis,
                chatMuteReason = if (activeMuteExpired) null else active.chatMuteReason
            )
        }
        if (clearedCount > 0) saveState()
        return clearedCount
    }

    fun checkAndClearExpiredBans(nowMillis: Long = System.currentTimeMillis()): Int =
        checkAndClearExpiredModeration(nowMillis)

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
            val promotedUser = existing.copy(
                role = UserRole.ADMIN,
                adminPasscode = code,
                appointedDate = "এইমাত্র"
            )
            list[index] = promotedUser
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
            cloudSync?.pushUser(promotedUser)
            cloudSync?.syncAdminGrant(promotedUser.id, grantAdmin = true, ign = promotedUser.ign)
            rebuildLiveLeaderboard()
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
            cloudSync?.pushUser(newAdmin)
            cloudSync?.syncAdminGrant(newAdmin.id, grantAdmin = true, ign = newAdmin.ign)
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
            val demotedUser = user.copy(
                role = UserRole.PLAYER,
                adminPasscode = null,
                customBannerUri = sanitizeBannerForAccount(UserRole.PLAYER, demotedBadges, user.customBannerUri),
                bannerColorId = sanitizeBannerColorForAccount(UserRole.PLAYER, demotedBadges, user.bannerColorId),
                customPfpUri = sanitizePfpForRole(UserRole.PLAYER, user.customPfpUri)
            )
            list[index] = demotedUser
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
            cloudSync?.pushUser(demotedUser)
            cloudSync?.syncAdminGrant(demotedUser.id, grantAdmin = false, ign = demotedUser.ign)
            rebuildLiveLeaderboard()
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
        if (targetUser.role == UserRole.OWNER && _userProfile.value.role == UserRole.ADMIN) {
            return Pair(false, "⛔ অ্যাডমিন ওনারের ব্যাজ পরিবর্তন করতে পারবেন না!")
        }
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

        rebuildLiveLeaderboard()

        appendModerationLog(
            target = updatedUser,
            actionType = ModerationActionType.GIVE_BADGE,
            reason = badge.title
        )
        saveState()
        cloudSync?.pushUserBadges(updatedUser)
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
        if (isActiveUserTimedOut()) {
            return Pair(false, "⏳ আপনি অ্যাপ টাইমআউটে আছেন! টাইমার শেষ হওয়ার আগে আবেদন করা যাবে না।")
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
        cloudSync?.pushBadgeAppeal(appeal)
        return Pair(true, "📩 '${requestedBadge.title}'-এর আবেদন সফলভাবে ওনার ও অ্যাডমিনের কাছে পাঠানো হয়েছে!")
    }

    fun sendAppealChatMessage(
        appealId: String,
        text: String,
        proofImageUri: String? = null
    ): Pair<Boolean, String> {
        checkAndClearExpiredModeration()
        val profile = _userProfile.value
        if (isActiveUserTimedOut()) {
            return Pair(false, "⏳ আপনি অ্যাপ টাইমআউটে আছেন! টাইমার শেষ হওয়ার আগে চ্যাট করা যাবে না।")
        }
        if (profile.isChatMuted && profile.role == UserRole.PLAYER) {
            return Pair(false, "🔇 আপনার চ্যাট মিউট করা আছে!")
        }
        val cleanText = text.trim()
        val cleanUri = proofImageUri?.trim()?.takeIf { it.isNotBlank() }
        if (cleanText.isBlank() && cleanUri == null) {
            return Pair(false, "দয়া করে একটি মেসেজ লিখুন বা প্রুফ ছবি যুক্ত করুন!")
        }
        val appeal = _badgeAppeals.value.firstOrNull { it.id == appealId }
            ?: return Pair(false, "আবেদনটি খুঁজে পাওয়া যায়নি!")

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
        var updatedAppeal: BadgeAppeal? = null
        _badgeAppeals.value = _badgeAppeals.value.map { item ->
            if (item.id == appealId) {
                item.copy(chatMessages = item.chatMessages + msg).also { updatedAppeal = it }
            } else {
                item
            }
        }
        saveState()
        updatedAppeal?.let { cloudSync?.pushBadgeAppeal(it) }
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

        var updatedAppeal: BadgeAppeal? = null
        _badgeAppeals.value = _badgeAppeals.value.map { item ->
            if (item.id == appealId) {
                item.copy(
                    status = AppealStatus.APPROVED,
                    grantedBadge = selectedBadgeToGrant,
                    reviewedBy = reviewerLabel,
                    adminNote = "✅ $reviewerLabel অনুমোদন করেছেন এবং '${selectedBadgeToGrant.title}' ব্যাজ দিয়েছেন।"
                ).also { updatedAppeal = it }
            } else {
                item
            }
        }
        saveState()
        updatedAppeal?.let { cloudSync?.pushBadgeAppeal(it) }
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

        var updatedAppeal: BadgeAppeal? = null
        _badgeAppeals.value = _badgeAppeals.value.map { item ->
            if (item.id == appealId) {
                item.copy(
                    status = AppealStatus.REJECTED,
                    reviewedBy = reviewerLabel,
                    adminNote = adminMsg
                ).also { updatedAppeal = it }
            } else {
                item
            }
        }
        saveState()
        updatedAppeal?.let { cloudSync?.pushBadgeAppeal(it) }
        if (userIdx != -1) {
            cloudSync?.pushUser(_registeredUsers.value[userIdx])
        }

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
        if (targetUser.role == UserRole.OWNER && _userProfile.value.role == UserRole.ADMIN) {
            return Pair(false, "⛔ অ্যাডমিন ওনারের ব্যাজ তুলে নিতে পারবেন না!")
        }
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

        rebuildLiveLeaderboard()

        appendModerationLog(
            target = updatedUser,
            actionType = ModerationActionType.TAKE_BADGE,
            reason = badge.title
        )
        saveState()
        cloudSync?.pushUserBadges(updatedUser)
        return Pair(true, "🗑️ ${targetUser.name} (${targetUser.phone})-এর থেকে '${badge.title}' ব্যাজটি তুলে নেওয়া হয়েছে!")
    }

    // ==================== OWNER SLOT, CAPACITY & APP MANAGEMENT ====================

    fun publishAppAnnouncement(
        title: String,
        message: String,
        isUrgent: Boolean = false
    ): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন অ্যাপ অ্যানাউন্সমেন্ট দিতে পারবেন!"
        }
        val cleanMsg = message.trim()
        if (cleanMsg.isBlank()) {
            return false to "দয়া করে অ্যানাউন্সমেন্টের মূল বার্তা লিখুন!"
        }
        val cleanTitle = title.trim().ifBlank {
            if (isUrgent) "🚨 জরুরি ঘোষণা" else "📢 অফিসিয়াল ঘোষণা"
        }
        val authorLabel = when (role) {
            UserRole.OWNER -> "👑 ওনার (${_userProfile.value.ign.ifBlank { "TH" }})"
            UserRole.ADMIN -> "🛡️ অ্যাডমিন (${_userProfile.value.ign.ifBlank { "TH" }})"
            UserRole.PLAYER -> "সিস্টেম"
        }
        val now = System.currentTimeMillis()
        _appCustomization.value = _appCustomization.value.copy(
            announcementTitle = cleanTitle.take(100),
            activeAnnouncement = cleanMsg.take(300),
            announcementAuthor = authorLabel.take(60),
            announcementUpdatedAtMillis = now,
            isAnnouncementUrgent = isUrgent,
            isAnnouncementEnabled = true
        )
        saveState()
        cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
        return true to "📢 লাইভ অ্যানাউন্সমেন্ট সবার ফোনে পাঠানো হয়েছে!"
    }

    fun clearAppAnnouncement(): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন অ্যানাউন্সমেন্ট বন্ধ করতে পারবেন!"
        }
        _appCustomization.value = _appCustomization.value.copy(
            activeAnnouncement = "",
            isAnnouncementEnabled = false,
            isAnnouncementUrgent = false,
            announcementUpdatedAtMillis = System.currentTimeMillis()
        )
        saveState()
        cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
        return true to "🗑️ লাইভ অ্যানাউন্সমেন্ট বন্ধ করা হয়েছে।"
    }

    fun publishMatchResultAndPayout(
        matchId: String,
        winnerTargetKey: String,
        winnerKills: Int,
        customPrizeAmount: Int? = null,
        runnerUpIgn: String = "",
        markMatchFinished: Boolean = true
    ): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন ম্যাচ রেজাল্ট প্রকাশ করতে পারবেন!"
        }
        val match = _matches.value.find { it.id == matchId }
            ?: return false to "❌ ম্যাচটি খুঁজে পাওয়া যায়নি!"
        val idx = findUserIndexByKey(winnerTargetKey)
        if (idx == -1) {
            return false to "❌ বিজয়ী প্লেয়ারের ফোন, UID বা IGN খুঁজে পাওয়া যায়নি!"
        }
        val safeKills = winnerKills.coerceIn(0, 99)
        val calculatedPrize = (customPrizeAmount ?: (match.booyahPrize + safeKills * match.perKillPrize)).coerceAtLeast(0)

        val list = _registeredUsers.value.toMutableList()
        val winner = list[idx]
        val nextMatchesPlayed = winner.matchesPlayed + 1
        val nextWins = winner.totalWins + 1
        val nextKills = winner.totalKills + safeKills
        val nextEarnings = winner.totalEarnings + calculatedPrize
        val nextBalance = winner.walletBalance + calculatedPrize
        val nextLevel = if (winner.role == UserRole.OWNER) 0 else (1 + (nextMatchesPlayed / 3))

        val updatedWinner = winner.copy(
            walletBalance = nextBalance,
            matchesPlayed = nextMatchesPlayed,
            totalWins = nextWins,
            totalKills = nextKills,
            totalEarnings = nextEarnings,
            level = nextLevel
        )
        list[idx] = updatedWinner
        _registeredUsers.value = list

        val active = _userProfile.value
        val isWinnerActive = (active.phone.isNotBlank() && active.phone == updatedWinner.phone) ||
            (active.uid.isNotBlank() && active.uid == updatedWinner.uid)
        if (isWinnerActive) {
            _walletBalance.value = nextBalance
            _userProfile.value = active.copy(
                matchesPlayed = nextMatchesPlayed,
                totalWins = nextWins,
                totalKills = nextKills,
                totalEarnings = nextEarnings,
                level = nextLevel
            )
        }

        if (calculatedPrize > 0) {
            val tx = WalletTransaction(
                id = UUID.randomUUID().toString(),
                title = "🏆 বুইয়া প্রাইজ (${match.title})",
                amount = calculatedPrize,
                isCredit = true,
                timeAgo = "এইমাত্র",
                statusText = "$safeKills কিল + বুইয়া • নতুন ব্যালেন্স: ৳$nextBalance",
                paymentMethod = "Match Result Auto-Payout",
                category = TransactionCategory.PRIZE_WIN,
                matchTitle = match.title,
                senderBkash = updatedWinner.phone,
                userPhone = updatedWinner.phone
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        val resultRecord = MatchResult(
            id = "res_${System.currentTimeMillis()}",
            matchTitle = match.title,
            category = match.category,
            completedDate = "আজ এইমাত্র",
            winnerIgn = updatedWinner.ign,
            winnerUid = updatedWinner.uid,
            winnerKills = safeKills,
            prizeMoney = calculatedPrize,
            runnerUpIgn = runnerUpIgn.trim().ifBlank { "-" },
            totalPlayers = match.bookedSlots.coerceAtLeast(1)
        )
        _results.value = listOf(resultRecord) + _results.value

        var updatedMatch: TournamentMatch? = null
        if (markMatchFinished && match.status != MatchStatus.FINISHED) {
            val finished = match.copy(status = MatchStatus.FINISHED)
            updatedMatch = finished
            _matches.value = _matches.value.map { if (it.id == match.id) finished else it }
        }

        rebuildLiveLeaderboard()
        saveState()
        cloudSync?.pushUser(updatedWinner)
        updatedMatch?.let { cloudSync?.pushMatch(it) }
        return true to "🏆 ${updatedWinner.ign}-কে ৳$calculatedPrize প্রাইজ ($safeKills কিল) ও লাইভ লিডারবোর্ডে #১/আপডেট করা হয়েছে!"
    }

    fun awardKillPrizeAndStats(
        targetKey: String,
        killsToAdd: Int,
        winsToAdd: Int = 0,
        matchesToAdd: Int = 1,
        prizeAmount: Int = 0,
        note: String = "কিল ও পারফরম্যান্স প্রাইজ"
    ): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন স্ট্যাটস ও প্রাইজ দিতে পারবেন!"
        }
        val idx = findUserIndexByKey(targetKey)
        if (idx == -1) return false to "❌ প্লেয়ার খুঁজে পাওয়া যায়নি!"

        val list = _registeredUsers.value.toMutableList()
        val target = list[idx]
        val safeKills = killsToAdd.coerceAtLeast(0)
        val safeWins = winsToAdd.coerceAtLeast(0)
        val safeMatches = matchesToAdd.coerceAtLeast(0)
        val safePrize = prizeAmount.coerceAtLeast(0)

        val nextMatches = target.matchesPlayed + safeMatches
        val nextWins = target.totalWins + safeWins
        val nextKills = target.totalKills + safeKills
        val nextEarnings = target.totalEarnings + safePrize
        val nextBalance = target.walletBalance + safePrize
        val nextLevel = if (target.role == UserRole.OWNER) 0 else (1 + (nextMatches / 3))

        val updated = target.copy(
            walletBalance = nextBalance,
            matchesPlayed = nextMatches,
            totalWins = nextWins,
            totalKills = nextKills,
            totalEarnings = nextEarnings,
            level = nextLevel
        )
        list[idx] = updated
        _registeredUsers.value = list

        val active = _userProfile.value
        if ((active.phone.isNotBlank() && active.phone == updated.phone) ||
            (active.uid.isNotBlank() && active.uid == updated.uid)
        ) {
            _walletBalance.value = nextBalance
            _userProfile.value = active.copy(
                matchesPlayed = nextMatches,
                totalWins = nextWins,
                totalKills = nextKills,
                totalEarnings = nextEarnings,
                level = nextLevel
            )
        }

        if (safePrize > 0) {
            val tx = WalletTransaction(
                id = UUID.randomUUID().toString(),
                title = "🎯 $note (+৳$safePrize)",
                amount = safePrize,
                isCredit = true,
                timeAgo = "এইমাত্র",
                statusText = "+$safeKills কিল • নতুন ব্যালেন্স: ৳$nextBalance",
                paymentMethod = "TH Prize Distribution",
                category = TransactionCategory.PRIZE_WIN,
                senderBkash = updated.phone,
                userPhone = updated.phone
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        rebuildLiveLeaderboard()
        saveState()
        cloudSync?.pushUser(updated)
        return true to "⚡ ${updated.ign}: +$safeKills কিল, +৳$safePrize প্রাইজ যোগ ও লাইভ লিডারবোর্ড আপডেট হয়েছে!"
    }

    private fun normalizeLegacyAppConfig(cfg: AppCustomization): AppCustomization {
        val migratedAnnouncement = if (cfg.activeAnnouncement == com.example.model.LEGACY_DEFAULT_ANNOUNCEMENT) {
            com.example.model.OFFICIAL_UPDATE_ANNOUNCEMENT
        } else {
            cfg.activeAnnouncement
        }
        val migratedWhatsApp = if (cfg.officialWhatsAppUrl.isBlank() || cfg.officialWhatsAppUrl == com.example.model.LEGACY_WHATSAPP_GROUP_URL) {
            com.example.model.OFFICIAL_WHATSAPP_CHANNEL_URL
        } else {
            cfg.officialWhatsAppUrl
        }
        return cfg.copy(
            activeAnnouncement = migratedAnnouncement,
            officialWhatsAppUrl = migratedWhatsApp
        )
    }

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
            noticeTickerText = noticeTicker.ifBlank { "ম্যাচ শুরুর ১৫ মিনিট আগে অ্যাপে কাস্টম রুম আইডি ও পাসওয়ার্ড দেওয়া হবে!" },
            activeAnnouncement = announcement.ifBlank { com.example.model.OFFICIAL_UPDATE_ANNOUNCEMENT },
            announcementUpdatedAtMillis = System.currentTimeMillis(),
            isAnnouncementEnabled = true
        )
        saveState()
        cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
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
            officialWhatsAppUrl = whatsAppUrl.ifBlank { com.example.model.OFFICIAL_WHATSAPP_CHANNEL_URL },
            minWithdraw = minWithdraw.coerceAtLeast(10),
            maxWithdraw = maxWithdraw.coerceAtLeast(minWithdraw)
        )
        officialWhatsAppGroupUrl.value = _appCustomization.value.officialWhatsAppUrl
        saveState()
        cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
    }

    fun toggleMaintenanceMode(enabled: Boolean, message: String) {
        _appCustomization.value = _appCustomization.value.copy(
            isMaintenanceMode = enabled,
            maintenanceMessage = message.ifBlank { "সার্ভার রক্ষণাবেক্ষণ চলছে। শীঘ্রই টুর্নামেন্ট শুরু হবে।" }
        )
        saveState()
        cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
    }

    fun updateExistingMatch(updatedMatch: TournamentMatch): Boolean {
        val normalizedMatch = if (updatedMatch.scheduledStartAtMillis <= 0L) {
            val target = System.currentTimeMillis() + updatedMatch.startsInMinutes.coerceAtLeast(0) * 60_000L
            updatedMatch.copy(scheduledStartAtMillis = target)
        } else {
            updatedMatch
        }
        var found = false
        _matches.value = _matches.value.map { match ->
            if (match.id == normalizedMatch.id) {
                found = true
                normalizedMatch
            } else match
        }
        if (found) {
            saveState()
            cloudSync?.pushMatch(normalizedMatch)
        }
        return found
    }

    fun rescheduleMatchStartMinutes(matchId: String, minutesFromNow: Int): Pair<Boolean, String> {
        val actorRole = _userProfile.value.role
        if (actorRole != UserRole.OWNER && actorRole != UserRole.ADMIN) {
            return false to "❌ শুধুমাত্র ওনার বা অ্যাডমিন ম্যাচের লাইভ সময় পরিবর্তন করতে পারবেন।"
        }
        val safeMins = minutesFromNow.coerceIn(1, 100000)
        val targetMillis = System.currentTimeMillis() + safeMins * 60_000L
        val timeLabel = formatMatchClockTimeLabel(targetMillis)
        var updatedMatch: TournamentMatch? = null
        _matches.value = _matches.value.map { m ->
            if (m.id == matchId) {
                m.copy(
                    startsInMinutes = safeMins,
                    scheduledStartAtMillis = targetMillis,
                    matchDateTime = timeLabel
                ).also { updatedMatch = it }
            } else m
        }
        val target = updatedMatch ?: return false to "❌ ম্যাচটি খুঁজে পাওয়া যায়নি!"
        saveState()
        cloudSync?.pushMatch(target)
        return true to "⏱️ '${target.title}' ম্যাচের লাইভ টাইমার $safeMins মিনিটে ($timeLabel) আপডেট হয়েছে!"
    }

    fun saveOrUpdateMatchPreset(preset: MatchPresetTemplate): Pair<Boolean, String> {
        val actorRole = _userProfile.value.role
        if (actorRole != UserRole.OWNER && actorRole != UserRole.ADMIN) {
            return false to "❌ শুধুমাত্র ওনার বা অ্যাডমিন প্রিসেট স্লট সেভ করতে পারবেন।"
        }
        val cleanName = preset.presetName.trim().ifBlank {
            "${preset.category.iconEmoji} ${preset.category.banglaTitle} (${preset.totalSlots} স্লট)"
        }
        val cleanTitle = preset.matchTitle.trim().ifBlank { cleanName }
        val normalized = preset.copy(
            id = preset.id.ifBlank { "preset_${System.currentTimeMillis()}" },
            presetName = cleanName,
            matchTitle = cleanTitle,
            defaultStartsInMinutes = preset.defaultStartsInMinutes.coerceIn(1, 100000),
            totalSlots = preset.totalSlots.coerceIn(2, 100)
        )
        val existingIdx = _matchPresets.value.indexOfFirst { it.id == normalized.id }
        _matchPresets.value = if (existingIdx >= 0) {
            _matchPresets.value.toMutableList().apply { set(existingIdx, normalized) }
        } else {
            _matchPresets.value + normalized
        }
        saveState()
        return true to "💾 '${normalized.presetName}' প্যানেলে সেভ করা হয়েছে!"
    }

    fun deleteMatchPreset(presetId: String): Pair<Boolean, String> {
        val actorRole = _userProfile.value.role
        if (actorRole != UserRole.OWNER && actorRole != UserRole.ADMIN) {
            return false to "❌ অনুমোদন নেই।"
        }
        val before = _matchPresets.value.size
        _matchPresets.value = _matchPresets.value.filterNot { it.id == presetId }
        return if (_matchPresets.value.size < before) {
            saveState()
            true to "🗑️ সেভড প্রিসেটটি মুছে ফেলা হয়েছে।"
        } else {
            false to "❌ প্রিসেট খুঁজে পাওয়া যায়নি।"
        }
    }

    fun launchMatchFromPreset(presetId: String, overrideStartsInMinutes: Int? = null): Pair<Boolean, String> {
        val actorRole = _userProfile.value.role
        if (actorRole != UserRole.OWNER && actorRole != UserRole.ADMIN) {
            return false to "❌ শুধুমাত্র ওনার বা অ্যাডমিন প্রিসেট থেকে ম্যাচ লাইভ করতে পারবেন।"
        }
        val preset = _matchPresets.value.firstOrNull { it.id == presetId }
            ?: return false to "❌ প্রিসেট খুঁজে পাওয়া যায়নি!"
        val mins = (overrideStartsInMinutes ?: preset.defaultStartsInMinutes).coerceIn(1, 100000)
        val targetMillis = System.currentTimeMillis() + mins * 60_000L
        val timeLabel = formatMatchClockTimeLabel(targetMillis)
        createNewMatch(
            title = preset.matchTitle,
            category = preset.category,
            matchType = preset.matchType,
            mapName = preset.mapName,
            matchDateTime = timeLabel,
            entryFee = preset.entryFee,
            totalPrize = preset.totalPrize,
            perKill = preset.perKillPrize,
            booyahPrize = preset.booyahPrize,
            totalSlots = preset.totalSlots,
            startsInMinutes = mins,
            scheduledStartAtMillis = targetMillis
        )
        return true to "🚀 '${preset.matchTitle}' (${preset.totalSlots} স্লট, ⏱️ ${mins}মি.) লাইভ হয়েছে!"
    }

    fun holdBrAndCsMatchesForLoneWolf(): Pair<Boolean, String> {
        val actorRole = _userProfile.value.role
        if (actorRole != UserRole.OWNER && actorRole != UserRole.ADMIN) {
            return false to "❌ শুধুমাত্র ওনার বা অ্যাডমিন এই পরিবর্তন করতে পারবেন।"
        }
        val brCsMatches = _matches.value.filter {
            it.category == GameCategory.BR_RANK || it.category == GameCategory.CS_RANK
        }
        if (brCsMatches.isEmpty()) {
            return true to "🐺 আগে থেকেই শুধু Lone Wolf ম্যাচ চালু আছে এবং BR/CS প্রিসেট প্যানেলে সেভ আছে!"
        }
        brCsMatches.forEach { match ->
            cancelMatchAndRefundAll(match.id)
            cloudSync?.deleteMatchCloud(match.id)
        }
        _matches.value = _matches.value.filter {
            it.category != GameCategory.BR_RANK && it.category != GameCategory.CS_RANK
        }
        saveState()
        return true to "🐺 ${brCsMatches.size}টি BR/CS ম্যাচ হোল্ড করা হয়েছে (বুকিং থাকলে অটো-রিফান্ডসহ)! এখন শুধু Lone Wolf চালু আছে ও BR/CS প্রিসেটে সেভ আছে।"
    }

    fun deleteMatch(matchId: String): Boolean {
        val countBefore = _matches.value.size
        cancelMatchAndRefundAll(matchId)
        _matches.value = _matches.value.filter { it.id != matchId }
        val deleted = _matches.value.size < countBefore
        if (deleted) {
            saveState()
            cloudSync?.deleteMatchCloud(matchId)
        }
        return deleted
    }

    fun updateMatchSlotCapacityResult(matchId: String, newCapacity: Int): Pair<Boolean, String> {
        val targetMatch = _matches.value.find { it.id == matchId }
            ?: return false to "❌ ম্যাচটি খুঁজে পাওয়া যায়নি!"
        if (newCapacity !in 2..100) {
            return false to "❌ মোট স্লট সংখ্যা ২ থেকে ১০০-এর মধ্যে হতে হবে!"
        }
        if (newCapacity < targetMatch.bookedSlots) {
            return false to "❌ বর্তমানে ${targetMatch.bookedSlots} টি স্লট বুক আছে! এর নিচে কমানো যাবে না।"
        }
        val updatedMatch = targetMatch.copy(totalSlots = newCapacity)
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) updatedMatch else match
        }
        saveState()
        cloudSync?.pushMatch(updatedMatch)
        return true to "✅ '${targetMatch.title}' ম্যাচের স্লট সংখ্যা ${targetMatch.totalSlots} → $newCapacity করা হয়েছে!"
    }

    fun updateMatchSlotCapacity(matchId: String, newCapacity: Int): Boolean =
        updateMatchSlotCapacityResult(matchId, newCapacity).first

    fun createManualSlotBookingResult(
        matchId: String,
        slotNumber: Int,
        playerIgn: String,
        playerUid: String,
        phoneOrBkash: String,
        feePaid: Int
    ): Pair<Boolean, String> {
        val targetMatch = _matches.value.find { it.id == matchId }
            ?: return false to "❌ ম্যাচটি খুঁজে পাওয়া যায়নি!"
        if (slotNumber < 1 || slotNumber > targetMatch.totalSlots) {
            return false to "❌ স্লট নম্বর ১ থেকে ${targetMatch.totalSlots}-এর মধ্যে হতে হবে!"
        }

        // Compute occupied slots combining both explicit bookings & legacy slot count
        val explicitSlots = _myBookings.value.filter { it.matchId == matchId }.map { it.slotNumber }.toSet()
        val occupiedSlots = explicitSlots.toMutableSet()
        var candidate = 1
        while (occupiedSlots.size < targetMatch.bookedSlots && candidate <= targetMatch.totalSlots) {
            if (candidate !in occupiedSlots) occupiedSlots.add(candidate)
            candidate++
        }
        if (slotNumber in occupiedSlots) {
            return false to "❌ স্লট #$slotNumber ইতিমধ্যেই বুকড/দখলকৃত আছে! অনুগ্রহ করে একটি সবুজ ফাঁকা স্লট বেছে নিন।"
        }

        val cleanIgn = playerIgn.trim().ifBlank { "VIP_PLAYER" }
        val cleanUid = playerUid.trim().ifBlank { "0000000000" }
        val cleanPhone = phoneOrBkash.trim().ifBlank { officialBkashNumber }
        val resolvedFee = if (feePaid > 0) feePaid else targetMatch.entryFee

        val manualBooking = SlotBooking(
            bookingId = "BK-OWNER-${System.currentTimeMillis() % 100000}-${(10..99).random()}",
            matchId = targetMatch.id,
            matchTitle = targetMatch.title,
            category = targetMatch.category,
            matchDateTime = targetMatch.matchDateTime,
            slotNumber = slotNumber,
            playerIgn = cleanIgn,
            playerUid = cleanUid,
            bkashSenderNumber = cleanPhone,
            trxId = "OWNER-ADDED",
            feePaid = resolvedFee,
            isVerified = true,
            roomId = targetMatch.roomId,
            roomPassword = targetMatch.roomPassword
        )
        _myBookings.value = listOf(manualBooking) + _myBookings.value

        val updatedBookedCount = (targetMatch.bookedSlots + 1).coerceIn(occupiedSlots.size + 1, targetMatch.totalSlots)
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) match.copy(bookedSlots = updatedBookedCount) else match
        }
        saveState()
        cloudSync?.pushBooking(manualBooking, updatedBookedCount)
        return true to "✅ স্লট #$slotNumber-এ $cleanIgn (UID: $cleanUid) সফলভাবে যোগ ও ক্লাউডে সিঙ্ক হয়েছে!"
    }

    fun createManualSlotBooking(
        matchId: String,
        slotNumber: Int,
        playerIgn: String,
        playerUid: String,
        phoneOrBkash: String,
        feePaid: Int
    ): Boolean = createManualSlotBookingResult(matchId, slotNumber, playerIgn, playerUid, phoneOrBkash, feePaid).first

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
        cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
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
        if (found) {
            saveState()
            cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
        }
        return found
    }

    fun deleteStrictRule(ruleId: String): Boolean {
        val before = _strictRules.value.size
        _strictRules.value = _strictRules.value.filter { it.id != ruleId }
        val deleted = _strictRules.value.size < before
        if (deleted) {
            saveState()
            cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
        }
        return deleted
    }

    fun resetDefaultRules() {
        _strictRules.value = defaultRules
        saveState()
        cloudSync?.pushAppConfig(_appCustomization.value, _strictRules.value)
    }

    fun resetAppForFreshDownload() {
        val ownerUser = AppUser(
            id = "u_owner",
            name = "তানভির হাসান (ওনার)",
            phone = "01861583924",
            email = "owner@trustedth.com",
            ign = "TH★DEVIL⚡",
            uid = "11410365896",
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
        persistenceStore?.clearAll()
        masterOwnerPasskey = "TH-OWNER-7788"
        masterOwnerPin = "7788"
        _appCustomization.value = AppCustomization()
        officialWhatsAppGroupUrl.value = com.example.model.OFFICIAL_WHATSAPP_CHANNEL_URL
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
        _registeredUsers.value = listOf(ownerUser)
        val resetMatches = defaultInitialMatches()
        _matches.value = resetMatches
        _myBookings.value = emptyList()
        _results.value = emptyList()
        _transactions.value = emptyList()
        _depositRequests.value = emptyList()
        _withdrawRequests.value = emptyList()
        _leaderboard.value = emptyList()
        _badgeAppeals.value = emptyList()
        _moderationLogs.value = emptyList()
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
        _promoCodes.value = emptyList()
        _bonusDrops.value = emptyList()
        _communityPolls.value = emptyList()
        _triviaProgress.value = DailyTriviaProgress()
        _giveaways.value = emptyList()
        _reflexStats.value = ReflexGameStats()
        _matchPresets.value = defaultSavedMatchPresets()
        resetAiChat()
        _isLoggedIn.value = false
        cloudSync?.resetCloudCollectionForFreshDownload(
            defaultMatches = resetMatches,
            defaultConfig = AppCustomization(),
            defaultRules = defaultRules,
            ownerUser = ownerUser
        )
    }

    // ==================== USER PREFERENCES & REAL REFRESH ====================

    fun updateUserPreferences(
        roomAlertNotification: Boolean = _userPreferences.value.roomAlertNotification,
        fiveMinReminder: Boolean = _userPreferences.value.fiveMinReminder,
        autoLaunchGame: Boolean = _userPreferences.value.autoLaunchGame
    ) {
        _userPreferences.value = UserPreferences(
            roomAlertNotification = roomAlertNotification,
            fiveMinReminder = fiveMinReminder,
            autoLaunchGame = autoLaunchGame
        )
        saveState()
    }

    fun refreshAppAndCloudState(): String {
        checkAndClearExpiredModeration()
        rebuildLiveLeaderboard()
        saveState()
        val sync = cloudSync
        return if (sync != null && sync.isSignedInCloud()) {
            sync.detachListeners()
            startCloudSync()
            pushActiveUserToCloud()
            "🔄 লাইভ ক্লাউড সিঙ্ক, লিডারবোর্ড ও ডেটা রিফ্রেশ হয়েছে!"
        } else {
            "🔄 লোকাল ডেটা ও লাইভ লিডারবোর্ড রিফ্রেশ হয়েছে!"
        }
    }

    private fun currentActorKey(): String {
        val p = _userProfile.value
        val matched = _registeredUsers.value.firstOrNull {
            (p.phone.isNotBlank() && it.phone == p.phone) ||
                (p.uid.isNotBlank() && it.uid == p.uid)
        }
        return matched?.id?.ifBlank { p.phone.ifBlank { p.uid } }
            ?: p.phone.ifBlank { p.uid.ifBlank { p.ign } }
    }

    private fun currentStaffLabel(): String {
        val p = _userProfile.value
        return when (p.role) {
            UserRole.OWNER -> "👑 ওনার (${p.ign.ifBlank { "TH" }})"
            UserRole.ADMIN -> "🛡️ অ্যাডমিন (${p.ign.ifBlank { "Staff" }})"
            UserRole.PLAYER -> p.ign.ifBlank { "Player" }
        }
    }

    // ==================== PROMO / GIFT REDEEM CODES ====================

    fun createPromoCode(
        code: String,
        title: String,
        rewardAmount: Int,
        maxUses: Int
    ): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন প্রোমো কোড তৈরি করতে পারবেন!"
        }
        val cleanCode = code.trim().uppercase().replace(" ", "")
        if (cleanCode.length < 3 || cleanCode.length > 25) {
            return false to "❌ প্রোমো কোড ৩ থেকে ২৫ অক্ষরের মধ্যে হতে হবে!"
        }
        if (rewardAmount !in 1..500) {
            return false to "❌ পুরস্কার ৳১ থেকে ৳৫০০-এর মধ্যে হতে হবে!"
        }
        if (maxUses !in 1..1000) {
            return false to "❌ ব্যবহারের সীমা ১ থেকে ১০০০ জন হতে হবে!"
        }
        if (_promoCodes.value.any { it.code.equals(cleanCode, ignoreCase = true) }) {
            return false to "❌ '$cleanCode' কোডটি ইতিমধ্যেই চালু আছে!"
        }
        val promo = PromoCodeItem(
            id = "promo_${System.currentTimeMillis()}",
            code = cleanCode,
            title = title.trim().ifBlank { "🎁 স্পেশাল গিফট প্রোমো" },
            rewardAmount = rewardAmount,
            maxUses = maxUses,
            usedCount = 0,
            claimedByKeys = emptyList(),
            active = true,
            createdBy = currentStaffLabel(),
            createdAtMillis = System.currentTimeMillis()
        )
        _promoCodes.value = listOf(promo) + _promoCodes.value
        saveState()
        cloudSync?.pushPromoCode(promo)
        return true to "🎟️ '$cleanCode' (৳$rewardAmount • $maxUses জন) প্রোমো কোড লাইভ হয়েছে!"
    }

    fun togglePromoCodeActive(promoId: String, active: Boolean): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন পরিবর্তন করতে পারবেন!"
        }
        var updatedPromo: PromoCodeItem? = null
        _promoCodes.value = _promoCodes.value.map { p ->
            if (p.id == promoId) p.copy(active = active).also { updatedPromo = it } else p
        }
        val target = updatedPromo ?: return false to "❌ প্রোমো কোডটি পাওয়া যায়নি!"
        saveState()
        cloudSync?.pushPromoCode(target)
        return true to if (active) "✅ ${target.code} পুনরায় চালু হয়েছে!" else "⏸️ ${target.code} বন্ধ করা হয়েছে!"
    }

    fun deletePromoCode(promoId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন মুছতে পারবেন!"
        }
        val before = _promoCodes.value.size
        _promoCodes.value = _promoCodes.value.filter { it.id != promoId }
        if (_promoCodes.value.size == before) return false to "❌ কোডটি পাওয়া যায়নি!"
        saveState()
        cloudSync?.deletePromoCodeCloud(promoId)
        return true to "🗑️ প্রোমো কোডটি মুছে ফেলা হয়েছে।"
    }

    fun redeemPromoCode(rawCode: String): Pair<Boolean, String> {
        checkAndClearExpiredModeration()
        val profile = _userProfile.value
        if (!profile.isLoggedIn) return false to "❌ আগে অ্যাকাউন্টে লগইন করুন!"
        if (profile.isBanned) return false to "🚫 আপনার অ্যাকাউন্ট ব্যান থাকায় রিডিম করা যাবে না!"
        if (isActiveUserTimedOut()) return false to "⏳ টাইমআউট শেষ না হওয়া পর্যন্ত রিডিম করা যাবে না!"
        if (profile.isWalletFrozen) return false to "❄️ আপনার ওয়ালেট ফ্রিজ করা আছে!"
        if (profile.isRewardBlocked) return false to "🔒 আপনার অ্যাকাউন্টে বোনাস রিডিম ব্লক করা আছে!"

        val cleanCode = rawCode.trim().uppercase().replace(" ", "")
        if (cleanCode.isBlank()) return false to "🎟️ দয়া করে একটি প্রোমো কোড লিখুন!"

        val promo = _promoCodes.value.firstOrNull { it.code.equals(cleanCode, ignoreCase = true) }
            ?: return false to "❌ '$cleanCode' সঠিক প্রোমো কোড নয় বা মেয়াদ শেষ!"
        if (!promo.active) return false to "⏸️ '$cleanCode' প্রোমো কোডটি বর্তমানে বন্ধ রয়েছে!"
        if (promo.isExhausted) return false to "💨 '$cleanCode' কোডের সবগুলো ক্লেইম শেষ হয়ে গেছে!"

        val actorKey = currentActorKey()
        if (actorKey in promo.claimedByKeys) {
            return false to "⚠️ আপনি ইতিমধ্যেই '$cleanCode' কোডটি ব্যবহার করেছেন!"
        }

        val updatedPromo = promo.copy(
            usedCount = promo.usedCount + 1,
            claimedByKeys = promo.claimedByKeys + actorKey
        )
        _promoCodes.value = _promoCodes.value.map { if (it.id == promo.id) updatedPromo else it }
        _walletBalance.value += promo.rewardAmount

        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "🎟️ প্রোমো রিডিম (${promo.code})",
            amount = promo.rewardAmount,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "${promo.title} • +৳${promo.rewardAmount} ওয়ালেটে যুক্ত",
            paymentMethod = "Promo Redeem",
            category = TransactionCategory.PRIZE_WIN,
            trxId = promo.code,
            userPhone = profile.phone
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncActiveUserToRegistry()
        cloudSync?.claimPromoCodeCloud(updatedPromo, actorKey)
        return true to "🎉 অভিনন্দন! '${promo.code}' রিডিম করে +৳${promo.rewardAmount} ওয়ালেটে পেয়েছেন!"
    }

    // ==================== LIVE BONUS GIFT DROPS (বোনাস রেইন) ====================

    fun launchBonusGiftDrop(
        title: String,
        subtitle: String,
        amountPerClaim: Int,
        totalBoxes: Int
    ): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন বোনাস ড্রপ চালু করতে পারবেন!"
        }
        val cleanTitle = title.trim().ifBlank { "🎁 লাইভ বোনাস গিফট বক্স!" }
        if (amountPerClaim !in 1..200) return false to "❌ প্রতি বক্সে পুরস্কার ৳১ থেকে ৳২০০ হতে হবে!"
        if (totalBoxes !in 1..500) return false to "❌ মোট বক্স ১ থেকে ৫০০টি হতে হবে!"

        val drop = BonusGiftDrop(
            id = "drop_${System.currentTimeMillis()}",
            title = cleanTitle,
            subtitle = subtitle.trim().ifBlank { "আগে আসলে আগে পাবেন • দ্রুত ক্লেইম বাটনে চাপুন!" },
            amountPerClaim = amountPerClaim,
            totalBoxes = totalBoxes,
            claimedCount = 0,
            claimedByKeys = emptyList(),
            active = true,
            createdBy = currentStaffLabel(),
            createdAtMillis = System.currentTimeMillis()
        )
        _bonusDrops.value = listOf(drop) + _bonusDrops.value
        saveState()
        cloudSync?.pushBonusDrop(drop)
        return true to "🎁 '$cleanTitle' ($totalBoxes টি বক্স x ৳$amountPerClaim) সব প্লেয়ারের স্ক্রিনে লাইভ হয়েছে!"
    }

    fun claimBonusGiftDrop(dropId: String): Pair<Boolean, String> {
        checkAndClearExpiredModeration()
        val profile = _userProfile.value
        if (!profile.isLoggedIn) return false to "❌ আগে অ্যাকাউন্টে লগইন করুন!"
        if (profile.isBanned) return false to "🚫 ব্যানকৃত অ্যাকাউন্ট দিয়ে বোনাস নেওয়া যাবে না!"
        if (isActiveUserTimedOut()) return false to "⏳ টাইমআউট চলাকালীন বোনাস নেওয়া যাবে না!"
        if (profile.isWalletFrozen || profile.isRewardBlocked) {
            return false to "🔒 আপনার অ্যাকাউন্টে বোনাস গ্রহণ সীমিত রয়েছে!"
        }

        val drop = _bonusDrops.value.firstOrNull { it.id == dropId }
            ?: return false to "❌ গিফট ড্রপটি পাওয়া যায়নি!"
        if (!drop.active) return false to "⏸️ এই গিফট ড্রপটি শেষ হয়ে গেছে!"
        if (drop.isSoldOut) return false to "💨 দুঃখিত! সবগুলো বোনাস বক্স অন্যরা ক্লেইম করে ফেলেছে!"

        val actorKey = currentActorKey()
        if (actorKey in drop.claimedByKeys) {
            return false to "✅ আপনি এই গিফট ড্রপ থেকে ইতিমধ্যেই ৳${drop.amountPerClaim} ক্লেইম করেছেন!"
        }

        val updatedDrop = drop.copy(
            claimedCount = drop.claimedCount + 1,
            claimedByKeys = drop.claimedByKeys + actorKey
        )
        _bonusDrops.value = _bonusDrops.value.map { if (it.id == drop.id) updatedDrop else it }
        _walletBalance.value += drop.amountPerClaim

        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "🎁 লাইভ বোনাস ড্রপ (${drop.title})",
            amount = drop.amountPerClaim,
            isCredit = true,
            timeAgo = "এইমাত্র",
            statusText = "${drop.createdBy}-এর বোনাস রেইন থেকে +৳${drop.amountPerClaim}",
            paymentMethod = "Live Gift Drop",
            category = TransactionCategory.PRIZE_WIN,
            userPhone = profile.phone
        )
        _transactions.value = listOf(tx) + _transactions.value
        syncActiveUserToRegistry()
        cloudSync?.claimBonusDropCloud(drop.id, actorKey)
        return true to "🎉 বুম! গিফট বক্স খুলে +৳${drop.amountPerClaim} ওয়ালেটে পেয়েছেন!"
    }

    fun closeBonusGiftDrop(dropId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন ড্রপ বন্ধ করতে পারবেন!"
        }
        var target: BonusGiftDrop? = null
        _bonusDrops.value = _bonusDrops.value.map { d ->
            if (d.id == dropId) d.copy(active = false).also { target = it } else d
        }
        val closed = target ?: return false to "❌ ড্রপটি পাওয়া যায়নি!"
        saveState()
        cloudSync?.pushBonusDrop(closed)
        return true to "⏸️ '${closed.title}' গিফট ড্রপ বন্ধ করা হয়েছে।"
    }

    fun deleteBonusGiftDrop(dropId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন মুছতে পারবেন!"
        }
        _bonusDrops.value = _bonusDrops.value.filter { it.id != dropId }
        saveState()
        cloudSync?.deleteBonusDropCloud(dropId)
        return true to "🗑️ গিফট ড্রপটি মুছে ফেলা হয়েছে।"
    }

    // ==================== LIVE COMMUNITY POLLS ====================

    fun launchCommunityPoll(
        question: String,
        optionLabels: List<String>
    ): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন পোল চালু করতে পারবেন!"
        }
        val cleanQuestion = question.trim()
        val cleanOptions = optionLabels.map { it.trim() }.filter { it.isNotBlank() }.distinct().take(4)
        if (cleanQuestion.length < 3) return false to "❌ পোলের প্রশ্ন কমপক্ষে ৩ অক্ষরের হতে হবে!"
        if (cleanOptions.size < 2) return false to "❌ কমপক্ষে ২টি অপশন দিন!"

        val poll = LiveCommunityPoll(
            id = "poll_${System.currentTimeMillis()}",
            question = cleanQuestion,
            options = cleanOptions.mapIndexed { idx, label ->
                PollOptionItem(id = "opt_${idx + 1}", label = label, votes = 0)
            },
            votedByKeys = emptyList(),
            active = true,
            createdBy = currentStaffLabel(),
            createdAtMillis = System.currentTimeMillis()
        )
        _communityPolls.value = listOf(poll) + _communityPolls.value
        saveState()
        cloudSync?.pushCommunityPoll(poll)
        return true to "📊 লাইভ কমিউনিটি পোল সবার কাছে প্রকাশিত হয়েছে!"
    }

    fun voteCommunityPoll(pollId: String, optionId: String): Pair<Boolean, String> {
        val profile = _userProfile.value
        if (!profile.isLoggedIn) return false to "❌ ভোট দিতে আগে লগইন করুন!"
        if (profile.isBanned) return false to "🚫 ব্যানকৃত অ্যাকাউন্ট ভোট দিতে পারবে না!"

        val poll = _communityPolls.value.firstOrNull { it.id == pollId }
            ?: return false to "❌ পোলটি খুঁজে পাওয়া যায়নি!"
        if (!poll.active) return false to "⏸️ এই পোলের ভোটগ্রহণ শেষ হয়ে গেছে!"

        val actorKey = currentActorKey()
        if (actorKey in poll.votedByKeys) {
            return false to "✅ আপনি এই পোলে ইতিমধ্যেই ভোট দিয়েছেন!"
        }
        if (poll.options.none { it.id == optionId }) {
            return false to "❌ অপশনটি সঠিক নয়!"
        }

        val updatedPoll = poll.copy(
            options = poll.options.map { opt ->
                if (opt.id == optionId) opt.copy(votes = opt.votes + 1) else opt
            },
            votedByKeys = poll.votedByKeys + actorKey
        )
        _communityPolls.value = _communityPolls.value.map { if (it.id == poll.id) updatedPoll else it }
        saveState()
        cloudSync?.voteCommunityPollCloud(poll.id, optionId, actorKey)
        return true to "🗳️ আপনার ভোট সফলভাবে যুক্ত হয়েছে!"
    }

    fun closeCommunityPoll(pollId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন পোল বন্ধ করতে পারবেন!"
        }
        var target: LiveCommunityPoll? = null
        _communityPolls.value = _communityPolls.value.map { p ->
            if (p.id == pollId) p.copy(active = false).also { target = it } else p
        }
        val closed = target ?: return false to "❌ পোলটি পাওয়া যায়নি!"
        saveState()
        cloudSync?.pushCommunityPoll(closed)
        return true to "⏸️ পোলটি বন্ধ করা হয়েছে।"
    }

    fun deleteCommunityPoll(pollId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন মুছতে পারবেন!"
        }
        _communityPolls.value = _communityPolls.value.filter { it.id != pollId }
        saveState()
        cloudSync?.deleteCommunityPollCloud(pollId)
        return true to "🗑️ পোলটি মুছে ফেলা হয়েছে।"
    }

    // ==================== DAILY FREE FIRE TRIVIA CHALLENGE ====================

    fun answerDailyTrivia(selectedIndex: Int): Pair<Boolean, String> {
        checkAndClearExpiredModeration()
        val profile = _userProfile.value
        if (!profile.isLoggedIn) return false to "❌ খেলতে আগে লগইন করুন!"
        if (profile.isBanned) return false to "🚫 ব্যানকৃত অ্যাকাউন্টে ট্রিভিয়া বন্ধ আছে!"

        val today = FreeFireTriviaBank.todayDateKey()
        val progress = _triviaProgress.value
        if (progress.lastAttemptDateKey == today) {
            return false to "🧠 আজকের ডেইলি ফ্রি ফায়ার কুইজ ইতিমধ্যেই খেলা হয়েছে! আগামীকাল আবার আসুন।"
        }

        val question = FreeFireTriviaBank.questionForDay(progress.currentQuestionIndex)
        if (selectedIndex !in question.options.indices) {
            return false to "❌ সঠিক অপশন নির্বাচন করুন!"
        }

        val isCorrect = selectedIndex == question.correctIndex
        val updatedCorrectCount = progress.totalCorrectCount + if (isCorrect) 1 else 0

        _triviaProgress.value = DailyTriviaProgress(
            lastAttemptDateKey = today,
            lastAnsweredCorrectly = isCorrect,
            lastSelectedOption = selectedIndex,
            totalCorrectCount = updatedCorrectCount,
            currentQuestionIndex = (progress.currentQuestionIndex + 1) % FreeFireTriviaBank.questions.size
        )
        saveState()

        return if (isCorrect) {
            true to "🎉 দারুণ! একদম সঠিক উত্তর দিয়েছেন! (মোট সঠিক: ${updatedCorrectCount}টি)"
        } else {
            false to "❌ ভুল উত্তর! সঠিক উত্তর ছিল: ${question.options[question.correctIndex]}"
        }
    }

    // ==================== OWNER AUDIT & GLOBAL MATCH BROADCAST ====================

    fun broadcastMatchToGlobalChat(matchId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন ম্যাচ ব্রডকাস্ট করতে পারবেন!"
        }
        val match = _matches.value.firstOrNull { it.id == matchId }
            ?: return false to "❌ ম্যাচটি খুঁজে পাওয়া যায়নি!"
        val spotsLeft = (match.totalSlots - match.bookedSlots).coerceAtLeast(0)
        val roomHint = if (!match.roomId.isNullOrBlank()) {
            "\n🔑 রুম আইডি প্রকাশিত হয়েছে — যারা স্লট নিয়েছেন রুম ডিটেইলস দেখুন!"
        } else {
            "\n🔥 মাত্র $spotsLeft টি স্লট বাকি! এখনই ম্যাচ লিস্ট থেকে জয়েন করুন!"
        }
        val msgText = "📢 [ম্যাচ অ্যালার্ট] ${match.title} • ${match.mapName}\n⏰ সময়: ${match.matchDateTime} | এন্ট্রি: ৳${match.entryFee} | প্রাইজ: ৳${match.totalPrize}$roomHint"
        return sendGlobalChatMessage(
            text = msgText,
            replyToId = null,
            pinAnnouncement = true
        )
    }

    fun buildOwnerAuditSummaryText(): String {
        val users = _registeredUsers.value
        val playerWalletSum = users.sumOf { it.walletBalance }
        val totalDepositsApproved = _depositRequests.value.filter { it.status == RequestStatus.APPROVED }.sumOf { it.amount }
        val totalWithdrawsApproved = _withdrawRequests.value.filter { it.status == RequestStatus.APPROVED }.sumOf { it.amount }
        val pendingDeps = _depositRequests.value.count { it.status == RequestStatus.PENDING }
        val pendingWdrs = _withdrawRequests.value.count { it.status == RequestStatus.PENDING }
        val pendingAppeals = _badgeAppeals.value.count { it.status == AppealStatus.PENDING }
        val bannedCount = users.count { it.isBanned }
        val bookedSlotsTotal = _matches.value.sumOf { it.bookedSlots }
        val promoClaims = _promoCodes.value.sumOf { it.usedCount }
        val dropClaims = _bonusDrops.value.sumOf { it.claimedCount }
        return buildString {
            appendLine("📊 ${_appCustomization.value.appName} — লাইভ অডিট রিপোর্ট")
            appendLine("👥 মোট ইউজার: ${users.size} জন (ব্যান: $bannedCount জন)")
            appendLine("🎮 সক্রিয় ম্যাচ: ${_matches.value.size} টি | মোট বুকড স্লট: $bookedSlotsTotal টি")
            appendLine("💰 সব প্লেয়ারের মোট ওয়ালেট: ৳$playerWalletSum")
            appendLine("📥 অ্যাপ্রুভড ডিপোজিট: ৳$totalDepositsApproved (পেন্ডিং: $pendingDeps টি)")
            appendLine("📤 অ্যাপ্রুভড উইথড্র: ৳$totalWithdrawsApproved (পেন্ডিং: $pendingWdrs টি)")
            appendLine("🏅 পেন্ডিং ব্যাজ আপিল: $pendingAppeals টি")
            appendLine("🎟️ প্রোমো ক্লেইম: $promoClaims বার | 🎁 বোনাস ড্রপ ক্লেইম: $dropClaims বার")
            appendLine("🎉 লাইভ গিভওয়ে: ${_giveaways.value.count { it.status == GiveawayStatus.LIVE }} টি | মোট গিভওয়ে: ${_giveaways.value.size} টি")
            append("🌐 ক্লাউড সিঙ্ক: ${if (_isCloudConnected.value) "চালু (${_cloudUserEmail.value})" else "অফলাইন/বন্ধ"}")
        }
    }

    // ==================== UPGRADED LIVE MEGA GIVEAWAY SYSTEM ====================

    fun createGiveaway(
        title: String,
        description: String,
        prizeType: GiveawayPrizeType,
        prizeLabel: String,
        cashAmountPerWinner: Int = 0,
        winnersCount: Int = 1,
        maxEntries: Int = 50,
        announceInChat: Boolean = true
    ): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন গিভওয়ে চালু করতে পারবেন!"
        }
        val cleanTitle = title.trim()
        if (cleanTitle.length < 3) {
            return false to "❌ গিভওয়ের সুন্দর একটি শিরোনাম লিখুন!"
        }
        val cleanPrizeLabel = prizeLabel.trim().ifBlank {
            when (prizeType) {
                GiveawayPrizeType.CASH_TAKA -> "৳${cashAmountPerWinner.coerceAtLeast(10)} নগদ"
                GiveawayPrizeType.FF_DIAMONDS -> "১০০ ডায়মন্ড"
                GiveawayPrizeType.WEEKLY_PASS -> "উইকলি মেম্বারশিপ"
            }
        }
        val cashAmt = if (prizeType == GiveawayPrizeType.CASH_TAKA) {
            if (cashAmountPerWinner !in 5..5000) {
                return false to "❌ নগদ পুরস্কার ৳৫ থেকে ৳৫০০০-এর মধ্যে হতে হবে!"
            }
            cashAmountPerWinner
        } else 0
        if (winnersCount !in 1..20) {
            return false to "❌ বিজয়ী সংখ্যা ১ থেকে ২০ জনের মধ্যে দিন!"
        }
        if (maxEntries !in 2..500) {
            return false to "❌ সর্বোচ্চ টিকিট সংখ্যা ২ থেকে ৫০০-এর মধ্যে দিন!"
        }

        val giveaway = GiveawayEvent(
            id = "gw_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
            title = cleanTitle.take(100),
            description = description.trim().ifBlank { "ফ্রি টিকিট নিয়ে মেগা গিভওয়েতে অংশগ্রহণ করুন!" }.take(240),
            prizeType = prizeType,
            prizeLabel = cleanPrizeLabel.take(60),
            cashAmountPerWinner = cashAmt,
            winnersCount = winnersCount,
            maxEntries = maxEntries,
            entries = emptyList(),
            winners = emptyList(),
            status = GiveawayStatus.LIVE,
            createdBy = currentStaffLabel(),
            createdAtMillis = System.currentTimeMillis()
        )
        _giveaways.value = listOf(giveaway) + _giveaways.value
        saveState()
        cloudSync?.pushGiveaway(giveaway)

        if (announceInChat) {
            sendGlobalChatMessage(
                text = "🎉 [লাইভ মেগা গিভওয়ে] ${giveaway.title}\n${giveaway.prizeType.emoji} পুরস্কার: ${giveaway.prizeLabel} (${giveaway.winnersCount} জন বিজয়ী)\n🎟️ মোট টিকিট: ${giveaway.maxEntries} টি — ম্যাচ স্ক্রিন থেকে এখনই ফ্রি জয়েন করুন!",
                replyToId = null,
                pinAnnouncement = true
            )
        }
        return true to "🎉 মেগা গিভওয়ে সফলভাবে সবার কাছে লাইভ করা হয়েছে!"
    }

    fun joinGiveaway(giveawayId: String): Pair<Boolean, String> {
        checkAndClearExpiredModeration()
        val profile = _userProfile.value
        if (!profile.isLoggedIn) return false to "❌ গিভওয়েতে জয়েন করতে আগে লগইন করুন!"
        if (profile.isBanned) return false to "🚫 ব্যানকৃত অ্যাকাউন্ট গিভওয়েতে অংশ নিতে পারবে না!"
        if (profile.isRewardBlocked) return false to "⛔ রিওয়ার্ড ব্লক থাকায় অংশগ্রহণ করা যাচ্ছে না।"

        val giveaway = _giveaways.value.firstOrNull { it.id == giveawayId }
            ?: return false to "❌ গিভওয়েটি খুঁজে পাওয়া যায়নি!"
        if (giveaway.status != GiveawayStatus.LIVE) {
            return false to "⏸️ এই গিভওয়েতে জয়েন করার সময় শেষ হয়ে গেছে!"
        }
        if (giveaway.isFull) {
            return false to "😅 দুঃখিত! সবগুলো টিকিট (${giveaway.maxEntries}/${giveaway.maxEntries}) পূর্ণ হয়ে গেছে।"
        }

        val actorKey = currentActorKey()
        val alreadyJoined = giveaway.entries.any { entry ->
            entry.actorKey == actorKey ||
                (profile.uid.isNotBlank() && entry.uid == profile.uid)
        }
        if (alreadyJoined) {
            return false to "✅ আপনি ইতিমধ্যেই এই গিভওয়েতে ফ্রি টিকিট নিয়েছেন!"
        }

        val newEntry = GiveawayEntry(
            actorKey = actorKey,
            authUid = cloudSync?.currentCloudUid().orEmpty(),
            ign = profile.ign.ifBlank { profile.accountName.ifBlank { "Player" } },
            uid = profile.uid.ifBlank { "N/A" },
            joinedAtMillis = System.currentTimeMillis()
        )
        val updated = giveaway.copy(entries = giveaway.entries + newEntry)
        _giveaways.value = _giveaways.value.map { if (it.id == giveawayId) updated else it }
        saveState()
        cloudSync?.joinGiveawayCloud(giveawayId, newEntry)
        return true to "🎟️ অভিনন্দন! গিভওয়ে টিকিট কনফার্ম হয়েছে (${updated.entries.size}/${updated.maxEntries})!"
    }

    fun drawGiveawayWinners(giveawayId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন গিভওয়ে লটারি ড্র করতে পারবেন!"
        }
        val giveaway = _giveaways.value.firstOrNull { it.id == giveawayId }
            ?: return false to "❌ গিভওয়েটি পাওয়া যায়নি!"
        if (giveaway.status == GiveawayStatus.DRAWN) {
            return false to "🏆 এই গিভওয়ের বিজয়ী ইতিমধ্যেই নির্বাচিত হয়েছে!"
        }
        if (giveaway.entries.isEmpty()) {
            return false to "⚠️ এখনো কেউ এই গিভওয়েতে জয়েন করেনি — কমপক্ষে ১ জন জয়েন হলে ড্র করুন!"
        }

        val selectedEntries = giveaway.entries.shuffled().take(giveaway.winnersCount.coerceAtLeast(1))
        val winners = selectedEntries.map { entry ->
            GiveawayWinner(
                actorKey = entry.actorKey,
                ign = entry.ign,
                uid = entry.uid,
                prizeLabel = giveaway.prizeLabel
            )
        }

        // Auto-credit wallet only when prizeType is CASH_TAKA
        if (giveaway.prizeType == GiveawayPrizeType.CASH_TAKA && giveaway.cashAmountPerWinner > 0) {
            val activeKey = currentActorKey()
            val newWinnerTxs = mutableListOf<WalletTransaction>()
            winners.forEach { winner ->
                var matchedWinnerPhone = ""
                _registeredUsers.value = _registeredUsers.value.map { u ->
                    val isWinnerUser = u.id == winner.actorKey ||
                        u.phone == winner.actorKey ||
                        (winner.uid.isNotBlank() && winner.uid != "N/A" && u.uid == winner.uid)
                    if (isWinnerUser) {
                        val updatedUser = u.copy(
                            walletBalance = u.walletBalance + giveaway.cashAmountPerWinner,
                            totalEarnings = u.totalEarnings + giveaway.cashAmountPerWinner
                        )
                        matchedWinnerPhone = updatedUser.phone
                        cloudSync?.pushUser(updatedUser)
                        updatedUser
                    } else u
                }
                val isCurrentUser = winner.actorKey == activeKey ||
                    (_userProfile.value.uid.isNotBlank() && winner.uid == _userProfile.value.uid)
                if (isCurrentUser) {
                    _walletBalance.value += giveaway.cashAmountPerWinner
                    _userProfile.value = _userProfile.value.copy(
                        totalEarnings = _userProfile.value.totalEarnings + giveaway.cashAmountPerWinner
                    )
                    if (matchedWinnerPhone.isBlank()) matchedWinnerPhone = _userProfile.value.phone
                }
                val tx = WalletTransaction(
                    id = "tx_gw_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4)}",
                    title = "🎉 গিভওয়ে জয় (${giveaway.title})",
                    amount = giveaway.cashAmountPerWinner,
                    isCredit = true,
                    timeAgo = "এইমাত্র",
                    statusText = "লাইভ লটারি বিজয়ী পুরস্কার (+৳${giveaway.cashAmountPerWinner})",
                    paymentMethod = "Mega Giveaway",
                    category = TransactionCategory.PRIZE_WIN,
                    userPhone = matchedWinnerPhone
                )
                newWinnerTxs += tx
            }
            if (newWinnerTxs.isNotEmpty()) {
                _transactions.value = newWinnerTxs + _transactions.value
            }
            rebuildLiveLeaderboard()
        }

        val updatedGiveaway = giveaway.copy(
            winners = winners,
            status = GiveawayStatus.DRAWN
        )
        _giveaways.value = _giveaways.value.map { if (it.id == giveawayId) updatedGiveaway else it }
        saveState()
        cloudSync?.pushGiveaway(updatedGiveaway)

        val winnerNames = winners.joinToString(", ") { "🏆 ${it.ign} (UID: ${it.uid})" }
        sendGlobalChatMessage(
            text = "🎊 [গিভওয়ে রেজাল্ট] \"${giveaway.title}\" এর লাইভ লটারি ড্র সম্পন্ন!\nপুরস্কার (${giveaway.prizeLabel}) জিতেছেন:\n$winnerNames\nঅভিনন্দন বিজয়ীদের! 🔥",
            replyToId = null,
            pinAnnouncement = true
        )

        return true to "🎉 লাইভ ড্র সম্পন্ন! বিজয়ী: ${winners.joinToString(", ") { it.ign }}"
    }

    fun closeGiveaway(giveawayId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন গিভওয়ে বন্ধ করতে পারবেন!"
        }
        var closed: GiveawayEvent? = null
        _giveaways.value = _giveaways.value.map { g ->
            if (g.id == giveawayId) g.copy(status = GiveawayStatus.CLOSED).also { closed = it } else g
        }
        val target = closed ?: return false to "❌ গিভওয়েটি পাওয়া যায়নি!"
        saveState()
        cloudSync?.pushGiveaway(target)
        return true to "⏸️ গিভওয়েটি বন্ধ করা হয়েছে।"
    }

    fun deleteGiveaway(giveawayId: String): Pair<Boolean, String> {
        val role = _userProfile.value.role
        if (role != UserRole.OWNER && role != UserRole.ADMIN) {
            return false to "⛔ কেবল ওনার বা অ্যাডমিন গিভওয়ে মুছতে পারবেন!"
        }
        _giveaways.value = _giveaways.value.filter { it.id != giveawayId }
        saveState()
        cloudSync?.deleteGiveawayCloud(giveawayId)
        return true to "🗑️ গিভওয়েটি মুছে ফেলা হয়েছে।"
    }

    // ==================== ADDICTIVE FF ONE-TAP REFLEX MINI-GAME (NO MONEY) ====================

    fun recordReflexGameResult(
        score: Int,
        bestReactionMs: Int,
        maxCombo: Int
    ): ReflexGameStats {
        val prev = _reflexStats.value
        val validMs = bestReactionMs.coerceAtLeast(0)
        val newBestMs = when {
            validMs == 0 -> prev.bestReactionMs
            prev.bestReactionMs == 0 -> validMs
            else -> minOf(prev.bestReactionMs, validMs)
        }
        val updated = ReflexGameStats(
            bestScore = maxOf(prev.bestScore, score.coerceAtLeast(0)),
            bestReactionMs = newBestMs,
            gamesPlayed = prev.gamesPlayed + 1,
            bestCombo = maxOf(prev.bestCombo, maxCombo.coerceAtLeast(0))
        )
        _reflexStats.value = updated
        saveState()
        return updated
    }
}

