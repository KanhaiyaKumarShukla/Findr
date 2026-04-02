package com.exa.android.reflekt.feature.auth.data.repository

import com.exa.android.reflekt.feature.auth.domain.model.AuthResult
import com.exa.android.reflekt.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class AuthRepositoryImpl : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        // TODO: Replace with actual Firebase/backend authentication
        return try {
            delay(1500) // Simulate network call
            AuthResult.Success(userId = "user_${email.hashCode()}")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign in failed")
        }
    }

    override suspend fun signInWithGoogle(): AuthResult {
        // TODO: Replace with actual Google Sign-In integration
        return try {
            delay(1000) // Simulate network call
            AuthResult.Success(userId = "google_user_123")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google sign in failed")
        }
    }
}
