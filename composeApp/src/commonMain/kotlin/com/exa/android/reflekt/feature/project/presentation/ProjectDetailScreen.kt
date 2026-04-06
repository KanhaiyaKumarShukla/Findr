package com.exa.android.reflekt.feature.project.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.Primary
import com.exa.android.reflekt.ui.theme.DonutTheme

// ═══════════════════════════════════════════════════════════════════════════════
// Data Models
// ═══════════════════════════════════════════════════════════════════════════════

data class OpenRole(
    val title: String,
    val description: String,
    val skills: List<String>,
    val isHighlighted: Boolean = false,
)

data class Collaborator(
    val name: String,
    val avatarColor: Color,
    val icon: ImageVector = Icons.Default.Person,
)

data class DiscussionComment(
    val authorName: String,
    val avatarColor: Color,
    val message: String,
    val timeAgo: String,
    val isOwner: Boolean = false,
)

data class ProjectDetail(
    val category: String,
    val title: String,
    val timeAgo: String,
    val authorName: String,
    val authorSubtitle: String,
    val authorAvatarColor: Color,
    val spotsTotal: Int,
    val spotsFilled: Int,
    val description: String,
    val roles: List<OpenRole>,
    val collaborators: List<Collaborator>,
    val comments: List<DiscussionComment>,
)

// ═══════════════════════════════════════════════════════════════════════════════
// Screen
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun ProjectDetailScreen(
    onBack: () -> Unit,
    onEnroll: () -> Unit,
) {
    val project = sampleProject()
    var isDescriptionExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DonutTheme.colorTokens.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Hero image section
            HeroSection(onBack = onBack)

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-32).dp)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 100.dp),
            ) {
                // Project header card
                ProjectHeaderCard(project = project)

                Spacer(modifier = Modifier.height(24.dp))

                // Team status
                TeamStatusSection(
                    spotsFilled = project.spotsFilled,
                    spotsTotal = project.spotsTotal,
                )

                Spacer(modifier = Modifier.height(28.dp))

                // About
                AboutSection(
                    description = project.description,
                    isExpanded = isDescriptionExpanded,
                    onToggleExpand = { isDescriptionExpanded = !isDescriptionExpanded },
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Open roles
                OpenRolesSection(roles = project.roles)

                Spacer(modifier = Modifier.height(28.dp))

                // Collaborators
                CollaboratorsSection(collaborators = project.collaborators)

                Spacer(modifier = Modifier.height(28.dp))

                // Discussion
                DiscussionSection(comments = project.comments)
            }
        }

        // Sticky "Enroll Now" button
        EnrollButton(
            onClick = onEnroll,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Private Composables
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeroSection(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(288.dp),
    ) {
        // Gradient background (placeholder for image)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            DonutTheme.colorTokens.surfaceVariant,
                            DonutTheme.colorTokens.surfaceVariant,
                            DonutTheme.staticColorTokens.accentBlue,
                            Primary.copy(alpha = 0.8f),
                        ),
                        start = Offset.Zero,
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    ),
                ),
        ) {
            // Decorative elements
            HeroDecorations()
        }

        // Dark gradient overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            DonutTheme.colorTokens.background,
                        ),
                    ),
                ),
        )

        // Top nav buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Back button
            GlassButton(
                onClick = onBack,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
            )

            // Bookmark & Share
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassButton(
                    onClick = { },
                    icon = Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                )
                GlassButton(
                    onClick = { },
                    icon = Icons.Default.Share,
                    contentDescription = "Share",
                )
            }
        }
    }
}

@Composable
private fun HeroDecorations() {
    // Central icon
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.White.copy(alpha = 0.6f),
            )
        }
    }

    // Floating small circles
    val infiniteTransition = rememberInfiniteTransition(label = "hero")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "float",
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = 40f,
            center = Offset(size.width * 0.15f, size.height * 0.3f + floatY),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.06f),
            radius = 60f,
            center = Offset(size.width * 0.85f, size.height * 0.25f - floatY),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = 30f,
            center = Offset(size.width * 0.7f, size.height * 0.65f + floatY * 0.5f),
        )
    }
}

