package com.exa.android.reflekt.feature.auth.presentation.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
internal fun BackgroundDecorations() {
    // Top-right glow
    Box(
        modifier = Modifier
            .offset(x = 100.dp, y = (-40).dp)
            .size(250.dp)
            .blur(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
    )

    // Left-center glow
    Box(
        modifier = Modifier
            .offset(x = (-60).dp, y = 250.dp)
            .size(300.dp)
            .blur(120.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
    )
}
