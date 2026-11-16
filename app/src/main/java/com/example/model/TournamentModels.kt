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

enum class WalletPaymentMethod(val displayName: String, val tag: String) {
    BKASH("বিকাশ (bKash)", "bKash"),
    NAGAD("নগদ (Nagad)", "Nagad")
}

enum class RequestStatus(val banglaLabel: String) {
    PENDING("অপেক্ষমাণ"),
    APPROVED("অনুমোদিত"),
    REJECTED("বাতিল")
}

data class DepositRequest(
    val id: String,
    val userPhone: String,
    val userName: String,
    val userIgn: String,
    val amount: Int,
    val paymentMethod: WalletPaymentMethod = WalletPaymentMethod.BKASH,
    val senderNumber: String,
    val trxId: String,
    val status: RequestStatus = RequestStatus.PENDING,
    val createdAt: String = "এইমাত্র",
    val adminNote: String = ""
)

data class WithdrawRequest(
    val id: String,
    val userPhone: String,
    val userName: String,
    val userIgn: String,
    val amount: Int,
    val paymentMethod: WalletPaymentMethod = WalletPaymentMethod.BKASH,
    val targetNumber: String,
    val status: RequestStatus = RequestStatus.PENDING,
    val createdAt: String = "এইমাত্র",
    val adminNote: String = ""
)

enum class TransactionCategory(val banglaTitle: String) {
    ALL("সকল লেনদেন"),
    DEPOSIT("টাকা ডিপোজিট"),
    SLOT_PURCHASE("স্লট ক্রয়"),
    PRIZE_WIN("উইনিং প্রাইজ"),
    WITHDRAWAL("ক্যাশআউট উইথড্র"),
    REFUND("স্লট রিফান্ড"),
    ADMIN_ADJUSTMENT("ওনার ব্যালেন্স")
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
    val exactDateTime: String = "আজ এইমাত্র",
    val userPhone: String = "",
    val requestId: String? = null
)

