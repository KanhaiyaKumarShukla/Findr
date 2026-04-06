package com.exa.android.reflekt.ui.components.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius

@Composable
private fun rememberShimmerBrush(
    durationMs: Int = 1_200,
): Brush {
    val baseColor: Color = DonutTheme.colorTokens.surfaceVariant
    val highlightColor: Color = DonutTheme.colorTokens.outlineVariant
    val transition = rememberInfiniteTransition(label = "shimmer_box")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_offset",
    )
    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(offset, offset),
        end = Offset(offset + 400f, offset + 400f),
    )
}

// Rectangular shimmer placeholder.
@Composable
fun ShimmerBox(
    height: Dp,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    cornerRadius: Dp = DonutRadius.lg,
) {
    val brush = rememberShimmerBrush()
    val sizeModifier = if (width != null) modifier.size(width, height) else modifier.fillMaxWidth().height(height)
    Box(
        modifier = sizeModifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush),
    )
}

// Circular shimmer placeholder for avatar/icon skeletons.
@Composable
fun ShimmerCircle(size: Dp, modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(brush),
    )
}

// Pre-built skeleton that mimics a user/profile card row.
@Composable
fun UserCardSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing12),
    ) {
        ShimmerCircle(size = DonutTheme.dimens.iconSize3X)
        Spacer(Modifier.width(DonutTheme.dimens.spacing12))
        Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(height = DonutTheme.dimens.spacing14, cornerRadius = DonutRadius.sm)
            Spacer(Modifier.height(DonutTheme.dimens.spacing8))
            ShimmerBox(height = DonutTheme.dimens.spacing12, width = DonutTheme.dimens.spacing120, cornerRadius = DonutRadius.sm)
        }
    }
}
