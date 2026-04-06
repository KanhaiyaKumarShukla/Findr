package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.feature.home.presentation.LiveEvent
import com.exa.android.reflekt.feature.home.presentation.LiveEventIconType
import com.exa.android.reflekt.feature.home.presentation.LiveGradientType
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize

private val LiveRed: Color @Composable get() = DonutTheme.colorTokens.error

@Composable
private fun LiveGradientType.toColors(): List<Color> = when (this) {
    LiveGradientType.BLUE_TECH       -> listOf(DonutTheme.colorTokens.surfaceVariant, DonutTheme.staticColorTokens.accentBlue)
    LiveGradientType.PURPLE_VOTE     -> listOf(DonutTheme.colorTokens.surfaceVariant, DonutTheme.staticColorTokens.accentIndigo)
    LiveGradientType.GREEN_SPORTS    -> listOf(DonutTheme.colorTokens.surfaceVariant, DonutTheme.staticColorTokens.accentGreen)
    LiveGradientType.ORANGE_CAMPAIGN -> listOf(DonutTheme.colorTokens.surfaceVariant, DonutTheme.staticColorTokens.accentOrange)
}

private fun LiveEventIconType.toImageVector(): ImageVector = when (this) {
    LiveEventIconType.LAPTOP   -> Icons.Default.Laptop
    LiveEventIconType.VOTE     -> Icons.Default.HowToVote
    LiveEventIconType.SOCCER   -> Icons.Default.SportsSoccer
    LiveEventIconType.CAMPAIGN -> Icons.Default.Campaign
}

@Composable
internal fun LiveNowSection(
    events: List<LiveEvent>,
    onEvent: (HomeEvent) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "livePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    Column(modifier = Modifier.padding(top = DonutTheme.dimens.spacing16)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DonutTheme.dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(DonutTheme.dimens.iconSizeSmall)
                    .clip(CircleShape)
                    .graphicsLayer { alpha = pulseAlpha }
                    .background(LiveRed),
            )
            Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing8))
            Text(
                text = "Live Now",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "View All",
                fontSize = DonutTextSize.small,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = androidx.compose.ui.unit.TextUnit(1f, androidx.compose.ui.unit.TextUnitType.Sp),
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onEvent(HomeEvent.ViewAllLiveClicked) },
            )
        }

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing16))

        LazyRow(
            contentPadding = PaddingValues(horizontal = DonutTheme.dimens.spacing16),
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12),
        ) {
            items(events, key = { it.id }) { event ->
                LiveEventCard(
                    event = event,
                    pulseAlpha = pulseAlpha,
                    onClick = { onEvent(HomeEvent.LiveEventClicked(event.id)) },
                )
            }
        }
    }
}

@Composable
private fun LiveEventCard(
    event: LiveEvent,
    pulseAlpha: Float,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(DonutTheme.dimens.liveCardWidth)
            .height(DonutTheme.dimens.liveCardHeight)
            .then(
                if (event.isPulsing) {
                    Modifier.border(
                        width = DonutTheme.dimens.borderThick,
                        color = LiveRed.copy(alpha = pulseAlpha),
                        shape = RoundedCornerShape(DonutRadius.panel),
                    )
                } else {
                    Modifier.border(
                        width = DonutTheme.dimens.borderThin,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(DonutRadius.panel),
                    )
                },
            )
            .clip(RoundedCornerShape(DonutRadius.panel))
            .clickable { onClick() },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(event.gradientType.toColors())),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = event.iconType.toImageVector(),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.size(DonutTheme.dimens.liveCardIconSize),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DonutTheme.dimens.liveGradientOverlayHeight)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                    ),
                ),
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(DonutTheme.dimens.spacing12)
                .background(LiveRed, RoundedCornerShape(DonutRadius.sm))
                .padding(horizontal = DonutTheme.dimens.spacing6, vertical = DonutTheme.dimens.spacing2),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Podcasts,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(DonutTheme.dimens.spacing12),
            )
            Text(text = "LIVE", fontSize = DonutTextSize.caption, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(DonutTheme.dimens.spacing12)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(DonutRadius.lg))
                .padding(horizontal = DonutTheme.dimens.spacing8, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing4),
        ) {
            Icon(
                imageVector = Icons.Default.People,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = formatParticipantCount(event.participantCount),
                fontSize = DonutTextSize.small,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(DonutTheme.dimens.spacing14),
        ) {
            Text(
                text = event.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = DonutTextSize.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing2))
            Text(
                text = event.subtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = DonutTextSize.small,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun formatParticipantCount(count: Int): String =
    if (count >= 1000) "${count / 1000}.${(count % 1000) / 100}k joined"
    else "$count joined"
