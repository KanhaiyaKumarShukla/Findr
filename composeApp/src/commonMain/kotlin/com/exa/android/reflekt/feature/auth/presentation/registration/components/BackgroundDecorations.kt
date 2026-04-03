package com.exa.android.reflekt.feature.auth.presentation.registration.components

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
import com.exa.android.reflekt.ui.theme.appColors

@Composable
internal fun BackgroundDecorations() {
    val appColors = MaterialTheme.appColors

    // Top-right glow
    Box(
        modifier = Modifier
            .offset(x = 120.dp, y = (-40).dp)
            .size(250.dp)
            .blur(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
    )

    // Left-center glow (purple tint)
    Box(
        modifier = Modifier
            .offset(x = (-60).dp, y = 350.dp)
            .size(200.dp)
            .blur(120.dp)
            .clip(CircleShape)
            .background(appColors.purpleGlow.copy(alpha = 0.08f)),
    )
}
