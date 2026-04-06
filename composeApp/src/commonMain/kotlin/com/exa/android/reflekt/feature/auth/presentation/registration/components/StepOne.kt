package com.exa.android.reflekt.feature.auth.presentation.registration.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.feature.auth.presentation.registration.RegistrationEvent
import com.exa.android.reflekt.feature.auth.presentation.registration.RegistrationUiState
import com.exa.android.reflekt.ui.components.textfield.FindrTextField
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun StepOne(
    uiState: RegistrationUiState,
    onEvent: (RegistrationEvent) -> Unit,
    focusManager: FocusManager,
) {
    val colors = DonutTheme.colorTokens

    Column {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.onBackground,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Join your campus community and start connecting with peers.",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.formCardBackground.copy(alpha = 0.7f))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            FindrTextField(
                value = uiState.fullName,
                onValueChange = { onEvent(RegistrationEvent.FullNameChanged(it)) },
                placeholder = "Full Name",
                leadingIcon = Icons.Default.Person,
                errorMessage = uiState.fullNameError,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
            )

            FindrTextField(
                value = uiState.email,
                onValueChange = { onEvent(RegistrationEvent.EmailChanged(it)) },
                placeholder = "College Email (.edu)",
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
                onValueChange = { onEvent(RegistrationEvent.PasswordChanged(it)) },
                placeholder = "Password",
                leadingIcon = Icons.Default.Lock,
                trailingIcon = {
                    IconButton(onClick = { onEvent(RegistrationEvent.TogglePasswordVisibility) }) {
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
                        onEvent(RegistrationEvent.NextStep)
                    },
                ),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { },
            )
        }
    }
}
