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
import com.example.model.ChatChannel
import com.example.ui.MainTournamentApp
import com.example.ui.TournamentTab
import com.example.ui.TournamentViewModel
import com.example.ui.screens.AccountScreen
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
        composeTestRule.onNodeWithText("অ্যাকাউন্ট খুলুন ও ইমেইল ভেরিফাই করুন").assertIsDisplayed()

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
    fun `owner login renders match slots directly without quests shows fun giveaway tab and withdraw dialog`() {
        val repo = TournamentRepository()
        val viewModel = TournamentViewModel(repo)

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Hardcoded 7788 / 77110 rejected; Owner signs in with registered credentials (Firestore role = OWNER)
        assertFalse(viewModel.login("01861583924", "7788"))
        assertFalse(viewModel.login("owner@trustedth.com", "77110"))
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
        composeTestRule.waitForIdle()

        // Owner TopAppBar crown button is visible for OWNER
        composeTestRule.onNodeWithTag("appbar_owner_btn").assertIsDisplayed()

        // Main dashboard renders Match Slots cleanly without quests, spin, or mini-games clutter
        composeTestRule.onNodeWithTag("matches_screen_list")
            .assertIsDisplayed()
            .performScrollToNode(hasTestTag("match_card_m_lw_1"))
        composeTestRule.onNodeWithTag("match_card_m_lw_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("mission_card_m1").assertDoesNotExist()
        composeTestRule.onNodeWithTag("sensitivity_lab_card").assertDoesNotExist()
        composeTestRule.onNodeWithText("প্রতি কিল", substring = true).assertDoesNotExist()

        // Fun & Giveaway tab sits in bottom navigation beside Global Chat
        composeTestRule.onNodeWithTag("tab_global_chat").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_fun_giveaway").assertIsDisplayed()

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
    fun `viewmodel reactive sync updates slot capacity bkash number and fun giveaway tab state`() {
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

        vm.selectTab(TournamentTab.FUN_GIVEAWAY)
        assertEquals(TournamentTab.FUN_GIVEAWAY, vm.currentTab.value)
    }

    @Test
    fun `new player registration grants 5 taka hides owner bar and opens pro sensitivity lab inside fun giveaway tab`() {
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

        // Open Fun & Giveaway tab (beside Global Chat) where all fun modules & Sensitivity Pro Lab live
        composeTestRule.onNodeWithTag("tab_fun_giveaway").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("fun_giveaway_screen").assertIsDisplayed()

        composeTestRule.onNodeWithTag("fun_giveaway_screen")
            .performScrollToNode(hasTestTag("sensitivity_lab_card"))
        composeTestRule.onNodeWithTag("sensitivity_lab_card").performScrollTo().assertIsDisplayed().performClick()
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
        assertTrue(firstSessionRepo.bookSlotWithWallet(match, 2, "TH_Hasan", "77665544").first)
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
        composeTestRule.onNodeWithTag("wallet_screen").performScrollToNode(hasTestTag("wallet_system_guide_card"))
        composeTestRule.onNodeWithTag("wallet_system_guide_card").assertIsDisplayed()

        composeTestRule.onNodeWithTag("wallet_screen").performScrollToIndex(0)
        composeTestRule.waitForIdle()
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

        // Owner logs in
        viewModel.logout()
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))

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
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))

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
            .performScrollToNode(hasTestTag("directory_player_card_11410365896"))
        composeTestRule.onNodeWithTag("directory_player_card_11410365896").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("player_profile_dialog").assertIsDisplayed()
        composeTestRule.onNode(
            hasText("TH★DEVIL⚡") and hasAnyAncestor(hasTestTag("player_profile_dialog"))
        ).assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_profile_give_kudos_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertEquals(13, repo.registeredUsers.value.first { it.uid == "11410365896" }.kudosCount)

        // Clicking GG/Respect again from the same player toggles/removes kudos cleanly back to 12
        composeTestRule.onNodeWithTag("player_profile_give_kudos_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertEquals(12, repo.registeredUsers.value.first { it.uid == "11410365896" }.kudosCount)
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
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
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
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))

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
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
        viewModel.openEditProfile()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("pick_gif_pfp_btn").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("pick_gif_banner_btn").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `discord global chat next to fun giveaway lets owner and members talk react once without pins and view profiles`() {
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

        // Global Chat and Fun/Giveaway tabs exist beside each other
        composeTestRule.onNodeWithTag("tab_fun_giveaway").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_global_chat").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("global_chat_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("global_chat_pinned_banner").assertDoesNotExist()
        composeTestRule.onNodeWithTag("global_chat_pin_toggle").assertDoesNotExist()

        // Member sends a message via UI
        composeTestRule.onNodeWithTag("global_chat_input").performTextReplacement("সবাই রেডি তো?")
        composeTestRule.onNodeWithTag("global_chat_send_btn").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        assertTrue(repo.globalChatMessages.value.any { it.text == "সবাই রেডি তো?" })
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("global_chat_list")) and hasText("সবাই রেডি তো?")
        ).assertIsDisplayed()

        // Owner logs in and sends message in Global Chat
        viewModel.logout()
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
        viewModel.selectTab(TournamentTab.GLOBAL_CHAT)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("global_chat_pin_toggle").assertDoesNotExist()
        composeTestRule.onNodeWithTag("global_chat_input").performTextReplacement("ম্যাচ ১৫ মিনিটে শুরু হবে!")
        composeTestRule.onNodeWithTag("global_chat_send_btn").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        val lastMsg = repo.globalChatMessages.value.last()
        assertFalse(lastMsg.isPinnedAnnouncement)
        assertEquals("ম্যাচ ১৫ মিনিটে শুরু হবে!", lastMsg.text)

        // Single-use toggleable reaction chip works (tap adds 1 GG, tapping WIN switches vote cleanly)
        composeTestRule.onNodeWithTag("global_chat_list")
            .performScrollToNode(hasTestTag("global_chat_msg_${lastMsg.id}"))
        composeTestRule.onNodeWithTag("global_chat_react_${lastMsg.id}_GG").performClick()
        composeTestRule.waitForIdle()
        assertEquals(1, repo.globalChatMessages.value.last().reactions["GG"])

        composeTestRule.onNodeWithTag("global_chat_react_${lastMsg.id}_WIN").performClick()
        composeTestRule.waitForIdle()
        assertEquals(null, repo.globalChatMessages.value.last().reactions["GG"])
        assertEquals(1, repo.globalChatMessages.value.last().reactions["WIN"])

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

    @Test
    fun `global chat renders role beside IGN, icon-only PRO_PLAYER and V_BADGE, icon plus name for other badges, and opens profile via PFP click`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        val playerPhone = "01755667788"
        assertTrue(viewModel.register("Rafi", "TH_Rafi", "55667788", playerPhone, playerPhone, "avatar_cyber", "pass123"))
        assertTrue(repo.grantBadgeToUser(playerPhone, com.example.model.BadgeType.PRO_PLAYER).first)
        assertTrue(repo.grantBadgeToUser(playerPhone, com.example.model.BadgeType.V_BADGE).first)
        assertTrue(repo.grantBadgeToUser(playerPhone, com.example.model.BadgeType.PRIME_SPENDER).first)

        val sendRes = repo.sendGlobalChatMessage("টিম রেডি, ম্যাচ জিতব!")
        assertTrue(sendRes.first)
        val memberMsg = repo.globalChatMessages.value.last()

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("tab_global_chat").performClick()
        composeTestRule.waitForIdle()

        // Scroll to the sent message card so it's laid out cleanly
        composeTestRule.onNodeWithTag("global_chat_list")
            .performScrollToNode(hasTestTag("global_chat_msg_${memberMsg.id}"))
        composeTestRule.waitForIdle()

        // Role chip & IGN visible
        composeTestRule.onNodeWithTag("global_chat_role_${memberMsg.id}", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("global_chat_role_${memberMsg.id}")) and hasText("মেম্বার", substring = true),
            useUnmergedTree = true
        ).assertIsDisplayed()

        // PRO_PLAYER and V_BADGE render as icon-only badges (no text label inside those tags)
        composeTestRule.onNodeWithTag("global_chat_badge_icon_${memberMsg.id}_PRO_PLAYER", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNodeWithTag("global_chat_badge_icon_${memberMsg.id}_V_BADGE", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("global_chat_badge_icon_${memberMsg.id}_PRO_PLAYER")) and hasText("প্রো প্লেয়ার"),
            useUnmergedTree = true
        ).assertDoesNotExist()

        // PRIME_SPENDER renders with icon + badge title
        composeTestRule.onNodeWithTag("global_chat_badge_${memberMsg.id}_PRIME_SPENDER", useUnmergedTree = true).assertIsDisplayed()
        composeTestRule.onNode(
            hasAnyAncestor(hasTestTag("global_chat_badge_${memberMsg.id}_PRIME_SPENDER")) and hasText("প্রাইম বিগ স্পেন্ডার [8]", substring = true),
            useUnmergedTree = true
        ).assertIsDisplayed()

        // Direct click on PFP opens PlayerProfileCardDialog
        composeTestRule.onNodeWithTag("global_chat_pfp_${memberMsg.id}").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("player_profile_dialog").assertIsDisplayed()
    }

    @Test
    fun `owner profile is clean without level 68, notun joddha, or gaming weapon clutter`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // 1. Account screen shows clean Owner identity without Level 68 / নতুন যোদ্ধা / MP40 clutter
        composeTestRule.onNodeWithTag("tab_account").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("owner_clean_rank_pill").assertIsDisplayed()
        composeTestRule.onNodeWithText("লেভেল ৬৮", substring = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("LVL 68", substring = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("নতুন যোদ্ধা", substring = true).assertDoesNotExist()
        composeTestRule.onNodeWithText("MP40", substring = true).assertDoesNotExist()

        // 2. Global Chat Owner PFP opens clean Owner PlayerProfileCardDialog (no stats grid / LVL)
        composeTestRule.onNodeWithTag("tab_global_chat").performClick()
        composeTestRule.waitForIdle()
        val ownerMsg = repo.globalChatMessages.value.first()
        composeTestRule.onNodeWithTag("global_chat_pfp_${ownerMsg.id}").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("player_profile_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("owner_profile_clean_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("player_profile_stats_card").assertDoesNotExist()
        composeTestRule.onNodeWithText("LVL 0").assertDoesNotExist()
    }

    @Test
    fun `strict validation blocks banned users, invalid phones, dummy trxIds and shows zero tolerance rules matrix`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)
        val phone = "01712345678"
        assertTrue(viewModel.register("Sakib", "TH_Sakib", "11223344", phone, phone, "th_logo", "pass123"))

        // Dummy TrxIDs and non-BD phone numbers must fail
        assertFalse(repo.submitDepositRequest(100, com.example.model.WalletPaymentMethod.BKASH, "99999999999", "BKS112233").first)
        assertFalse(repo.submitDepositRequest(100, com.example.model.WalletPaymentMethod.BKASH, phone, "1234").first)
        assertFalse(repo.submitDepositRequest(100, com.example.model.WalletPaymentMethod.BKASH, phone, "TEST1").first)
        assertFalse(repo.submitDepositRequest(100, com.example.model.WalletPaymentMethod.BKASH, phone, "AAAAAA").first)

        // Valid request works
        assertTrue(repo.submitDepositRequest(100, com.example.model.WalletPaymentMethod.BKASH, phone, "BKS887766").first)

        // Banned user cannot submit deposit or withdraw
        assertTrue(repo.banUser(phone, "ফ্রড"))
        assertFalse(repo.submitDepositRequest(100, com.example.model.WalletPaymentMethod.NAGAD, phone, "NGD998877").first)
        assertFalse(repo.submitWithdrawRequest(50, com.example.model.WalletPaymentMethod.BKASH, phone).first)

        // Rules screen shows Zero-Tolerance enforcement card & 8 strict rules
        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        viewModel.openRules()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("rules_support_screen")
            .performScrollToNode(hasTestTag("strict_enforcement_card"))
        composeTestRule.onNodeWithTag("strict_enforcement_card").assertIsDisplayed()
        assertEquals(8, repo.strictRules.value.size)
    }

    @Test
    fun `owner command overview and prepare for download reset flow clear test data cleanly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)
        assertTrue(viewModel.register("TestUser", "TH_Test", "10203040", "01710203040", "01710203040", "th_logo", "pass123"))
        viewModel.logout()
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        viewModel.openOwnerPanel()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("owner_command_overview").assertIsDisplayed()
        composeTestRule.onNodeWithTag("owner_tab_settings").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("owner_prepare_download_card").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("owner_fresh_reset_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("হ্যাঁ, রিসেট করুন").performClick()
        composeTestRule.waitForIdle()

        assertFalse(viewModel.isLoggedIn.value)
        assertEquals(0, viewModel.walletBalance.value)
        assertEquals(1, repo.registeredUsers.value.size)
        val owner = repo.registeredUsers.value.first()
        assertEquals(com.example.model.UserRole.OWNER, owner.role)
        assertEquals("11410365896", owner.uid)
        assertEquals(0, owner.level)
        assertEquals("", owner.favoriteWeapon)
        assertEquals("", owner.activeCharacter)
    }

    @Test
    fun `owner and admin moderation panel manages badges, 7 warn ban, and custom timeout`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        val playerPhone = "01788776655"
        val adminPhone = "01766554433"
        assertTrue(viewModel.register("Sajid", "TH_Sajid", "88776655", playerPhone, playerPhone, "th_logo", "pass123"))
        assertTrue(viewModel.register("AdminRafi", "TH_AdminRafi", "66554433", adminPhone, adminPhone, "th_logo", "pass123"))
        assertTrue(repo.promoteToAdmin(adminPhone, "AdminRafi", "TH_AdminRafi", "66554433"))

        // Log in as Admin and render MainTournamentApp
        viewModel.logout()
        assertTrue(viewModel.login(adminPhone, "pass123"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // 1. Admin opens Admin Panel -> Moderation tab
        viewModel.openAdminPanel()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("admin_tab_moderation").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("moderation_panel_section").assertIsDisplayed()

        // Select target user by phone
        composeTestRule.onNodeWithTag("mod_target_input").performScrollTo().performTextReplacement(playerPhone)
        composeTestRule.waitForIdle()

        // Grant PRO_PLAYER badge then revoke it
        composeTestRule.onNodeWithTag("mod_grant_badge_PRO_PLAYER").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertTrue(repo.registeredUsers.value.first { it.phone == playerPhone }.effectiveBadges.contains(com.example.model.BadgeType.PRO_PLAYER))

        composeTestRule.onNodeWithTag("mod_revoke_badge_PRO_PLAYER").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertFalse(repo.registeredUsers.value.first { it.phone == playerPhone }.effectiveBadges.contains(com.example.model.BadgeType.PRO_PLAYER))

        // Issue a moderation warn
        composeTestRule.onNodeWithTag("mod_warn_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertEquals(1, repo.registeredUsers.value.first { it.phone == playerPhone }.moderationWarningsCount)

        // Apply 1 hour timeout via 60m preset
        composeTestRule.onNodeWithTag("mod_timeout_preset_60").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("mod_apply_timeout_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertNotNull(repo.registeredUsers.value.first { it.phone == playerPhone }.timeoutUntilMillis)
        assertTrue(repo.moderationLogs.value.isNotEmpty())

        // 2. Owner opens Owner Panel -> Moderation tab and verifies Moderation Panel + Audit Log
        viewModel.closeAdminPanel()
        viewModel.logout()
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
        composeTestRule.waitForIdle()

        viewModel.openOwnerPanel()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("owner_tab_moderation").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("moderation_panel_section").assertIsDisplayed()
    }

    @Test
    fun `timed out user sees non-interactive live countdown overlay until timeout expires`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        val playerPhone = "01744558899"
        assertTrue(viewModel.register("Rony", "TH_Rony", "44558899", playerPhone, playerPhone, "th_logo", "pass123"))

        // Apply 60-minute (1 hour) timeout while user is logged in and verify persistence across restart
        val timeoutRes = viewModel.timeoutUser(playerPhone, 60L, "টক্সিক আচরণ")
        assertTrue(timeoutRes.first)
        val persistedTimeoutUntil = TournamentRepository(context).userProfile.value.timeoutUntilMillis
        assertNotNull(persistedTimeoutUntil)

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Lockout screen and live timer must be visible
        composeTestRule.onNodeWithTag("user_timeout_lock_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("timeout_countdown_timer").assertIsDisplayed()

        // Underlying Wallet tab tap must NOT work while lockout overlay consumes all taps
        composeTestRule.onNodeWithTag("tab_wallet", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        assertEquals(TournamentTab.MATCHES, viewModel.currentTab.value)
        composeTestRule.onNodeWithTag("user_timeout_lock_screen").assertIsDisplayed()

        // Removing timeout unlocks the app cleanly
        assertTrue(viewModel.removeTimeout(playerPhone).first)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("user_timeout_lock_screen").assertDoesNotExist()

        // Now Wallet tab click works again
        composeTestRule.onNodeWithTag("tab_wallet").performClick()
        composeTestRule.waitForIdle()
        assertEquals(TournamentTab.WALLET, viewModel.currentTab.value)
    }

    @Test
    fun `a to z owner editor is completely removed and appbar owner button opens owner panel`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Logged out screen must NOT show Google Sign-In button
        composeTestRule.onNodeWithTag("google_signin_btn").assertDoesNotExist()
        composeTestRule.onNodeWithTag("auth_submit_btn").performScrollTo().assertIsDisplayed()

        // Owner logs in
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
        composeTestRule.waitForIdle()

        // Cloud Sync pill is visible in quick action strip
        composeTestRule.onNodeWithTag("appbar_cloud_sync_pill").assertIsDisplayed()

        // Settings dialog must NOT have open_master_editor_from_settings anymore
        composeTestRule.onNodeWithTag("appbar_settings_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("settings_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("open_master_editor_from_settings").assertDoesNotExist()
        composeTestRule.onNodeWithText("সেটিংস বন্ধ করুন").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Clicking appbar_owner_btn must directly open OwnerControlDialog
        composeTestRule.onNodeWithTag("appbar_owner_btn").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("owner_control_dialog").assertIsDisplayed()
    }

    @Test
    fun `owner slot panel adds slots updates capacity removes slots and refunds booked player wallet`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        // Player registers & gets balance, then books slot 3 with wallet
        val playerPhone = "01755667788"
        assertTrue(viewModel.register("Tamim", "TH_Tamim", "55667788", playerPhone, playerPhone, "th_logo", "pass123"))
        assertTrue(viewModel.adjustUserBalance(playerPhone, 100, isAdd = true, reason = "Test balance"))
        val match = viewModel.allMatches.value.first()
        val initialCapacity = match.totalSlots
        val expectedPlayerBalanceBeforeRefund = 105 - match.entryFee
        assertTrue(repo.bookSlotWithWallet(match, 2, "TH_Tamim", "55667788").first)
        val playerBookingId = repo.myBookings.value.first { it.slotNumber == 2 && it.matchId == match.id }.bookingId
        assertEquals(expectedPlayerBalanceBeforeRefund, repo.registeredUsers.value.first { it.phone == playerPhone }.walletBalance)

        // Owner logs in (wallet balance 0)
        viewModel.logout()
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
        viewModel.clearSnackbar()
        val ownerBalanceBefore = repo.walletBalance.value

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Verify upgraded RoomSlotGridDialog opens cleanly from MatchCard slot grid button
        composeTestRule.onNodeWithTag("matches_screen_list")
            .performScrollToNode(hasTestTag("slot_grid_btn_${match.id}"))
        composeTestRule.onNodeWithTag("slot_grid_btn_${match.id}").performScrollTo()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("slot_grid_btn_${match.id}").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("room_slot_grid_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("slot_grid_item_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("slot_booked_player_chip_2").performScrollTo().assertIsDisplayed()
        viewModel.closeSlotGrid()
        composeTestRule.waitForIdle()

        // Open Owner Panel -> Slot Control Tab
        viewModel.openOwnerPanel()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("owner_tab_slots").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // 1. Increase slot capacity via UI button (+2)
        composeTestRule.onNodeWithTag("owner_slot_control_list")
            .performScrollToNode(hasTestTag("owner_slot_capacity_inc_btn"))
        composeTestRule.onNodeWithTag("owner_slot_capacity_inc_btn").performClick()
        composeTestRule.waitForIdle()
        assertEquals(initialCapacity + 2, repo.matches.value.first { it.id == match.id }.totalSlots)

        // 2. Add a manual slot (#4) via Slot Add panel
        composeTestRule.onNodeWithTag("owner_slot_control_list")
            .performScrollToNode(hasTestTag("owner_add_slot_num_input"))
        composeTestRule.onNodeWithTag("owner_add_slot_num_input").performTextReplacement("4")
        composeTestRule.onNodeWithTag("owner_add_slot_ign_input").performTextReplacement("TH_ManualPro")
        composeTestRule.onNodeWithTag("owner_add_slot_uid_input").performTextReplacement("99887766")
        composeTestRule.onNodeWithTag("owner_slot_control_list")
            .performScrollToNode(hasTestTag("owner_add_slot_btn"))
        composeTestRule.onNodeWithTag("owner_add_slot_btn").performClick()
        composeTestRule.waitForIdle()
        assertTrue(repo.myBookings.value.any { it.matchId == match.id && it.slotNumber == 4 && it.playerIgn == "TH_ManualPro" })

        // 3. Remove player Tamim's slot (#2) via UI cancel & confirm refund button
        composeTestRule.onNodeWithTag("owner_slot_control_list")
            .performScrollToNode(hasTestTag("cancel_slot_btn_$playerBookingId"))
        composeTestRule.onNodeWithTag("cancel_slot_btn_$playerBookingId").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("confirm_refund_btn").performClick()
        composeTestRule.waitForIdle()

        // Verify Tamim's wallet received the refund (+entryFee), NOT the Owner's wallet
        assertFalse(repo.myBookings.value.any { it.bookingId == playerBookingId })
        assertEquals(105, repo.registeredUsers.value.first { it.phone == playerPhone }.walletBalance)
        assertEquals(ownerBalanceBefore, repo.walletBalance.value)
    }

    @Test
    fun `owner and admin publish live announcement globally visible across tabs and match result updates live leaderboard`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        // Register a player and an admin
        val playerPhone = "01722334455"
        val adminPhone = "01733445566"
        assertTrue(viewModel.register("Sakib", "TH_SakibPro", "22334455", playerPhone, playerPhone, "th_logo", "pass123"))
        assertTrue(viewModel.register("AdminKai", "TH_KaiMod", "33445566", adminPhone, adminPhone, "th_logo", "pass123"))
        assertTrue(repo.promoteToAdmin(adminPhone, "AdminKai", "TH_KaiMod", "33445566"))

        // Initially leaderboard is empty on fresh download
        assertTrue(viewModel.leaderboard.value.isEmpty())

        // Log in as Owner
        viewModel.logout()
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
        viewModel.clearSnackbar()

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // 1. Owner opens Owner Panel -> Live Announcement tab & publishes urgent live announcement
        viewModel.openOwnerPanel()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("owner_tab_announcement").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("announcement_title_input").performScrollTo()
            .performTextReplacement("🚨 রুম কোড আপডেট")
        composeTestRule.onNodeWithTag("announcement_message_input").performScrollTo()
            .performTextReplacement("সবাই ৯টায় জয়েন করুন!")
        composeTestRule.onNodeWithTag("announcement_urgent_switch").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("publish_announcement_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        viewModel.closeOwnerPanel()
        composeTestRule.waitForIdle()

        // Global banner is displayed across tabs for everyone
        composeTestRule.onNodeWithTag("live_app_announcement_banner").assertIsDisplayed()
        composeTestRule.onNodeWithText("সবাই ৯টায় জয়েন করুন!", substring = true).assertIsDisplayed()

        // Switch to Wallet and Leaderboard tabs -> banner remains prominently visible
        composeTestRule.onNodeWithTag("tab_wallet").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("live_app_announcement_banner").assertIsDisplayed()

        composeTestRule.onNodeWithTag("tab_leaderboard").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("live_app_announcement_banner").assertIsDisplayed()
        composeTestRule.onNodeWithTag("leaderboard_live_badge").assertIsDisplayed()

        // 2. Owner opens Owner Panel -> Match Results & Command Center -> publishes Booyah winner result & prize
        viewModel.openOwnerPanel()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("owner_tab_results").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("owner_match_result_command_section").assertIsDisplayed()
        composeTestRule.onNodeWithTag("result_winner_target_input").performScrollTo()
            .performTextReplacement(playerPhone)
        composeTestRule.onNodeWithTag("result_winner_kills_input").performScrollTo()
            .performTextReplacement("6")
        composeTestRule.onNodeWithTag("result_prize_input").performScrollTo()
            .performTextReplacement("85")
        composeTestRule.onNodeWithTag("publish_match_result_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        viewModel.closeOwnerPanel()
        composeTestRule.waitForIdle()

        // Winner wallet & stats updated AND Live Leaderboard is now populated immediately!
        val updatedPlayer = repo.registeredUsers.value.first { it.phone == playerPhone }
        assertEquals(90, updatedPlayer.walletBalance) // 5 bonus + 85 prize
        assertEquals(6, updatedPlayer.totalKills)
        assertEquals(1, updatedPlayer.totalWins)
        assertEquals(85, updatedPlayer.totalEarnings)

        composeTestRule.onNodeWithTag("leaderboard_top_player_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("leaderboard_recent_results_strip").assertIsDisplayed()
        composeTestRule.onNodeWithTag("leaderboard_screen")
            .performScrollToNode(hasTestTag("leaderboard_player_row_${updatedPlayer.uid}"))
        composeTestRule.onNodeWithTag("leaderboard_player_row_${updatedPlayer.uid}").assertIsDisplayed()

        // 3. Admin can also update & clear live announcement from Admin Panel
        viewModel.logout()
        assertTrue(viewModel.login(adminPhone, "pass123"))
        viewModel.openAdminPanel()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("admin_tab_announcement").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("announcement_message_input").performScrollTo()
            .performTextReplacement("অ্যাডমিন থেকে লাইভ আপডেট")
        composeTestRule.onNodeWithTag("publish_announcement_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        viewModel.closeAdminPanel()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("live_app_announcement_banner").assertIsDisplayed()
        composeTestRule.onNodeWithText("অ্যাডমিন থেকে লাইভ আপডেট", substring = true).assertIsDisplayed()

        // Admin clears announcement -> banner disappears
        viewModel.openAdminPanel()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("admin_tab_announcement").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("clear_announcement_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        viewModel.closeAdminPanel()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("live_app_announcement_banner").assertDoesNotExist()
    }

    @Test
    fun `owner and admin add and remove badges directly on clicked player profile while regular player cannot`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.getSharedPreferences("th_tournament_persistent_store_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()

        val repo = TournamentRepository(context)
        val viewModel = TournamentViewModel(repo)

        val playerPhone = "01711223344"
        val adminPhone = "01799887766"
        assertTrue(viewModel.register("Rafi", "TH_RafiPro", "11223344", playerPhone, playerPhone, "th_logo", "pass123"))
        assertTrue(repo.sendGlobalChatMessage("আমি টুর্নামেন্ট খেলতে রেডি!").first)
        val playerMsgId = repo.globalChatMessages.value.last().id

        assertTrue(viewModel.register("AdminNabil", "TH_NabilMod", "99887766", adminPhone, adminPhone, "th_logo", "pass123"))
        assertTrue(repo.promoteToAdmin(adminPhone, "AdminNabil", "TH_NabilMod", "99887766"))

        // Register a second regular player to view Rafi's profile
        viewModel.logout()
        val viewerPhone = "01744556677"
        assertTrue(viewModel.register("Viewer", "TH_Viewer", "44556677", viewerPhone, viewerPhone, "th_logo", "pass123"))

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("tab_global_chat").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("global_chat_list")
            .performScrollToNode(hasTestTag("global_chat_msg_$playerMsgId"))
        composeTestRule.onNodeWithTag("global_chat_pfp_$playerMsgId").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("player_profile_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_toggle_badge_picker_btn").assertDoesNotExist()
        composeTestRule.onNodeWithTag("profile_inline_add_badge_btn").assertDoesNotExist()
        composeTestRule.onNodeWithTag("player_profile_close_btn").performClick()
        composeTestRule.waitForIdle()

        // 1. Owner logs in -> clicks Rafi's profile in Global Chat -> grants & revokes badges live
        viewModel.logout()
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("global_chat_list")
            .performScrollToNode(hasTestTag("global_chat_msg_$playerMsgId"))
        composeTestRule.onNodeWithTag("global_chat_pfp_$playerMsgId").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("player_profile_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_toggle_badge_picker_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("profile_badge_picker_panel").assertIsDisplayed()

        // Grant V_BADGE and HEADSHOT_KING directly inside the profile card
        composeTestRule.onNodeWithTag("profile_badge_option_V_BADGE").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("profile_badge_feedback_text").performScrollTo().assertIsDisplayed()
        assertTrue(
            repo.registeredUsers.value.first { it.phone == playerPhone }
                .effectiveBadges.contains(com.example.model.BadgeType.V_BADGE)
        )

        composeTestRule.onNodeWithTag("profile_badge_option_HEADSHOT_KING").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertTrue(
            repo.registeredUsers.value.first { it.phone == playerPhone }
                .effectiveBadges.contains(com.example.model.BadgeType.HEADSHOT_KING)
        )

        // Tap V_BADGE again to revoke it live
        composeTestRule.onNodeWithTag("profile_badge_option_V_BADGE").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertFalse(
            repo.registeredUsers.value.first { it.phone == playerPhone }
                .effectiveBadges.contains(com.example.model.BadgeType.V_BADGE)
        )

        composeTestRule.onNodeWithTag("player_profile_close_btn").performClick()
        composeTestRule.waitForIdle()

        // 2. Admin logs in -> clicks Rafi's profile -> grants BOOYAH_MASTER; cannot edit Owner's profile badges
        viewModel.logout()
        assertTrue(viewModel.login(adminPhone, "pass123"))
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("global_chat_list")
            .performScrollToNode(hasTestTag("global_chat_msg_$playerMsgId"))
        composeTestRule.onNodeWithTag("global_chat_pfp_$playerMsgId").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("profile_inline_add_badge_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("profile_badge_picker_panel").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_badge_option_BOOYAH_MASTER").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        assertTrue(
            repo.registeredUsers.value.first { it.phone == playerPhone }
                .effectiveBadges.contains(com.example.model.BadgeType.BOOYAH_MASTER)
        )
        composeTestRule.onNodeWithTag("player_profile_close_btn").performClick()
        composeTestRule.waitForIdle()

        // Admin clicking Owner's welcome message PFP must NOT see badge editing controls on Owner
        val ownerMsgId = repo.globalChatMessages.value.first().id
        composeTestRule.onNodeWithTag("global_chat_list")
            .performScrollToNode(hasTestTag("global_chat_msg_$ownerMsgId"))
        composeTestRule.onNodeWithTag("global_chat_pfp_$ownerMsgId").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("player_profile_dialog").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_toggle_badge_picker_btn").assertDoesNotExist()
        composeTestRule.onNodeWithTag("player_profile_close_btn").performClick()
        composeTestRule.waitForIdle()

        // 3. Reset app for fresh download restores clean initial state
        viewModel.resetAppForFreshDownload()
        composeTestRule.waitForIdle()
        assertFalse(viewModel.isLoggedIn.value)
        assertEquals(1, repo.registeredUsers.value.size)
        assertEquals("11410365896", repo.registeredUsers.value.first().uid)
        assertTrue(repo.leaderboard.value.isEmpty())
        assertTrue(repo.myBookings.value.isEmpty())
    }

    @Test
    fun `account screen shows Please verify your email and resend verification button when unverified`() {
        val repo = TournamentRepository()
        val viewModel = TournamentViewModel(repo)
        val unverifiedEmail = "unverified.player@gmail.com"
        assertTrue(
            repo.register(
                accountName = "Unverified Player",
                ign = "TH_Unverified",
                uid = "5544332211",
                phone = "01733445566",
                bkash = "01733445566",
                password = "password123",
                email = unverifiedEmail
            )
        )
        repo.logout()

        composeTestRule.setContent {
            TrustedTournamentTheme {
                AccountScreen(
                    userProfile = repo.userProfile.value,
                    walletBalance = 0,
                    onEditProfileClick = {},
                    onLogin = { e, p -> viewModel.loginWithEmailVerification(e, p) },
                    onRegister = { n, e, ign, uid, ph, bk, av, pw ->
                        viewModel.signUpWithEmailVerification(n, e, ign, uid, ph, bk, av, pw)
                    },
                    onResendVerification = { e, p -> viewModel.resendVerificationEmail(e, p) },
                    onLogout = {},
                    onNavigateToWallet = {},
                    onNavigateToMyMatches = {},
                    onOpenRules = {},
                    onOpenSupportChat = {},
                    onOpenOwnerPanel = {},
                    onOpenSettings = {},
                    appCustomization = repo.appCustomization.value
                )
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("login_email_input").performScrollTo().performTextReplacement(unverifiedEmail)
        composeTestRule.onNodeWithTag("login_password_input").performScrollTo().performTextReplacement("password123")
        composeTestRule.onNodeWithTag("submit_login_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Please verify your email").assertIsDisplayed()
        composeTestRule.onNodeWithTag("account_resend_verification_btn").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `discord global chat renders category channels excludes staff filter and filters messages by channel`() {
        val repo = TournamentRepository()
        val viewModel = TournamentViewModel(repo)
        assertTrue(viewModel.login("owner@trustedth.com", "123456"))
        assertTrue(
            viewModel.sendGlobalChatMessage(
                text = "Lone Wolf 1v1 slot open",
                channelId = ChatChannel.LONE_WOLF.id
            ).first
        )

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("tab_global_chat").assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        // Category channels rendered cleanly; staff-only filter is removed
        composeTestRule.onNodeWithTag("chat_channel_global").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_channel_lone_wolf").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_channel_br_rank").assertIsDisplayed()
        composeTestRule.onNodeWithTag("chat_channel_cs_rank").assertIsDisplayed()
        composeTestRule.onNodeWithTag("global_chat_filter_staff").assertDoesNotExist()

        // Lone Wolf message is hidden in #global-chat and appears when switching to #lone-wolf
        composeTestRule.onNodeWithText("Lone Wolf 1v1 slot open").assertDoesNotExist()
        composeTestRule.onNodeWithTag("chat_channel_lone_wolf").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Lone Wolf 1v1 slot open").assertIsDisplayed()
    }
}




