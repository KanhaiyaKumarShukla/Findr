package com.exa.android.reflekt.feature.auth.presentation.login

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data object TogglePasswordVisibility : LoginEvent()
    data object SignInClicked : LoginEvent()
    data object GoogleSignInClicked : LoginEvent()
    data object ForgotPasswordClicked : LoginEvent()
    data object CreateAccountClicked : LoginEvent()
    data object DismissError : LoginEvent()
}