enum class BadgeType(
    val title: String,
    val iconEmoji: String,
    val description: String,
    val criteria: String,
    val hexColor: Long,
    val tagText: String,
    val tierLabel: String = "ELITE"
) {
    V_BADGE(
        title = "গোল্ডেন [V] ব্যাজ",
        iconEmoji = "Ⓥ",
        description = "হাই-ইনভেস্টর ও অফিসিয়াল গোল্ডেন [V] পার্টনার ভিআইপি ব্যাজ",
        criteria = "১০০৳+ প্রাইজ আয় অথবা ১০+ ম্যাচ খেললে",
        hexColor = 0xFFFFD700,
        tagText = "[V] VIP",
        tierLabel = "PARTNER"
    ),
    GREEN_VERIFIED(
        title = "গ্রিন ভেরিফাইড ওনার",
        iconEmoji = "✔",
        description = "TRUSTED TOURNAMENT অ্যাপের একমাত্র অফিসিয়াল মালিকানা ব্যাজ",
        criteria = "অ্যাপের মূল ওনার (Owner) এর জন্য সংরক্ষিত",
        hexColor = 0xFF22C55E,
        tagText = "OWNER",
        tierLabel = "SUPREME"
    ),
    MODERATOR(
        title = "অফিসিয়াল অ্যাডমিন ব্যাজ",
        iconEmoji = "🛡️",
        description = "টুর্নামেন্ট ম্যাচ ও রুম পরিচালনাকারী ভেরিফাইড অ্যাডমিন",
        criteria = "ওনার কর্তৃক নিযুক্ত টুর্নামেন্ট অ্যাডমিন",
        hexColor = 0xFFF59E0B,
        tagText = "ADMIN",
        tierLabel = "STAFF"
    ),
    PRO_PLAYER(
        title = "প্রো প্লেয়ার [PRO] ব্যাজ",
        iconEmoji = "👑",
        description = "সবচেয়ে বেশি ম্যাচ খেলা ও অভিজ্ঞ প্রো টুর্নামেন্ট যোদ্ধা",
        criteria = "৫+ ম্যাচ খেললে প্রো ব্যাজ",
        hexColor = 0xFFEAB308,
        tagText = "PRO",
        tierLabel = "MASTER"
    ),
    PRIME_SPENDER(
        title = "প্রাইম বিগ স্পেন্ডার [8]",
        iconEmoji = "💠",
        description = "অ্যাপে সবচেয়ে বেশি টাকা খরচ ও এন্ট্রি করা রয়্যাল স্পেন্ডার ব্যাজ",
        criteria = "যে বেশি টাকা খরচ করবে (৳১৫০+ স্লট/এন্ট্রি খরচ)",
        hexColor = 0xFFFF9800,
        tagText = "PRIME 8",
        tierLabel = "WHALE"
    ),
    BOOYAH_MASTER(
        title = "বুইয়া মাস্টার",
        iconEmoji = "🏆",
        description = "চ্যাম্পিয়ন ও সর্বোচ্চ উইনিং বুইয়া অর্জনকারী",
        criteria = "৩টির বেশি বুইয়া জিতলে",
        hexColor = 0xFFFF9100,
        tagText = "BOOYAH",
        tierLabel = "CHAMPION"
    ),
    HEADSHOT_KING(
        title = "হেডশট কিং",
        iconEmoji = "🎯",
        description = "ওয়ান-ট্যাপ ও নিখুঁত হেডশট স্কিল সম্পন্ন এলিট শুটার",
        criteria = "৪৫%+ হেডশট রেট বা ২৫+ কিল",
        hexColor = 0xFF00E5FF,
        tagText = "HEADSHOT",
        tierLabel = "SNIPER"
    ),
    KILL_MACHINE(
        title = "কিল মেশিন",
        iconEmoji = "💀",
        description = "আক্রমণাত্মক গেমপ্লে ও সর্বোচ্চ এনিমি এলিমিনেটর ব্যাজ",
        criteria = "টুর্নামেন্টে ৫০+ এনিমি কিল করলে",
        hexColor = 0xFFEF4444,
        tagText = "SLAYER",
        tierLabel = "LETHAL"
    ),
    CHAMPION_LEGEND(
        title = "লিজেন্ড চ্যাম্পিয়ন",
        iconEmoji = "💎",
        description = "টুর্নামেন্টের শীর্ষ পর্যায়ের অপরাজেয় গ্র্যান্ডমাস্টার ব্যাজ",
        criteria = "১০+ বুইয়া অথবা ৫০০৳+ প্রাইজ জিতলে",
        hexColor = 0xFFA855F7,
        tagText = "LEGEND",
        tierLabel = "MYTHIC"
    );

    fun isRequirementMet(profile: UserProfile): Boolean = when (this) {
        V_BADGE -> profile.totalEarnings >= 100 || profile.matchesPlayed >= 10
        GREEN_VERIFIED -> profile.role == UserRole.OWNER
        MODERATOR -> profile.role == UserRole.ADMIN || profile.role == UserRole.OWNER
        PRO_PLAYER -> profile.matchesPlayed >= 5
        PRIME_SPENDER -> profile.totalSpent >= 150
        BOOYAH_MASTER -> profile.totalWins >= 3
        HEADSHOT_KING -> profile.headshotRate >= 45 || profile.totalKills >= 25
        KILL_MACHINE -> profile.totalKills >= 50
        CHAMPION_LEGEND -> profile.totalWins >= 10 || profile.totalEarnings >= 500
    }

    fun requirementProgressText(profile: UserProfile): String = when (this) {
        V_BADGE -> "ম্যাচ: ${profile.matchesPlayed}/১০ • আয়: ৳${profile.totalEarnings}/১০০"
        GREEN_VERIFIED -> if (profile.role == UserRole.OWNER) "ওনার রোল সক্রিয়" else "কেবল মূল ওনার"
        MODERATOR -> if (profile.role == UserRole.ADMIN || profile.role == UserRole.OWNER) "স্টাফ রোল সক্রিয়" else "অ্যাডমিন পদের জন্য"
        PRO_PLAYER -> "ম্যাচ খেলা: ${profile.matchesPlayed}/৫"
        PRIME_SPENDER -> "মোট খরচ: ৳${profile.totalSpent}/১৫০"
        BOOYAH_MASTER -> "মোট বুইয়া: ${profile.totalWins}/৩"
        HEADSHOT_KING -> "মোট কিল: ${profile.totalKills}/২৫ • HS: ${profile.headshotRate}%/৪৫%"
        KILL_MACHINE -> "মোট কিল: ${profile.totalKills}/৫০"
        CHAMPION_LEGEND -> "বুইয়া: ${profile.totalWins}/১০ • আয়: ৳${profile.totalEarnings}/৫০০"
    }
}

