package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.ui.components.ForgotPasswordDialog
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppCustomization
import com.example.model.EmailAuthResult
import com.example.ui.components.getLogoDrawable
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.launch
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DisplayFontFamily
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.MonoFontFamily
import com.example.ui.theme.NeonPink
import com.example.ui.theme.PinkBlackHeaderGradient
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen

data class CaptchaChallenge(
    val prompt: String,
    val expectedAnswer: String,
    val displayBadge: String
)

private fun generateNewCaptcha(): CaptchaChallenge {
    val type = (0..2).random()
    return when (type) {
        0 -> {
            val a = (3..9).random()
            val b = (2..8).random()
            CaptchaChallenge(
                prompt = "$a + $b = ?",
                expectedAnswer = "${a + b}",
                displayBadge = "যোগফল নির্ণয় করুন: $a + $b"
            )
        }
        1 -> {
            val a = (10..18).random()
            val b = (2..7).random()
            CaptchaChallenge(
                prompt = "$a - $b = ?",
                expectedAnswer = "${a - b}",
                displayBadge = "বিয়োগফল নির্ণয় করুন: $a - $b"
            )
        }
        else -> {
            val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
            val code = (1..4).map { chars.random() }.joinToString("")
            CaptchaChallenge(
                prompt = code,
                expectedAnswer = code,
                displayBadge = "কোডটি লিখুন: $code"
            )
        }
    }
}

