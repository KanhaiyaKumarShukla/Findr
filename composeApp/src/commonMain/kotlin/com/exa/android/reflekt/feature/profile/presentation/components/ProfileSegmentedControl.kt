package com.exa.android.reflekt.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.profile.presentation.ProfileEvent
import com.exa.android.reflekt.feature.profile.presentation.ProfileTab
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

private val tabs = listOf("Profile", "Activity", "Resume")

@Composable
internal fun ProfileSegmentedControl(
    selectedTab: ProfileTab,
    onEvent: (ProfileEvent) -> Unit,
) {
    val dimens = DonutTheme.dimens
    val outerShape = RoundedCornerShape(DonutRadius.lg)
    val innerShape = RoundedCornerShape(DonutRadius.md)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = dimens.spacing16, vertical = dimens.spacing8),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(outerShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(dimens.spacing4),
            horizontalArrangement = Arrangement.spacedBy(dimens.spacing4),
        ) {
            tabs.forEachIndexed { index, label ->
                val isSelected = index == selectedTab.ordinal
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(innerShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { onEvent(ProfileEvent.TabSelected(index)) }
                        .padding(vertical = dimens.spacing8),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        fontSize = DonutTextSize.body,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color.White
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
