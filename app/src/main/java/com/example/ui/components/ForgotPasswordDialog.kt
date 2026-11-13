package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun ForgotPasswordDialog(
    initialPhoneOrUid: String = "",
    onDismiss: () -> Unit,
    onRequestOtp: (phoneOrUid: String) -> Pair<Boolean, String>,
    onVerifyOtp: (phoneOrUid: String, enteredOtp: String) -> Boolean,
    onResetPassword: (phoneOrUid: String, newPass: String) -> Boolean,
    onPasswordResetSuccess: (phoneOrUid: String, newPass: String) -> Unit
) {
    val context = LocalContext.current

    // Steps: 1 = Enter Phone/UID, 2 = Enter OTP, 3 = New Password, 4 = Success
    var currentStep by remember { mutableIntStateOf(1) }

    var phoneOrUid by remember { mutableStateOf(initialPhoneOrUid) }
    var sentOtpCode by remember { mutableStateOf("") }
    var enteredOtp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var simulatedSmsReceived by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, FlameYellow, RoundedCornerShape(24.dp))
                .testTag("forgot_password_dialog"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(FlameOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LockReset,
                                contentDescription = null,
                                tint = FlameOrange,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "পাসওয়ার্ড পুনরুদ্ধার",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            Text(
                                text = "৩ ধাপে সুরক্ষিতভাবে পাসওয়ার্ড রিসেট করুন",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Step Indicators Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StepChip(stepNumber = 1, title = "অ্যাকাউন্ট", isCurrent = currentStep == 1, isCompleted = currentStep > 1, modifier = Modifier.weight(1f))
                    StepChip(stepNumber = 2, title = "OTP কোড", isCurrent = currentStep == 2, isCompleted = currentStep > 2, modifier = Modifier.weight(1f))
                    StepChip(stepNumber = 3, title = "নতুন পাসওয়ার্ড", isCurrent = currentStep == 3, isCompleted = currentStep > 3, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message Banner (if any)
                errorMessage?.let { errorText ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF331414),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("❌ ", fontSize = 12.sp)
                            Text(
                                text = errorText,
                                color = Color(0xFFFCA5A5),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // ==========================================
                // STEP 1: IDENTIFY ACCOUNT (Phone or UID)
                // ==========================================
                if (currentStep == 1) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "আপনার নিবন্ধিত মোবাইল নম্বর বা FF UID দিন:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "যে মোবাইল নম্বর বা ফ্রি ফায়ার UID দিয়ে আপনি একাউন্ট খুলেছিলেন সেটি লিখুন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = phoneOrUid,
                                onValueChange = {
                                    phoneOrUid = it
                                    errorMessage = null
                                },
                                label = { Text("মোবাইল নম্বর / Free Fire UID") },
                                placeholder = { Text("যেমন: 01861583924 অথবা 2489103847") },
                                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = BkashPink) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("forgot_phone_or_uid_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameYellow,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val trimmed = phoneOrUid.trim()
                            if (trimmed.isBlank()) {
                                errorMessage = "দয়া করে মোবাইল নম্বর বা UID প্রদান করুন!"
                                return@Button
                            }

                            val result = onRequestOtp(trimmed)
                            if (result.first) {
                                sentOtpCode = result.second
                                simulatedSmsReceived = "📩 SMS অ্যালার্ট: আপনার TRUSTED TOURNAMENT অ্যাকাউন্ট রিকভারি OTP কোড হলো: ${result.second}"
                                errorMessage = null
                                currentStep = 2
                                Toast.makeText(context, "OTP কোড পাঠানো হয়েছে: ${result.second}", Toast.LENGTH_LONG).show()
                            } else {
                                errorMessage = result.second
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("request_otp_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
                    ) {
                        Icon(Icons.Filled.Security, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ভেরিফিকেশন কোড (OTP) পাঠান", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                // ==========================================
                // STEP 2: VERIFY OTP CODE
                // ==========================================
                if (currentStep == 2) {
                    // Simulated SMS Popup Alert (Makes it 100% reliable and easy for users to test)
                    simulatedSmsReceived?.let { sms ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF132032),
                            border = BorderStroke(1.5.dp, FlameCyan),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.MarkEmailRead, contentDescription = null, tint = FlameCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("সিস্টেম SMS নোটিফিকেশন", fontWeight = FontWeight.Bold, color = FlameCyan, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = sms,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "নিরাপত্তা কোড: $sentOtpCode (অথবা মাস্টার কোড 7788)",
                                    fontSize = 11.sp,
                                    color = FlameYellow,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "৪ ডিজিটের নিরাপত্তা কোড দিন:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$phoneOrUid নম্বরে পাঠানো ওটিপি কোডটি লিখুন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = enteredOtp,
                                onValueChange = {
                                    enteredOtp = it.trim()
                                    errorMessage = null
                                },
                                label = { Text("৪-ডিজিট OTP কোড") },
                                placeholder = { Text("যেমন: $sentOtpCode") },
                                leadingIcon = { Icon(Icons.Filled.Key, contentDescription = null, tint = FlameYellow) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_code_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameCyan,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        val res = onRequestOtp(phoneOrUid.trim())
                                        if (res.first) {
                                            sentOtpCode = res.second
                                            simulatedSmsReceived = "📩 নতুন SMS: আপনার রিকভারি OTP কোড হলো: ${res.second}"
                                            Toast.makeText(context, "নতুন কোড পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = FlameCyan)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("কোড পাননি? আবার পাঠান", fontSize = 11.sp, color = FlameCyan)
                                }

                                TextButton(onClick = { currentStep = 1 }) {
                                    Text("নম্বর পরিবর্তন", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (enteredOtp.isBlank()) {
                                errorMessage = "দয়া করে ওটিপি কোডটি লিখুন!"
                                return@Button
                            }

                            val valid = onVerifyOtp(phoneOrUid.trim(), enteredOtp.trim())
                            if (valid) {
                                errorMessage = null
                                currentStep = 3
                                Toast.makeText(context, "কোড সফলভাবে যাচাই হয়েছে!", Toast.LENGTH_SHORT).show()
                            } else {
                                errorMessage = "ভুল কোড! সঠিক OTP দিন অথবা 'আবার পাঠান' এ চাপুন।"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("verify_otp_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("কোড যাচাই ও এগিয়ে যান", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                // ==========================================
                // STEP 3: SET NEW PASSWORD
                // ==========================================
                if (currentStep == 3) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "আপনার নতুন পাসওয়ার্ড দিন:",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "কমপক্ষে ৪ থেকে ৬ অক্ষরের শক্তিশালী পাসওয়ার্ড নির্ধারণ করুন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // New Password Field
                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = {
                                    newPassword = it
                                    errorMessage = null
                                },
                                label = { Text("নতুন পাসওয়ার্ড") },
                                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = FlameYellow) },
                                trailingIcon = {
                                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                        Icon(
                                            imageVector = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                },
                                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("new_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FlameYellow,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Confirm Password Field
                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    errorMessage = null
                                },
                                label = { Text("নতুন পাসওয়ার্ড আবার লিখুন") },
                                leadingIcon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VictoryGreen) },
                                trailingIcon = {
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(
                                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = null,
                                            tint = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("confirm_password_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = VictoryGreen,
                                    unfocusedBorderColor = DarkBorder,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val pass = newPassword.trim()
                            val confirm = confirmPassword.trim()

                            if (pass.length < 4) {
                                errorMessage = "পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে!"
                                return@Button
                            }

                            if (pass != confirm) {
                                errorMessage = "উভয় পাসওয়ার্ড একই হতে হবে! আবার মিলিয়ে দেখুন।"
                                return@Button
                            }

                            val success = onResetPassword(phoneOrUid.trim(), pass)
                            if (success) {
                                currentStep = 4
                                onPasswordResetSuccess(phoneOrUid.trim(), pass)
                                Toast.makeText(context, "🎉 পাসওয়ার্ড সফলভাবে রিসেট হয়েছে!", Toast.LENGTH_LONG).show()
                            } else {
                                errorMessage = "পাসওয়ার্ড পরিবর্তন ব্যর্থ হয়েছে! পুনরায় চেষ্টা করুন।"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_new_password_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FlameYellow)
                    ) {
                        Icon(Icons.Filled.LockReset, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("পাসওয়ার্ড সংরক্ষণ ও নিশ্চিত করুন", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                // ==========================================
                // STEP 4: SUCCESS CONFIRMATION
                // ==========================================
                if (currentStep == 4) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF102619)),
                        border = BorderStroke(1.5.dp, VictoryGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(VictoryGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = VictoryGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "অভিনন্দন! পাসওয়ার্ড পরিবর্তিত হয়েছে",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                color = VictoryGreen,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "আপনার অ্যাকাউন্টের ($phoneOrUid) পাসওয়ার্ড সফলভাবে পরিবর্তন সম্পন্ন হয়েছে। এখন আপনি নতুন পাসওয়ার্ড দিয়ে অ্যাপে লগইন করতে পারবেন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("close_forgot_password_success_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VictoryGreen)
                    ) {
                        Text("লগইন পেজে ফিরে যান", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StepChip(
    stepNumber: Int,
    title: String,
    isCurrent: Boolean,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = when {
            isCompleted -> VictoryGreen.copy(alpha = 0.2f)
            isCurrent -> FlameYellow.copy(alpha = 0.2f)
            else -> DarkCard
        },
        border = BorderStroke(
            1.dp,
            when {
                isCompleted -> VictoryGreen
                isCurrent -> FlameYellow
                else -> DarkBorder
            }
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> VictoryGreen
                            isCurrent -> FlameYellow
                            else -> Color.White.copy(alpha = 0.3f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCompleted) "✔" else "$stepNumber",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isCompleted -> VictoryGreen
                    isCurrent -> FlameYellow
                    else -> Color.White.copy(alpha = 0.6f)
                },
                maxLines = 1
            )
        }
    }
}
