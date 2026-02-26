package com.bubbzeniac.apssof.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppThemeColors(
    val bgColors: List<Color>,
    val bubbleColors: List<Color>,
)

val LocalAppTheme = compositionLocalOf {
    AppThemeColors(
        bgColors = listOf(DeepNight, OceanMid, ClearSky),
        bubbleColors = listOf(BubbleBlue, BubbleTeal, BubblePurple),
    )
}

fun themeColorsForIndex(index: Int): AppThemeColors = when (index) {
    1 -> AppThemeColors(
        bgColors = listOf(SunsetBg1, SunsetBg2, SunsetBg3),
        bubbleColors = listOf(SunsetBubble1, SunsetBubble2, BubblePurple),
    )
    2 -> AppThemeColors(
        bgColors = listOf(NightBg1, NightBg2, NightBg3),
        bubbleColors = listOf(NightBubble1, NightBubble2, BubbleBlue),
    )
    else -> AppThemeColors(
        bgColors = listOf(DeepNight, OceanMid, ClearSky),
        bubbleColors = listOf(BubbleBlue, BubbleTeal, BubblePurple),
    )
}
