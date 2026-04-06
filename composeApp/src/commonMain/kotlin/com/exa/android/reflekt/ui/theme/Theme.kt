package com.exa.android.reflekt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = InputBackgroundDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = InputBorderDark,
    outlineVariant = DividerDark,
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = InputBackgroundLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = InputBorderLight,
    outlineVariant = DividerLight,
)

object DonutTheme{

val colorTokens: DonutColorTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalDonutColorTokens.current

val staticColorTokens: DonutColorTokens
    @Composable
    @ReadOnlyComposable
    get() = LocalStaticDonutColorTokens.current

//val typography: DonutTypography
//    @Composable
//    @ReadOnlyComposable
//    get() = DonutTypography

val shapes: DonutShapes
    @Composable
    @ReadOnlyComposable
    get() = DonutShapes

val dimens: DonutDimens
    @Composable
    @ReadOnlyComposable
    get() = LocalDonutDimens.current
}

@Composable
fun DonutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val colorTokens = if (darkTheme) donutDarkColorTokens() else donutLightColorTokens()

    CompositionLocalProvider(
        LocalDonutColorTokens provides colorTokens,
        LocalDonutDimens provides donutDefaultDimens(),
        ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
