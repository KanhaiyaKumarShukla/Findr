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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.appColors

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel,
    onNavigateToLogin: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val appColors = MaterialTheme.appColors

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                        1 -> Step1PersonalInfo(
                            uiState = uiState,
                            onEvent = viewModel::onEvent,
                            focusManager = focusManager,
                        )

                        2 -> Step2AcademicInfo(
                            uiState = uiState,
                            onEvent = viewModel::onEvent,
                        )

                        else -> Step3Interests(
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
                    containerColor = appColors.error,
                    contentColor = appColors.onError,
                    shape = RoundedCornerShape(12.dp),
                    action = {
                        TextButton(onClick = { viewModel.onEvent(RegistrationEvent.DismissError) }) {
                            Text("Dismiss", color = appColors.onError)
                        }
                    },
                ) {
                    Text(message)
                }
            }
        }
    }
}

// ─── Progress Header ─────────────────────────────────────────────────────────

@Composable
private fun StepProgressHeader(
    currentStep: Int,
    totalSteps: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Text(
            text = "Step $currentStep of $totalSteps",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = currentStep.toFloat() / totalSteps)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

// ─── Step 1: Personal Info ───────────────────────────────────────────────────

@Composable
private fun Step1PersonalInfo(
    uiState: RegistrationUiState,
    onEvent: (RegistrationEvent) -> Unit,
    focusManager: androidx.compose.ui.focus.FocusManager,
) {
    val appColors = MaterialTheme.appColors

    Column {
        // Header
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Join your campus community and start connecting with peers.",
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
            // Full Name
            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = { onEvent(RegistrationEvent.FullNameChanged(it)) },
                placeholder = { Text("Full Name") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                shape = RoundedCornerShape(16.dp),
                colors = registrationTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
            )

            // Email
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { onEvent(RegistrationEvent.EmailChanged(it)) },
                placeholder = { Text("College Email (.edu)") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
                shape = RoundedCornerShape(16.dp),
                colors = registrationTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
            )

            // Password
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { onEvent(RegistrationEvent.PasswordChanged(it)) },
                placeholder = { Text("Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onEvent(RegistrationEvent.TogglePasswordVisibility) }) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (uiState.isPasswordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                singleLine = true,
                visualTransformation = if (uiState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
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
                shape = RoundedCornerShape(16.dp),
                colors = registrationTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Already have an account
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { /* handled by onNavigateToLogin via Back on step 1 */ },
            )
        }
    }
}

// ─── Step 2: Academic Info ───────────────────────────────────────────────────

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
private fun Step2AcademicInfo(
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

// ─── Step 3: Interests ──────────────────────────────────────────────────────

private val availableInterests = listOf(
    "Coding", "Design", "AI / ML", "Web Dev",
    "Mobile Dev", "Data Science", "Cybersecurity", "Cloud",
    "Open Source", "Hackathons", "Startups", "Research",
    "Music", "Photography", "Gaming", "Sports",
)

@Composable
private fun Step3Interests(
    uiState: RegistrationUiState,
    onEvent: (RegistrationEvent) -> Unit,
) {
    val appColors = MaterialTheme.appColors

    Column {
        // Header
        Text(
            text = "Your Interests",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Pick topics you're passionate about so we can personalize your experience.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Interest chips in a flow layout
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(appColors.formCardBackground.copy(alpha = 0.7f))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Show chips in rows of 2
            availableInterests.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    rowItems.forEach { interest ->
                        val isSelected = interest in uiState.selectedInterests
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(16.dp),
                                )
                                .clickable { onEvent(RegistrationEvent.ToggleInterest(interest)) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = interest,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    // Fill remaining space if odd number of items in row
                    if (rowItems.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ─── Dropdown Selector ───────────────────────────────────────────────────────

@Composable
private fun DropdownSelector(
    label: String,
    placeholder: String,
    icon: ImageVector,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder) },
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = registrationTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { expanded = true },
                enabled = enabled,
            )

            // Invisible clickable overlay (since readOnly text fields don't trigger click)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(enabled = enabled) { expanded = true },
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth(0.75f),
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                color = if (option == selectedOption) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                fontWeight = if (option == selectedOption) FontWeight.SemiBold
                                else FontWeight.Normal,
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

// ─── Bottom Navigation ───────────────────────────────────────────────────────

@Composable
private fun BottomNavigation(
    currentStep: Int,
    totalSteps: Int = 3,
    isLoading: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Back Button
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline,
            ),
            enabled = !isLoading,
        ) {
            Text(
                text = "Back",
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
            )
        }

        // Next Button
        Button(
            onClick = onNext,
            modifier = Modifier
                .weight(2f)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            ),
            enabled = !isLoading,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp,
            ),
        ) {
            val isLastStep = currentStep == totalSteps
            Text(
                text = if (isLastStep) "Complete" else "Next Step",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            if (!isLastStep) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ─── Background Decorations ──────────────────────────────────────────────────

@Composable
private fun BackgroundDecorations() {
    val appColors = MaterialTheme.appColors

    // Top-right glow
    Box(
        modifier = Modifier
            .offset(x = 120.dp, y = (-40).dp)
            .size(250.dp)
            .blur(100.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
    )

    // Left-center glow (purple tint)
    Box(
        modifier = Modifier
            .offset(x = (-60).dp, y = 350.dp)
            .size(200.dp)
            .blur(120.dp)
            .clip(CircleShape)
            .background(appColors.purpleGlow.copy(alpha = 0.08f)),
    )
}

// ─── Shared TextField Colors ─────────────────────────────────────────────────

@Composable
private fun registrationTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
    focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    cursorColor = MaterialTheme.colorScheme.primary,
)
