package com.exa.android.reflekt.feature.post.presentation.create_post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatePostViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    fun onEvent(event: CreatePostEvent) {
        when (event) {
            is CreatePostEvent.TitleChanged -> {
                _uiState.update { it.copy(title = event.title, errorMessage = null) }
            }

            is CreatePostEvent.ContentChanged -> {
                _uiState.update { it.copy(content = event.content, errorMessage = null) }
            }

            is CreatePostEvent.TagInputChanged -> {
                _uiState.update { it.copy(currentTagInput = event.input) }
            }

            is CreatePostEvent.AddTag -> {
                val current = _uiState.value
                val tag = current.currentTagInput.trim()
                if (tag.isNotEmpty() && tag !in current.tags) {
                    _uiState.update {
                        it.copy(tags = it.tags + tag, currentTagInput = "")
                    }
                }
            }

            is CreatePostEvent.RemoveTag -> {
                _uiState.update { it.copy(tags = it.tags - event.tag) }
            }

            is CreatePostEvent.AddMedia -> {
                _uiState.update { it.copy(showMediaPickerDialog = true) }
            }

            is CreatePostEvent.MediaSelected -> {
                _uiState.update {
                    it.copy(
                        mediaItems = it.mediaItems + AttachedMedia(event.name, event.size, event.type),
                        showMediaPickerDialog = false,
                    )
                }
            }

            is CreatePostEvent.DismissMediaPicker -> {
                _uiState.update { it.copy(showMediaPickerDialog = false) }
            }

            is CreatePostEvent.RemoveMedia -> {
                _uiState.update {
                    it.copy(mediaItems = it.mediaItems.filterIndexed { i, _ -> i != event.index })
                }
            }

            is CreatePostEvent.SharePost -> sharePost()

            is CreatePostEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun sharePost() {
        val state = _uiState.value
        val error = validate(state)
        if (error != null) {
            _uiState.update { it.copy(errorMessage = error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isPosting = true) }
            // TODO: Replace with actual API call
            delay(1500)
            _uiState.update { it.copy(isPosting = false, isPostSuccess = true) }
        }
    }

    private fun validate(state: CreatePostUiState): String? {
        if (state.title.isBlank()) return "Please enter a title"
        if (state.content.isBlank()) return "Please write some content"
        return null
    }
}
