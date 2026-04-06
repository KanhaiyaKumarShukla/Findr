package com.exa.android.reflekt.ui.components.loading

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.ui.theme.DonutTheme

// ─────────────────────────────────────────────────────────────
//  Three-dot pulse indicator
// ─────────────────────────────────────────────────────────────

/**
 * Three dots that pulse in sequence — lightweight "typing" style loader.
 *
 * @param dotColor  Color of each dot (defaults to [Primary]).
 * @param dotSize   Diameter of each dot.
 * @param spacing   Gap between dots.
 */
@Composable
fun ThreeDotLoadingIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = DonutTheme.colorTokens.primary,
    dotSize: Dp = DonutTheme.dimens.spacing10,
    spacing: Dp = DonutTheme.dimens.spacing6,
) {
    val transition = rememberInfiniteTransition(label = "three_dot")
    val dotCount = 3

    val scales = (0 until dotCount).map { index ->
        val scale by transition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1_200
                    0.6f at (index * 200) with LinearEasing
                    1.0f at (index * 200 + 200) with LinearEasing
                    0.6f at (index * 200 + 400) with LinearEasing
                },
                repeatMode = RepeatMode.Restart,
            ),
            label = "dot_scale_$index",
        )
        scale
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        scales.forEach { scale ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(scale)
                    .background(color = dotColor, shape = CircleShape),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Standard circular indicator (convenience wrapper)
// ─────────────────────────────────────────────────────────────

/**
 * Material3 circular progress indicator with consistent brand color.
 */
@Composable
fun FindrCircularLoader(
    modifier: Modifier = Modifier,
    size: Dp = DonutTheme.dimens.spacing32,
    color: Color = DonutTheme.colorTokens.primary,
    strokeWidth: Dp = DonutTheme.dimens.borderThick,
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        color = color,
        strokeWidth = strokeWidth,
        trackColor = color.copy(alpha = 0.2f),
    )
}

// ─────────────────────────────────────────────────────────────
//  Full-screen loading overlay
// ─────────────────────────────────────────────────────────────

/**
 * Centered full-screen loading overlay using [ThreeDotLoadingIndicator].
 * Wrap the content in a [Box] and conditionally show this on top.
 */
@Composable
fun FindrFullScreenLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        ThreeDotLoadingIndicator()
    }
}
