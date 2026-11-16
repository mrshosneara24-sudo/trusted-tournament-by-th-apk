package com.example.ui.components

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GifBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsEsports
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.BadgeType
import com.example.model.ProfileBannerColor
import com.example.model.UserProfile
import com.example.model.canCustomizeProfileBanner
import com.example.model.isAnimatedGifPathOrUri
import com.example.ui.theme.BkashPink
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.FlameCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VictoryGreen

data class AvatarItem(
    val id: String,
    val title: String,
    val drawableRes: Int
)

fun getAvailableAvatars(): List<AvatarItem> {
    return listOf(
        AvatarItem("th_logo", "TH অফিসিয়াল লোগো", R.drawable.th_logo),
        AvatarItem("avatar_cyber", "সাইবার ফাইটার", R.drawable.avatar_cyber),
        AvatarItem("avatar_wolf", "লোন উলফ স্পেশাল", R.drawable.avatar_wolf)
    )
}

fun getAvatarDrawableRes(avatarId: String): Int {
    return when (avatarId) {
        "avatar_cyber" -> R.drawable.avatar_cyber
        "avatar_wolf" -> R.drawable.avatar_wolf
        else -> R.drawable.th_logo
    }
}

@Composable
fun EditAccountDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (
        accountName: String,
        ign: String,
        uid: String,
        phone: String,
        bkash: String,
        avatarId: String,
        customPfpUri: String?,
        customBannerUri: String?,
        bannerColorId: String
    ) -> Unit,
    onSaveBio: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var accountName by remember { mutableStateOf(userProfile.accountName) }
    var ign by remember { mutableStateOf(userProfile.ign) }
    var uid by remember { mutableStateOf(userProfile.uid) }
    var phone by remember { mutableStateOf(userProfile.phone) }
    var bkash by remember { mutableStateOf(userProfile.bkashNumber) }
    var bio by remember { mutableStateOf(userProfile.battleTagline) }
    var selectedAvatarId by remember { mutableStateOf(userProfile.avatarId) }
    var selectedCustomPfpUri by remember { mutableStateOf(userProfile.customPfpUri) }
    var selectedCustomBannerUri by remember { mutableStateOf(userProfile.customBannerUri) }
    var selectedBannerColorId by remember { mutableStateOf(userProfile.bannerColorId) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val earnedBadges = userProfile.earnedBadges
    val canUseAnimatedGif = userProfile.role.canUseAnimatedGifProfileMedia()
    val canCustomizeBanner = canCustomizeProfileBanner(userProfile.role, earnedBadges)
    val hasPrime8Only = !canUseAnimatedGif && BadgeType.PRIME_SPENDER in earnedBadges

    val avatars = remember { getAvailableAvatars() }

    val pfpPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val (savedUri, err) = persistPickedProfileMedia(
                context = context,
                uri = uri,
                role = userProfile.role,
                badges = earnedBadges,
                isBanner = false
            )
            if (savedUri != null) {
                selectedCustomPfpUri = savedUri
                errorMessage = null
                Toast.makeText(
                    context,
                    if (isAnimatedGifPathOrUri(savedUri)) "✨ Animated GIF PFP প্রিভিউয়ে যুক্ত হয়েছে!" else "✅ প্রোফাইল ছবি প্রিভিউয়ে যুক্ত হয়েছে!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (err != null) {
                errorMessage = err
                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
            }
        }
    }

    val bannerPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val (savedUri, err) = persistPickedProfileMedia(
                context = context,
                uri = uri,
                role = userProfile.role,
                badges = earnedBadges,
                isBanner = true
            )
            if (savedUri != null) {
                selectedCustomBannerUri = savedUri
                errorMessage = null
                Toast.makeText(
                    context,
                    if (isAnimatedGifPathOrUri(savedUri)) "✨ Animated GIF ব্যানার প্রিভিউয়ে যুক্ত হয়েছে!" else "✅ ছবি ব্যানার প্রিভিউয়ে যুক্ত হয়েছে!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (err != null) {
                errorMessage = err
                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 720.dp)
                .testTag("edit_account_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
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
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = FlameYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "একাউন্ট ও প্রোফাইল এডিট",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = if (canUseAnimatedGif) {
                                    "👑 ওনার/অ্যাডমিন: GIF ব্যানার ও GIF PFP আনলকড"
                                } else if (hasPrime8Only) {
                                    "💠 PRIME [8]: ছবি/কালার ব্যানার ও ছবি PFP আনলকড"
                                } else {
                                    "🎮 প্লেয়ার: নরমাল ছবি PFP (ব্যানার পরিবর্তন করতে PRIME 8 লাগবে)"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (canUseAnimatedGif) FlameYellow else FlameCyan
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Esports Profile Banner & PFP Preview
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DarkCard,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_profile_live_preview")
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ProfileBannerBackground(
                            role = userProfile.role,
                            badges = earnedBadges,
                            customBannerUri = selectedCustomBannerUri,
                            bannerColorId = selectedBannerColorId,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(86.dp)
                                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                        ) {
                            val bannerBadgeText = when {
                                selectedCustomBannerUri != null && canUseAnimatedGif && isAnimatedGifPathOrUri(selectedCustomBannerUri) -> "✨ GIF BANNER"
                                selectedCustomBannerUri != null && canCustomizeBanner -> "🖼️ PICTURE BANNER"
                                canCustomizeBanner -> "🎨 ${ProfileBannerColor.fromId(selectedBannerColorId).title}"
                                else -> "🔒 APP BANNER COLOUR"
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.Black.copy(alpha = 0.55f),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = bannerBadgeText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (canCustomizeBanner) FlameYellow else Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 50.dp, start = 14.dp, end = 14.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(DarkCard)
                                    .padding(3.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, FlameYellow, CircleShape)
                            ) {
                                ProfileAvatarImage(
                                    avatarId = selectedAvatarId,
                                    customPfpUri = selectedCustomPfpUri,
                                    role = userProfile.role,
                                    contentDescription = "Selected Avatar Preview",
                                    showGifBadge = true,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.padding(bottom = 4.dp)) {
                                Text(
                                    text = ign.ifBlank { "আপনার IGN" },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "${userProfile.role.badge} লাইভ প্লেয়ার প্রোফাইল প্রিভিউ",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar / PFP Selection Section
                Text(
                    text = if (canUseAnimatedGif) {
                        "প্রোফাইল ছবি / GIF (Animated GIF PFP আনলকড):"
                    } else {
                        "প্রোফাইল ছবি (শুধুমাত্র নরমাল ছবি / অবতার — GIF অনুমোদিত নয়):"
                    },
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = FlameYellow
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            pfpPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canUseAnimatedGif) FlameOrange else FlameCyan
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag(if (canUseAnimatedGif) "pick_gif_pfp_btn" else "pick_normal_pfp_btn")
                    ) {
                        Icon(
                            imageVector = if (canUseAnimatedGif) Icons.Filled.GifBox else Icons.Filled.AddPhotoAlternate,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (canUseAnimatedGif) "গ্যালারি থেকে GIF / ছবি PFP" else "গ্যালারি থেকে নরমাল ছবি",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                    if (!selectedCustomPfpUri.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { selectedCustomPfpUri = null },
                            border = BorderStroke(1.dp, DarkBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("clear_pfp_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Reset PFP",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preset Avatar Choice List
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(avatars) { item ->
                        val isSelected = selectedCustomPfpUri == null && item.id == selectedAvatarId
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else DarkBorder)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) FlameYellow else DarkBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedAvatarId = item.id
                                    selectedCustomPfpUri = null
                                }
                                .padding(8.dp)
                        ) {
                            Box(modifier = Modifier.size(50.dp)) {
                                Image(
                                    painter = painterResource(id = item.drawableRes),
                                    contentDescription = item.title,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(FlameYellow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (isSelected) FlameYellow else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Banner Customization Section (3-Tier Rule)
                Text(
                    text = "প্রোফাইল ব্যানার কাস্টমাইজেশন:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = FlameYellow
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (canCustomizeBanner) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkCard,
                        border = BorderStroke(
                            1.dp,
                            if (canUseAnimatedGif) FlameYellow.copy(alpha = 0.55f) else FlameOrange.copy(alpha = 0.55f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = if (canUseAnimatedGif) {
                                    "👑 ওনার/অ্যাডমিন প্রিভিলেজ: আপনি ব্যানারে Animated GIF অথবা ছবি এবং যেকোনো কালার ব্যবহার করতে পারবেন!"
                                } else {
                                    "💠 PRIME [8] ব্যাজ আনলকড: আপনি ব্যানারে নরমাল ছবি অথবা অ্যাপের ব্যানার কালার ব্যবহার করতে পারবেন (GIF ব্যানার শুধুমাত্র ওনার/অ্যাডমিনের জন্য)!"
                                },
                                fontSize = 11.sp,
                                color = TextSecondary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        bannerPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (canUseAnimatedGif) FlameYellow else VictoryGreen
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag(if (canUseAnimatedGif) "pick_gif_banner_btn" else "pick_picture_banner_btn")
                                ) {
                                    Icon(
                                        imageVector = if (canUseAnimatedGif) Icons.Filled.GifBox else Icons.Filled.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (canUseAnimatedGif) {
                                            "গ্যালারি থেকে GIF / ছবি ব্যানার"
                                        } else {
                                            "গ্যালারি থেকে ছবি ব্যানার (No GIF)"
                                        },
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                }
                                if (!selectedCustomBannerUri.isNullOrBlank()) {
                                    OutlinedButton(
                                        onClick = { selectedCustomBannerUri = null },
                                        border = BorderStroke(1.dp, DarkBorder),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.testTag("clear_banner_btn")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DeleteOutline,
                                            contentDescription = "Clear Banner",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "অথবা অ্যাপের ব্যানার কালার সিলেক্ট করুন:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(ProfileBannerColor.entries) { colorPreset ->
                                    val isColorSelected = selectedCustomBannerUri == null && selectedBannerColorId == colorPreset.id
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(
                                            width = if (isColorSelected) 2.dp else 1.dp,
                                            color = if (isColorSelected) FlameYellow else DarkBorder
                                        ),
                                        modifier = Modifier
                                            .clickable {
                                                selectedBannerColorId = colorPreset.id
                                                selectedCustomBannerUri = null
                                            }
                                            .testTag("banner_color_option_${colorPreset.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .background(
                                                    Brush.horizontalGradient(
                                                        listOf(
                                                            Color(colorPreset.startColorHex),
                                                            Color(colorPreset.midColorHex),
                                                            Color(colorPreset.endColorHex)
                                                        )
                                                    )
                                                )
                                                .padding(horizontal = 10.dp, vertical = 7.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (isColorSelected) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    tint = FlameYellow,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = colorPreset.title,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkElevated,
                        border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("player_banner_locked_notice")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FlameOrange.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Locked Banner",
                                    tint = FlameYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "অ্যাপ নির্ধারিত ব্যানার কালার সক্রিয় (লকড)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = FlameYellow
                                )
                                Text(
                                    text = "সাধারণ প্লেয়াররা ব্যানার পরিবর্তন করতে পারবেন না। ছবি/কালার ব্যানার আনলক করতে PRIME [8] ব্যাজ (৳১৫০+ খরচ) লাগবে এবং GIF ব্যানার শুধুমাত্র ওনার ও অ্যাডমিনের জন্য।",
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Account Name Field
                OutlinedTextField(
                    value = accountName,
                    onValueChange = {
                        accountName = it
                        errorMessage = null
                    },
                    label = { Text("একাউন্টের নাম (Account Name)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Name",
                            tint = FlameYellow
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = FlameYellow,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Free Fire In-Game Name (IGN)
                OutlinedTextField(
                    value = ign,
                    onValueChange = {
                        ign = it
                        errorMessage = null
                    },
                    label = { Text("ফ্রি ফায়ার গেমের নাম (Free Fire IGN)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.SportsEsports,
                            contentDescription = "IGN",
                            tint = FlameYellow
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_ign_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = FlameYellow,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Free Fire UID
                OutlinedTextField(
                    value = uid,
                    onValueChange = {
                        uid = it
                        errorMessage = null
                    },
                    label = { Text("ফ্রি ফায়ার UID (Player ID)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Badge,
                            contentDescription = "UID",
                            tint = FlameYellow
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_uid_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = FlameYellow,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Number
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        errorMessage = null
                    },
                    label = { Text("মোবাইল নম্বর (Contact Phone)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Phone,
                            contentDescription = "Phone",
                            tint = FlameYellow
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_phone_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = FlameYellow,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Saved bKash Number for payouts
                OutlinedTextField(
                    value = bkash,
                    onValueChange = {
                        bkash = it
                        errorMessage = null
                    },
                    label = { Text("বিকাশ নম্বর (টাকা তোলার জন্য)") },
                    leadingIcon = {
                        Text(
                            text = "৳",
                            color = BkashPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_bkash_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BkashPink,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = BkashPink,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Public Bio / Battle Tagline
                OutlinedTextField(
                    value = bio,
                    onValueChange = {
                        if (it.length <= 140) bio = it
                        errorMessage = null
                    },
                    label = { Text("আমার সম্পর্কে / বায়ো (Player Bio)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Bio",
                            tint = FlameYellow
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_bio_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FlameYellow,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = FlameYellow,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 2
                )

                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save Action Button
                Button(
                    onClick = {
                        if (accountName.isBlank()) {
                            errorMessage = "অনুগ্রহ করে আপনার নাম দিন"
                            return@Button
                        }
                        if (ign.isBlank()) {
                            errorMessage = "ফ্রি ফায়ার গেমের নাম দিন"
                            return@Button
                        }
                        if (uid.isBlank()) {
                            errorMessage = "ফ্রি ফায়ার প্লেয়ার UID দিন"
                            return@Button
                        }
                        onSaveBio(bio)
                        onSaveProfile(
                            accountName,
                            ign,
                            uid,
                            phone,
                            bkash,
                            selectedAvatarId,
                            selectedCustomPfpUri,
                            selectedCustomBannerUri,
                            selectedBannerColorId
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_account_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "সংরক্ষণ করুন (Save Changes)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}
