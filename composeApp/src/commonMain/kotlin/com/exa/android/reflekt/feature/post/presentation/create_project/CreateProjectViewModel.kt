package com.exa.android.reflekt.feature.post.presentation.create_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateProjectViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateProjectUiState())
    val uiState: StateFlow<CreateProjectUiState> = _uiState.asStateFlow()

    fun onEvent(event: CreateProjectEvent) {
        when (event) {
            is CreateProjectEvent.ProjectTitleChanged -> {
                _uiState.update { it.copy(projectTitle = event.title, errorMessage = null) }
            }

            is CreateProjectEvent.DescriptionChanged -> {
                _uiState.update { it.copy(description = event.description, errorMessage = null) }
            }

            is CreateProjectEvent.RoleNameChanged -> {
                _uiState.update { it.copy(roleName = event.roleName, errorMessage = null) }
            }

            is CreateProjectEvent.SkillInputChanged -> {
                _uiState.update { it.copy(currentSkillInput = event.input) }
            }

            is CreateProjectEvent.AddSkill -> {
                val current = _uiState.value
                val skill = current.currentSkillInput.trim()
                if (skill.isNotEmpty() && skill !in current.techSkills) {
                    _uiState.update {
                        it.copy(
                            techSkills = it.techSkills + skill,
                            currentSkillInput = "",
                        )
                    }
                }
            }

            is CreateProjectEvent.RemoveSkill -> {
                _uiState.update {
                    it.copy(techSkills = it.techSkills - event.skill)
                }
            }

            is CreateProjectEvent.IncrementOpenings -> {
                _uiState.update {
                    it.copy(numberOfOpenings = (it.numberOfOpenings + 1).coerceAtMost(10))
                }
            }

            is CreateProjectEvent.DecrementOpenings -> {
                _uiState.update {
                    it.copy(numberOfOpenings = (it.numberOfOpenings - 1).coerceAtLeast(1))
                }
            }

            is CreateProjectEvent.UploadAttachment -> {
                // TODO: Integrate with platform file picker
                _uiState.update {
                    it.copy(
                        attachedFileName = "Project_Wireframes_v1.pdf",
                        attachedFileSize = "2.4 MB",
                    )
                }
            }

            is CreateProjectEvent.RemoveAttachment -> {
                _uiState.update {
                    it.copy(attachedFileName = null, attachedFileSize = null)
                }
            }

            is CreateProjectEvent.PreviewClicked -> {
                // TODO: Navigate to preview
            }

            is CreateProjectEvent.PostProject -> postProject()

            is CreateProjectEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun postProject() {
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

    private fun validate(state: CreateProjectUiState): String? {
        if (state.projectTitle.isBlank()) return "Please enter a project title"
        if (state.description.isBlank()) return "Please add a project description"
        if (state.roleName.isBlank()) return "Please specify a role name"
        return null
    }
}
