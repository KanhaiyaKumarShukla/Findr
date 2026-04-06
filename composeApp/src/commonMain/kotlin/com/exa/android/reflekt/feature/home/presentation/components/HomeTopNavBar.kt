package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutTextSize

private val AlertRed: Color @Composable get() = DonutTheme.colorTokens.error

@Composable
internal fun HomeTopNavBar(
    notificationCount: Int,
    onEvent: (HomeEvent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(DonutTheme.dimens.avatarSizeBase)
                .clip(CircleShape)
                .border(DonutTheme.dimens.borderThick, MaterialTheme.colorScheme.primary, CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onEvent(HomeEvent.ProfileClicked) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(DonutTheme.dimens.iconSizeExtraLarge),
            )
        }

        Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing12))

        Column {
            Text(
                text = "College Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "CAMPUS LIVE",
                fontSize = DonutTextSize.caption,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = androidx.compose.ui.unit.TextUnit(1.5f, androidx.compose.ui.unit.TextUnitType.Sp),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { onEvent(HomeEvent.SearchClicked) }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Box {
            IconButton(onClick = { onEvent(HomeEvent.NotificationClicked) }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-DonutTheme.dimens.spacing6), y = DonutTheme.dimens.spacing6)
                        .size(DonutTheme.dimens.iconSizeSmall)
                        .clip(CircleShape)
                        .background(AlertRed),
                )
            }
        }
    }
}
