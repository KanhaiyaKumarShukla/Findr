package com.exa.android.reflekt.feature.home.presentation.bug_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.Primary

// ═══════════════════════════════════════════════════════════════════════════════
// Data Models
// ═══════════════════════════════════════════════════════════════════════════════

data class BugDetail(
    val bugId: String,
    val priority: String,
    val status: String,
    val title: String,
    val reporterName: String,
    val reporterSubtitle: String,
    val reporterAvatarColor: Color,
    val timeAgo: String,
    val description: String,
    val errorCode: String,
    val errorTrace: String,
    val repoName: String,
    val filePath: String,
    val attachments: List<BugAttachment>,
    val comments: List<BugComment>,
    val assigneeColors: List<Color>,
    val extraAssigneeCount: Int = 0,
)

data class BugAttachment(
    val label: String,
    val color: Color,
)

data class BugComment(
    val authorName: String,
    val avatarColor: Color,
    val message: String,
    val timeAgo: String,
    val reactions: List<String> = emptyList(),
)

// ═══════════════════════════════════════════════════════════════════════════════
// Screen
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun BugDetailScreen(
    onBack: () -> Unit,
    onCollaborate: () -> Unit,
) {
    val bug = sampleBug()
    var commentText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DonutTheme.colorTokens.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 160.dp),
        ) {
            // Header
            BugHeader(bugId = bug.bugId, onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                // Priority & Status badges
                BadgesRow(priority = bug.priority, status = bug.status)

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = bug.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                    ),
                    color = DonutTheme.colorTokens.onBackground,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Reporter row
                ReporterRow(bug = bug)

                Spacer(modifier = Modifier.height(24.dp))

                // Description card
                DescriptionCard(
                    description = bug.description,
                    errorCode = bug.errorCode,
                    errorTrace = bug.errorTrace,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Repository link
                RepositoryCard(repoName = bug.repoName, filePath = bug.filePath)

                Spacer(modifier = Modifier.height(24.dp))

                // Attachments carousel
                AttachmentsSection(attachments = bug.attachments)

                Spacer(modifier = Modifier.height(24.dp))

                // Discussion
                DiscussionSection(
                    comments = bug.comments,
                    commentText = commentText,
                    onCommentChanged = { commentText = it },
                    onSend = { commentText = "" },
                )
            }
        }

        // Bottom action bar
        BugBottomBar(
            onCollaborate = onCollaborate,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Private Composables
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun BugHeader(bugId: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DonutTheme.colorTokens.surface),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = DonutTheme.colorTokens.onSurfaceVariant,
            ),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(22.dp),
            )
        }

        Text(
            text = bugId,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = DonutTheme.colorTokens.onBackground,
        )

        IconButton(
            onClick = { },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DonutTheme.colorTokens.surface),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = DonutTheme.colorTokens.onSurfaceVariant,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.MoreHoriz,
                contentDescription = "More",
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun BadgesRow(priority: String, status: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        // Priority badge
        Text(
            text = priority,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = DonutTheme.colorTokens.error,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(DonutTheme.colorTokens.error.copy(alpha = 0.15f))
                .border(1.dp, DonutTheme.colorTokens.error.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        )

        // Status badge
        Text(
            text = status,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = Primary,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Primary.copy(alpha = 0.15f))
                .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun ReporterRow(bug: BugDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Reporter avatar
            Box(
                modifier = Modifier.size(44.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    bug.reporterAvatarColor,
                                    bug.reporterAvatarColor.copy(alpha = 0.6f),
                                ),
                            ),
                        )
                        .border(2.dp, DonutTheme.colorTokens.background, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = bug.reporterName.first().toString(),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                }

                // Bug icon badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(DonutTheme.colorTokens.background)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.BugReport,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Primary,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = bug.reporterName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = DonutTheme.colorTokens.onBackground,
                )
                Text(
                    text = "Reported ${bug.timeAgo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }
        }

        // Assignee avatars
        if (bug.assigneeColors.isNotEmpty()) {
            Row {
                bug.assigneeColors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .offset(x = (-(index * 8)).dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(color, color.copy(alpha = 0.6f)),
                                ),
                            )
                            .border(2.dp, DonutTheme.colorTokens.background, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White,
                        )
                    }
                }

                if (bug.extraAssigneeCount > 0) {
                    Box(
                        modifier = Modifier
                            .offset(x = (-(bug.assigneeColors.size * 8)).dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DonutTheme.colorTokens.surface)
                            .border(2.dp, DonutTheme.colorTokens.background, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "+${bug.extraAssigneeCount}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = DonutTheme.colorTokens.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DescriptionCard(description: String, errorCode: String, errorTrace: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DonutTheme.colorTokens.surface)
            .border(1.dp, DonutTheme.colorTokens.outline.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(
            text = "DESCRIPTION",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp,
            ),
            color = DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = DonutTheme.colorTokens.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error code block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DonutTheme.colorTokens.background.copy(alpha = 0.8f))
                .border(1.dp, DonutTheme.colorTokens.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .padding(12.dp),
        ) {
            Text(
                text = errorCode,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = DonutTheme.colorTokens.error,
            )
            Text(
                text = errorTrace,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = DonutTheme.colorTokens.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RepositoryCard(repoName: String, filePath: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DonutTheme.colorTokens.surface)
            .border(1.dp, DonutTheme.colorTokens.outline.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { },
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF1a1a2e)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = repoName,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = DonutTheme.colorTokens.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = filePath,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                color = DonutTheme.colorTokens.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = "Open",
            modifier = Modifier.size(16.dp),
            tint = DonutTheme.colorTokens.onSurfaceVariant,
        )
    }
}

@Composable
private fun AttachmentsSection(attachments: List<BugAttachment>) {
    Column {
        Text(
            text = "ATTACHMENTS (${attachments.size})",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp,
            ),
            color = DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            attachments.forEach { attachment ->
                Box(
                    modifier = Modifier
                        .size(width = 128.dp, height = 176.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    attachment.color.copy(alpha = 0.3f),
                                    attachment.color.copy(alpha = 0.1f),
                                ),
                            ),
                        )
                        .border(1.dp, DonutTheme.colorTokens.outline.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = attachment.color.copy(alpha = 0.6f),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = attachment.label,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = DonutTheme.colorTokens.onSurfaceVariant,
                        )
                    }
                }
            }

            // Add evidence button
            Box(
                modifier = Modifier
                    .size(width = 128.dp, height = 176.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 2.dp,
                        color = DonutTheme.colorTokens.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = { },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Add Evidence",
                        modifier = Modifier.size(24.dp),
                        tint = DonutTheme.colorTokens.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add Evidence",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = DonutTheme.colorTokens.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscussionSection(
    comments: List<BugComment>,
    commentText: String,
    onCommentChanged: (String) -> Unit,
    onSend: () -> Unit,
) {
    Column {
        Text(
            text = "DISCUSSION",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp,
            ),
            color = DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(16.dp))

        comments.forEach { comment ->
            CommentBubble(comment = comment)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Comment input
        OutlinedTextField(
            value = commentText,
            onValueChange = onCommentChanged,
            placeholder = {
                Text(
                    text = "Add a comment...",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onSend,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Primary),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White,
                    )
                }
            },
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = DonutTheme.colorTokens.outline.copy(alpha = 0.5f),
                focusedBorderColor = Primary,
                unfocusedContainerColor = DonutTheme.colorTokens.surface,
                focusedContainerColor = DonutTheme.colorTokens.surface,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CommentBubble(comment: BugComment) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            comment.avatarColor,
                            comment.avatarColor.copy(alpha = 0.6f),
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = comment.authorName.first().toString(),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = DonutTheme.colorTokens.onBackground,
                )
                Text(
                    text = comment.timeAgo,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment.message,
                style = MaterialTheme.typography.bodyMedium,
                color = DonutTheme.colorTokens.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 12.dp,
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp,
                        ),
                    )
                    .background(DonutTheme.colorTokens.surface)
                    .border(
                        1.dp,
                        DonutTheme.colorTokens.outline.copy(alpha = 0.1f),
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 12.dp,
                            bottomStart = 12.dp,
                            bottomEnd = 12.dp,
                        ),
                    )
                    .padding(12.dp),
            )

            if (comment.reactions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    comment.reactions.forEach { reaction ->
                        Text(
                            text = reaction,
                            style = MaterialTheme.typography.bodySmall,
                            color = Primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Primary.copy(alpha = 0.1f))
                                .border(1.dp, Primary.copy(alpha = 0.2f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BugBottomBar(
    onCollaborate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        DonutTheme.colorTokens.background.copy(alpha = 0.9f),
                        DonutTheme.colorTokens.background,
                    ),
                ),
            )
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 32.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Primary)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = Color.White),
                    onClick = onCollaborate,
                )
                .drawBehind {
                    drawCircle(
                        color = Primary.copy(alpha = 0.3f),
                        radius = size.width * 0.6f,
                        center = Offset(size.width / 2, size.height + 20f),
                    )
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Handyman,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.White,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Collaborate to Fix",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
                color = Color.White,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Sample Data
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun sampleBug() = BugDetail(
    bugId = "#BUG-402",
    priority = "High Priority",
    status = "In Progress",
    title = "Login API returns 500 error on iOS devices during peak load",
    reporterName = "Alex Chen",
    reporterSubtitle = "Reported 2h ago",
    reporterAvatarColor = DonutTheme.staticColorTokens.accentIndigo,
    timeAgo = "2h ago",
    description = "When attempting to log in with a valid user token, the server crashes intermittently. This seems to happen specifically when multiple concurrent requests are hit from iOS clients.",
    errorCode = "Error: 500 Internal Server Error",
    errorTrace = "at AuthController.login (lines 42:15)",
    repoName = "student-collab-backend",
    filePath = "/src/controllers/auth.ts",
    attachments = listOf(
        BugAttachment("Error Code", DonutTheme.staticColorTokens.accentBlue),
        BugAttachment("Stack Trace", DonutTheme.staticColorTokens.accentPurple),
    ),
    comments = listOf(
        BugComment(
            authorName = "Sarah Jenkins",
            avatarColor = DonutTheme.staticColorTokens.accentGreen,
            message = "I think this is a database timeout. The connection pool might be exhausted.",
            timeAgo = "1h ago",
        ),
        BugComment(
            authorName = "Marcus Ray",
            avatarColor = DonutTheme.staticColorTokens.accentOrange,
            message = "Checking the logs now. I'll push a fix if it's just the pool config.",
            timeAgo = "45m ago",
            reactions = listOf("\uD83D\uDC4D 1"),
        ),
    ),
    assigneeColors = listOf(
        DonutTheme.staticColorTokens.accentBlue,
    ),
    extraAssigneeCount = 2,
)
