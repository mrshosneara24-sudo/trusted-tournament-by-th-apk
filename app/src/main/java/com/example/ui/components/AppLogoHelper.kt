package com.example.ui.components

import com.example.R
import com.example.model.AppLogoOption

fun getLogoDrawable(logo: AppLogoOption): Int = when (logo) {
    AppLogoOption.TH_LOGO -> R.drawable.th_logo
    AppLogoOption.TH_ESPORTS -> R.drawable.th_esports_logo_1792210276945
    AppLogoOption.CYBER_WOLF -> R.drawable.avatar_wolf
    AppLogoOption.PRO_GAMER -> R.drawable.avatar_cyber
}
