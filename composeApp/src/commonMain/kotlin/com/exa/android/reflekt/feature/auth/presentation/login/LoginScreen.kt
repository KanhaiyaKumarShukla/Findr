package com.exa.android.reflekt.feature.auth.presentation.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import com.exa.android.reflekt.feature.auth.presentation.login.components.AuthCard
import com.exa.android.reflekt.feature.auth.presentation.login.components.BackgroundDecorations
import com.exa.android.reflekt.feature.auth.presentation.login.components.FooterSection
import com.exa.android.reflekt.feature.auth.presentation.login.components.LogoSection
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val colors = DonutTheme.colorTokens

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            when (event) {
                LoginNavEvent.NavigateToHome            -> onLoginSuccess()
                LoginNavEvent.NavigateToRegistration    -> onNavigateToSignUp()
                LoginNavEvent.NavigateToForgotPassword  -> onNavigateToForgotPassword()
            }
        }
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

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo & Branding
            LogoSection()

            Spacer(modifier = Modifier.weight(1f))

            // Auth Card
            AuthCard(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                focusManager = focusManager,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            FooterSection(
                onCreateAccountClick = {
                    viewModel.onEvent(LoginEvent.CreateAccountClicked)
                    onNavigateToSignUp()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error Snackbar
        AnimatedVisibility(
            visible = uiState.errorMessage != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ) {
            uiState.errorMessage?.let { message ->
                Snackbar(
                    containerColor = colors.error,
                    contentColor = colors.onError,
                    shape = RoundedCornerShape(12.dp),
                    action = {
                        TextButton(onClick = { viewModel.onEvent(LoginEvent.DismissError) }) {
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
