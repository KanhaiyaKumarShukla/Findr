package com.exa.android.reflekt.feature.post.presentation.create_post

enum class MediaType { IMAGE, VIDEO }

data class AttachedMedia(
    val name: String,
    val size: String,
    val type: MediaType,
)

data class CreatePostUiState(
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    val currentTagInput: String = "",
    val mediaItems: List<AttachedMedia> = emptyList(),
    val showMediaPickerDialog: Boolean = false,
    val isPosting: Boolean = false,
    val errorMessage: String? = null,
    val isPostSuccess: Boolean = false,
)
