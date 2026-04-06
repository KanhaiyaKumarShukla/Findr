package com.exa.android.reflekt.feature.auth.presentation.registration

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.feature.auth.presentation.login.components.BackgroundDecorations
import com.exa.android.reflekt.feature.auth.presentation.registration.components.BottomNavigation
import com.exa.android.reflekt.feature.auth.presentation.registration.components.StepOne
import com.exa.android.reflekt.feature.auth.presentation.registration.components.StepProgressHeader
import com.exa.android.reflekt.feature.auth.presentation.registration.components.StepThree
import com.exa.android.reflekt.feature.auth.presentation.registration.components.StepTwo
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onNavigateToLogin: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val colors = DonutTheme.colorTokens

    // Navigate away on success
    LaunchedEffect(uiState.isRegistrationSuccess) {
        if (uiState.isRegistrationSuccess) onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { focusManager.clearFocus() },
    ) {
        // Background decorative elements
        BackgroundDecorations()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
        ) {
            // Progress Header
            StepProgressHeader(
                currentStep = uiState.currentStep,
                totalSteps = 3,
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Animated step content
                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                        }
                    },
                    label = "step_content",
                ) { step ->
                    when (step) {
                        1 -> StepOne(
                            uiState = uiState,
                            onEvent = viewModel::onEvent,
                            focusManager = focusManager,
                        )

                        2 -> StepTwo(
                            uiState = uiState,
                            onEvent = viewModel::onEvent,
                        )

                        else -> StepThree(
                            uiState = uiState,
                            onEvent = viewModel::onEvent,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }

            // Bottom Navigation
            BottomNavigation(
                currentStep = uiState.currentStep,
                isLoading = uiState.isLoading,
                onBack = {
                    if (uiState.currentStep == 1) {
                        onNavigateToLogin()
                    } else {
                        viewModel.onEvent(RegistrationEvent.PreviousStep)
                    }
                },
                onNext = { viewModel.onEvent(RegistrationEvent.NextStep) },
            )
        }

        // Error Snackbar
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .padding(bottom = 80.dp),
        ) {
            uiState.errorMessage?.let { message ->
                Snackbar(
                    containerColor = colors.error,
                    contentColor = colors.onError,
                    shape = RoundedCornerShape(12.dp),
                    action = {
                        TextButton(onClick = { viewModel.onEvent(RegistrationEvent.DismissError) }) {
                            Text("Dismiss", color = colors.onError)
                        }
                    },
                ) {
                    Text(message)
                }
            }
        }
    }
}
