package com.exa.android.reflekt.feature.post.presentation.create_post

sealed class CreatePostEvent {
    data class TitleChanged(val title: String) : CreatePostEvent()
    data class ContentChanged(val content: String) : CreatePostEvent()
    data class TagInputChanged(val input: String) : CreatePostEvent()
    data object AddTag : CreatePostEvent()
    data class RemoveTag(val tag: String) : CreatePostEvent()
    data object AddMedia : CreatePostEvent()
    data class MediaSelected(val name: String, val size: String, val type: MediaType) : CreatePostEvent()
    data object DismissMediaPicker : CreatePostEvent()
    data class RemoveMedia(val index: Int) : CreatePostEvent()
    data object SharePost : CreatePostEvent()
    data object DismissError : CreatePostEvent()
}
