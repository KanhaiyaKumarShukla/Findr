package com.exa.android.reflekt.feature.chat.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    fun onEvent(event: ChatDetailEvent) {
        when (event) {
            is ChatDetailEvent.MessageTextChanged -> {
                _uiState.update { it.copy(inputText = event.text) }
            }
            is ChatDetailEvent.SendClicked -> { /* TODO */ }
            is ChatDetailEvent.AttachClicked -> { /* TODO */ }
            is ChatDetailEvent.MicClicked -> { /* TODO */ }
        }
    }

    private fun createInitialState(): ChatDetailUiState = ChatDetailUiState(
        chatName = "Advanced Physics II",
        memberInfo = "4 members studying",
        isOnline = true,
        avatarInitial = "A",
        avatarColorArgb = 0xFF6366F1,
        messages = listOf(
            ChatMessage(
                id = "sep_1",
                type = MessageType.DATE_SEPARATOR,
                content = "Today",
            ),
            ChatMessage(
                id = "msg_1",
                content = "Hey team! Did anyone manage to solve problem 4 from the quantum mechanics worksheet?",
                timestamp = "Sent 10:24 AM",
                isOutgoing = true,
                reactions = listOf(MessageReaction("\uD83E\uDD14", 2)),
            ),
            ChatMessage(
                id = "msg_2_img",
                senderName = "Alex",
                senderInitial = "A",
                senderColorArgb = 0xFF13B6EC,
                type = MessageType.IMAGE,
                content = "Whiteboard sketch",
                timestamp = "Alex \u2022 10:26 AM",
            ),
            ChatMessage(
                id = "msg_2_text",
                senderName = "Alex",
                senderInitial = "A",
                senderColorArgb = 0xFF13B6EC,
                content = "I think I got it! Check my whiteboard sketch above. It's all about the Schr\u00F6dinger derivation.",
                timestamp = "Alex \u2022 10:26 AM",
                reactions = listOf(MessageReaction("\u2764\uFE0F")),
            ),
            ChatMessage(
                id = "msg_3_file",
                senderName = "Sarah",
                senderInitial = "S",
                senderColorArgb = 0xFFA855F7,
                type = MessageType.FILE,
                content = "Lecture_Notes_W4.pdf",
                fileName = "Lecture_Notes_W4.pdf",
                fileSize = "2.4 MB \u2022 PDF Document",
                timestamp = "Sarah \u2022 10:28 AM",
            ),
            ChatMessage(
                id = "msg_4_video",
                type = MessageType.VIDEO,
                content = "Physics lab experiment",
                timestamp = "You \u2022 10:30 AM",
                isOutgoing = true,
                videoDuration = "0:15",
            ),
        ),
    )
}
