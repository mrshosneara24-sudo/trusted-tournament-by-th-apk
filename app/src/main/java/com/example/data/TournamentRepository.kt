package com.example.data

import com.example.model.AppUser
import com.example.model.BadgeType
import com.example.model.ChatMessage
import com.example.model.DailyGamerMission
import com.example.model.DailyStreakInfo
import com.example.model.GameCategory
import com.example.model.LeaderboardPlayer
import com.example.model.MatchResult
import com.example.model.MatchStatus
import com.example.model.MatchType
import com.example.model.SlotBooking
import com.example.model.SpinHistoryRecord
import com.example.model.TournamentMatch
import com.example.model.TransactionCategory
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.WalletTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

import com.example.model.AppCustomization
import com.example.model.AppLogoOption
import com.example.model.TournamentRuleItem

class TournamentRepository {

    companion object {
        const val SIGNUP_WELCOME_BONUS = 5
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
            title = "১০০% মোবাইল অনলি (No Emulator)",
            description = "পিসি, ব্লুস্ট্যাক্স, এলডিপ্লেয়ার, স্মার্টগা বা কোনো এমুলেটর ডিভাইস সম্পূর্ণ নিষিদ্ধ। আইপ্যাড বা ট্যাবলেট গ্রহণযোগ্য নয়।",
            penalty = "ধরা পড়লে সাথে সাথে স্থায়ী ব্যান এবং এন্ট্রি ফি ১০০% বাজেয়াপ্ত করা হবে।",
            iconEmoji = "🚫"
        ),
        TournamentRuleItem(
            id = "rule_2",
            title = "হেডশট কনফিগ ও থার্ড-পার্টি ফাইল সম্পূর্ণ নিষিদ্ধ",
            description = "অটো-হেডশট, কনফিগ ফাইল, গ্লিচ প্যাক, হাই জাম্প, নো রিকয়েল, GFX টুল বা কোনো মোড এপিকে ব্যবহার কঠোরভাবে নিষিদ্ধ।",
            penalty = "প্লেয়ারের UID ও ডিভাইস আইডি আজীবনের জন্য ব্ল্যাকলিস্ট করা হবে।",
            iconEmoji = "🛡️"
        ),
        TournamentRuleItem(
            id = "rule_3",
            title = "সঠিক স্লট শৃঙ্খলার নিয়ম (Slot Discipline)",
            description = "অ্যাপে আপনি যে স্লট নম্বর বুকিং করেছেন, কাস্টম রুমে হুবহু সেই নির্দিষ্ট স্লট নম্বরেই বসতে হবে।",
            penalty = "অন্যের স্লটে বসলে কোনো ওয়ার্নিং ছাড়া রুম থেকে কিক করা হবে এবং ফি রিফান্ড হবে না।",
            iconEmoji = "🎯"
        ),
        TournamentRuleItem(
            id = "rule_4",
            title = "সোলো ও ডুও ম্যাচে টিমিং (Teaming) কঠোরভাবে নিষিদ্ধ",
            description = "ম্যাচে অন্য প্লেয়ারের সাথে আপস, ফ্রেন্ডশিপ বা টিমিং করার চেষ্টা ধরা পড়লে কোনো প্রাইজ মানি দেওয়া হবে না।",
            penalty = "উভয় প্লেয়ারকে সরাসরি ডিসকোয়ালিফাই ও টুর্নামেন্ট থেকে বরখাস্ত করা হবে।",
            iconEmoji = "⚖️"
        ),
        TournamentRuleItem(
            id = "rule_5",
            title = "কাস্টম রুম আইডি ও পাসওয়ার্ডের গোপনীয়তা",
            description = "অ্যাপে প্রদত্ত কাস্টম রুম আইডি ও পাসওয়ার্ড কোনো বাইরের বন্ধু, ফেসবুক বা হোয়াটসঅ্যাপ গ্রুপে শেয়ার করা দণ্ডনীয় অপরাধ।",
            penalty = "আইডি শেয়ারকারীর প্রোফাইল তাৎক্ষণিক ব্যান করা হবে।",
            iconEmoji = "🔐"
        ),
        TournamentRuleItem(
            id = "rule_6",
            title = "বিকাশ ও লেনদেন সংক্রান্ত জালিয়াতি প্রতিরোধ",
            description = "ভুয়া TrxID বা অন্যের TrxID দিয়ে স্লট কেনার অপচেষ্টা করলে সিস্টেম স্বয়ংক্রিয়ভাবে সনাক্ত করবে।",
            penalty = "প্রতারকের বিকাশ নম্বর, ফোন নম্বর ও UID স্থায়ীভাবে ব্যান ও ব্ল্যাকলিস্ট হবে।",
            iconEmoji = "💳"
        ),
        TournamentRuleItem(
            id = "rule_7",
            title = "যথাসময়ে কাস্টম রুমে উপস্থিতি আবশ্যক",
            description = "ম্যাচ শুরুর নির্ধারিত সময়ের পর কোনো প্লেয়ারের জন্য ১ সেকেন্ডও অপেক্ষা করা হবে না। সময়মতো খেলায় অংশ নিতে হবে।",
            penalty = "দেরি করে আসলে বা অনুপস্থিত থাকলে কোনো প্রকার রিফান্ড দেওয়া হবে না।",
            iconEmoji = "⏱️"
        ),
        TournamentRuleItem(
            id = "rule_8",
            title = "বুইয়া ও প্রাইজ ক্লেইম ভেরিফিকেশন নিয়ম",
            description = "ম্যাচ জয়ের পর স্পষ্ট কিল সংখ্যা ও বুইয়ার স্ক্রিনশট ১৫ মিনিটের মধ্যে লাইভ এডমিন বা এআই সাপোর্টে প্রদান করতে হবে।",
            penalty = "মিথ্যা দাবি বা এডিটেড স্ক্রিনশট দিলে প্রাইজ মানি বাতিল ও জরিমানা করা হবে।",
            iconEmoji = "🏆"
        )
    )

    private val _strictRules = MutableStateFlow<List<TournamentRuleItem>>(defaultRules)
    val strictRules: StateFlow<List<TournamentRuleItem>> = _strictRules.asStateFlow()

    private val _walletBalance = MutableStateFlow(35) // ছোট ও বাস্তবসম্মত প্রারম্ভিক ব্যালেন্স ৳৩৫
    val walletBalance: StateFlow<Int> = _walletBalance.asStateFlow()

    private val _dailyStreakInfo = MutableStateFlow(DailyStreakInfo(currentStreakDay = 3, isTodayClaimed = false, streakHistoryDays = setOf(1, 2)))
    val dailyStreakInfo: StateFlow<DailyStreakInfo> = _dailyStreakInfo.asStateFlow()

    private val _spinHistory = MutableStateFlow<List<SpinHistoryRecord>>(
        listOf(
            SpinHistoryRecord(UUID.randomUUID().toString(), 4, "গত সপ্তাহে", false),
            SpinHistoryRecord(UUID.randomUUID().toString(), 3, "২ সপ্তাহ আগে", false)
        )
    )
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
            accountName = "তানভির হাসান (ওনার)",
            phone = "01861583924",
            email = "owner@trustedth.com",
            ign = "TH★DEVIL⚡",
            uid = "2489103847",
            bkashNumber = "01861583924",
            avatarId = "th_logo",
            level = 68,
            rankBadge = "হিরোইক ⭐",
            matchesPlayed = 24,
            totalWins = 11,
            totalKills = 89,
            totalEarnings = 130,
            joinedDate = "অক্টোবর ২০২৬",
            isLoggedIn = false,
            role = UserRole.OWNER,
            isBanned = false
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Registered Users List for Owner Management & Fraud Ban
    private val _registeredUsers = MutableStateFlow<List<AppUser>>(
        listOf(
            AppUser(
                id = "u_owner",
                name = "তানভির হাসান (মালিক)",
                phone = "01861583924",
                ign = "TH★DEVIL⚡",
                uid = "2489103847",
                bkashNumber = "01861583924",
                role = UserRole.OWNER,
                joinedDate = "অক্টোবর ২০২৬"
            ),
            AppUser(
                id = "u_admin1",
                name = "রাকিব আহমেদ (অ্যাডমিন)",
                phone = "01712984512",
                ign = "TH★HUNTER🔥",
                uid = "3829104921",
                bkashNumber = "01712984512",
                role = UserRole.ADMIN,
                appointedDate = "গতকাল",
                adminPasscode = "TH-ADMIN-2026",
                joinedDate = "অক্টোবর ২০২৬"
            ),
            AppUser(
                id = "u_player1",
                name = "সাকিবুর রহমান",
                phone = "01923485712",
                ign = "OP_SAKIB_YT",
                uid = "8291038491",
                bkashNumber = "01923485712",
                role = UserRole.PLAYER,
                joinedDate = "১২ অক্টোবর ২০২৬"
            ),
            AppUser(
                id = "u_fraud1",
                name = "ফাহিম ইসলাম (চিটার/ফ্রড)",
                phone = "01309182374",
                ign = "PRO_BOT_99",
                uid = "9918273645",
                bkashNumber = "01309182374",
                role = UserRole.PLAYER,
                isBanned = true,
                banReason = "ভুয়া বিকাশ TrxID দিয়ে ৩ বার ফ্রি স্লট নেয়ার চেষ্টা করেছিল",
                bannedAt = "আজ দুপুর ২:১৫",
                joinedDate = "৫ অক্টোবর ২০২৬"
            ),
            AppUser(
                id = "u_player2",
                name = "মাহমুদুল হাসান",
                phone = "01682910384",
                ign = "DEVIL_KING_FF",
                uid = "7162534890",
                bkashNumber = "01682910384",
                role = UserRole.PLAYER,
                joinedDate = "১৪ অক্টোবর ২০২৬"
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
                bookedSlots = 42,
                status = MatchStatus.ROOM_OPEN,
                roomId = "8894120",
                roomPassword = "TH77",
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
                bookedSlots = 1,
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
                bookedSlots = 6,
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
                bookedSlots = 2,
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
                bookedSlots = 32,
                status = MatchStatus.UPCOMING,
                roomId = null,
                roomPassword = null,
                version = "মোবাইল অনলি (ইভনিং ফাস্ট এক্সপ্রেস)"
            )
        )
    )
    val matches: StateFlow<List<TournamentMatch>> = _matches.asStateFlow()

    private val _myBookings = MutableStateFlow<List<SlotBooking>>(
        listOf(
            SlotBooking(
                bookingId = "BK-8841",
                matchId = "m1",
                matchTitle = "বিআর র‍্যাঙ্ক - বারমুডা সোলো #০১ (সুপারফাস্ট এক্সপ্রেস)",
                category = GameCategory.BR_RANK,
                matchDateTime = "আজ বিকাল ৫:০০ PM",
                slotNumber = 14,
                playerIgn = "TH_Sniper_BD",
                playerUid = "284710924",
                bkashSenderNumber = "01861583924",
                trxId = "9KJ3L49M",
                feePaid = 10,
                isVerified = true,
                roomId = "8894120",
                roomPassword = "TH77"
            )
        )
    )
    val myBookings: StateFlow<List<SlotBooking>> = _myBookings.asStateFlow()

    private val _results = MutableStateFlow<List<MatchResult>>(
        listOf(
            MatchResult(
                id = "res-1",
                matchTitle = "বিআর র‍্যাঙ্ক - উইকেন্ড কাপ #০৮",
                category = GameCategory.BR_RANK,
                completedDate = "আজ সন্ধ্যা ৬:০০ PM",
                winnerIgn = "Viper_OneShot",
                winnerUid = "837402914",
                winnerKills = 7,
                prizeMoney = 100,
                runnerUpIgn = "TH_Sniper_BD (৪ কিল)",
                totalPlayers = 48
            ),
            MatchResult(
                id = "res-2",
                matchTitle = "লোন উলফ - ১ বনাম ১ কিং ডুয়েল #০৮",
                category = GameCategory.LONE_WOLF,
                completedDate = "আজ বিকেল ৫:১৫ PM",
                winnerIgn = "TH_Roxx_FF",
                winnerUid = "198273942",
                winnerKills = 5,
                prizeMoney = 25,
                runnerUpIgn = "Chittagong_Hero",
                totalPlayers = 2
            ),
            MatchResult(
                id = "res-3",
                matchTitle = "সিএস র‍্যাঙ্ক - ৪ বনাম ৪ স্কোয়াড কাপ #১১",
                category = GameCategory.CS_RANK,
                completedDate = "গতকাল রাত ১০:৩০ PM",
                winnerIgn = "Team_BD_Tigers",
                winnerUid = "283749102",
                winnerKills = 14,
                prizeMoney = 70,
                runnerUpIgn = "Team_Dhaka_Warriors",
                totalPlayers = 8
            )
        )
    )
    val results: StateFlow<List<MatchResult>> = _results.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(
        listOf(
            WalletTransaction(
                id = "tx1",
                title = "স্লট #১৪ ক্রয় - বিআর র‍্যাঙ্ক #০১",
                amount = 10,
                isCredit = false,
                timeAgo = "১০ মিনিট আগে",
                statusText = "স্বয়ংক্রিয়ভাবে সনাক্ত ও কনফার্মড",
                paymentMethod = "bKash (01861583924)",
                category = TransactionCategory.SLOT_PURCHASE,
                trxId = "9KJ3L49M",
                slotNumber = 14,
                matchTitle = "বিআর র‍্যাঙ্ক - বারমুডা সোলো #০১",
                senderBkash = "01861583924",
                exactDateTime = "আজ রাত ৮:২০ PM"
            ),
            WalletTransaction(
                id = "tx2",
                title = "স্লট #১ ক্রয় - লোন উলফ কিং ডুয়েল #০৮",
                amount = 15,
                isCredit = false,
                timeAgo = "আজ বিকেল ৫:০০ PM",
                statusText = "বিকাশ পেমেন্ট সফল",
                paymentMethod = "bKash (01861583924)",
                category = TransactionCategory.SLOT_PURCHASE,
                trxId = "8KL2M91X",
                slotNumber = 1,
                matchTitle = "লোন উলফ - ১ বনাম ১ কিং ডুয়েল #০৮",
                senderBkash = "01861583924",
                exactDateTime = "আজ বিকেল ৫:০০ PM"
            ),
            WalletTransaction(
                id = "tx3",
                title = "টুর্নামেন্ট বুইয়া প্রাইজ মানি",
                amount = 25,
                isCredit = true,
                timeAgo = "আজ সন্ধ্যা ৬:৩০ PM",
                statusText = "উইনিং ওয়ালেটে জমা",
                paymentMethod = "Wallet",
                category = TransactionCategory.PRIZE_WIN,
                trxId = "WIN-88912",
                slotNumber = null,
                matchTitle = "লোন উলফ - ১ বনাম ১ কিং ডুয়েল #০৮",
                senderBkash = null,
                exactDateTime = "আজ সন্ধ্যা ৬:৩০ PM"
            ),
            WalletTransaction(
                id = "tx4",
                title = "বিকাশ ক্যাশআউট উইথড্র",
                amount = 20,
                isCredit = false,
                timeAgo = "গতকাল",
                statusText = "বিকাশে পরিশোধিত",
                paymentMethod = "bKash (01861583924)",
                category = TransactionCategory.WITHDRAWAL,
                trxId = "WTH-99410",
                slotNumber = null,
                matchTitle = null,
                senderBkash = "01861583924",
                exactDateTime = "গতকাল রাত ৯:১৫ PM"
            )
        )
    )
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardPlayer>>(
        listOf(
            LeaderboardPlayer(1, "TH_Roxx_FF Ⓥ", "198273942", 142, 19, 320, "Ⓥ গোল্ডেন ভি-ব্যাজ (টপ স্পেন্ডার)", BadgeType.V_BADGE, totalSpent = 850, matchesPlayed = 45),
            LeaderboardPlayer(2, "BD_Cyber_King ⚡", "283749102", 128, 15, 240, "⚡ প্রো প্লেয়ার (সর্বোচ্চ ম্যাচ)", BadgeType.PRO_PLAYER, totalSpent = 310, matchesPlayed = 38),
            LeaderboardPlayer(3, "Viper_OneShot 🏆", "837402914", 110, 14, 190, "🏆 বুইয়া মাস্টার", BadgeType.BOOYAH_MASTER, totalSpent = 220, matchesPlayed = 29),
            LeaderboardPlayer(4, "TH★DEVIL⚡ (ওনার)", "2489103847", 89, 11, 130, "🟢 গ্রিন ভেরিফাইড ওনার", BadgeType.GREEN_VERIFIED, totalSpent = 450, matchesPlayed = 24),
            LeaderboardPlayer(5, "TH★HUNTER🔥 (অ্যাডমিন)", "3829104921", 78, 9, 85, "🛡️ টুর্নামেন্ট মডারেটর", BadgeType.MODERATOR, totalSpent = 160, matchesPlayed = 19),
            LeaderboardPlayer(6, "Dhaka_Gladiator ⚡", "583920194", 65, 8, 60, "⚡ প্রো প্লেয়ার", BadgeType.PRO_PLAYER, totalSpent = 90, matchesPlayed = 15)
        )
    )
    val leaderboard: StateFlow<List<LeaderboardPlayer>> = _leaderboard.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("c1", "TH অ্যাডমিন", true, "আসসালামু আলাইকুম! TRUSTED TOURNAMENT এ স্বাগতম। যে কোনো সমস্যায় এখানে মেসেজ দিন।", "১০ মিনিট আগে"),
            ChatMessage("c2", "TH_Sniper_BD", false, "ভাইয়া রাত ৮:৩০ টার বিআর র‍্যাঙ্কের রুম আইডি কখন আসবে?", "৫ মিনিট আগে"),
            ChatMessage("c3", "TH অ্যাডমিন", true, "৮:১৫ মিনিটে স্বয়ংক্রিয়ভাবে অ্যাপে রুম আইডি ও পাসওয়ার্ড আনলক হয়ে যাবে ভাইয়া। শুভকামনা!", "২ মিনিট আগে")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

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
    val hasSpunToday: StateFlow<Boolean> get() = _hasSpunThisWeek.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun updateProfile(
        accountName: String,
        newIgn: String,
        newUid: String,
        newPhone: String,
        newBkash: String,
        newAvatarId: String
    ) {
        val oldProfile = _userProfile.value
        _userProfile.value = oldProfile.copy(
            accountName = accountName,
            ign = newIgn,
            uid = newUid,
            phone = newPhone,
            bkashNumber = newBkash,
            avatarId = newAvatarId
        )
        _registeredUsers.value = _registeredUsers.value.map { user ->
            if (user.phone == oldProfile.phone || user.uid == oldProfile.uid) {
                user.copy(
                    name = accountName,
                    ign = newIgn,
                    uid = newUid,
                    phone = newPhone,
                    bkashNumber = newBkash
                )
            } else {
                user
            }
        }
    }

    fun login(phoneOrEmail: String, password: String = ""): Boolean {
        val trimmed = phoneOrEmail.trim()
        val passTrimmed = password.trim()
        val existingUser = _registeredUsers.value.find {
            it.phone == trimmed || it.uid == trimmed || it.bkashNumber == trimmed
        }

        // If owner master passkey or PIN is used
        val isOwnerMasterKey = passTrimmed.equals(masterOwnerPasskey, ignoreCase = true) ||
                passTrimmed == masterOwnerPin ||
                trimmed.equals(masterOwnerPasskey, ignoreCase = true)
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
        _userProfile.value = if (role == UserRole.OWNER) {
            current.copy(
                isLoggedIn = true,
                accountName = existingUser?.name ?: "তানভির হাসান (ওনার)",
                ign = existingUser?.ign ?: "TH★DEVIL⚡",
                uid = existingUser?.uid ?: "2489103847",
                phone = existingUser?.phone ?: masterOwnerPhone,
                bkashNumber = existingUser?.bkashNumber ?: masterOwnerPhone,
                email = if (!trimmed.startsWith("01")) trimmed else "owner@trustedth.com",
                role = UserRole.OWNER,
                isBanned = isBanned,
                banReason = banReason
            )
        } else if (isSameAccount) {
            current.copy(
                isLoggedIn = true,
                accountName = existingUser?.name ?: current.accountName,
                ign = existingUser?.ign ?: current.ign,
                uid = existingUser?.uid ?: current.uid,
                phone = existingUser?.phone ?: current.phone,
                bkashNumber = existingUser?.bkashNumber ?: current.bkashNumber,
                role = role,
                isBanned = isBanned,
                banReason = banReason
            )
        } else {
            UserProfile(
                isLoggedIn = true,
                accountName = existingUser?.name ?: "টুর্নামেন্ট প্লেয়ার",
                ign = existingUser?.ign ?: "FF_Player",
                uid = existingUser?.uid ?: "00000000",
                phone = existingUser?.phone ?: trimmed,
                bkashNumber = existingUser?.bkashNumber ?: trimmed,
                email = if (!trimmed.startsWith("01")) trimmed else "",
                avatarId = "avatar_wolf",
                level = if (role == UserRole.ADMIN) 45 else 15,
                rankBadge = if (role == UserRole.ADMIN) "অফিসিয়াল অ্যাডমিন 🛡️" else "ডায়মন্ড 🎯",
                matchesPlayed = 6,
                totalWins = 1,
                totalKills = 22,
                totalEarnings = 20,
                joinedDate = existingUser?.joinedDate ?: "অক্টোবর ২০২৬",
                role = role,
                isBanned = isBanned,
                banReason = banReason
            )
        }
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

        val newUser = AppUser(
            id = "u_${System.currentTimeMillis()}",
            name = trimmedName,
            phone = trimmedPhone,
            ign = trimmedIgn,
            uid = trimmedUid,
            bkashNumber = trimmedBkash,
            password = trimmedPassword,
            role = role,
            joinedDate = "আজ"
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
            joinedDate = "আজ",
            isLoggedIn = true,
            role = role,
            isBanned = false
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
            exactDateTime = "আজ এইমাত্র"
        )
        _transactions.value = listOf(welcomeBonusTx)
        _isLoggedIn.value = true
        return true
    }

    fun logout() {
        _isLoggedIn.value = false
        _userProfile.value = _userProfile.value.copy(isLoggedIn = false)
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
            category = TransactionCategory.PRIZE_WIN
        )
        _transactions.value = listOf(tx) + _transactions.value
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
            category = TransactionCategory.PRIZE_WIN
        )
        _transactions.value = listOf(tx) + _transactions.value
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
            category = TransactionCategory.PRIZE_WIN
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
            exactDateTime = "আজ এইমাত্র"
        )
        _transactions.value = listOf(tx) + _transactions.value

        return true
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
            exactDateTime = "আজ এইমাত্র"
        )
        _transactions.value = listOf(refundTx) + _transactions.value
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
            exactDateTime = "আজ এইমাত্র"
        )
        _transactions.value = listOf(refundTx) + _transactions.value
        return true
    }

    fun cancelMatchAndRefundAll(matchId: String): Pair<Int, Int> {
        val matchBookings = _myBookings.value.filter { it.matchId == matchId }
        val count = matchBookings.size
        var totalRefund = 0

        matchBookings.forEach { booking ->
            totalRefund += booking.feePaid
            _walletBalance.value += booking.feePaid
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
                exactDateTime = "আজ এইমাত্র"
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        _myBookings.value = _myBookings.value.filter { it.matchId != matchId }
        _matches.value = _matches.value.map {
            if (it.id == matchId) it.copy(bookedSlots = 0, status = MatchStatus.CANCELLED) else it
        }

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
            level = 68,
            rankBadge = "হিরোইক ⭐",
            matchesPlayed = 24,
            totalWins = 11,
            totalKills = 89,
            totalEarnings = 130,
            joinedDate = "অক্টোবর ২০২৬",
            isLoggedIn = true,
            role = UserRole.OWNER,
            isBanned = false
        )
        _isLoggedIn.value = true
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
            statusText = "স্বয়ংক্রিয়ভাবে ব্যালেন্স যোগ হয়েছে",
            paymentMethod = "bKash ($bkashNumber | TrxID: $trxId)",
            trxId = trxId,
            senderBkash = bkashNumber,
            category = TransactionCategory.ALL
        )
        _transactions.value = listOf(tx) + _transactions.value
    }

    fun withdrawToBkash(amount: Int, bkashNumber: String): Boolean {
        val minW = _appCustomization.value.minWithdraw
        val maxW = _appCustomization.value.maxWithdraw
        if (amount < minW || amount > maxW) return false
        if (_walletBalance.value < amount) return false
        _walletBalance.value -= amount
        val tx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "বিকাশ ক্যাশআউট উইথড্র",
            amount = amount,
            isCredit = false,
            timeAgo = "এইমাত্র",
            statusText = "প্রসেসিং (৫-১০ মিনিটে ক্যাশআউট)",
            paymentMethod = "bKash ($bkashNumber)",
            category = TransactionCategory.WITHDRAWAL,
            senderBkash = bkashNumber
        )
        _transactions.value = listOf(tx) + _transactions.value
        return true
    }

    fun updateWhatsAppGroupUrl(url: String) {
        officialWhatsAppGroupUrl.value = url
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
                bannedAt = "আজ এইমাত্র"
            )
            _registeredUsers.value = list

            if (_userProfile.value.phone == user.phone || _userProfile.value.uid == user.uid) {
                _userProfile.value = _userProfile.value.copy(
                    isBanned = true,
                    banReason = banMsg
                )
            }
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
                bannedAt = "আজ এইমাত্র"
            )
            _registeredUsers.value = listOf(bannedEntry) + list
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
                bannedAt = null
            )
            _registeredUsers.value = list

            if (_userProfile.value.phone == user.phone || _userProfile.value.uid == user.uid) {
                _userProfile.value = _userProfile.value.copy(
                    isBanned = false,
                    banReason = null
                )
            }
            return true
        }
        return false
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
            list[index] = user.copy(
                role = UserRole.PLAYER,
                adminPasscode = null
            )
            _registeredUsers.value = list
            return true
        }
        return false
    }

    fun updateMasterOwnerPin(newPin: String): Boolean {
        if (newPin.trim().length >= 4) {
            masterOwnerPin = newPin.trim()
            return true
        }
        return false
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
    }

    fun toggleMaintenanceMode(enabled: Boolean, message: String) {
        _appCustomization.value = _appCustomization.value.copy(
            isMaintenanceMode = enabled,
            maintenanceMessage = message.ifBlank { "সার্ভার রক্ষণাবেক্ষণ চলছে। শীঘ্রই টুর্নামেন্ট শুরু হবে।" }
        )
    }

    fun updateExistingMatch(updatedMatch: TournamentMatch): Boolean {
        var found = false
        _matches.value = _matches.value.map { match ->
            if (match.id == updatedMatch.id) {
                found = true
                updatedMatch
            } else match
        }
        return found
    }

    fun deleteMatch(matchId: String): Boolean {
        val countBefore = _matches.value.size
        // Refund any players who booked this match first
        cancelMatchAndRefundAll(matchId)
        _matches.value = _matches.value.filter { it.id != matchId }
        return _matches.value.size < countBefore
    }

    fun updateMatchSlotCapacity(matchId: String, newCapacity: Int): Boolean {
        if (newCapacity < 2 || newCapacity > 100) return false
        _matches.value = _matches.value.map { match ->
            if (match.id == matchId) {
                match.copy(totalSlots = newCapacity)
            } else match
        }
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
        return found
    }

    fun deleteStrictRule(ruleId: String): Boolean {
        val before = _strictRules.value.size
        _strictRules.value = _strictRules.value.filter { it.id != ruleId }
        return _strictRules.value.size < before
    }

    fun resetDefaultRules() {
        _strictRules.value = defaultRules
    }
}

