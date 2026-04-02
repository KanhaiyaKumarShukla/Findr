package com.exa.android.reflekt.feature.auth.presentation.registration

sealed class RegistrationEvent {
    // Step 1
    data class FullNameChanged(val name: String) : RegistrationEvent()
    data class EmailChanged(val email: String) : RegistrationEvent()
    data class PasswordChanged(val password: String) : RegistrationEvent()
    data object TogglePasswordVisibility : RegistrationEvent()
    // Step 2
    data class CollegeChanged(val college: String) : RegistrationEvent()
    data class BranchChanged(val branch: String) : RegistrationEvent()
    data class GraduationYearChanged(val year: String) : RegistrationEvent()
    // Step 3
    data class ToggleInterest(val interest: String) : RegistrationEvent()
    // Navigation
    data object NextStep : RegistrationEvent()
    data object PreviousStep : RegistrationEvent()
    data object DismissError : RegistrationEvent()
}
