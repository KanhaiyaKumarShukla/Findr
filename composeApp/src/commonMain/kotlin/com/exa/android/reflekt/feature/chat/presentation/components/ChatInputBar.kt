package com.exa.android.reflekt.feature.chat.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import com.exa.android.reflekt.feature.chat.presentation.ChatDetailEvent
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun ChatInputBar(
    inputText: String,
    onEvent: (ChatDetailEvent) -> Unit,
) {
    val dimens = DonutTheme.dimens
    val hasText = inputText.isNotBlank()

    // Gradient fade background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.background,
                    ),
                ),
            )
            .padding(horizontal = dimens.spacing16, vertical = dimens.spacing12)
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimens.spacing8),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spacing4),
        ) {
            // Add attachment button
            InputIconButton(
                onClick = { onEvent(ChatDetailEvent.AttachClicked) },
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Icon(
                    Icons.Default.Add, "Attach",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(dimens.iconSizeLarge),
                )
            }

            // Text input
            BasicTextField(
                value = inputText,
                onValueChange = { onEvent(ChatDetailEvent.MessageTextChanged(it)) },
                modifier = Modifier.weight(1f).padding(horizontal = dimens.spacing8),
                textStyle = TextStyle(
                    fontSize = DonutTextSize.body,
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box {
                        if (inputText.isEmpty()) {
                            Text(
                                text = "Message...",
                                fontSize = DonutTextSize.body,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            )
                        }
                        innerTextField()
                    }
                },
            )

            // Emoji button
            InputIconButton(
                onClick = { },
                backgroundColor = Color.Transparent,
            ) {
                Icon(
                    Icons.Default.SentimentSatisfied, "Emoji",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(dimens.iconSizeLarge),
                )
            }

            // Mic / Send button
            InputIconButton(
                onClick = {
                    if (hasText) onEvent(ChatDetailEvent.SendClicked)
                    else onEvent(ChatDetailEvent.MicClicked)
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    if (hasText) Icons.Default.Send else Icons.Default.Mic,
                    if (hasText) "Send" else "Mic",
                    tint = Color.White,
                    modifier = Modifier.size(dimens.iconSizeLarge),
                )
            }
        }
    }
}

@Composable
private fun InputIconButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(DonutTheme.dimens.spacing40)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) { content() }
}
