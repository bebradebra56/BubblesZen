package com.bubbzeniac.apssof.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bubbzeniac.apssof.sound.AmbientType
import com.bubbzeniac.apssof.ui.theme.ActiveTeal
import com.bubbzeniac.apssof.ui.theme.BubbleBlue
import com.bubbzeniac.apssof.ui.theme.BubblePurple
import com.bubbzeniac.apssof.ui.theme.BubbleTeal
import com.bubbzeniac.apssof.ui.theme.ClearSky
import com.bubbzeniac.apssof.ui.theme.GoldenAccent
import com.bubbzeniac.apssof.ui.theme.InactiveGray
import com.bubbzeniac.apssof.ui.theme.TextPrimary
import com.bubbzeniac.apssof.ui.theme.TextSecondary
import com.bubbzeniac.apssof.viewmodel.AppViewModel

private data class SoundCard(
    val type: AmbientType,
    val color1: Color,
    val color2: Color,
)

private val soundCards = listOf(
    SoundCard(AmbientType.OCEAN, Color(0xFF004488), BubbleBlue),
    SoundCard(AmbientType.RAIN, Color(0xFF224466), ActiveTeal),
    SoundCard(AmbientType.WIND, Color(0xFF1A4422), Color(0xFF44EE88)),
    SoundCard(AmbientType.SILENCE, Color(0xFF220044), BubblePurple),
)

@Composable
fun SoundsScreen(viewModel: AppViewModel) {
    val settings by viewModel.settings.collectAsState()
    val currentAmbient by viewModel.currentAmbient.collectAsState()
    val isPlaying by viewModel.isAmbientPlaying.collectAsState()
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
                text = "Choose your calm",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.5.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Select sounds for serenity",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                soundCards.take(2).forEach { card ->
                    SoundBubbleCard(
                        card = card,
                        isSelected = currentAmbient == card.type,
                        isPlaying = isPlaying && currentAmbient == card.type,
                        onClick = { viewModel.setAmbient(card.type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                soundCards.drop(2).forEach { card ->
                    SoundBubbleCard(
                        card = card,
                        isSelected = currentAmbient == card.type,
                        isPlaying = isPlaying && currentAmbient == card.type,
                        onClick = { viewModel.setAmbient(card.type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (currentAmbient != AmbientType.SILENCE) {
                PlayPauseButton(
                    isPlaying = isPlaying,
                    accentColor = soundCards.find { it.type == currentAmbient }?.color2 ?: BubbleBlue,
                    onClick = { viewModel.toggleAmbient() }
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            Text(
                text = "Volume",
                color = TextSecondary,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Slider(
                    value = settings.ambientVolume,
                    onValueChange = { viewModel.setAmbientVolume(it) },
                    valueRange = 0f..1f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = BubbleTeal,
                        activeTrackColor = BubbleTeal,
                        inactiveTrackColor = InactiveGray.copy(alpha = 0.4f),
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = BubbleTeal,
                    modifier = Modifier.size(20.dp)
                )
            }

            VolumeVisualizer(
                volume = settings.ambientVolume,
                accentColor = soundCards.find { it.type == currentAmbient }?.color2 ?: BubbleBlue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SoundBubbleCard(
    card: SoundCard,
    isSelected: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "soundBubbleScale"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .shadow(
                    elevation = if (isSelected) 20.dp else 6.dp,
                    shape = CircleShape,
                    ambientColor = card.color2.copy(alpha = 0.6f),
                    spotColor = card.color2.copy(alpha = 0.6f),
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = if (isSelected) {
                            listOf(
                                card.color2.copy(alpha = 0.85f),
                                card.color1.copy(alpha = 0.7f),
                            )
                        } else {
                            listOf(
                                card.color1.copy(alpha = 0.5f),
                                card.color1.copy(alpha = 0.3f),
                            )
                        }
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = card.type.emoji,
                    fontSize = 36.sp,
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                                radius = 200f,
                                center = Offset(30f, 30f)
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = card.type.label,
            color = if (isSelected) card.color2 else TextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
        Text(
            text = card.type.description,
            color = TextSecondary.copy(alpha = 0.6f),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(72.dp)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                ambientColor = accentColor.copy(alpha = 0.6f),
                spotColor = accentColor.copy(alpha = 0.6f),
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(accentColor.copy(alpha = 0.9f), accentColor.copy(alpha = 0.6f))
                )
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun VolumeVisualizer(
    volume: Float,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val barCount = 9
        val centerY = size.height / 2f
        val spacing = size.width / (barCount + 1)

        for (i in 0 until barCount) {
            val x = spacing * (i + 1)
            val distFromCenter = kotlin.math.abs(i - barCount / 2f) / (barCount / 2f)
            val baseHeight = size.height * 0.25f * (1f - distFromCenter * 0.4f)
            val activeHeight = baseHeight + size.height * volume * 0.5f * (1f - distFromCenter * 0.5f)
            val alpha = if ((i.toFloat() / barCount) < volume) 0.9f else 0.25f
            val barH = if ((i.toFloat() / barCount) < volume) activeHeight else baseHeight

            drawRoundRect(
                color = accentColor.copy(alpha = alpha),
                topLeft = Offset(x - 3.dp.toPx(), centerY - barH / 2f),
                size = androidx.compose.ui.geometry.Size(6.dp.toPx(), barH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
            )
        }
    }
}
