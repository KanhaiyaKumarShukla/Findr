package com.exa.android.reflekt.feature.chat.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.ConversationClicked -> { /* TODO */ }
            is ChatEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            is ChatEvent.NewChatClicked -> { /* TODO */ }
        }
    }

    private fun createInitialState(): ChatUiState = ChatUiState(
        conversations = listOf(
            ChatConversation(
                id = "chat_1",
                name = "David Chen",
                lastMessage = "Hey, are you joining the hackathon this weekend?",
                timestamp = "2m",
                unreadCount = 3,
                isOnline = true,
                avatarInitial = "D",
                avatarColorArgb = 0xFF13B6EC,
            ),
            ChatConversation(
                id = "chat_2",
                name = "Campus Connect Team",
                lastMessage = "Sarah: I pushed the new UI changes",
                timestamp = "15m",
                unreadCount = 7,
                isOnline = false,
                avatarInitial = "C",
                avatarColorArgb = 0xFF6366F1,
            ),
            ChatConversation(
                id = "chat_3",
                name = "Elena Rodriguez",
                lastMessage = "Thanks for helping with the Docker issue!",
                timestamp = "1h",
                isOnline = true,
                avatarInitial = "E",
                avatarColorArgb = 0xFFEF4444,
            ),
            ChatConversation(
                id = "chat_4",
                name = "Alex Rodriguez",
                lastMessage = "The capstone demo went great!",
                timestamp = "2h",
                isOnline = false,
                avatarInitial = "A",
                avatarColorArgb = 0xFF22C55E,
            ),
            ChatConversation(
                id = "chat_5",
                name = "Priya Sharma",
                lastMessage = "Have you tried the new bubble tea place?",
                timestamp = "5h",
                unreadCount = 1,
                isOnline = false,
                avatarInitial = "P",
                avatarColorArgb = 0xFFA855F7,
            ),
            ChatConversation(
                id = "chat_6",
                name = "Study Group - CS301",
                lastMessage = "Prof. Kim extended the deadline",
                timestamp = "1d",
                isOnline = false,
                avatarInitial = "S",
                avatarColorArgb = 0xFFF97316,
            ),
        ),
    )
}
