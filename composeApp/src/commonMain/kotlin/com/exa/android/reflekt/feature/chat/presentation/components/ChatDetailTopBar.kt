package com.exa.android.reflekt.feature.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.chat.presentation.ChatDetailUiState
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun ChatDetailTopBar(
    state: ChatDetailUiState,
    onBackClick: () -> Unit,
) {
    val dimens = DonutTheme.dimens
    val avatarColor = Color(state.avatarColorArgb.toInt() or (0xFF shl 24))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = dimens.spacing12, vertical = dimens.spacing12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spacing12),
    ) {
        // Back button
        TopBarIconButton(onClick = onBackClick) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack, "Back",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(dimens.iconSizeLarge),
            )
        }

        // Avatar with online indicator
        Box {
            Box(
                modifier = Modifier
                    .size(dimens.spacing40)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = state.avatarInitial,
                    fontSize = DonutTextSize.body,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            if (state.isOnline) {
                val bgColor = MaterialTheme.colorScheme.background
                Box(
                    modifier = Modifier
                        .size(dimens.spacing12)
                        .align(Alignment.BottomEnd)
                        .drawBehind { drawCircle(bgColor) }
                        .padding(dimens.spacing2)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E)),
                )
            }
        }

        // Chat info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = state.chatName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = state.memberInfo,
                fontSize = DonutTextSize.caption,
                fontWeight = FontWeight.Medium,
                letterSpacing = DonutTextSize.micro,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        // More button
        TopBarIconButton(onClick = { }) {
            Icon(
                Icons.Default.MoreVert, "More",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(dimens.iconSizeLarge),
            )
        }
    }
}

@Composable
private fun TopBarIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(DonutTheme.dimens.spacing40)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) { content() }
}
