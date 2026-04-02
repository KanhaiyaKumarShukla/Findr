package com.exa.android.reflekt.feature.post.presentation.create_event

sealed class CreateEventEvent {
    data class EventTypeChanged(val type: EventType) : CreateEventEvent()
    data class TopicChanged(val topic: String) : CreateEventEvent()
    data class DescriptionChanged(val description: String) : CreateEventEvent()
    data class DateChanged(val date: String) : CreateEventEvent()
    data class TimeChanged(val time: String) : CreateEventEvent()
    data class LocationChanged(val location: String) : CreateEventEvent()
    data class MeetingLinkChanged(val link: String) : CreateEventEvent()
    data object IncrementParticipants : CreateEventEvent()
    data object DecrementParticipants : CreateEventEvent()
    data object UploadCoverImage : CreateEventEvent()
    data object RemoveCoverImage : CreateEventEvent()
    data object PostEvent : CreateEventEvent()
    data object DismissError : CreateEventEvent()
}
