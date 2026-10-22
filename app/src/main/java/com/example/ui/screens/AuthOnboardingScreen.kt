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
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
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
    onRegister: (name: String, ign: String, uid: String, phone: String, bkash: String) -> Boolean,
    onLogin: (phoneOrEmail: String, pass: String) -> Boolean,
    onDemoOwnerLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Create Account, 1 = Sign In

    // Registration States
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regIgn by remember { mutableStateOf("") }
    var regUid by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }

    // Login States
    var loginPhone by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Captcha States
    var currentCaptcha by remember { mutableStateOf(generateNewCaptcha()) }
    var captchaInput by remember { mutableStateOf("") }
    var captchaError by remember { mutableStateOf<String?>(null) }
    var isCaptchaSolved by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // App Logo with glowing border
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(FlameYellow.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )
                    .border(2.5.dp, FlameYellow, CircleShape)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.th_logo),
                    contentDescription = "TH Logo",
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // App Brand Name & Slogan
            Text(
                text = "TRUSTED TOURNAMENT",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = FlameYellow.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow)
                ) {
                    Text(
                        text = "BY TH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = FlameYellow,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "বাংলাদেশের বিশ্বস্ত ফ্রি ফায়ার টুর্নামেন্ট",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Highlight feature pills row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF141A28),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("⚡ অটো রিফান্ড", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FlameCyan)
                        Text("ওনার ক্যান্সেলে সাথে সাথে", fontSize = 8.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF281E10),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎡 ফ্রি স্পিন", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = FlameYellow)
                        Text("৩-৩০৳ ক্যাশ প্রাইজ", fontSize = 8.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10281C),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VictoryGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🛡️ ১০০% সেফ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = VictoryGreen)
                        Text("বিকাশ সুরক্ষিত গেটওয়ে", fontSize = 8.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Auth Card (Discord-like clean modern gaming card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_onboarding_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Discord-inspired Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = DarkCard,
                        contentColor = FlameYellow,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = FlameYellow,
                                height = 3.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = {
                                selectedTab = 0
                                captchaInput = ""
                                captchaError = null
                                isCaptchaSolved = false
                                currentCaptcha = generateNewCaptcha()
                            },
                            text = {
                                Text(
                                    text = "নতুন অ্যাকাউন্ট",
                                    fontWeight = if (selectedTab == 0) FontWeight.Black else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 0) FlameYellow else Color.White.copy(alpha = 0.7f)
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                captchaInput = ""
                                captchaError = null
                                isCaptchaSolved = false
                                currentCaptcha = generateNewCaptcha()
                            },
                            text = {
                                Text(
                                    text = "সাইন ইন",
                                    fontWeight = if (selectedTab == 1) FontWeight.Black else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == 1) FlameYellow else Color.White.copy(alpha = 0.7f)
                                )
                            }
                        )
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
                            text = "অ্যাকাউন্ট খুললেই পাচ্ছেন ফ্রি ৳১০ ওয়েলকাম বোনাস!",
                            fontSize = 11.sp,
                            color = VictoryGreen
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Full Name
                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("আপনার পুরো নাম") },
                            placeholder = { Text("যেমন: সাকিব আল হাসান") },
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
                        // Sign In Form
                        Text(
                            text = "আপনার অ্যাকাউন্টে সাইন ইন করুন",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "মোবাইল নম্বর ও পাসওয়ার্ড দিয়ে টুর্নামেন্টে প্রবেশ করুন",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Login Phone
                        OutlinedTextField(
                            value = loginPhone,
                            onValueChange = { loginPhone = it },
                            label = { Text("মোবাইল / বিকাশ নম্বর") },
                            placeholder = { Text("018XXXXXXXX বা ইমেইল") },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = BkashPink) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BkashPink,
                                unfocusedBorderColor = DarkBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Login Password
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
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
                    }

                    Spacer(modifier = Modifier.height(14.dp))

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
                        onClick = {
                            if (!isCaptchaSolved) {
                                captchaError = "❌ দয়া করে ক্যাপচার সঠিক উত্তর দিন!"
                                Toast.makeText(context, "ক্যাপচা সমাধান করুন", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (selectedTab == 0) {
                                // Register
                                if (regName.isBlank() || regPhone.isBlank() || regIgn.isBlank()) {
                                    Toast.makeText(context, "সকল তথ্য পূরণ করুন!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val ok = onRegister(regName.trim(), regIgn.trim(), regUid.trim().ifEmpty { "1002345" }, regPhone.trim(), regPhone.trim())
                                if (ok) {
                                    Toast.makeText(context, "🎉 রেজিস্ট্রেশন সফল! স্বাগতম $regName", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                // Login
                                if (loginPhone.isBlank()) {
                                    Toast.makeText(context, "মোবাইল নম্বর লিখুন!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val ok = onLogin(loginPhone.trim(), loginPassword.trim())
                                if (ok) {
                                    Toast.makeText(context, "সফলভাবে লগইন হয়েছে!", Toast.LENGTH_SHORT).show()
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
                            text = if (selectedTab == 0) "🚀 অ্যাকাউন্ট খুলুন (৳১০ ফ্রি বোনাস)" else "🔑 সাইন ইন করুন",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1-Tap Supreme Owner Demo Login Button
                    OutlinedButton(
                        onClick = onDemoOwnerLogin,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("demo_owner_login_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FlameYellow),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FlameYellow.copy(alpha = 0.6f))
                    ) {
                        Text("👑", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "১-ট্যাপ ওনার লগইন (তানভির হাসান)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = FlameYellow
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
    }
}
