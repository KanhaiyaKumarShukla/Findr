package com.exa.android.reflekt.feature.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.feature.chat.presentation.ChatMessage
import com.exa.android.reflekt.feature.chat.presentation.MessageType
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun ChatAttachmentBubble(message: ChatMessage) {
    val dimens = DonutTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.spacing16, vertical = dimens.spacing4),
        horizontalAlignment = if (message.isOutgoing) Alignment.End else Alignment.Start,
    ) {
        when (message.type) {
            MessageType.IMAGE -> ImageBubble(message)
            MessageType.FILE -> FileBubble(message)
            MessageType.VIDEO -> VideoBubble(message)
            else -> {}
        }
        Text(
            text = message.timestamp,
            fontSize = DonutTextSize.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(
                start = if (!message.isOutgoing) dimens.spacing40 else dimens.spacing0,
                top = dimens.spacing2,
            ),
        )
    }
}

@Composable
private fun ImageBubble(message: ChatMessage) {
    val dimens = DonutTheme.dimens
    val avatarColor = Color(message.senderColorArgb.toInt() or (0xFF shl 24))
    val shape = RoundedCornerShape(
        topStart = DonutRadius.panel, topEnd = DonutRadius.panel,
        bottomStart = 0.dp, bottomEnd = DonutRadius.panel,
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.spacing8),
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!message.isOutgoing) {
            Box(
                modifier = Modifier.size(dimens.spacing32).clip(CircleShape).background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(message.senderInitial, fontSize = DonutTextSize.small, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(shape)
                .border(dimens.borderThin, DonutTheme.colorTokens.cardBorder, shape),
        ) {
            // Gradient placeholder for image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                avatarColor.copy(alpha = 0.3f),
                            ),
                        ),
                    ),
            )
            // Fullscreen button overlay
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(dimens.spacing8)
                    .size(dimens.spacing32)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Fullscreen, "Fullscreen", tint = Color.White, modifier = Modifier.size(dimens.iconSizeBase))
            }
        }
    }
}

@Composable
private fun FileBubble(message: ChatMessage) {
    val dimens = DonutTheme.dimens
    val avatarColor = Color(message.senderColorArgb.toInt() or (0xFF shl 24))
    val shape = RoundedCornerShape(
        topStart = DonutRadius.panel, topEnd = DonutRadius.panel,
        bottomStart = 0.dp, bottomEnd = DonutRadius.panel,
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.spacing8),
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!message.isOutgoing) {
            Box(
                modifier = Modifier.size(dimens.spacing32).clip(CircleShape).background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(message.senderInitial, fontSize = DonutTextSize.small, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        Row(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface)
                .border(dimens.borderThin, DonutTheme.colorTokens.cardBorder, shape)
                .padding(dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spacing12),
        ) {
            // PDF icon
            Box(
                modifier = Modifier
                    .size(dimens.spacing48)
                    .clip(RoundedCornerShape(DonutRadius.lg))
                    .background(Color(0xFFEF4444).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Description, "PDF", tint = Color(0xFFEF4444), modifier = Modifier.size(dimens.iconSize2Xl))
            }
            // File info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = message.fileSize,
                    fontSize = DonutTextSize.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // Download button
            Box(
                modifier = Modifier
                    .size(dimens.spacing32)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.Download, "Download", tint = Color.White, modifier = Modifier.size(dimens.iconSizeBase))
            }
        }
    }
}

@Composable
private fun VideoBubble(message: ChatMessage) {
    val dimens = DonutTheme.dimens
    val shape = if (message.isOutgoing) {
        RoundedCornerShape(topStart = DonutRadius.panel, topEnd = 0.dp, bottomStart = DonutRadius.panel, bottomEnd = DonutRadius.panel)
    } else {
        RoundedCornerShape(topStart = DonutRadius.panel, topEnd = DonutRadius.panel, bottomStart = 0.dp, bottomEnd = DonutRadius.panel)
    }

    Box(
        modifier = Modifier
            .widthIn(max = 260.dp)
            .clip(shape)
            .border(dimens.borderThin, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape),
    ) {
        // Gradient placeholder for video
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ),
                ),
        )
        // Play button overlay
        Box(
            modifier = Modifier.fillMaxSize().aspectRatio(16f / 9f),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(dimens.spacing56)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(dimens.borderThin, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(dimens.spacing36))
            }
        }
        // Duration badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(dimens.spacing8)
                .clip(RoundedCornerShape(DonutRadius.sm))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = dimens.spacing8, vertical = dimens.spacing2),
        ) {
            Text(
                text = message.videoDuration,
                fontSize = DonutTextSize.caption,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}
