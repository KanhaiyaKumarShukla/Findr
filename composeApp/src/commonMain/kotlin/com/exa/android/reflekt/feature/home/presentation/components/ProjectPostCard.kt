package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.feature.home.presentation.ProjectPost
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutLineHeight

@Composable
internal fun ProjectPostCard(
    project: ProjectPost,
    onEvent: (HomeEvent) -> Unit,
    onCardClick: () -> Unit = {},
) {
    val colors = DonutTheme.colorTokens
    val avatarColor = project.avatarColorArgb.toColor()
    val categoryColor = colorFromKey(project.categoryColorKey)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing4)
            .clip(RoundedCornerShape(DonutRadius.panel))
            .background(colors.surface)
            .border(DonutTheme.dimens.borderThin, colors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(DonutRadius.panel))
            .clickable(onClick = onCardClick)
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
                        text = project.authorName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(DonutRadius.md))
                            .background(categoryColor.copy(alpha = 0.1f))
                            .padding(horizontal = DonutTheme.dimens.spacing8, vertical = DonutTheme.dimens.spacing2),
                    ) {
                        Text(
                            text = project.categoryLabel,
                            fontSize = DonutTextSize.caption,
                            fontWeight = FontWeight.SemiBold,
                            color = categoryColor,
                        )
                    }
                }
                Text(
                    text = project.authorSubtitle,
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
            text = project.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground,
        )

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing8))

        Text(
            text = project.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = DonutLineHeight.body,
        )

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing14))

        if (project.tags.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8)) {
                project.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(DonutRadius.md))
                            .border(DonutTheme.dimens.borderThin, colors.outline.copy(alpha = 0.5f), RoundedCornerShape(DonutRadius.md))
                            .padding(horizontal = DonutTheme.dimens.spacing10, vertical = DonutTheme.dimens.spacing5),
                    ) {
                        Text(
                            text = "#$tag",
                            fontSize = DonutTextSize.small,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurfaceVariant,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing14))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (project.location != null) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(DonutTheme.dimens.iconSizeMedium),
                )
                Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing4))
                Text(
                    text = project.location,
                    fontSize = DonutTextSize.small,
                    color = colors.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onEvent(HomeEvent.ProjectEnrollClicked(project.id)) },
                shape = RoundedCornerShape(DonutRadius.xl),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                contentPadding = PaddingValues(horizontal = DonutTheme.dimens.spacing20, vertical = DonutTheme.dimens.spacing8),
            ) {
                Text(text = "Enroll", fontWeight = FontWeight.Bold, fontSize = DonutTextSize.button)
            }
        }
    }
}
