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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exa.android.reflekt.feature.home.presentation.FilterChip
import com.exa.android.reflekt.feature.home.presentation.HomeEvent

/**
 * Horizontal scrollable row of filter chips.
 * Note: We use a custom chip here (not FindrFilterChip) because this chip has a unique
 * "pill with box shadow" style specific to the Home feed, not the generic filter chip style.
 * If the design system unifies these, migrate to [FindrFilterChip].
 */
@Composable
internal fun FilterChipsRow(
    chips: List<FilterChip>,
    onEvent: (HomeEvent) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 20.dp),
    ) {
        itemsIndexed(chips) { index, chip ->
            val interactionSource = remember { MutableInteractionSource() }
            Text(
                text = chip.label,
                fontSize = 14.sp,
                fontWeight = if (chip.isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (chip.isSelected) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .then(
                        if (chip.isSelected) {
                            Modifier
                                .shadow(8.dp, RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier.background(MaterialTheme.colorScheme.surface)
                        },
                    )
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource,
                    ) { onEvent(HomeEvent.FilterSelected(index)) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
}
