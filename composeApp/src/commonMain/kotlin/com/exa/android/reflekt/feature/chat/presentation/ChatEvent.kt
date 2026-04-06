package com.exa.android.reflekt.feature.chat.presentation

sealed class ChatEvent {
    data class ConversationClicked(val conversationId: String) : ChatEvent()
    data class SearchQueryChanged(val query: String) : ChatEvent()
    data object NewChatClicked : ChatEvent()
}
