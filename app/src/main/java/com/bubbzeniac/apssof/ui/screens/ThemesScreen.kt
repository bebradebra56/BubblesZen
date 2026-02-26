package com.bubbzeniac.apssof.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bubbzeniac.apssof.ui.theme.BubbleBlue
import com.bubbzeniac.apssof.ui.theme.BubblePurple
import com.bubbzeniac.apssof.ui.theme.BubbleTeal
import com.bubbzeniac.apssof.ui.theme.CardBorder
import com.bubbzeniac.apssof.ui.theme.ClearSky
import com.bubbzeniac.apssof.ui.theme.DangerRed
import com.bubbzeniac.apssof.ui.theme.DangerRedDim
import com.bubbzeniac.apssof.ui.theme.DeepNight
import com.bubbzeniac.apssof.ui.theme.GoldenAccent
import com.bubbzeniac.apssof.ui.theme.InactiveGray
import com.bubbzeniac.apssof.ui.theme.NightBg1
import com.bubbzeniac.apssof.ui.theme.NightBg2
import com.bubbzeniac.apssof.ui.theme.NightBg3
import com.bubbzeniac.apssof.ui.theme.NightBubble1
import com.bubbzeniac.apssof.ui.theme.NightBubble2
import com.bubbzeniac.apssof.ui.theme.OceanBubble1
import com.bubbzeniac.apssof.ui.theme.OceanBubble2
import com.bubbzeniac.apssof.ui.theme.OceanMid
import com.bubbzeniac.apssof.ui.theme.SunsetBg1
import com.bubbzeniac.apssof.ui.theme.SunsetBg2
import com.bubbzeniac.apssof.ui.theme.SunsetBg3
import com.bubbzeniac.apssof.ui.theme.SunsetBubble1
import com.bubbzeniac.apssof.ui.theme.SunsetBubble2
import com.bubbzeniac.apssof.ui.theme.TextPrimary
import com.bubbzeniac.apssof.ui.theme.TextSecondary
import com.bubbzeniac.apssof.viewmodel.AppViewModel

private data class ThemeOption(
    val index: Int,
    val name: String,
    val subtitle: String,
    val bgGradient: List<Color>,
    val bubbleColors: List<Color>,
    val accentColor: Color,
)

private val themeOptions = listOf(
    ThemeOption(
        index = 0,
        name = "Ocean Calm",
        subtitle = "Blue · Teal · Sky",
        bgGradient = listOf(DeepNight, OceanMid, ClearSky),
        bubbleColors = listOf(OceanBubble1, OceanBubble2, BubbleBlue),
        accentColor = BubbleTeal,
    ),
    ThemeOption(
        index = 1,
        name = "Sunset Zen",
        subtitle = "Violet · Pink · Rose",
        bgGradient = listOf(SunsetBg1, SunsetBg2, SunsetBg3),
        bubbleColors = listOf(SunsetBubble1, SunsetBubble2, BubblePurple),
        accentColor = SunsetBubble1,
    ),
    ThemeOption(
        index = 2,
        name = "Deep Night",
        subtitle = "Dark · Navy · Electric",
        bgGradient = listOf(NightBg1, NightBg2, NightBg3),
        bubbleColors = listOf(NightBubble1, NightBubble2, BubbleBlue),
        accentColor = NightBubble1,
    ),
)

@Composable
fun ThemesScreen(viewModel: AppViewModel) {
    val settings by viewModel.settings.collectAsState()
    val theme = com.bubbzeniac.apssof.ui.theme.LocalAppTheme.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(theme.bgColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Set the atmosphere",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.5.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose a visual theme for your bubbles",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(36.dp))

            themeOptions.forEach { option ->
                ThemeBubbleCard(
                    option = option,
                    isSelected = settings.selectedTheme == option.index,
                    onClick = { viewModel.setSelectedTheme(option.index) }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            SectionLabel("Bubble Speed")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("🐢", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = settings.bubbleSpeed,
                    onValueChange = { viewModel.setBubbleSpeed(it) },
                    valueRange = 0.3f..2.5f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = BubbleTeal,
                        activeTrackColor = BubbleTeal,
                        inactiveTrackColor = InactiveGray.copy(alpha = 0.4f),
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("🐇", fontSize = 18.sp)
            }

            Text(
                text = "Speed: ${String.format("%.1f", settings.bubbleSpeed)}x",
                color = TextSecondary,
                fontSize = 12.sp,
            )

            Spacer(modifier = Modifier.height(32.dp))

            ResetButton(onClick = { viewModel.resetSettings() })

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ThemeBubbleCard(
    option: ThemeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "themeCardScale"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .height(110.dp)
            .shadow(
                elevation = if (isSelected) 16.dp else 4.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = option.accentColor.copy(alpha = 0.4f),
                spotColor = option.accentColor.copy(alpha = 0.4f),
            )
            .clip(RoundedCornerShape(32.dp))
            .background(Brush.horizontalGradient(option.bgGradient))
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    brush = Brush.horizontalGradient(
                        listOf(option.accentColor, Color.White.copy(alpha = 0.6f), option.accentColor)
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) else Modifier.border(
                    width = 1.dp,
                    color = CardBorder,
                    shape = RoundedCornerShape(32.dp)
                )
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.name,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = option.subtitle,
                    color = TextPrimary.copy(alpha = 0.65f),
                    fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    option.bubbleColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .shadow(6.dp, CircleShape, ambientColor = color.copy(0.5f))
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.85f))
                        )
                    }
                }
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = option.accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(Color.White.copy(alpha = 0.1f))
        )
        Text(
            text = text,
            color = TextSecondary,
            fontSize = 12.sp,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(Color.White.copy(alpha = 0.1f))
        )
    }
}

@Composable
private fun ResetButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(52.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = DangerRed.copy(alpha = 0.2f),
            )
            .clip(RoundedCornerShape(28.dp))
            .background(DangerRedDim)
            .border(1.dp, DangerRed.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = DangerRed,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reset Settings",
                color = DangerRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
