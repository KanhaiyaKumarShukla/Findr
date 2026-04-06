package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.exa.android.reflekt.feature.home.presentation.NewsTicker
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutTextSize

// Auto-scrolling marquee of campus buzz items.
@Composable
internal fun TrendingNewsTicker(items: List<NewsTicker>) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        while (true) {
            scrollState.animateScrollTo(
                scrollState.maxValue,
                animationSpec = tween(durationMillis = 15000, easing = LinearEasing),
            )
            scrollState.scrollTo(0)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = DonutTheme.dimens.spacing20)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(vertical = DonutTheme.dimens.spacing10),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "CAMPUS BUZZ",
            fontSize = DonutTextSize.caption,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = androidx.compose.ui.unit.TextUnit(0.5f, androidx.compose.ui.unit.TextUnitType.Sp),
            modifier = Modifier.padding(start = DonutTheme.dimens.spacing16, end = DonutTheme.dimens.spacing12),
        )

        Box(
            modifier = Modifier
                .width(DonutTheme.dimens.dividerThickness)
                .height(DonutTheme.dimens.tickerHeight)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(scrollState)
                .padding(horizontal = DonutTheme.dimens.spacing12),
            horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing24),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(DonutTheme.dimens.spacing6),
                ) {
                    Text(
                        text = "#",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = DonutTextSize.body,
                    )
                    Text(
                        text = item.text,
                        fontSize = DonutTextSize.body,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
