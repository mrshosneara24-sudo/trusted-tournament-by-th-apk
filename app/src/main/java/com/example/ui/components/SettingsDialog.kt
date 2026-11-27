package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppCustomization
import com.example.model.DepositRequest
import com.example.model.RequestStatus
import com.example.model.UserPreferences
import com.example.model.WithdrawRequest
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen

@Composable
fun SettingsDialog(
    officialBkashNumber: String,
    officialWhatsAppUrl: String,
    appCustomization: AppCustomization = AppCustomization(),
    isOwner: Boolean = false,
    isAdmin: Boolean = false,
    currentBalance: Int = 0,
    matchesPlayed: Int = 0,
    currentUserPhone: String = "",
    depositRequests: List<DepositRequest> = emptyList(),
    withdrawRequests: List<WithdrawRequest> = emptyList(),
    onDismiss: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onOpenOwnerPanel: () -> Unit = {},
    onOpenDepositDialog: () -> Unit = {},
    onOpenWithdrawDialog: () -> Unit = {},
    onApproveDeposit: (requestId: String) -> Boolean = { false },
    onRejectDeposit: (requestId: String) -> Boolean = { false },
    onApproveWithdraw: (requestId: String) -> Boolean = { false },
    onRejectWithdraw: (requestId: String) -> Boolean = { false },
    userPreferences: UserPreferences = UserPreferences(),
    onUpdateUserPreferences: (roomAlert: Boolean, fiveMin: Boolean, autoLaunch: Boolean) -> Unit = { _, _, _ -> },
    onRefreshAppCache: () -> String = { "অ্যাপ ও ক্লাউড ক্যাশে রিফ্রেশ সম্পন্ন!" },
    onResetAppForFreshDownload: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var showFreshResetConfirm by remember { mutableStateOf(false) }

    val userDeposits = remember(depositRequests, currentUserPhone) {
        if (currentUserPhone.isBlank()) emptyList()
        else depositRequests.filter { it.userPhone == currentUserPhone }
    }
    val userWithdraws = remember(withdrawRequests, currentUserPhone) {
        if (currentUserPhone.isBlank()) emptyList()
        else withdrawRequests.filter { it.userPhone == currentUserPhone }
    }
    val pendingGlobalCount = remember(depositRequests, withdrawRequests) {
        depositRequests.count { it.status == RequestStatus.PENDING } +
            withdrawRequests.count { it.status == RequestStatus.PENDING }
    }
    val pendingUserCount = remember(userDeposits, userWithdraws) {
        userDeposits.count { it.status == RequestStatus.PENDING } +
            userWithdraws.count { it.status == RequestStatus.PENDING }
    }
    // 0 = My Requests, 1 = Admin/Owner Approval Queue
    var requestSectionTab by remember(isOwner, isAdmin, pendingGlobalCount) {
        mutableIntStateOf(if ((isOwner || isAdmin) && pendingGlobalCount > 0) 1 else 0)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 760.dp)
                .testTag("settings_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(listOf(FlameYellow.copy(alpha = 0.45f), DarkBorder, NeonPink.copy(alpha = 0.4f)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(NeonPink.copy(alpha = 0.25f), FlameYellow.copy(alpha = 0.2f))
                                    )
                                )
                                .border(1.dp, FlameYellow.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = FlameYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "সেটিংস ও প্রেফারেন্স",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = TextPrimary
                            )
                            Text(
                                text = "${appCustomization.appName} নিয়ন্ত্রণ",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==================== SECTION 0: DEPOSIT / WITHDRAW REQUESTS ====================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ডিপোজিট ও উইথড্র রিকোয়েস্ট",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = FlameYellow
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = VictoryGreen.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.45f))
                    ) {
                        Text(
                            text = "ব্যালেন্স: ৳$currentBalance",
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VictoryGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_deposit_withdraw_section")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Top Quick Summary & Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(BkashPink.copy(alpha = 0.18f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = BkashPink,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "পেমেন্ট রিকোয়েস্ট সেন্টার",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (isOwner || isAdmin) {
                                            "অপেক্ষমাণ রিকোয়েস্ট: $pendingGlobalCount টি (মোট)"
                                        } else {
                                            "আপনার অপেক্ষমাণ রিকোয়েস্ট: $pendingUserCount টি"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (pendingGlobalCount > 0 || pendingUserCount > 0) FlameYellow else TextSecondary
                                    )
                                }
                            }
                        }

                        // Career 1-Match Rule Micro Note
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (matchesPlayed >= 1) VictoryGreen.copy(alpha = 0.12f) else DarkElevated,
                            border = BorderStroke(
                                1.dp,
                                if (matchesPlayed >= 1) VictoryGreen.copy(alpha = 0.35f) else DarkBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (matchesPlayed >= 1) Icons.Filled.Bolt else Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = if (matchesPlayed >= 1) VictoryGreen else FlameOrange,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (matchesPlayed >= 1) {
                                        "উইথড্র আনলকড (ম্যাচ খেলেছেন: $matchesPlayed টি)"
                                    } else {
                                        "প্রথম উইথড্র করতে কমপক্ষে ১টি ম্যাচ খেলা প্রয়োজন"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (matchesPlayed >= 1) VictoryGreen else TextSecondary
                                )
                            }
                        }

                        // Quick Deposit & Withdraw Request Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onDismiss()
                                    onOpenDepositDialog()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BkashPink, contentColor = Color.White),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("settings_deposit_request_btn")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ডিপোজিট রিকোয়েস্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = {
                                    onDismiss()
                                    onOpenWithdrawDialog()
                                },
                                border = BorderStroke(1.dp, FlameYellow),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FlameYellow),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("settings_withdraw_request_btn")
                            ) {
                                Icon(Icons.Filled.CallMade, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("উইথড্র রিকোয়েস্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                            }
                        }

                        // Switcher for Owner/Admin (My Requests vs Approval Queue)
                        if (isOwner || isAdmin) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurface)
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (requestSectionTab == 0) DarkElevated else Color.Transparent,
                                    border = if (requestSectionTab == 0) BorderStroke(1.dp, FlameYellow.copy(alpha = 0.5f)) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { requestSectionTab = 0 }
                                        .testTag("settings_my_requests_tab")
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "আমার রিকোয়েস্ট",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (requestSectionTab == 0) FlameYellow else TextSecondary
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (requestSectionTab == 1) BkashPink.copy(alpha = 0.25f) else Color.Transparent,
                                    border = if (requestSectionTab == 1) BorderStroke(1.dp, BkashPink) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { requestSectionTab = 1 }
                                        .testTag("settings_admin_queue_tab")
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "অ্যাপ্রুভাল কিউ ($pendingGlobalCount)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (requestSectionTab == 1) Color.White else TextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        if ((isOwner || isAdmin) && requestSectionTab == 1) {
                            // Admin/Owner inline approval queue inside Settings
                            DepositWithdrawQueueContent(
                                depositRequests = depositRequests,
                                withdrawRequests = withdrawRequests,
                                onApproveDeposit = onApproveDeposit,
                                onRejectDeposit = onRejectDeposit,
                                onApproveWithdraw = onApproveWithdraw,
                                onRejectWithdraw = onRejectWithdraw,
                                modifier = Modifier.testTag("settings_admin_requests_queue")
                            )
                        } else {
                            // User's Deposit & Withdraw Request History inside Settings
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("settings_my_requests_list"),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (userDeposits.isEmpty() && userWithdraws.isEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = DarkSurface,
                                        border = BorderStroke(1.dp, DarkBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "আপনার এখনো কোনো ডিপোজিট বা উইথড্র রিকোয়েস্ট নেই। উপরের বাটন থেকে নতুন রিকোয়েস্ট পাঠাতে পারেন।",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                } else {
                                    userDeposits.take(4).forEach { req ->
                                        SettingsRequestRow(
                                            title = "ডিপোজিট • ${req.paymentMethod.displayName} (+৳${req.amount})",
                                            subtitle = "TrxID: ${req.trxId} • প্রেরক: ${com.example.model.maskPhoneForPrivacy(req.senderNumber)}",
                                            status = req.status,
                                            note = req.adminNote
                                        )
                                    }
                                    userWithdraws.take(4).forEach { req ->
                                        SettingsRequestRow(
                                            title = "উইথড্র • ${req.paymentMethod.displayName} (-৳${req.amount})",
                                            subtitle = "প্রাপক: ${com.example.model.maskPhoneForPrivacy(req.targetNumber)}",
                                            status = req.status,
                                            note = req.adminNote
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Express Fast Match Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = FlameOrange.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(FlameOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "সুপারফাস্ট এক্সপ্রেস ম্যাচ মোড (৫:০০ - ৮:০০ PM)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = FlameYellow
                            )
                            Text(
                                text = "প্লেয়ারদের সুবিধার জন্য সকল টুর্নামেন্ট বিকাল ৫:০০ থেকে রাত ৮:০০ এর মধ্যে দ্রুততম সময়ে শেষ হয়!",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 1: Notification & Sound
                Text(
                    text = "নোটিফিকেশন ও অ্যালার্ট",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = FlameYellow
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        SettingSwitchRow(
                            icon = Icons.Filled.NotificationsActive,
                            title = "রুম আইডি ও পাসওয়ার্ড অ্যালার্ট",
                            subtitle = "অ্যাডমিন রুম দিলে সাথে সাথে সাউন্ড নোটিফিকেশন",
                            checked = userPreferences.roomAlertNotification,
                            onCheckedChange = {
                                onUpdateUserPreferences(
                                    it,
                                    userPreferences.fiveMinReminder,
                                    userPreferences.autoLaunchGame
                                )
                            }
                        )

                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                        SettingSwitchRow(
                            icon = Icons.Filled.Bolt,
                            title = "ম্যাচ শুরু হওয়ার ৫ মিনিট আগের অ্যালার্ট",
                            subtitle = "কাস্টম রুমে জয়েন করার আগাম রিমাইন্ডার",
                            checked = userPreferences.fiveMinReminder,
                            onCheckedChange = {
                                onUpdateUserPreferences(
                                    userPreferences.roomAlertNotification,
                                    it,
                                    userPreferences.autoLaunchGame
                                )
                            }
                        )

                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 8.dp))

                        SettingSwitchRow(
                            icon = Icons.Filled.SportsEsports,
                            title = "অটো ফ্রি ফায়ার ওপেন",
                            subtitle = "রুম আইডি কপি করার পর স্বয়ংক্রিয়ভাবে ফ্রি ফায়ার লঞ্চ",
                            checked = userPreferences.autoLaunchGame,
                            onCheckedChange = {
                                onUpdateUserPreferences(
                                    userPreferences.roomAlertNotification,
                                    userPreferences.fiveMinReminder,
                                    it
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (isOwner || isAdmin) {
                    Text(
                        text = "অ্যাডমিন ও ওনার কন্ট্রোল সেন্টার",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = FlameYellow
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkCard,
                        border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismiss()
                                onOpenAdminPanel()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(FlameOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = FlameYellow,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🛡️ TH অ্যাডমিন কন্ট্রোল প্যানেল",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "১-ট্যাপে রুম আইডি প্রকাশ, ব্যালেন্স ও রিকোয়েস্ট",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = FlameYellow,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    if (isOwner) {
                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkCard,
                            border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    onOpenOwnerPanel()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(FlameYellow.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👑", fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "👑 TH সুপ্রিম ওনার প্যানেল (মালিকানা)",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                                        color = FlameYellow
                                    )
                                    Text(
                                        text = "ব্যালেন্স, প্রতারক প্লেয়ার ব্যান ও নতুন অ্যাডমিন নিয়োগ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = FlameYellow,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                }

                // Section 3: Official Links & Helpline
                Text(
                    text = "হেল্পলাইন ও অফিশিয়াল লিংক",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = FlameYellow
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "৳",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = BkashPink
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "সুরক্ষিত পেমেন্ট গেটওয়ে",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = if (isOwner || isAdmin) officialBkashNumber else "TH-BKASH-GATEWAY",
                                        fontFamily = MonoFontFamily,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = BkashPink
                                    )
                                }
                            }
                            if (isOwner || isAdmin) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(officialBkashNumber))
                                        Toast.makeText(context, "বিকাশ নম্বর কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = BkashPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Phone,
                                    contentDescription = null,
                                    tint = VictoryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "অফিসিয়াল WhatsApp টুর্নামেন্ট লিঙ্ক",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                    Text(
                                        text = "ক্লিক করে লিঙ্ক কপি করুন",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = VictoryGreen
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(officialWhatsAppUrl))
                                    Toast.makeText(context, "WhatsApp গ্রুপ লিঙ্ক কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = VictoryGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 4: About App & Brand
                Text(
                    text = "অ্যাপ ও ব্র্যান্ড তথ্য",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = FlameYellow
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, NeonPink, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = getLogoDrawable(appCustomization.selectedLogo)),
                                    contentDescription = "TH Logo",
                                    modifier = Modifier.size(46.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = appCustomization.appName,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "ভার্সন: ২.৫.০ (অফিসিয়াল প্রো সংস্করণ)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = appCustomization.appTagline,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = FlameYellow
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val msg = onRefreshAppCache()
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("settings_refresh_cache_btn"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkElevated)
                        ) {
                            Icon(imageVector = Icons.Filled.CleaningServices, contentDescription = "Refresh App Cache", modifier = Modifier.size(16.dp), tint = TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("অ্যাপ ক্যাশে ও লাইভ ডাটা রিফ্রেশ করুন", fontSize = 12.sp, color = TextPrimary)
                        }

                        if (isOwner) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { showFreshResetConfirm = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("settings_owner_fresh_reset_btn"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.65f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFCA5A5))
                            ) {
                                Text("🔄 অ্যাপ ফ্রেশ রিসেট (ডাউনলোড স্টেট)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("সেটিংস বন্ধ করুন", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    if (showFreshResetConfirm) {
        AlertDialog(
            onDismissRequest = { showFreshResetConfirm = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "অ্যাপ ফ্রেশ রিসেট করবেন?",
                    fontWeight = FontWeight.Black,
                    color = FlameYellow
                )
            },
            text = {
                Text(
                    text = "এটি করলে সকল টেস্ট অ্যাকাউন্ট, বুকিং, ডিপোজিট/উইথড্র রিকোয়েস্ট ও ব্যালেন্স মুছে অ্যাপ সম্পূর্ণ নতুন অবস্থায় ফিরে যাবে।",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFreshResetConfirm = false
                        onDismiss()
                        onResetAppForFreshDownload()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("হ্যাঁ, রিসেট করুন", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFreshResetConfirm = false }) {
                    Text("বাতিল", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SettingsRequestRow(
    title: String,
    subtitle: String,
    status: RequestStatus,
    note: String
) {
    val statusColor = when (status) {
        RequestStatus.PENDING -> FlameYellow
        RequestStatus.APPROVED -> VictoryGreen
        RequestStatus.REJECTED -> Color(0xFFEF4444)
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                if (note.isNotBlank()) {
                    Text(
                        text = note,
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = statusColor.copy(alpha = 0.16f)
            ) {
                Text(
                    text = status.banglaLabel,
                    color = statusColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FlameYellow,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = DarkSurface
            )
        )
    }
}
