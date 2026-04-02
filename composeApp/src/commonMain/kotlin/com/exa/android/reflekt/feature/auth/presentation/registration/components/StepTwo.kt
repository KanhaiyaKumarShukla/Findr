package com.exa.android.reflekt.feature.auth.presentation.registration.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.School
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
import com.exa.android.reflekt.ui.theme.appColors

private val colleges = listOf(
    "Stanford University",
    "Massachusetts Institute of Technology",
    "UC Berkeley",
    "Georgia Tech",
    "Harvard University",
    "Carnegie Mellon University",
)

private val branches = listOf(
    "Computer Science",
    "Mechanical Engineering",
    "Electrical Engineering",
    "Fine Arts",
    "Business Administration",
    "Data Science",
)

private val graduationYears = listOf("2024", "2025", "2026", "2027", "2028")

@Composable
internal fun StepTwo(
    uiState: RegistrationUiState,
    onEvent: (RegistrationEvent) -> Unit,
) {
    val appColors = MaterialTheme.appColors

    Column {
        // Header
        Text(
            text = "Academic Info",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tell us where you study so we can connect you with your peers.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Form Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(appColors.formCardBackground.copy(alpha = 0.7f))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // College Name
            DropdownSelector(
                label = "College Name",
                placeholder = "Select your college",
                icon = Icons.Default.School,
                options = colleges,
                selectedOption = uiState.college,
                onOptionSelected = { onEvent(RegistrationEvent.CollegeChanged(it)) },
                enabled = !uiState.isLoading,
            )

            // Branch / Major
            DropdownSelector(
                label = "Branch / Major",
                placeholder = "Select your major",
                icon = Icons.Default.Category,
                options = branches,
                selectedOption = uiState.branch,
                onOptionSelected = { onEvent(RegistrationEvent.BranchChanged(it)) },
                enabled = !uiState.isLoading,
            )

            // Graduation Year
            DropdownSelector(
                label = "Graduation Year",
                placeholder = "Year",
                icon = Icons.Default.CalendarMonth,
                options = graduationYears,
                selectedOption = uiState.graduationYear,
                onOptionSelected = { onEvent(RegistrationEvent.GraduationYearChanged(it)) },
                enabled = !uiState.isLoading,
            )
        }
    }
}
