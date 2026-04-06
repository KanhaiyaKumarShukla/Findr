package com.exa.android.reflekt.feature.auth.presentation.registration.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius

/** Step x of y label + animated progress bar. */
@Composable
internal fun StepProgressHeader(
    currentStep: Int,
    totalSteps: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DonutTheme.dimens.spacing24, vertical = DonutTheme.dimens.spacing16),
    ) {
        Text(
            text = "Step $currentStep of $totalSteps",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing8))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DonutTheme.dimens.spacing6)
                .clip(RoundedCornerShape(DonutRadius.sm))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = currentStep.toFloat() / totalSteps)
                    .height(DonutTheme.dimens.spacing6)
                    .clip(RoundedCornerShape(DonutRadius.sm))
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}
