package com.exa.android.reflekt.feature.auth.domain.repository

import com.exa.android.reflekt.feature.auth.domain.model.AuthResult

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(): AuthResult
}
