package com.example.model

import androidx.compose.ui.graphics.Color

enum class GameCategory(
    val banglaTitle: String,
    val iconEmoji: String,
    val accentColor: Color
) {
    ALL("সব ম্যাচ", "🔥", Color(0xFFFF5722)),
    LONE_WOLF("লোন উলফ", "🐺", Color(0xFFE2136E)),
    BR_RANK("বিআর র‍্যাঙ্ক", "🏆", Color(0xFFFFB300)),
    CS_RANK("সিএস র‍্যাঙ্ক", "⚔️", Color(0xFF00E5FF))
}

enum class MatchStatus(val banglaLabel: String, val color: Color) {
    UPCOMING("আসন্ন", Color(0xFF10B981)),
    ROOM_OPEN("রুম ওপেন", Color(0xFFFF9100)),
    ONGOING("খেলা চলছে", Color(0xFF3B82F6)),
    FINISHED("ফলাফল প্রকাশিত", Color(0xFF94A3B8)),
    CANCELLED("ম্যাচ বাতিল (রিফান্ডকৃত)", Color(0xFFEF4444))
}

enum class MatchType(val banglaLabel: String) {
    SOLO("সোলো (১v১ / ১ জন)"),
    DUO("ডুও (২ জন স্কোয়াড)"),
    SQUAD("স্কোয়াড (৪ জন টিম)")
}

data class TournamentMatch(
    val id: String,
    val title: String,
    val category: GameCategory,
    val matchType: MatchType,
    val mapName: String, // বারমুডা, কালাহারি, পারগেটরী, আয়রন কেজ
    val matchDateTime: String,
    val startsInMinutes: Int, // কাউন্টডাউন টাইমারের জন্য
    val entryFee: Int, // টাকা ৳
    val totalPrize: Int, // মোট পুরস্কার ৳
    val perKillPrize: Int, // প্রতি কিল ৳
    val booyahPrize: Int, // বুইয়া পুরস্কার ৳
    val totalSlots: Int,
    val bookedSlots: Int,
    val status: MatchStatus,
    val roomId: String? = null,
    val roomPassword: String? = null,
    val version: String = "মোবাইল অনলি (No Emulator)"
) {
    val isFull: Boolean get() = bookedSlots >= totalSlots
    val availableSlots: Int get() = (totalSlots - bookedSlots).coerceAtLeast(0)
}

data class SlotBooking(
    val bookingId: String,
    val matchId: String,
    val matchTitle: String,
    val category: GameCategory,
    val matchDateTime: String,
    val slotNumber: Int,
    val playerIgn: String,
    val playerUid: String,
    val bkashSenderNumber: String,
    val trxId: String,
    val feePaid: Int,
    val isVerified: Boolean = true,
    val roomId: String? = null,
    val roomPassword: String? = null
)

enum class TransactionCategory(val banglaTitle: String) {
    ALL("সকল লেনদেন"),
    SLOT_PURCHASE("স্লট ক্রয়"),
    PRIZE_WIN("উইনিং প্রাইজ"),
    WITHDRAWAL("বিকাশ ক্যাশআউট"),
    REFUND("স্লট রিফান্ড")
}

data class WalletTransaction(
    val id: String,
    val title: String,
    val amount: Int,
    val isCredit: Boolean, // true = টাকা জমা, false = খরচ/উইথড্র
    val timeAgo: String,
    val statusText: String,
    val paymentMethod: String = "bKash",
    val category: TransactionCategory = TransactionCategory.ALL,
    val trxId: String? = null,
    val slotNumber: Int? = null,
    val matchTitle: String? = null,
    val senderBkash: String? = null,
    val exactDateTime: String = "আজ রাত ৮:১৫ PM"
)

enum class BadgeType(
    val title: String,
    val iconEmoji: String,
    val description: String,
    val criteria: String,
    val hexColor: Long,
    val tagText: String
) {
    V_BADGE(
        title = "গোল্ডেন [V] ব্যাজ",
        iconEmoji = "Ⓥ",
        description = "হাই-ইনভেস্টর ও টপ বিকাশ ডিপোজিট ভিআইপি প্লেয়ার ব্যাজ",
        criteria = "যে বেশি টাকা লাগাবে বা ডিপোজিট করবে (VIP)",
        hexColor = 0xFFFFD700,
        tagText = "[V] VIP"
    ),
    GREEN_VERIFIED(
        title = "গ্রিন ভেরিফাইড ওনার",
        iconEmoji = "🟢",
        description = "TRUSTED TOURNAMENT অ্যাপের অফিসিয়াল মালিকানা যাচাইকৃত ব্যাজ",
        criteria = "অ্যাপের মূল ওনার (Owner) এর জন্য সংরক্ষিত",
        hexColor = 0xFF10B981,
        tagText = "✔ OWNER"
    ),
    MODERATOR(
        title = "মডারেটর ব্যাজ",
        iconEmoji = "🛡️",
        description = "টুর্নামেন্ট ম্যাচ পরিচালনাকারী অফিশিয়াল অ্যাডমিন/মডারেটর",
        criteria = "ওনার কর্তৃক নিযুক্ত টুর্নামেন্ট অ্যাডমিন",
        hexColor = 0xFFFF5722,
        tagText = "MODERATOR"
    ),
    PRO_PLAYER(
        title = "প্রো প্লেয়ার ব্যাজ",
        iconEmoji = "⚡",
        description = "সবচেয়ে বেশি ম্যাচ খেলা ও অভিজ্ঞ প্রো টুর্নামেন্ট প্লেয়ার",
        criteria = "যে বেশি ম্যাচ খেলবে (৫+ ম্যাচ খেলেছে)",
        hexColor = 0xFF00E5FF,
        tagText = "PRO"
    ),
    BOOYAH_MASTER(
        title = "বুইয়া মাস্টার",
        iconEmoji = "🏆",
        description = "চ্যাম্পিয়ন ও সর্বোচ্চ উইনিং বুইয়া অর্জনকারী",
        criteria = "৩টির বেশি বুইয়া জিতলে",
        hexColor = 0xFFFF9100,
        tagText = "CHAMPION"
    )
}

