package com.example

import com.example.data.TournamentRepository
import com.example.model.BadgeType
import com.example.model.UserProfile
import com.example.model.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
    }

    @Test
    fun testAddMoneyAndWithdrawal() {
        val initialBalance = repository.walletBalance.value
        val depositAmount = 200
        repository.addMoneyWithBkash(depositAmount, "01861583924", "TRX_TEST_123")

        assertEquals("Balance should increase by deposit", initialBalance + depositAmount, repository.walletBalance.value)
        assertTrue("Transaction history should record deposit", repository.transactions.value.any { it.trxId == "TRX_TEST_123" })

        // Below minimum 50 must fail
        val belowMinWithdraw = repository.withdrawToBkash(20, "01861583924")
        assertFalse("Withdrawal below minimum 50 must fail", belowMinWithdraw)

        // Above maximum 200 must fail
        val aboveMaxWithdraw = repository.withdrawToBkash(250, "01861583924")
        assertFalse("Withdrawal above maximum 200 must fail", aboveMaxWithdraw)

        // Withdraw valid amount (e.g. 50 taka)
        val validWithdraw = repository.withdrawToBkash(50, "01861583924")
        assertTrue("Withdrawal of 50 within balance should succeed", validWithdraw)
        assertEquals("Balance should decrease after withdrawal", initialBalance + depositAmount - 50, repository.walletBalance.value)

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
}

