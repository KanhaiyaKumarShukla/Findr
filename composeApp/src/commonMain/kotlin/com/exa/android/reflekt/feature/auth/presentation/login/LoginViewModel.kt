package com.exa.android.reflekt.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithGoogleUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /** One-shot navigation / side-effect events collected in the Screen. */
    private val _navEvent = MutableSharedFlow<LoginNavEvent>()
    val navEvent: SharedFlow<LoginNavEvent> = _navEvent.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, emailError = null, errorMessage = null) }
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, passwordError = null, errorMessage = null) }
            }
            is LoginEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is LoginEvent.SignInClicked       -> signInWithEmail()
            is LoginEvent.GoogleSignInClicked -> signInWithGoogle()
            is LoginEvent.ForgotPasswordClicked -> {
                viewModelScope.launch { _navEvent.emit(LoginNavEvent.NavigateToForgotPassword) }
            }
            is LoginEvent.CreateAccountClicked -> {
                viewModelScope.launch { _navEvent.emit(LoginNavEvent.NavigateToRegistration) }
            }
            is LoginEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun signInWithEmail() {
        val state = _uiState.value
        var hasError = false
        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Please enter your email") }
            hasError = true
        } else if (!state.email.contains("@")) {
            _uiState.update { it.copy(emailError = "Please enter a valid email address") }
            hasError = true
        }
        if (state.password.length < 6) {
            _uiState.update { it.copy(passwordError = "Password must be at least 6 characters") }
            hasError = true
        }
        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Replace with real Firebase auth call via use case
            // val result = signInWithEmailUseCase(state.email, state.password)
            // For now succeed immediately (stub)
            _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
            _navEvent.emit(LoginNavEvent.NavigateToHome)
        }
    }

    private fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGoogleLoading = true) }
            // TODO: Replace with real Google Sign-In use case
            _uiState.update { it.copy(isGoogleLoading = false, isLoginSuccess = true) }
            _navEvent.emit(LoginNavEvent.NavigateToHome)
        }
    }
}
