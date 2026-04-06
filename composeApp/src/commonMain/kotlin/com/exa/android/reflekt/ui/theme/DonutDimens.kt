package com.exa.android.reflekt.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class DonutDimens(
    // Spacing
    val spacing0: Dp,
    val spacing1: Dp,
    val spacing2: Dp,
    val spacing4: Dp,
    val spacing5: Dp,
    val spacing6: Dp,
    val spacing8: Dp,
    val spacing10: Dp,
    val spacing12: Dp,
    val spacing14: Dp,
    val spacing16: Dp,
    val spacing18: Dp,
    val spacing20: Dp,
    val spacing24: Dp,
    val spacing28: Dp,
    val spacing32: Dp,
    val spacing36: Dp,
    val spacing40: Dp,
    val spacing44: Dp,
    val spacing48: Dp,
    val spacing52: Dp,
    val spacing56: Dp,
    val spacing64: Dp,
    val spacing72: Dp,
    val spacing80: Dp,
    val spacing100: Dp,
    val spacing120: Dp,

    // Icon sizes
    val iconSizeExtraSmall: Dp,
    val iconSizeSmall: Dp,
    val iconSizeMedium: Dp,
    val iconSizeNormal: Dp,
    val iconSizeBase: Dp,
    val iconSizeLarge: Dp,
    val iconSizeExtraLarge: Dp,
    val iconSize2Xl: Dp,
    val iconSize2X: Dp,
    val iconSize3X: Dp,

    // Avatar scales
    val avatarSizeSmall: Dp,
    val avatarSizeMedium: Dp,
    val avatarSizeBase: Dp,
    val avatarSizeLarge: Dp,

    // Borders & dividers
    val dividerThickness: Dp,
    val borderThin: Dp,
    val borderMedium: Dp,
    val borderThick: Dp,
    val borderAccent: Dp,

    // Component heights
    val progressBarHeight: Dp,
    val pollBarHeight: Dp,
    val tickerHeight: Dp,
    val tabIndicatorSize: Dp,
    val fabSize: Dp,

    // Fixed layout sizes
    val heroHeight: Dp,
    val bottomBarHeight: Dp,
    val navBarMinSize: Dp,
    val cardImageHeight: Dp,
    val liveCardWidth: Dp,
    val liveCardHeight: Dp,
    val liveCardIconSize: Dp,
    val liveGradientOverlayHeight: Dp,
    val shimmerHeight: Dp,
    val filterChipHeight: Dp,

    // Misc component sizes
    val buttonLoadingIndicatorSize: Dp,
    val collapsedAppBarHeight: Dp,
    val alertDialogMinHeight: Dp,
    val animatedTagBoxSize: Dp,

    // Elevations
    val elevationNone: Dp,
    val elevationLow: Dp,
    val elevationMedium: Dp,
    val elevationHigh: Dp,
    val elevationCard: Dp,
    val elevationPressed: Dp,
    val elevationFab: Dp,
)

fun donutDefaultDimens(): DonutDimens = DonutDimens(
    spacing0 = 0.dp,
    spacing1 = 1.dp,
    spacing2 = 2.dp,
    spacing4 = 4.dp,
    spacing5 = 5.dp,
    spacing6 = 6.dp,
    spacing8 = 8.dp,
    spacing10 = 10.dp,
    spacing12 = 12.dp,
    spacing14 = 14.dp,
    spacing16 = 16.dp,
    spacing18 = 18.dp,
    spacing20 = 20.dp,
    spacing24 = 24.dp,
    spacing28 = 28.dp,
    spacing32 = 32.dp,
    spacing36 = 36.dp,
    spacing40 = 40.dp,
    spacing44 = 44.dp,
    spacing48 = 48.dp,
    spacing52 = 52.dp,
    spacing56 = 56.dp,
    spacing64 = 64.dp,
    spacing72 = 72.dp,
    spacing80 = 80.dp,
    spacing100 = 100.dp,
    spacing120 = 120.dp,

    iconSizeExtraSmall = 6.dp,
    iconSizeSmall = 8.dp,
    iconSizeMedium = 14.dp,
    iconSizeNormal = 16.dp,
    iconSizeBase = 18.dp,
    iconSizeLarge = 20.dp,
    iconSizeExtraLarge = 22.dp,
    iconSize2Xl = 24.dp,
    iconSize2X = 40.dp,
    iconSize3X = 48.dp,

    avatarSizeSmall = 28.dp,
    avatarSizeMedium = 36.dp,
    avatarSizeBase = 40.dp,
    avatarSizeLarge = 56.dp,

    dividerThickness = 1.dp,
    borderThin = 1.dp,
    borderMedium = 1.5.dp,
    borderThick = 2.dp,
    borderAccent = 4.dp,

    progressBarHeight = 10.dp,
    pollBarHeight = 44.dp,
    tickerHeight = 16.dp,
    tabIndicatorSize = 2.dp,
    fabSize = 56.dp,

    heroHeight = 288.dp,
    bottomBarHeight = 52.dp,
    navBarMinSize = 40.dp,
    cardImageHeight = 160.dp,
    liveCardWidth = 220.dp,
    liveCardHeight = 176.dp,
    liveCardIconSize = 64.dp,
    liveGradientOverlayHeight = 100.dp,
    shimmerHeight = 80.dp,
    filterChipHeight = 36.dp,

    buttonLoadingIndicatorSize = 16.dp,
    collapsedAppBarHeight = 56.dp,
    alertDialogMinHeight = 128.dp,
    animatedTagBoxSize = 20.dp,

    elevationNone = 0.dp,
    elevationLow = 2.dp,
    elevationMedium = 4.dp,
    elevationHigh = 6.dp,
    elevationCard = 8.dp,
    elevationPressed = 2.dp,
    elevationFab = 8.dp,
)

internal val LocalDonutDimens = staticCompositionLocalOf { donutDefaultDimens() }

// ═══════════════════════════════════════════════════════════════════════════════
//  Static token objects (Donut* style)
//  Shapes and Text sizes are retained as static objects per instructions
// ═══════════════════════════════════════════════════════════════════════════════

/** Corner radius tokens — used with RoundedCornerShape. */
object DonutRadius {
    val xs = 2.dp
    val sm = 4.dp
    val md = 6.dp
    val lg = 8.dp
    val xl = 10.dp
    val card = 12.dp
    val cardLg = 14.dp
    val panel = 16.dp
    val dialog = 20.dp
    val pill = 50     // use RoundedCornerShape(DonutRadius.pill)
}

/** Font size tokens — sp values. */
object DonutTextSize {
    val micro = 8.sp
    val caption = 10.sp
    val overline = 11.sp
    val small = 12.sp
    val button = 13.sp
    val body = 14.sp
    val bodyLarge = 16.sp
    val title = 18.sp
    val display = 28.sp
    val hero = 30.sp
}

/** Line height tokens paired with DonutTextSize. */
object DonutLineHeight {
    val xs = 12.sp
    val small = 16.sp
    val body = 20.sp
    val bodyLoose = 22.sp
    val bodyLarge = 24.sp
    val title = 28.sp
    val titleLg = 30.sp
    val headlineMd = 32.sp
    val headlineLg = 36.sp
    val headlineXl = 38.sp
}