@Composable
private fun GlassButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.2f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = Color.White,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun ProjectHeaderCard(project: ProjectDetail) {
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
            // Category badge
            Text(
                text = project.category.uppercase(),
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

            // Time ago
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = DonutTheme.colorTokens.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = project.timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = project.title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
            ),
            color = DonutTheme.colorTokens.onBackground,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DonutTheme.colorTokens.outlineVariant.copy(alpha = 0.5f)),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Author row
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                project.authorAvatarColor,
                                project.authorAvatarColor.copy(alpha = 0.6f),
                            ),
                        ),
                    )
                    .border(2.dp, Primary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = project.authorName.first().toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = project.authorName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = DonutTheme.colorTokens.onBackground,
                )
                Text(
                    text = project.authorSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TeamStatusSection(spotsFilled: Int, spotsTotal: Int) {
    val progress = spotsFilled.toFloat() / spotsTotal.toFloat()
    val spotsRemaining = spotsTotal - spotsFilled

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
                text = "$spotsFilled/$spotsTotal Spots Filled",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = Primary,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50)),
        ) {
            // Track
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.15f),
                    ),
            )
            // Fill
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Primary, Primary.copy(alpha = 0.8f)),
                        ),
                    ),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Only $spotsRemaining roles remaining. Application closes in 48h.",
            style = MaterialTheme.typography.bodySmall,
            color = DonutTheme.colorTokens.onSurfaceVariant,
        )
    }
}

@Composable
private fun AboutSection(
    description: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
) {
    Column {
        Text(
            text = "About the Project",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = DonutTheme.colorTokens.onBackground,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 22.sp,
            ),
            color = DonutTheme.colorTokens.onSurfaceVariant,
            maxLines = if (isExpanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onToggleExpand,
                )
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isExpanded) "Show Less" else "Read More",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                ),
                color = Primary,
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = Primary,
            )
        }
    }
}

@Composable
private fun OpenRolesSection(roles: List<OpenRole>) {
    Column {
        Text(
            text = "Open Roles",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = DonutTheme.colorTokens.onBackground,
        )

        Spacer(modifier = Modifier.height(16.dp))

        roles.forEachIndexed { index, role ->
            RoleCard(role = role)
            if (index < roles.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun RoleCard(role: OpenRole) {
    val borderColor = if (role.isHighlighted) {
        Primary.copy(alpha = 0.3f)
    } else {
        DonutTheme.colorTokens.outline.copy(alpha = 0.3f)
    }
    val backgroundColor = if (role.isHighlighted) {
        Primary.copy(alpha = 0.05f)
    } else {
        DonutTheme.colorTokens.surface
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = role.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = DonutTheme.colorTokens.onBackground,
                )

                if (role.isHighlighted) {
                    PulsingDot()
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = role.description,
                style = MaterialTheme.typography.bodySmall,
                color = DonutTheme.colorTokens.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Skill tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()),
            ) {
                role.skills.forEach { skill ->
                    SkillTag(text = skill)
                }
            }
        }
    }
}

@Composable
private fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Primary.copy(alpha = alpha)),
    )
}

@Composable
private fun SkillTag(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium,
        ),
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

@Composable
private fun CollaboratorsSection(collaborators: List<Collaborator>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Collaborators",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = DonutTheme.colorTokens.onBackground,
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                ),
                color = Primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = { },
                    )
                    .padding(4.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) {
            collaborators.forEach { collaborator ->
                CollaboratorAvatar(collaborator = collaborator)
            }

            // Invite button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = DonutTheme.colorTokens.outline.copy(alpha = 0.3f),
                            shape = CircleShape,
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = { },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Invite",
                        modifier = Modifier.size(24.dp),
                        tint = DonutTheme.colorTokens.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Invite",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                    ),
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CollaboratorAvatar(collaborator: Collaborator) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            collaborator.avatarColor,
                            collaborator.avatarColor.copy(alpha = 0.6f),
                        ),
                    ),
                )
                .border(2.dp, DonutTheme.colorTokens.surface, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = collaborator.name.first().toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = collaborator.name,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
            ),
            color = DonutTheme.colorTokens.onSurfaceVariant,
        )
    }
}

