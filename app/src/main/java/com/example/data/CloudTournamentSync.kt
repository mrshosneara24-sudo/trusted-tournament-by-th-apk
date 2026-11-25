package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.model.AppCustomization
import com.example.model.AppLogoOption
import com.example.model.AppUser
import com.example.model.AppealChatMessage
import com.example.model.AppealStatus
import com.example.model.BadgeAppeal
import com.example.model.BadgeType
import com.example.model.BonusGiftDrop
import com.example.model.DepositRequest
import com.example.model.GameCategory
import com.example.model.GiveawayEntry
import com.example.model.GiveawayEvent
import com.example.model.GiveawayPrizeType
import com.example.model.GiveawayStatus
import com.example.model.GiveawayWinner
import com.example.model.GlobalChatMessage
import com.example.model.LiveCommunityPoll
import com.example.model.MatchStatus
import com.example.model.MatchType
import com.example.model.ModerationActionType
import com.example.model.ModerationLogEntry
import com.example.model.PollOptionItem
import com.example.model.ProfileBannerColor
import com.example.model.PromoCodeItem
import com.example.model.RequestStatus
import com.example.model.SlotBooking
import com.example.model.TournamentMatch
import com.example.model.TournamentRuleItem
import com.example.model.UserRole
import com.example.model.WalletPaymentMethod
import com.example.model.WithdrawRequest
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Real-time multi-device Cloud Firestore sync manager.
 * Always targets the single named Firestore database (`R.string.firestore_database_id`)
 * and gates all queries/writes on an authenticated Firebase user.
 */
class CloudTournamentSync(private val context: Context) {

    companion object {
        private const val TAG = "CloudTournamentSync"
        private const val BOOTSTRAPPED_OWNER_EMAIL = "mrshosneara24@gmail.com"
        private const val COL_ADMINS = "admins"
        private const val COL_APP_CONFIG = "app_config"
        private const val DOC_MAIN_CONFIG = "main"
        private const val COL_MATCHES = "matches"
        private const val COL_BOOKINGS = "bookings"
        private const val COL_PLAYER_DIRECTORY = "player_directory"
        private const val COL_USER_ACCOUNTS = "user_accounts"
        private const val COL_DEPOSITS = "deposit_requests"
        private const val COL_WITHDRAWS = "withdraw_requests"
        private const val COL_BADGE_APPEALS = "badge_appeals"
        private const val COL_GLOBAL_CHAT = "global_chat"
        private const val COL_MODERATION_LOGS = "moderation_logs"
        private const val COL_PROMO_CODES = "promo_codes"
        private const val COL_BONUS_DROPS = "bonus_drops"
        private const val COL_COMMUNITY_POLLS = "community_polls"
        private const val COL_GIVEAWAYS = "giveaways"
    }

