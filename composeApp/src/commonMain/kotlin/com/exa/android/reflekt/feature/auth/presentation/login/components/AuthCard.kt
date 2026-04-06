package com.exa.android.reflekt.feature.auth.presentation.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.exa.android.reflekt.feature.auth.presentation.login.LoginEvent
import com.exa.android.reflekt.feature.auth.presentation.login.LoginUiState
import com.exa.android.reflekt.ui.components.button.FindrPrimaryButton
import com.exa.android.reflekt.ui.components.button.FindrSocialButton
import com.exa.android.reflekt.ui.components.button.FindrTextButton
import com.exa.android.reflekt.ui.components.textfield.FindrTextField
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius

@Composable
internal fun AuthCard(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    focusManager: FocusManager,
) {
    val colors = DonutTheme.colorTokens

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DonutRadius.dialog))
            .background(colors.formCardBackground.copy(alpha = 0.7f))
            .padding(DonutTheme.dimens.spacing24),
        verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing20),
    ) {
        FindrTextField(
            value = uiState.email,
            onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
            placeholder = "Student Email (.edu)",
            leadingIcon = Icons.Default.Email,
            errorMessage = uiState.emailError,
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
            ),
        )

        FindrTextField(
            value = uiState.password,
            onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
            placeholder = "Password",
            leadingIcon = Icons.Default.Lock,
            trailingIcon = {
                IconButton(onClick = { onEvent(LoginEvent.TogglePasswordVisibility) }) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility
                                      else Icons.Default.VisibilityOff,
                        contentDescription = if (uiState.isPasswordVisible) "Hide password"
                                             else "Show password",
                        tint = colors.onSurfaceVariant,
                    )
                }
            },
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            errorMessage = uiState.passwordError,
            enabled = !uiState.isLoading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onEvent(LoginEvent.SignInClicked)
                },
            ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            FindrTextButton(
                text = "Forgot password?",
                onClick = { onEvent(LoginEvent.ForgotPasswordClicked) },
            )
        }

        FindrPrimaryButton(
            text = "Sign In",
            onClick = { onEvent(LoginEvent.SignInClicked) },
            isLoading = uiState.isLoading,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = DonutTheme.dimens.elevationCard, pressedElevation = DonutTheme.dimens.elevationPressed),
            trailingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(DonutTheme.dimens.iconSizeBase),
                )
            },
        )

        OrDivider()

        FindrSocialButton(
            text = "Continue with Google",
            icon = {
                Text(
                    text = "G",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.googleBlue,
                )
            },
            onClick = { onEvent(LoginEvent.GoogleSignInClicked) },
            isLoading = uiState.isGoogleLoading,
        )
    }
}
