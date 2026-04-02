package com.exa.android.reflekt.feature.auth.presentation.registration

data class RegistrationUiState(
    val currentStep: Int = 1,
    // Step 1 — Personal Info
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val college: String = "",
    val branch: String = "",
    val graduationYear: String = "",
    val selectedInterests: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccess: Boolean = false,
)
