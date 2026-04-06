package com.exa.android.reflekt.feature.chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.chat.presentation.components.ChatAttachmentBubble
import com.exa.android.reflekt.feature.chat.presentation.components.ChatDetailTopBar
import com.exa.android.reflekt.feature.chat.presentation.components.ChatInputBar
import com.exa.android.reflekt.feature.chat.presentation.components.ChatMessageBubble
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel,
    onNavigateBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val dimens = DonutTheme.dimens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ChatDetailTopBar(state = uiState, onBackClick = onNavigateBack)

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.dividerThickness)
                .background(DonutTheme.colorTokens.cardBorder.copy(alpha = 0.3f)),
        )

        // Messages area
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = false,
            ) {
                item { Spacer(modifier = Modifier.height(dimens.spacing16)) }

                items(uiState.messages, key = { it.id }) { message ->
                    when (message.type) {
                        MessageType.DATE_SEPARATOR -> DateSeparator(message.content)
                        MessageType.TEXT -> ChatMessageBubble(message)
                        MessageType.IMAGE,
                        MessageType.FILE,
                        MessageType.VIDEO -> ChatAttachmentBubble(message)
                    }
                    Spacer(modifier = Modifier.height(dimens.spacing8))
                }

                item { Spacer(modifier = Modifier.height(dimens.spacing100)) }
            }

            // Input bar overlaid at bottom
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                ChatInputBar(inputText = uiState.inputText, onEvent = viewModel::onEvent)
            }
        }
    }
}

@Composable
private fun DateSeparator(text: String) {
    val dimens = DonutTheme.dimens

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimens.spacing8),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text.uppercase(),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                .padding(horizontal = dimens.spacing16, vertical = dimens.spacing4),
            fontSize = DonutTextSize.caption,
            fontWeight = FontWeight.Bold,
            letterSpacing = DonutTextSize.micro,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        )
    }
}
