package com.exa.android.reflekt.feature.post.presentation.create_project

sealed class CreateProjectEvent {
    data class ProjectTitleChanged(val title: String) : CreateProjectEvent()
    data class DescriptionChanged(val description: String) : CreateProjectEvent()
    data class RoleNameChanged(val roleName: String) : CreateProjectEvent()
    data class SkillInputChanged(val input: String) : CreateProjectEvent()
    data object AddSkill : CreateProjectEvent()
    data class RemoveSkill(val skill: String) : CreateProjectEvent()
    data object IncrementOpenings : CreateProjectEvent()
    data object DecrementOpenings : CreateProjectEvent()
    data object UploadAttachment : CreateProjectEvent()
    data class FileSelected(val fileName: String, val fileSize: String) : CreateProjectEvent()
    data object DismissFilePicker : CreateProjectEvent()
    data object RemoveAttachment : CreateProjectEvent()
    data object PreviewClicked : CreateProjectEvent()
    data object PostProject : CreateProjectEvent()
    data object DismissError : CreateProjectEvent()
}
