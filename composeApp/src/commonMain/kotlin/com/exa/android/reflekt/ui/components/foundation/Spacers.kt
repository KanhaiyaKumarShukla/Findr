package com.exa.android.reflekt.ui.components.foundation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import com.exa.android.reflekt.ui.theme.DonutTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable fun HeightSpacer(height: Dp) = Spacer(Modifier.height(height))

@Composable fun WidthSpacer(width: Dp) = Spacer(Modifier.width(width))

@Composable fun SmallSpacer()  = HeightSpacer(DonutTheme.dimens.spacing4)
@Composable fun MediumSpacer() = HeightSpacer(DonutTheme.dimens.spacing8)
@Composable fun LargeSpacer()  = HeightSpacer(DonutTheme.dimens.spacing16)
@Composable fun XLargeSpacer() = HeightSpacer(DonutTheme.dimens.spacing24)
@Composable fun XXLargeSpacer() = HeightSpacer(DonutTheme.dimens.spacing32)
