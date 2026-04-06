package com.exa.android.reflekt.feature.home.presentation.components

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.home.presentation.BugPost
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutLineHeight



@Composable
internal fun BugPostCard(
    bug: BugPost,
    onEvent: (HomeEvent) -> Unit,
) {
    val colors = DonutTheme.colorTokens
    val avatarColor = bug.avatarColorArgb.toColor()
    val collaboratorColors = bug.collaboratorColorArgbs.map { it.toColor() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing4)
            .clip(RoundedCornerShape(DonutRadius.panel))
            .background(colors.surface)
            .border(DonutTheme.dimens.borderThin, DonutTheme.colorTokens.error.copy(alpha = 0.25f), RoundedCornerShape(DonutRadius.panel))
            .padding(DonutTheme.dimens.spacing16),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(DonutTheme.dimens.avatarSizeBase)
                    .clip(CircleShape)
                    .background(avatarColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = avatarColor,
                    modifier = Modifier.size(DonutTheme.dimens.iconSizeExtraLarge),
                )
            }

            Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing12))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
                ) {
                    Text(
                        text = bug.authorName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(DonutRadius.md))
                            .background(DonutTheme.colorTokens.error.copy(alpha = 0.1f))
                            .padding(horizontal = DonutTheme.dimens.spacing8, vertical = DonutTheme.dimens.spacing2),
                    ) {
                        Text(
                            text = bug.categoryLabel,
                            fontSize = DonutTextSize.caption,
                            fontWeight = FontWeight.SemiBold,
                            color = DonutTheme.colorTokens.error,
                        )
                    }
                }
                Text(
                    text = bug.authorSubtitle,
                    fontSize = DonutTextSize.overline,
                    color = colors.onSurfaceVariant,
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = colors.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing14))

        Text(
            text = bug.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing10))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(DonutRadius.xl))
                .background(colors.background)
                .border(DonutTheme.dimens.borderThin, DonutTheme.colorTokens.error.copy(alpha = 0.2f), RoundedCornerShape(DonutRadius.xl))
                .padding(DonutTheme.dimens.spacing12),
        ) {
            Text(
                text = bug.errorSnippet,
                fontSize = DonutTextSize.small,
                color = colors.onSurfaceVariant,
                lineHeight = DonutLineHeight.bodyLoose,
                fontFamily = FontFamily.Monospace,
            )
        }

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing14))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (collaboratorColors.isNotEmpty()) {
                Box(modifier = Modifier.height(DonutTheme.dimens.avatarSizeSmall)) {
                    collaboratorColors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .offset(x = (index * DonutTheme.dimens.spacing18.value).toInt().let { DonutTheme.dimens.spacing18 })
                                .size(DonutTheme.dimens.avatarSizeSmall)
                                .clip(CircleShape)
                                .border(DonutTheme.dimens.borderThick, colors.surface, CircleShape)
                                .background(color.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(DonutTheme.dimens.iconSizeMedium),
                            )
                        }
                    }
                    if (bug.collaboratorCount > 0) {
                        Box(
                            modifier = Modifier
                                .offset(x = (collaboratorColors.size * DonutTheme.dimens.spacing18.value).toInt().let { DonutTheme.dimens.spacing18 })
                                .size(DonutTheme.dimens.avatarSizeSmall)
                                .clip(CircleShape)
                                .border(DonutTheme.dimens.borderThick, colors.surface, CircleShape)
                                .background(colors.cardBackground),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "+${bug.collaboratorCount}",
                                fontSize = DonutTextSize.caption,
                                fontWeight = FontWeight.Bold,
                                color = colors.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(DonutRadius.xl))
                    .border(DonutTheme.dimens.borderThin, colors.primary.copy(alpha = 0.4f), RoundedCornerShape(DonutRadius.xl))
                    .clickable { onEvent(HomeEvent.BugCollaborateClicked(bug.id)) }
                    .padding(horizontal = DonutTheme.dimens.spacing14, vertical = DonutTheme.dimens.spacing8),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing6),
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(DonutTheme.dimens.iconSizeNormal),
                )
                Text(
                    text = "Collaborate",
                    fontSize = DonutTextSize.button,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.primary,
                )
            }
        }
    }
}