@Composable
private fun DiscussionSection(comments: List<DiscussionComment>) {
    var commentText by remember { mutableStateOf("") }

    Column {
        Text(
            text = "Discussion (${comments.size})",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = DonutTheme.colorTokens.onBackground,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Comments
        comments.forEach { comment ->
            CommentBubble(comment = comment)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Input field
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = {
                Text(
                    text = "Ask a question...",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { commentText = "" },
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
private fun CommentBubble(comment: DiscussionComment) {
    val bgColor = if (comment.isOwner) {
        Primary.copy(alpha = 0.1f)
    } else {
        DonutTheme.colorTokens.surface
    }
    val borderColor = if (comment.isOwner) {
        Primary.copy(alpha = 0.2f)
    } else {
        DonutTheme.colorTokens.outline.copy(alpha = 0.2f)
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        // Avatar
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
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Message bubble
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 12.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp,
                    ),
                )
                .background(bgColor)
                .border(
                    1.dp,
                    borderColor,
                    RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 12.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp,
                    ),
                )
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.authorName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = if (comment.isOwner) Primary else DonutTheme.colorTokens.onBackground,
                    )

                    if (comment.isOwner) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OWNER",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Primary)
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                        )
                    }
                }

                Text(
                    text = comment.timeAgo,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                    ),
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment.message,
                style = MaterialTheme.typography.bodyMedium,
                color = DonutTheme.colorTokens.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EnrollButton(
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
                        DonutTheme.colorTokens.background.copy(alpha = 0.9f),
                        DonutTheme.colorTokens.background,
                    ),
                ),
            )
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 32.dp),
        contentAlignment = Alignment.Center,
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
                    onClick = onClick,
                )
                .drawBehind {
                    // Subtle shadow glow
                    drawCircle(
                        color = Primary.copy(alpha = 0.3f),
                        radius = size.width * 0.6f,
                        center = Offset(size.width / 2, size.height + 20f),
                    )
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Enroll Now",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
                color = Color.White,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.White,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Sample Data
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun sampleProject() = ProjectDetail(
    category = "Tech Startup",
    title = "EcoTracker: Campus Carbon Footprint App",
    timeAgo = "2 days ago",
    authorName = "Alex Johnson",
    authorSubtitle = "Computer Science \u2022 Junior",
    authorAvatarColor = DonutTheme.staticColorTokens.accentIndigo,
    spotsTotal = 5,
    spotsFilled = 3,
    description = "We are building a cross-platform mobile app to track carbon footprints for campus buildings in real-time. The goal is to gamify energy saving for students. We have the initial prototype ready in Figma and are looking for developers to bring it to life using React Native.",
    roles = listOf(
        OpenRole(
            title = "Frontend Developer",
            description = "Lead the implementation of UI components.",
            skills = listOf("React Native", "TypeScript", "Tailwind"),
            isHighlighted = true,
        ),
        OpenRole(
            title = "UI/UX Designer",
            description = "Refine user flows and create high-fidelity assets.",
            skills = listOf("Figma", "Prototyping"),
        ),
    ),
    collaborators = listOf(
        Collaborator(name = "You", avatarColor = DonutTheme.staticColorTokens.accentBlue),
        Collaborator(name = "Sarah", avatarColor = DonutTheme.colorTokens.error),
        Collaborator(name = "Mike", avatarColor = DonutTheme.staticColorTokens.accentGreen),
    ),
    comments = listOf(
        DiscussionComment(
            authorName = "Dave M.",
            avatarColor = DonutTheme.staticColorTokens.accentOrange,
            message = "Is this a paid project or for credit?",
            timeAgo = "1h ago",
        ),
        DiscussionComment(
            authorName = "Alex Johnson",
            avatarColor = DonutTheme.staticColorTokens.accentIndigo,
            message = "Hey Dave! It's unpaid but great for your portfolio. We might enter the campus hackathon though!",
            timeAgo = "30m ago",
            isOwner = true,
        ),
    ),
)