enum class AppealStatus(val banglaLabel: String) {
    PENDING("অপেক্ষমাণ"),
    APPROVED("অনুমোদিত"),
    REJECTED("বাতিল (ওয়ার্ন)")
}

data class AppealChatMessage(
    val id: String,
    val senderName: String,
    val senderRoleLabel: String,
    val isFromStaff: Boolean,
    val text: String,
    val proofImageUri: String? = null,
    val timeAgo: String = "এইমাত্র"
)

data class BadgeAppeal(
    val id: String,
    val userId: String,
    val userName: String,
    val userIgn: String,
    val userUid: String,
    val userPhone: String,
    val requestedBadge: BadgeType,
    val reasonNote: String,
    val status: AppealStatus = AppealStatus.PENDING,
    val grantedBadge: BadgeType? = null,
    val adminNote: String = "",
    val reviewedBy: String = "",
    val createdAt: String = "এইমাত্র",
    val matchesPlayedSnapshot: Int = 0,
    val totalSpentSnapshot: Int = 0,
    val totalWinsSnapshot: Int = 0,
    val totalKillsSnapshot: Int = 0,
    val totalEarningsSnapshot: Int = 0,
    val chatMessages: List<AppealChatMessage> = emptyList()
)

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
    OWNER("অ্যাপ ওনার (মালিক)", "👑");

    fun canUseAnimatedGifProfileMedia(): Boolean = this == OWNER || this == ADMIN
}

fun canCustomizeProfileBanner(role: UserRole, badges: Collection<BadgeType>): Boolean =
    role.canUseAnimatedGifProfileMedia() || BadgeType.PRIME_SPENDER in badges

enum class ProfileBannerColor(
    val id: String,
    val title: String,
    val startColorHex: Long,
    val midColorHex: Long,
    val endColorHex: Long,
    val accentHex: Long
) {
    DEFAULT_CRIMSON("crimson_flame", "ক্রিমসন ফ্লেম", 0xFF240B29, 0xFF6B0F47, 0xFF0F172A, 0xFFE2136E),
    CYBER_CYAN("cyber_cyan", "সাইবার সায়ান", 0xFF042F2E, 0xFF0E7490, 0xFF0F172A, 0xFF00E5FF),
    ROYAL_GOLD("royal_gold", "রয়্যাল গোল্ড", 0xFF3F2305, 0xFF9A6700, 0xFF1F2937, 0xFFFFB300),
    EMERALD_VIPER("emerald_viper", "এমেরাল্ড গ্রিন", 0xFF022C22, 0xFF047857, 0xFF0F172A, 0xFF10B981),
    ELECTRIC_PURPLE("electric_purple", "ইলেকট্রিক পার্পল", 0xFF2E1065, 0xFF7E22CE, 0xFF1E1B4B, 0xFFA855F7),
    COBALT_STRIKE("cobalt_strike", "কোবাল্ট ব্লু", 0xFF0A192F, 0xFF1D4ED8, 0xFF111827, 0xFF3B82F6),
    INFERNO_ORANGE("inferno_orange", "ইনফার্নো অরেঞ্জ", 0xFF431407, 0xFFC2410C, 0xFF18181B, 0xFFFF5722),
    OBSIDIAN_STEALTH("obsidian_stealth", "অবসিডিয়ান ডার্ক", 0xFF09090B, 0xFF27272A, 0xFF0F172A, 0xFF94A3B8);

    companion object {
        fun fromId(id: String?): ProfileBannerColor =
            entries.firstOrNull { it.id == id } ?: DEFAULT_CRIMSON
    }
}

fun isAnimatedGifPathOrUri(uri: String?): Boolean {
    if (uri.isNullOrBlank()) return false
    val lower = uri.lowercase()
    return lower.endsWith(".gif") ||
        lower.contains(".gif?") ||
        lower.contains(".gif#") ||
        lower.contains("image/gif") ||
        lower.contains("format=gif") ||
        lower.contains("gif_")
}

