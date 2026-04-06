package com.exa.android.reflekt.feature.profile.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.feature.profile.presentation.ProfileEvent
import com.exa.android.reflekt.feature.profile.presentation.ProfileUiState
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun ProfileCoverSection(
    state: ProfileUiState,
    onEvent: (ProfileEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val dimens = DonutTheme.dimens
    val avatarColor = Color(state.avatarColorArgb.toInt() or (0xFF shl 24))

    Box(modifier = Modifier.fillMaxWidth()) {
        // Cover gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        ),
                    ),
                ),
        ) {
            // Bottom gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.spacing80)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    ),
            )

            // Top bar overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = dimens.spacing12, vertical = dimens.spacing8),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlassButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, "Back",
                        tint = Color.White, modifier = Modifier.size(dimens.iconSizeLarge),
                    )
                }
                Text(
                    text = "PROFILE",
                    fontSize = DonutTextSize.small,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = DonutTextSize.micro,
                    color = Color.White.copy(alpha = 0.7f),
                )
                GlassButton(onClick = { onEvent(ProfileEvent.SettingsClicked) }) {
                    Icon(
                        Icons.Default.Settings, "Settings",
                        tint = Color.White, modifier = Modifier.size(dimens.iconSizeLarge),
                    )
                }
            }
        }

        // Avatar + Social links row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spacing16)
                .offset(y = 112.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Avatar with online indicator
            Box {
                val bgColor = MaterialTheme.colorScheme.background
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .drawBehind { drawCircle(bgColor) }
                        .padding(dimens.spacing4)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.avatarInitial,
                        fontSize = DonutTextSize.display,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
                if (state.isOnline) {
                    OnlineIndicator(
                        modifier = Modifier.align(Alignment.BottomEnd),
                    )
                }
            }

            // Social link buttons
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.spacing12)) {
                SocialButton(onClick = { onEvent(ProfileEvent.LinkedInClicked) }) {
                    Icon(
                        Icons.Default.Link, "LinkedIn",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(dimens.iconSizeLarge),
                    )
                }
                SocialButton(onClick = { onEvent(ProfileEvent.GitHubClicked) }) {
                    Icon(
                        Icons.Default.Code, "GitHub",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(dimens.iconSizeLarge),
                    )
                }
            }
        }
    }

    // Spacer for the overlapping avatar
    Spacer(modifier = Modifier.height(dimens.spacing56))
}

@Composable
private fun OnlineIndicator(modifier: Modifier = Modifier) {
    val dimens = DonutTheme.dimens
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha = transition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "pulseAlpha",
    )
    val bgColor = MaterialTheme.colorScheme.background
    Box(
        modifier = modifier
            .size(dimens.spacing24)
            .drawBehind { drawCircle(bgColor) }
            .padding(dimens.spacing4)
            .clip(CircleShape)
            .background(Color(0xFF22C55E).copy(alpha = alpha.value)),
    )
}

@Composable
private fun GlassButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(DonutTheme.dimens.spacing40)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun SocialButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val dimens = DonutTheme.dimens
    Box(
        modifier = Modifier
            .size(dimens.spacing40)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(dimens.borderThin, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) { content() }
}
