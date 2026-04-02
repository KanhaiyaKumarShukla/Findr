package com.exa.android.reflekt.ui.components.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a dashed border around the composable without affecting its layout.
 *
 * @param color   Border color.
 * @param strokeWidth   Width of the dash stroke.
 * @param dashWidth   Length of each dash segment.
 * @param gapWidth    Gap between segments.
 * @param cornerRadius  Corner rounding in pixels (use with `clip` for matching shape).
 */
fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp = 1.dp,
    dashWidth: Float = 10f,
    gapWidth: Float = 5f,
    cornerRadius: Float = 0f,
): Modifier = this.drawWithContent {
    drawContent()
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, gapWidth)),
    )
    if (cornerRadius > 0f) {
        drawRoundRect(
            color = color,
            style = stroke,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
        )
    } else {
        drawRect(color = color, style = stroke)
    }
}
