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
import androidx.compose.material.icons.filled.RemoveRedEye
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.feature.home.presentation.LiveEvent
import com.exa.android.reflekt.feature.home.presentation.LiveEventIconType
import com.exa.android.reflekt.feature.home.presentation.LiveGradientType

private val LiveRed = Color(0xFFEF4444)

// Maps domain enums to Compose types — pure presentation logic
private fun LiveGradientType.toColors(): List<Color> = when (this) {
    LiveGradientType.BLUE_TECH    -> listOf(Color(0xFF1E3A5F), Color(0xFF0EA0D1))
    LiveGradientType.PURPLE_VOTE  -> listOf(Color(0xFF2D1B4E), Color(0xFF6366F1))
    LiveGradientType.GREEN_SPORTS -> listOf(Color(0xFF1B4332), Color(0xFF22C55E))
    LiveGradientType.ORANGE_CAMPAIGN -> listOf(Color(0xFF7C2D12), Color(0xFFEA580C))
}

private fun LiveEventIconType.toImageVector(): ImageVector = when (this) {
    LiveEventIconType.LAPTOP   -> Icons.Default.Laptop
    LiveEventIconType.VOTE     -> Icons.Default.HowToVote
    LiveEventIconType.SOCCER   -> Icons.Default.SportsSoccer
    LiveEventIconType.CAMPAIGN -> Icons.Default.Campaign
}

/** Live Now section: pulsing header + horizontal card row. */
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

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .graphicsLayer { alpha = pulseAlpha }
                    .background(LiveRed),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Live Now",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "View All",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onEvent(HomeEvent.ViewAllLiveClicked) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            .width(280.dp)
            .height(176.dp)
            .then(
                if (event.isPulsing) {
                    Modifier.border(
                        width = 2.dp,
                        color = LiveRed.copy(alpha = pulseAlpha),
                        shape = RoundedCornerShape(16.dp),
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp),
                    )
                },
            )
            .clip(RoundedCornerShape(16.dp))
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
                modifier = Modifier.size(64.dp),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                    ),
                ),
        )

        // LIVE badge
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(LiveRed, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Podcasts,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
            Text(text = "LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        // Viewer count badge
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.RemoveRedEye,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp),
            )
            Text(text = formatViewerCount(event.viewerCount), fontSize = 10.sp, color = Color.White)
        }

        // Title & subtitle
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp),
        ) {
            Text(
                text = event.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = event.subtitle,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private fun formatViewerCount(count: Int): String =
    if (count >= 1000) "${count / 1000}.${(count % 1000) / 100}k watching"
    else "$count watching"