data class AppUser(
    val id: String,
    val name: String,
    val phone: String,
    val ign: String,
    val uid: String,
    val bkashNumber: String,
    val password: String = "123456",
    val role: UserRole = UserRole.PLAYER,
    val isBanned: Boolean = false,
    val banReason: String? = null,
    val bannedAt: String? = null,
    val adminPasscode: String? = null,
    val appointedDate: String? = null,
    val joinedDate: String = "অক্টোবর ২০২৬",
    val walletBalance: Int = 0,
    val matchesPlayed: Int = 0,
    val totalWins: Int = 0,
    val totalKills: Int = 0,
    val totalEarnings: Int = 0,
    val totalSpent: Int = 0,
    val customBadges: Set<BadgeType> = emptySet(),
    val revokedBadges: Set<BadgeType> = emptySet(),
    val avatarId: String = "th_logo",
    val customPfpUri: String? = null,
    val customBannerUri: String? = null,
    val bannerColorId: String = ProfileBannerColor.DEFAULT_CRIMSON.id,
    val battleTagline: String = "নতুন চ্যালেঞ্জার! 🎯",
    val favoriteWeapon: String = "MP40",
    val activeCharacter: String = "Alok",
    val guildName: String = "TRUSTED TH",
    val playerRole: String = "সোলো প্লেয়ার",
    val level: Int = 1,
    val kudosCount: Int = 0,
    val warningsCount: Int = 0,
    val banExpiresAtMillis: Long? = null
) {
    val effectiveBadges: List<BadgeType> get() {
        val auto = mutableListOf<BadgeType>()
        if (role == UserRole.OWNER) auto.add(BadgeType.GREEN_VERIFIED)
        if (role == UserRole.ADMIN) auto.add(BadgeType.MODERATOR)
        if (totalEarnings >= 100 || matchesPlayed >= 10) auto.add(BadgeType.V_BADGE)
        if (matchesPlayed >= 5) auto.add(BadgeType.PRO_PLAYER)
        if (totalSpent >= 150) auto.add(BadgeType.PRIME_SPENDER)
        if (totalWins >= 3) auto.add(BadgeType.BOOYAH_MASTER)
        if (totalKills >= 25) auto.add(BadgeType.HEADSHOT_KING)
        if (totalKills >= 50) auto.add(BadgeType.KILL_MACHINE)
        if (totalWins >= 10 || totalEarnings >= 500) auto.add(BadgeType.CHAMPION_LEGEND)
        return ((auto + customBadges) - revokedBadges).distinct()
    }

    val primaryBadge: BadgeType get() {
        val defaultBadge = when (role) {
            UserRole.OWNER -> BadgeType.GREEN_VERIFIED
            UserRole.ADMIN -> BadgeType.MODERATOR
            UserRole.PLAYER -> BadgeType.PRO_PLAYER
        }
        val active = effectiveBadges
        return if (defaultBadge in active) defaultBadge else active.firstOrNull() ?: defaultBadge
    }
}

data class DailyStreakInfo(
    val currentStreakDay: Int = 1, // 1 to 7
    val isTodayClaimed: Boolean = false,
    val streakHistoryDays: Set<Int> = emptySet(),
    val rewards: List<Int> = listOf(1, 1, 1, 1, 2, 2, 3)
)