data class LeaderboardPlayer(
    val rank: Int,
    val ign: String,
    val uid: String,
    val totalKills: Int,
    val booyahs: Int,
    val totalEarnings: Int,
    val badge: String,
    val primaryBadge: BadgeType = BadgeType.PRO_PLAYER,
    val totalSpent: Int = 120,
    val matchesPlayed: Int = 22
)

data class MatchResult(
    val id: String,
    val matchTitle: String,
    val category: GameCategory,
    val completedDate: String,
    val winnerIgn: String,
    val winnerUid: String,
    val winnerKills: Int,
    val prizeMoney: Int,
    val runnerUpIgn: String,
    val totalPlayers: Int
)

enum class UserRole(val banglaTitle: String, val badge: String) {
    PLAYER("সাধারণ প্লেয়ার", "🎮"),
    ADMIN("টুর্নামেন্ট অ্যাডমিন", "🛡️"),
    OWNER("অ্যাপ ওনার (মালিক)", "👑")
}

data class AppUser(
    val id: String,
    val name: String,
    val phone: String,
    val ign: String,
    val uid: String,
    val bkashNumber: String,
    val role: UserRole = UserRole.PLAYER,
    val isBanned: Boolean = false,
    val banReason: String? = null,
    val bannedAt: String? = null,
    val adminPasscode: String? = null,
    val appointedDate: String? = null,
    val joinedDate: String = "অক্টোবর ২০২৬"
) {
    val primaryBadge: BadgeType get() = when (role) {
        UserRole.OWNER -> BadgeType.GREEN_VERIFIED
        UserRole.ADMIN -> BadgeType.MODERATOR
        UserRole.PLAYER -> BadgeType.PRO_PLAYER
    }
}

data class DailyStreakInfo(
    val currentStreakDay: Int = 3, // 1 to 7
    val isTodayClaimed: Boolean = false,
    val streakHistoryDays: Set<Int> = setOf(1, 2),
    val rewards: List<Int> = listOf(1, 1, 1, 1, 2, 2, 3)
)

data class UserProfile(
    val accountName: String = "তানভির হাসান (ওনার)",
    val phone: String = "01861583924",
    val email: String = "owner@trustedth.com",
    val ign: String = "TH★DEVIL⚡",
    val uid: String = "2489103847",
    val bkashNumber: String = "01861583924",
    val avatarId: String = "th_logo",
    val level: Int = 68,
    val rankBadge: String = "হিরোইক ⭐",
    val matchesPlayed: Int = 24,
    val totalWins: Int = 11,
    val totalKills: Int = 89,
    val totalEarnings: Int = 130,
    val joinedDate: String = "অক্টোবর ২০২৬",
    val isLoggedIn: Boolean = true,
    val role: UserRole = UserRole.OWNER,
    val isBanned: Boolean = false,
    val banReason: String? = null,
    val favoriteWeapon: String = "MP40 কোবরা (Cobra)",
    val activeCharacter: String = "Alok / Tatsuya",
    val headshotRate: Int = 64, // 64%
    val kdRatio: Double = 3.85,
    val guildName: String = "TH★ESPORTS BD",
    val playerRole: String = "রাশার (Rusher)",
    val battleTagline: String = "ওয়ান শট, ওয়ান নক! 🎯",
    val totalSpinsDone: Int = 2,
    val totalSpinEarnings: Int = 7,
    val highestSpinWon: Int = 4
) {
    val earnedBadges: List<BadgeType> get() {
        val list = mutableListOf<BadgeType>()
        if (role == UserRole.OWNER) {
            list.add(BadgeType.GREEN_VERIFIED)
        }
        if (role == UserRole.ADMIN) {
            list.add(BadgeType.MODERATOR)
        }
        // "je beshi taka lagabe app a shey pabe v badge"
        if (totalEarnings >= 100 || matchesPlayed >= 10) {
            list.add(BadgeType.V_BADGE)
        }
        // "je beshi khelbe match pro badge"
        if (matchesPlayed >= 5) {
            list.add(BadgeType.PRO_PLAYER)
        }
        if (totalWins >= 3) {
            list.add(BadgeType.BOOYAH_MASTER)
        }
        return list
    }
}

data class ChatMessage(
    val id: String,
    val senderName: String,
    val isFromAdmin: Boolean,
    val text: String,
    val timeAgo: String,
    val isAiAssistant: Boolean = false,
    val actionType: String? = null,
    val actionLabel: String? = null
)

data class SpinHistoryRecord(
    val id: String,
    val amount: Int,
    val date: String,
    val isJackpot: Boolean = false
)

data class DailyGamerMission(
    val id: String,
    val title: String,
    val description: String,
    val rewardAmount: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean,
    val iconEmoji: String,
    val actionType: String
)

