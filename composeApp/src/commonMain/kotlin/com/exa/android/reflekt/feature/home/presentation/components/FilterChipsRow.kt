package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.home.presentation.FilterChip
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutTextSize

// Horizontal scrollable row of filter chips.
// Custom pill style with box-shadow specific to the Home feed.
@Composable
internal fun FilterChipsRow(
    chips: List<FilterChip>,
    onEvent: (HomeEvent) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = DonutTheme.dimens.spacing16),
        horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing8),
        modifier = Modifier.padding(vertical = DonutTheme.dimens.spacing20),
    ) {
        itemsIndexed(chips) { index, chip ->
            val interactionSource = remember { MutableInteractionSource() }
            val pillShape = RoundedCornerShape(DonutTheme.dimens.spacing20)
            Text(
                text = chip.label,
                fontSize = DonutTextSize.body,
                fontWeight = if (chip.isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (chip.isSelected) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(pillShape)
                    .then(
                        if (chip.isSelected) {
                            Modifier
                                .shadow(DonutTheme.dimens.elevationCard, pillShape)
                                .background(MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier.background(MaterialTheme.colorScheme.surface)
                        },
                    )
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                    ) { onEvent(HomeEvent.FilterSelected(index)) }
                    .padding(horizontal = DonutTheme.dimens.spacing16, vertical = DonutTheme.dimens.spacing10),
            )
        }
    }
}
