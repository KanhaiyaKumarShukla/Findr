package com.exa.android.reflekt.feature.auth.presentation.registration.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.feature.auth.presentation.registration.RegistrationEvent
import com.exa.android.reflekt.feature.auth.presentation.registration.RegistrationUiState
import com.exa.android.reflekt.ui.components.chip.FindrFilterChip
import com.exa.android.reflekt.ui.theme.DonutTheme

private val availableInterests = listOf(
    "Technology", "Design",
    "Business", "Marketing",
    "Sports", "Music",
    "Art", "Gaming",
    "Science", "Literature",
)

@Composable
internal fun StepThree(
    uiState: RegistrationUiState,
    onEvent: (RegistrationEvent) -> Unit,
) {
    val colors = DonutTheme.colorTokens

    Column {
        Text(
            text = "Your Interests",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.onBackground,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pick a few topics you're interested in.",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Card container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.formCardBackground.copy(alpha = 0.7f))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Using precise chunking to maintain exactly 2 columns to match the old UI perfectly
            val chunkedInterests = availableInterests.chunked(2)

            chunkedInterests.forEach { rowInterests ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowInterests.forEach { interest ->
                        val isSelected = uiState.selectedInterests.contains(interest)

                        FindrFilterChip(
                            label = interest,
                            selected = isSelected,
                            onToggle = { onEvent(RegistrationEvent.ToggleInterest(interest)) },
                            modifier = Modifier.weight(1f),
                        )
                    }

                    // Pad empty spots in the row if the chunk is odd
                    if (rowInterests.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
