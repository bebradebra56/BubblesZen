package com.bubbzeniac.apssof.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bubbzeniac.apssof.navigation.Screen
import com.bubbzeniac.apssof.ui.theme.ActiveTeal
import com.bubbzeniac.apssof.ui.theme.BubbleBlue
import com.bubbzeniac.apssof.ui.theme.BubblePurple
import com.bubbzeniac.apssof.ui.theme.BubbleTeal
import com.bubbzeniac.apssof.ui.theme.GoldenAccent
import com.bubbzeniac.apssof.ui.theme.InactiveGray

private data class NavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String,
    val activeGradient: List<Color>,
)

private val navItems = listOf(
    NavItem(Screen.Main, Icons.Filled.AutoAwesome, "Bubbles",
        listOf(BubbleBlue, BubbleTeal)),
    NavItem(Screen.Sounds, Icons.Filled.MusicNote, "Sounds",
        listOf(BubbleTeal, ActiveTeal)),
    NavItem(Screen.Themes, Icons.Filled.Palette, "Themes",
        listOf(BubblePurple, BubbleBlue)),
    NavItem(Screen.Progress, Icons.Filled.BarChart, "Progress",
        listOf(GoldenAccent, BubbleBlue)),
    NavItem(Screen.Settings, Icons.Filled.Settings, "Settings",
        listOf(Color(0xFF88AAFF), BubblePurple)),
)

@Composable
fun BubbleBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.18f))
            .navigationBarsPadding()
            .padding(bottom = 6.dp, top = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navItems.forEach { item ->
                NavBubble(
                    item = item,
                    isSelected = currentScreen == item.screen,
                    onClick = { onNavigate(item.screen) }
                )
            }
        }
    }
}

@Composable
private fun NavBubble(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "navScale"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .scale(scale)
                .size(if (isSelected) 52.dp else 46.dp)
                .shadow(
                    elevation = if (isSelected) 12.dp else 2.dp,
                    shape = CircleShape,
                    ambientColor = item.activeGradient.first().copy(alpha = 0.5f),
                    spotColor = item.activeGradient.first().copy(alpha = 0.5f),
                )
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        Brush.radialGradient(
                            colors = listOf(
                                item.activeGradient.first().copy(alpha = 0.9f),
                                item.activeGradient.last().copy(alpha = 0.7f),
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                InactiveGray.copy(alpha = 0.4f),
                                InactiveGray.copy(alpha = 0.2f),
                            )
                        )
                    }
                )
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.45f),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = item.label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) item.activeGradient.first() else Color.White.copy(alpha = 0.4f),
        )
    }
}
