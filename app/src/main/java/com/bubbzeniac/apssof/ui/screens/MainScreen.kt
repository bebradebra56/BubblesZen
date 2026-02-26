package com.bubbzeniac.apssof.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bubbzeniac.apssof.ui.components.BubbleSimulation
import com.bubbzeniac.apssof.ui.components.drawAnimatedBackground
import com.bubbzeniac.apssof.ui.components.drawGlossyBubble
import com.bubbzeniac.apssof.ui.theme.LocalAppTheme
import com.bubbzeniac.apssof.ui.theme.TextSecondary
import com.bubbzeniac.apssof.viewmodel.AppViewModel

@Composable
fun MainScreen(viewModel: AppViewModel) {
    val settings by viewModel.settings.collectAsState()
    val haptic = LocalHapticFeedback.current
    val theme = LocalAppTheme.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screenHeight = constraints.maxHeight.toFloat()

        val simulation = remember(screenWidth, screenHeight) {
            BubbleSimulation(
                width = screenWidth,
                height = screenHeight,
                bubbleCount = 12,
                speedMultiplier = settings.bubbleSpeed,
            )
        }

        LaunchedEffect(settings.bubbleSpeed) {
            simulation.updateSpeed(settings.bubbleSpeed)
        }

        if (settings.animationEnabled) {
            LaunchedEffect(simulation) {
                var previousNanos = withFrameNanos { it }
                while (true) {
                    withFrameNanos { currentNanos ->
                        val dt = ((currentNanos - previousNanos) / 1_000_000_000f).coerceIn(0f, 0.05f)
                        previousNanos = currentNanos
                        simulation.update(dt, settings.bubbleSpeed)
                    }
                }
            }
        }

        val infiniteTransition = rememberInfiniteTransition(label = "bg")
        val bgPhase by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = (2f * Math.PI).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 14000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "bgPhase"
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(settings.hapticEnabled, settings.bubbleSoundsEnabled) {
                    detectTapGestures { offset ->
                        val popped = simulation.onTouch(offset.x, offset.y)
                        if (popped) {
                            viewModel.playBubblePop()
                            if (settings.hapticEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        } else {
                            simulation.onTouch(offset.x, offset.y)
                        }
                    }
                }
        ) {
            drawAnimatedBackground(
                phase = bgPhase,
                color1 = theme.bgColors[0],
                color2 = theme.bgColors[1],
                color3 = theme.bgColors[2],
            )

            simulation.bubbles.forEach { bubble ->
                drawGlossyBubble(
                    bubble = bubble,
                    colors = theme.bubbleColors,
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "Touch the bubbles to relax",
                color = TextSecondary.copy(alpha = 0.65f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
