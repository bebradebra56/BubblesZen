package com.bubbzeniac.apssof.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ZenColorScheme = darkColorScheme(
    primary = BubbleBlue,
    secondary = BubbleTeal,
    tertiary = BubblePurple,
    background = DeepNight,
    surface = SurfaceDark,
    surfaceVariant = SurfaceMedium,
    onPrimary = Color(0xFF001A33),
    onSecondary = Color(0xFF001A33),
    onTertiary = Color(0xFF1A0033),
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    outlineVariant = Color(0x2255AADD),
    error = DangerRed,
    scrim = Color(0xAA000022),
)

@Composable
fun BubblesZenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ZenColorScheme,
        typography = Typography,
        content = content
    )
}
