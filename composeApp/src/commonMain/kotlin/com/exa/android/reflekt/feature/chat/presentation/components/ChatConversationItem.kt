package com.exa.android.reflekt.feature.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import com.exa.android.reflekt.feature.chat.presentation.ChatConversation
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun ChatConversationItem(
    conversation: ChatConversation,
    onClick: () -> Unit,
) {
    val dimens = DonutTheme.dimens
    val avatarColor = Color(conversation.avatarColorArgb.toInt() or (0xFF shl 24))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            )
            .padding(horizontal = dimens.spacing20, vertical = dimens.spacing12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spacing12),
    ) {
        // Avatar with online indicator
        Box {
            Box(
                modifier = Modifier
                    .size(dimens.spacing52)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = conversation.avatarInitial,
                    fontSize = DonutTextSize.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
            if (conversation.isOnline) {
                val bgColor = MaterialTheme.colorScheme.background
                Box(
                    modifier = Modifier
                        .size(dimens.spacing14)
                        .align(Alignment.BottomEnd)
                        .drawBehind { drawCircle(bgColor) }
                        .padding(dimens.spacing2)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E)),
                )
            }
        }

        // Name + last message
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimens.spacing4),
        ) {
            Text(
                text = conversation.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = conversation.lastMessage,
                fontSize = DonutTextSize.body,
                color = if (conversation.unreadCount > 0)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // Timestamp + unread badge
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(dimens.spacing6),
        ) {
            Text(
                text = conversation.timestamp,
                fontSize = DonutTextSize.small,
                color = if (conversation.unreadCount > 0)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
            if (conversation.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(dimens.spacing20)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = conversation.unreadCount.toString(),
                        fontSize = DonutTextSize.caption,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }
        }
    }
}
