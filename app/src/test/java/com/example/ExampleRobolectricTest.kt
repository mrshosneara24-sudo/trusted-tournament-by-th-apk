package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import com.example.data.TournamentRepository
import com.example.ui.MainTournamentApp
import com.example.ui.TournamentTab
import com.example.ui.TournamentViewModel
import com.example.ui.theme.TrustedTournamentTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w412dp-h915dp")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("TRUSTED TOURNAMENT BY TH", appName)
    }

    @Test
    fun `logged out state switches to sign in and opens forgot password dialog`() {
        val viewModel = TournamentViewModel(TournamentRepository())
        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Verify 5 Taka free welcome bonus copy on registration tab
        composeTestRule.onNodeWithText("অ্যাকাউন্ট খুললেই পাচ্ছেন ফ্রি ৳৫ ওয়েলকাম বোনাস!").assertIsDisplayed()
        composeTestRule.onNodeWithTag("auth_submit_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("🚀 অ্যাকাউন্ট খুলুন (৳৫ ফ্রি বোনাস)").assertIsDisplayed()

        // Verify 1-tap demo owner login shortcut is completely removed
        composeTestRule.onNodeWithTag("demo_owner_login_btn").assertDoesNotExist()

        // Switch to Sign In tab so forgot_password_btn is rendered
        composeTestRule.onNodeWithText("সাইন ইন").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("forgot_password_btn").performScrollTo().assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("forgot_password_dialog").assertIsDisplayed()
    }

    @Test
    fun `owner login with 01861583924 and 7788 renders matches missions maintenance banner empty leaderboard and withdraw dialog`() {
        val repo = TournamentRepository()
        val viewModel = TournamentViewModel(repo)

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Owner signs in via 01861583924 + 7788
        assertTrue(viewModel.login("01861583924", "7788"))
        composeTestRule.waitForIdle()

        // Owner TopAppBar crown button is visible for OWNER
        composeTestRule.onNodeWithTag("appbar_owner_btn").assertIsDisplayed()

        // Main dashboard renders with matches & daily missions
        composeTestRule.onNodeWithTag("matches_screen_list").assertIsDisplayed()
        composeTestRule.onNodeWithTag("matches_screen_list").performScrollToIndex(2)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("mission_card_m1").assertIsDisplayed()

        // Empty leaderboard state displays cleanly on fresh app
        viewModel.selectTab(TournamentTab.LEADERBOARD)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("leaderboard_empty_state").assertIsDisplayed()

        // Toggle maintenance mode and verify Pink-Black maintenance banner shows live
        viewModel.toggleMaintenanceMode(true, "সার্ভার আপডেট চলছে")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("maintenance_banner").assertIsDisplayed()

        // Update withdraw limits dynamically and verify WithdrawDialog pre-fills updated minimum
        viewModel.updateFinancialSettings("01861583924", "01861583924", "https://chat.whatsapp.com/test", 80, 350)
        viewModel.selectTab(TournamentTab.WALLET)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("wallet_withdraw_btn").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("withdraw_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithText("লিমিট: ৳80 - ৳350").assertIsDisplayed()
    }

    @Test
    fun `viewmodel reactive sync updates slot capacity bkash number and missions`() {
        val repo = TournamentRepository()
        val vm = TournamentViewModel(repo)
        val match = vm.allMatches.value.first()

        vm.openSlotGrid(match)
        composeTestRule.waitForIdle()
        assertEquals(match.totalSlots, vm.roomSlotGridMatch.value?.totalSlots)

        vm.updateMatchSlotCapacity(match.id, 52)
        composeTestRule.waitForIdle()
        assertEquals(52, vm.roomSlotGridMatch.value?.totalSlots)

        vm.updateFinancialSettings("01955555555", "01966666666", "https://wa.me/test", 60, 300)
        assertEquals("01955555555", vm.officialBkashNumber)

        vm.sendAiMessage("রুম আইডি কখন পাবো?")
        val aiMission = vm.dailyMissions.value.find { it.actionType == "AI_CHAT" }
        assertNotNull(aiMission)
        assertTrue("AI_CHAT mission should auto-complete on sending message", aiMission!!.isCompleted)

        val matchMission = vm.dailyMissions.value.find { it.actionType == "MATCH" }
        assertNotNull(matchMission)
        assertTrue("MATCH mission should complete on viewing slot grid", matchMission!!.isCompleted)
    }

    @Test
    fun `new player registration grants 5 taka hides owner bar and opens pro sensitivity lab`() {
        val repo = TournamentRepository()
        val viewModel = TournamentViewModel(repo)

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Register a fresh regular player
        assertTrue(viewModel.register("Rashed", "TH_Rashed", "55443322", "01755443322", "01755443322", "avatar_fire", "pass123"))
        composeTestRule.waitForIdle()

        // Regular player must NOT see Owner/Admin toolbar buttons
        composeTestRule.onNodeWithTag("appbar_owner_btn").assertDoesNotExist()
        composeTestRule.onNodeWithTag("appbar_admin_btn").assertDoesNotExist()
        assertEquals(5, viewModel.walletBalance.value)

        // Scroll to item index 3 where Sensitivity Pro Lab card sits
        composeTestRule.onNodeWithTag("matches_screen_list").performScrollToIndex(3)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("sensitivity_lab_card").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("sensitivity_pro_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("sens_ram_0").performClick()
        composeTestRule.onNodeWithTag("sens_style_1").performClick()
        composeTestRule.onNodeWithTag("copy_sensitivity_btn").assertIsDisplayed().performClick()
    }

    @Test
    fun `persistent login balance and bookings survive repository restart`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val firstSessionRepo = TournamentRepository(context)
        assertTrue(firstSessionRepo.register("Hasan", "TH_Hasan", "77665544", "01777665544", "01777665544", password = "pass999"))
        assertTrue(firstSessionRepo.adjustUserBalance("01777665544", 100, isAdd = true, reason = "Owner gift").first)
        val match = firstSessionRepo.matches.value.first()
        assertTrue(firstSessionRepo.bookSlotWithWallet(match, 4, "TH_Hasan", "77665544").first)
        val expectedBalance = firstSessionRepo.walletBalance.value

        // Simulate closing and reopening the app by creating a brand-new TournamentRepository(context)
        val restartedRepo = TournamentRepository(context)
        assertTrue("Signed-in session must persist across app restart", restartedRepo.isLoggedIn.value)
        assertEquals("01777665544", restartedRepo.userProfile.value.phone)
        assertEquals("Hasan", restartedRepo.userProfile.value.accountName)
        assertEquals(expectedBalance, restartedRepo.walletBalance.value)
        assertEquals(1, restartedRepo.userProfile.value.matchesPlayed)
        assertEquals(1, restartedRepo.myBookings.value.size)

        // Logging out and logging back in still remembers the user & updated balance
        restartedRepo.logout()
        val afterLogoutRepo = TournamentRepository(context)
        assertFalse(afterLogoutRepo.isLoggedIn.value)
        assertTrue(afterLogoutRepo.login("01777665544", "pass999"))
        assertEquals(expectedBalance, afterLogoutRepo.walletBalance.value)
    }

    @Test
    fun `wallet screen shows deposit withdraw guide and locks first withdraw until 1 match played`() {
        val repo = TournamentRepository()
        val viewModel = TournamentViewModel(repo)
        assertTrue(viewModel.register("Rana", "TH_Rana", "11220099", "01711220099", "01711220099", avatarId = "th_logo", password = "pass123"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        viewModel.selectTab(TournamentTab.WALLET)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("wallet_withdraw_eligibility_pill").assertIsDisplayed()
        composeTestRule.onNodeWithTag("wallet_system_guide_card").assertIsDisplayed()

        composeTestRule.onNodeWithTag("wallet_withdraw_btn").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("withdraw_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("withdraw_career_rule_banner").assertIsDisplayed()
    }

    @Test
    fun `settings dialog displays deposit and withdraw request section and allows owner approval`() {
        val repo = TournamentRepository()
        val viewModel = TournamentViewModel(repo)

        // Player submits a deposit request first
        assertTrue(viewModel.register("Karim", "TH_Karim", "33445566", "01733445566", "01733445566", avatarId = "th_logo", password = "pass123"))
        assertTrue(viewModel.submitDepositRequest(100, com.example.model.WalletPaymentMethod.BKASH, "01733445566", "TRX998811"))
        val depositReqId = viewModel.depositRequests.value.first().id

        // Owner logs in via 01861583924 + 7788
        viewModel.logout()
        assertTrue(viewModel.login("01861583924", "7788"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Open Settings dialog from header
        composeTestRule.onNodeWithTag("appbar_settings_btn").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("settings_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_deposit_withdraw_section").assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_deposit_request_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("settings_withdraw_request_btn").performScrollTo().assertIsDisplayed()

        // Owner sees approval queue automatically when pending requests exist
        composeTestRule.onNodeWithTag("settings_admin_requests_queue").assertIsDisplayed()
        composeTestRule.onNodeWithTag("approve_deposit_btn_$depositReqId").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        val updatedKarim = repo.registeredUsers.value.find { it.phone == "01733445566" }
        assertNotNull(updatedKarim)
        assertEquals(105, updatedKarim!!.walletBalance)

        // Switch to My Requests tab inside Settings
        composeTestRule.onNodeWithTag("settings_my_requests_tab").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("settings_my_requests_list").assertIsDisplayed()
    }

    @Test
    fun `fresh download reset clears persisted session bookings requests and leaderboard`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        assertTrue(viewModel.register("Nabil", "TH_Nabil", "88990011", "01788990011", "01788990011", avatarId = "th_logo", password = "pass123"))
        assertTrue(viewModel.submitDepositRequest(50, com.example.model.WalletPaymentMethod.NAGAD, "01788990011", "TRX554433"))
        assertTrue(viewModel.isLoggedIn.value)
        assertEquals(5, viewModel.walletBalance.value)

        viewModel.resetAppForFreshDownload()

        assertFalse(viewModel.isLoggedIn.value)
        assertEquals(0, viewModel.walletBalance.value)
        assertTrue(viewModel.myBookings.value.isEmpty())
        assertTrue(viewModel.transactions.value.isEmpty())
        assertTrue(viewModel.depositRequests.value.isEmpty())
        assertTrue(viewModel.withdrawRequests.value.isEmpty())
        assertTrue(viewModel.leaderboard.value.isEmpty())
        assertTrue(viewModel.spinHistory.value.isEmpty())
        assertEquals(1, viewModel.registeredUsers.value.size)

        // Verify persisted store was also cleared on restart
        val freshRepo = TournamentRepository(context)
        assertFalse(freshRepo.isLoggedIn.value)
        assertEquals(0, freshRepo.walletBalance.value)
        assertTrue(freshRepo.depositRequests.value.isEmpty())
    }

    @Test
    fun `owner panel badge control tab grants revokes and persists badges across restart`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        // Register a player, then sign in as Owner
        assertTrue(viewModel.register("Siam", "TH_Siam", "66554433", "01766554433", "01766554433", "th_logo", "pass123"))
        viewModel.logout()
        assertTrue(viewModel.login("01861583924", "7788"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Open Owner Control Panel and switch to Badge Control tab
        viewModel.openOwnerPanel()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("owner_control_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("owner_tab_badges").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("owner_badge_manage_section").assertIsDisplayed()
        composeTestRule.onNodeWithTag("owner_badge_quick_chip_01766554433").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("owner_badge_selected_user_card").assertIsDisplayed()

        // Grant V_BADGE via UI button
        composeTestRule.onNodeWithTag("owner_badge_manage_section")
            .performScrollToNode(hasTestTag("grant_badge_btn_V_BADGE"))
        composeTestRule.onNodeWithTag("grant_badge_btn_V_BADGE").performClick()
        composeTestRule.waitForIdle()
        assertTrue(
            repo.registeredUsers.value.first { it.phone == "01766554433" }.effectiveBadges
                .contains(com.example.model.BadgeType.V_BADGE)
        )

        // Revoke V_BADGE via UI button
        composeTestRule.onNodeWithTag("owner_badge_manage_section")
            .performScrollToNode(hasTestTag("revoke_badge_btn_V_BADGE"))
        composeTestRule.onNodeWithTag("revoke_badge_btn_V_BADGE").performClick()
        composeTestRule.waitForIdle()
        assertFalse(
            repo.registeredUsers.value.first { it.phone == "01766554433" }.effectiveBadges
                .contains(com.example.model.BadgeType.V_BADGE)
        )

        // Grant BOOYAH_MASTER and verify persistence across repository restart
        composeTestRule.onNodeWithTag("owner_badge_manage_section")
            .performScrollToNode(hasTestTag("grant_badge_btn_BOOYAH_MASTER"))
        composeTestRule.onNodeWithTag("grant_badge_btn_BOOYAH_MASTER").performClick()
        composeTestRule.waitForIdle()

        // Grant PRIME_SPENDER (highest spender emblem) via UI button
        composeTestRule.onNodeWithTag("owner_badge_manage_section")
            .performScrollToNode(hasTestTag("grant_badge_btn_PRIME_SPENDER"))
        composeTestRule.onNodeWithTag("grant_badge_btn_PRIME_SPENDER").performClick()
        composeTestRule.waitForIdle()

        // Verify custom badge drawable mappings for all 5 custom logos
        assertEquals(R.drawable.badge_v_logo, com.example.ui.components.getBadgeDrawableRes(com.example.model.BadgeType.V_BADGE))
        assertEquals(R.drawable.badge_pro_logo, com.example.ui.components.getBadgeDrawableRes(com.example.model.BadgeType.PRO_PLAYER))
        assertEquals(R.drawable.badge_spender_logo, com.example.ui.components.getBadgeDrawableRes(com.example.model.BadgeType.PRIME_SPENDER))
        assertEquals(R.drawable.badge_admin_logo, com.example.ui.components.getBadgeDrawableRes(com.example.model.BadgeType.MODERATOR))
        assertEquals(R.drawable.badge_owner_logo, com.example.ui.components.getBadgeDrawableRes(com.example.model.BadgeType.GREEN_VERIFIED))

        val restartedRepo = TournamentRepository(context)
        val persistedSiam = restartedRepo.registeredUsers.value.first { it.phone == "01766554433" }
        assertTrue(persistedSiam.effectiveBadges.contains(com.example.model.BadgeType.BOOYAH_MASTER))
        assertTrue(persistedSiam.effectiveBadges.contains(com.example.model.BadgeType.PRIME_SPENDER))
        assertFalse(persistedSiam.effectiveBadges.contains(com.example.model.BadgeType.V_BADGE))

        assertTrue(restartedRepo.login("01766554433", "pass123"))
        assertTrue(restartedRepo.userProfile.value.earnedBadges.contains(com.example.model.BadgeType.BOOYAH_MASTER))
        assertTrue(restartedRepo.userProfile.value.earnedBadges.contains(com.example.model.BadgeType.PRIME_SPENDER))
    }

    @Test
    fun `clickable player profile cards and animated profile badges work across screens`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        // Register a player and give them matches, spending, and custom bio
        assertTrue(viewModel.register("Rafi", "TH_Rafi", "11229988", "01711229988", "01711229988", "avatar_cyber", "pass123"))
        viewModel.updateBio("ওয়ান-ট্যাপ লেজেন্ড ও টুর্নামেন্ট চ্যাম্পিয়ন 🔥")
        viewModel.incrementUserMatchesPlayed("01711229988", 6)

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // 1. Account screen shows interactive animated profile badge strip & own profile card preview button
        viewModel.selectTab(TournamentTab.ACCOUNT)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("profile_animated_badges_strip").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_animated_badge_pro_player").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        // Open own profile card from Account card
        composeTestRule.onNodeWithTag("account_screen")
            .performScrollToNode(hasTestTag("ff_esports_id_card"))
        composeTestRule.onNodeWithTag("open_own_player_profile_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("player_profile_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_profile_bio_card").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_profile_joined_date").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_profile_stat_matches").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_profile_stat_spent").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("ওয়ান-ট্যাপ লেজেন্ড ও টুর্নামেন্ট চ্যাম্পিয়ন 🔥").performScrollTo().assertIsDisplayed()

        // Close modal
        composeTestRule.onNodeWithTag("player_profile_close_btn").performClick()
        composeTestRule.waitForIdle()

        // 2. Leaderboard screen lets anyone click Owner TH★DEVIL⚡ or any player in Community Directory
        viewModel.selectTab(TournamentTab.LEADERBOARD)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("leaderboard_screen")
            .performScrollToNode(hasTestTag("directory_player_card_2489103847"))
        composeTestRule.onNodeWithTag("directory_player_card_2489103847").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("player_profile_dialog").assertIsDisplayed()
        composeTestRule.onNode(
            hasText("TH★DEVIL⚡") and hasAnyAncestor(hasTestTag("player_profile_dialog"))
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_profile_give_kudos_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertEquals(13, repo.registeredUsers.value.first { it.uid == "2489103847" }.kudosCount)
    }

    @Test
    fun `player badge appeal accept selects badge for player and deny over 3 warnings triggers 7 day ban`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        // Register a player and simulate completed matches
        assertTrue(viewModel.register("Rony", "TH_Rony", "90908080", "01790908080", "01790908080", "th_logo", "pass123"))
        viewModel.incrementUserMatchesPlayed("01790908080", 6)

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // 1. Player opens Badges Showcase -> clicks "Appeal for Badge" -> submits application
        viewModel.selectTab(TournamentTab.ACCOUNT)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("account_view_all_badges_chip").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("badge_showcase_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("appeal_for_badge_btn").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("badge_appeal_form_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("badge_appeal_reason_input").performTextReplacement("আমি ৬টি ম্যাচ খেলেছি, প্রো ব্যাজ দিন")
        composeTestRule.onNodeWithTag("submit_badge_appeal_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        val firstAppealId = repo.badgeAppeals.value.firstOrNull()?.id
        assertNotNull("Submitted badge appeal must exist in repository", firstAppealId)

        // 2. Owner logs in, opens Owner panel -> Badges tab -> accepts appeal and selects V_BADGE
        viewModel.logout()
        assertTrue(viewModel.login("01861583924", "7788"))
        viewModel.openOwnerPanel()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("owner_tab_badges").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("owner_badge_manage_section")
            .performScrollToNode(hasTestTag("accept_badge_appeal_btn_$firstAppealId"))
        composeTestRule.onNodeWithTag("accept_badge_appeal_btn_$firstAppealId").performClick()
        composeTestRule.waitForIdle()

        // Select V_BADGE inside the badge picker dialog and confirm
        composeTestRule.onNodeWithTag("select_grant_badge_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("grant_appeal_badge_option_V_BADGE").performClick()
        composeTestRule.onNodeWithTag("confirm_grant_selected_badge_btn").performClick()
        composeTestRule.waitForIdle()

        // Selected badge is automatically delivered to player & persisted
        val ronyAfterAccept = repo.registeredUsers.value.first { it.phone == "01790908080" }
        assertTrue(ronyAfterAccept.effectiveBadges.contains(com.example.model.BadgeType.V_BADGE))

        // 3. Player sends repeated false appeals -> Admin/Owner denies 4 times -> 7-day ban triggers on > 3 warnings
        viewModel.closeOwnerPanel()
        viewModel.logout()
        assertTrue(viewModel.login("01790908080", "pass123"))

        val badBadges = listOf(
            com.example.model.BadgeType.PRIME_SPENDER,
            com.example.model.BadgeType.BOOYAH_MASTER,
            com.example.model.BadgeType.HEADSHOT_KING,
            com.example.model.BadgeType.KILL_MACHINE
        )
        badBadges.forEachIndexed { idx, badge ->
            assertTrue(repo.submitBadgeAppeal(badge, "অসম্পূর্ণ আবেদন #${idx + 1}").first)
            val id = repo.badgeAppeals.value.first().id
            assertTrue(repo.denyBadgeAppeal(id, "শর্ত পূরণ হয়নি", "অ্যাডমিন").first)
        }

        val bannedRony = repo.registeredUsers.value.first { it.phone == "01790908080" }
        assertEquals(4, bannedRony.warningsCount)
        assertTrue("4 warnings (>3) must ban user for 7 days", bannedRony.isBanned)
        assertNotNull(bannedRony.banExpiresAtMillis)

        // Restart repository and verify persistence of granted badge, appeal history, warnings & 7-day ban
        val restarted = TournamentRepository(context)
        val persistedRony = restarted.registeredUsers.value.first { it.phone == "01790908080" }
        assertTrue(persistedRony.effectiveBadges.contains(com.example.model.BadgeType.V_BADGE))
        assertEquals(4, persistedRony.warningsCount)
        assertTrue(persistedRony.isBanned)
        assertEquals(5, restarted.badgeAppeals.value.size)

        // 4. Fresh download reset clears appeals as well
        viewModel.resetAppForFreshDownload()
        assertTrue(viewModel.badgeAppeals.value.isEmpty())
        assertFalse(viewModel.isLoggedIn.value)
        assertEquals(0, viewModel.walletBalance.value)
    }

    @Test
    fun `owner uses chat with user to verify proof and clicks sticky top accept button to grant badge`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        assertTrue(viewModel.register("Tariq", "TH_Tariq", "70706060", "01770706060", "01770706060", "th_logo", "pass123"))
        viewModel.incrementUserMatchesPlayed("01770706060", 6)
        assertTrue(viewModel.submitBadgeAppeal(com.example.model.BadgeType.PRO_PLAYER, "আমার ৬টি ম্যাচ শেষ").first)
        val appealId = repo.badgeAppeals.value.first().id

        // Player sends proof reply in chat before owner checks
        assertTrue(viewModel.sendAppealChatMessage(appealId, "আমার প্রোফাইল প্রুফ দেখুন", "content://media/proof_tariq.jpg").first)

        // Owner signs in and opens Owner Panel -> Badges tab
        viewModel.logout()
        assertTrue(viewModel.login("01861583924", "7788"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        viewModel.openOwnerPanel()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("owner_tab_badges").performClick()
        composeTestRule.waitForIdle()

        // Owner clicks "Chat with User" button on the appeal card
        composeTestRule.onNodeWithTag("owner_badge_manage_section")
            .performScrollToNode(hasTestTag("chat_with_user_btn_$appealId"))
        composeTestRule.onNodeWithTag("chat_with_user_btn_$appealId").performClick()
        composeTestRule.waitForIdle()

        // Chat dialog & sticky TOP Accept/Deny buttons must be displayed
        composeTestRule.onNodeWithTag("badge_appeal_chat_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("appeal_chat_top_actions").assertIsDisplayed()
        composeTestRule.onNodeWithTag("appeal_chat_top_accept_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("appeal_chat_top_deny_btn").assertIsDisplayed()
        composeTestRule.onNodeWithText("আমার প্রোফাইল প্রুফ দেখুন").assertIsDisplayed()

        // Owner asks/replies in chat
        composeTestRule.onNodeWithTag("appeal_chat_input").performTextReplacement("প্রুফ ঠিক আছে, এখন ব্যাজ দিচ্ছি")
        composeTestRule.onNodeWithTag("appeal_chat_send_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("প্রুফ ঠিক আছে, এখন ব্যাজ দিচ্ছি").assertIsDisplayed()

        // Owner taps TOP Accept button directly inside the chat dialog -> picks PRIME_SPENDER -> confirms
        composeTestRule.onNodeWithTag("appeal_chat_top_accept_btn").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("select_grant_badge_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("grant_appeal_badge_option_PRIME_SPENDER").performClick()
        composeTestRule.onNodeWithTag("confirm_grant_selected_badge_btn").performClick()
        composeTestRule.waitForIdle()

        val updatedTariq = repo.registeredUsers.value.first { it.phone == "01770706060" }
        assertTrue(updatedTariq.effectiveBadges.contains(com.example.model.BadgeType.PRIME_SPENDER))
        assertEquals(com.example.model.AppealStatus.APPROVED, repo.badgeAppeals.value.first { it.id == appealId }.status)
    }

    @Test
    fun `edit account dialog enforces 3 tier pfp and banner permissions for player prime8 and owner`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        // 1. Regular Player (no Prime 8): normal picture PFP only + banner locked notice
        assertTrue(viewModel.register("Rakin", "TH_Rakin", "81819191", "01781819191", "01781819191", "th_logo", "pass123"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        viewModel.openEditProfile()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("edit_account_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("pick_normal_pfp_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_banner_locked_notice").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("pick_gif_pfp_btn").assertDoesNotExist()
        composeTestRule.onNodeWithTag("pick_gif_banner_btn").assertDoesNotExist()
        composeTestRule.onNodeWithTag("pick_picture_banner_btn").assertDoesNotExist()

        viewModel.closeEditProfile()
        composeTestRule.waitForIdle()

        // 2. Grant Prime 8 badge -> Picture Banner + Color Selector unlocked, still no GIF buttons
        assertTrue(repo.grantBadgeToUser("01781819191", com.example.model.BadgeType.PRIME_SPENDER).first)
        viewModel.openEditProfile()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("pick_normal_pfp_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("pick_picture_banner_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("banner_color_option_cyber_cyan").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("pick_gif_pfp_btn").assertDoesNotExist()
        composeTestRule.onNodeWithTag("pick_gif_banner_btn").assertDoesNotExist()

        viewModel.closeEditProfile()
        composeTestRule.waitForIdle()

        // 3. Owner login -> Animated GIF PFP & Animated GIF Banner unlocked
        viewModel.logout()
        assertTrue(viewModel.login("01861583924", "7788"))
        viewModel.openEditProfile()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("pick_gif_pfp_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("pick_gif_banner_btn").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `global chat tab next to leaderboard lets owner admin and members talk reply pin and view profile cards`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        assertTrue(viewModel.register("Nabil", "TH_Nabil", "70701010", "01770701010", "01770701010", "avatar_cyber", "pass123"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Both Leaderboard and Global Chat bottom tabs exist side-by-side
        composeTestRule.onNodeWithTag("tab_leaderboard").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_global_chat").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("global_chat_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("global_chat_pinned_banner").assertIsDisplayed()

        // Member sends a message via UI
        composeTestRule.onNodeWithTag("global_chat_input").performTextReplacement("সবাই রেডি তো?")
        composeTestRule.onNodeWithTag("global_chat_send_btn").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        assertTrue(repo.globalChatMessages.value.any { it.text == "সবাই রেডি তো?" })
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("global_chat_list")) and hasText("সবাই রেডি তো?")
        ).assertIsDisplayed()

        // Owner logs in and sends a pinned announcement in Global Chat
        viewModel.logout()
        assertTrue(viewModel.login("01861583924", "7788"))
        viewModel.selectTab(TournamentTab.GLOBAL_CHAT)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("global_chat_pin_toggle").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("global_chat_input").performTextReplacement("ম্যাচ ১৫ মিনিটে শুরু হবে!")
        composeTestRule.onNodeWithTag("global_chat_send_btn").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        val lastMsg = repo.globalChatMessages.value.last()
        assertTrue(lastMsg.isPinnedAnnouncement)
        assertEquals("ম্যাচ ১৫ মিনিটে শুরু হবে!", lastMsg.text)

        // Clicking sender opens PlayerProfileCardDialog
        composeTestRule.onNodeWithTag("global_chat_sender_${lastMsg.id}").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("player_profile_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_profile_close_btn").performClick()
        composeTestRule.waitForIdle()

        // Persistence across restart keeps Global Chat messages
        val restartedRepo = TournamentRepository(context)
        assertTrue(restartedRepo.globalChatMessages.value.any { it.text == "সবাই রেডি তো?" })
        assertTrue(restartedRepo.globalChatMessages.value.any { it.text == "ম্যাচ ১৫ মিনিটে শুরু হবে!" })
    }
}



