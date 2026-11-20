package com.example

import com.example.data.TournamentRepository
import com.example.model.BadgeType
import com.example.model.UserProfile
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TournamentBusinessLogicTest {

    private lateinit var repository: TournamentRepository

    @Before
    fun setUp() {
        repository = TournamentRepository()
    }

    @Test
    fun testWeeklyLuckySpinGivesMax30TakaAndOnlyOncePerWeek() {
        repository.resetWeeklySpinForTesting()
        val initialBalance = repository.walletBalance.value

        val won = repository.spinWeeklyWheel()
        assertTrue("Prize should be between 3 and 30 Taka", won in 3..30)
        assertEquals("Wallet balance should increase by won amount", initialBalance + won, repository.walletBalance.value)
        assertTrue("hasSpunThisWeek should be true", repository.hasSpunThisWeek.value)

        // Second spin in the same week should be blocked (return 0)
        val secondSpin = repository.spinWeeklyWheel()
        assertEquals("Second spin in same week must be blocked and return 0", 0, secondSpin)
    }

    @Test
    fun testSpinDistributionFavorsSmallPrizesAndMakes30VeryHard() {
        var count345 = 0
        var count30 = 0
        val totalRolls = 300

        for (i in 0 until totalRolls) {
            repository.resetWeeklySpinForTesting()
            val prize = repository.spinWeeklyWheel()
            assertTrue("Prize must be one of the configured tiers", prize in listOf(3, 4, 5, 8, 30))
            if (prize in listOf(3, 4, 5)) {
                count345++
            }
            if (prize == 30) {
                count30++
            }
        }

        // Vast majority (> 85%) must be 3, 4, 5 taka
        assertTrue("3, 4, 5 taka should constitute vast majority of wins, got $count345 / $totalRolls", count345 >= 250)
        // 30 taka should be extremely rare (<= 15 out of 300)
        assertTrue("30 taka should be extremely rare, got $count30 / $totalRolls", count30 <= 15)
    }

    @Test
    fun testOwnerAuthenticationWithMasterKeyAndPin() {
        // Test with invalid key
        assertFalse("Invalid key should be rejected", repository.verifyOwnerKey("WRONG_KEY"))

        // Test with official master key
        assertTrue("Master key TH-OWNER-7788 should be verified", repository.verifyOwnerKey("TH-OWNER-7788"))
        assertTrue("Master PIN 7788 should be verified", repository.verifyOwnerKey("7788"))

        // Claim role
        val claimed = repository.claimOwnerRole("TH-OWNER-7788")
        assertTrue("Claim owner role should succeed", claimed)
        assertEquals("User role should be OWNER", UserRole.OWNER, repository.userProfile.value.role)
    }

    @Test
    fun testFraudUserBanPreventsBooking() {
        val testUserPhone = "01999887766"
        // Promote/add player
        repository.promoteToAdmin(
            targetUserOrPhone = testUserPhone,
            adminPasscode = "1234",
            customName = "Fraud Suspect",
            ign = "HackerBD",
            uid = "99887766"
        )

        // Ban the user
        repository.banUser(testUserPhone, "ভুয়া TrxID দিয়ে প্রতারণার চেষ্টা")
        val bannedUser = repository.registeredUsers.value.find { it.phone == testUserPhone }
        assertNotNull("User should exist", bannedUser)
        assertTrue("User should be marked banned", bannedUser!!.isBanned)
        assertEquals("Ban reason should match", "ভুয়া TrxID দিয়ে প্রতারণার চেষ্টা", bannedUser.banReason)

        // Unban
        repository.unbanUser(testUserPhone)
        val unbannedUser = repository.registeredUsers.value.find { it.phone == testUserPhone }
        assertFalse("User should now be unbanned", unbannedUser!!.isBanned)
    }

    @Test
    fun testAdminPromotionAndDemotion() {
        val newAdminPhone = "01755443322"
        repository.promoteToAdmin(
            targetUserOrPhone = newAdminPhone,
            adminPasscode = "ADMIN-PASS-99",
            customName = "Shakil Ahmed",
            ign = "TH_Mod_Shakil",
            uid = "33445566"
        )

        val admin = repository.registeredUsers.value.find { it.phone == newAdminPhone }
        assertNotNull("Admin should exist", admin)
        assertEquals("Role should be ADMIN", UserRole.ADMIN, admin!!.role)
        assertEquals("Passcode should match", "ADMIN-PASS-99", admin.adminPasscode)

        // Demote
        repository.demoteAdmin(newAdminPhone)
        val demoted = repository.registeredUsers.value.find { it.phone == newAdminPhone }
        assertEquals("Role should revert to PLAYER", UserRole.PLAYER, demoted!!.role)
    }

    @Test
    fun testBadgeCriteriaLogic() {
        // Top spender with 100+ earnings or 10+ matches gets V_BADGE
        val vipProfile = UserProfile(
            totalEarnings = 150,
            matchesPlayed = 12,
            role = UserRole.PLAYER
        )
        assertTrue("VIP spender should earn V_BADGE", vipProfile.earnedBadges.contains(BadgeType.V_BADGE))

        // Frequent player with 5+ matches gets PRO_PLAYER badge
        val proProfile = UserProfile(
            matchesPlayed = 8,
            totalEarnings = 20,
            role = UserRole.PLAYER
        )
        assertTrue("Player with 5+ matches should earn PRO_PLAYER badge", proProfile.earnedBadges.contains(BadgeType.PRO_PLAYER))

        // Owner gets GREEN_VERIFIED badge
        val ownerProfile = UserProfile(role = UserRole.OWNER)
        assertTrue("Owner must earn GREEN_VERIFIED badge", ownerProfile.earnedBadges.contains(BadgeType.GREEN_VERIFIED))

        // Admin gets MODERATOR badge
        val adminProfile = UserProfile(role = UserRole.ADMIN)
        assertTrue("Admin must earn MODERATOR badge", adminProfile.earnedBadges.contains(BadgeType.MODERATOR))

        // Big spender with 150+ spent gets PRIME_SPENDER badge
        val spenderProfile = UserProfile(totalSpent = 160, role = UserRole.PLAYER)
        assertTrue("Top spender should earn PRIME_SPENDER badge", spenderProfile.earnedBadges.contains(BadgeType.PRIME_SPENDER))

        // Elite stats unlock BOOYAH_MASTER, HEADSHOT_KING, KILL_MACHINE, and CHAMPION_LEGEND
        val legendProfile = UserProfile(
            totalWins = 10,
            totalKills = 55,
            headshotRate = 48,
            totalEarnings = 550,
            role = UserRole.PLAYER
        )
        assertTrue(legendProfile.earnedBadges.contains(BadgeType.BOOYAH_MASTER))
        assertTrue(legendProfile.earnedBadges.contains(BadgeType.HEADSHOT_KING))
        assertTrue(legendProfile.earnedBadges.contains(BadgeType.KILL_MACHINE))
        assertTrue(legendProfile.earnedBadges.contains(BadgeType.CHAMPION_LEGEND))
    }

    @Test
    fun testAddMoneyAndWithdrawal() {
        val initialBalance = repository.walletBalance.value
        val depositAmount = 200
        repository.addMoneyWithBkash(depositAmount, "01861583924", "TRX_TEST_123")

        assertEquals("Balance should increase by deposit", initialBalance + depositAmount, repository.walletBalance.value)
        assertTrue("Transaction history should record deposit", repository.transactions.value.any { it.trxId == "TRX_TEST_123" })

        // First withdrawal blocked while matchesPlayed == 0
        assertFalse("First withdrawal must fail when matchesPlayed == 0", repository.withdrawToBkash(50, "01861583924"))

        // Play 1 match via wallet to unlock career withdrawal
        val match = repository.matches.value.first()
        assertTrue(repository.bookSlotWithWallet(match, 5, "HeroPlayer", "12345678").first)
        assertEquals(1, repository.userProfile.value.matchesPlayed)

        // Below minimum 50 must fail
        val belowMinWithdraw = repository.withdrawToBkash(20, "01861583924")
        assertFalse("Withdrawal below minimum 50 must fail", belowMinWithdraw)

        // Above maximum 200 must fail
        val aboveMaxWithdraw = repository.withdrawToBkash(250, "01861583924")
        assertFalse("Withdrawal above maximum 200 must fail", aboveMaxWithdraw)

        // Withdraw valid amount (e.g. 50 taka)
        val validWithdraw = repository.withdrawToBkash(50, "01861583924")
        assertTrue("Withdrawal of 50 within balance should succeed", validWithdraw)
        assertEquals(
            "Balance should reflect deposit - entryFee - withdraw",
            initialBalance + depositAmount - match.entryFee - 50,
            repository.walletBalance.value
        )

        // Withdraw exceeding amount
        val failWithdraw = repository.withdrawToBkash(999999, "01861583924")
        assertFalse("Withdrawal exceeding balance must fail", failWithdraw)
    }

    @Test
    fun testLuckySpinPrizesAndDifficulty() {
        repository.resetWeeklySpinForTesting()
        val initialBal = repository.walletBalance.value

        val prize = repository.spinWeeklyWheel()
        assertTrue("Spin prize must be valid (3, 4, 5, 8, or 30)", prize in listOf(3, 4, 5, 8, 30))
        assertEquals("Balance must increase by prize amount", initialBal + prize, repository.walletBalance.value)
        assertTrue("Spin history must record won spin", repository.spinHistory.value.any { it.amount == prize })

        // Second spin in same week must be blocked (returns 0)
        val secondSpin = repository.spinWeeklyWheel()
        assertEquals("Subsequent spin in same week must return 0", 0, secondSpin)

        // Test distribution over 100 simulated spins: 3, 4, 5 must be overwhelmingly common (>90%)
        var commonPrizesCount = 0
        val sampleSize = 100
        for (i in 0 until sampleSize) {
            repository.resetWeeklySpinForTesting()
            val p = repository.spinWeeklyWheel()
            if (p in listOf(3, 4, 5)) {
                commonPrizesCount++
            }
        }
        assertTrue("3, 4, 5 taka must make up vast majority (at least 85%) of all spins", commonPrizesCount >= 85)
    }

    @Test
    fun testDailyGamerMissionsClaiming() {
        val initialBal = repository.walletBalance.value
        val mission = repository.dailyMissions.value.first()
        val missionId = mission.id

        // Cannot claim incomplete mission
        val prematureClaim = repository.claimMissionReward(missionId)
        assertFalse("Cannot claim incomplete mission", prematureClaim)

        // Complete the mission
        repository.markMissionCompleted(mission.actionType)
        val updatedMission = repository.dailyMissions.value.find { it.id == missionId }
        assertNotNull("Mission must exist", updatedMission)
        assertTrue("Mission should now be completed", updatedMission!!.isCompleted)

        // Claim reward
        val successClaim = repository.claimMissionReward(missionId)
        assertTrue("Claiming completed mission should succeed", successClaim)
        assertEquals("Wallet balance must increase by mission reward", initialBal + updatedMission.rewardAmount, repository.walletBalance.value)

        // Cannot claim again
        val doubleClaim = repository.claimMissionReward(missionId)
        assertFalse("Cannot claim already claimed mission", doubleClaim)
    }

    @Test
    fun testOwnerSlotCancellationWithAutomaticRefund() {
        val match = repository.matches.value.first()
        val initialWalletBalance = repository.walletBalance.value

        // Book a slot
        val bookingSuccess = repository.bookSlotWithBkash(
            match = match,
            slotNumber = 12,
            playerIgn = "KnightRider",
            playerUid = "9876543210",
            bkashNumber = "01811223344",
            trxId = "BKASH123456"
        )
        assertTrue("Booking must succeed", bookingSuccess)

        val bookedSlot = repository.myBookings.value.find { it.slotNumber == 12 && it.matchId == match.id }
        assertNotNull("Booked slot must exist in bookings", bookedSlot)
        val feePaid = bookedSlot!!.feePaid
        val balanceAfterBooking = repository.walletBalance.value

        // Owner cancels the booked slot
        val cancelSuccess = repository.cancelSlotBookingAndRefund(bookedSlot.bookingId)
        assertTrue("Cancelling slot by owner must return true", cancelSuccess)

        // Verify booking is removed from bookings
        val bookingAfterCancel = repository.myBookings.value.find { it.bookingId == bookedSlot.bookingId }
        assertTrue("Booking must no longer exist after cancellation", bookingAfterCancel == null)

        // Verify automatic refund was added directly to player wallet
        assertEquals(
            "Wallet balance must be refunded by feePaid",
            balanceAfterBooking + feePaid,
            repository.walletBalance.value
        )

        // Verify transaction entry with REFUND category
        val refundTx = repository.transactions.value.first()
        assertEquals("Transaction category must be REFUND", com.example.model.TransactionCategory.REFUND, refundTx.category)
        assertTrue("Refund transaction must be credit", refundTx.isCredit)
        assertEquals("Refund amount must match fee paid", feePaid, refundTx.amount)
    }

    @Test
    fun testCancelMatchAndRefundAllPlayers() {
        val match = repository.matches.value.first()
        val initialBalance = repository.walletBalance.value

        // Book two slots
        repository.bookSlotWithBkash(match, 1, "PlayerOne", "1111", "01700000001", "TRX1")
        repository.bookSlotWithBkash(match, 2, "PlayerTwo", "2222", "01700000002", "TRX2")

        val balanceAfterBookings = repository.walletBalance.value

        // Cancel entire match and refund all
        val (refundedSlots, totalRefund) = repository.cancelMatchAndRefundAll(match.id)
        assertTrue("At least 2 slots should be refunded", refundedSlots >= 2)
        assertTrue("Total refund should be greater than 0", totalRefund > 0)
        assertEquals(
            "Wallet balance must increase by total refund",
            balanceAfterBookings + totalRefund,
            repository.walletBalance.value
        )
    }

    @Test
    fun testDemoOwnerLoginSetsOwnerRoleAndLoggedIn() {
        // App starts logged out for new users
        assertFalse("App starts with isLoggedIn = false", repository.isLoggedIn.value)

        val success = repository.loginAsDemoOwner()
        assertTrue("Demo owner login should succeed", success)
        assertTrue("isLoggedIn must be true after login", repository.isLoggedIn.value)
        assertEquals("Role must be OWNER", UserRole.OWNER, repository.userProfile.value.role)
        assertEquals("Owner name must be তানভির হাসান (ওনার)", "তানভির হাসান (ওনার)", repository.userProfile.value.accountName)
    }

    @Test
    fun testDailyLoginStreakRewardsAreLowAndBudgetSafe() {
        val streakInfo = repository.dailyStreakInfo.value
        val rewards = streakInfo.rewards

        // 7 days of rewards
        assertEquals("Should have 7 days of rewards", 7, rewards.size)

        // Day 1 to 4 should be 1 Taka
        assertEquals(1, rewards[0])
        assertEquals(1, rewards[1])
        assertEquals(1, rewards[2])
        assertEquals(1, rewards[3])

        // Maximum day 7 reward should be only 3 Taka
        assertEquals(3, rewards[6])

        // Total 7-day bonus must be small (<= 12 Taka)
        val totalWeekBonus = rewards.sum()
        assertTrue("Total 7-day bonus must be low and budget-safe (<= 12 Taka), was $totalWeekBonus", totalWeekBonus <= 12)
    }

    @Test
    fun testAppBrandingUpdateByOwner() {
        assertEquals("TRUSTED TOURNAMENT BY TH", repository.appCustomization.value.appName)
        repository.updateAppBranding(
            appName = "TH PRO ESPORTS CHAMPIONSHIP",
            appTagline = "সেরা টুর্নামেন্ট • দ্রুত ক্যাশআউট",
            logo = com.example.model.AppLogoOption.TH_ESPORTS,
            noticeTicker = "নতুন রুম শুরু হবে ৭টায়!",
            announcement = "বিশাল ধামাকা অফার"
        )
        val updated = repository.appCustomization.value
        assertEquals("TH PRO ESPORTS CHAMPIONSHIP", updated.appName)
        assertEquals("সেরা টুর্নামেন্ট • দ্রুত ক্যাশআউট", updated.appTagline)
        assertEquals(com.example.model.AppLogoOption.TH_ESPORTS, updated.selectedLogo)
        assertEquals("নতুন রুম শুরু হবে ৭টায়!", updated.noticeTickerText)
    }

    @Test
    fun testStrictRulesManagement() {
        val initialCount = repository.strictRules.value.size
        assertTrue("Should start with 8 default strict rules", initialCount >= 8)

        // Add rule
        val added = repository.addStrictRule(
            title = "নো রিকয়েল স্ক্রিপ্ট নিষিদ্ধ",
            description = "স্ক্রিপ্ট ব্যবহার করলে সাথে সাথে ব্যান",
            penalty = "আজীবন বহিষ্কার",
            emoji = "🚫"
        )
        assertTrue(added)
        assertEquals(initialCount + 1, repository.strictRules.value.size)

        // Reset to default
        repository.resetDefaultRules()
        assertEquals(8, repository.strictRules.value.size)
    }

    @Test
    fun testSlotCapacityAdjustmentAndManualSlotCreation() {
        val match = repository.matches.value.first()
        val originalCapacity = match.totalSlots

        // Adjust capacity
        val updated = repository.updateMatchSlotCapacity(match.id, 50)
        assertTrue(updated)
        val updatedMatch = repository.matches.value.find { it.id == match.id }
        assertEquals(50, updatedMatch?.totalSlots)

        // Create manual VIP slot
        val manualSuccess = repository.createManualSlotBooking(
            matchId = match.id,
            slotNumber = 49,
            playerIgn = "VIP_TH_WARRIOR",
            playerUid = "1122334455",
            phoneOrBkash = "01861583924",
            feePaid = 0
        )
        assertTrue(manualSuccess)
        val bookedSlot = repository.myBookings.value.find { it.matchId == match.id && it.slotNumber == 49 }
        assertNotNull(bookedSlot)
        assertEquals("VIP_TH_WARRIOR", bookedSlot?.playerIgn)
    }

    @Test
    fun testForgotPasswordOtpFlowAndPasswordReset() {
        val ownerPhone = "01861583924"

        // 1. Unknown phone or UID returns false
        val unknownResult = repository.requestPasswordResetOtp("01000000000")
        assertFalse("Unknown user must not receive OTP", unknownResult.first)

        // 2. Registered user receives a 4-digit OTP
        val otpResult = repository.requestPasswordResetOtp(ownerPhone)
        assertTrue("Registered user must receive OTP", otpResult.first)
        val generatedOtp = otpResult.second
        assertEquals("OTP must be 4 digits", 4, generatedOtp.length)

        // 3. Wrong OTP fails verification
        assertFalse("Wrong OTP must fail verification", repository.verifyPasswordResetOtp(ownerPhone, "0000"))

        // 4. Generated OTP passes verification
        assertTrue("Generated OTP must verify successfully", repository.verifyPasswordResetOtp(ownerPhone, generatedOtp))

        // 5. Master recovery code also works if active session exists
        assertTrue("Master recovery code 7788 must verify", repository.verifyPasswordResetOtp(ownerPhone, "7788"))

        // 6. Password shorter than 4 chars must be rejected
        assertFalse("Short password must be rejected", repository.resetPassword(ownerPhone, "123"))

        // 7. Valid new password updates user password and clears session
        assertTrue("Valid new password should reset successfully", repository.resetPassword(ownerPhone, "newPass2026"))

        // 8. Old default password must now fail login
        assertFalse("Old password must fail after reset", repository.login(ownerPhone, "123456"))

        // 9. New password must succeed login
        assertTrue("New password must succeed login", repository.login(ownerPhone, "newPass2026"))
        assertTrue("isLoggedIn must be true", repository.isLoggedIn.value)
    }

    @Test
    fun testRegistrationStoresPasswordAndLoginValidatesIt() {
        val testPhone = "01712345678"
        val testUid = "5566778899"

        val registered = repository.register(
            accountName = "Rahim Uddin",
            ign = "TH_Rahim",
            uid = testUid,
            phone = testPhone,
            bkash = testPhone,
            password = "mySecret99"
        )
        assertTrue("Registration should succeed", registered)

        repository.logout()
        assertFalse("Should be logged out", repository.isLoggedIn.value)

        // Wrong password must fail
        assertFalse("Login with wrong password must fail", repository.login(testPhone, "wrongPass"))
        assertFalse("User must stay logged out", repository.isLoggedIn.value)

        // Correct password with UID or phone must succeed
        assertTrue("Login with UID and right password must succeed", repository.login(testUid, "mySecret99"))
        assertTrue("User must be logged in", repository.isLoggedIn.value)
        assertEquals("TH_Rahim", repository.userProfile.value.ign)
    }

    @Test
    fun testDynamicWithdrawLimitsAndWithdrawalCategory() {
        repository.addMoneyWithBkash(500, "01711223344", "DEP_LIMIT_1")
        val match = repository.matches.value.first()
        assertTrue(repository.bookSlotWithWallet(match, 9, "HeroLimit", "44556677").first)
        repository.updateFinancialSettings(
            bkash = "01900000001",
            nagad = "01900000002",
            whatsAppUrl = "https://chat.whatsapp.com/test",
            minWithdraw = 100,
            maxWithdraw = 400
        )

        // Amount 60 was valid under default 50..200, must now fail under updated 100..400
        assertFalse("Amount below updated minWithdraw 100 must fail", repository.withdrawToBkash(60, "01711223344"))
        // Amount 300 must now succeed under updated 100..400
        assertTrue("Amount 300 within updated limits must succeed", repository.withdrawToBkash(300, "01711223344"))
        val latestTx = repository.transactions.value.first()
        assertEquals(com.example.model.TransactionCategory.WITHDRAWAL, latestTx.category)
        assertEquals("01711223344", latestTx.senderBkash)
    }

    @Test
    fun testBookSlotRejectsDuplicateSlotCancelledMatchAndFullMatch() {
        val match = repository.matches.value.first()
        val firstBook = repository.bookSlotWithBkash(match, 20, "Hero1", "111", "01711111111", "TRX_A")
        assertTrue(firstBook)

        // Duplicate same slot must be rejected
        val duplicateBook = repository.bookSlotWithBkash(match, 20, "Hero2", "222", "01722222222", "TRX_B")
        assertFalse("Duplicate booking on same slot #20 must fail", duplicateBook)

        // Cancel match -> booking must fail
        repository.cancelMatchAndRefundAll(match.id)
        val cancelledMatch = repository.matches.value.first { it.id == match.id }
        assertFalse(
            "Booking on CANCELLED match must fail",
            repository.bookSlotWithBkash(cancelledMatch, 21, "Hero3", "333", "01733333333", "TRX_C")
        )
    }

    @Test
    fun testLegacySlotCancellationAndRefund() {
        val match = repository.matches.value.first()
        repository.updateExistingMatch(match.copy(bookedSlots = 3))
        val seededMatch = repository.matches.value.first { it.id == match.id }
        val initialBooked = seededMatch.bookedSlots
        val initialBalance = repository.walletBalance.value

        val refunded = repository.cancelLegacySlotByNumber(match.id, 1)
        assertTrue("Cancelling legacy slot #1 must succeed when bookedSlots > 0", refunded)

        val updatedMatch = repository.matches.value.first { it.id == match.id }
        assertEquals("bookedSlots must decrement by 1", initialBooked - 1, updatedMatch.bookedSlots)
        assertEquals(
            "Wallet balance must increase by match.entryFee",
            initialBalance + match.entryFee,
            repository.walletBalance.value
        )
        assertEquals(
            com.example.model.TransactionCategory.REFUND,
            repository.transactions.value.first().category
        )
    }

    @Test
    fun testProfileUpdateSyncsRegisteredUsersAndRejectsDuplicateRegistration() {
        val phone = "01699887766"
        assertTrue(repository.register("Karim Mia", "TH_Karim", "77889900", phone, phone, password = "pass1234"))
        assertFalse(
            "Duplicate registration with same phone must fail",
            repository.register("Fake Karim", "Fake", "1122", phone, phone, password = "pass9999")
        )
        assertFalse(
            "Registration with short password must fail",
            repository.register("Short Pass", "SP", "3344", "01600001111", "01600001111", password = "12")
        )

        repository.updateProfile("Karim Pro", "TH_Karim_VIP", "77889900", phone, "01699887700", "avatar_fire")
        repository.logout()
        assertTrue(repository.login(phone, "pass1234"))
        assertEquals("Karim Pro", repository.userProfile.value.accountName)
        assertEquals("TH_Karim_VIP", repository.userProfile.value.ign)
        assertEquals("01699887700", repository.userProfile.value.bkashNumber)
    }

    @Test
    fun testNewAccountRegistrationGrantsFiveTakaFreeBonus() {
        val registered = repository.register(
            accountName = "New Gamer",
            ign = "TH_Newbie",
            uid = "99001122",
            phone = "01799001122",
            bkash = "01799001122",
            password = "pass1234"
        )
        assertTrue("New account registration should succeed", registered)
        assertEquals(5, TournamentRepository.SIGNUP_WELCOME_BONUS)
        assertEquals("New account wallet balance must start with 5 Taka free bonus", 5, repository.walletBalance.value)
        val bonusTx = repository.transactions.value.firstOrNull()
        assertNotNull("Welcome bonus transaction should be recorded", bonusTx)
        assertEquals(5, bonusTx!!.amount)
        assertTrue("Welcome bonus transaction must be credit", bonusTx.isCredit)
    }

    @Test
    fun testFreshAppInitialStateHasNoFakeDemoData() {
        assertFalse(repository.isLoggedIn.value)
        assertEquals(0, repository.walletBalance.value)
        assertTrue("Leaderboard must start empty", repository.leaderboard.value.isEmpty())
        assertTrue("Bookings must start empty", repository.myBookings.value.isEmpty())
        assertTrue("Transactions must start empty", repository.transactions.value.isEmpty())
        assertTrue("Results must start empty", repository.results.value.isEmpty())
        assertTrue("Spin history must start empty", repository.spinHistory.value.isEmpty())
        assertEquals(1, repository.dailyStreakInfo.value.currentStreakDay)
        assertEquals(1, repository.registeredUsers.value.size)
        assertEquals(UserRole.OWNER, repository.registeredUsers.value.first().role)
        assertTrue("All fresh matches start with 0 booked slots", repository.matches.value.all { it.bookedSlots == 0 })
    }

    @Test
    fun testOwnerSignInWith01861583924AndCode7788GrantsOwnerRoleAndRejectsRandomPhone() {
        // Random unregistered phone cannot hijack owner using 7788
        assertFalse(
            "Unregistered random phone with 7788 must be rejected",
            repository.login("01711112222", "7788")
        )
        assertFalse(repository.isLoggedIn.value)

        // Official owner phone 01861583924 + code 7788 automatically signs in as OWNER
        assertTrue(
            "Owner phone 01861583924 with code 7788 must sign in cleanly",
            repository.login("01861583924", "7788")
        )
        assertTrue(repository.isLoggedIn.value)
        assertEquals(UserRole.OWNER, repository.userProfile.value.role)
    }

    @Test
    fun testOwnerAdminAdjustUserBalanceAddsRemovesAndPreventsNegative() {
        val phone = "01788990011"
        assertTrue(repository.register("Sajid", "TH_Sajid", "88990011", phone, phone, password = "pass123"))
        assertEquals(5, repository.walletBalance.value)

        // Add 100 taka
        val addRes = repository.adjustUserBalance(phone, 100, isAdd = true, reason = "Winner Prize", performedBy = "ওনার")
        assertTrue(addRes.first)
        assertEquals(105, repository.walletBalance.value)
        assertEquals(105, repository.registeredUsers.value.first { it.phone == phone }.walletBalance)
        assertEquals(com.example.model.TransactionCategory.ADMIN_ADJUSTMENT, repository.transactions.value.first().category)

        // Remove 40 taka
        val removeRes = repository.adjustUserBalance(phone, 40, isAdd = false, reason = "Penalty", performedBy = "অ্যাডমিন")
        assertTrue(removeRes.first)
        assertEquals(65, repository.walletBalance.value)
        assertEquals(65, repository.registeredUsers.value.first { it.phone == phone }.walletBalance)

        // Reject removing more than current balance
        val overRemove = repository.adjustUserBalance(phone, 500, isAdd = false, reason = "Too much", performedBy = "ওনার")
        assertFalse("Must reject balance removal exceeding user's wallet balance", overRemove.first)
        assertEquals(65, repository.walletBalance.value)
    }

    @Test
    fun testDepositRequestApprovalAndRejectionFlow() {
        val phone = "01733445566"
        assertTrue(repository.register("Nabil", "TH_Nabil", "33445566", phone, phone, password = "pass123"))
        assertEquals(5, repository.walletBalance.value)

        // Submit Nagad deposit request
        val depRes = repository.submitDepositRequest(
            amount = 150,
            paymentMethod = com.example.model.WalletPaymentMethod.NAGAD,
            senderNumber = phone,
            trxId = "NGD998877"
        )
        assertTrue(depRes.first)
        // Balance must remain 5 until approved
        assertEquals(5, repository.walletBalance.value)
        val pendingReq = repository.depositRequests.value.first()
        assertEquals(com.example.model.RequestStatus.PENDING, pendingReq.status)

        // Duplicate TrxID must be blocked
        assertFalse(
            repository.submitDepositRequest(100, com.example.model.WalletPaymentMethod.NAGAD, phone, "NGD998877").first
        )

        // Approve deposit request -> credits balance
        assertTrue(repository.approveDepositRequest(pendingReq.id).first)
        assertEquals(155, repository.walletBalance.value)
        assertEquals(com.example.model.RequestStatus.APPROVED, repository.depositRequests.value.first { it.id == pendingReq.id }.status)

        // Second deposit request rejected -> balance unchanged
        assertTrue(repository.submitDepositRequest(100, com.example.model.WalletPaymentMethod.BKASH, phone, "BKS112233").first)
        val secondReq = repository.depositRequests.value.first()
        assertTrue(repository.rejectDepositRequest(secondReq.id, "ভুয়া TrxID").first)
        assertEquals(155, repository.walletBalance.value)
        assertEquals(com.example.model.RequestStatus.REJECTED, repository.depositRequests.value.first { it.id == secondReq.id }.status)
    }

    @Test
    fun testWithdrawRequestFirstMatchRuleAndRejectionAutoRefund() {
        val phone = "01744556677"
        assertTrue(repository.register("Arif", "TH_Arif", "44556677", phone, phone, password = "pass123"))
        repository.adjustUserBalance(phone, 200, isAdd = true, reason = "Test fund")
        assertEquals(205, repository.walletBalance.value)
        assertEquals(0, repository.userProfile.value.matchesPlayed)

        // Blocked at 0 matches played
        val blocked = repository.submitWithdrawRequest(100, com.example.model.WalletPaymentMethod.BKASH, phone)
        assertFalse("First withdrawal must be blocked before playing 1 match", blocked.first)
        assertTrue(blocked.second.contains("১টি ম্যাচ"))

        // Play 1 match using wallet
        val match = repository.matches.value.first()
        val booked = repository.bookSlotWithWallet(match, 3, "TH_Arif", "44556677")
        assertTrue(booked.first)
        assertEquals(1, repository.userProfile.value.matchesPlayed)
        val balanceAfterMatch = repository.walletBalance.value // 205 - 10 = 195

        // Now withdrawal succeeds
        val wdrRes = repository.submitWithdrawRequest(100, com.example.model.WalletPaymentMethod.NAGAD, phone)
        assertTrue(wdrRes.first)
        assertEquals(balanceAfterMatch - 100, repository.walletBalance.value)

        // Rejecting withdrawal refunds the money automatically
        val wdrReq = repository.withdrawRequests.value.first()
        assertTrue(repository.rejectWithdrawRequest(wdrReq.id, "ভুল নগদ নম্বর").first)
        assertEquals(balanceAfterMatch, repository.walletBalance.value)
        assertEquals(com.example.model.RequestStatus.REJECTED, repository.withdrawRequests.value.first { it.id == wdrReq.id }.status)
    }

    @Test
    fun testOwnerCanGrantAndRevokeAnyBadgeIncludingAutoEarned() {
        val phone = "01799887766"
        assertTrue(repository.register("BadgeHero", "TH_BadgeHero", "77889911", phone, phone, password = "pass123"))
        assertTrue("New player starts with no badges", repository.userProfile.value.earnedBadges.isEmpty())

        // Blank or unknown user returns false
        assertFalse(repository.grantBadgeToUser("", BadgeType.V_BADGE).first)
        assertFalse(repository.grantBadgeToUser("01000000000", BadgeType.V_BADGE).first)
        assertFalse(repository.revokeBadgeFromUser(phone, BadgeType.V_BADGE).first)

        // Owner grants V_BADGE and MODERATOR
        assertTrue(repository.grantBadgeToUser(phone, BadgeType.V_BADGE).first)
        assertTrue(repository.grantBadgeToUser(phone, BadgeType.MODERATOR).first)
        assertTrue(repository.userProfile.value.earnedBadges.contains(BadgeType.V_BADGE))
        assertTrue(repository.userProfile.value.earnedBadges.contains(BadgeType.MODERATOR))
        assertTrue(repository.registeredUsers.value.first { it.phone == phone }.effectiveBadges.contains(BadgeType.V_BADGE))

        // Owner revokes V_BADGE
        assertTrue(repository.revokeBadgeFromUser(phone, BadgeType.V_BADGE).first)
        assertFalse(repository.userProfile.value.earnedBadges.contains(BadgeType.V_BADGE))
        assertTrue(repository.userProfile.value.earnedBadges.contains(BadgeType.MODERATOR))

        // Auto-earned PRO_PLAYER badge (6 matches played) can also be revoked and re-granted by Owner
        assertTrue(repository.incrementUserMatchesPlayed(phone, 6).first)
        assertTrue(repository.userProfile.value.earnedBadges.contains(BadgeType.PRO_PLAYER))

        // Revoke using case-insensitive IGN
        assertTrue(repository.revokeBadgeFromUser("th_badgehero", BadgeType.PRO_PLAYER).first)
        assertFalse(repository.userProfile.value.earnedBadges.contains(BadgeType.PRO_PLAYER))

        // Re-grant PRO_PLAYER
        assertTrue(repository.grantBadgeToUser("th_badgehero", BadgeType.PRO_PLAYER).first)
        assertTrue(repository.userProfile.value.earnedBadges.contains(BadgeType.PRO_PLAYER))
    }

    @Test
    fun testPrimeSpenderAutoBadgeOnSlotSpendingAndRefund() {
        val phone = "01711223344"
        assertTrue(repository.register("BigWhale", "TH_Whale", "99881122", phone, phone, password = "pass123"))
        repository.addMoneyWithBkash(500, phone, "TRX_WHALE_1")

        // Create a 60 Taka tournament match and book 3 slots (total 180 Taka >= 150)
        repository.createNewMatch(
            title = "VIP Whale Cup",
            category = com.example.model.GameCategory.BR_RANK,
            matchType = com.example.model.MatchType.SOLO,
            mapName = "বারমুডা",
            matchDateTime = "আজ রাত ১০:০০টা",
            entryFee = 60,
            totalPrize = 2000,
            perKill = 20,
            booyahPrize = 500,
            totalSlots = 20
        )
        val match = repository.matches.value.first { it.title == "VIP Whale Cup" }
        assertTrue(repository.bookSlotWithWallet(match, 1, "TH_Whale", "99881122").first)
        assertTrue(repository.bookSlotWithWallet(match, 2, "TH_Whale", "99881122").first)
        assertTrue(repository.bookSlotWithBkash(match, 3, "TH_Whale", "99881122", phone, "TRX_BK_WHALE"))
        val lastBookingId = repository.myBookings.value.first().bookingId

        assertTrue("totalSpent should be at least 150", repository.userProfile.value.totalSpent >= 150)
        assertTrue(
            "User spending 150+ Taka must auto-earn PRIME_SPENDER badge",
            repository.userProfile.value.earnedBadges.contains(BadgeType.PRIME_SPENDER)
        )

        // Also test Owner granting/revoking PRIME_SPENDER directly
        assertTrue(repository.revokeBadgeFromUser(phone, BadgeType.PRIME_SPENDER).first)
        assertFalse(repository.userProfile.value.earnedBadges.contains(BadgeType.PRIME_SPENDER))
        assertTrue(repository.grantBadgeToUser(phone, BadgeType.PRIME_SPENDER).first)
        assertTrue(repository.userProfile.value.earnedBadges.contains(BadgeType.PRIME_SPENDER))

        // Refund reduces totalSpent
        val spentBeforeRefund = repository.userProfile.value.totalSpent
        repository.cancelSlotBookingAndRefund(lastBookingId)
        assertEquals(spentBeforeRefund - match.entryFee, repository.userProfile.value.totalSpent)
    }

    @Test
    fun testPlayerProfileBioKudosAndRegistrySync() {
        val phone = "01722334455"
        assertTrue(repository.register("Farhan", "TH_Farhan", "44332211", phone, phone, avatarId = "avatar_wolf", password = "pass123"))

        // Update bio and profile
        repository.updateProfile(
            accountName = "Farhan Pro",
            newIgn = "TH_Farhan_OP",
            newUid = "44332211",
            newPhone = phone,
            newBkash = phone,
            newAvatarId = "avatar_cyber",
            newBio = "সোলো রাশার & শটগান কিং 🔥"
        )

        val syncedUser = repository.registeredUsers.value.first { it.phone == phone }
        assertEquals("avatar_cyber", syncedUser.avatarId)
        assertEquals("সোলো রাশার & শটগান কিং 🔥", syncedUser.battleTagline)
        assertTrue("Join date must not be blank", syncedUser.joinedDate.isNotBlank())

        // Give kudos / GG
        val kudos1 = repository.giveProfileKudos(syncedUser.id)
        val kudos2 = repository.giveProfileKudos("44332211")
        assertEquals(1, kudos1)
        assertEquals(2, kudos2)
        assertEquals(2, repository.registeredUsers.value.first { it.phone == phone }.kudosCount)
    }

    @Test
    fun testBadgeAppealAcceptGrantsSelectedBadgeAutomatically() {
        val playerPhone = "01755667788"
        assertTrue(repository.register("Mehedi", "TH_Mehedi", "33445566", playerPhone, playerPhone, password = "pass123"))
        repository.incrementUserMatchesPlayed(playerPhone, 6)

        // Player submits appeal for PRO_PLAYER badge
        val submitRes = repository.submitBadgeAppeal(
            BadgeType.PRO_PLAYER,
            "আমি ৫+ ম্যাচ সম্পন্ন করেছি, আমার ব্যাজ চাই"
        )
        assertTrue("Submitting appeal should succeed", submitRes.first)
        val appeal = repository.badgeAppeals.value.first()
        assertEquals(com.example.model.AppealStatus.PENDING, appeal.status)
        assertEquals(6, appeal.matchesPlayedSnapshot)

        // Owner/Admin reviews, clicks Accept and selects V_BADGE to grant
        val approveRes = repository.approveBadgeAppeal(
            appealId = appeal.id,
            selectedBadgeToGrant = BadgeType.V_BADGE,
            reviewerLabel = "অ্যাডমিন"
        )
        assertTrue("Approving appeal should succeed", approveRes.first)

        val updatedAppeal = repository.badgeAppeals.value.first { it.id == appeal.id }
        assertEquals(com.example.model.AppealStatus.APPROVED, updatedAppeal.status)
        assertEquals(BadgeType.V_BADGE, updatedAppeal.grantedBadge)

        // Player automatically receives selected badge
        assertTrue(
            "Player active profile must automatically receive selected V_BADGE",
            repository.userProfile.value.earnedBadges.contains(BadgeType.V_BADGE)
        )
        assertTrue(
            "Registered user record must also contain selected V_BADGE",
            repository.registeredUsers.value.first { it.phone == playerPhone }.effectiveBadges.contains(BadgeType.V_BADGE)
        )
    }

    @Test
    fun testBadgeAppealDenyIssuesWarningsAnd7DayBanAfterMoreThan3() {
        val playerPhone = "01799887766"
        assertTrue(repository.register("Sajid", "TH_Sajid", "77665544", playerPhone, playerPhone, password = "pass123"))

        val badgesForFalseAppeals = listOf(
            BadgeType.PRO_PLAYER,
            BadgeType.V_BADGE,
            BadgeType.BOOYAH_MASTER,
            BadgeType.KILL_MACHINE
        )
        val baseTime = System.currentTimeMillis()

        // Warnings 1, 2, 3 -> warning only, NOT banned yet
        for (i in 0 until 3) {
            val sub = repository.submitBadgeAppeal(badgesForFalseAppeals[i], "মিথ্যা আবেদন #${i + 1}")
            assertTrue("Appeal ${i + 1} submission should succeed before ban", sub.first)
            val appealId = repository.badgeAppeals.value.first().id
            val denyRes = repository.denyBadgeAppeal(
                appealId = appealId,
                reason = "শর্ত পূরণ হয়নি",
                reviewerLabel = "ওনার",
                nowMillis = baseTime + (i * 1000L)
            )
            assertTrue(denyRes.first)
            val user = repository.registeredUsers.value.first { it.phone == playerPhone }
            assertEquals("Warnings count must be ${i + 1}", i + 1, user.warningsCount)
            assertFalse("User must NOT be banned at ${i + 1} warnings (needs > 3)", user.isBanned)
            assertFalse("Active profile must NOT be banned at ${i + 1} warnings", repository.userProfile.value.isBanned)
        }

        // 4th warning (> 3 warnings) -> triggers automatic 7-day app ban
        val sub4 = repository.submitBadgeAppeal(badgesForFalseAppeals[3], "৪র্থ মিথ্যা আবেদন")
        assertTrue(sub4.first)
        val appeal4Id = repository.badgeAppeals.value.first().id
        val denyTime = baseTime + 5000L
        val deny4Res = repository.denyBadgeAppeal(
            appealId = appeal4Id,
            reason = "বারবার ভুয়া আবেদন",
            reviewerLabel = "ওনার",
            nowMillis = denyTime
        )
        assertTrue(deny4Res.first)

        val bannedUser = repository.registeredUsers.value.first { it.phone == playerPhone }
        assertEquals(4, bannedUser.warningsCount)
        assertTrue("User MUST be banned when warnings > 3", bannedUser.isBanned)
        assertTrue("Active profile MUST reflect 7-day ban", repository.userProfile.value.isBanned)
        assertEquals(denyTime + TournamentRepository.SEVEN_DAYS_MILLIS, bannedUser.banExpiresAtMillis)

        // Banned user cannot submit new appeal or book match slots
        val blockedAppeal = repository.submitBadgeAppeal(BadgeType.PRIME_SPENDER, "আবার আবেদন করছি")
        assertFalse("Banned user must be blocked from appealing", blockedAppeal.first)

        // Before 7 days pass -> still banned
        assertEquals(0, repository.checkAndClearExpiredBans(denyTime + TournamentRepository.SEVEN_DAYS_MILLIS - 1000L))
        assertTrue(repository.registeredUsers.value.first { it.phone == playerPhone }.isBanned)

        // After 7 days expire -> automatically unbanned
        assertEquals(1, repository.checkAndClearExpiredBans(denyTime + TournamentRepository.SEVEN_DAYS_MILLIS + 1000L))
        assertFalse(repository.registeredUsers.value.first { it.phone == playerPhone }.isBanned)
        assertFalse(repository.userProfile.value.isBanned)
    }

    @Test
    fun testBadgeAppealChatMessagesAndFreshDownloadReset() {
        val playerPhone = "01766112233"
        assertTrue(repository.register("Farabi", "TH_Farabi", "66112233", playerPhone, playerPhone, password = "pass123"))
        repository.incrementUserMatchesPlayed(playerPhone, 6)

        assertTrue(repository.submitBadgeAppeal(BadgeType.PRO_PLAYER, "প্রো ব্যাজের শর্ত পূরণ হয়েছে").first)
        val appealId = repository.badgeAppeals.value.first().id
        assertEquals(1, repository.badgeAppeals.value.first().chatMessages.size)

        // Player sends chat reply with proof screenshot URI
        val playerReply = repository.sendAppealChatMessage(
            appealId = appealId,
            text = "ম্যাচ হিস্টোরির স্ক্রিনশট দিলাম",
            proofImageUri = "content://media/picker/proof_farabi.png"
        )
        assertTrue(playerReply.first)
        val msgsAfterPlayer = repository.badgeAppeals.value.first { it.id == appealId }.chatMessages
        assertEquals(2, msgsAfterPlayer.size)
        assertFalse(msgsAfterPlayer.last().isFromStaff)
        assertEquals("content://media/picker/proof_farabi.png", msgsAfterPlayer.last().proofImageUri)

        // Owner logs in and replies in the appeal chat
        repository.logout()
        assertTrue(repository.login("01861583924", "7788"))
        val staffReply = repository.sendAppealChatMessage(
            appealId = appealId,
            text = "প্রুফ ভেরিফাইড, এখন ব্যাজ দিচ্ছি"
        )
        assertTrue(staffReply.first)
        val msgsAfterStaff = repository.badgeAppeals.value.first { it.id == appealId }.chatMessages
        assertEquals(3, msgsAfterStaff.size)
        assertTrue(msgsAfterStaff.last().isFromStaff)
        assertEquals("ওনার 👑", msgsAfterStaff.last().senderRoleLabel)

        // Fresh download reset wipes all user data, bookings, requests, leaderboard, and badge appeals
        repository.resetAppForFreshDownload()
        assertFalse(repository.isLoggedIn.value)
        assertEquals(0, repository.walletBalance.value)
        assertTrue(repository.badgeAppeals.value.isEmpty())
        assertTrue(repository.myBookings.value.isEmpty())
        assertTrue(repository.depositRequests.value.isEmpty())
        assertTrue(repository.withdrawRequests.value.isEmpty())
        assertTrue(repository.leaderboard.value.isEmpty())
    }

    @Test
    fun testThreeTierProfileMediaAndBannerPermissions() {
        // 1. Regular Player (no Prime 8): normal picture PFP allowed, GIF PFP blocked, banner & banner color locked
        val playerPhone = "01733445566"
        assertTrue(repository.register("Nabil", "TH_Nabil", "33445566", playerPhone, playerPhone, password = "pass123"))

        val gifPfpAttempt = repository.updateProfileAppearance(
            customPfpUri = "file:///data/user/0/com.example/files/profile_media/gif_pfp_1.gif",
            customBannerUri = null,
            bannerColorId = com.example.model.ProfileBannerColor.DEFAULT_CRIMSON.id
        )
        assertFalse("Regular player must be blocked from GIF PFP", gifPfpAttempt.first)

        val playerBannerPicAttempt = repository.updateProfileAppearance(
            customPfpUri = "file:///data/user/0/com.example/files/profile_media/pic_pfp_1.jpg",
            customBannerUri = "file:///data/user/0/com.example/files/profile_media/pic_banner_1.jpg",
            bannerColorId = com.example.model.ProfileBannerColor.DEFAULT_CRIMSON.id
        )
        assertFalse("Regular player without Prime 8 must be blocked from banner photo", playerBannerPicAttempt.first)

        val playerColorChangeAttempt = repository.updateProfileAppearance(
            customPfpUri = "file:///data/user/0/com.example/files/profile_media/pic_pfp_1.jpg",
            customBannerUri = null,
            bannerColorId = com.example.model.ProfileBannerColor.CYBER_CYAN.id
        )
        assertFalse("Regular player without Prime 8 cannot change app banner color", playerColorChangeAttempt.first)

        val playerNormalPfpSuccess = repository.updateProfileAppearance(
            customPfpUri = "file:///data/user/0/com.example/files/profile_media/pic_pfp_1.jpg",
            customBannerUri = null,
            bannerColorId = com.example.model.ProfileBannerColor.DEFAULT_CRIMSON.id
        )
        assertTrue("Regular player should be allowed normal static picture PFP", playerNormalPfpSuccess.first)
        assertEquals("file:///data/user/0/com.example/files/profile_media/pic_pfp_1.jpg", repository.userProfile.value.customPfpUri)
        assertNull(repository.userProfile.value.customBannerUri)

        // 2. Grant Prime 8 badge -> can customize banner with static picture or color, STILL blocked from GIFs
        assertTrue(repository.grantBadgeToUser(playerPhone, BadgeType.PRIME_SPENDER).first)

        val prime8GifBannerAttempt = repository.updateProfileAppearance(
            customPfpUri = "file:///data/user/0/com.example/files/profile_media/pic_pfp_1.jpg",
            customBannerUri = "file:///data/user/0/com.example/files/profile_media/gif_banner_9.gif",
            bannerColorId = com.example.model.ProfileBannerColor.CYBER_CYAN.id
        )
        assertFalse("Prime 8 player must STILL be blocked from GIF banner", prime8GifBannerAttempt.first)

        val prime8GifPfpAttempt = repository.updateProfileAppearance(
            customPfpUri = "file:///data/user/0/com.example/files/profile_media/gif_pfp_9.gif",
            customBannerUri = "file:///data/user/0/com.example/files/profile_media/pic_banner_2.jpg",
            bannerColorId = com.example.model.ProfileBannerColor.CYBER_CYAN.id
        )
        assertFalse("Prime 8 player must STILL be blocked from GIF PFP", prime8GifPfpAttempt.first)

        val prime8PictureBannerSuccess = repository.updateProfileAppearance(
            customPfpUri = "file:///data/user/0/com.example/files/profile_media/pic_pfp_1.jpg",
            customBannerUri = "file:///data/user/0/com.example/files/profile_media/pic_banner_2.jpg",
            bannerColorId = com.example.model.ProfileBannerColor.CYBER_CYAN.id
        )
        assertTrue("Prime 8 player should be allowed normal picture banner & banner color", prime8PictureBannerSuccess.first)
        assertEquals("file:///data/user/0/com.example/files/profile_media/pic_banner_2.jpg", repository.userProfile.value.customBannerUri)
        assertEquals(com.example.model.ProfileBannerColor.CYBER_CYAN.id, repository.userProfile.value.bannerColorId)

        // 3. Owner & Admin: Animated GIF PFP + Animated GIF Banner allowed
        repository.logout()
        assertTrue(repository.login("01861583924", "7788"))
        val ownerGifSuccess = repository.updateProfileAppearance(
            customPfpUri = "file:///data/user/0/com.example/files/profile_media/gif_pfp_owner.gif",
            customBannerUri = "file:///data/user/0/com.example/files/profile_media/gif_banner_owner.gif",
            bannerColorId = com.example.model.ProfileBannerColor.ROYAL_GOLD.id
        )
        assertTrue("Owner must be allowed animated GIF PFP & GIF Banner", ownerGifSuccess.first)
        assertEquals("file:///data/user/0/com.example/files/profile_media/gif_pfp_owner.gif", repository.userProfile.value.customPfpUri)
        assertEquals("file:///data/user/0/com.example/files/profile_media/gif_banner_owner.gif", repository.userProfile.value.customBannerUri)

        // Promote to Admin -> Admin gets GIF PFP & GIF Banner -> Demote Admin -> GIFs sanitized!
        val adminPhone = "01744556677"
        assertTrue(repository.register("AdminKai", "TH_Kai", "44556677", adminPhone, adminPhone, password = "pass123"))
        assertTrue(repository.promoteToAdmin(adminPhone, "TH-KAI", "Kai", "TH_Kai", "44556677"))
        repository.logout()
        assertTrue(repository.login(adminPhone, "pass123"))
        assertTrue(
            repository.updateProfileAppearance(
                customPfpUri = "file:///data/user/0/com.example/files/profile_media/gif_pfp_kai.gif",
                customBannerUri = "file:///data/user/0/com.example/files/profile_media/gif_banner_kai.gif",
                bannerColorId = com.example.model.ProfileBannerColor.ELECTRIC_PURPLE.id
            ).first
        )
        assertTrue(repository.demoteAdmin(adminPhone))
        val demotedKai = repository.registeredUsers.value.first { it.phone == adminPhone }
        assertNull("Demoted admin must lose GIF PFP", demotedKai.customPfpUri)
        assertNull("Demoted admin without Prime 8 must lose GIF Banner", demotedKai.customBannerUri)
        assertEquals(com.example.model.ProfileBannerColor.DEFAULT_CRIMSON.id, demotedKai.bannerColorId)
    }

    @Test
    fun testGlobalChatOwnerAdminMemberRolesRepliesPinsReactionsAndBanRestriction() {
        // Default seeded Owner announcement exists
        assertTrue(repository.globalChatMessages.value.any { it.senderRole == UserRole.OWNER && it.isPinnedAnnouncement })

        // 1. Regular Member registers and sends message
        val memberPhone = "01712340011"
        assertTrue(repository.register("Sajid", "TH_Sajid", "12340011", memberPhone, memberPhone, password = "pass123"))
        val memberSend = repository.sendGlobalChatMessage("হ্যালো সবাই! আজকের BR ম্যাচ কখন?")
        assertTrue("Member should be able to send global chat", memberSend.first)
        val memberMsg = repository.globalChatMessages.value.last()
        assertEquals(UserRole.PLAYER, memberMsg.senderRole)
        assertEquals("TH_Sajid", memberMsg.senderIgn)

        // Member cannot pin announcements
        assertFalse("Regular member must not pin messages", repository.togglePinGlobalChatMessage(memberMsg.id).first)

        // 2. Owner replies & pins official announcement
        repository.logout()
        assertTrue(repository.login("01861583924", "7788"))
        val ownerSend = repository.sendGlobalChatMessage(
            text = "রাত ৮টায় রুম আইডি দেওয়া হবে!",
            replyToId = memberMsg.id,
            pinAnnouncement = true
        )
        assertTrue(ownerSend.first)
        val ownerMsg = repository.globalChatMessages.value.last()
        assertEquals(UserRole.OWNER, ownerMsg.senderRole)
        assertEquals(memberMsg.id, ownerMsg.replyToId)
        assertEquals("TH_Sajid", ownerMsg.replyToIgn)
        assertTrue(ownerMsg.isPinnedAnnouncement)

        // 3. Reaction works
        assertTrue(repository.reactToGlobalChatMessage(ownerMsg.id, "🔥"))
        assertEquals(1, repository.globalChatMessages.value.first { it.id == ownerMsg.id }.reactions["🔥"])

        // 4. Admin sends message
        val adminPhone = "01799887700"
        assertTrue(repository.register("AdminRafi", "TH_AdminRafi", "99887700", adminPhone, adminPhone, password = "pass123"))
        assertTrue(repository.promoteToAdmin(adminPhone, "TH-RAFI", "AdminRafi", "TH_AdminRafi", "99887700"))
        repository.logout()
        assertTrue(repository.login(adminPhone, "pass123"))
        val adminSend = repository.sendGlobalChatMessage("স্লট দ্রুত ফুল হচ্ছে গাইজ!")
        assertTrue(adminSend.first)
        assertEquals(UserRole.ADMIN, repository.globalChatMessages.value.last().senderRole)

        // 5. Banned user cannot send messages in Global Chat
        repository.logout()
        assertTrue(repository.login("01861583924", "7788"))
        assertTrue(repository.banUser(memberPhone, "স্প্যাম"))
        repository.logout()
        assertTrue(repository.login(memberPhone, "pass123"))
        val bannedSend = repository.sendGlobalChatMessage("আমি কি লিখতে পারব?")
        assertFalse("Banned user must be blocked from global chat", bannedSend.first)

        // 6. Fresh reset restores clean seeded Global Chat
        repository.resetAppForFreshDownload()
        assertEquals(1, repository.globalChatMessages.value.size)
        assertEquals(UserRole.OWNER, repository.globalChatMessages.value.first().senderRole)
    }

    @Test
    fun testOwnerUidIs11410365896AndLoweredBudgetMatchPrizesPreservedAcrossReset() {
        val owner = repository.registeredUsers.value.first { it.role == UserRole.OWNER }
        assertEquals("11410365896", owner.uid)

        // All default matches must use budget-friendly lowered prizes
        val matches = repository.matches.value
        assertTrue("Initial matches must not be empty", matches.isNotEmpty())
        assertTrue("All match total prizes should be <= 100", matches.all { it.totalPrize <= 100 })
        assertTrue("All match per-kill prizes should be <= 1", matches.all { it.perKillPrize <= 1 })
        assertTrue("All match booyah prizes should be <= 40", matches.all { it.booyahPrize <= 40 })

        // Verify login as owner keeps updated UID 11410365896
        assertTrue(repository.login("01861583924", "7788"))
        assertEquals("11410365896", repository.userProfile.value.uid)

        // Reset and verify Owner UID + lowered match prizes + clean moderationLogs persist
        repository.resetAppForFreshDownload()
        val resetOwner = repository.registeredUsers.value.first { it.role == UserRole.OWNER }
        assertEquals("11410365896", resetOwner.uid)
        assertTrue(repository.matches.value.all { it.totalPrize <= 100 && it.perKillPrize <= 1 })
        assertTrue(repository.moderationLogs.value.isEmpty())
    }

    @Test
    fun testModeration7WarningsTrigger10DayAppBanAndExpireCleanly() {
        val playerPhone = "01711220011"
        assertTrue(repository.register("Tanvir", "TH_Tanvir", "11220011", playerPhone, playerPhone, password = "pass123"))
        val baseTime = 1_700_000_000_000L

        // Warnings 1 through 6 increment counter without banning
        for (i in 1..6) {
            val res = repository.warnUser(playerPhone, "সতর্কতা #$i", moderatorRole = UserRole.ADMIN, nowMillis = baseTime + i)
            assertTrue("Warn #$i should succeed", res.first)
            val target = repository.registeredUsers.value.first { it.phone == playerPhone }
            assertEquals(i, target.moderationWarningsCount)
            assertFalse("Must not ban before 7 warnings", target.isBanned)
        }

        // 7th warning automatically triggers 10-day App Ban
        val banTriggerTime = baseTime + 10_000L
        val warn7 = repository.warnUser(playerPhone, "চূড়ান্ত ৭ম ভঙ্গ", moderatorRole = UserRole.OWNER, nowMillis = banTriggerTime)
        assertTrue("7th warn should succeed", warn7.first)
        val banned = repository.registeredUsers.value.first { it.phone == playerPhone }
        assertEquals(7, banned.moderationWarningsCount)
        assertTrue("7 warnings MUST automatically ban for 10 days", banned.isBanned)
        assertEquals(banTriggerTime + TournamentRepository.TEN_DAYS_MILLIS, banned.banExpiresAtMillis)

        // Before 10 days -> still banned
        assertEquals(0, repository.checkAndClearExpiredModeration(banTriggerTime + TournamentRepository.TEN_DAYS_MILLIS - 1000L))
        assertTrue(repository.registeredUsers.value.first { it.phone == playerPhone }.isBanned)

        // After 10 days -> automatically unbanned and moderation warn count resets to 0
        assertEquals(1, repository.checkAndClearExpiredModeration(banTriggerTime + TournamentRepository.TEN_DAYS_MILLIS + 1000L))
        val unbanned = repository.registeredUsers.value.first { it.phone == playerPhone }
        assertFalse(unbanned.isBanned)
        assertEquals(0, unbanned.moderationWarningsCount)
    }

    @Test
    fun testModerationTimeoutChatMuteMatchBanWalletFreezeBadgeStripAndPardonAll() {
        val playerPhone = "01755009911"
        val adminPhone = "01755009922"
        assertTrue(repository.register("Hasan", "TH_Hasan", "55009911", playerPhone, playerPhone, password = "pass123"))
        assertTrue(repository.register("ModKai", "TH_ModKai", "55009922", adminPhone, adminPhone, password = "pass123"))
        assertTrue(repository.promoteToAdmin(adminPhone, "TH-KAI", "ModKai", "TH_ModKai", "55009922"))

        // Log in back as Hasan so active profile checks apply to Hasan
        repository.logout()
        assertTrue(repository.login(playerPhone, "pass123"))
        repository.adjustUserBalance(playerPhone, 200, isAdd = true, reason = "Test")

        val t0 = System.currentTimeMillis()
        // 1. Custom 90-minute timeout blocks chat, slot booking, deposit, withdraw & daily streak
        val timeoutRes = repository.timeoutUser(playerPhone, 90L, "স্প্যাম ও অসদাচরণ", moderatorRole = UserRole.ADMIN, nowMillis = t0)
        assertTrue(timeoutRes.first)
        assertTrue(repository.isActiveUserTimedOut(t0 + 1000L))
        assertFalse("Timed-out user cannot send global chat", repository.sendGlobalChatMessage("হ্যালো").first)
        assertFalse("Timed-out user cannot book slot", repository.bookSlotWithWallet(repository.matches.value.first(), 2, "TH_Hasan", "55009911").first)
        assertFalse("Timed-out user cannot deposit", repository.submitDepositRequest(100, com.example.model.WalletPaymentMethod.BKASH, playerPhone, "BKS778899").first)
        assertEquals("Timed-out user cannot claim streak", 0, repository.claimDailyStreakReward())

        // Timeout clears after 90 minutes expire
        assertEquals(1, repository.checkAndClearExpiredModeration(t0 + 90L * 60_000L + 1000L))
        assertNull(repository.userProfile.value.timeoutUntilMillis)

        // 2. Chat Mute blocks global chat, Unmute restores it
        assertTrue(repository.muteUserChat(playerPhone, 6L, "গালাগালি", moderatorRole = UserRole.ADMIN).first)
        assertFalse("Muted user blocked from chat", repository.sendGlobalChatMessage("টেস্ট মেসেজ").first)
        assertTrue(repository.unmuteUserChat(playerPhone, moderatorRole = UserRole.ADMIN).first)
        assertTrue("Unmuted user can chat", repository.sendGlobalChatMessage("এখন মেসেজ যায়").first)

        // 3. Match Slot Ban blocks booking, unban restores it
        assertTrue(repository.setUserMatchBan(playerPhone, true, "স্লট নষ্ট করা", moderatorRole = UserRole.OWNER).first)
        assertFalse("Match-banned user blocked from booking", repository.bookSlotWithWallet(repository.matches.value.first(), 4, "TH_Hasan", "55009911").first)
        assertTrue(repository.setUserMatchBan(playerPhone, false, moderatorRole = UserRole.OWNER).first)
        assertTrue("Match booking works after unban", repository.bookSlotWithWallet(repository.matches.value.first(), 4, "TH_Hasan", "55009911").first)

        // 4. Wallet Freeze blocks deposit & withdraw
        assertTrue(repository.setUserWalletFreeze(playerPhone, true, "ফ্রড তদন্ত", moderatorRole = UserRole.OWNER).first)
        assertFalse("Wallet-frozen user blocked from deposit", repository.submitDepositRequest(100, com.example.model.WalletPaymentMethod.NAGAD, playerPhone, "NGD654321").first)
        assertFalse("Wallet-frozen user blocked from withdraw", repository.submitWithdrawRequest(50, com.example.model.WalletPaymentMethod.BKASH, playerPhone).first)

        // 5. Grant badges, Strip All Badges, then Pardon All punishments
        assertTrue(repository.grantBadgeToUser(playerPhone, BadgeType.V_BADGE).first)
        assertTrue(repository.grantBadgeToUser(playerPhone, BadgeType.HEADSHOT_KING).first)
        assertTrue(repository.removeAllBadgesFromUser(playerPhone, moderatorRole = UserRole.OWNER).first)
        assertTrue(repository.userProfile.value.earnedBadges.isEmpty())

        assertTrue(repository.pardonAllPunishments(playerPhone, moderatorRole = UserRole.OWNER).first)
        val pardoned = repository.userProfile.value
        assertFalse(pardoned.isBanned)
        assertFalse(pardoned.isChatMuted)
        assertFalse(pardoned.isMatchBanned)
        assertFalse(pardoned.isWalletFrozen)
        assertNull(pardoned.timeoutUntilMillis)
        assertEquals(0, pardoned.moderationWarningsCount)

        // 6. Staff Hierarchy Protection: Admin CANNOT moderate Owner or another Admin
        assertFalse("Admin must not warn Owner", repository.warnUser("01861583924", "test", moderatorRole = UserRole.ADMIN).first)
        assertFalse("Admin must not timeout another Admin", repository.timeoutUser(adminPhone, 10L, "test", moderatorRole = UserRole.ADMIN).first)
        assertTrue("Owner CAN timeout an Admin", repository.timeoutUser(adminPhone, 10L, "শৃঙ্খলা ভঙ্গ", moderatorRole = UserRole.OWNER).first)
    }

    @Test
    fun testLiveAppAnnouncementPublishClearAndLiveLeaderboardAutoRanking() {
        // 1. Regular players cannot publish announcements
        val p1 = "01711112222"
        val p2 = "01733334444"
        val adminPhone = "01755556666"
        assertTrue(repository.register("PlayerOne", "TH_One", "11112222", p1, p1, password = "pass123"))
        assertFalse("Regular player must not publish app announcement", repository.publishAppAnnouncement("Hi", "Test").first)

        // Fresh state has empty leaderboard (no fake players)
        assertTrue(repository.leaderboard.value.isEmpty())

        // 2. Register second player & admin, promote admin
        assertTrue(repository.register("PlayerTwo", "TH_Two", "33334444", p2, p2, password = "pass123"))
        assertTrue(repository.register("AdminThree", "TH_Mod", "55556666", adminPhone, adminPhone, password = "pass123"))
        assertTrue(repository.promoteToAdmin(adminPhone, "TH-MOD", "AdminThree", "TH_Mod", "55556666"))

        // Admin publishes urgent announcement
        repository.logout()
        assertTrue(repository.login(adminPhone, "pass123"))
        val pubAdmin = repository.publishAppAnnouncement("জরুরি ম্যাচ", "রুম পাসওয়ার্ড ৭৭৮৮", isUrgent = true)
        assertTrue(pubAdmin.first)
        assertTrue(repository.appCustomization.value.isAnnouncementEnabled)
        assertTrue(repository.appCustomization.value.isAnnouncementUrgent)
        assertEquals("রুম পাসওয়ার্ড ৭৭৮৮", repository.appCustomization.value.activeAnnouncement)

        // Owner clears announcement
        repository.logout()
        assertTrue(repository.login("01861583924", "7788"))
        assertTrue(repository.clearAppAnnouncement().first)
        assertFalse(repository.appCustomization.value.isAnnouncementEnabled)
        assertEquals("", repository.appCustomization.value.activeAnnouncement)

        // Owner publishes match result & payout for PlayerOne -> populates Leaderboard Rank #1
        val targetMatch = repository.matches.value.first()
        val res1 = repository.publishMatchResultAndPayout(
            matchId = targetMatch.id,
            winnerTargetKey = p1,
            winnerKills = 4,
            customPrizeAmount = 40,
            runnerUpIgn = "TH_Two",
            markMatchFinished = true
        )
        assertTrue(res1.first)
        assertEquals(1, repository.leaderboard.value.size)
        assertEquals("TH_One", repository.leaderboard.value.first().ign)
        assertEquals(40, repository.leaderboard.value.first().totalEarnings)
        assertEquals(com.example.model.MatchStatus.FINISHED, repository.matches.value.first { it.id == targetMatch.id }.status)

        // Owner boosts PlayerTwo with higher earnings -> PlayerTwo becomes Rank #1 automatically!
        val res2 = repository.awardKillPrizeAndStats(
            targetKey = p2,
            killsToAdd = 8,
            winsToAdd = 2,
            matchesToAdd = 1,
            prizeAmount = 95,
            note = "মেগা কিল বোনাস"
        )
        assertTrue(res2.first)
        assertEquals(2, repository.leaderboard.value.size)
        assertEquals("TH_Two", repository.leaderboard.value[0].ign)
        assertEquals(1, repository.leaderboard.value[0].rank)
        assertEquals("TH_One", repository.leaderboard.value[1].ign)
        assertEquals(2, repository.leaderboard.value[1].rank)

        // Banning PlayerTwo removes them from the live leaderboard; Unbanning restores them!
        assertTrue(repository.banUser(p2, "হ্যাক"))
        assertEquals(1, repository.leaderboard.value.size)
        assertEquals("TH_One", repository.leaderboard.value.first().ign)
        assertTrue(repository.unbanUser(p2))
        assertEquals(2, repository.leaderboard.value.size)
        assertEquals("TH_Two", repository.leaderboard.value.first().ign)

        // Fresh download reset wipes results & leaderboard cleanly
        repository.resetAppForFreshDownload()
        assertTrue(repository.leaderboard.value.isEmpty())
        assertTrue(repository.results.value.isEmpty())
        assertTrue(repository.appCustomization.value.isAnnouncementEnabled)
    }
}




