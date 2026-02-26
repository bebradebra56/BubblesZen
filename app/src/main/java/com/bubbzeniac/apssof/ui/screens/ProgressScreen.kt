package com.bubbzeniac.apssof.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bubbzeniac.apssof.data.DayProgress
import com.bubbzeniac.apssof.ui.theme.BubbleBlue
import com.bubbzeniac.apssof.ui.theme.BubbleTeal
import com.bubbzeniac.apssof.ui.theme.ClearSky
import com.bubbzeniac.apssof.ui.theme.GoldenAccent
import com.bubbzeniac.apssof.ui.theme.GoldenDark
import com.bubbzeniac.apssof.ui.theme.TextPrimary
import com.bubbzeniac.apssof.ui.theme.TextSecondary
import com.bubbzeniac.apssof.viewmodel.AppViewModel

@Composable
fun ProgressScreen(viewModel: AppViewModel) {
    val progress by viewModel.progressData.collectAsState()
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
                text = "Moments of calm",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.5.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your relaxation journey",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(36.dp))

            TodayBubble(minutes = progress.todayMinutes)

            Spacer(modifier = Modifier.height(40.dp))

            StatsRow(
                totalMinutes = progress.totalMinutes,
                bestDay = progress.bestDayMinutes,
            )

            Spacer(modifier = Modifier.height(36.dp))

            SectionDivider("Last 7 Days")

            Spacer(modifier = Modifier.height(24.dp))

            WeekBubbleChart(
                days = progress.weekData,
                bestDay = progress.bestDayMinutes,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (progress.todayMinutes >= 10) "Great job relaxing today! 💧"
                else "Relax more often 💧",
                color = TextSecondary.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TodayBubble(minutes: Int) {
    val animSize = remember { Animatable(0f) }
    val animAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animSize.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        animAlpha.animateTo(1f, tween(400))
    }

    val isRecord = minutes > 0
    val glowColor = if (isRecord) GoldenAccent else BubbleBlue

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size((180 * animSize.value).dp)
            .shadow(
                elevation = 24.dp,
                shape = CircleShape,
                ambientColor = glowColor.copy(alpha = 0.6f),
                spotColor = glowColor.copy(alpha = 0.6f),
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        BubbleTeal.copy(alpha = 0.75f),
                        BubbleBlue.copy(alpha = 0.6f),
                        ClearSky.copy(alpha = 0.3f),
                    )
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent),
                        radius = 120f,
                        center = androidx.compose.ui.geometry.Offset(60f, 60f)
                    )
                )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$minutes",
                fontSize = 52.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-1).sp,
            )
            Text(
                text = "min today",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f),
                letterSpacing = 1.sp,
            )
        }
    }
}

@Composable
private fun StatsRow(totalMinutes: Int, bestDay: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        StatBubble(
            value = "$totalMinutes",
            label = "Total min",
            color1 = BubbleBlue,
            color2 = BubbleTeal,
        )
        Spacer(modifier = Modifier.width(20.dp))
        StatBubble(
            value = "$bestDay",
            label = "Best day",
            color1 = GoldenDark,
            color2 = GoldenAccent,
        )
    }
}

@Composable
private fun StatBubble(
    value: String,
    label: String,
    color1: Color,
    color2: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = color2.copy(alpha = 0.5f),
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(color2.copy(alpha = 0.7f), color1.copy(alpha = 0.5f))
                    )
                )
        ) {
            Text(
                text = value,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun WeekBubbleChart(
    days: List<DayProgress>,
    bestDay: Int,
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(days) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    val maxMins = bestDay.coerceAtLeast(1)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
    ) {
        days.forEach { day ->
            val fraction = (day.minutes.toFloat() / maxMins) * animProgress.value
            val minFraction = 0.08f
            val displayFraction = minFraction + (1f - minFraction) * fraction

            DayBubbleBar(
                day = day,
                fraction = displayFraction,
                isBest = day.minutes == bestDay && bestDay > 0,
            )
        }
    }
}

@Composable
private fun DayBubbleBar(
    day: DayProgress,
    fraction: Float,
    isBest: Boolean,
) {
    val bubbleSize = (40f + 60f * fraction).dp
    val color = if (day.isToday) BubbleTeal
    else if (isBest) GoldenAccent
    else BubbleBlue

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.width(40.dp),
    ) {
        if (day.minutes > 0) {
            Text(
                text = "${day.minutes}",
                fontSize = 9.sp,
                color = color.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(bubbleSize)
                .shadow(
                    elevation = if (day.isToday || isBest) 10.dp else 4.dp,
                    shape = CircleShape,
                    ambientColor = color.copy(alpha = 0.5f),
                )
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.8f), color.copy(alpha = 0.4f))
                    )
                )
        ) {}

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = day.label,
            fontSize = 9.sp,
            color = if (day.isToday) BubbleTeal else TextSecondary,
            textAlign = TextAlign.Center,
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

@Composable
private fun SectionDivider(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.1f)))
        Text(
            text = title,
            color = TextSecondary,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.1f)))
    }
}
