package com.exa.android.reflekt.ui.components.button

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize

// ─────────────────────────────────────────────────────────────
//  Primary button  (filled, brand color)
// ─────────────────────────────────────────────────────────────

/**
 * Full-width primary action button with built-in loading state.
 *
 * @param text        Label shown when not loading.
 * @param onClick     Click callback.
 * @param isLoading   When true, shows a [CircularProgressIndicator] and disables the button.
 * @param enabled     External enable/disable control (independent of [isLoading]).
 * @param trailingIcon  Optional trailing icon composable placed after the label.
 */
@Composable
fun FindrPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    height: Dp = DonutTheme.dimens.spacing52,
    elevation: androidx.compose.material3.ButtonElevation = ButtonDefaults.buttonElevation(defaultElevation = DonutTheme.dimens.elevationMedium, pressedElevation = DonutTheme.dimens.elevationPressed),
    colors: androidx.compose.material3.ButtonColors? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val buttonColors = colors ?: ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(DonutRadius.panel),
        enabled = enabled && !isLoading,
        colors = buttonColors,
        elevation = elevation,
        contentPadding = PaddingValues(horizontal = DonutTheme.dimens.spacing24),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(DonutTheme.dimens.iconSizeExtraLarge),
                color = buttonColors.contentColor,
                strokeWidth = DonutTheme.dimens.elevationPressed,
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = DonutTextSize.bodyLarge,
                        fontWeight = FontWeight.Bold
                    ),
                )
                if (trailingIcon != null) {
                    Spacer(Modifier.width(DonutTheme.dimens.spacing8))
                    trailingIcon()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Secondary / Outlined button
// ─────────────────────────────────────────────────────────────

/**
 * Full-width outlined button. Use for secondary actions.
 */
@Composable
fun FindrOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = DonutTheme.dimens.spacing52,
    colors: androidx.compose.material3.ButtonColors? = null,
    border: androidx.compose.foundation.BorderStroke? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val defaultColors = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
    )

    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(DonutRadius.panel),
        enabled = enabled,
        colors = colors ?: defaultColors,
        border = border ?: androidx.compose.foundation.BorderStroke(
            width = DonutTheme.dimens.borderThin,
            color = if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        ),
        contentPadding = PaddingValues(horizontal = DonutTheme.dimens.spacing24),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(DonutTheme.dimens.spacing8))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = DonutTextSize.body,
                    fontWeight = FontWeight.Medium
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Text / Ghost button
// ─────────────────────────────────────────────────────────────

/**
 * Minimal text button for tertiary actions (e.g. "Forgot password?", "Skip").
 */
@Composable
fun FindrTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color? = null,
    enabled: Boolean = true,
) {
    val textColor = color ?: MaterialTheme.colorScheme.primary
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = textColor,
            disabledContentColor = textColor.copy(alpha = 0.4f),
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Social sign-in button  (white pill, custom icon slot)
// ─────────────────────────────────────────────────────────────

/**
 * White pill button used for OAuth providers (Google, Apple, etc.).
 */
@Composable
fun FindrSocialButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    height: Dp = DonutTheme.dimens.spacing52,
    elevation: androidx.compose.material3.ButtonElevation = ButtonDefaults.buttonElevation(defaultElevation = DonutTheme.dimens.elevationPressed, pressedElevation = DonutTheme.dimens.borderThin),
    colors: androidx.compose.material3.ButtonColors? = null,
) {
    val defaultColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(DonutRadius.panel),
        enabled = !isLoading,
        colors = colors ?: defaultColors,
        elevation = elevation,
        contentPadding = PaddingValues(horizontal = DonutTheme.dimens.spacing24),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(DonutTheme.dimens.iconSizeExtraLarge),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = DonutTheme.dimens.elevationPressed,
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(DonutTheme.dimens.iconSizeLarge), contentAlignment = Alignment.Center) {
                    icon()
                }
                Spacer(Modifier.width(DonutTheme.dimens.spacing12))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = DonutTextSize.body,
                        fontWeight = FontWeight.SemiBold
                    ),
                )
            }
        }
    }
}
