package com.exa.android.reflekt.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColorScheme(
    // Error
    val error: Color,
    val onError: Color,
    // Cards (illustration stats, avatar bubbles)
    val cardBackground: Color,
    val cardBorder: Color,
    val cardText: Color,
    // Illustration area
    val illustrationBackground: Color,
    // Borders / Badges
    val badgeBorder: Color,
    val avatarBorder: Color,
    // Pagination
    val paginationDotInactive: Color,
    // Bar chart
    val barChartLow: Color,
    val barChartMid: Color,
    val barChartHigh: Color,
    val barChartLabel: Color,
    // Form card
    val formCardBackground: Color,
    // Accent colors (decorative — same in both themes)
    val accentBlue: Color,
    val accentIndigo: Color,
    val accentGreen: Color,
    val accentPurple: Color,
    val accentYellow: Color,
    val accentOrange: Color,
    val purpleGlow: Color,
    val googleBlue: Color,
    // Icon on accent / colored backgrounds
    val iconOnAccent: Color,
)

val DarkAppColors = AppColorScheme(
    error = ErrorRed,
    onError = Color.White,
    cardBackground = CardBackgroundDark,
    cardBorder = CardBorderDark,
    cardText = Color.White,
    illustrationBackground = IllustrationBgDark,
    badgeBorder = DeepDark,
    avatarBorder = Color.White.copy(alpha = 0.2f),
    paginationDotInactive = CardBorderDark,
    barChartLow = Slate600,
    barChartMid = Slate500,
    barChartHigh = Slate400,
    barChartLabel = Slate400,
    formCardBackground = SurfaceDark,
    accentBlue = AccentBlue,
    accentIndigo = AccentIndigo,
    accentGreen = AccentGreen,
    accentPurple = AccentPurple,
    accentYellow = AccentYellow,
    accentOrange = AccentOrange,
    purpleGlow = PurpleGlow,
    googleBlue = GoogleBlue,
    iconOnAccent = Color.White,
)

val LightAppColors = AppColorScheme(
    error = ErrorRed,
    onError = Color.White,
    cardBackground = CardBackgroundLight,
    cardBorder = CardBorderLight,
    cardText = OnBackgroundLight,
    illustrationBackground = IllustrationBgLight,
    badgeBorder = Color.White,
    avatarBorder = Color.Black.copy(alpha = 0.1f),
    paginationDotInactive = CardBorderLight,
    barChartLow = CardBorderLight,
    barChartMid = TextSecondaryLight,
    barChartHigh = OnBackgroundLight,
    barChartLabel = TextSecondaryLight,
    formCardBackground = SurfaceLight,
    accentBlue = AccentBlue,
    accentIndigo = AccentIndigo,
    accentGreen = AccentGreen,
    accentPurple = AccentPurple,
    accentYellow = AccentYellow,
    accentOrange = AccentOrange,
    purpleGlow = PurpleGlow,
    googleBlue = GoogleBlue,
    iconOnAccent = Color.White,
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

val MaterialTheme.appColors: AppColorScheme
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current
