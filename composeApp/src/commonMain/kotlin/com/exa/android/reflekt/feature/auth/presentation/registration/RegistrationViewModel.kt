package com.exa.android.reflekt.feature.auth.presentation.registration

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegistrationEvent) {
        when (event) {
            // Step 1
            is RegistrationEvent.FullNameChanged -> {
                _uiState.update { it.copy(fullName = event.name, errorMessage = null) }
            }

            is RegistrationEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, errorMessage = null) }
            }

            is RegistrationEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }

            is RegistrationEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            // Step 2
            is RegistrationEvent.CollegeChanged -> {
                _uiState.update { it.copy(college = event.college, errorMessage = null) }
            }

            is RegistrationEvent.BranchChanged -> {
                _uiState.update { it.copy(branch = event.branch, errorMessage = null) }
            }

            is RegistrationEvent.GraduationYearChanged -> {
                _uiState.update { it.copy(graduationYear = event.year, errorMessage = null) }
            }

            // Step 3
            is RegistrationEvent.ToggleInterest -> {
                _uiState.update {
                    val updated = if (event.interest in it.selectedInterests) {
                        it.selectedInterests - event.interest
                    } else {
                        it.selectedInterests + event.interest
                    }
                    it.copy(selectedInterests = updated, errorMessage = null)
                }
            }

            // Navigation
            is RegistrationEvent.NextStep -> handleNextStep()
            is RegistrationEvent.PreviousStep -> handlePreviousStep()
            is RegistrationEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun handleNextStep() {
        val state = _uiState.value
        when (state.currentStep) {
            1 -> {
                val error = validateStep1(state)
                if (error != null) {
                    _uiState.update { it.copy(errorMessage = error) }
                } else {
                    _uiState.update { it.copy(currentStep = 2, errorMessage = null) }
                }
            }

            2 -> {
                val error = validateStep2(state)
                if (error != null) {
                    _uiState.update { it.copy(errorMessage = error) }
                } else {
                    _uiState.update { it.copy(currentStep = 3, errorMessage = null) }
                }
            }

            3 -> {
                val error = validateStep3(state)
                if (error != null) {
                    _uiState.update { it.copy(errorMessage = error) }
                } else {
                    _uiState.update { it.copy(isRegistrationSuccess = true, errorMessage = null) }
                }
            }
        }
    }

    private fun handlePreviousStep() {
        val state = _uiState.value
        if (state.currentStep > 1) {
            _uiState.update { it.copy(currentStep = state.currentStep - 1, errorMessage = null) }
        }
    }

    private fun validateStep1(state: RegistrationUiState): String? {
        if (state.fullName.isBlank()) return "Please enter your full name"
        if (state.email.isBlank()) return "Please enter your email"
        if (!state.email.contains("@")) return "Please enter a valid email address"
        if (state.password.length < 6) return "Password must be at least 6 characters"
        return null
    }

    private fun validateStep2(state: RegistrationUiState): String? {
        if (state.college.isBlank()) return "Please select your college"
        if (state.branch.isBlank()) return "Please select your branch"
        if (state.graduationYear.isBlank()) return "Please select your graduation year"
        return null
    }

    private fun validateStep3(state: RegistrationUiState): String? {
        if (state.selectedInterests.isEmpty()) return "Please select at least one interest"
        return null
    }
}
