package com.exa.android.reflekt.feature.post.presentation.create_project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectPreviewSheet(
    state: CreateProjectUiState,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DonutTheme.colorTokens.background,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.3f)),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Preview",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = DonutTheme.colorTokens.onBackground,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp),
        ) {
            // Project Header Card
            PreviewHeaderCard(state)

            Spacer(modifier = Modifier.height(24.dp))

            // Team Status
            PreviewTeamStatus(openings = state.numberOfOpenings)

            Spacer(modifier = Modifier.height(28.dp))

            // About
            if (state.description.isNotBlank()) {
                PreviewAboutSection(description = state.description)
                Spacer(modifier = Modifier.height(28.dp))
            }

            // Open Role
            if (state.roleName.isNotBlank()) {
                PreviewRoleSection(
                    roleName = state.roleName,
                    skills = state.techSkills,
                    openings = state.numberOfOpenings,
                )
                Spacer(modifier = Modifier.height(28.dp))
            }

            // Attachment
            if (state.attachedFileName != null) {
                PreviewAttachmentSection(
                    fileName = state.attachedFileName,
                    fileSize = state.attachedFileSize,
                )
            }
        }
    }
}

@Composable
private fun PreviewHeaderCard(state: CreateProjectUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DonutTheme.colorTokens.surface)
            .border(
                width = 1.dp,
                color = DonutTheme.colorTokens.outline.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(20.dp),
    ) {
        // Category & Time
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "PROJECT",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                ),
                color = Primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Primary.copy(alpha = 0.1f))
                    .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = DonutTheme.colorTokens.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Just now",
                    style = MaterialTheme.typography.bodySmall,
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = state.projectTitle.ifBlank { "Untitled Project" },
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
            ),
            color = if (state.projectTitle.isBlank()) {
                DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.5f)
            } else {
                DonutTheme.colorTokens.onBackground
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DonutTheme.colorTokens.outlineVariant.copy(alpha = 0.5f)),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Author row (placeholder — the current user)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                DonutTheme.staticColorTokens.accentBlue,
                                DonutTheme.staticColorTokens.accentBlue.copy(alpha = 0.6f),
                            ),
                        ),
                    )
                    .border(2.dp, Primary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = Color.White,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "You",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = DonutTheme.colorTokens.onBackground,
                )
                Text(
                    text = "Project Owner",
                    style = MaterialTheme.typography.bodySmall,
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PreviewTeamStatus(openings: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = "TEAM STATUS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp,
                ),
                color = DonutTheme.colorTokens.onSurfaceVariant,
            )
            Text(
                text = "0/$openings Spots Filled",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Primary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.15f)),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$openings open role${if (openings != 1) "s" else ""} available.",
            style = MaterialTheme.typography.bodySmall,
            color = DonutTheme.colorTokens.onSurfaceVariant,
        )
    }
}

@Composable
private fun PreviewAboutSection(description: String) {
    Column {
        Text(
            text = "About the Project",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = DonutTheme.colorTokens.onBackground,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = DonutTheme.colorTokens.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreviewRoleSection(
    roleName: String,
    skills: List<String>,
    openings: Int,
) {
    Column {
        Text(
            text = "Open Roles",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = DonutTheme.colorTokens.onBackground,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Role card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Primary.copy(alpha = 0.05f))
                .border(1.dp, Primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = roleName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = DonutTheme.colorTokens.onBackground,
                )

                Text(
                    text = "$openings spot${if (openings != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = Primary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                )
            }

            if (skills.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    skills.forEach { skill ->
                        Text(
                            text = skill,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = DonutTheme.colorTokens.onSurfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DonutTheme.colorTokens.background)
                                .border(
                                    1.dp,
                                    DonutTheme.colorTokens.outline.copy(alpha = 0.3f),
                                    RoundedCornerShape(4.dp),
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PreviewAttachmentSection(fileName: String, fileSize: String?) {
    Column {
        Text(
            text = "Attachments",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = DonutTheme.colorTokens.onBackground,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DonutTheme.colorTokens.surface)
                .border(
                    1.dp,
                    DonutTheme.colorTokens.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp),
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DonutTheme.colorTokens.error.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (fileName.endsWith(".pdf")) Icons.Default.PictureAsPdf else Icons.Default.Attachment,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = DonutTheme.colorTokens.error,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = DonutTheme.colorTokens.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                fileSize?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = DonutTheme.colorTokens.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
