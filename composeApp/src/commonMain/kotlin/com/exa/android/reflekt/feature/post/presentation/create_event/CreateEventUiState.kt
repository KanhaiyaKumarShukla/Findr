package com.exa.android.reflekt.feature.post.presentation.create_event

enum class EventType { PHYSICAL, VIRTUAL }

data class CreateEventUiState(
    val eventType: EventType = EventType.PHYSICAL,
    val topic: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val meetingLink: String = "",
    val maxParticipants: Int = 10,
    val coverImagePath: String? = null,
    val isPosting: Boolean = false,
    val errorMessage: String? = null,
    val isPostSuccess: Boolean = false,
) {
    val isFormValid: Boolean
        get() = topic.isNotBlank() &&
                description.isNotBlank() &&
                date.isNotBlank() &&
                time.isNotBlank() &&
                if (eventType == EventType.PHYSICAL) location.isNotBlank() else meetingLink.isNotBlank()
}
