package com.example.data

import android.content.Context
import com.example.model.AppCustomization
import com.example.model.AppLogoOption
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
import com.example.model.TournamentRuleItem
import com.example.model.TransactionCategory
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.model.WalletPaymentMethod
import com.example.model.WalletTransaction
import com.example.model.WithdrawRequest
import org.json.JSONArray
import org.json.JSONObject

data class PersistedAppState(
    val isLoggedIn: Boolean,
    val userProfile: UserProfile,
    val walletBalance: Int,
    val registeredUsers: List<AppUser>,
    val matches: List<TournamentMatch>,
    val myBookings: List<SlotBooking>,
    val transactions: List<WalletTransaction>,
    val depositRequests: List<DepositRequest>,
    val withdrawRequests: List<WithdrawRequest>,
    val results: List<MatchResult>,
    val leaderboard: List<LeaderboardPlayer>,
    val dailyStreakInfo: DailyStreakInfo,
    val spinHistory: List<SpinHistoryRecord>,
    val hasSpunThisWeek: Boolean,
    val dailyMissions: List<DailyGamerMission>,
    val appCustomization: AppCustomization,
    val strictRules: List<TournamentRuleItem>,
    val officialWhatsAppUrl: String,
    val chatMessages: List<ChatMessage>,
    val masterOwnerPin: String,
    val badgeAppeals: List<BadgeAppeal> = emptyList(),
    val globalChatMessages: List<GlobalChatMessage> = emptyList()
)

class AppPersistenceStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "th_tournament_persistent_store_v1"
        private const val KEY_STATE_JSON = "app_state_json"
    }

    fun saveState(state: PersistedAppState) {
        runCatching {
            val root = JSONObject().apply {
                put("isLoggedIn", state.isLoggedIn)
                put("userProfile", encodeUserProfile(state.userProfile))
                put("walletBalance", state.walletBalance)
                put("registeredUsers", JSONArray().apply { state.registeredUsers.forEach { put(encodeAppUser(it)) } })
                put("matches", JSONArray().apply { state.matches.forEach { put(encodeMatch(it)) } })
                put("myBookings", JSONArray().apply { state.myBookings.forEach { put(encodeBooking(it)) } })
                put("transactions", JSONArray().apply { state.transactions.forEach { put(encodeTransaction(it)) } })
                put("depositRequests", JSONArray().apply { state.depositRequests.forEach { put(encodeDepositRequest(it)) } })
                put("withdrawRequests", JSONArray().apply { state.withdrawRequests.forEach { put(encodeWithdrawRequest(it)) } })
                put("results", JSONArray().apply { state.results.forEach { put(encodeResult(it)) } })
                put("leaderboard", JSONArray().apply { state.leaderboard.forEach { put(encodeLeaderboard(it)) } })
                put("dailyStreakInfo", encodeStreak(state.dailyStreakInfo))
                put("spinHistory", JSONArray().apply { state.spinHistory.forEach { put(encodeSpin(it)) } })
                put("hasSpunThisWeek", state.hasSpunThisWeek)
                put("dailyMissions", JSONArray().apply { state.dailyMissions.forEach { put(encodeMission(it)) } })
                put("appCustomization", encodeCustomization(state.appCustomization))
                put("strictRules", JSONArray().apply { state.strictRules.forEach { put(encodeRule(it)) } })
                put("officialWhatsAppUrl", state.officialWhatsAppUrl)
                put("chatMessages", JSONArray().apply { state.chatMessages.forEach { put(encodeChat(it)) } })
                put("masterOwnerPin", state.masterOwnerPin)
                put("badgeAppeals", JSONArray().apply { state.badgeAppeals.forEach { put(encodeBadgeAppeal(it)) } })
                put("globalChatMessages", JSONArray().apply { state.globalChatMessages.forEach { put(encodeGlobalChat(it)) } })
            }
            prefs.edit().putString(KEY_STATE_JSON, root.toString()).apply()
        }
    }

    fun loadState(): PersistedAppState? {
        val raw = prefs.getString(KEY_STATE_JSON, null) ?: return null
        return runCatching {
            val root = JSONObject(raw)
            PersistedAppState(
                isLoggedIn = root.optBoolean("isLoggedIn", false),
                userProfile = decodeUserProfile(root.getJSONObject("userProfile")),
                walletBalance = root.optInt("walletBalance", 0),
                registeredUsers = decodeList(root.optJSONArray("registeredUsers"), ::decodeAppUser),
                matches = decodeList(root.optJSONArray("matches"), ::decodeMatch),
                myBookings = decodeList(root.optJSONArray("myBookings"), ::decodeBooking),
                transactions = decodeList(root.optJSONArray("transactions"), ::decodeTransaction),
                depositRequests = decodeList(root.optJSONArray("depositRequests"), ::decodeDepositRequest),
                withdrawRequests = decodeList(root.optJSONArray("withdrawRequests"), ::decodeWithdrawRequest),
                results = decodeList(root.optJSONArray("results"), ::decodeResult),
                leaderboard = decodeList(root.optJSONArray("leaderboard"), ::decodeLeaderboard),
                dailyStreakInfo = decodeStreak(root.getJSONObject("dailyStreakInfo")),
                spinHistory = decodeList(root.optJSONArray("spinHistory"), ::decodeSpin),
                hasSpunThisWeek = root.optBoolean("hasSpunThisWeek", false),
                dailyMissions = decodeList(root.optJSONArray("dailyMissions"), ::decodeMission),
                appCustomization = decodeCustomization(root.getJSONObject("appCustomization")),
                strictRules = decodeList(root.optJSONArray("strictRules"), ::decodeRule),
                officialWhatsAppUrl = root.optString("officialWhatsAppUrl", "https://chat.whatsapp.com/G8TrustedTHTournament"),
                chatMessages = decodeList(root.optJSONArray("chatMessages"), ::decodeChat),
                masterOwnerPin = root.optString("masterOwnerPin", "7788"),
                badgeAppeals = decodeList(root.optJSONArray("badgeAppeals"), ::decodeBadgeAppeal),
                globalChatMessages = decodeList(root.optJSONArray("globalChatMessages"), ::decodeGlobalChat)
            )
        }.getOrNull()
    }

    fun clearAll() {
        prefs.edit().remove(KEY_STATE_JSON).commit()
    }

    private inline fun <T> decodeList(array: JSONArray?, mapper: (JSONObject) -> T): List<T> {
        if (array == null) return emptyList()
        val list = ArrayList<T>(array.length())
        for (i in 0 until array.length()) {
            list.add(mapper(array.getJSONObject(i)))
        }
        return list
    }

    private fun JSONObject.optNullableString(key: String): String? =
        if (has(key) && !isNull(key)) getString(key) else null

    private fun JSONObject.optNullableLong(key: String): Long? =
        if (has(key) && !isNull(key)) optLong(key) else null

    private fun encodeBadgeSet(badges: Set<BadgeType>): JSONArray =
        JSONArray().apply { badges.forEach { put(it.name) } }

    private fun decodeBadgeSet(array: JSONArray?): Set<BadgeType> {
        if (array == null) return emptySet()
        val set = linkedSetOf<BadgeType>()
        for (i in 0 until array.length()) {
            val name = array.optString(i)
            enumValues<BadgeType>().firstOrNull { it.name == name }?.let { set.add(it) }
        }
        return set
    }

    private fun encodeUserProfile(p: UserProfile) = JSONObject().apply {
        put("accountName", p.accountName)
        put("phone", p.phone)
        put("email", p.email)
        put("ign", p.ign)
        put("uid", p.uid)
        put("bkashNumber", p.bkashNumber)
        put("avatarId", p.avatarId)
        put("customPfpUri", p.customPfpUri)
        put("customBannerUri", p.customBannerUri)
        put("bannerColorId", p.bannerColorId)
        put("level", p.level)
        put("rankBadge", p.rankBadge)
        put("matchesPlayed", p.matchesPlayed)
        put("totalWins", p.totalWins)
        put("totalKills", p.totalKills)
        put("totalEarnings", p.totalEarnings)
        put("totalSpent", p.totalSpent)
        put("joinedDate", p.joinedDate)
        put("isLoggedIn", p.isLoggedIn)
        put("role", p.role.name)
        put("isBanned", p.isBanned)
        put("banReason", p.banReason)
        put("favoriteWeapon", p.favoriteWeapon)
        put("activeCharacter", p.activeCharacter)
        put("headshotRate", p.headshotRate)
        put("kdRatio", p.kdRatio)
        put("guildName", p.guildName)
        put("playerRole", p.playerRole)
        put("battleTagline", p.battleTagline)
        put("totalSpinsDone", p.totalSpinsDone)
        put("totalSpinEarnings", p.totalSpinEarnings)
        put("highestSpinWon", p.highestSpinWon)
        put("customBadges", encodeBadgeSet(p.customBadges))
        put("revokedBadges", encodeBadgeSet(p.revokedBadges))
        put("warningsCount", p.warningsCount)
        put("banExpiresAtMillis", p.banExpiresAtMillis)
    }

    private fun decodeUserProfile(o: JSONObject) = UserProfile(
        accountName = o.optString("accountName", ""),
        phone = o.optString("phone", ""),
        email = o.optString("email", ""),
        ign = o.optString("ign", ""),
        uid = o.optString("uid", ""),
        bkashNumber = o.optString("bkashNumber", ""),
        avatarId = o.optString("avatarId", "th_logo"),
        customPfpUri = o.optNullableString("customPfpUri"),
        customBannerUri = o.optNullableString("customBannerUri"),
        bannerColorId = o.optString("bannerColorId", ProfileBannerColor.DEFAULT_CRIMSON.id),
        level = o.optInt("level", 1),
        rankBadge = o.optString("rankBadge", "নতুন যোদ্ধা 🛡️"),
        matchesPlayed = o.optInt("matchesPlayed", 0),
        totalWins = o.optInt("totalWins", 0),
        totalKills = o.optInt("totalKills", 0),
        totalEarnings = o.optInt("totalEarnings", 0),
        totalSpent = o.optInt("totalSpent", 0),
        joinedDate = o.optString("joinedDate", "আজ"),
        isLoggedIn = o.optBoolean("isLoggedIn", false),
        role = enumValueOrDefault(o.optString("role"), UserRole.PLAYER),
        isBanned = o.optBoolean("isBanned", false),
        banReason = o.optNullableString("banReason"),
        favoriteWeapon = o.optString("favoriteWeapon", "MP40"),
        activeCharacter = o.optString("activeCharacter", "Alok"),
        headshotRate = o.optInt("headshotRate", 0),
        kdRatio = o.optDouble("kdRatio", 0.0),
        guildName = o.optString("guildName", "নেই"),
        playerRole = o.optString("playerRole", "সোলো প্লেয়ার"),
        battleTagline = o.optString("battleTagline", "নতুন চ্যালেঞ্জার! 🎯"),
        totalSpinsDone = o.optInt("totalSpinsDone", 0),
        totalSpinEarnings = o.optInt("totalSpinEarnings", 0),
        highestSpinWon = o.optInt("highestSpinWon", 0),
        customBadges = decodeBadgeSet(o.optJSONArray("customBadges")),
        revokedBadges = decodeBadgeSet(o.optJSONArray("revokedBadges")),
        warningsCount = o.optInt("warningsCount", 0),
        banExpiresAtMillis = o.optNullableLong("banExpiresAtMillis")
    )

    private fun encodeAppUser(u: AppUser) = JSONObject().apply {
        put("id", u.id)
        put("name", u.name)
        put("phone", u.phone)
        put("ign", u.ign)
        put("uid", u.uid)
        put("bkashNumber", u.bkashNumber)
        put("password", u.password)
        put("role", u.role.name)
        put("isBanned", u.isBanned)
        put("banReason", u.banReason)
        put("bannedAt", u.bannedAt)
        put("adminPasscode", u.adminPasscode)
        put("appointedDate", u.appointedDate)
        put("joinedDate", u.joinedDate)
        put("walletBalance", u.walletBalance)
        put("matchesPlayed", u.matchesPlayed)
        put("totalWins", u.totalWins)
        put("totalKills", u.totalKills)
        put("totalEarnings", u.totalEarnings)
        put("totalSpent", u.totalSpent)
        put("customBadges", encodeBadgeSet(u.customBadges))
        put("revokedBadges", encodeBadgeSet(u.revokedBadges))
        put("avatarId", u.avatarId)
        put("customPfpUri", u.customPfpUri)
        put("customBannerUri", u.customBannerUri)
        put("bannerColorId", u.bannerColorId)
        put("battleTagline", u.battleTagline)
        put("favoriteWeapon", u.favoriteWeapon)
        put("activeCharacter", u.activeCharacter)
        put("guildName", u.guildName)
        put("playerRole", u.playerRole)
        put("level", u.level)
        put("kudosCount", u.kudosCount)
        put("warningsCount", u.warningsCount)
        put("banExpiresAtMillis", u.banExpiresAtMillis)
    }

    private fun decodeAppUser(o: JSONObject) = AppUser(
        id = o.getString("id"),
        name = o.getString("name"),
        phone = o.getString("phone"),
        ign = o.getString("ign"),
        uid = o.getString("uid"),
        bkashNumber = o.getString("bkashNumber"),
        password = o.optString("password", "123456"),
        role = enumValueOrDefault(o.optString("role"), UserRole.PLAYER),
        isBanned = o.optBoolean("isBanned", false),
        banReason = o.optNullableString("banReason"),
        bannedAt = o.optNullableString("bannedAt"),
        adminPasscode = o.optNullableString("adminPasscode"),
        appointedDate = o.optNullableString("appointedDate"),
        joinedDate = o.optString("joinedDate", "আজ"),
        walletBalance = o.optInt("walletBalance", 0),
        matchesPlayed = o.optInt("matchesPlayed", 0),
        totalWins = o.optInt("totalWins", 0),
        totalKills = o.optInt("totalKills", 0),
        totalEarnings = o.optInt("totalEarnings", 0),
        totalSpent = o.optInt("totalSpent", 0),
        customBadges = decodeBadgeSet(o.optJSONArray("customBadges")),
        revokedBadges = decodeBadgeSet(o.optJSONArray("revokedBadges")),
        avatarId = o.optString("avatarId", "th_logo"),
        customPfpUri = o.optNullableString("customPfpUri"),
        customBannerUri = o.optNullableString("customBannerUri"),
        bannerColorId = o.optString("bannerColorId", ProfileBannerColor.DEFAULT_CRIMSON.id),
        battleTagline = o.optString("battleTagline", "নতুন চ্যালেঞ্জার! 🎯"),
        favoriteWeapon = o.optString("favoriteWeapon", "MP40"),
        activeCharacter = o.optString("activeCharacter", "Alok"),
        guildName = o.optString("guildName", "TRUSTED TH"),
        playerRole = o.optString("playerRole", "সোলো প্লেয়ার"),
        level = o.optInt("level", 1),
        kudosCount = o.optInt("kudosCount", 0),
        warningsCount = o.optInt("warningsCount", 0),
        banExpiresAtMillis = o.optNullableLong("banExpiresAtMillis")
    )

    private fun encodeMatch(m: TournamentMatch) = JSONObject().apply {
        put("id", m.id)
        put("title", m.title)
        put("category", m.category.name)
        put("matchType", m.matchType.name)
        put("mapName", m.mapName)
        put("matchDateTime", m.matchDateTime)
        put("startsInMinutes", m.startsInMinutes)
        put("entryFee", m.entryFee)
        put("totalPrize", m.totalPrize)
        put("perKillPrize", m.perKillPrize)
        put("booyahPrize", m.booyahPrize)
        put("totalSlots", m.totalSlots)
        put("bookedSlots", m.bookedSlots)
        put("status", m.status.name)
        put("roomId", m.roomId)
        put("roomPassword", m.roomPassword)
        put("version", m.version)
    }

    private fun decodeMatch(o: JSONObject) = TournamentMatch(
        id = o.getString("id"),
        title = o.getString("title"),
        category = enumValueOrDefault(o.optString("category"), GameCategory.BR_RANK),
        matchType = enumValueOrDefault(o.optString("matchType"), MatchType.SOLO),
        mapName = o.getString("mapName"),
        matchDateTime = o.getString("matchDateTime"),
        startsInMinutes = o.optInt("startsInMinutes", 30),
        entryFee = o.optInt("entryFee", 10),
        totalPrize = o.optInt("totalPrize", 100),
        perKillPrize = o.optInt("perKillPrize", 0),
        booyahPrize = o.optInt("booyahPrize", 50),
        totalSlots = o.optInt("totalSlots", 48),
        bookedSlots = o.optInt("bookedSlots", 0),
        status = enumValueOrDefault(o.optString("status"), MatchStatus.UPCOMING),
        roomId = o.optNullableString("roomId"),
        roomPassword = o.optNullableString("roomPassword"),
        version = o.optString("version", "মোবাইল অনলি")
    )

    private fun encodeBooking(b: SlotBooking) = JSONObject().apply {
        put("bookingId", b.bookingId)
        put("matchId", b.matchId)
        put("matchTitle", b.matchTitle)
        put("category", b.category.name)
        put("matchDateTime", b.matchDateTime)
        put("slotNumber", b.slotNumber)
        put("playerIgn", b.playerIgn)
        put("playerUid", b.playerUid)
        put("bkashSenderNumber", b.bkashSenderNumber)
        put("trxId", b.trxId)
        put("feePaid", b.feePaid)
        put("isVerified", b.isVerified)
        put("roomId", b.roomId)
        put("roomPassword", b.roomPassword)
    }

    private fun decodeBooking(o: JSONObject) = SlotBooking(
        bookingId = o.getString("bookingId"),
        matchId = o.getString("matchId"),
        matchTitle = o.getString("matchTitle"),
        category = enumValueOrDefault(o.optString("category"), GameCategory.BR_RANK),
        matchDateTime = o.getString("matchDateTime"),
        slotNumber = o.optInt("slotNumber", 1),
        playerIgn = o.getString("playerIgn"),
        playerUid = o.getString("playerUid"),
        bkashSenderNumber = o.getString("bkashSenderNumber"),
        trxId = o.getString("trxId"),
        feePaid = o.optInt("feePaid", 0),
        isVerified = o.optBoolean("isVerified", true),
        roomId = o.optNullableString("roomId"),
        roomPassword = o.optNullableString("roomPassword")
    )

    private fun encodeTransaction(t: WalletTransaction) = JSONObject().apply {
        put("id", t.id)
        put("title", t.title)
        put("amount", t.amount)
        put("isCredit", t.isCredit)
        put("timeAgo", t.timeAgo)
        put("statusText", t.statusText)
        put("paymentMethod", t.paymentMethod)
        put("category", t.category.name)
        put("trxId", t.trxId)
        put("slotNumber", t.slotNumber)
        put("matchTitle", t.matchTitle)
        put("senderBkash", t.senderBkash)
        put("exactDateTime", t.exactDateTime)
        put("userPhone", t.userPhone)
        put("requestId", t.requestId)
    }

    private fun decodeTransaction(o: JSONObject) = WalletTransaction(
        id = o.getString("id"),
        title = o.getString("title"),
        amount = o.getInt("amount"),
        isCredit = o.getBoolean("isCredit"),
        timeAgo = o.optString("timeAgo", "এইমাত্র"),
        statusText = o.optString("statusText", ""),
        paymentMethod = o.optString("paymentMethod", "bKash"),
        category = enumValueOrDefault(o.optString("category"), TransactionCategory.ALL),
        trxId = o.optNullableString("trxId"),
        slotNumber = if (o.has("slotNumber") && !o.isNull("slotNumber")) o.getInt("slotNumber") else null,
        matchTitle = o.optNullableString("matchTitle"),
        senderBkash = o.optNullableString("senderBkash"),
        exactDateTime = o.optString("exactDateTime", "আজ এইমাত্র"),
        userPhone = o.optString("userPhone", ""),
        requestId = o.optNullableString("requestId")
    )

    private fun encodeDepositRequest(r: DepositRequest) = JSONObject().apply {
        put("id", r.id)
        put("userPhone", r.userPhone)
        put("userName", r.userName)
        put("userIgn", r.userIgn)
        put("amount", r.amount)
        put("paymentMethod", r.paymentMethod.name)
        put("senderNumber", r.senderNumber)
        put("trxId", r.trxId)
        put("status", r.status.name)
        put("createdAt", r.createdAt)
        put("adminNote", r.adminNote)
    }

    private fun decodeDepositRequest(o: JSONObject) = DepositRequest(
        id = o.getString("id"),
        userPhone = o.getString("userPhone"),
        userName = o.getString("userName"),
        userIgn = o.optString("userIgn", ""),
        amount = o.getInt("amount"),
        paymentMethod = enumValueOrDefault(o.optString("paymentMethod"), WalletPaymentMethod.BKASH),
        senderNumber = o.getString("senderNumber"),
        trxId = o.getString("trxId"),
        status = enumValueOrDefault(o.optString("status"), RequestStatus.PENDING),
        createdAt = o.optString("createdAt", "এইমাত্র"),
        adminNote = o.optString("adminNote", "")
    )

    private fun encodeWithdrawRequest(r: WithdrawRequest) = JSONObject().apply {
        put("id", r.id)
        put("userPhone", r.userPhone)
        put("userName", r.userName)
        put("userIgn", r.userIgn)
        put("amount", r.amount)
        put("paymentMethod", r.paymentMethod.name)
        put("targetNumber", r.targetNumber)
        put("status", r.status.name)
        put("createdAt", r.createdAt)
        put("adminNote", r.adminNote)
    }

    private fun decodeWithdrawRequest(o: JSONObject) = WithdrawRequest(
        id = o.getString("id"),
        userPhone = o.getString("userPhone"),
        userName = o.getString("userName"),
        userIgn = o.optString("userIgn", ""),
        amount = o.getInt("amount"),
        paymentMethod = enumValueOrDefault(o.optString("paymentMethod"), WalletPaymentMethod.BKASH),
        targetNumber = o.getString("targetNumber"),
        status = enumValueOrDefault(o.optString("status"), RequestStatus.PENDING),
        createdAt = o.optString("createdAt", "এইমাত্র"),
        adminNote = o.optString("adminNote", "")
    )

    private fun encodeResult(r: MatchResult) = JSONObject().apply {
        put("id", r.id)
        put("matchTitle", r.matchTitle)
        put("category", r.category.name)
        put("completedDate", r.completedDate)
        put("winnerIgn", r.winnerIgn)
        put("winnerUid", r.winnerUid)
        put("winnerKills", r.winnerKills)
        put("prizeMoney", r.prizeMoney)
        put("runnerUpIgn", r.runnerUpIgn)
        put("totalPlayers", r.totalPlayers)
    }

    private fun decodeResult(o: JSONObject) = MatchResult(
        id = o.getString("id"),
        matchTitle = o.getString("matchTitle"),
        category = enumValueOrDefault(o.optString("category"), GameCategory.BR_RANK),
        completedDate = o.getString("completedDate"),
        winnerIgn = o.getString("winnerIgn"),
        winnerUid = o.getString("winnerUid"),
        winnerKills = o.optInt("winnerKills", 0),
        prizeMoney = o.optInt("prizeMoney", 0),
        runnerUpIgn = o.optString("runnerUpIgn"),
        totalPlayers = o.optInt("totalPlayers", 0)
    )

    private fun encodeLeaderboard(l: LeaderboardPlayer) = JSONObject().apply {
        put("rank", l.rank)
        put("ign", l.ign)
        put("uid", l.uid)
        put("totalKills", l.totalKills)
        put("booyahs", l.booyahs)
        put("totalEarnings", l.totalEarnings)
        put("badge", l.badge)
        put("primaryBadge", l.primaryBadge.name)
        put("totalSpent", l.totalSpent)
        put("matchesPlayed", l.matchesPlayed)
    }

    private fun decodeLeaderboard(o: JSONObject) = LeaderboardPlayer(
        rank = o.getInt("rank"),
        ign = o.getString("ign"),
        uid = o.getString("uid"),
        totalKills = o.optInt("totalKills", 0),
        booyahs = o.optInt("booyahs", 0),
        totalEarnings = o.optInt("totalEarnings", 0),
        badge = o.optString("badge", "⚡"),
        primaryBadge = enumValueOrDefault(o.optString("primaryBadge"), BadgeType.PRO_PLAYER),
        totalSpent = o.optInt("totalSpent", 0),
        matchesPlayed = o.optInt("matchesPlayed", 0)
    )

    private fun encodeStreak(s: DailyStreakInfo) = JSONObject().apply {
        put("currentStreakDay", s.currentStreakDay)
        put("isTodayClaimed", s.isTodayClaimed)
        put("streakHistoryDays", JSONArray().apply { s.streakHistoryDays.forEach { put(it) } })
    }

    private fun decodeStreak(o: JSONObject): DailyStreakInfo {
        val arr = o.optJSONArray("streakHistoryDays")
        val set = mutableSetOf<Int>()
        if (arr != null) {
            for (i in 0 until arr.length()) set.add(arr.getInt(i))
        }
        return DailyStreakInfo(
            currentStreakDay = o.optInt("currentStreakDay", 1),
            isTodayClaimed = o.optBoolean("isTodayClaimed", false),
            streakHistoryDays = set
        )
    }

    private fun encodeSpin(s: SpinHistoryRecord) = JSONObject().apply {
        put("id", s.id)
        put("amount", s.amount)
        put("date", s.date)
        put("isJackpot", s.isJackpot)
    }

    private fun decodeSpin(o: JSONObject) = SpinHistoryRecord(
        id = o.getString("id"),
        amount = o.getInt("amount"),
        date = o.getString("date"),
        isJackpot = o.optBoolean("isJackpot", false)
    )

    private fun encodeMission(m: DailyGamerMission) = JSONObject().apply {
        put("id", m.id)
        put("title", m.title)
        put("description", m.description)
        put("rewardAmount", m.rewardAmount)
        put("isCompleted", m.isCompleted)
        put("isClaimed", m.isClaimed)
        put("iconEmoji", m.iconEmoji)
        put("actionType", m.actionType)
    }

    private fun decodeMission(o: JSONObject) = DailyGamerMission(
        id = o.getString("id"),
        title = o.getString("title"),
        description = o.getString("description"),
        rewardAmount = o.getInt("rewardAmount"),
        isCompleted = o.optBoolean("isCompleted", false),
        isClaimed = o.optBoolean("isClaimed", false),
        iconEmoji = o.optString("iconEmoji", "🎯"),
        actionType = o.optString("actionType", "MATCH")
    )

    private fun encodeCustomization(c: AppCustomization) = JSONObject().apply {
        put("appName", c.appName)
        put("appTagline", c.appTagline)
        put("selectedLogo", c.selectedLogo.name)
        put("noticeTickerText", c.noticeTickerText)
        put("activeAnnouncement", c.activeAnnouncement)
        put("officialBkashNumber", c.officialBkashNumber)
        put("officialNagadNumber", c.officialNagadNumber)
        put("officialWhatsAppUrl", c.officialWhatsAppUrl)
        put("minWithdraw", c.minWithdraw)
        put("maxWithdraw", c.maxWithdraw)
        put("isMaintenanceMode", c.isMaintenanceMode)
        put("maintenanceMessage", c.maintenanceMessage)
    }

    private fun decodeCustomization(o: JSONObject) = AppCustomization(
        appName = o.optString("appName", "TRUSTED TOURNAMENT BY TH"),
        appTagline = o.optString("appTagline", "১০০% বিশ্বস্ত • মোবাইল অনলি • দ্রুত ক্যাশআউট"),
        selectedLogo = enumValueOrDefault(o.optString("selectedLogo"), AppLogoOption.TH_LOGO),
        noticeTickerText = o.optString("noticeTickerText", "ম্যাচ শুরুর ১৫ মিনিট আগে অ্যাপে কাস্টম রুম আইডি ও পাসওয়ার্ড দেওয়া হবে!"),
        activeAnnouncement = o.optString("activeAnnouncement", "প্রতিদিন বিকাল ৫:০০ থেকে রাত ৮:০০ পর্যন্ত সুপারফাস্ট এক্সপ্রেস ম্যাচ!"),
        officialBkashNumber = o.optString("officialBkashNumber", "01861583924"),
        officialNagadNumber = o.optString("officialNagadNumber", "01861583924"),
        officialWhatsAppUrl = o.optString("officialWhatsAppUrl", "https://chat.whatsapp.com/G8TrustedTHTournament"),
        minWithdraw = o.optInt("minWithdraw", 50),
        maxWithdraw = o.optInt("maxWithdraw", 200),
        isMaintenanceMode = o.optBoolean("isMaintenanceMode", false),
        maintenanceMessage = o.optString("maintenanceMessage", "সার্ভার রক্ষণাবেক্ষণ চলছে। শীঘ্রই টুর্নামেন্ট রুম উন্মুক্ত করা হবে।")
    )

    private fun encodeRule(r: TournamentRuleItem) = JSONObject().apply {
        put("id", r.id)
        put("title", r.title)
        put("description", r.description)
        put("penalty", r.penalty)
        put("iconEmoji", r.iconEmoji)
        put("isStrict", r.isStrict)
    }

    private fun decodeRule(o: JSONObject) = TournamentRuleItem(
        id = o.getString("id"),
        title = o.getString("title"),
        description = o.getString("description"),
        penalty = o.getString("penalty"),
        iconEmoji = o.optString("iconEmoji", "⚠️"),
        isStrict = o.optBoolean("isStrict", true)
    )

    private fun encodeChat(c: ChatMessage) = JSONObject().apply {
        put("id", c.id)
        put("senderName", c.senderName)
        put("isFromAdmin", c.isFromAdmin)
        put("text", c.text)
        put("timeAgo", c.timeAgo)
        put("isAiAssistant", c.isAiAssistant)
        put("actionType", c.actionType)
        put("actionLabel", c.actionLabel)
    }

    private fun decodeChat(o: JSONObject) = ChatMessage(
        id = o.getString("id"),
        senderName = o.getString("senderName"),
        isFromAdmin = o.optBoolean("isFromAdmin", false),
        text = o.getString("text"),
        timeAgo = o.optString("timeAgo", "এইমাত্র"),
        isAiAssistant = o.optBoolean("isAiAssistant", false),
        actionType = o.optNullableString("actionType"),
        actionLabel = o.optNullableString("actionLabel")
    )

    private fun encodeBadgeAppeal(a: BadgeAppeal) = JSONObject().apply {
        put("id", a.id)
        put("userId", a.userId)
        put("userName", a.userName)
        put("userIgn", a.userIgn)
        put("userUid", a.userUid)
        put("userPhone", a.userPhone)
        put("requestedBadge", a.requestedBadge.name)
        put("reasonNote", a.reasonNote)
        put("status", a.status.name)
        put("grantedBadge", a.grantedBadge?.name)
        put("adminNote", a.adminNote)
        put("reviewedBy", a.reviewedBy)
        put("createdAt", a.createdAt)
        put("matchesPlayedSnapshot", a.matchesPlayedSnapshot)
        put("totalSpentSnapshot", a.totalSpentSnapshot)
        put("totalWinsSnapshot", a.totalWinsSnapshot)
        put("totalKillsSnapshot", a.totalKillsSnapshot)
        put("totalEarningsSnapshot", a.totalEarningsSnapshot)
        put("chatMessages", JSONArray().apply { a.chatMessages.forEach { put(encodeAppealChat(it)) } })
    }

    private fun decodeBadgeAppeal(o: JSONObject): BadgeAppeal {
        val grantedName = o.optNullableString("grantedBadge")
        val granted = grantedName?.let { name -> enumValues<BadgeType>().firstOrNull { it.name == name } }
        return BadgeAppeal(
            id = o.getString("id"),
            userId = o.optString("userId", ""),
            userName = o.optString("userName", ""),
            userIgn = o.optString("userIgn", ""),
            userUid = o.optString("userUid", ""),
            userPhone = o.optString("userPhone", ""),
            requestedBadge = enumValueOrDefault(o.optString("requestedBadge"), BadgeType.PRO_PLAYER),
            reasonNote = o.optString("reasonNote", ""),
            status = enumValueOrDefault(o.optString("status"), AppealStatus.PENDING),
            grantedBadge = granted,
            adminNote = o.optString("adminNote", ""),
            reviewedBy = o.optString("reviewedBy", ""),
            createdAt = o.optString("createdAt", "এইমাত্র"),
            matchesPlayedSnapshot = o.optInt("matchesPlayedSnapshot", 0),
            totalSpentSnapshot = o.optInt("totalSpentSnapshot", 0),
            totalWinsSnapshot = o.optInt("totalWinsSnapshot", 0),
            totalKillsSnapshot = o.optInt("totalKillsSnapshot", 0),
            totalEarningsSnapshot = o.optInt("totalEarningsSnapshot", 0),
            chatMessages = decodeList(o.optJSONArray("chatMessages"), ::decodeAppealChat)
        )
    }

    private fun encodeAppealChat(m: AppealChatMessage) = JSONObject().apply {
        put("id", m.id)
        put("senderName", m.senderName)
        put("senderRoleLabel", m.senderRoleLabel)
        put("isFromStaff", m.isFromStaff)
        put("text", m.text)
        put("proofImageUri", m.proofImageUri)
        put("timeAgo", m.timeAgo)
    }

    private fun decodeAppealChat(o: JSONObject) = AppealChatMessage(
        id = o.optString("id", ""),
        senderName = o.optString("senderName", ""),
        senderRoleLabel = o.optString("senderRoleLabel", "প্লেয়ার"),
        isFromStaff = o.optBoolean("isFromStaff", false),
        text = o.optString("text", ""),
        proofImageUri = o.optNullableString("proofImageUri"),
        timeAgo = o.optString("timeAgo", "এইমাত্র")
    )

    private fun encodeGlobalChat(m: GlobalChatMessage) = JSONObject().apply {
        put("id", m.id)
        put("senderUserId", m.senderUserId)
        put("senderName", m.senderName)
        put("senderIgn", m.senderIgn)
        put("senderUid", m.senderUid)
        put("senderPhone", m.senderPhone)
        put("senderRole", m.senderRole.name)
        put("senderAvatarId", m.senderAvatarId)
        put("senderCustomPfpUri", m.senderCustomPfpUri)
        put("senderPrimaryBadge", m.senderPrimaryBadge.name)
        put("senderBadges", JSONArray().apply { m.senderBadges.forEach { put(it.name) } })
        put("text", m.text)
        put("timestampMillis", m.timestampMillis)
        put("timeLabel", m.timeLabel)
        put("isPinnedAnnouncement", m.isPinnedAnnouncement)
        put("replyToId", m.replyToId)
        put("replyToIgn", m.replyToIgn)
        put("replyToText", m.replyToText)
        put("reactions", JSONObject().apply { m.reactions.forEach { (k, v) -> put(k, v) } })
    }

    private fun decodeGlobalChat(o: JSONObject): GlobalChatMessage {
        val badgesArr = o.optJSONArray("senderBadges")
        val badges = mutableListOf<BadgeType>()
        if (badgesArr != null) {
            for (i in 0 until badgesArr.length()) {
                val bName = badgesArr.optString(i)
                enumValues<BadgeType>().firstOrNull { it.name == bName }?.let { badges.add(it) }
            }
        }
        val reactionsObj = o.optJSONObject("reactions")
        val reactionsMap = linkedMapOf<String, Int>()
        if (reactionsObj != null) {
            val keys = reactionsObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                val v = reactionsObj.optInt(k, 0)
                if (v > 0) reactionsMap[k] = v
            }
        }
        return GlobalChatMessage(
            id = o.optString("id", ""),
            senderUserId = o.optString("senderUserId", ""),
            senderName = o.optString("senderName", ""),
            senderIgn = o.optString("senderIgn", "Player"),
            senderUid = o.optString("senderUid", ""),
            senderPhone = o.optString("senderPhone", ""),
            senderRole = enumValueOrDefault(o.optString("senderRole"), UserRole.PLAYER),
            senderAvatarId = o.optString("senderAvatarId", "th_logo"),
            senderCustomPfpUri = o.optNullableString("senderCustomPfpUri"),
            senderPrimaryBadge = enumValueOrDefault(o.optString("senderPrimaryBadge"), BadgeType.PRO_PLAYER),
            senderBadges = badges,
            text = o.optString("text", ""),
            timestampMillis = o.optLong("timestampMillis", System.currentTimeMillis()),
            timeLabel = o.optString("timeLabel", "এইমাত্র"),
            isPinnedAnnouncement = o.optBoolean("isPinnedAnnouncement", false),
            replyToId = o.optNullableString("replyToId"),
            replyToIgn = o.optNullableString("replyToIgn"),
            replyToText = o.optNullableString("replyToText"),
            reactions = reactionsMap
        )
    }

    private inline fun <reified T : Enum<T>> enumValueOrDefault(name: String, default: T): T {
        return enumValues<T>().firstOrNull { it.name == name } ?: default
    }
}