data class UserProfile(
    val accountName: String = "",
    val phone: String = "",
    val email: String = "",
    val ign: String = "",
    val uid: String = "",
    val bkashNumber: String = "",
    val avatarId: String = "th_logo",
    val customPfpUri: String? = null,
    val customBannerUri: String? = null,
    val bannerColorId: String = ProfileBannerColor.DEFAULT_CRIMSON.id,
    val level: Int = 1,
    val rankBadge: String = "নতুন যোদ্ধা 🛡️",
    val matchesPlayed: Int = 0,
    val totalWins: Int = 0,
    val totalKills: Int = 0,
    val totalEarnings: Int = 0,
    val totalSpent: Int = 0,
    val joinedDate: String = "আজ",
    val isLoggedIn: Boolean = false,
    val role: UserRole = UserRole.PLAYER,
    val isBanned: Boolean = false,
    val banReason: String? = null,
    val favoriteWeapon: String = "MP40",
    val activeCharacter: String = "Alok",
    val headshotRate: Int = 0,
    val kdRatio: Double = 0.0,
    val guildName: String = "নেই",
    val playerRole: String = "সোলো প্লেয়ার",
    val battleTagline: String = "নতুন চ্যালেঞ্জার! 🎯",
    val totalSpinsDone: Int = 0,
    val totalSpinEarnings: Int = 0,
    val highestSpinWon: Int = 0,
    val customBadges: Set<BadgeType> = emptySet(),
    val revokedBadges: Set<BadgeType> = emptySet(),
    val warningsCount: Int = 0,
    val banExpiresAtMillis: Long? = null
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
        // "je beshi taka khoroch korbe tar badge eita"
        if (totalSpent >= 150) {
            list.add(BadgeType.PRIME_SPENDER)
        }
        if (totalWins >= 3) {
            list.add(BadgeType.BOOYAH_MASTER)
        }
        if (headshotRate >= 45 || totalKills >= 25) {
            list.add(BadgeType.HEADSHOT_KING)
        }
        if (totalKills >= 50) {
            list.add(BadgeType.KILL_MACHINE)
        }
        if (totalWins >= 10 || totalEarnings >= 500) {
            list.add(BadgeType.CHAMPION_LEGEND)
        }
        return ((list + customBadges) - revokedBadges).distinct()
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

data class GlobalChatMessage(
    val id: String,
    val senderUserId: String,
    val senderName: String,
    val senderIgn: String,
    val senderUid: String,
    val senderPhone: String = "",
    val senderRole: UserRole = UserRole.PLAYER,
    val senderAvatarId: String = "th_logo",
    val senderCustomPfpUri: String? = null,
    val senderPrimaryBadge: BadgeType = BadgeType.PRO_PLAYER,
    val senderBadges: List<BadgeType> = emptyList(),
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val timeLabel: String = "এইমাত্র",
    val isPinnedAnnouncement: Boolean = false,
    val replyToId: String? = null,
    val replyToIgn: String? = null,
    val replyToText: String? = null,
    val reactions: Map<String, Int> = emptyMap()
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

data class TournamentRuleItem(
    val id: String,
    val title: String,
    val description: String,
    val penalty: String,
    val iconEmoji: String = "⚠️",
    val isStrict: Boolean = true
)

enum class AppLogoOption(val id: String, val title: String, val iconEmoji: String) {
    TH_LOGO("th_logo", "TH ট্রাস্টেড শিল্ড (অফিসিয়াল)", "🛡️"),
    TH_ESPORTS("th_esports", "TH গোল্ডেন ফ্লেম এসপোর্টস", "⚡"),
    CYBER_WOLF("avatar_wolf", "সাইবার উলফ ফাইটার", "🐺"),
    PRO_GAMER("avatar_cyber", "প্রো নিয়ন গেমার", "🎮")
}

data class AppCustomization(
    val appName: String = "TRUSTED TOURNAMENT BY TH",
    val appTagline: String = "১০০% বিশ্বস্ত • মোবাইল অনলি • দ্রুত ক্যাশআউট",
    val selectedLogo: AppLogoOption = AppLogoOption.TH_LOGO,
    val noticeTickerText: String = "ম্যাচ শুরুর ১৫ মিনিট আগে অ্যাপে কাস্টম রুম আইডি ও পাসওয়ার্ড দেওয়া হবে!",
    val activeAnnouncement: String = "প্রতিদিন বিকাল ৫:০০ থেকে রাত ৮:০০ পর্যন্ত সুপারফাস্ট এক্সপ্রেস ম্যাচ!",
    val officialBkashNumber: String = "01861583924",
    val officialNagadNumber: String = "01861583924",
    val officialWhatsAppUrl: String = "https://chat.whatsapp.com/G8TrustedTHTournament",
    val minWithdraw: Int = 50,
    val maxWithdraw: Int = 200,
    val isMaintenanceMode: Boolean = false,
    val maintenanceMessage: String = "সার্ভার রক্ষণাবেক্ষণ চলছে। শীঘ্রই টুর্নামেন্ট রুম উন্মুক্ত করা হবে।"
)

