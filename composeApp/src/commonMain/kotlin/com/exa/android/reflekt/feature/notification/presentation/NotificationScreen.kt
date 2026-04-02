package com.exa.android.reflekt.feature.notification.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.appColors

// ─── Models ──────────────────────────────────────────────────────────────────

private enum class NotificationType {
    PROFILE_VIEW, JOB, REMINDER, COMMENT, EVENT, PLACEMENT
}

private data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timeAgo: String,
    val isUnread: Boolean = false,
    val hasActions: Boolean = false,
    val quotedReply: String? = null,
    val replyAuthor: String? = null,
    val replyContext: String? = null,
)

private val filterChips = listOf("All", "Placements", "Social", "Events")

// ─── Sample Data ─────────────────────────────────────────────────────────────

private val todayNotifications = listOf(
    NotificationItem(
        id = "1",
        type = NotificationType.PROFILE_VIEW,
        title = "3 recruiters viewed your profile",
        body = "Recruiters from Google and Meta checked your profile today. Update your resume for better visibility.",
        timeAgo = "2m ago",
        isUnread = true,
    ),
    NotificationItem(
        id = "2",
        type = NotificationType.JOB,
        title = "Google posted a new role",
        body = "Software Engineer Intern (Summer 2025) is now open for applications. Deadline in 5 days.",
        timeAgo = "15m ago",
        isUnread = true,
        hasActions = true,
    ),
    NotificationItem(
        id = "3",
        type = NotificationType.REMINDER,
        title = "Database Midterm Reminder",
        body = "Upcoming: Database Systems Midterm tomorrow at 10:00 AM. Don't forget your ID!",
        timeAgo = "1h ago",
    ),
)

private val yesterdayNotifications = listOf(
    NotificationItem(
        id = "4",
        type = NotificationType.COMMENT,
        title = "", // handled with annotated string
        body = "",
        timeAgo = "1d ago",
        replyAuthor = "Sarah Williams",
        replyContext = "Project Collab",
        quotedReply = "\"I'd love to help with the backend integration! Let's meet tomorrow?\"",
    ),
    NotificationItem(
        id = "5",
        type = NotificationType.EVENT,
        title = "New post in Events",
        body = "Hackathon 2024 is now open! \$5000 prize pool. Form your teams by Friday.",
        timeAgo = "1d ago",
    ),
    NotificationItem(
        id = "6",
        type = NotificationType.PLACEMENT,
        title = "Placement Deadline",
        body = "Microsoft SDE-1 application closes in 24 hours. Complete your coding test now.",
        timeAgo = "1d ago",
    ),
)

// ─── Screen ──────────────────────────────────────────────────────────────────

@Composable
fun NotificationScreen(
    onBack: () -> Unit,
) {
    var selectedFilter by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // ── Sticky Header ────────────────────────────────────────────────────
        NotificationHeader(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
            onBack = onBack,
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // ── Notification List ────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // Today section
            item {
                SectionHeader(
                    title = "Today",
                    showMarkAllRead = true,
                )
            }
            items(todayNotifications, key = { it.id }) { notification ->
                NotificationRow(notification)
            }

            // Yesterday section
            item {
                SectionHeader(title = "Yesterday")
            }
            items(yesterdayNotifications, key = { it.id }) { notification ->
                NotificationRow(notification)
            }

            // Caught up
            item {
                CaughtUpSection()
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// Private composables
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun NotificationHeader(
    selectedFilter: Int,
    onFilterSelected: (Int) -> Unit,
    onBack: () -> Unit,
) {
    Column {
        // Title row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.weight(1f),
            )

            IconButton(onClick = { /* TODO: Settings */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        // Filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            filterChips.forEachIndexed { index, label ->
                FilterChip(
                    label = label,
                    isSelected = index == selectedFilter,
                    onClick = { onFilterSelected(index) },
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (isSelected) {
        Color.White
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    showMarkAllRead: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (showMarkAllRead) {
            Text(
                text = "Mark all as read",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = { /* TODO */ },
                    )
                    .padding(4.dp),
            )
        }
    }
}

@Composable
private fun NotificationRow(notification: NotificationItem) {
    val iconInfo = notification.iconInfo()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (notification.isUnread) {
                    Modifier.drawUnreadIndicator()
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { /* TODO: Open notification detail */ },
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconInfo.backgroundColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = iconInfo.icon,
                contentDescription = null,
                tint = iconInfo.tintColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            // Title + time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (notification.type == NotificationType.COMMENT) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(notification.replyAuthor ?: "")
                            }
                            append(" replied to your comment in ")
                            withStyle(
                                SpanStyle(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            ) {
                                append(notification.replyContext ?: "")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = notification.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Body or quoted reply
            if (notification.quotedReply != null) {
                Text(
                    text = notification.quotedReply,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(8.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                // Reply / Like actions
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Reply",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(),
                                onClick = { /* TODO */ },
                            )
                            .padding(2.dp),
                    )
                    Text(
                        text = "Like",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(),
                                onClick = { /* TODO */ },
                            )
                            .padding(2.dp),
                    )
                }
            } else {
                Text(
                    text = notification.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            // Action buttons (Apply Now / Save)
            if (notification.hasActions) {
                Row(
                    modifier = Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = { /* TODO: Apply */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp),
                    ) {
                        Text(
                            text = "Apply Now",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }

                    Button(
                        onClick = { /* TODO: Save */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp),
                    ) {
                        Text(
                            text = "Save",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                }
            }
        }

        // Unread dot
        if (notification.isUnread) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
private fun CaughtUpSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You're all caught up!",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Check back later for new updates and opportunities.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

private data class IconInfo(
    val icon: ImageVector,
    val tintColor: Color,
    val backgroundColor: Color,
)

private val AmberColor = Color(0xFFF59E0B)
private val RoseColor = Color(0xFFEF4444)
private val EmeraldColor = Color(0xFF10B981)
private val PurpleColor = Color(0xFF8B5CF6)

@Composable
private fun NotificationItem.iconInfo(): IconInfo = when (type) {
    NotificationType.PROFILE_VIEW -> IconInfo(
        icon = Icons.Default.Visibility,
        tintColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
    )
    NotificationType.JOB -> IconInfo(
        icon = Icons.Default.Work,
        tintColor = AmberColor,
        backgroundColor = AmberColor.copy(alpha = 0.15f),
    )
    NotificationType.REMINDER -> IconInfo(
        icon = Icons.Default.Event,
        tintColor = RoseColor,
        backgroundColor = RoseColor.copy(alpha = 0.15f),
    )
    NotificationType.COMMENT -> IconInfo(
        icon = Icons.Default.Campaign,
        tintColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
    )
    NotificationType.EVENT -> IconInfo(
        icon = Icons.Default.Campaign,
        tintColor = EmeraldColor,
        backgroundColor = EmeraldColor.copy(alpha = 0.15f),
    )
    NotificationType.PLACEMENT -> IconInfo(
        icon = Icons.Default.Verified,
        tintColor = PurpleColor,
        backgroundColor = PurpleColor.copy(alpha = 0.15f),
    )
}

/**
 * Draws a left-side primary-colored border to indicate an unread notification.
 */
private fun Modifier.drawUnreadIndicator(): Modifier = this.then(
    Modifier.drawWithContent {
        drawContent()
        drawRect(
            color = PrimaryBlue,
            topLeft = Offset.Zero,
            size = Size(4.dp.toPx(), size.height),
        )
    }
)

private val PrimaryBlue = Color(0xFF13B6EC)
