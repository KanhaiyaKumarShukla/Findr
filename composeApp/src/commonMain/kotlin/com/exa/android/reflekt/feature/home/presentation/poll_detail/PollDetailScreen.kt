package com.exa.android.reflekt.feature.home.presentation.poll_detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.Primary

// Data Models

data class PollDetail(
    val question: String,
    val isLive: Boolean,
    val timeRemaining: String,
    val totalVotes: Int,
    val participationRate: Int,
    val commentsCount: Int,
    val options: List<PollOptionDetail>,
    val recentVoters: List<RecentVoter>,
    val allowMultipleVotes: Boolean,
)

data class PollOptionDetail(
    val text: String,
    val percentage: Int,
    val voteCount: Int,
    val color: Color,
)

data class RecentVoter(
    val name: String,
    val avatarColor: Color,
    val votedFor: String,
    val timeAgo: String,
)

// Screen

@Composable
fun PollDetailScreen(
    onBack: () -> Unit,
    onClosePoll: () -> Unit,
) {
    val poll = samplePoll()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DonutTheme.colorTokens.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 140.dp),
        ) {
            PollHeader(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                // Question + Live badge
                PollQuestionSection(
                    question = poll.question,
                    isLive = poll.isLive,
                    timeRemaining = poll.timeRemaining,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stats cards
                StatsRow(
                    totalVotes = poll.totalVotes,
                    participationRate = poll.participationRate,
                    commentsCount = poll.commentsCount,
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Current standings
                StandingsSection(options = poll.options)

                Spacer(modifier = Modifier.height(24.dp))

                // Recent voters
                RecentVotersSection(voters = poll.recentVoters)

                Spacer(modifier = Modifier.height(24.dp))

                // Settings
                SettingsSection(allowMultiple = poll.allowMultipleVotes)
            }
        }

        // Bottom bar
        PollBottomBar(
            onEditOptions = { },
            onClosePoll = onClosePoll,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

// Private Composables

@Composable
private fun PollHeader(onBack: () -> Unit) {
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
            text = "Poll Management",
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
private fun PollQuestionSection(
    question: String,
    isLive: Boolean,
    timeRemaining: String,
) {
    Column {
        // Live badge row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (isLive) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(DonutTheme.staticColorTokens.accentGreen.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            DonutTheme.staticColorTokens.accentGreen.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(DonutTheme.staticColorTokens.accentGreen),
                        )
                        Text(
                            text = "Live",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            color = DonutTheme.staticColorTokens.accentGreen,
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = DonutTheme.colorTokens.onSurfaceVariant,
                )
                Text(
                    text = timeRemaining,
                    style = MaterialTheme.typography.bodySmall,
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = question,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp,
            ),
            color = DonutTheme.colorTokens.onBackground,
        )
    }
}

@Composable
private fun StatsRow(totalVotes: Int, participationRate: Int, commentsCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(
            icon = Icons.Default.HowToVote,
            label = "Total Votes",
            value = totalVotes.toString(),
            color = Primary,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            icon = Icons.Default.Groups,
            label = "Participation",
            value = "$participationRate%",
            color = DonutTheme.staticColorTokens.accentGreen,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            icon = Icons.AutoMirrored.Filled.Comment,
            label = "Comments",
            value = commentsCount.toString(),
            color = DonutTheme.staticColorTokens.accentPurple,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DonutTheme.colorTokens.surface)
            .border(1.dp, DonutTheme.colorTokens.outline.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = color,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = DonutTheme.colorTokens.onBackground,
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = DonutTheme.colorTokens.onSurfaceVariant,
        )
    }
}

@Composable
private fun StandingsSection(options: List<PollOptionDetail>) {
    Column {
        Text(
            text = "CURRENT STANDINGS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp,
            ),
            color = DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DonutTheme.colorTokens.surface)
                .border(
                    1.dp,
                    DonutTheme.colorTokens.outline.copy(alpha = 0.1f),
                    RoundedCornerShape(16.dp),
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            options.forEachIndexed { index, option ->
                StandingBar(option = option, rank = index + 1)
            }
        }
    }
}