@Composable
fun AuthOnboardingScreen(
    onRegister: suspend (name: String, email: String, ign: String, uid: String, phone: String, bkash: String, password: String) -> EmailAuthResult,
    onLogin: suspend (email: String, pass: String) -> EmailAuthResult,
    onResendVerification: suspend (email: String, pass: String) -> EmailAuthResult = { _, _ -> EmailAuthResult(false, "") },
    onRequestOtp: (phoneOrUid: String) -> Pair<Boolean, String> = { Pair(false, "") },
    onVerifyOtp: (phoneOrUid: String, enteredOtp: String) -> Boolean = { _, _ -> false },
    onResetPassword: (phoneOrUid: String, newPass: String) -> Boolean = { _, _ -> false },
    appCustomization: AppCustomization = AppCustomization(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Create Account, 1 = Sign In
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Registration States
    var regName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regIgn by remember { mutableStateOf("") }
    var regUid by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }

    // Login States (Email + Password only)
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var needsEmailVerification by remember { mutableStateOf(false) }
    var authStatusBanner by remember { mutableStateOf<String?>(null) }
    var isAuthLoading by remember { mutableStateOf(false) }

    // Captcha States
    var currentCaptcha by remember { mutableStateOf(generateNewCaptcha()) }
    var captchaInput by remember { mutableStateOf("") }
    var captchaError by remember { mutableStateOf<String?>(null) }
    var isCaptchaSolved by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PinkBlackHeaderGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // App Logo with serene layered aura ring
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                FlameYellow.copy(alpha = 0.28f),
                                FlameCyan.copy(alpha = 0.22f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(
                        2.dp,
                        Brush.linearGradient(listOf(FlameOrange, FlameYellow, FlameCyan)),
                        CircleShape
                    )
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getLogoDrawable(appCustomization.selectedLogo)),
                    contentDescription = "TH Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(82.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Brand Name & Arena Tag
            Text(
                text = appCustomization.appName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = DisplayFontFamily,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                ),
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FlameCyan.copy(alpha = 0.14f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameCyan.copy(alpha = 0.65f))
                ) {
                    Text(
                        text = "BY TH • ESPORTS",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlameYellow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = appCustomization.appTagline,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Clean Trust Pillars Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚡ অটো রিফান্ড", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FlameYellow)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("ম্যাচ ক্যান্সেলে দ্রুত", fontSize = 9.sp, color = TextSecondary, textAlign = TextAlign.Center)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BkashPink.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎡 ফ্রি স্পিন", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = FlameYellow)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("৩-৩০৳ ক্যাশ প্রাইজ", fontSize = 9.sp, color = TextSecondary, textAlign = TextAlign.Center)
                    }
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🛡️ ১০০% সেফ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = VictoryGreen)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("বিকাশ-নগদ ভেরিফাইড", fontSize = 9.sp, color = TextSecondary, textAlign = TextAlign.Center)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Auth Card (Calm Luxury Dark Surface)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_onboarding_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(
                            FlameYellow.copy(alpha = 0.45f),
                            DarkBorder,
                            NeonPink.copy(alpha = 0.45f)
                        )
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Sleek Pill Switcher Tabs
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedTab == 0) DarkElevated else Color.Transparent,
                                border = if (selectedTab == 0) androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.65f)) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedTab = 0
                                        captchaInput = ""
                                        captchaError = null
                                        isCaptchaSolved = false
                                        currentCaptcha = generateNewCaptcha()
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 11.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "নতুন অ্যাকাউন্ট",
                                        fontWeight = if (selectedTab == 0) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (selectedTab == 0) FlameYellow else TextSecondary
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedTab == 1) DarkElevated else Color.Transparent,
                                border = if (selectedTab == 1) androidx.compose.foundation.BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.65f)) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedTab = 1
                                        captchaInput = ""
                                        captchaError = null
                                        isCaptchaSolved = false
                                        currentCaptcha = generateNewCaptcha()
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 11.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "সাইন ইন",
                                        fontWeight = if (selectedTab == 1) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (selectedTab == 1) VictoryGreen else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTab == 0) {
                        // Registration Form
                        Text(
                            text = "খেলোয়াড় অ্যাকাউন্ট নিবন্ধন করুন",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "অ্যাকাউন্ট খুললেই পাচ্ছেন ফ্রি ৳৫ ওয়েলকাম বোনাস!",
                            fontSize = 11.sp,
                            color = VictoryGreen
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Full Name
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("আপনার পুরো নাম") },
                            placeholder = { Text("যেমন: Raistar") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = FlameYellow) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FlameYellow,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Email
                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("ইমেইল (Email)") },
                            placeholder = { Text("example@gmail.com") },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = FlameCyan) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FlameCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Phone Number
                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("বিকাশ মোবাইল নম্বর") },
                            placeholder = { Text("018XXXXXXXX") },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = BkashPink) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BkashPink,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Free Fire IGN & UID in a Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = regIgn,
                                onValueChange = { regIgn = it },
                                label = { Text("গেমের নাম (IGN)") },
                                placeholder = { Text("FF★Killer") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reg_ign_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            OutlinedTextField(
                                value = regUid,
                                onValueChange = { regUid = it },
                                label = { Text("FF UID") },
                                placeholder = { Text("284710924") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("reg_uid_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("পাসওয়ার্ড") },
                            placeholder = { Text("পাসওয়ার্ড দিন") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = FlameOrange) },
                            trailingIcon = {
                                IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                                    Icon(
                                        imageVector = if (regPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            visualTransformation = if (regPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FlameOrange,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                    } else {
                        // Sign In Form (Email + Password only)
                        Text(
                            text = "আপনার অ্যাকাউন্টে সাইন ইন করুন",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "ইমেইল ও পাসওয়ার্ড দিয়ে টুর্নামেন্টে প্রবেশ করুন",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Login Email
                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = {
                                loginEmail = it
                                loginError = null
                            },
                            label = { Text("ইমেইল (Email)") },
                            placeholder = { Text("example@gmail.com") },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = FlameCyan) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FlameCyan,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Login Password
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = {
                                loginPassword = it
                                loginError = null
                            },
                            label = { Text("পাসওয়ার্ড") },
                            placeholder = { Text("আপনার পাসওয়ার্ড") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = FlameOrange) },
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            },
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FlameOrange,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Forgot Password button row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showForgotPasswordDialog = true },
                                modifier = Modifier.testTag("forgot_password_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LockReset,
                                    contentDescription = null,
                                    tint = FlameYellow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "পাসওয়ার্ড ভুলে গেছেন? (Forgot Password)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameYellow
                                )
                            }
                        }

                        authStatusBanner?.let { statusMsg ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0A281E),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VictoryGreen),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 6.dp)
                                    .testTag("auth_verification_sent_banner")
                            ) {
                                Text(
                                    text = statusMsg,
                                    color = VictoryGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        loginError?.let { err ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF331414),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .testTag("login_error_banner")
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = err,
                                        color = Color(0xFFFCA5A5),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (needsEmailVerification) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedButton(
                                            enabled = !isAuthLoading,
                                            onClick = {
                                                val trimmedEmail = loginEmail.trim()
                                                val trimmedPass = loginPassword.trim()
                                                coroutineScope.launch {
                                                    isAuthLoading = true
                                                    val resendRes = onResendVerification(trimmedEmail, trimmedPass)
                                                    isAuthLoading = false
                                                    if (resendRes.success) {
                                                        authStatusBanner = resendRes.message
                                                    } else {
                                                        loginError = resendRes.message
                                                    }
                                                }
                                            },
                                            border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("resend_verification_btn")
                                        ) {
                                            Text(
                                                text = "Resend Verification Email",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FlameYellow
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Real Captcha Verification Box (রোবট প্রতিরোধ ও হিউম্যান যাচাইকরণ)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF131924),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCaptchaSolved) VictoryGreen else DarkBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Security,
                                        contentDescription = "Security Captcha",
                                        tint = if (isCaptchaSolved) VictoryGreen else FlameYellow,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "নিরাপত্তা ক্যাপচা যাচাই (Security Captcha)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (isCaptchaSolved) VictoryGreen else FlameYellow
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        currentCaptcha = generateNewCaptcha()
                                        captchaInput = ""
                                        captchaError = null
                                        isCaptchaSolved = false
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "Refresh Captcha",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Captcha Puzzle Display Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1F2B3E),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, FlameCyan),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = currentCaptcha.prompt,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 3.sp,
                                            color = FlameYellow
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = captchaInput,
                                    onValueChange = {
                                        captchaInput = it.trim()
                                        captchaError = null
                                        isCaptchaSolved = (captchaInput.equals(currentCaptcha.expectedAnswer, ignoreCase = true))
                                    },
                                    placeholder = { Text("উত্তর লিখুন") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .testTag("captcha_input_field"),
                                    trailingIcon = {
                                        if (isCaptchaSolved) {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = "Verified",
                                                tint = VictoryGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = if (isCaptchaSolved) VictoryGreen else FlameYellow,
                                        unfocusedBorderColor = DarkBorder,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }

                            if (captchaError != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = captchaError!!,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 11.sp
                                )
                            } else if (isCaptchaSolved) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "✔ ক্যাপচা ভেরিফাইড (আপনি হিউম্যান প্লেয়ার)",
                                    color = VictoryGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Main Submit Button
                    Button(
                        enabled = !isAuthLoading,
                        onClick = {
                            if (!isCaptchaSolved) {
                                captchaError = "দয়া করে ক্যাপচার সঠিক উত্তর দিন!"
                                Toast.makeText(context, "ক্যাপচা সমাধান করুন", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (selectedTab == 0) {
                                // Register with Firebase Email Verification
                                val trimmedEmail = regEmail.trim()
                                if (regName.isBlank() || trimmedEmail.isBlank() || regPhone.isBlank() || regIgn.isBlank()) {
                                    Toast.makeText(context, "নাম, ইমেইল, নম্বর ও IGN পূরণ করুন!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
                                    Toast.makeText(context, "সঠিক ইমেইল অ্যাড্রেস লিখুন!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (regPassword.trim().length < 6) {
                                    Toast.makeText(context, "কমপক্ষে ৬ অক্ষরের পাসওয়ার্ড দিন!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                coroutineScope.launch {
                                    isAuthLoading = true
                                    val result = onRegister(
                                        regName.trim(),
                                        trimmedEmail,
                                        regIgn.trim(),
                                        regUid.trim().ifEmpty { "1002345" },
                                        regPhone.trim(),
                                        regPhone.trim(),
                                        regPassword.trim()
                                    )
                                    isAuthLoading = false
                                    if (result.success) {
                                        loginEmail = trimmedEmail
                                        loginPassword = regPassword.trim()
                                        loginError = null
                                        needsEmailVerification = true
                                        authStatusBanner = "Verification email sent to $trimmedEmail. Please verify your email before login."
                                        selectedTab = 1
                                        currentCaptcha = generateNewCaptcha()
                                        captchaInput = ""
                                        isCaptchaSolved = false
                                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                // Email + Password Login with Firebase Email Verification
                                val trimmedEmail = loginEmail.trim()
                                if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
                                    loginError = "দয়া করে আপনার নিবন্ধিত ইমেইল অ্যাড্রেস লিখুন!"
                                    needsEmailVerification = false
                                    Toast.makeText(context, "সঠিক ইমেইল লিখুন!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (loginPassword.isBlank()) {
                                    loginError = "দয়া করে পাসওয়ার্ড লিখুন!"
                                    needsEmailVerification = false
                                    Toast.makeText(context, "পাসওয়ার্ড লিখুন!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                coroutineScope.launch {
                                    isAuthLoading = true
                                    val result = onLogin(trimmedEmail, loginPassword.trim())
                                    isAuthLoading = false
                                    if (result.success) {
                                        loginError = null
                                        needsEmailVerification = false
                                        authStatusBanner = null
                                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                    } else {
                                        loginError = result.message
                                        needsEmailVerification = result.needsVerification
                                        Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == 0) FlameYellow else VictoryGreen
                        )
                    ) {
                        Text(
                            text = if (isAuthLoading) {
                                "অপেক্ষা করুন..."
                            } else if (selectedTab == 0) {
                                "অ্যাকাউন্ট খুলুন ও ইমেইল ভেরিফাই করুন"
                            } else {
                                "সাইন ইন করুন"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer assurance
            Text(
                text = "TRUSTED TOURNAMENT BY TH • কপিরাইট ২০২৬\nবিকাশ সিকিউর পেমেন্ট ও অটো-রিফান্ড সিস্টেম সমর্থিত",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            ForgotPasswordDialog(
                initialPhoneOrUid = loginEmail,
                onDismiss = { showForgotPasswordDialog = false },
                onRequestOtp = onRequestOtp,
                onVerifyOtp = onVerifyOtp,
                onResetPassword = onResetPassword,
                onPasswordResetSuccess = { resetIdentifier, resetPass ->
                    loginEmail = resetIdentifier
                    loginPassword = resetPass
                    loginError = null
                    showForgotPasswordDialog = false
                    selectedTab = 1 // Switch to Sign In tab
                }
            )
        }
    }
}
