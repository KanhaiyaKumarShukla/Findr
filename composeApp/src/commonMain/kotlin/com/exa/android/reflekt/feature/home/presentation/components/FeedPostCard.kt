package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.home.presentation.FeedImageIconType
import com.exa.android.reflekt.feature.home.presentation.FeedPost
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutLineHeight



private fun FeedImageIconType.toImageVector(): ImageVector? = when (this) {
    FeedImageIconType.CODE          -> Icons.Default.Code
    FeedImageIconType.AWARD         -> Icons.AutoMirrored.Filled.MenuBook
    FeedImageIconType.BUG_REPORT    -> Icons.Default.Code
    FeedImageIconType.ANNOUNCEMENT  -> Icons.AutoMirrored.Filled.MenuBook
    FeedImageIconType.NONE          -> null
}

@Composable
private fun feedImageGradients() = mapOf(
    FeedImageIconType.CODE         to listOf(DonutTheme.colorTokens.surfaceVariant, DonutTheme.staticColorTokens.accentBlue),
    FeedImageIconType.AWARD        to listOf(DonutTheme.colorTokens.surfaceVariant, DonutTheme.staticColorTokens.accentGreen),
    FeedImageIconType.BUG_REPORT   to listOf(DonutTheme.colorTokens.surfaceVariant, DonutTheme.staticColorTokens.accentIndigo),
    FeedImageIconType.ANNOUNCEMENT to listOf(DonutTheme.colorTokens.surfaceVariant, DonutTheme.staticColorTokens.accentOrange),
    FeedImageIconType.NONE         to emptyList(),
)

@Composable
internal fun FeedPostCard(
    post: FeedPost,
    onEvent: (HomeEvent) -> Unit,
) {
    val colors = DonutTheme.colorTokens
    val avatarColor = post.avatarColorArgb.toColor()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing4)
            .clip(RoundedCornerShape(DonutRadius.panel))
            .background(colors.surface)
            .border(DonutTheme.dimens.borderThin, colors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(DonutRadius.panel)),
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
                    text = post.authorName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = post.authorSubtitle,
                    fontSize = DonutTextSize.caption,
                    color = colors.onSurfaceVariant,
                )
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onBackground,
            lineHeight = DonutLineHeight.bodyLoose,
            modifier = Modifier.padding(horizontal = DonutTheme.dimens.spacing16),
        )

        if (post.hasImage && post.imageIconType != FeedImageIconType.NONE) {
            val gradientColors = feedImageGradients()[post.imageIconType] ?: emptyList()
            val icon = post.imageIconType.toImageVector()
            if (gradientColors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing12))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Brush.linearGradient(gradientColors)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(DonutTheme.dimens.fabSize),
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DonutTheme.dimens.spacing12, vertical = DonutTheme.dimens.spacing8),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(DonutRadius.lg))
                    .clickable { onEvent(HomeEvent.LikeToggled(post.id)) }
                    .padding(horizontal = DonutTheme.dimens.spacing8, vertical = DonutTheme.dimens.spacing6),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing4),
            ) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.isLiked) DonutTheme.colorTokens.error else colors.onSurfaceVariant,
                    modifier = Modifier.size(DonutTheme.dimens.iconSizeLarge),
                )
                Text(
                    text = "${post.likeCount}",
                    fontSize = DonutTextSize.small,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing8))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(DonutRadius.lg))
                    .clickable { }
                    .padding(horizontal = DonutTheme.dimens.spacing8, vertical = DonutTheme.dimens.spacing6),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing4),
            ) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Comment",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(DonutTheme.dimens.iconSizeLarge),
                )
                Text(
                    text = "${post.commentCount}",
                    fontSize = DonutTextSize.small,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { }, modifier = Modifier.size(DonutTheme.dimens.avatarSizeMedium)) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(DonutTheme.dimens.iconSizeLarge),
                )
            }
        }
    }
}
