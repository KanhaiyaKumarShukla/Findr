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
        val state = _uiState.value
        if (state.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = signInWithEmailUseCase(state.email, state.password)) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                }

                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    private fun signInWithGoogle() {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = signInWithGoogleUseCase()) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                }

                is AuthResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }
}
