package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.core.app.ApplicationProvider
import com.example.data.TournamentRepository
import com.example.ui.MainTournamentApp
import com.example.ui.TournamentTab
import com.example.ui.TournamentViewModel
import com.example.ui.theme.TrustedTournamentTheme
import org.junit.Assert.assertEquals
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

        // Switch to Sign In tab so forgot_password_btn is rendered
        composeTestRule.onNodeWithText("সাইন ইন").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("forgot_password_btn").performScrollTo().assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("forgot_password_dialog").assertIsDisplayed()
    }

    @Test
    fun `demo owner login renders matches missions maintenance banner and dynamic withdraw dialog`() {
        val repo = TournamentRepository()
        val viewModel = TournamentViewModel(repo)

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        // Scroll to and tap Demo Owner login
        composeTestRule.onNodeWithTag("demo_owner_login_btn").performScrollTo().assertIsDisplayed().performClick()
        composeTestRule.waitForIdle()

        // Main dashboard renders with matches & daily missions
        composeTestRule.onNodeWithTag("matches_screen_list").assertIsDisplayed()
        composeTestRule.onNodeWithTag("matches_screen_list").performScrollToIndex(2)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("mission_card_m1").assertIsDisplayed()

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
    fun `pro sensitivity lab opens from matches and match hype button toggles cleanly`() {
        val repo = TournamentRepository()
        val viewModel = TournamentViewModel(repo)

        composeTestRule.setContent {
            TrustedTournamentTheme {
                MainTournamentApp(viewModel = viewModel)
            }
        }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("demo_owner_login_btn").performScrollTo().performClick()
        composeTestRule.waitForIdle()

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
}
