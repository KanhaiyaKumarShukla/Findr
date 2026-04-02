package com.exa.android.reflekt.feature.auth.domain.usecase

import com.exa.android.reflekt.feature.auth.domain.model.AuthResult
import com.exa.android.reflekt.feature.auth.domain.repository.AuthRepository

class SignInWithGoogleUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): AuthResult {
        return repository.signInWithGoogle()
    }
}
