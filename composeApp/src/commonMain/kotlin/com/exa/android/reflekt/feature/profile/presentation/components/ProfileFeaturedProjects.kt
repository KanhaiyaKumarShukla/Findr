package com.exa.android.reflekt.feature.profile.presentation.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.feature.profile.presentation.FeaturedProject
import com.exa.android.reflekt.feature.profile.presentation.ProfileEvent
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun ProfileFeaturedProjects(
    projects: List<FeaturedProject>,
    onEvent: (ProfileEvent) -> Unit,
) {
    val dimens = DonutTheme.dimens

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.spacing20),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Featured Projects",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "See All",
                fontSize = DonutTextSize.body,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(dimens.spacing12))

        LazyRow(
            contentPadding = PaddingValues(horizontal = dimens.spacing20),
            horizontalArrangement = Arrangement.spacedBy(dimens.spacing16),
        ) {
            items(projects, key = { it.id }) { project ->
                ProjectCard(project = project, onClick = { onEvent(ProfileEvent.ProjectClicked(project.id)) })
            }
            item {
                AddProjectCard(onClick = { onEvent(ProfileEvent.AddProjectClicked) })
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: FeaturedProject,
    onClick: () -> Unit,
) {
    val dimens = DonutTheme.dimens
    val shape = RoundedCornerShape(DonutRadius.card)
    val accentColor = Color(project.accentColorArgb.toInt() or (0xFF shl 24))

    Column(
        modifier = Modifier
            .width(260.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(dimens.borderThin, DonutTheme.colorTokens.cardBorder, shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
    ) {
        // Image placeholder with category badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(dimens.spacing8)
                    .clip(RoundedCornerShape(DonutRadius.sm))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = dimens.spacing8, vertical = dimens.spacing4),
            ) {
                Text(
                    text = project.category,
                    fontSize = DonutTextSize.caption,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                )
            }
        }

        // Content
        Column(modifier = Modifier.padding(dimens.spacing16)) {
            Text(
                text = project.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(dimens.spacing4))
            Text(
                text = project.description,
                fontSize = DonutTextSize.small,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(dimens.spacing12))

            // Members info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person, "Members",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(dimens.iconSizeMedium),
                )
                Spacer(modifier = Modifier.width(dimens.spacing4))
                Text(
                    text = if (project.isSolo) "Solo Project"
                           else "+${project.memberCount - 1} others",
                    fontSize = DonutTextSize.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AddProjectCard(onClick: () -> Unit) {
    val dimens = DonutTheme.dimens
    val shape = RoundedCornerShape(DonutRadius.card)

    Column(
        modifier = Modifier
            .width(120.dp)
            .height(240.dp)
            .border(
                dimens.borderThick,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape,
            )
            .clip(shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Default.AddCircleOutline, "Add Project",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(dimens.iconSize2Xl),
        )
        Spacer(modifier = Modifier.height(dimens.spacing8))
        Text(
            text = "Add Project",
            fontSize = DonutTextSize.small,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