    data class SyncCallbacks(
        val onAppConfigSynced: (AppCustomization, List<TournamentRuleItem>?) -> Unit,
        val onMatchesSynced: (List<TournamentMatch>) -> Unit,
        val onBookingsSynced: (List<SlotBooking>) -> Unit,
        val onPlayersSynced: (List<AppUser>) -> Unit,
        val onPrivateAccountsSynced: (Map<String, Triple<String, String, String>>) -> Unit,
        val onDepositsSynced: (List<DepositRequest>) -> Unit,
        val onWithdrawsSynced: (List<WithdrawRequest>) -> Unit,
        val onAppealsSynced: (List<BadgeAppeal>) -> Unit,
        val onGlobalChatSynced: (List<GlobalChatMessage>) -> Unit,
        val onModerationLogsSynced: (List<ModerationLogEntry>) -> Unit,
        val onPromoCodesSynced: (List<PromoCodeItem>) -> Unit = {},
        val onBonusDropsSynced: (List<BonusGiftDrop>) -> Unit = {},
        val onCommunityPollsSynced: (List<LiveCommunityPoll>) -> Unit = {},
        val onGiveawaysSynced: (List<GiveawayEvent>) -> Unit = {},
        val onSyncStateChanged: (connected: Boolean, email: String, statusText: String) -> Unit
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val registrations = mutableListOf<ListenerRegistration>()
    private val playerFirebaseUidByUserId = mutableMapOf<String, String>()
    private var attachedUid: String? = null
    private var callbacks: SyncCallbacks? = null

    private fun isFirebaseReady(): Boolean = runCatching {
        FirebaseApp.getApps(context).isNotEmpty() || FirebaseApp.initializeApp(context) != null
    }.getOrDefault(false)

    private fun firestoreOrNull(): FirebaseFirestore? {
        if (!isFirebaseReady()) return null
        return runCatching {
            val dbId = context.getString(R.string.firestore_database_id)
            FirebaseFirestore.getInstance(dbId)
        }.getOrNull()
    }

    private fun currentUser(): FirebaseUser? {
        if (!isFirebaseReady()) return null
        return runCatching { FirebaseAuth.getInstance().currentUser }.getOrNull()
    }

    fun isSignedInCloud(): Boolean = currentUser() != null

    fun currentCloudEmail(): String = currentUser()?.email.orEmpty()

    fun currentCloudUid(): String = currentUser()?.uid.orEmpty()

    private fun isOwnerEmail(user: FirebaseUser?): Boolean =
        user?.email?.equals(BOOTSTRAPPED_OWNER_EMAIL, ignoreCase = true) == true

    private suspend fun isCloudStaff(db: FirebaseFirestore, user: FirebaseUser): Boolean {
        if (isOwnerEmail(user)) return true
        return runCatching {
            db.collection(COL_ADMINS).document(user.uid).get().await().exists()
        }.getOrDefault(false)
    }

    fun startRealtimeSync(syncCallbacks: SyncCallbacks) {
        callbacks = syncCallbacks
        val user = currentUser()
        val db = firestoreOrNull()
        if (user == null || db == null) {
            syncCallbacks.onSyncStateChanged(false, "", "অফলাইন / লোকাল মোড সক্রিয়")
            return
        }
        if (attachedUid == user.uid && registrations.isNotEmpty()) {
            return
        }
        detachListeners()
        attachedUid = user.uid

        scope.launch {
            val isOwner = isOwnerEmail(user)
            val isStaff = isCloudStaff(db, user)
            syncCallbacks.onSyncStateChanged(
                true,
                user.email.orEmpty(),
                "লাইভ ক্লাউড সিঙ্ক চালু (${user.email ?: "Connected"})"
            )

            // 1. App Config & Rules
            registrations += db.collection(COL_APP_CONFIG).document(DOC_MAIN_CONFIG)
                .addSnapshotListener { snap, err ->
                    if (err != null) {
                        handleFirestoreError(err, "LISTEN", "$COL_APP_CONFIG/$DOC_MAIN_CONFIG")
                        return@addSnapshotListener
                    }
                    if (snap != null && snap.exists()) {
                        parseAppConfig(snap)?.let { (cfg, rules) ->
                            callbacks?.onAppConfigSynced(cfg, rules)
                        }
                    }
                }

            // 2. Tournament Matches (status filter aligns with allow list)
            registrations += db.collection(COL_MATCHES)
                .whereIn("status", listOf("UPCOMING", "ROOM_OPEN", "ONGOING", "FINISHED", "CANCELLED"))
                .addSnapshotListener { snaps, err ->
                    if (err != null) {
                        handleFirestoreError(err, "LIST", COL_MATCHES)
                        return@addSnapshotListener
                    }
                    if (snaps != null) {
                        val list = snaps.documents.mapNotNull { parseMatch(it) }
                        if (list.isNotEmpty()) {
                            callbacks?.onMatchesSynced(list)
                        }
                    }
                }

            // 3. Slot Bookings (non-PII shared across all friends)
            registrations += db.collection(COL_BOOKINGS)
                .whereGreaterThanOrEqualTo("slotNumber", 1)
                .addSnapshotListener { snaps, err ->
                    if (err != null) {
                        handleFirestoreError(err, "LIST", COL_BOOKINGS)
                        return@addSnapshotListener
                    }
                    if (snaps != null) {
                        val list = snaps.documents.mapNotNull { parseBooking(it) }
                        callbacks?.onBookingsSynced(list)
                    }
                }

            // 4. Public Player Directory (non-PII roster, badges, kudos & moderation flags)
            registrations += db.collection(COL_PLAYER_DIRECTORY)
                .whereGreaterThanOrEqualTo("level", 0)
                .addSnapshotListener { snaps, err ->
                    if (err != null) {
                        handleFirestoreError(err, "LIST", COL_PLAYER_DIRECTORY)
                        return@addSnapshotListener
                    }
                    if (snaps != null) {
                        val list = snaps.documents.mapNotNull { parsePlayerDirectory(it) }
                        if (list.isNotEmpty()) {
                            callbacks?.onPlayersSynced(list)
                        }
                    }
                }

            // 5. Global Chat
            registrations += db.collection(COL_GLOBAL_CHAT)
                .whereGreaterThanOrEqualTo("timestampMillis", 0L)
                .addSnapshotListener { snaps, err ->
                    if (err != null) {
                        handleFirestoreError(err, "LIST", COL_GLOBAL_CHAT)
                        return@addSnapshotListener
                    }
                    if (snaps != null) {
                        val list = snaps.documents.mapNotNull { parseGlobalChat(it) }
                            .sortedBy { it.timestampMillis }
                        if (list.isNotEmpty()) {
                            callbacks?.onGlobalChatSynced(list)
                        }
                    }
                }

            // 6. Private User Accounts (owner sees all, users see own)
            val accountsQuery = if (isOwner) {
                db.collection(COL_USER_ACCOUNTS)
            } else {
                db.collection(COL_USER_ACCOUNTS).whereEqualTo("ownerUid", user.uid)
            }
            registrations += accountsQuery.addSnapshotListener { snaps, err ->
                if (err != null) {
                    handleFirestoreError(err, "LIST", COL_USER_ACCOUNTS)
                    return@addSnapshotListener
                }
                if (snaps != null) {
                    val map = mutableMapOf<String, Triple<String, String, String>>()
                    snaps.documents.forEach { d ->
                        val profileId = d.getString("profileId") ?: d.id
                        val phone = d.getString("phone").orEmpty()
                        val bkash = d.getString("bkashNumber").orEmpty()
                        val joined = d.getString("joinedDate").orEmpty()
                        if (phone.isNotBlank()) {
                            map[profileId] = Triple(phone, bkash.ifBlank { phone }, joined)
                        }
                    }
                    callbacks?.onPrivateAccountsSynced(map)
                }
            }

            // 7. Deposit Requests
            val depositsQuery = if (isOwner) {
                db.collection(COL_DEPOSITS)
            } else {
                db.collection(COL_DEPOSITS).whereEqualTo("ownerUid", user.uid)
            }
            registrations += depositsQuery.addSnapshotListener { snaps, err ->
                if (err != null) {
                    handleFirestoreError(err, "LIST", COL_DEPOSITS)
                    return@addSnapshotListener
                }
                if (snaps != null) {
                    callbacks?.onDepositsSynced(snaps.documents.mapNotNull { parseDeposit(it) })
                }
            }

            // 8. Withdraw Requests
            val withdrawsQuery = if (isOwner) {
                db.collection(COL_WITHDRAWS)
            } else {
                db.collection(COL_WITHDRAWS).whereEqualTo("ownerUid", user.uid)
            }
            registrations += withdrawsQuery.addSnapshotListener { snaps, err ->
                if (err != null) {
                    handleFirestoreError(err, "LIST", COL_WITHDRAWS)
                    return@addSnapshotListener
                }
                if (snaps != null) {
                    callbacks?.onWithdrawsSynced(snaps.documents.mapNotNull { parseWithdraw(it) })
                }
            }

            // 9. Badge Appeals
            val appealsQuery = if (isOwner) {
                db.collection(COL_BADGE_APPEALS)
            } else {
                db.collection(COL_BADGE_APPEALS).whereEqualTo("ownerUid", user.uid)
            }
            registrations += appealsQuery.addSnapshotListener { snaps, err ->
                if (err != null) {
                    handleFirestoreError(err, "LIST", COL_BADGE_APPEALS)
                    return@addSnapshotListener
                }
                if (snaps != null) {
                    callbacks?.onAppealsSynced(snaps.documents.mapNotNull { parseAppeal(it) })
                }
            }

            // 10. Moderation Logs
            val logsQuery = if (isOwner || isStaff && isOwner) {
                db.collection(COL_MODERATION_LOGS)
            } else {
                db.collection(COL_MODERATION_LOGS).whereEqualTo("targetAuthUid", user.uid)
            }
            registrations += logsQuery.addSnapshotListener { snaps, err ->
                if (err != null) {
                    handleFirestoreError(err, "LIST", COL_MODERATION_LOGS)
                    return@addSnapshotListener
                }
                if (snaps != null) {
                    val list = snaps.documents.mapNotNull { parseModLog(it) }
                        .sortedByDescending { it.timestampMillis }
                    callbacks?.onModerationLogsSynced(list)
                }
            }

            // 11. Promo Redeem Codes
            registrations += db.collection(COL_PROMO_CODES)
                .whereGreaterThanOrEqualTo("createdAtMillis", 0L)
                .addSnapshotListener { snaps, err ->
                    if (err != null) {
                        handleFirestoreError(err, "LIST", COL_PROMO_CODES)
                        return@addSnapshotListener
                    }
                    if (snaps != null) {
                        val list = snaps.documents.mapNotNull { parsePromoCode(it) }
                            .sortedByDescending { it.createdAtMillis }
                        callbacks?.onPromoCodesSynced(list)
                    }
                }

            // 12. Live Bonus Gift Drops
            registrations += db.collection(COL_BONUS_DROPS)
                .whereGreaterThanOrEqualTo("createdAtMillis", 0L)
                .addSnapshotListener { snaps, err ->
                    if (err != null) {
                        handleFirestoreError(err, "LIST", COL_BONUS_DROPS)
                        return@addSnapshotListener
                    }
                    if (snaps != null) {
                        val list = snaps.documents.mapNotNull { parseBonusDrop(it) }
                            .sortedByDescending { it.createdAtMillis }
                        callbacks?.onBonusDropsSynced(list)
                    }
                }

            // 13. Live Community Polls
            registrations += db.collection(COL_COMMUNITY_POLLS)
                .whereGreaterThanOrEqualTo("createdAtMillis", 0L)
                .addSnapshotListener { snaps, err ->
                    if (err != null) {
                        handleFirestoreError(err, "LIST", COL_COMMUNITY_POLLS)
                        return@addSnapshotListener
                    }
                    if (snaps != null) {
                        val list = snaps.documents.mapNotNull { parseCommunityPoll(it) }
                            .sortedByDescending { it.createdAtMillis }
                        callbacks?.onCommunityPollsSynced(list)
                    }
                }

            // 14. Live Mega Giveaways
            registrations += db.collection(COL_GIVEAWAYS)
                .whereGreaterThanOrEqualTo("createdAtMillis", 0L)
                .addSnapshotListener { snaps, err ->
                    if (err != null) {
                        handleFirestoreError(err, "LIST", COL_GIVEAWAYS)
                        return@addSnapshotListener
                    }
                    if (snaps != null) {
                        val list = snaps.documents.mapNotNull { parseGiveaway(it) }
                            .sortedByDescending { it.createdAtMillis }
                        callbacks?.onGiveawaysSynced(list)
                    }
                }
        }
    }

    fun detachListeners() {
        registrations.forEach { runCatching { it.remove() } }
        registrations.clear()
        attachedUid = null
    }

    suspend fun safeSignOutCloud() = withContext(NonCancellable) {
        detachListeners()
        if (isFirebaseReady()) {
            runCatching { FirebaseAuth.getInstance().signOut() }
        }
        callbacks?.onSyncStateChanged(false, "", "ক্লাউড সিঙ্ক সংযোগ বিচ্ছিন্ন")
    }

    // ==================== CLOUD WRITES ====================

    fun syncAdminGrant(userId: String, grantAdmin: Boolean, ign: String = "") {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        if (!isOwnerEmail(user)) return
        val targetFirebaseUid = playerFirebaseUidByUserId[sanitizeDocId(userId)] ?: return
        if (targetFirebaseUid.isBlank()) return
        scope.launch {
            try {
                val adminRef = db.collection(COL_ADMINS).document(sanitizeDocId(targetFirebaseUid))
                if (grantAdmin) {
                    adminRef.set(
                        mapOf(
                            "role" to "ADMIN",
                            "ign" to ign.take(50),
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                } else {
                    adminRef.delete().await()
                }
            } catch (e: Exception) {
                handleFirestoreError(e, "ADMIN_GRANT", "$COL_ADMINS/$targetFirebaseUid")
            }
        }
    }

    fun pushAppConfig(config: AppCustomization, rules: List<TournamentRuleItem>) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                val payload = hashMapOf<String, Any>(
                    "appName" to config.appName.take(80),
                    "appTagline" to config.appTagline.take(180),
                    "selectedLogo" to config.selectedLogo.name,
                    "noticeTickerText" to config.noticeTickerText.take(300),
                    "activeAnnouncement" to config.activeAnnouncement.take(300),
                    "announcementTitle" to config.announcementTitle.take(100),
                    "announcementAuthor" to config.announcementAuthor.take(60),
                    "announcementUpdatedAtMillis" to config.announcementUpdatedAtMillis.coerceAtLeast(0L),
                    "isAnnouncementUrgent" to config.isAnnouncementUrgent,
                    "isAnnouncementEnabled" to config.isAnnouncementEnabled,
                    "officialBkashNumber" to config.officialBkashNumber.take(20),
                    "officialNagadNumber" to config.officialNagadNumber.take(20),
                    "officialWhatsAppUrl" to config.officialWhatsAppUrl.take(300),
                    "minWithdraw" to config.minWithdraw,
                    "maxWithdraw" to config.maxWithdraw,
                    "isMaintenanceMode" to config.isMaintenanceMode,
                    "maintenanceMessage" to config.maintenanceMessage.take(300),
                    "strictRules" to rules.take(30).map { r ->
                        mapOf(
                            "id" to r.id,
                            "title" to r.title,
                            "description" to r.description,
                            "penalty" to r.penalty,
                            "iconEmoji" to r.iconEmoji,
                            "isStrict" to r.isStrict
                        )
                    },
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                db.collection(COL_APP_CONFIG).document(DOC_MAIN_CONFIG).set(payload).await()
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_APP_CONFIG/$DOC_MAIN_CONFIG")
            }
        }
    }

    fun seedInitialMatchesIfCloudEmpty(matches: List<TournamentMatch>, config: AppCustomization, rules: List<TournamentRuleItem>) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                val cfgSnap = db.collection(COL_APP_CONFIG).document(DOC_MAIN_CONFIG).get().await()
                if (!cfgSnap.exists()) {
                    pushAppConfig(config, rules)
                }
                val matchSnaps = db.collection(COL_MATCHES)
                    .whereIn("status", listOf("UPCOMING", "ROOM_OPEN", "ONGOING", "FINISHED", "CANCELLED"))
                    .limit(1)
                    .get()
                    .await()
                if (matchSnaps.isEmpty) {
                    matches.forEach { pushMatch(it) }
                }
            } catch (e: Exception) {
                handleFirestoreError(e, "SEED", COL_MATCHES)
            }
        }
    }

    fun pushMatch(match: TournamentMatch) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(match.id)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                val docRef = db.collection(COL_MATCHES).document(safeId)
                val fields = mutableMapOf<String, Any>(
                    "title" to match.title.take(120),
                    "category" to match.category.name,
                    "matchType" to match.matchType.name,
                    "mapName" to match.mapName.take(60),
                    "matchDateTime" to match.matchDateTime.take(80),
                    "startsInMinutes" to match.startsInMinutes.coerceIn(0, 100000),
                    "scheduledStartAtMillis" to match.effectiveStartMillis().coerceIn(0L, 9999999999999L),
                    "entryFee" to match.entryFee.coerceIn(0, 50000),
                    "totalPrize" to match.totalPrize.coerceIn(0, 500000),
                    "perKillPrize" to match.perKillPrize.coerceIn(0, 50000),
                    "booyahPrize" to match.booyahPrize.coerceIn(0, 500000),
                    "totalSlots" to match.totalSlots.coerceIn(2, 100),
                    "bookedSlots" to match.bookedSlots.coerceIn(0, match.totalSlots.coerceIn(2, 100)),
                    "status" to match.status.name,
                    "version" to match.version.take(80)
                )
                match.roomId?.takeIf { it.isNotBlank() }?.let { fields["roomId"] = it.take(60) }
                match.roomPassword?.takeIf { it.isNotBlank() }?.let { fields["roomPassword"] = it.take(60) }
                upsertWithTimestamps(docRef, fields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_MATCHES/$safeId")
            }
        }
    }

    fun pushMatchBookedCount(matchId: String, bookedSlots: Int) {
        val db = firestoreOrNull() ?: return
        currentUser() ?: return
        val safeId = sanitizeDocId(matchId)
        scope.launch {
            try {
                db.collection(COL_MATCHES).document(safeId).update(
                    mapOf(
                        "bookedSlots" to bookedSlots.coerceAtLeast(0),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, "UPDATE", "$COL_MATCHES/$safeId")
            }
        }
    }

    fun deleteMatchCloud(matchId: String) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(matchId)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                db.collection(COL_MATCHES).document(safeId).delete().await()
            } catch (e: Exception) {
                handleFirestoreError(e, "DELETE", "$COL_MATCHES/$safeId")
            }
        }
    }

    fun pushBooking(booking: SlotBooking, updatedBookedCount: Int? = null) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeBookingId = sanitizeDocId(booking.bookingId)
        val safeMatchId = sanitizeDocId(booking.matchId)
        scope.launch {
            try {
                val docRef = db.collection(COL_BOOKINGS).document(safeBookingId)
                val fields = mutableMapOf<String, Any>(
                    "bookingId" to safeBookingId,
                    "matchId" to safeMatchId,
                    "matchTitle" to booking.matchTitle.take(120),
                    "category" to booking.category.name,
                    "matchDateTime" to booking.matchDateTime.take(80),
                    "slotNumber" to booking.slotNumber.coerceIn(1, 100),
                    "playerIgn" to booking.playerIgn.ifBlank { "PLAYER" }.take(50),
                    "playerUid" to booking.playerUid.ifBlank { "000000" }.take(40),
                    "trxId" to booking.trxId.ifBlank { "WALLET-PAY" }.take(60),
                    "feePaid" to booking.feePaid.coerceIn(0, 50000),
                    "verified" to booking.isVerified,
                    "ownerUid" to user.uid
                )
                booking.roomId?.takeIf { it.isNotBlank() }?.let { fields["roomId"] = it.take(60) }
                booking.roomPassword?.takeIf { it.isNotBlank() }?.let { fields["roomPassword"] = it.take(60) }
                upsertWithTimestamps(docRef, fields)
                if (updatedBookedCount != null) {
                    db.collection(COL_MATCHES).document(safeMatchId).update(
                        mapOf(
                            "bookedSlots" to updatedBookedCount.coerceAtLeast(0),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                }
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_BOOKINGS/$safeBookingId")
            }
        }
    }

    fun deleteBookingCloud(bookingId: String, matchId: String, updatedBookedCount: Int) {
        val db = firestoreOrNull() ?: return
        currentUser() ?: return
        val safeBookingId = sanitizeDocId(bookingId)
        val safeMatchId = sanitizeDocId(matchId)
        scope.launch {
            try {
                db.collection(COL_BOOKINGS).document(safeBookingId).delete().await()
                db.collection(COL_MATCHES).document(safeMatchId).update(
                    mapOf(
                        "bookedSlots" to updatedBookedCount.coerceAtLeast(0),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, "DELETE", "$COL_BOOKINGS/$safeBookingId")
            }
        }
    }

    fun pushUser(appUser: AppUser, firebaseUidOverride: String? = null) {
        val db = firestoreOrNull() ?: return
        val authUser = currentUser() ?: return
        val safeUserId = sanitizeDocId(appUser.id)
        scope.launch {
            try {
                val targetAuthUid = (firebaseUidOverride ?: playerFirebaseUidByUserId[safeUserId] ?: authUser.uid).ifBlank { authUser.uid }
                val publicRef = db.collection(COL_PLAYER_DIRECTORY).document(safeUserId)
                val publicFields = mutableMapOf<String, Any>(
                    "firebaseUid" to targetAuthUid,
                    "name" to appUser.name.ifBlank { "Player" }.take(60),
                    "ign" to appUser.ign.ifBlank { "PLAYER" }.take(50),
                    "uid" to appUser.uid.ifBlank { "000000" }.take(40),
                    "role" to appUser.role.name,
                    "avatarId" to appUser.avatarId.take(40),
                    "bannerColorId" to appUser.bannerColorId.take(40),
                    "battleTagline" to appUser.battleTagline.take(180),
                    "favoriteWeapon" to appUser.favoriteWeapon.take(60),
                    "activeCharacter" to appUser.activeCharacter.take(60),
                    "guildName" to appUser.guildName.take(60),
                    "playerRole" to appUser.playerRole.take(60),
                    "level" to appUser.level.coerceIn(0, 500),
                    "walletBalance" to appUser.walletBalance,
                    "matchesPlayed" to appUser.matchesPlayed,
                    "totalWins" to appUser.totalWins,
                    "totalKills" to appUser.totalKills,
                    "totalEarnings" to appUser.totalEarnings,
                    "totalSpent" to appUser.totalSpent,
                    "kudosCount" to appUser.kudosCount,
                    "customBadges" to appUser.customBadges.map { it.name }.take(12),
                    "revokedBadges" to appUser.revokedBadges.map { it.name }.take(12),
                    "banned" to appUser.isBanned,
                    "warningsCount" to appUser.warningsCount.coerceIn(0, 100),
                    "moderationWarningsCount" to appUser.moderationWarningsCount.coerceIn(0, 100),
                    "chatMuted" to appUser.isChatMuted,
                    "matchBanned" to appUser.isMatchBanned,
                    "walletFrozen" to appUser.isWalletFrozen,
                    "rewardBlocked" to appUser.isRewardBlocked,
                    "appealBanned" to appUser.isAppealBanned,
                    "profileLocked" to appUser.isProfileLocked
                )
                appUser.customPfpUri?.takeIf { it.isNotBlank() && it.length <= 500 }?.let { publicFields["customPfpUri"] = it }
                appUser.customBannerUri?.takeIf { it.isNotBlank() && it.length <= 500 }?.let { publicFields["customBannerUri"] = it }
                appUser.banReason?.takeIf { it.isNotBlank() }?.let { publicFields["banReason"] = it.take(250) }
                appUser.bannedAt?.takeIf { it.isNotBlank() }?.let { publicFields["bannedAt"] = it.take(60) }
                appUser.banExpiresAtMillis?.let { publicFields["banExpiresAtMillis"] = it }
                appUser.timeoutUntilMillis?.let { publicFields["timeoutUntilMillis"] = it }
                appUser.timeoutReason?.takeIf { it.isNotBlank() }?.let { publicFields["timeoutReason"] = it.take(250) }
                appUser.timeoutBy?.takeIf { it.isNotBlank() }?.let { publicFields["timeoutBy"] = it.take(60) }
                appUser.chatMuteUntilMillis?.let { publicFields["chatMuteUntilMillis"] = it }
                appUser.chatMuteReason?.takeIf { it.isNotBlank() }?.let { publicFields["chatMuteReason"] = it.take(250) }
                appUser.matchBanReason?.takeIf { it.isNotBlank() }?.let { publicFields["matchBanReason"] = it.take(250) }
                appUser.walletFreezeReason?.takeIf { it.isNotBlank() }?.let { publicFields["walletFreezeReason"] = it.take(250) }
                appUser.rewardBlockReason?.takeIf { it.isNotBlank() }?.let { publicFields["rewardBlockReason"] = it.take(250) }
                appUser.appealBanReason?.takeIf { it.isNotBlank() }?.let { publicFields["appealBanReason"] = it.take(250) }
                appUser.profileLockReason?.takeIf { it.isNotBlank() }?.let { publicFields["profileLockReason"] = it.take(250) }

                upsertWithTimestamps(publicRef, publicFields)

                val accountRef = db.collection(COL_USER_ACCOUNTS).document(safeUserId)
                val accountFields = mutableMapOf<String, Any>(
                    "ownerUid" to targetAuthUid,
                    "profileId" to safeUserId,
                    "phone" to appUser.phone.ifBlank { "0000" }.take(30),
                    "bkashNumber" to appUser.bkashNumber.ifBlank { appUser.phone.ifBlank { "0000" } }.take(30),
                    "role" to appUser.role.name,
                    "joinedDate" to appUser.joinedDate.take(40)
                )
                upsertWithTimestamps(accountRef, accountFields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_PLAYER_DIRECTORY/$safeUserId")
            }
        }
    }

    fun pushUserBadges(appUser: AppUser) {
        val db = firestoreOrNull() ?: return
        currentUser() ?: return
        val safeUserId = sanitizeDocId(appUser.id)
        scope.launch {
            try {
                val publicRef = db.collection(COL_PLAYER_DIRECTORY).document(safeUserId)
                val snap = publicRef.get().await()
                if (snap.exists()) {
                    publicRef.update(
                        mapOf(
                            "customBadges" to appUser.customBadges.map { it.name }.take(12),
                            "revokedBadges" to appUser.revokedBadges.map { it.name }.take(12),
                            "updatedAt" to FieldValue.serverTimestamp()
                        )
                    ).await()
                } else {
                    pushUser(appUser)
                }
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_PLAYER_DIRECTORY/$safeUserId")
            }
        }
    }

    fun pushKudosUpdate(userId: String, newKudosCount: Int) {
        val db = firestoreOrNull() ?: return
        currentUser() ?: return
        val safeUserId = sanitizeDocId(userId)
        scope.launch {
            try {
                db.collection(COL_PLAYER_DIRECTORY).document(safeUserId).update(
                    mapOf(
                        "kudosCount" to newKudosCount.coerceAtLeast(0),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, "UPDATE", "$COL_PLAYER_DIRECTORY/$safeUserId")
            }
        }
    }

    fun pushDepositRequest(req: DepositRequest) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(req.id)
        scope.launch {
            try {
                val docRef = db.collection(COL_DEPOSITS).document(safeId)
                val fields = mutableMapOf<String, Any>(
                    "ownerUid" to user.uid,
                    "userPhone" to req.userPhone.take(30),
                    "userName" to req.userName.ifBlank { "Player" }.take(60),
                    "userIgn" to req.userIgn.ifBlank { "PLAYER" }.take(50),
                    "amount" to req.amount.coerceIn(5, 50000),
                    "paymentMethod" to req.paymentMethod.name,
                    "senderNumber" to req.senderNumber.ifBlank { "0000" }.take(30),
                    "trxId" to req.trxId.ifBlank { "TRX" }.take(60),
                    "status" to req.status.name,
                    "createdAtLabel" to req.createdAt.take(40),
                    "adminNote" to req.adminNote.take(200)
                )
                upsertWithTimestamps(docRef, fields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_DEPOSITS/$safeId")
            }
        }
    }

    fun pushWithdrawRequest(req: WithdrawRequest) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(req.id)
        scope.launch {
            try {
                val docRef = db.collection(COL_WITHDRAWS).document(safeId)
                val fields = mutableMapOf<String, Any>(
                    "ownerUid" to user.uid,
                    "userPhone" to req.userPhone.take(30),
                    "userName" to req.userName.ifBlank { "Player" }.take(60),
                    "userIgn" to req.userIgn.ifBlank { "PLAYER" }.take(50),
                    "amount" to req.amount.coerceIn(10, 50000),
                    "paymentMethod" to req.paymentMethod.name,
                    "targetNumber" to req.targetNumber.ifBlank { "0000" }.take(30),
                    "status" to req.status.name,
                    "createdAtLabel" to req.createdAt.take(40),
                    "adminNote" to req.adminNote.take(200)
                )
                upsertWithTimestamps(docRef, fields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_WITHDRAWS/$safeId")
            }
        }
    }

    fun pushBadgeAppeal(appeal: BadgeAppeal) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(appeal.id)
        scope.launch {
            try {
                val docRef = db.collection(COL_BADGE_APPEALS).document(safeId)
                val fields = mutableMapOf<String, Any>(
                    "ownerUid" to user.uid,
                    "userId" to appeal.userId.take(128),
                    "userName" to appeal.userName.take(60),
                    "userIgn" to appeal.userIgn.take(50),
                    "userUid" to appeal.userUid.take(40),
                    "requestedBadge" to appeal.requestedBadge.name,
                    "reasonNote" to appeal.reasonNote.ifBlank { "-" }.take(400),
                    "status" to appeal.status.name,
                    "adminNote" to appeal.adminNote.take(250),
                    "reviewedBy" to appeal.reviewedBy.take(60),
                    "createdAtLabel" to appeal.createdAt.take(40),
                    "matchesPlayedSnapshot" to appeal.matchesPlayedSnapshot,
                    "totalSpentSnapshot" to appeal.totalSpentSnapshot,
                    "totalWinsSnapshot" to appeal.totalWinsSnapshot,
                    "totalKillsSnapshot" to appeal.totalKillsSnapshot,
                    "totalEarningsSnapshot" to appeal.totalEarningsSnapshot,
                    "chatMessages" to appeal.chatMessages.take(100).map { msg ->
                        mapOf(
                            "id" to msg.id,
                            "senderName" to msg.senderName,
                            "senderRoleLabel" to msg.senderRoleLabel,
                            "isFromStaff" to msg.isFromStaff,
                            "text" to msg.text,
                            "proofImageUri" to (msg.proofImageUri ?: ""),
                            "timeAgo" to msg.timeAgo
                        )
                    }
                )
                appeal.grantedBadge?.let { fields["grantedBadge"] = it.name }
                upsertWithTimestamps(docRef, fields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_BADGE_APPEALS/$safeId")
            }
        }
    }

    fun pushGlobalChatMessage(msg: GlobalChatMessage) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(msg.id)
        scope.launch {
            try {
                val docRef = db.collection(COL_GLOBAL_CHAT).document(safeId)
                val fields = mutableMapOf<String, Any>(
                    "senderAuthUid" to user.uid,
                    "senderUserId" to msg.senderUserId.take(128),
                    "senderName" to msg.senderName.ifBlank { "Player" }.take(60),
                    "senderIgn" to msg.senderIgn.ifBlank { "PLAYER" }.take(50),
                    "senderUid" to msg.senderUid.take(40),
                    "senderRole" to msg.senderRole.name,
                    "senderAvatarId" to msg.senderAvatarId.take(40),
                    "senderPrimaryBadge" to msg.senderPrimaryBadge.name,
                    "senderBadges" to msg.senderBadges.map { it.name }.take(12),
                    "text" to msg.text.ifBlank { " " }.take(500),
                    "timestampMillis" to msg.timestampMillis.coerceAtLeast(0L),
                    "timeLabel" to msg.timeLabel.take(40),
                    "pinnedAnnouncement" to msg.isPinnedAnnouncement,
                    "reactions" to msg.reactions
                )
                msg.senderCustomPfpUri?.takeIf { it.isNotBlank() && it.length <= 500 }?.let { fields["senderCustomPfpUri"] = it }
                msg.replyToId?.takeIf { it.isNotBlank() }?.let { fields["replyToId"] = it.take(128) }
                msg.replyToIgn?.takeIf { it.isNotBlank() }?.let { fields["replyToIgn"] = it.take(50) }
                msg.replyToText?.takeIf { it.isNotBlank() }?.let { fields["replyToText"] = it.take(200) }
                upsertWithTimestamps(docRef, fields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_GLOBAL_CHAT/$safeId")
            }
        }
    }

    fun deleteGlobalChatCloud(messageId: String) {
        val db = firestoreOrNull() ?: return
        currentUser() ?: return
        val safeId = sanitizeDocId(messageId)
        scope.launch {
            try {
                db.collection(COL_GLOBAL_CHAT).document(safeId).delete().await()
            } catch (e: Exception) {
                handleFirestoreError(e, "DELETE", "$COL_GLOBAL_CHAT/$safeId")
            }
        }
    }

    fun pushModerationLog(entry: ModerationLogEntry, targetAuthUid: String = "") {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(entry.id)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                val payload = hashMapOf<String, Any>(
                    "targetAuthUid" to targetAuthUid.ifBlank { entry.targetUserId }.take(128),
                    "targetUserId" to entry.targetUserId.take(128),
                    "targetIgn" to entry.targetIgn.take(50),
                    "actionType" to entry.actionType.name,
                    "durationLabel" to entry.durationLabel.take(60),
                    "reason" to entry.reason.take(250),
                    "moderatorRole" to if (entry.moderatorRole == UserRole.OWNER) "OWNER" else "ADMIN",
                    "moderatorName" to entry.moderatorName.take(60),
                    "timestampMillis" to entry.timestampMillis.coerceAtLeast(0L),
                    "timeLabel" to entry.timeLabel.take(40),
                    "createdAt" to FieldValue.serverTimestamp()
                )
                db.collection(COL_MODERATION_LOGS).document(safeId).set(payload).await()
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_MODERATION_LOGS/$safeId")
            }
        }
    }

    fun resetCloudCollectionForFreshDownload(
        defaultMatches: List<TournamentMatch>,
        defaultConfig: AppCustomization,
        defaultRules: List<TournamentRuleItem>,
        ownerUser: AppUser
    ) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                pushAppConfig(defaultConfig, defaultRules)
                // Remove old bookings and replace matches cleanly
                val oldBookings = db.collection(COL_BOOKINGS).whereGreaterThanOrEqualTo("slotNumber", 1).get().await()
                oldBookings.documents.forEach { it.reference.delete().await() }

                val oldMatches = db.collection(COL_MATCHES)
                    .whereIn("status", listOf("UPCOMING", "ROOM_OPEN", "ONGOING", "FINISHED", "CANCELLED"))
                    .get()
                    .await()
                oldMatches.documents.forEach { it.reference.delete().await() }
                defaultMatches.forEach { pushMatch(it) }

                val oldPlayers = db.collection(COL_PLAYER_DIRECTORY).whereGreaterThanOrEqualTo("level", 0).get().await()
                oldPlayers.documents.forEach { doc ->
                    if (doc.id != sanitizeDocId(ownerUser.id)) {
                        doc.reference.delete().await()
                    }
                }
                runCatching {
                    val oldChat = db.collection(COL_GLOBAL_CHAT)
                        .whereGreaterThanOrEqualTo("timestampMillis", 0L)
                        .get()
                        .await()
                    oldChat.documents.forEach { it.reference.delete().await() }
                }
                runCatching {
                    val oldPromos = db.collection(COL_PROMO_CODES)
                        .whereGreaterThanOrEqualTo("createdAtMillis", 0L)
                        .get()
                        .await()
                    oldPromos.documents.forEach { it.reference.delete().await() }
                }
                runCatching {
                    val oldDrops = db.collection(COL_BONUS_DROPS)
                        .whereGreaterThanOrEqualTo("createdAtMillis", 0L)
                        .get()
                        .await()
                    oldDrops.documents.forEach { it.reference.delete().await() }
                }
                runCatching {
                    val oldPolls = db.collection(COL_COMMUNITY_POLLS)
                        .whereGreaterThanOrEqualTo("createdAtMillis", 0L)
                        .get()
                        .await()
                    oldPolls.documents.forEach { it.reference.delete().await() }
                }
                runCatching {
                    val oldGiveaways = db.collection(COL_GIVEAWAYS)
                        .whereGreaterThanOrEqualTo("createdAtMillis", 0L)
                        .get()
                        .await()
                    oldGiveaways.documents.forEach { it.reference.delete().await() }
                }

                pushUser(ownerUser, user.uid)
            } catch (e: Exception) {
                handleFirestoreError(e, "RESET", "global")
            }
        }
    }

    fun pushPromoCode(promo: PromoCodeItem) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(promo.id)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                val docRef = db.collection(COL_PROMO_CODES).document(safeId)
                val existingSnap = docRef.get().await()
                @Suppress("UNCHECKED_CAST")
                val existingUids = (existingSnap.get("claimedByUids") as? List<String>) ?: emptyList()
                val fields = hashMapOf<String, Any>(
                    "code" to promo.code.take(30),
                    "title" to promo.title.take(80),
                    "rewardAmount" to promo.rewardAmount.coerceIn(1, 500),
                    "maxUses" to promo.maxUses.coerceIn(1, 1000),
                    "usedCount" to promo.usedCount.coerceIn(0, promo.maxUses.coerceIn(1, 1000)),
                    "claimedByUids" to existingUids,
                    "claimedByKeys" to promo.claimedByKeys.take(1000),
                    "active" to promo.active,
                    "createdBy" to promo.createdBy.take(60),
                    "createdAtMillis" to promo.createdAtMillis.coerceAtLeast(0L)
                )
                upsertWithTimestamps(docRef, fields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_PROMO_CODES/$safeId")
            }
        }
    }

    fun claimPromoCodeCloud(promo: PromoCodeItem, claimerKey: String) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(promo.id)
        scope.launch {
            try {
                val docRef = db.collection(COL_PROMO_CODES).document(safeId)
                val snap = docRef.get().await()
                if (!snap.exists()) return@launch
                @Suppress("UNCHECKED_CAST")
                val uids = ((snap.get("claimedByUids") as? List<String>) ?: emptyList()).toMutableList()
                @Suppress("UNCHECKED_CAST")
                val keys = ((snap.get("claimedByKeys") as? List<String>) ?: emptyList()).toMutableList()
                if (user.uid in uids) return@launch
                val currentUsed = (snap.get("usedCount") as? Number)?.toInt() ?: 0
                val maxUses = (snap.get("maxUses") as? Number)?.toInt() ?: promo.maxUses
                if (currentUsed >= maxUses) return@launch
                uids.add(user.uid)
                if (claimerKey.isNotBlank() && claimerKey !in keys) keys.add(claimerKey)
                docRef.update(
                    mapOf(
                        "usedCount" to (currentUsed + 1),
                        "claimedByUids" to uids,
                        "claimedByKeys" to keys,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, "UPDATE", "$COL_PROMO_CODES/$safeId")
            }
        }
    }

    fun deletePromoCodeCloud(promoId: String) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(promoId)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                db.collection(COL_PROMO_CODES).document(safeId).delete().await()
            } catch (e: Exception) {
                handleFirestoreError(e, "DELETE", "$COL_PROMO_CODES/$safeId")
            }
        }
    }

    fun pushBonusDrop(drop: BonusGiftDrop) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(drop.id)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                val docRef = db.collection(COL_BONUS_DROPS).document(safeId)
                val existingSnap = docRef.get().await()
                @Suppress("UNCHECKED_CAST")
                val existingUids = (existingSnap.get("claimedByUids") as? List<String>) ?: emptyList()
                val fields = hashMapOf<String, Any>(
                    "title" to drop.title.take(80),
                    "subtitle" to drop.subtitle.take(160),
                    "amountPerClaim" to drop.amountPerClaim.coerceIn(1, 200),
                    "totalBoxes" to drop.totalBoxes.coerceIn(1, 500),
                    "claimedCount" to drop.claimedCount.coerceIn(0, drop.totalBoxes.coerceIn(1, 500)),
                    "claimedByUids" to existingUids,
                    "claimedByKeys" to drop.claimedByKeys.take(500),
                    "active" to drop.active,
                    "createdBy" to drop.createdBy.take(60),
                    "createdAtMillis" to drop.createdAtMillis.coerceAtLeast(0L)
                )
                upsertWithTimestamps(docRef, fields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_BONUS_DROPS/$safeId")
            }
        }
    }

    fun claimBonusDropCloud(dropId: String, claimerKey: String) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(dropId)
        scope.launch {
            try {
                val docRef = db.collection(COL_BONUS_DROPS).document(safeId)
                val snap = docRef.get().await()
                if (!snap.exists()) return@launch
                @Suppress("UNCHECKED_CAST")
                val uids = ((snap.get("claimedByUids") as? List<String>) ?: emptyList()).toMutableList()
                @Suppress("UNCHECKED_CAST")
                val keys = ((snap.get("claimedByKeys") as? List<String>) ?: emptyList()).toMutableList()
                if (user.uid in uids) return@launch
                val currentClaimed = (snap.get("claimedCount") as? Number)?.toInt() ?: 0
                val totalBoxes = (snap.get("totalBoxes") as? Number)?.toInt() ?: 1
                if (currentClaimed >= totalBoxes) return@launch
                uids.add(user.uid)
                if (claimerKey.isNotBlank() && claimerKey !in keys) keys.add(claimerKey)
                docRef.update(
                    mapOf(
                        "claimedCount" to (currentClaimed + 1),
                        "claimedByUids" to uids,
                        "claimedByKeys" to keys,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, "UPDATE", "$COL_BONUS_DROPS/$safeId")
            }
        }
    }

    fun deleteBonusDropCloud(dropId: String) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(dropId)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                db.collection(COL_BONUS_DROPS).document(safeId).delete().await()
            } catch (e: Exception) {
                handleFirestoreError(e, "DELETE", "$COL_BONUS_DROPS/$safeId")
            }
        }
    }

    fun pushCommunityPoll(poll: LiveCommunityPoll) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(poll.id)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                val docRef = db.collection(COL_COMMUNITY_POLLS).document(safeId)
                val existingSnap = docRef.get().await()
                @Suppress("UNCHECKED_CAST")
                val existingUids = (existingSnap.get("votedByUids") as? List<String>) ?: emptyList()
                val fields = hashMapOf<String, Any>(
                    "question" to poll.question.take(180),
                    "options" to poll.options.take(6).map { opt ->
                        mapOf("id" to opt.id, "label" to opt.label.take(80), "votes" to opt.votes.coerceAtLeast(0))
                    },
                    "votedByUids" to existingUids,
                    "votedByKeys" to poll.votedByKeys.take(1000),
                    "active" to poll.active,
                    "createdBy" to poll.createdBy.take(60),
                    "createdAtMillis" to poll.createdAtMillis.coerceAtLeast(0L)
                )
                upsertWithTimestamps(docRef, fields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_COMMUNITY_POLLS/$safeId")
            }
        }
    }

    fun voteCommunityPollCloud(pollId: String, optionId: String, voterKey: String) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(pollId)
        scope.launch {
            try {
                val docRef = db.collection(COL_COMMUNITY_POLLS).document(safeId)
                val snap = docRef.get().await()
                if (!snap.exists()) return@launch
                @Suppress("UNCHECKED_CAST")
                val uids = ((snap.get("votedByUids") as? List<String>) ?: emptyList()).toMutableList()
                @Suppress("UNCHECKED_CAST")
                val keys = ((snap.get("votedByKeys") as? List<String>) ?: emptyList()).toMutableList()
                if (user.uid in uids) return@launch
                @Suppress("UNCHECKED_CAST")
                val rawOptions = (snap.get("options") as? List<Map<String, Any?>>) ?: return@launch
                val updatedOptions = rawOptions.map { opt ->
                    val id = opt["id"] as? String ?: ""
                    val label = opt["label"] as? String ?: ""
                    val votes = (opt["votes"] as? Number)?.toInt() ?: 0
                    mapOf("id" to id, "label" to label, "votes" to if (id == optionId) votes + 1 else votes)
                }
                uids.add(user.uid)
                if (voterKey.isNotBlank() && voterKey !in keys) keys.add(voterKey)
                docRef.update(
                    mapOf(
                        "options" to updatedOptions,
                        "votedByUids" to uids,
                        "votedByKeys" to keys,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, "UPDATE", "$COL_COMMUNITY_POLLS/$safeId")
            }
        }
    }

    fun deleteCommunityPollCloud(pollId: String) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(pollId)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                db.collection(COL_COMMUNITY_POLLS).document(safeId).delete().await()
            } catch (e: Exception) {
                handleFirestoreError(e, "DELETE", "$COL_COMMUNITY_POLLS/$safeId")
            }
        }
    }

    fun pushGiveaway(giveaway: GiveawayEvent) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(giveaway.id)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                val docRef = db.collection(COL_GIVEAWAYS).document(safeId)
                val existingSnap = docRef.get().await()
                @Suppress("UNCHECKED_CAST")
                val existingAuthUids = (existingSnap.get("joinedAuthUids") as? List<String>) ?: emptyList()
                val mergedAuthUids = (existingAuthUids + giveaway.entries.mapNotNull { it.authUid.takeIf(String::isNotBlank) })
                    .distinct()
                    .take(giveaway.maxEntries.coerceIn(2, 500))
                val fields = hashMapOf<String, Any>(
                    "title" to giveaway.title.take(100),
                    "description" to giveaway.description.take(240),
                    "prizeType" to giveaway.prizeType.name,
                    "prizeLabel" to giveaway.prizeLabel.take(60),
                    "cashAmountPerWinner" to giveaway.cashAmountPerWinner.coerceIn(0, 5000),
                    "winnersCount" to giveaway.winnersCount.coerceIn(1, 20),
                    "maxEntries" to giveaway.maxEntries.coerceIn(2, 500),
                    "entries" to giveaway.entries.take(500).map { e ->
                        mapOf(
                            "actorKey" to e.actorKey,
                            "authUid" to e.authUid,
                            "ign" to e.ign.take(50),
                            "uid" to e.uid.take(40),
                            "joinedAtMillis" to e.joinedAtMillis.coerceAtLeast(0L)
                        )
                    },
                    "winners" to giveaway.winners.take(20).map { w ->
                        mapOf(
                            "actorKey" to w.actorKey,
                            "ign" to w.ign.take(50),
                            "uid" to w.uid.take(40),
                            "prizeLabel" to w.prizeLabel.take(60)
                        )
                    },
                    "joinedAuthUids" to mergedAuthUids,
                    "status" to giveaway.status.name,
                    "createdBy" to giveaway.createdBy.take(60),
                    "createdAtMillis" to giveaway.createdAtMillis.coerceAtLeast(0L)
                )
                upsertWithTimestamps(docRef, fields)
            } catch (e: Exception) {
                handleFirestoreError(e, "WRITE", "$COL_GIVEAWAYS/$safeId")
            }
        }
    }

    fun joinGiveawayCloud(giveawayId: String, entry: GiveawayEntry) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(giveawayId)
        scope.launch {
            try {
                val docRef = db.collection(COL_GIVEAWAYS).document(safeId)
                val snap = docRef.get().await()
                if (!snap.exists()) return@launch
                if (snap.getString("status") != GiveawayStatus.LIVE.name) return@launch
                @Suppress("UNCHECKED_CAST")
                val authUids = ((snap.get("joinedAuthUids") as? List<String>) ?: emptyList()).toMutableList()
                if (user.uid in authUids) return@launch
                @Suppress("UNCHECKED_CAST")
                val entries = ((snap.get("entries") as? List<Map<String, Any?>>) ?: emptyList()).toMutableList()
                val maxEntries = (snap.get("maxEntries") as? Number)?.toInt() ?: 50
                if (entries.size >= maxEntries) return@launch

                entries.add(
                    mapOf(
                        "actorKey" to entry.actorKey,
                        "authUid" to user.uid,
                        "ign" to entry.ign.take(50),
                        "uid" to entry.uid.take(40),
                        "joinedAtMillis" to entry.joinedAtMillis
                    )
                )
                authUids.add(user.uid)
                docRef.update(
                    mapOf(
                        "entries" to entries,
                        "joinedAuthUids" to authUids,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (e: Exception) {
                handleFirestoreError(e, "UPDATE", "$COL_GIVEAWAYS/$safeId")
            }
        }
    }

    fun deleteGiveawayCloud(giveawayId: String) {
        val db = firestoreOrNull() ?: return
        val user = currentUser() ?: return
        val safeId = sanitizeDocId(giveawayId)
        scope.launch {
            try {
                if (!isCloudStaff(db, user)) return@launch
                db.collection(COL_GIVEAWAYS).document(safeId).delete().await()
            } catch (e: Exception) {
                handleFirestoreError(e, "DELETE", "$COL_GIVEAWAYS/$safeId")
            }
        }
    }

    // ==================== PARSERS & HELPERS ====================

    private suspend fun upsertWithTimestamps(docRef: DocumentReference, fields: Map<String, Any>) {
        val now = FieldValue.serverTimestamp()
        val snap = docRef.get().await()
        if (snap.exists()) {
            docRef.update(fields + ("updatedAt" to now)).await()
        } else {
            docRef.set(fields + mapOf("createdAt" to now, "updatedAt" to now)).await()
        }
    }

    private fun sanitizeDocId(raw: String): String {
        val cleaned = raw.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
        return cleaned.ifBlank { "doc_${System.currentTimeMillis()}" }.take(120)
    }

    private fun handleFirestoreError(error: Exception, operationType: String, path: String) {
        val uid = currentUser()?.uid ?: "unauthenticated"
        Log.e(TAG, "Firestore [$operationType] failed on '$path' (uid=$uid): ${error.message}", error)
    }

    private fun parseAppConfig(snap: DocumentSnapshot): Pair<AppCustomization, List<TournamentRuleItem>?>? {
        return runCatching {
            val appName = snap.getString("appName") ?: return@runCatching null
            val logo = AppLogoOption.entries.firstOrNull { it.name == snap.getString("selectedLogo") }
                ?: AppLogoOption.TH_LOGO
            val cfg = AppCustomization(
                appName = appName,
                appTagline = snap.getString("appTagline") ?: AppCustomization().appTagline,
                selectedLogo = logo,
                noticeTickerText = snap.getString("noticeTickerText") ?: AppCustomization().noticeTickerText,
                activeAnnouncement = snap.getString("activeAnnouncement") ?: "",
                announcementTitle = snap.getString("announcementTitle") ?: AppCustomization().announcementTitle,
                announcementAuthor = snap.getString("announcementAuthor") ?: AppCustomization().announcementAuthor,
                announcementUpdatedAtMillis = (snap.get("announcementUpdatedAtMillis") as? Number)?.toLong() ?: 0L,
                isAnnouncementUrgent = snap.getBoolean("isAnnouncementUrgent") ?: false,
                isAnnouncementEnabled = snap.getBoolean("isAnnouncementEnabled") ?: true,
                officialBkashNumber = snap.getString("officialBkashNumber") ?: AppCustomization().officialBkashNumber,
                officialNagadNumber = snap.getString("officialNagadNumber") ?: AppCustomization().officialNagadNumber,
                officialWhatsAppUrl = snap.getString("officialWhatsAppUrl") ?: AppCustomization().officialWhatsAppUrl,
                minWithdraw = (snap.get("minWithdraw") as? Number)?.toInt() ?: 50,
                maxWithdraw = (snap.get("maxWithdraw") as? Number)?.toInt() ?: 200,
                isMaintenanceMode = snap.getBoolean("isMaintenanceMode") ?: false,
                maintenanceMessage = snap.getString("maintenanceMessage") ?: AppCustomization().maintenanceMessage
            )
            @Suppress("UNCHECKED_CAST")
            val rawRules = snap.get("strictRules") as? List<Map<String, Any?>>
            val parsedRules = rawRules?.mapNotNull { r ->
                val title = r["title"] as? String ?: return@mapNotNull null
                TournamentRuleItem(
                    id = (r["id"] as? String) ?: "rule_${title.hashCode()}",
                    title = title,
                    description = (r["description"] as? String).orEmpty(),
                    penalty = (r["penalty"] as? String).orEmpty(),
                    iconEmoji = (r["iconEmoji"] as? String) ?: "⚠️",
                    isStrict = (r["isStrict"] as? Boolean) ?: true
                )
            }
            cfg to parsedRules
        }.getOrNull()
    }

    private fun parseMatch(snap: DocumentSnapshot): TournamentMatch? {
        return runCatching {
            val title = snap.getString("title") ?: return@runCatching null
            val cat = GameCategory.entries.firstOrNull { it.name == snap.getString("category") }
                ?: GameCategory.BR_RANK
            val mType = MatchType.entries.firstOrNull { it.name == snap.getString("matchType") }
                ?: MatchType.SOLO
            val status = MatchStatus.entries.firstOrNull { it.name == snap.getString("status") }
                ?: MatchStatus.UPCOMING
            val startsInMinutes = (snap.get("startsInMinutes") as? Number)?.toInt() ?: 30
            val rawScheduled = (snap.get("scheduledStartAtMillis") as? Number)?.toLong() ?: 0L
            val fallbackBase = snap.getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()
            val effectiveScheduled = if (rawScheduled > 0L) {
                rawScheduled
            } else {
                fallbackBase + startsInMinutes.coerceAtLeast(0) * 60_000L
            }
            TournamentMatch(
                id = snap.id,
                title = title,
                category = cat,
                matchType = mType,
                mapName = snap.getString("mapName") ?: "বারমুডা",
                matchDateTime = snap.getString("matchDateTime") ?: "আজ",
                startsInMinutes = startsInMinutes,
                entryFee = (snap.get("entryFee") as? Number)?.toInt() ?: 20,
                totalPrize = (snap.get("totalPrize") as? Number)?.toInt() ?: 300,
                perKillPrize = (snap.get("perKillPrize") as? Number)?.toInt() ?: 10,
                booyahPrize = (snap.get("booyahPrize") as? Number)?.toInt() ?: 100,
                totalSlots = ((snap.get("totalSlots") as? Number)?.toInt() ?: 48).coerceIn(2, 100),
                bookedSlots = ((snap.get("bookedSlots") as? Number)?.toInt() ?: 0).coerceAtLeast(0),
                status = status,
                roomId = snap.getString("roomId"),
                roomPassword = snap.getString("roomPassword"),
                version = snap.getString("version") ?: "মোবাইল অনলি (No Emulator)",
                scheduledStartAtMillis = effectiveScheduled
            )
        }.getOrNull()
    }

    private fun parseBooking(snap: DocumentSnapshot): SlotBooking? {
        return runCatching {
            val matchId = snap.getString("matchId") ?: return@runCatching null
            val cat = GameCategory.entries.firstOrNull { it.name == snap.getString("category") }
                ?: GameCategory.BR_RANK
            SlotBooking(
                bookingId = snap.getString("bookingId") ?: snap.id,
                matchId = matchId,
                matchTitle = snap.getString("matchTitle").orEmpty(),
                category = cat,
                matchDateTime = snap.getString("matchDateTime").orEmpty(),
                slotNumber = (snap.get("slotNumber") as? Number)?.toInt() ?: 1,
                playerIgn = snap.getString("playerIgn") ?: "PLAYER",
                playerUid = snap.getString("playerUid") ?: "000000",
                bkashSenderNumber = "",
                trxId = snap.getString("trxId") ?: "CLOUD",
                feePaid = (snap.get("feePaid") as? Number)?.toInt() ?: 0,
                isVerified = snap.getBoolean("verified") ?: true,
                roomId = snap.getString("roomId"),
                roomPassword = snap.getString("roomPassword")
            )
        }.getOrNull()
    }

    private fun parsePlayerDirectory(snap: DocumentSnapshot): AppUser? {
        return runCatching {
            val ign = snap.getString("ign") ?: return@runCatching null
            val fbUid = snap.getString("firebaseUid").orEmpty()
            if (fbUid.isNotBlank()) {
                playerFirebaseUidByUserId[snap.id] = fbUid
            }
            val role = UserRole.entries.firstOrNull { it.name == snap.getString("role") } ?: UserRole.PLAYER
            @Suppress("UNCHECKED_CAST")
            val customBadges = (snap.get("customBadges") as? List<String>)
                ?.mapNotNull { name -> BadgeType.entries.firstOrNull { it.name == name } }
                ?.toSet() ?: emptySet()
            @Suppress("UNCHECKED_CAST")
            val revokedBadges = (snap.get("revokedBadges") as? List<String>)
                ?.mapNotNull { name -> BadgeType.entries.firstOrNull { it.name == name } }
                ?.toSet() ?: emptySet()

            AppUser(
                id = snap.id,
                name = snap.getString("name") ?: ign,
                phone = "",
                ign = ign,
                uid = snap.getString("uid") ?: "000000",
                bkashNumber = "",
                role = role,
                isBanned = snap.getBoolean("banned") ?: false,
                banReason = snap.getString("banReason"),
                bannedAt = snap.getString("bannedAt"),
                walletBalance = (snap.get("walletBalance") as? Number)?.toInt() ?: 0,
                matchesPlayed = (snap.get("matchesPlayed") as? Number)?.toInt() ?: 0,
                totalWins = (snap.get("totalWins") as? Number)?.toInt() ?: 0,
                totalKills = (snap.get("totalKills") as? Number)?.toInt() ?: 0,
                totalEarnings = (snap.get("totalEarnings") as? Number)?.toInt() ?: 0,
                totalSpent = (snap.get("totalSpent") as? Number)?.toInt() ?: 0,
                customBadges = customBadges,
                revokedBadges = revokedBadges,
                avatarId = snap.getString("avatarId") ?: "th_logo",
                customPfpUri = snap.getString("customPfpUri"),
                customBannerUri = snap.getString("customBannerUri"),
                bannerColorId = snap.getString("bannerColorId") ?: ProfileBannerColor.DEFAULT_CRIMSON.id,
                battleTagline = snap.getString("battleTagline") ?: "নতুন চ্যালেঞ্জার! 🎯",
                favoriteWeapon = snap.getString("favoriteWeapon") ?: "MP40",
                activeCharacter = snap.getString("activeCharacter") ?: "Alok",
                guildName = snap.getString("guildName") ?: "TRUSTED TH",
                playerRole = snap.getString("playerRole") ?: "সোলো প্লেয়ার",
                level = (snap.get("level") as? Number)?.toInt() ?: 1,
                kudosCount = (snap.get("kudosCount") as? Number)?.toInt() ?: 0,
                warningsCount = (snap.get("warningsCount") as? Number)?.toInt() ?: 0,
                banExpiresAtMillis = (snap.get("banExpiresAtMillis") as? Number)?.toLong(),
                moderationWarningsCount = (snap.get("moderationWarningsCount") as? Number)?.toInt() ?: 0,
                timeoutUntilMillis = (snap.get("timeoutUntilMillis") as? Number)?.toLong(),
                timeoutReason = snap.getString("timeoutReason"),
                timeoutBy = snap.getString("timeoutBy"),
                isChatMuted = snap.getBoolean("chatMuted") ?: false,
                chatMuteUntilMillis = (snap.get("chatMuteUntilMillis") as? Number)?.toLong(),
                chatMuteReason = snap.getString("chatMuteReason"),
                isMatchBanned = snap.getBoolean("matchBanned") ?: false,
                matchBanReason = snap.getString("matchBanReason"),
                isWalletFrozen = snap.getBoolean("walletFrozen") ?: false,
                walletFreezeReason = snap.getString("walletFreezeReason"),
                isRewardBlocked = snap.getBoolean("rewardBlocked") ?: false,
                rewardBlockReason = snap.getString("rewardBlockReason"),
                isAppealBanned = snap.getBoolean("appealBanned") ?: false,
                appealBanReason = snap.getString("appealBanReason"),
                isProfileLocked = snap.getBoolean("profileLocked") ?: false,
                profileLockReason = snap.getString("profileLockReason")
            )
        }.getOrNull()
    }

    private fun parseDeposit(snap: DocumentSnapshot): DepositRequest? {
        return runCatching {
            val method = WalletPaymentMethod.entries.firstOrNull { it.name == snap.getString("paymentMethod") }
                ?: WalletPaymentMethod.BKASH
            val status = RequestStatus.entries.firstOrNull { it.name == snap.getString("status") }
                ?: RequestStatus.PENDING
            DepositRequest(
                id = snap.id,
                userPhone = snap.getString("userPhone").orEmpty(),
                userName = snap.getString("userName").orEmpty(),
                userIgn = snap.getString("userIgn").orEmpty(),
                amount = (snap.get("amount") as? Number)?.toInt() ?: 0,
                paymentMethod = method,
                senderNumber = snap.getString("senderNumber").orEmpty(),
                trxId = snap.getString("trxId").orEmpty(),
                status = status,
                createdAt = snap.getString("createdAtLabel") ?: "এইমাত্র",
                adminNote = snap.getString("adminNote").orEmpty()
            )
        }.getOrNull()
    }

    private fun parseWithdraw(snap: DocumentSnapshot): WithdrawRequest? {
        return runCatching {
            val method = WalletPaymentMethod.entries.firstOrNull { it.name == snap.getString("paymentMethod") }
                ?: WalletPaymentMethod.BKASH
            val status = RequestStatus.entries.firstOrNull { it.name == snap.getString("status") }
                ?: RequestStatus.PENDING
            WithdrawRequest(
                id = snap.id,
                userPhone = snap.getString("userPhone").orEmpty(),
                userName = snap.getString("userName").orEmpty(),
                userIgn = snap.getString("userIgn").orEmpty(),
                amount = (snap.get("amount") as? Number)?.toInt() ?: 0,
                paymentMethod = method,
                targetNumber = snap.getString("targetNumber").orEmpty(),
                status = status,
                createdAt = snap.getString("createdAtLabel") ?: "এইমাত্র",
                adminNote = snap.getString("adminNote").orEmpty()
            )
        }.getOrNull()
    }

    private fun parseAppeal(snap: DocumentSnapshot): BadgeAppeal? {
        return runCatching {
            val requested = BadgeType.entries.firstOrNull { it.name == snap.getString("requestedBadge") }
                ?: BadgeType.PRO_PLAYER
            val status = AppealStatus.entries.firstOrNull { it.name == snap.getString("status") }
                ?: AppealStatus.PENDING
            val granted = snap.getString("grantedBadge")?.let { name ->
                BadgeType.entries.firstOrNull { it.name == name }
            }
            @Suppress("UNCHECKED_CAST")
            val rawChat = snap.get("chatMessages") as? List<Map<String, Any?>>
            val chat = rawChat?.mapNotNull { m ->
                val text = m["text"] as? String ?: return@mapNotNull null
                AppealChatMessage(
                    id = (m["id"] as? String) ?: "m_${text.hashCode()}",
                    senderName = (m["senderName"] as? String).orEmpty(),
                    senderRoleLabel = (m["senderRoleLabel"] as? String).orEmpty(),
                    isFromStaff = (m["isFromStaff"] as? Boolean) ?: false,
                    text = text,
                    proofImageUri = (m["proofImageUri"] as? String)?.takeIf { it.isNotBlank() },
                    timeAgo = (m["timeAgo"] as? String) ?: "এইমাত্র"
                )
            } ?: emptyList()

            BadgeAppeal(
                id = snap.id,
                userId = snap.getString("userId").orEmpty(),
                userName = snap.getString("userName").orEmpty(),
                userIgn = snap.getString("userIgn").orEmpty(),
                userUid = snap.getString("userUid").orEmpty(),
                userPhone = "",
                requestedBadge = requested,
                reasonNote = snap.getString("reasonNote").orEmpty(),
                status = status,
                grantedBadge = granted,
                adminNote = snap.getString("adminNote").orEmpty(),
                reviewedBy = snap.getString("reviewedBy").orEmpty(),
                createdAt = snap.getString("createdAtLabel") ?: "এইমাত্র",
                matchesPlayedSnapshot = (snap.get("matchesPlayedSnapshot") as? Number)?.toInt() ?: 0,
                totalSpentSnapshot = (snap.get("totalSpentSnapshot") as? Number)?.toInt() ?: 0,
                totalWinsSnapshot = (snap.get("totalWinsSnapshot") as? Number)?.toInt() ?: 0,
                totalKillsSnapshot = (snap.get("totalKillsSnapshot") as? Number)?.toInt() ?: 0,
                totalEarningsSnapshot = (snap.get("totalEarningsSnapshot") as? Number)?.toInt() ?: 0,
                chatMessages = chat
            )
        }.getOrNull()
    }

    private fun parseGlobalChat(snap: DocumentSnapshot): GlobalChatMessage? {
        return runCatching {
            val text = snap.getString("text") ?: return@runCatching null
            val role = UserRole.entries.firstOrNull { it.name == snap.getString("senderRole") }
                ?: UserRole.PLAYER
            val primaryBadge = BadgeType.entries.firstOrNull { it.name == snap.getString("senderPrimaryBadge") }
                ?: BadgeType.PRO_PLAYER
            @Suppress("UNCHECKED_CAST")
            val badges = (snap.get("senderBadges") as? List<String>)
                ?.mapNotNull { b -> BadgeType.entries.firstOrNull { it.name == b } }
                ?: emptyList()
            @Suppress("UNCHECKED_CAST")
            val rawReactions = snap.get("reactions") as? Map<String, Any?>
            val reactions = rawReactions?.mapNotNull { (k, v) ->
                val count = (v as? Number)?.toInt() ?: return@mapNotNull null
                k to count
            }?.toMap() ?: emptyMap()

            GlobalChatMessage(
                id = snap.id,
                senderUserId = snap.getString("senderUserId").orEmpty(),
                senderName = snap.getString("senderName").orEmpty(),
                senderIgn = snap.getString("senderIgn") ?: "PLAYER",
                senderUid = snap.getString("senderUid").orEmpty(),
                senderPhone = "",
                senderRole = role,
                senderAvatarId = snap.getString("senderAvatarId") ?: "th_logo",
                senderCustomPfpUri = snap.getString("senderCustomPfpUri"),
                senderPrimaryBadge = primaryBadge,
                senderBadges = badges,
                text = text,
                timestampMillis = (snap.get("timestampMillis") as? Number)?.toLong() ?: 0L,
                timeLabel = snap.getString("timeLabel") ?: "এইমাত্র",
                isPinnedAnnouncement = snap.getBoolean("pinnedAnnouncement") ?: false,
                replyToId = snap.getString("replyToId"),
                replyToIgn = snap.getString("replyToIgn"),
                replyToText = snap.getString("replyToText"),
                reactions = reactions
            )
        }.getOrNull()
    }

    private fun parseModLog(snap: DocumentSnapshot): ModerationLogEntry? {
        return runCatching {
            val action = ModerationActionType.entries.firstOrNull { it.name == snap.getString("actionType") }
                ?: ModerationActionType.WARN
            val role = UserRole.entries.firstOrNull { it.name == snap.getString("moderatorRole") }
                ?: UserRole.OWNER
            ModerationLogEntry(
                id = snap.id,
                targetUserId = snap.getString("targetUserId").orEmpty(),
                targetIgn = snap.getString("targetIgn").orEmpty(),
                targetPhone = "",
                actionType = action,
                durationLabel = snap.getString("durationLabel").orEmpty(),
                reason = snap.getString("reason").orEmpty(),
                moderatorRole = role,
                moderatorName = snap.getString("moderatorName").orEmpty(),
                timestampMillis = (snap.get("timestampMillis") as? Number)?.toLong() ?: 0L,
                timeLabel = snap.getString("timeLabel") ?: "এইমাত্র"
            )
        }.getOrNull()
    }

    private fun parsePromoCode(snap: DocumentSnapshot): PromoCodeItem? {
        return runCatching {
            val code = snap.getString("code") ?: return@runCatching null
            @Suppress("UNCHECKED_CAST")
            val claimedKeys = (snap.get("claimedByKeys") as? List<String>) ?: emptyList()
            PromoCodeItem(
                id = snap.id,
                code = code,
                title = snap.getString("title") ?: "প্রোমো কোড",
                rewardAmount = (snap.get("rewardAmount") as? Number)?.toInt() ?: 5,
                maxUses = (snap.get("maxUses") as? Number)?.toInt() ?: 25,
                usedCount = (snap.get("usedCount") as? Number)?.toInt() ?: 0,
                claimedByKeys = claimedKeys,
                active = snap.getBoolean("active") ?: true,
                createdBy = snap.getString("createdBy") ?: "👑 ওনার",
                createdAtMillis = (snap.get("createdAtMillis") as? Number)?.toLong() ?: 0L
            )
        }.getOrNull()
    }

    private fun parseBonusDrop(snap: DocumentSnapshot): BonusGiftDrop? {
        return runCatching {
            val title = snap.getString("title") ?: return@runCatching null
            @Suppress("UNCHECKED_CAST")
            val claimedKeys = (snap.get("claimedByKeys") as? List<String>) ?: emptyList()
            BonusGiftDrop(
                id = snap.id,
                title = title,
                subtitle = snap.getString("subtitle") ?: "সবার আগে ক্লেইম করে বোনাস লুটে নিন!",
                amountPerClaim = (snap.get("amountPerClaim") as? Number)?.toInt() ?: 5,
                totalBoxes = (snap.get("totalBoxes") as? Number)?.toInt() ?: 10,
                claimedCount = (snap.get("claimedCount") as? Number)?.toInt() ?: 0,
                claimedByKeys = claimedKeys,
                active = snap.getBoolean("active") ?: true,
                createdBy = snap.getString("createdBy") ?: "👑 ওনার",
                createdAtMillis = (snap.get("createdAtMillis") as? Number)?.toLong() ?: 0L
            )
        }.getOrNull()
    }

    private fun parseCommunityPoll(snap: DocumentSnapshot): LiveCommunityPoll? {
        return runCatching {
            val question = snap.getString("question") ?: return@runCatching null
            @Suppress("UNCHECKED_CAST")
            val rawOptions = snap.get("options") as? List<Map<String, Any?>> ?: emptyList()
            val options = rawOptions.mapIndexedNotNull { idx, m ->
                val label = m["label"] as? String ?: return@mapIndexedNotNull null
                PollOptionItem(
                    id = (m["id"] as? String) ?: "opt_$idx",
                    label = label,
                    votes = (m["votes"] as? Number)?.toInt() ?: 0
                )
            }
            @Suppress("UNCHECKED_CAST")
            val votedKeys = (snap.get("votedByKeys") as? List<String>) ?: emptyList()
            LiveCommunityPoll(
                id = snap.id,
                question = question,
                options = options,
                votedByKeys = votedKeys,
                active = snap.getBoolean("active") ?: true,
                createdBy = snap.getString("createdBy") ?: "👑 ওনার",
                createdAtMillis = (snap.get("createdAtMillis") as? Number)?.toLong() ?: 0L
            )
        }.getOrNull()
    }

    private fun parseGiveaway(snap: DocumentSnapshot): GiveawayEvent? {
        return runCatching {
            val title = snap.getString("title") ?: return@runCatching null
            val prizeType = GiveawayPrizeType.entries.firstOrNull { it.name == snap.getString("prizeType") }
                ?: GiveawayPrizeType.FF_DIAMONDS
            val status = GiveawayStatus.entries.firstOrNull { it.name == snap.getString("status") }
                ?: GiveawayStatus.LIVE
            @Suppress("UNCHECKED_CAST")
            val rawEntries = snap.get("entries") as? List<Map<String, Any?>> ?: emptyList()
            val entries = rawEntries.mapNotNull { m ->
                val ign = m["ign"] as? String ?: return@mapNotNull null
                GiveawayEntry(
                    actorKey = (m["actorKey"] as? String).orEmpty(),
                    authUid = (m["authUid"] as? String).orEmpty(),
                    ign = ign,
                    uid = (m["uid"] as? String).orEmpty(),
                    joinedAtMillis = (m["joinedAtMillis"] as? Number)?.toLong() ?: 0L
                )
            }
            @Suppress("UNCHECKED_CAST")
            val rawWinners = snap.get("winners") as? List<Map<String, Any?>> ?: emptyList()
            val winners = rawWinners.mapNotNull { m ->
                val ign = m["ign"] as? String ?: return@mapNotNull null
                GiveawayWinner(
                    actorKey = (m["actorKey"] as? String).orEmpty(),
                    ign = ign,
                    uid = (m["uid"] as? String).orEmpty(),
                    prizeLabel = (m["prizeLabel"] as? String).orEmpty()
                )
            }
            GiveawayEvent(
                id = snap.id,
                title = title,
                description = snap.getString("description") ?: "",
                prizeType = prizeType,
                prizeLabel = snap.getString("prizeLabel") ?: "১০০ ডায়মন্ড",
                cashAmountPerWinner = (snap.get("cashAmountPerWinner") as? Number)?.toInt() ?: 0,
                winnersCount = (snap.get("winnersCount") as? Number)?.toInt() ?: 1,
                maxEntries = (snap.get("maxEntries") as? Number)?.toInt() ?: 50,
                entries = entries,
                winners = winners,
                status = status,
                createdBy = snap.getString("createdBy") ?: "👑 ওনার",
                createdAtMillis = (snap.get("createdAtMillis") as? Number)?.toLong() ?: 0L
            )
        }.getOrNull()
    }
}
