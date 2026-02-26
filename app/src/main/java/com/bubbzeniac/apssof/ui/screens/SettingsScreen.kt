package com.bubbzeniac.apssof.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bubbzeniac.apssof.ui.theme.ActiveTeal
import com.bubbzeniac.apssof.ui.theme.BubbleBlue
import com.bubbzeniac.apssof.ui.theme.BubblePurple
import com.bubbzeniac.apssof.ui.theme.BubbleTeal
import com.bubbzeniac.apssof.ui.theme.CardBorder
import com.bubbzeniac.apssof.ui.theme.DangerRed
import com.bubbzeniac.apssof.ui.theme.DangerRedDim
import com.bubbzeniac.apssof.ui.theme.GoldenAccent
import com.bubbzeniac.apssof.ui.theme.InactiveGray
import com.bubbzeniac.apssof.ui.theme.SurfaceDark
import com.bubbzeniac.apssof.ui.theme.TextMuted
import com.bubbzeniac.apssof.ui.theme.TextPrimary
import com.bubbzeniac.apssof.ui.theme.TextSecondary
import com.bubbzeniac.apssof.viewmodel.AppViewModel

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val settings by viewModel.settings.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    val theme = com.bubbzeniac.apssof.ui.theme.LocalAppTheme.current
    val context = LocalContext.current

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
                text = "Settings",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.5.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Customize your experience",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(32.dp))

            SettingsGroup(title = "Audio & Effects") {
                ToggleRow(
                    icon = Icons.Default.MusicNote,
                    title = "Bubble Sounds",
                    subtitle = "Pop sound when touching bubbles",
                    isEnabled = settings.bubbleSoundsEnabled,
                    activeColor = BubbleTeal,
                    onToggle = { viewModel.setBubbleSounds(it) }
                )
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.White.copy(alpha = 0.06f)))
                ToggleRow(
                    icon = Icons.Default.Animation,
                    title = "Animations",
                    subtitle = "Enable bubble movement & effects",
                    isEnabled = settings.animationEnabled,
                    activeColor = BubbleBlue,
                    onToggle = { viewModel.setAnimationEnabled(it) }
                )
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.White.copy(alpha = 0.06f)))
                ToggleRow(
                    icon = Icons.Default.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibration when popping bubbles",
                    isEnabled = settings.hapticEnabled,
                    activeColor = BubblePurple,
                    onToggle = { viewModel.setHapticEnabled(it) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(title = "Data") {
                ActionRow(
                    icon = Icons.Default.Delete,
                    title = "Clear Progress",
                    subtitle = "Reset all session data",
                    color = DangerRed,
                    onClick = { showClearDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(title = "About") {
                ActionRow(
                    icon = Icons.Default.Info,
                    title = "About BubblesZen",
                    subtitle = "Version 1.0 · Your calm companion",
                    color = GoldenAccent,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://bubbleszen.com/privacy-policy.html"))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Made with 💙 for your peace of mind",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showClearDialog) {
        ZenAlertDialog(
            title = "Clear Progress?",
            message = "This will reset all your relaxation session data. This action cannot be undone.",
            confirmText = "Clear",
            confirmColor = DangerRed,
            onConfirm = {
                viewModel.clearProgress()
                showClearDialog = false
            },
            onDismiss = { showClearDialog = false }
        )
    }

    if (showAboutDialog) {
        ZenAlertDialog(
            title = "BubblesZen",
            message = "Version 1.0\n\nAn anti-stress bubble experience. Touch, pop, and float away with gentle animations and calming sounds.\n\nDesigned for pure relaxation.",
            confirmText = "Got it",
            confirmColor = BubbleTeal,
            onConfirm = { showAboutDialog = false },
            onDismiss = { showAboutDialog = false }
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            color = TextMuted,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
        ) {
            content()
        }
    }
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    activeColor: Color,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle(!isEnabled) }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isEnabled) activeColor.copy(alpha = 0.2f) else InactiveGray.copy(alpha = 0.15f)
                )
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isEnabled) activeColor else InactiveGray,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 12.sp,
            )
        }

        BubbleToggle(
            isEnabled = isEnabled,
            activeColor = activeColor,
            onToggle = { onToggle(!isEnabled) }
        )
    }
}

@Composable
private fun BubbleToggle(
    isEnabled: Boolean,
    activeColor: Color,
    onToggle: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isEnabled) 1.05f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "toggleScale"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = if (isEnabled) Alignment.CenterEnd else Alignment.CenterStart,
        modifier = Modifier
            .scale(scale)
            .width(52.dp)
            .height(28.dp)
            .shadow(
                elevation = if (isEnabled) 8.dp else 2.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = activeColor.copy(alpha = 0.4f),
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isEnabled) {
                    Brush.horizontalGradient(listOf(activeColor.copy(0.8f), activeColor))
                } else {
                    Brush.horizontalGradient(listOf(InactiveGray.copy(0.3f), InactiveGray.copy(0.2f)))
                }
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onToggle)
            .padding(3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun ZenAlertDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        title = {
            Text(text = title, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        },
        text = {
            Text(text = message, color = TextSecondary)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, color = confirmColor, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", color = TextSecondary)
            }
        }
    )
}
