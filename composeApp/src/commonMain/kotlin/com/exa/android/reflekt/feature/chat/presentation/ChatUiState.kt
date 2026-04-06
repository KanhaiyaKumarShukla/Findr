package com.exa.android.reflekt.feature.chat.presentation

data class ChatConversation(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val avatarInitial: String = "",
    val avatarColorArgb: Long = 0xFF13B6EC,
)

data class ChatUiState(
    val conversations: List<ChatConversation> = emptyList(),
    val searchQuery: String = "",
)
