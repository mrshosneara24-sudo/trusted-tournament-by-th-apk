package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.model.BadgeType
import com.example.model.ProfileBannerColor
import com.example.model.UserRole
import com.example.model.canCustomizeProfileBanner
import com.example.model.isAnimatedGifPathOrUri
import java.io.File
import java.io.FileOutputStream

private object ProfileGifImageLoaderHolder {
    @Volatile
    private var instance: ImageLoader? = null

    fun get(context: Context): ImageLoader {
        val existing = instance
        if (existing != null) return existing
        return synchronized(this) {
            instance ?: ImageLoader.Builder(context.applicationContext)
                .components {
                    if (Build.VERSION.SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                }
                .crossfade(true)
                .build()
                .also { instance = it }
        }
    }
}

@Composable
fun rememberProfileImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember(context) { ProfileGifImageLoaderHolder.get(context) }
}

private fun detectGifContent(context: Context, uri: Uri): Boolean {
    val mime = runCatching { context.contentResolver.getType(uri) }.getOrNull()?.lowercase()
    if (mime == "image/gif") return true
    if (isAnimatedGifPathOrUri(uri.toString())) return true
    val header = runCatching {
        context.contentResolver.openInputStream(uri)?.use { stream ->
            val bytes = ByteArray(6)
            val read = stream.read(bytes)
            if (read >= 6) String(bytes, Charsets.US_ASCII) else ""
        }
    }.getOrNull() ?: ""
    return header == "GIF87a" || header == "GIF89a"
}

/**
 * Copies picked gallery media into app-private storage (`filesDir/profile_media`) so it persists across restarts
 * without broad storage permissions, while strictly enforcing the 3-tier role & Prime 8 badge rules:
 * - Owner/Admin: Animated GIF & normal pictures allowed for both PFP and Banner.
 * - Prime 8 Badge Holders: Normal picture PFP (NO GIF) and Normal picture Banner (NO GIF).
 * - Regular Players (no Prime 8): Normal picture PFP (NO GIF) and NO custom banner at all.
 */
fun persistPickedProfileMedia(
    context: Context,
    uri: Uri,
    role: UserRole,
    badges: Collection<BadgeType>,
    isBanner: Boolean
): Pair<String?, String?> {
    val isGif = detectGifContent(context, uri)
    if (!isBanner) {
        if (isGif && !role.canUseAnimatedGifProfileMedia()) {
            return Pair(
                null,
                "❌ শুধুমাত্র ওনার ও অ্যাডমিন PFP-তে অ্যানিমেটেড GIF ব্যবহার করতে পারবেন! সাধারণ প্লেয়াররা শুধু গ্যালারি থেকে নরমাল ছবি দিতে পারবেন।"
            )
        }
    } else {
        if (!canCustomizeProfileBanner(role, badges)) {
            return Pair(
                null,
                "❌ ব্যানার পরিবর্তন করতে PRIME [8] ব্যাজ (অথবা ওনার/অ্যাডমিন পদ) প্রয়োজন! সাধারণ প্লেয়াররা শুধু অ্যাপের নির্ধারিত কালারেই ব্যানার রাখবেন।"
            )
        }
        if (isGif && !role.canUseAnimatedGifProfileMedia()) {
            return Pair(
                null,
                "❌ PRIME [8] প্লেয়াররা ব্যানারে GIF ব্যবহার করতে পারবেন না—শুধুমাত্র নরমাল ছবি বা ব্যানার কালার ব্যবহার করতে পারবেন!"
            )
        }
    }

    return runCatching {
        val dir = File(context.filesDir, "profile_media").apply { if (!exists()) mkdirs() }
        val mime = context.contentResolver.getType(uri)?.lowercase().orEmpty()
        val ext = when {
            isGif -> "gif"
            mime.contains("png") -> "png"
            mime.contains("webp") -> "webp"
            else -> "jpg"
        }
        val prefix = if (isBanner) {
            if (isGif) "gif_banner" else "pic_banner"
        } else {
            if (isGif) "gif_pfp" else "pic_pfp"
        }
        val destFile = File(dir, "${prefix}_${System.currentTimeMillis()}.$ext")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        } ?: return Pair(null, "❌ নির্বাচিত ফাইলটি পড়া যায়নি। অনুগ্রহ করে অন্য ছবি/GIF চেষ্টা করুন।")
        Pair(Uri.fromFile(destFile).toString(), null)
    }.getOrElse {
        Pair(null, "❌ মিডিয়া সংরক্ষণ করা যায়নি: ${it.localizedMessage ?: "Unknown error"}")
    }
}

@Composable
fun ProfileAvatarImage(
    avatarId: String,
    customPfpUri: String?,
    role: UserRole,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    showGifBadge: Boolean = false
) {
    val context = LocalContext.current
    val imageLoader = rememberProfileImageLoader()
    val fallbackRes = getAvatarDrawableRes(avatarId)
    val allowedUri = customPfpUri?.takeIf {
        it.isNotBlank() && (role.canUseAnimatedGifProfileMedia() || !isAnimatedGifPathOrUri(it))
    }
    val isGif = allowedUri != null && role.canUseAnimatedGifProfileMedia() && isAnimatedGifPathOrUri(allowedUri)

    Box(modifier = modifier) {
        if (allowedUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(allowedUri)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = fallbackRes),
                error = painterResource(id = fallbackRes),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = fallbackRes),
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showGifBadge && isGif) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xCC000000),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
            ) {
                Text(
                    text = "GIF",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD700),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileBannerBackground(
    role: UserRole,
    badges: Collection<BadgeType>,
    customBannerUri: String?,
    bannerColorId: String,
    isBanned: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val context = LocalContext.current
    val imageLoader = rememberProfileImageLoader()
    val canCustomize = canCustomizeProfileBanner(role, badges)
    val effectiveColorPreset = ProfileBannerColor.fromId(
        if (canCustomize) bannerColorId else ProfileBannerColor.DEFAULT_CRIMSON.id
    )
    val gradientColors = if (isBanned) {
        listOf(Color(0xFF450A0A), Color(0xFF991B1B), Color(0xFF280B1C))
    } else {
        listOf(
            Color(effectiveColorPreset.startColorHex),
            Color(effectiveColorPreset.midColorHex),
            Color(effectiveColorPreset.endColorHex)
        )
    }

    val allowedBannerUri = if (!isBanned && canCustomize && !customBannerUri.isNullOrBlank()) {
        if (!role.canUseAnimatedGifProfileMedia() && isAnimatedGifPathOrUri(customBannerUri)) {
            null
        } else {
            customBannerUri
        }
    } else {
        null
    }

    Box(
        modifier = modifier.background(Brush.horizontalGradient(gradientColors))
    ) {
        if (allowedBannerUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(allowedBannerUri)
                    .crossfade(true)
                    .build(),
                imageLoader = imageLoader,
                contentDescription = "Profile Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Subtle readability scrim so top controls remain crisp over bright GIFs/photos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.40f)
                            )
                        )
                    )
            )
        }
        content()
    }
}
