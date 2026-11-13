package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.unit.sp
import com.example.model.AppCustomization
import com.example.model.TournamentRuleItem
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.VictoryGreen

@Composable
fun RulesAndSupportScreen(
    officialBkashNumber: String,
    onOpenSupportChat: () -> Unit,
    strictRules: List<TournamentRuleItem> = emptyList(),
    appCustomization: AppCustomization = AppCustomization(),
    isOwner: Boolean = false,
    onOpenOwnerRulesEditor: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("rules_support_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "টুর্নামেন্টের কঠোর নিয়মাবলী",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = Color.White
                        )
                        Text(
                            text = "${appCustomization.appName} এর সার্বিক অ্যান্টি-চিট ও ফেয়ার-প্লে গাইডলাইন",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isOwner) {
                        Button(
                            onClick = onOpenOwnerRulesEditor,
                            colors = ButtonDefaults.buttonColors(containerColor = FlameYellow, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "এডিট রুলস", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("এডিট রুলস", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Strict Tournament Warning Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261014)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Block,
                            contentDescription = "Strict Policy",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "🚨 কঠোর নীতি: ১০০% হ্যাকার ও এমুলেটর মুক্ত গ্যারান্টি",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFEF4444)
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TRUSTED TOURNAMENT এ কোনো প্রতারণা বরদাশত করা হয় না। প্রত্যেক ম্যাচের কাস্টম রুম স্পেকটেটরে এডমিন ও এআই দ্বারা কড়া নজরদারি করা হয়। নিয়ম লঙ্ঘন করলেই এন্ট্রি ফি বাজেয়াপ্ত ও স্থায়ী ব্যান কার্যকর হবে।",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            ),
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }
            }
        }

        // Dedicated Strict Rules Cards
        item {
            Text(
                text = "📜 টুর্নামেন্ট খেলার ৮টি কঠোর শর্ত ও নিয়ম:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = FlameYellow
            )
        }

        val rulesToDisplay = strictRules.ifEmpty {
            listOf(
                TournamentRuleItem("r1", "১০০% মোবাইল অনলি (No Emulator)", "পিসি, ব্লুস্ট্যাক্স, এলডিপ্লেয়ার বা যেকোনো এমুলেটর সম্পূর্ণ নিষিদ্ধ।", "ধরা পড়লে সাথে সাথে স্থায়ী ব্যান এবং এন্ট্রি ফি বাজেয়াপ্ত।", "🚫"),
                TournamentRuleItem("r2", "হেডশট কনফিগ ও থার্ড-পার্টি ফাইল নিষিদ্ধ", "অটো-হেডশট, কনফিগ ফাইল, গ্লিচ প্যাক, GFX টুল ব্যবহার সম্পূর্ণ নিষিদ্ধ।", "UID ও ডিভাইস আইডি আজীবনের জন্য ব্ল্যাকলিস্ট হবে।", "🛡️"),
                TournamentRuleItem("r3", "সঠিক স্লট শৃঙ্খলার নিয়ম (Slot Discipline)", "অ্যাপে বুকিং করা নির্দিষ্ট স্লট নম্বরেই রুমে বসতে হবে। অন্যের স্লটে বসা যাবে না।", "অন্যের স্লটে বসলে কোনো ওয়ার্নিং ছাড়া কিক ও রিফান্ড বাতিল।", "🎯"),
                TournamentRuleItem("r4", "টিমিং (Teaming) কঠোরভাবে নিষিদ্ধ", "সোলো বা ডুও ম্যাচে প্রতিপক্ষের সাথে ফ্রেন্ডশিপ বা টিমিং করা যাবে না।", "উভয় প্লেয়ারকে সরাসরি ডিসকোয়ালিফাই করা হবে।", "⚖️"),
                TournamentRuleItem("r5", "রুম আইডি ও পাসওয়ার্ডের গোপনীয়তা", "অ্যাপের রুম কোড কোনো বাইরের বন্ধু বা গ্রুপে শেয়ার করা সম্পূর্ণ নিষিদ্ধ।", "শেয়ারকারীর অ্যাকাউন্ট অবিলম্বে ব্যান করা হবে।", "🔐"),
                TournamentRuleItem("r6", "বিকাশ ও লেনদেন সংক্রান্ত জালিয়াতি প্রতিরোধ", "ভুয়া TrxID দিয়ে স্লট নেওয়ার চেষ্টা করলে কঠোর আইনি ব্যবস্থা নেওয়া হবে।", "প্রতারকের নম্বর ও UID স্থায়ীভাবে ব্যান হবে।", "💳"),
                TournamentRuleItem("r7", "যথাসময়ে কাস্টম রুমে উপস্থিতি", "ম্যাচ শুরুর নির্ধারিত সময়ের পর কোনো প্লেয়ারের জন্য অপেক্ষা করা হবে না।", "দেরিতে আসলে এন্ট্রি ফি রিফান্ড যোগ্য নয়।", "⏱️"),
                TournamentRuleItem("r8", "বুইয়া ও প্রাইজ ক্লেইম ভেরিফিকেশন", "ম্যাচ জয়ের পর স্পষ্ট কিল ও বুইয়ার স্ক্রিনশট ১৫ মিনিটের মধ্যে দিতে হবে।", "মিথ্যা দাবিতে প্রাইজ মানি বাতিল করা হবে।", "🏆")
            )
        }

        items(rulesToDisplay.size) { index ->
            val rule = rulesToDisplay[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(rule.iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${index + 1}. ${rule.title}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "STRICT",
                                color = Color(0xFFEF4444),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = rule.description,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1E1418),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️ শাস্তি: ", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444), fontSize = 11.sp)
                            Text(rule.penalty, color = Color(0xFFFCA5A5), fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Step-by-Step Custom Room Join Guide
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.PlayCircle,
                            contentDescription = "Tutorial",
                            tint = VictoryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "কীভাবে গেমে কাস্টম রুমে জয়েন করবেন?",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val steps = listOf(
                        "ধাপ ১: ম্যাচ শুরুর ১৫ মিনিট আগে এই অ্যাপের 'আমার ম্যাচ' এ গিয়ে রুম আইডি ও পাসওয়ার্ড কপি করুন।",
                        "ধাপ ২: ফ্রি ফায়ার গেম ওপেন করে মোড সিলেকশন থেকে 'Custom' এ ট্যাপ করুন।",
                        "ধাপ ৩: রুম আইডি সার্চ বক্সে পেস্ট করে রুমে প্রবেশ করুন এবং পাসওয়ার্ড দিন।",
                        "ধাপ ৪: রুমে আপনার নির্ধারিত স্লট নম্বরেই বসুন। ভুল স্লটে বসলে এডমিন কিক করে দেবে।"
                    )

                    steps.forEach { step ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Step",
                                tint = VictoryGreen,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Point System
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Gavel,
                            contentDescription = "Points",
                            tint = FlameYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "বিআর র‍্যাঙ্ক পয়েন্ট ও প্রাইজ নিয়ম",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• ১ম স্থান (বুইয়া): নগদ ৫০০-১০০০ টাকা (ম্যাচ অনুযায়ী)\n• প্রতি কিল বোনাস: ১০ থেকে ২৫ টাকা (ম্যাচে যত কিল করবেন প্রতি কিলের টাকা আলাদা পাবেন)\n• উইনিং টাকা ম্যাচ শেষের ১০-১৫ মিনিটের মধ্যে ওয়ালেটে যোগ হয়ে যাবে।",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Helpline & Support Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.SupportAgent,
                            contentDescription = "Support",
                            tint = BkashPink,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "বিকাশ ও হেল্পলাইন তথ্য",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "আমাদের অফিসিয়াল বিকাশ নম্বর:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = officialBkashNumber,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FlameYellow
                                )
                            )

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(officialBkashNumber))
                                    Toast.makeText(context, "নম্বর ($officialBkashNumber) কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BkashPink),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("কপি", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
