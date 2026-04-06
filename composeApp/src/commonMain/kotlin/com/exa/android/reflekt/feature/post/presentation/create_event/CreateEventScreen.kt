package com.exa.android.reflekt.feature.post.presentation.create_event

import com.exa.android.reflekt.ui.theme.DonutTheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CreateEventScreen(
    viewModel: CreateEventViewModel,
    onCancel: () -> Unit,
    onPostSuccess: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.isPostSuccess) {
        if (state.isPostSuccess) onPostSuccess()
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(CreateEventEvent.DismissError)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Header ─────────────────────────────────────────────────────────
            HeaderBar(onCancel = onCancel)

            // ── Form Content ───────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 24.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
            ) {
                // Event Type Toggle
                EventTypeToggle(
                    selected = state.eventType,
                    onTypeSelected = { viewModel.onEvent(CreateEventEvent.EventTypeChanged(it)) },
                )

                // Basic Info
                BasicInfoSection(
                    topic = state.topic,
                    description = state.description,
                    onTopicChange = { viewModel.onEvent(CreateEventEvent.TopicChanged(it)) },
                    onDescriptionChange = { viewModel.onEvent(CreateEventEvent.DescriptionChanged(it)) },
                )

                // Cover Image
                CoverImageSection(
                    imagePath = state.coverImagePath,
                    onUpload = { viewModel.onEvent(CreateEventEvent.UploadCoverImage) },
                )

                // Date, Time & Location
                DateTimeLocationSection(
                    eventType = state.eventType,
                    date = state.date,
                    time = state.time,
                    location = state.location,
                    meetingLink = state.meetingLink,
                    onDateChange = { viewModel.onEvent(CreateEventEvent.DateChanged(it)) },
                    onTimeChange = { viewModel.onEvent(CreateEventEvent.TimeChanged(it)) },
                    onLocationChange = { viewModel.onEvent(CreateEventEvent.LocationChanged(it)) },
                    onMeetingLinkChange = { viewModel.onEvent(CreateEventEvent.MeetingLinkChanged(it)) },
                )

                // Max Participants
                ParticipantStepper(
                    count = state.maxParticipants,
                    onIncrement = { viewModel.onEvent(CreateEventEvent.IncrementParticipants) },
                    onDecrement = { viewModel.onEvent(CreateEventEvent.DecrementParticipants) },
                )
            }
        }

        // ── Sticky Footer ──────────────────────────────────────────────────
        PostEventButton(
            isPosting = state.isPosting,
            isEnabled = state.isFormValid && !state.isPosting,
            onClick = { viewModel.onEvent(CreateEventEvent.PostEvent) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars),
        )

        // ── Snackbar ───────────────────────────────────────────────────────
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Private composables
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeaderBar(onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Cancel",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    onClick = onCancel,
                )
                .padding(DonutTheme.dimens.spacing4),
        )

        Text(
            text = "Create Event",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Invisible spacer for symmetry
        Text(
            text = "Cancel",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Transparent,
            modifier = Modifier.padding(DonutTheme.dimens.spacing4),
        )
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        thickness = 0.5.dp,
    )
}

@Composable
private fun EventTypeToggle(
    selected: EventType,
    onTypeSelected: (EventType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DonutTheme.dimens.spacing12))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(DonutTheme.dimens.spacing4),
    ) {
        EventType.entries.forEach { type ->
            val isSelected = type == selected
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.surface
                else Color.Transparent,
                animationSpec = tween(200),
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(200),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(bgColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = { onTypeSelected(type) },
                    )
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (type == EventType.PHYSICAL) "Physical" else "Virtual",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                )
            }
        }
    }
}

@Composable
private fun BasicInfoSection(
    topic: String,
    description: String,
    onTopicChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing16)) {
        StyledTextField(
            value = topic,
            onValueChange = onTopicChange,
            label = "Event Topic",
            placeholder = "e.g. Finals Study Group",
            trailingIcon = Icons.Default.Edit,
            singleLine = true,
            imeAction = ImeAction.Next,
        )

        Column {
            FieldLabel("Description")
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = {
                    Text(
                        "What are we doing?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
                colors = textFieldColors(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default,
                ),
            )
        }
    }
}

