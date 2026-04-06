package com.exa.android.reflekt.feature.post.presentation.create_bug_report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
fun CreateBugReportScreen(
    viewModel: CreateBugReportViewModel,
    onCancel: () -> Unit,
    onPostSuccess: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isPostSuccess) {
        onPostSuccess()
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── Header ─────────────────────────────────────────────────
                Header(onCancel = onCancel)

                // ── Scrollable Content ─────────────────────────────────────
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 100.dp, top = DonutTheme.dimens.spacing4),
                ) {
                    HeroSection()

                    Spacer(modifier = Modifier.height(24.dp))

                    BasicInfoSection(
                        title = uiState.title,
                        steps = uiState.stepsToReproduce,
                        expected = uiState.expectedBehavior,
                        onTitleChanged = { viewModel.onEvent(CreateBugReportEvent.TitleChanged(it)) },
                        onStepsChanged = { viewModel.onEvent(CreateBugReportEvent.StepsChanged(it)) },
                        onExpectedChanged = { viewModel.onEvent(CreateBugReportEvent.ExpectedBehaviorChanged(it)) },
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    SeveritySection(
                        selected = uiState.severity,
                        onSelect = { viewModel.onEvent(CreateBugReportEvent.SeveritySelected(it)) },
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    AttachmentsSection(
                        fileName = uiState.attachedFileName,
                        fileSize = uiState.attachedFileSize,
                        onUpload = { viewModel.onEvent(CreateBugReportEvent.UploadAttachment) },
                        onRemove = { viewModel.onEvent(CreateBugReportEvent.RemoveAttachment) },
                    )
                }
            }

            // ── Sticky Bottom Bar ──────────────────────────────────────────
            BottomActionBar(
                isPosting = uiState.isPosting,
                onSubmit = { viewModel.onEvent(CreateBugReportEvent.SubmitReport) },
                modifier = Modifier.align(Alignment.BottomCenter),
            )

            // ── Error Snackbar ─────────────────────────────────────────────
            uiState.errorMessage?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(DonutTheme.dimens.spacing16)
                        .padding(bottom = 80.dp),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    action = {
                        Text(
                            text = "Dismiss",
                            modifier = Modifier.clickable { viewModel.onEvent(CreateBugReportEvent.DismissError) },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                ) {
                    Text(text = error, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // ── File Picker Dialog ─────────────────────────────────────────────
        if (uiState.showFilePickerDialog) {
            FilePickerDialog(
                onFileSelected = { name, size ->
                    viewModel.onEvent(CreateBugReportEvent.FileSelected(name, size))
                },
                onDismiss = { viewModel.onEvent(CreateBugReportEvent.DismissFilePicker) },
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Private composables
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun Header(onCancel: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Cancel",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onCancel,
                )
                .padding(horizontal = DonutTheme.dimens.spacing4, vertical = 2.dp),
        )

        Text(
            text = "Report Bug",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing40))
    }
}

@Composable
private fun HeroSection() {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(DonutTheme.dimens.spacing40)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DonutTheme.colorTokens.error.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.BugReport,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = DonutTheme.colorTokens.error,
                )
            }

            Text(
                text = "Found a bug?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing4))

        Text(
            text = "Help us squash it. Describe the issue in detail.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun BasicInfoSection(
    title: String,
    steps: String,
    expected: String,
    onTitleChanged: (String) -> Unit,
    onStepsChanged: (String) -> Unit,
    onExpectedChanged: (String) -> Unit,
) {
    val colors = fieldColors()

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Bug Title
        Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8)) {
            Text(
                text = "Bug Title",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                placeholder = {
                    Text(
                        text = "e.g. App crashes on login",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
                singleLine = true,
                colors = colors,
            )
        }

        // Steps to Reproduce
        Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8)) {
            Text(
                text = "Steps to Reproduce",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = steps,
                onValueChange = onStepsChanged,
                placeholder = {
                    Text(
                        text = "1. Open the app\n2. Tap on login\n3. Enter credentials\n4. App crashes",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
                colors = colors,
            )
        }

        // Expected Behavior
        Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8)) {
            Row {
                Text(
                    text = "Expected Behavior",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "  (Optional)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
            OutlinedTextField(
                value = expected,
                onValueChange = onExpectedChanged,
                placeholder = {
                    Text(
                        text = "What should have happened instead?",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
                colors = colors,
            )
        }
    }
}

@Composable
private fun SeveritySection(
    selected: BugSeverity,
    onSelect: (BugSeverity) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12)) {
        Text(
            text = "Severity",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
        ) {
            BugSeverity.entries.forEach { severity ->
                val isSelected = severity == selected
                val chipColor = when (severity) {
                    BugSeverity.LOW -> DonutTheme.staticColorTokens.accentGreen
                    BugSeverity.MEDIUM -> DonutTheme.staticColorTokens.accentYellow
                    BugSeverity.HIGH -> DonutTheme.staticColorTokens.accentOrange
                    BugSeverity.CRITICAL -> DonutTheme.colorTokens.error
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) chipColor.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) chipColor.copy(alpha = 0.5f)
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp),
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = { onSelect(severity) },
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = severity.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        ),
                        color = if (isSelected) chipColor
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AttachmentsSection(
    fileName: String?,
    fileSize: String?,
    onUpload: () -> Unit,
    onRemove: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing12)) {
        Row {
            Text(
                text = "Screenshot",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "  (Optional)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(DonutTheme.dimens.spacing16))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(DonutTheme.dimens.spacing16),
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onUpload,
                )
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Upload",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing12))

                Text(
                    text = "Tap to upload screenshot",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing4))

                Text(
                    text = "PNG, JPG up to 10MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            }
        }

        if (fileName != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(DonutTheme.dimens.spacing12))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(DonutTheme.dimens.spacing12),
                    )
                    .padding(DonutTheme.dimens.spacing12),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(DonutTheme.dimens.spacing40)
                        .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                        .background(
                            if (fileName.endsWith(".pdf")) DonutTheme.colorTokens.error.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (fileName.endsWith(".pdf")) Icons.Default.PictureAsPdf
                        else Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (fileName.endsWith(".pdf")) DonutTheme.colorTokens.error
                        else MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing12))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fileName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    fileSize?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                IconButton(onClick = onRemove, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove file",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomActionBar(
    isPosting: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing12),
    ) {
        Button(
            onClick = onSubmit,
            enabled = !isPosting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp,
            ),
        ) {
            if (isPosting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = "Submit Report",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(DonutTheme.dimens.spacing16),
                )
            }
        }
    }
}

@Composable
private fun FilePickerDialog(
    onFileSelected: (name: String, size: String) -> Unit,
    onDismiss: () -> Unit,
) {
    val sampleFiles = listOf(
        Triple("Screenshot_bug_login.png", "1.2 MB", Icons.Default.Image),
        Triple("Crash_log_report.pdf", "340 KB", Icons.Default.PictureAsPdf),
        Triple("Error_screenshot.png", "2.1 MB", Icons.Default.Image),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Select a File",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing4)) {
                sampleFiles.forEach { (name, size, icon) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(DonutTheme.dimens.spacing12))
                            .clickable { onFileSelected(name, size) }
                            .padding(DonutTheme.dimens.spacing12),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(DonutTheme.dimens.spacing40)
                                .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                                .background(
                                    if (name.endsWith(".pdf")) DonutTheme.colorTokens.error.copy(alpha = 0.1f)
                                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = if (name.endsWith(".pdf")) DonutTheme.colorTokens.error
                                else MaterialTheme.colorScheme.primary,
                            )
                        }

                        Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing12))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = size,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(DonutTheme.dimens.spacing8))
                    .clickable(onClick = onDismiss)
                    .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing8),
            )
        },
    )
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
)
