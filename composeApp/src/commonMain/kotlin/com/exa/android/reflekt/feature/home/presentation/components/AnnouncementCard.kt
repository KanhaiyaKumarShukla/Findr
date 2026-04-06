package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.home.presentation.AnnouncementPost
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutLineHeight

@Composable
internal fun AnnouncementCard(
    announcement: AnnouncementPost,
    onEvent: (HomeEvent) -> Unit,
) {
    val colors = DonutTheme.colorTokens
    val accentColor = colorFromKey(announcement.accentColorKey)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing4)
            .clip(RoundedCornerShape(DonutRadius.panel))
            .background(colors.surface)
            .border(DonutTheme.dimens.borderThin, colors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(DonutRadius.panel)),
    ) {
        Row(
            modifier = Modifier.padding(DonutTheme.dimens.spacing16),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(DonutTheme.dimens.avatarSizeMedium)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(DonutTheme.dimens.iconSizeBase),
                )
            }

            Spacer(modifier = Modifier.width(DonutTheme.dimens.spacing12))

            Column {
                Text(
                    text = announcement.source,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = announcement.sourceSubtitle,
                    fontSize = DonutTextSize.caption,
                    color = colors.onSurfaceVariant,
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DonutTheme.dimens.spacing16)
                .clip(RoundedCornerShape(DonutRadius.card))
                .background(accentColor.copy(alpha = 0.05f))
                .border(DonutTheme.dimens.borderThin, accentColor.copy(alpha = 0.1f), RoundedCornerShape(DonutRadius.card))
                .padding(DonutTheme.dimens.spacing14),
        ) {
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
            )
            Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing6))
            Text(
                text = announcement.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant,
                lineHeight = DonutLineHeight.bodyLoose,
            )
            Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing12))
            Button(
                onClick = { onEvent(HomeEvent.AnnouncementCtaClicked(announcement.id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DonutTheme.dimens.navBarMinSize),
                shape = RoundedCornerShape(DonutRadius.xl),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor.copy(alpha = 0.12f),
                    contentColor = accentColor,
                ),
                elevation = ButtonDefaults.buttonElevation(DonutTheme.dimens.elevationNone),
            ) {
                Text(text = announcement.ctaLabel, fontWeight = FontWeight.Bold, fontSize = DonutTextSize.small)
            }
        }

        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing16))
    }
}
