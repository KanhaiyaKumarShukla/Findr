package com.exa.android.reflekt.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exa.android.reflekt.feature.auth.domain.model.AuthResult
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithGoogleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, errorMessage = null) }
            }

            is LoginEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }

            is LoginEvent.TogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            is LoginEvent.SignInClicked -> signInWithEmail()
            is LoginEvent.GoogleSignInClicked -> signInWithGoogle()
            is LoginEvent.ForgotPasswordClicked -> { /* Navigate to forgot password */ }
            is LoginEvent.CreateAccountClicked -> { /* Navigate to sign up */ }
            is LoginEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun signInWithEmail() {
        _uiState.update { it.copy(isLoginSuccess = true) }
    }

    private fun signInWithGoogle() {
        _uiState.update { it.copy(isLoginSuccess = true) }
    }
}
