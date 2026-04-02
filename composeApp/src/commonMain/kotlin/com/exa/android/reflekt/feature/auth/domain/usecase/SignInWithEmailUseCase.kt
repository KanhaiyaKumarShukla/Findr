package com.exa.android.reflekt.feature.auth.domain.usecase

import com.exa.android.reflekt.feature.auth.domain.model.AuthResult
import com.exa.android.reflekt.feature.auth.domain.repository.AuthRepository

class SignInWithEmailUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        if (email.isBlank()) {
            return AuthResult.Error("Email cannot be empty")
        }
        if (!email.contains("@")) {
            return AuthResult.Error("Please enter a valid email address")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Password cannot be empty")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters")
        }
        return repository.signInWithEmail(email, password)
    }
}
