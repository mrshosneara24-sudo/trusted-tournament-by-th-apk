package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DepositRequest
import com.example.model.PromoCodeItem
import com.example.model.RequestStatus
import com.example.model.TransactionCategory
import com.example.model.WalletTransaction
import com.example.model.WithdrawRequest
import com.example.ui.components.PaymentReceiptDialog
import com.example.ui.components.PromoCodeRedeemCard
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen
import com.example.ui.theme.WalletHeroBrush

@Composable
fun WalletScreen(
    balance: Int,
    transactions: List<WalletTransaction>,
    onAddMoneyClick: () -> Unit,
    onWithdrawClick: () -> Unit,
    matchesPlayed: Int = 0,
    depositRequests: List<DepositRequest> = emptyList(),
    withdrawRequests: List<WithdrawRequest> = emptyList(),
    currentUserPhone: String = "",
    promoCodes: List<PromoCodeItem> = emptyList(),
    onRedeemPromoCode: (String) -> Pair<Boolean, String> = { false to "" },
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(TransactionCategory.ALL) }
    var selectedReceiptTx by remember { mutableStateOf<WalletTransaction?>(null) }

    val userTransactions = remember(transactions, currentUserPhone) {
        if (currentUserPhone.isBlank()) transactions
        else transactions.filter { it.userPhone.isBlank() || it.userPhone == currentUserPhone }
    }
    val userDeposits = remember(depositRequests, currentUserPhone) {
        if (currentUserPhone.isBlank()) depositRequests
        else depositRequests.filter { it.userPhone.isBlank() || it.userPhone == currentUserPhone }
    }
    val userWithdraws = remember(withdrawRequests, currentUserPhone) {
        if (currentUserPhone.isBlank()) withdrawRequests
        else withdrawRequests.filter { it.userPhone.isBlank() || it.userPhone == currentUserPhone }
    }

    val filteredTransactions = remember(userTransactions, selectedCategory) {
        userTransactions.filter {
            selectedCategory == TransactionCategory.ALL || it.category == selectedCategory
        }
    }

    val isWithdrawUnlocked = matchesPlayed >= 1

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(
                            FlameYellow.copy(alpha = 0.65f),
                            NeonPink.copy(alpha = 0.55f),
                            DarkBorder
                        )
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WalletHeroBrush)
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(BkashPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Payment,
                                        contentDescription = "bKash",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "বিকাশ ও নগদ ওয়ালেট",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = VictoryGreen.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = "● সক্রিয়",
                                    color = VictoryGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "৳$balance",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = MonoFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 40.sp,
                                letterSpacing = (-0.5).sp
                            ),
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "টুর্নামেন্টের উইনিং মানি এবং ডিপোজিট ব্যালেন্স",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Career Withdraw Status Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isWithdrawUnlocked) VictoryGreen.copy(alpha = 0.16f) else Color(0xFF2B0E0A),
                            border = BorderStroke(1.dp, if (isWithdrawUnlocked) VictoryGreen.copy(alpha = 0.6f) else NeonPink.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wallet_withdraw_eligibility_pill")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isWithdrawUnlocked) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                                    contentDescription = null,
                                    tint = if (isWithdrawUnlocked) VictoryGreen else NeonPink,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isWithdrawUnlocked) {
                                        "✅ উইথড্র আনলকড • ক্যারিয়ার ম্যাচ: $matchesPlayed টি"
                                    } else {
                                        "🔒 ১টি ম্যাচ খেললে প্রথম উইথড্র আনলক হবে (খেলেছেন: ০ টি)"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isWithdrawUnlocked) VictoryGreen else Color.White
                                )
                            }
                        }

                        // Quick Pending Stats Row if any deposit/withdraw requests exist
                        val pendingDeposits = userDeposits.count { it.status == RequestStatus.PENDING }
                        val pendingWithdraws = userWithdraws.count { it.status == RequestStatus.PENDING }
                        if (pendingDeposits > 0 || pendingWithdraws > 0) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkCard,
                                    border = BorderStroke(1.dp, BkashPink.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "📥 পেন্ডিং ডিপোজিট: $pendingDeposits",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FlameYellow,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkCard,
                                    border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.45f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "📤 পেন্ডিং উইথড্র: $pendingWithdraws",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FlameOrange,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: Deposit & Withdraw
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onAddMoneyClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("wallet_add_money_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ডিপোজিট করুন", fontWeight = FontWeight.Black)
                            }

                            Button(
                                onClick = onWithdrawClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("wallet_withdraw_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = FlameYellow,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.CallMade, contentDescription = "Withdraw", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ক্যাশআউট", fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        // Promo / Gift Redeem Card
        item {
            PromoCodeRedeemCard(
                activePromoCount = promoCodes.count { it.active && !it.isExhausted },
                onRedeemCode = onRedeemPromoCode
            )
        }

        // Deposit & Withdraw System Guide Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_system_guide_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, FlameYellow.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = FlameYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ডিপোজিট ও উইথড্র সিস্টেম গাইড",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Black),
                            color = FlameYellow
                        )
                    }
                    Text(
                        text = "• ডিপোজিট: বিকাশ/নগদ সেন্ড মানি করে TrxID সাবমিট করুন। ওনার/অ্যাডমিন যাচাই করে অ্যাপ্রুভ করলেই ওয়ালেটে টাকা যোগ হবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "• ম্যাচ জয়েন: ওয়ালেট ব্যালেন্স দিয়েও সরাসরি টুর্নামেন্ট স্লট কেনা যাবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    Text(
                        text = "• প্রথম উইথড্র নিয়ম: প্রথমবার ক্যাশআউট করতে পুরো অ্যাপ ক্যারিয়ারে কমপক্ষে ১টি ম্যাচ খেলা বাধ্যতামূলক।",
                        style = MaterialTheme.typography.bodySmall,
                        color = FlameOrange,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Live Deposit & Withdraw Requests Status
        if (userDeposits.isNotEmpty() || userWithdraws.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "চলমান ও সাম্প্রতিক ডিপোজিট / উইথড্র রিকোয়েস্ট",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    userDeposits.take(5).forEach { req ->
                        val statusColor = when (req.status) {
                            RequestStatus.PENDING -> FlameYellow
                            RequestStatus.APPROVED -> VictoryGreen
                            RequestStatus.REJECTED -> Color(0xFFEF4444)
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "ডিপোজিট • ${req.paymentMethod.displayName} (৳${req.amount})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "TrxID: ${req.trxId} • নম্বর: ${req.senderNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    if (req.adminNote.isNotBlank()) {
                                        Text(
                                            text = req.adminNote,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = statusColor
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = statusColor.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = req.status.banglaLabel,
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    userWithdraws.take(5).forEach { req ->
                        val statusColor = when (req.status) {
                            RequestStatus.PENDING -> FlameYellow
                            RequestStatus.APPROVED -> VictoryGreen
                            RequestStatus.REJECTED -> Color(0xFFEF4444)
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.45f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "উইথড্র • ${req.paymentMethod.displayName} (৳${req.amount})",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "প্রাপক নম্বর: ${req.targetNumber}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    if (req.adminNote.isNotBlank()) {
                                        Text(
                                            text = req.adminNote,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = statusColor
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = statusColor.copy(alpha = 0.18f)
                                ) {
                                    Text(
                                        text = req.status.banglaLabel,
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dedicated Transaction History Header
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.ReceiptLong,
                            contentDescription = "History",
                            tint = FlameYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "পেমেন্ট ও লেনদেনের ইতিহাস",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Text(
                        text = "${filteredTransactions.size} টি রেকর্ড",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(TransactionCategory.entries, key = { it.name }) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.banglaTitle, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (cat == TransactionCategory.SLOT_PURCHASE) BkashPink else MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurface,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) FlameYellow else DarkBorder
                            )
                        )
                    }
                }
            }
        }

        // Transactions & Slot Payment Records List
        if (filteredTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "এই ক্যাটাগরিতে কোনো লেনদেনের রেকর্ড নেই",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedReceiptTx = tx }
                        .testTag("tx_card_${tx.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (tx.category == TransactionCategory.SLOT_PURCHASE) Color(0xFF1C1322) else DarkSurface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (tx.category == TransactionCategory.SLOT_PURCHASE) BkashPink.copy(alpha = 0.35f) else DarkBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (tx.category == TransactionCategory.SLOT_PURCHASE) BkashPink.copy(alpha = 0.2f)
                                            else if (tx.isCredit) VictoryGreen.copy(alpha = 0.2f)
                                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (tx.category == TransactionCategory.SLOT_PURCHASE) Icons.Filled.SportsEsports
                                        else if (tx.isCredit) Icons.Filled.CallReceived
                                        else Icons.Filled.CallMade,
                                        contentDescription = "Type",
                                        tint = if (tx.category == TransactionCategory.SLOT_PURCHASE) BkashPink
                                        else if (tx.isCredit) VictoryGreen
                                        else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = tx.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = tx.exactDateTime,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Text(
                                text = "${if (tx.isCredit) "+" else "-"}৳${tx.amount}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = if (tx.isCredit) VictoryGreen else BkashPink
                                )
                            )
                        }

                        if (tx.category == TransactionCategory.SLOT_PURCHASE) {
                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (tx.slotNumber != null) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = MaterialTheme.colorScheme.primary
                                            ) {
                                                Text(
                                                    text = "স্লট #${tx.slotNumber}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }

                                        if (tx.trxId != null) {
                                            Text(
                                                text = "TrxID: ${tx.trxId}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = FlameYellow
                                            )
                                        }
                                    }

                                    Text(
                                        text = "রসিদ দেখুন >",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BkashPink
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = tx.paymentMethod,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = tx.statusText,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = FlameYellow
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedReceiptTx?.let { tx ->
        PaymentReceiptDialog(
            transaction = tx,
            onDismiss = { selectedReceiptTx = null }
        )
    }
}
