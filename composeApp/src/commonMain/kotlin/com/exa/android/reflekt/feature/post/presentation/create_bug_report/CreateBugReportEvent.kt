package com.exa.android.reflekt.feature.post.presentation.create_bug_report

sealed class CreateBugReportEvent {
    data class TitleChanged(val title: String) : CreateBugReportEvent()
    data class StepsChanged(val steps: String) : CreateBugReportEvent()
    data class ExpectedBehaviorChanged(val expected: String) : CreateBugReportEvent()
    data class SeveritySelected(val severity: BugSeverity) : CreateBugReportEvent()
    data object UploadAttachment : CreateBugReportEvent()
    data class FileSelected(val fileName: String, val fileSize: String) : CreateBugReportEvent()
    data object DismissFilePicker : CreateBugReportEvent()
    data object RemoveAttachment : CreateBugReportEvent()
    data object SubmitReport : CreateBugReportEvent()
    data object DismissError : CreateBugReportEvent()
}
