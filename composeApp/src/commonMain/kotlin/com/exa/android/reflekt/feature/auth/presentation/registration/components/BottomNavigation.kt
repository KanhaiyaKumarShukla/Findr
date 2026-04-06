package com.exa.android.reflekt.feature.auth.presentation.registration.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.exa.android.reflekt.ui.components.button.FindrOutlinedButton
import com.exa.android.reflekt.ui.components.button.FindrPrimaryButton
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun BottomNavigation(
    currentStep: Int,
    isLoading: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(DonutTheme.dimens.spacing24),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Only show Back button if not loading
            if (!isLoading) {
                FindrOutlinedButton(
                    text = "Back",
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing16))
            } else if (currentStep > 1) {
                // Keep the spacing when loading to prevent button jump
                Spacer(modifier = Modifier.weight(1f).padding(end = DonutTheme.dimens.spacing16))
            }

            FindrPrimaryButton(
                text = if (currentStep == 3) "Complete" else "Next",
                onClick = onNext,
                isLoading = isLoading,
                modifier = Modifier.weight(if (isLoading && currentStep == 1) 2f else 1f),
            )
        }
    }
}
