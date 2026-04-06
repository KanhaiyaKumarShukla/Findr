package com.exa.android.reflekt.feature.chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.chat.presentation.components.ChatConversationItem
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (conversationId: String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val dimens = DonutTheme.dimens

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "CHAT",
                        fontSize = DonutTextSize.small,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = DonutTextSize.micro,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, "Back",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(ChatEvent.NewChatClicked) },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.navigationBarsPadding(),
            ) {
                Icon(Icons.Default.Edit, "New Chat", tint = Color.White)
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            items(uiState.conversations, key = { it.id }) { conversation ->
                ChatConversationItem(
                    conversation = conversation,
                    onClick = { onNavigateToDetail(conversation.id) },
                )
                // Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = dimens.spacing80)
                        .height(dimens.dividerThickness)
                        .background(DonutTheme.colorTokens.cardBorder.copy(alpha = 0.3f)),
                )
            }
        }
    }
}
