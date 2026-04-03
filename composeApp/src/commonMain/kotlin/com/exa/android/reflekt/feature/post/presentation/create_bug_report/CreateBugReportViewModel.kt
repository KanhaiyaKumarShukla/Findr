package com.exa.android.reflekt.feature.post.presentation.create_bug_report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateBugReportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateBugReportUiState())
    val uiState: StateFlow<CreateBugReportUiState> = _uiState.asStateFlow()

    fun onEvent(event: CreateBugReportEvent) {
        when (event) {
            is CreateBugReportEvent.TitleChanged -> {
                _uiState.update { it.copy(title = event.title, errorMessage = null) }
            }

            is CreateBugReportEvent.StepsChanged -> {
                _uiState.update { it.copy(stepsToReproduce = event.steps, errorMessage = null) }
            }

            is CreateBugReportEvent.ExpectedBehaviorChanged -> {
                _uiState.update { it.copy(expectedBehavior = event.expected, errorMessage = null) }
            }

            is CreateBugReportEvent.SeveritySelected -> {
                _uiState.update { it.copy(severity = event.severity) }
            }

            is CreateBugReportEvent.UploadAttachment -> {
                _uiState.update { it.copy(showFilePickerDialog = true) }
            }

            is CreateBugReportEvent.FileSelected -> {
                _uiState.update {
                    it.copy(
                        attachedFileName = event.fileName,
                        attachedFileSize = event.fileSize,
                        showFilePickerDialog = false,
                    )
                }
            }

            is CreateBugReportEvent.DismissFilePicker -> {
                _uiState.update { it.copy(showFilePickerDialog = false) }
            }

            is CreateBugReportEvent.RemoveAttachment -> {
                _uiState.update { it.copy(attachedFileName = null, attachedFileSize = null) }
            }

            is CreateBugReportEvent.SubmitReport -> submitReport()

            is CreateBugReportEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun submitReport() {
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

    private fun validate(state: CreateBugReportUiState): String? {
        if (state.title.isBlank()) return "Please enter a bug title"
        if (state.stepsToReproduce.isBlank()) return "Please describe the steps to reproduce"
        return null
    }
}
