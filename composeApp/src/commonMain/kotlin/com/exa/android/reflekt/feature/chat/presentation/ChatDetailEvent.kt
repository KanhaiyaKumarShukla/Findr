package com.exa.android.reflekt.feature.chat.presentation

sealed class ChatDetailEvent {
    data class MessageTextChanged(val text: String) : ChatDetailEvent()
    data object SendClicked : ChatDetailEvent()
    data object AttachClicked : ChatDetailEvent()
    data object MicClicked : ChatDetailEvent()
}
