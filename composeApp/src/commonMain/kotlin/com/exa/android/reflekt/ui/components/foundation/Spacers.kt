package com.exa.android.reflekt.ui.components.foundation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable fun HeightSpacer(height: Dp) = Spacer(Modifier.height(height))

@Composable fun WidthSpacer(width: Dp) = Spacer(Modifier.width(width))

@Composable fun SmallSpacer()  = HeightSpacer(4.dp)
@Composable fun MediumSpacer() = HeightSpacer(8.dp)
@Composable fun LargeSpacer()  = HeightSpacer(16.dp)
@Composable fun XLargeSpacer() = HeightSpacer(24.dp)
@Composable fun XXLargeSpacer() = HeightSpacer(32.dp)
