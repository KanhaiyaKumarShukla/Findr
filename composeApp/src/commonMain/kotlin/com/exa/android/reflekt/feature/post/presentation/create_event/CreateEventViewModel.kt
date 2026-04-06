package com.exa.android.reflekt.feature.post.presentation.create_event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exa.android.reflekt.core.data.EventRepository
import com.exa.android.reflekt.core.data.PostedEvent
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateEventViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: CreateEventEvent) {
        when (event) {
            is CreateEventEvent.EventTypeChanged -> {
                _uiState.update { it.copy(eventType = event.type) }
            }

            is CreateEventEvent.TopicChanged -> {
                _uiState.update { it.copy(topic = event.topic) }
            }

            is CreateEventEvent.DescriptionChanged -> {
                _uiState.update { it.copy(description = event.description) }
            }

            is CreateEventEvent.DateChanged -> {
                _uiState.update { it.copy(date = event.date) }
            }

            is CreateEventEvent.TimeChanged -> {
                _uiState.update { it.copy(time = event.time) }
            }

            is CreateEventEvent.LocationChanged -> {
                _uiState.update { it.copy(location = event.location) }
            }

            is CreateEventEvent.MeetingLinkChanged -> {
                _uiState.update { it.copy(meetingLink = event.link) }
            }

            is CreateEventEvent.IncrementParticipants -> {
                _uiState.update { it.copy(maxParticipants = (it.maxParticipants + 1).coerceAtMost(100)) }
            }

            is CreateEventEvent.DecrementParticipants -> {
                _uiState.update { it.copy(maxParticipants = (it.maxParticipants - 1).coerceAtLeast(2)) }
            }

            is CreateEventEvent.UploadCoverImage -> {
                // TODO: integrate file picker
            }

            is CreateEventEvent.RemoveCoverImage -> {
                _uiState.update { it.copy(coverImagePath = null) }
            }

            is CreateEventEvent.PostEvent -> postEvent()

            is CreateEventEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun postEvent() {
        val state = _uiState.value
        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "Please fill in all required fields.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true) }
            delay(1500) // simulate network
            EventRepository.addEvent(
                PostedEvent(
                    id = "event_${Random.nextInt(100_000)}",
                    topic = state.topic,
                    description = state.description,
                    date = state.date,
                    time = state.time,
                    location = if (state.eventType == EventType.PHYSICAL) state.location else state.meetingLink,
                    isVirtual = state.eventType == EventType.VIRTUAL,
                ),
            )
            _uiState.update { it.copy(isPosting = false, isPostSuccess = true) }
        }
    }
}
