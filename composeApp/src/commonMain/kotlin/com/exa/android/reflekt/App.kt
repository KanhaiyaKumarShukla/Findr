package com.exa.android.reflekt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.exa.android.reflekt.feature.auth.data.repository.AuthRepositoryImpl
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.exa.android.reflekt.feature.auth.presentation.login.LoginScreen
import com.exa.android.reflekt.feature.auth.presentation.login.LoginViewModel
import com.exa.android.reflekt.ui.theme.FindrTheme

@Composable
@Preview
fun App() {
    FindrTheme(darkTheme = true) {
        // Manual DI — replace with Koin/Hilt when you integrate a DI framework
        val viewModel = remember {
            val repository = AuthRepositoryImpl()
            val signInWithEmail = SignInWithEmailUseCase(repository)
            val signInWithGoogle = SignInWithGoogleUseCase(repository)
            LoginViewModel(signInWithEmail, signInWithGoogle)
        }

        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = { /* TODO: Navigate to home */ },
            onNavigateToSignUp = { /* TODO: Navigate to sign up */ },
            onNavigateToForgotPassword = { /* TODO: Navigate to forgot password */ },
        )
    }
}
