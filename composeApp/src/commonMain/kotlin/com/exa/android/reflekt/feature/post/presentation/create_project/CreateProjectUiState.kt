package com.exa.android.reflekt.feature.post.presentation.create_project

data class CreateProjectUiState(
    val projectTitle: String = "",
    val description: String = "",
    val roleName: String = "",
    val techSkills: List<String> = emptyList(),
    val currentSkillInput: String = "",
    val numberOfOpenings: Int = 1,
    val attachedFileName: String? = null,
    val attachedFileSize: String? = null,
    val isPosting: Boolean = false,
    val errorMessage: String? = null,
    val isPostSuccess: Boolean = false,
    val showFilePickerDialog: Boolean = false,
)
