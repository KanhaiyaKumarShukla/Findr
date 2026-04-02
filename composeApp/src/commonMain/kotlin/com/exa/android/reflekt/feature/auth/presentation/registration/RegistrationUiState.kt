package com.exa.android.reflekt.feature.auth.presentation.registration

data class RegistrationUiState(
    val currentStep: Int = 1,
    // Step 1 — Personal Info
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    // Step 2 — Academic Info
    val college: String = "",
    val branch: String = "",
    val graduationYear: String = "",
    // Step 3 — Interests
    val selectedInterests: Set<String> = emptySet(),
    // General
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccess: Boolean = false,
)
