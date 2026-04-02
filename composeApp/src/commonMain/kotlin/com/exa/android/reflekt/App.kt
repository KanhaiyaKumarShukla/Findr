package com.exa.android.reflekt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.exa.android.reflekt.feature.auth.data.repository.AuthRepositoryImpl
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.exa.android.reflekt.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.exa.android.reflekt.feature.auth.presentation.login.LoginScreen
import com.exa.android.reflekt.feature.auth.presentation.login.LoginViewModel
import com.exa.android.reflekt.feature.auth.presentation.registration.RegistrationScreen
import com.exa.android.reflekt.feature.auth.presentation.registration.RegistrationViewModel
import com.exa.android.reflekt.feature.home.presentation.HomeScreen
import com.exa.android.reflekt.feature.home.presentation.HomeViewModel
import com.exa.android.reflekt.feature.onboarding.OnboardingScreen
import com.exa.android.reflekt.feature.post.presentation.CreatePostSelectionScreen
import com.exa.android.reflekt.feature.post.presentation.PostCategory
import com.exa.android.reflekt.feature.post.presentation.create_event.CreateEventScreen
import com.exa.android.reflekt.feature.post.presentation.create_event.CreateEventViewModel
import com.exa.android.reflekt.feature.post.presentation.create_project.CreateProjectScreen
import com.exa.android.reflekt.feature.post.presentation.create_project.CreateProjectViewModel
import com.exa.android.reflekt.feature.project.presentation.ProjectDetailScreen
import com.exa.android.reflekt.ui.theme.CampusConnectTheme

@Composable
@Preview
fun App() {
    CampusConnectTheme(darkTheme = true) {
        var showOnboarding by remember { mutableStateOf(true) }
        var currentScreen by remember { mutableStateOf("login") }

        if (showOnboarding) {
            OnboardingScreen(onFinished = { showOnboarding = false })
        } else {
            when (currentScreen) {
                "login" -> {
                    // Manual DI — replace with Koin/Hilt when you integrate a DI framework
                    val viewModel = remember {
                        val repository = AuthRepositoryImpl()
                        val signInWithEmail = SignInWithEmailUseCase(repository)
                        val signInWithGoogle = SignInWithGoogleUseCase(repository)
                        LoginViewModel(signInWithEmail, signInWithGoogle)
                    }

                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { currentScreen = "home" },
                        onNavigateToSignUp = { currentScreen = "registration" },
                        onNavigateToForgotPassword = { /* TODO: Navigate to forgot password */ },
                    )
                }

                "registration" -> {
                    val viewModel = remember { RegistrationViewModel() }

                    RegistrationScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = { currentScreen = "login" },
                    )
                }

                "home" -> {
                    val viewModel = remember { HomeViewModel() }
                    HomeScreen(viewModel = viewModel)
                }

                "create_post" -> {
                    CreatePostSelectionScreen(
                        onCategorySelected = { category ->
                            when (category) {
                                PostCategory.PROJECT -> currentScreen = "create_project"
                                PostCategory.EVENT -> currentScreen = "create_event"
                                else -> { /* TODO: Navigate to other post creation screens */ }
                            }
                        },
                        onDismiss = { currentScreen = "login" },
                    )
                }

                "create_project" -> {
                    val viewModel = remember { CreateProjectViewModel() }

                    CreateProjectScreen(
                        viewModel = viewModel,
                        onCancel = { currentScreen = "create_post" },
                        onPostSuccess = { currentScreen = "login" },
                    )
                }

                "create_event" -> {
                    val viewModel = remember { CreateEventViewModel() }

                    CreateEventScreen(
                        viewModel = viewModel,
                        onCancel = { currentScreen = "create_post" },
                        onPostSuccess = { currentScreen = "home" },
                    )
                }

                "project_detail" -> {
                    ProjectDetailScreen(
                        onBack = { currentScreen = "login" },
                        onEnroll = { /* TODO: Handle enrollment */ },
                    )
                }
            }
        }
    }
}
