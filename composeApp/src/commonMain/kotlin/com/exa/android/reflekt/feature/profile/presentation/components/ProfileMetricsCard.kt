package com.exa.android.reflekt.feature.profile.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.profile.presentation.ProfileUiState
import com.exa.android.reflekt.ui.theme.DonutRadius
import com.exa.android.reflekt.ui.theme.DonutTextSize
import com.exa.android.reflekt.ui.theme.DonutTheme

@Composable
internal fun ProfileMetricsCard(state: ProfileUiState) {
    val dimens = DonutTheme.dimens
    val shape = RoundedCornerShape(DonutRadius.panel)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.spacing16)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(dimens.borderThin, DonutTheme.colorTokens.cardBorder, shape)
            .padding(dimens.spacing16),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetricItem(value = state.connections.toString(), label = "CONNECTIONS", modifier = Modifier.weight(1f))
        VerticalDivider()
        MetricItem(value = state.projectCount.toString(), label = "PROJECTS", modifier = Modifier.weight(1f))
        VerticalDivider()
        MetricItem(value = state.rating.toString(), label = "RATING", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MetricItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = label,
            fontSize = DonutTextSize.micro,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = DonutTextSize.micro,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

@Composable
private fun VerticalDivider() {
    val dimens = DonutTheme.dimens
    Box(
        modifier = Modifier
            .width(dimens.borderThin)
            .height(dimens.spacing40)
            .background(DonutTheme.colorTokens.cardBorder),
    )
}