@Composable
private fun CoverImageSection(
    imagePath: String?,
    onUpload: () -> Unit,
) {
    Column {
        FieldLabel("Cover Image")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing16))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(DonutTheme.dimens.spacing16),
                )
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onUpload,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload Photo",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = "UPLOAD PHOTO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DateTimeLocationSection(
    eventType: EventType,
    date: String,
    time: String,
    location: String,
    meetingLink: String,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onMeetingLinkChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing16)) {
        // Date & Time row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                StyledTextField(
                    value = date,
                    onValueChange = onDateChange,
                    label = "Date",
                    placeholder = "DD/MM/YYYY",
                    trailingIcon = Icons.Default.CalendarToday,
                    singleLine = true,
                    imeAction = ImeAction.Next,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                StyledTextField(
                    value = time,
                    onValueChange = onTimeChange,
                    label = "Time",
                    placeholder = "HH:MM",
                    trailingIcon = Icons.Default.Schedule,
                    singleLine = true,
                    imeAction = ImeAction.Next,
                )
            }
        }

        // Location or Meeting Link
        if (eventType == EventType.PHYSICAL) {
            StyledTextField(
                value = location,
                onValueChange = onLocationChange,
                label = "Location",
                placeholder = "Add location or address",
                leadingIcon = Icons.Default.LocationOn,
                singleLine = true,
                imeAction = ImeAction.Done,
            )
            Text(
                text = "Switch to \"Virtual\" above to add a meeting link instead.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = DonutTheme.dimens.spacing4),
            )
        } else {
            StyledTextField(
                value = meetingLink,
                onValueChange = onMeetingLinkChange,
                label = "Meeting Link",
                placeholder = "Paste your meeting URL",
                leadingIcon = Icons.Default.Link,
                singleLine = true,
                imeAction = ImeAction.Done,
            )
            Text(
                text = "Switch to \"Physical\" above to add a location instead.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = DonutTheme.dimens.spacing4),
            )
        }
    }
}

@Composable
private fun ParticipantStepper(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DonutTheme.dimens.spacing16))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(DonutTheme.dimens.spacing16),
            )
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Max Participants",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "How many people can join?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing12))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(DonutTheme.dimens.spacing4),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing4),
        ) {
            StepperButton(
                icon = Icons.Default.Remove,
                onClick = onDecrement,
                enabled = count > 2,
            )

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.width(DonutTheme.dimens.spacing32),
                textAlign = TextAlign.Center,
            )

            StepperButton(
                icon = Icons.Default.Add,
                onClick = onIncrement,
                enabled = count < 100,
            )
        }
    }
}

@Composable
private fun StepperButton(
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean,
) {
    Box(
        modifier = Modifier
            .size(DonutTheme.dimens.spacing40)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
        )
    }
}

@Composable
private fun PostEventButton(
    isPosting: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.background,
                    ),
                )
            )
            .padding(horizontal = 20.dp)
            .padding(top = DonutTheme.dimens.spacing40, bottom = DonutTheme.dimens.spacing16),
    ) {
        Button(
            onClick = onClick,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                disabledContentColor = Color.White.copy(alpha = 0.6f),
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp,
            ),
            contentPadding = PaddingValues(horizontal = 24.dp),
        ) {
            if (isPosting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = Color.White,
                )
            } else {
                Text(
                    text = "Post Event",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing8))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Shared helpers
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = DonutTheme.dimens.spacing4, bottom = 6.dp),
    )
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
) {
    Column {
        FieldLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
            colors = textFieldColors(),
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            trailingIcon = trailingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = imeAction,
            ),
        )
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
    cursorColor = MaterialTheme.colorScheme.primary,
)
