package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.feature.home.presentation.PollPost
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize

@Composable
internal fun PollCard(
    poll: PollPost,
    onEvent: (HomeEvent) -> Unit,
) {
    val colors = DonutTheme.colorTokens
    val avatarColor = poll.avatarColorArgb.toColor()
    val accentColor = colorFromKey(poll.accentColorKey)
    val hasVoted = poll.votedIndex != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing4)
            .clip(RoundedCornerShape(DonutRadius.panel))
            .background(colors.surface)
            .border(DonutTheme.dimens.borderThin, accentColor.copy(alpha = 0.25f), RoundedCornerShape(DonutRadius.panel)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DonutTheme.dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(DonutTheme.dimens.avatarSizeMedium)
                    .clip(CircleShape)
                    .background(avatarColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = avatarColor,
                    modifier = Modifier.size(DonutTheme.dimens.iconSizeLarge),
                )
            }

            Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing12))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = poll.authorName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = poll.authorSubtitle,
                    fontSize = DonutTextSize.caption,
                    color = colors.onSurfaceVariant,
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(DonutRadius.md))
                    .background(accentColor.copy(alpha = 0.1f))
                    .padding(horizontal = DonutTheme.dimens.spacing8, vertical = DonutTheme.dimens.spacing4),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing4),
                ) {
                    Icon(
                        imageVector = Icons.Default.Poll,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(DonutTheme.dimens.iconSizeMedium),
                    )
                    Text(
                        text = "Poll",
                        fontSize = DonutTextSize.overline,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor,
                    )
                }
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = colors.onSurfaceVariant,
                )
            }
        }

        Text(
            text = poll.question,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurface,
            modifier = Modifier.padding(horizontal = DonutTheme.dimens.spacing16),
        )

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing12))

        Column(
            modifier = Modifier
                .padding(horizontal = DonutTheme.dimens.spacing16)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
        ) {
            poll.options.forEachIndexed { index, option ->
                val isSelected = poll.votedIndex == index
                val fraction = if (hasVoted && poll.totalVotes > 0) {
                    option.voteCount.toFloat() / poll.totalVotes
                } else 0f
                val animatedFraction by animateFloatAsState(
                    targetValue = fraction,
                    animationSpec = tween(durationMillis = 400),
                )
                val percentage = (fraction * 100).toInt()

                if (hasVoted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(DonutTheme.dimens.pollBarHeight)
                            .clip(RoundedCornerShape(DonutRadius.xl))
                            .background(colors.background)
                            .border(
                                width = if (isSelected) DonutTheme.dimens.borderMedium else DonutTheme.dimens.borderThin,
                                color = if (isSelected) accentColor.copy(alpha = 0.6f)
                                else colors.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(DonutRadius.xl),
                            ),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedFraction)
                                .height(DonutTheme.dimens.pollBarHeight)
                                .background(
                                    if (isSelected) accentColor.copy(alpha = 0.15f)
                                    else colors.onSurface.copy(alpha = 0.05f),
                                ),
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(DonutTheme.dimens.pollBarHeight)
                                .padding(horizontal = DonutTheme.dimens.spacing12),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(DonutTheme.dimens.iconSizeNormal),
                                )
                                Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing6))
                            }
                            Text(
                                text = option.text,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) accentColor else colors.onSurface,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "$percentage%",
                                fontSize = DonutTextSize.button,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) accentColor else colors.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(DonutTheme.dimens.pollBarHeight)
                            .clip(RoundedCornerShape(DonutRadius.xl))
                            .border(
                                DonutTheme.dimens.borderThin,
                                colors.outline.copy(alpha = 0.3f),
                                RoundedCornerShape(DonutRadius.xl),
                            )
                            .clickable { onEvent(HomeEvent.PollVoted(poll.id, index)) }
                            .padding(horizontal = DonutTheme.dimens.spacing12),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = option.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurface,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing12))
        Text(
            text = "${poll.totalVotes} votes",
            fontSize = DonutTextSize.small,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing8),
        )
    }
}
