package com.exa.android.reflekt.feature.auth.presentation.login

/**
 * One-shot navigation / side-effect events emitted by [LoginViewModel].
 * Collected in the Screen via [LaunchedEffect].
 */
sealed class LoginNavEvent {
    data object NavigateToHome         : LoginNavEvent()
    data object NavigateToRegistration : LoginNavEvent()
    data object NavigateToForgotPassword : LoginNavEvent()
}
