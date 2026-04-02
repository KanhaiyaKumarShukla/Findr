package com.exa.android.reflekt.ui.components.modifier

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/**
 * Debounces rapid taps — only the first click within [throttleMs] is forwarded.
 * Prevents double-submit on buttons and nav triggers.
 */
fun Modifier.throttleClick(
    throttleMs: Long = 600L,
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = composed {
    val mark = remember { TimeSource.Monotonic }
    var lastClick = remember { mark.markNow() - throttleMs.milliseconds }
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        enabled = enabled,
    ) {
        val now = mark.markNow()
        if ((now - lastClick) >= throttleMs.milliseconds) {
            lastClick = now
            onClick()
        }
    }
}
