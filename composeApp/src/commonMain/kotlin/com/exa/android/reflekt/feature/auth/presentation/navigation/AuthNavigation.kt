package com.exa.android.reflekt.feature.auth.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.exa.android.reflekt.feature.auth.data.repository.AuthRepositoryImpl
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.exa.android.reflekt.feature.auth.presentation.login.LoginScreen
import com.exa.android.reflekt.feature.auth.presentation.login.LoginViewModel
import com.exa.android.reflekt.feature.auth.presentation.registration.RegistrationScreen
import com.exa.android.reflekt.feature.auth.presentation.registration.RegistrationViewModel
import com.exa.android.reflekt.navigation.AuthGraph
import com.exa.android.reflekt.navigation.ForgotPasswordRoute
import com.exa.android.reflekt.navigation.HomeGraph
import com.exa.android.reflekt.navigation.LoginRoute
import com.exa.android.reflekt.navigation.RegistrationRoute

fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation<AuthGraph>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            val viewModel = remember {
                val repository = AuthRepositoryImpl()
                LoginViewModel(
                    signInWithEmailUseCase = SignInWithEmailUseCase(repository),
                    signInWithGoogleUseCase = SignInWithGoogleUseCase(repository),
                )
            }
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { 
                    navController.navigate(HomeGraph) {
                        popUpTo(AuthGraph) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(RegistrationRoute) },
                onNavigateToForgotPassword = { navController.navigate(ForgotPasswordRoute) },
            )
        }

        composable<RegistrationRoute> {
            val viewModel = remember { RegistrationViewModel() }
            RegistrationScreen(
                viewModel = viewModel,
                onNavigateToLogin = { navController.popBackStack() },
            )
        }

        composable<ForgotPasswordRoute> {
            // For now fall back to login component until implemented
            val viewModel = remember {
                val repository = AuthRepositoryImpl()
                LoginViewModel(SignInWithEmailUseCase(repository), SignInWithGoogleUseCase(repository))
            }
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { 
                    navController.navigate(HomeGraph) {
                        popUpTo(AuthGraph) { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate(RegistrationRoute) },
            )
        }
    }
}
