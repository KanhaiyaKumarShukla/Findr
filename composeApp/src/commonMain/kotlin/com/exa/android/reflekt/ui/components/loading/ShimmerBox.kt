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

// Shared shimmer brush builder

@Composable
private fun rememberShimmerBrush(
    baseColor: Color = Color(0xFFE0E0E0),
    highlightColor: Color = Color(0xFFF5F5F5),
    durationMs: Int = 1_200,
): Brush {
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

// ─────────────────────────────────────────────────────────────
//  Primitive shimmer placeholder
// ─────────────────────────────────────────────────────────────

/**
 * A rectangular shimmer placeholder.
 *
 * @param width   Explicit width — leave null for [Modifier.fillMaxWidth].
 * @param height  Height of the placeholder.
 * @param cornerRadius  Corner rounding.
 */
@Composable
fun ShimmerBox(
    height: Dp,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    cornerRadius: Dp = 8.dp,
) {
    val brush = rememberShimmerBrush()
    val sizeModifier = if (width != null) modifier.size(width, height) else modifier.fillMaxWidth().height(height)
    Box(
        modifier = sizeModifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush),
    )
}

/**
 * A circular shimmer placeholder — useful for avatar/icon skeletons.
 */
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

// ─────────────────────────────────────────────────────────────
//  Compound skeleton: user card row
// ─────────────────────────────────────────────────────────────

/**
 * Pre-built skeleton that mimics a user/profile card row.
 * Use inside a LazyColumn while content is loading.
 */
@Composable
fun UserCardSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        ShimmerCircle(size = 48.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            ShimmerBox(height = 14.dp, cornerRadius = 4.dp)
            Spacer(Modifier.height(8.dp))
            ShimmerBox(height = 12.dp, width = 140.dp, cornerRadius = 4.dp)
        }
    }
}
