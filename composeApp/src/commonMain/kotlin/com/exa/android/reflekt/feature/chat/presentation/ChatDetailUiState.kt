package com.exa.android.reflekt.feature.chat.presentation

enum class MessageType {
    TEXT, IMAGE, FILE, VIDEO, DATE_SEPARATOR,
}

data class MessageReaction(
    val emoji: String,
    val count: Int = 1,
)

data class ChatMessage(
    val id: String,
    val senderName: String = "",
    val senderInitial: String = "",
    val senderColorArgb: Long = 0xFF13B6EC,
    val content: String = "",
    val timestamp: String = "",
    val isOutgoing: Boolean = false,
    val type: MessageType = MessageType.TEXT,
    val reactions: List<MessageReaction> = emptyList(),
    val fileName: String = "",
    val fileSize: String = "",
    val videoDuration: String = "",
)

data class ChatDetailUiState(
    val chatName: String = "",
    val memberInfo: String = "",
    val isOnline: Boolean = false,
    val avatarInitial: String = "",
    val avatarColorArgb: Long = 0xFF13B6EC,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
)
