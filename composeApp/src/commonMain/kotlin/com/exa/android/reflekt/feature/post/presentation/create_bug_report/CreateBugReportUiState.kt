package com.exa.android.reflekt.feature.post.presentation.create_bug_report

enum class BugSeverity(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    CRITICAL("Critical"),
}

data class CreateBugReportUiState(
    val title: String = "",
    val stepsToReproduce: String = "",
    val expectedBehavior: String = "",
    val severity: BugSeverity = BugSeverity.MEDIUM,
    val attachedFileName: String? = null,
    val attachedFileSize: String? = null,
    val showFilePickerDialog: Boolean = false,
    val isPosting: Boolean = false,
    val errorMessage: String? = null,
    val isPostSuccess: Boolean = false,
)
