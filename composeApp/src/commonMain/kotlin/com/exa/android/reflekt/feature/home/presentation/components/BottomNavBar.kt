package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.exa.android.reflekt.feature.home.presentation.BottomNavItem
import com.exa.android.reflekt.feature.home.presentation.HomeEvent
import com.exa.android.reflekt.ui.theme.DonutTheme
import com.exa.android.reflekt.ui.theme.DonutTextSize

private fun String.toNavIcon(): ImageVector = when (this) {
    "home"    -> Icons.Default.Home
    "explore" -> Icons.Default.Explore
    "add"     -> Icons.Default.Add
    "chat"    -> Icons.Outlined.ChatBubbleOutline
    "person"  -> Icons.Default.Person
    else      -> Icons.Default.Home
}

@Composable
internal fun BottomNavBar(
    items: List<BottomNavItem>,
    onEvent: (HomeEvent) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.navigationBars),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DonutTheme.dimens.dividerThickness)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .align(Alignment.TopCenter),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = DonutTheme.dimens.spacing8, vertical = DonutTheme.dimens.spacing8),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom,
        ) {
            items.forEachIndexed { index, item ->
                if (item.isFab) {
                    Box(
                        modifier = Modifier
                            .offset(y = -DonutTheme.dimens.spacing16)
                            .size(DonutTheme.dimens.fabSize)
                            .shadow(DonutTheme.dimens.elevationFab + DonutTheme.dimens.elevationMedium, CircleShape, spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) { onEvent(HomeEvent.BottomNavClicked(index)) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = item.iconKey.toNavIcon(),
                            contentDescription = item.label,
                            tint = Color.White,
                            modifier = Modifier.size(DonutTheme.dimens.spacing28),
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) { onEvent(HomeEvent.BottomNavClicked(index)) }
                            .padding(horizontal = DonutTheme.dimens.spacing12, vertical = DonutTheme.dimens.spacing4),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = item.iconKey.toNavIcon(),
                            contentDescription = item.label,
                            tint = if (item.isSelected) MaterialTheme.colorScheme.primary
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(DonutTheme.dimens.iconSize2Xl),
                        )
                        Spacer(modifier = Modifier.height(DonutTheme.dimens.spacing4))
                        Text(
                            text = item.label.uppercase(),
                            fontSize = DonutTextSize.caption,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = TextUnit(0.5f, TextUnitType.Sp),
                            color = if (item.isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
