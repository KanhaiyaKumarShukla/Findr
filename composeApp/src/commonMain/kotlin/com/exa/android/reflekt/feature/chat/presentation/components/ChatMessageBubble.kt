package com.exa.android.reflekt.feature.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.feature.chat.presentation.ChatMessage
import com.exa.android.reflekt.feature.chat.presentation.MessageReaction
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun ChatMessageBubble(message: ChatMessage) {
    val dimens = DonutTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.spacing16, vertical = dimens.spacing4),
        horizontalAlignment = if (message.isOutgoing) Alignment.End else Alignment.Start,
    ) {
        if (message.isOutgoing) {
            OutgoingBubble(message)
        } else {
            IncomingBubble(message)
        }
    }
}

@Composable
private fun OutgoingBubble(message: ChatMessage) {
    val dimens = DonutTheme.dimens
    val shape = RoundedCornerShape(
        topStart = DonutRadius.panel, topEnd = 0.dp,
        bottomStart = DonutRadius.panel, bottomEnd = DonutRadius.panel,
    )

    Column(horizontalAlignment = Alignment.End) {
        Box {
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = dimens.spacing20, vertical = dimens.spacing12),
            ) {
                Text(
                    text = message.content,
                    fontSize = DonutTextSize.body,
                    color = Color.White,
                )
            }
            if (message.reactions.isNotEmpty()) {
                ReactionBadges(
                    reactions = message.reactions,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(top = dimens.spacing4),
                )
            }
        }
        if (message.reactions.isNotEmpty()) {
            Box(modifier = Modifier.size(dimens.spacing8))
        }
        Text(
            text = message.timestamp,
            fontSize = DonutTextSize.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = dimens.spacing8),
        )
    }
}

@Composable
private fun IncomingBubble(message: ChatMessage) {
    val dimens = DonutTheme.dimens
    val avatarColor = Color(message.senderColorArgb.toInt() or (0xFF shl 24))
    val shape = RoundedCornerShape(
        topStart = DonutRadius.panel, topEnd = DonutRadius.panel,
        bottomStart = 0.dp, bottomEnd = DonutRadius.panel,
    )

    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimens.spacing8),
            verticalAlignment = Alignment.Bottom,
        ) {
            // Sender avatar
            Box(
                modifier = Modifier
                    .size(dimens.spacing32)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = message.senderInitial,
                    fontSize = DonutTextSize.small,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            Box {
                Box(
                    modifier = Modifier
                        .clip(shape)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = dimens.spacing20, vertical = dimens.spacing12),
                ) {
                    Text(
                        text = message.content,
                        fontSize = DonutTextSize.body,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                if (message.reactions.isNotEmpty()) {
                    ReactionBadges(
                        reactions = message.reactions,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(top = dimens.spacing4),
                    )
                }
            }
        }
        if (message.reactions.isNotEmpty()) {
            Box(modifier = Modifier.size(dimens.spacing8))
        }
        Text(
            text = message.timestamp,
            fontSize = DonutTextSize.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = dimens.spacing40, top = dimens.spacing2),
        )
    }
}

@Composable
private fun ReactionBadges(
    reactions: List<MessageReaction>,
    modifier: Modifier = Modifier,
) {
    val dimens = DonutTheme.dimens

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.spacing4),
    ) {
        reactions.forEach { reaction ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(dimens.borderThin, DonutTheme.colorTokens.cardBorder, RoundedCornerShape(50))
                    .padding(horizontal = dimens.spacing6, vertical = dimens.spacing2),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.spacing4),
            ) {
                Text(text = reaction.emoji, fontSize = DonutTextSize.small)
                if (reaction.count > 1) {
                    Text(
                        text = reaction.count.toString(),
                        fontSize = DonutTextSize.caption,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