@Composable
private fun StandingBar(option: PollOptionDetail, rank: Int) {
    val fraction = option.percentage / 100f
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 600),
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (rank == 1) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = option.color,
                    )
                }
                Text(
                    text = option.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (rank == 1) FontWeight.SemiBold else FontWeight.Normal,
                    ),
                    color = DonutTheme.colorTokens.onBackground,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "${option.percentage}%",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = option.color,
                )
                Text(
                    text = "(${option.voteCount})",
                    style = MaterialTheme.typography.bodySmall,
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(DonutTheme.colorTokens.background),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(option.color, option.color.copy(alpha = 0.6f)),
                        ),
                    ),
            )
        }
    }
}

@Composable
private fun RecentVotersSection(voters: List<RecentVoter>) {
    Column {
        Text(
            text = "RECENT VOTERS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp,
            ),
            color = DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DonutTheme.colorTokens.surface)
                .border(
                    1.dp,
                    DonutTheme.colorTokens.outline.copy(alpha = 0.1f),
                    RoundedCornerShape(16.dp),
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            voters.forEach { voter ->
                VoterRow(voter = voter)
            }
        }
    }
}

@Composable
private fun VoterRow(voter: RecentVoter) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(voter.avatarColor, voter.avatarColor.copy(alpha = 0.6f)),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = voter.name.first().toString(),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = voter.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = DonutTheme.colorTokens.onBackground,
            )
            Text(
                text = "Voted for ${voter.votedFor}",
                style = MaterialTheme.typography.bodySmall,
                color = DonutTheme.colorTokens.onSurfaceVariant,
            )
        }

        Text(
            text = voter.timeAgo,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = DonutTheme.colorTokens.onSurfaceVariant,
        )
    }
}

@Composable
private fun SettingsSection(allowMultiple: Boolean) {
    Column {
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                fontSize = 10.sp,
            ),
            color = DonutTheme.colorTokens.onSurfaceVariant.copy(alpha = 0.7f),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DonutTheme.colorTokens.surface)
                .border(
                    1.dp,
                    DonutTheme.colorTokens.outline.copy(alpha = 0.1f),
                    RoundedCornerShape(16.dp),
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = DonutTheme.colorTokens.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Allow Multiple Votes",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = DonutTheme.colorTokens.onBackground,
                )
                Text(
                    text = "Participants can select more than one option",
                    style = MaterialTheme.typography.bodySmall,
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }

            Switch(
                checked = allowMultiple,
                onCheckedChange = null,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Primary,
                    checkedThumbColor = Color.White,
                ),
            )
        }
    }
}

@Composable
private fun PollBottomBar(
    onEditOptions: () -> Unit,
    onClosePoll: () -> Unit,
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Edit Options button
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DonutTheme.colorTokens.surface)
                    .border(
                        1.dp,
                        DonutTheme.colorTokens.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(14.dp),
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        onClick = onEditOptions,
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = DonutTheme.colorTokens.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Edit Options",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = DonutTheme.colorTokens.onSurfaceVariant,
                )
            }

            // Close Poll button
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DonutTheme.colorTokens.error)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White),
                        onClick = onClosePoll,
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Close Poll",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                )
            }
        }
    }
}

// Sample Data

@Composable
private fun samplePoll() = PollDetail(
    question = "Where should we hold the annual tech fest?",
    isLive = true,
    timeRemaining = "2d 14h remaining",
    totalVotes = 124,
    participationRate = 82,
    commentsCount = 14,
    options = listOf(
        PollOptionDetail(
            text = "The Quad",
            percentage = 45,
            voteCount = 56,
            color = Primary,
        ),
        PollOptionDetail(
            text = "Student Union",
            percentage = 30,
            voteCount = 37,
            color = DonutTheme.staticColorTokens.accentPurple,
        ),
        PollOptionDetail(
            text = "Off-campus Venue",
            percentage = 25,
            voteCount = 31,
            color = DonutTheme.staticColorTokens.accentOrange,
        ),
    ),
    recentVoters = listOf(
        RecentVoter(
            name = "Emma Wilson",
            avatarColor = DonutTheme.staticColorTokens.accentIndigo,
            votedFor = "The Quad",
            timeAgo = "2m ago",
        ),
        RecentVoter(
            name = "James Chen",
            avatarColor = DonutTheme.staticColorTokens.accentGreen,
            votedFor = "Student Union",
            timeAgo = "5m ago",
        ),
        RecentVoter(
            name = "Aisha Patel",
            avatarColor = DonutTheme.staticColorTokens.accentOrange,
            votedFor = "The Quad",
            timeAgo = "12m ago",
        ),
    ),
    allowMultipleVotes = false,
)
