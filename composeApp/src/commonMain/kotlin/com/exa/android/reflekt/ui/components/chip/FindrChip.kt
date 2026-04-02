package com.exa.android.reflekt.ui.components.chip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.ui.theme.Primary

// ─────────────────────────────────────────────────────────────
//  Filter chip  (toggleable, selection-aware)
// ─────────────────────────────────────────────────────────────

/**
 * A toggleable chip used for filter/category selection.
 *
 * @param label     Chip label.
 * @param selected  Whether this chip is currently selected.
 * @param onToggle  Invoked when the chip is tapped.
 */
@Composable
fun FindrFilterChip(
    label: String,
    selected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onToggle,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        },
        leadingIcon = leadingIcon,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Primary.copy(alpha = 0.15f),
            selectedLabelColor = Primary,
            selectedLeadingIconColor = Primary,
        ),
        border = if (selected) {
            BorderStroke(width = 1.5.dp, color = Primary)
        } else {
            BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outline)
        },
        modifier = modifier,
    )
}

// ─────────────────────────────────────────────────────────────
//  Input chip  (dismissible tag, e.g. selected skill/interest)
// ─────────────────────────────────────────────────────────────

/**
 * An input chip that can be dismissed (removed from a list).
 *
 * @param label     Tag label.
 * @param onRemove  Called when the trailing × is tapped. Pass null to hide × icon.
 */
@Composable
fun FindrInputChip(
    label: String,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
) {
    InputChip(
        selected = true,
        onClick = { onRemove?.invoke() },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
            )
        },
        trailingIcon = if (onRemove != null) {
            {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove $label",
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        } else null,
        colors = InputChipDefaults.inputChipColors(
            selectedContainerColor = Primary.copy(alpha = 0.12f),
            selectedLabelColor = Primary,
        ),
        border = BorderStroke(width = 1.dp, color = Primary.copy(alpha = 0.5f)),
        modifier = modifier,
    )
}

// ─────────────────────────────────────────────────────────────
//  Suggestion chip  (read-only, tap-to-action)
// ─────────────────────────────────────────────────────────────

/**
 * A non-toggleable chip for suggestions or quick-action labels.
 */
@Composable
fun FindrSuggestionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
) {
    SuggestionChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        icon = icon,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = modifier,
    )
}

// ─────────────────────────────────────────────────────────────
//  Chip group  (wrapping row of filter chips)
// ─────────────────────────────────────────────────────────────

/**
 * A wrapping row of [FindrFilterChip]s driven by a list of items.
 *
 * @param items         All available chip labels.
 * @param selectedItems Currently selected labels.
 * @param onToggle      Called with the label that was tapped.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FindrChipGroup(
    items: List<String>,
    selectedItems: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            FindrFilterChip(
                label = item,
                selected = item in selectedItems,
                onToggle = { onToggle(item) },
            )
        }
    }
}
