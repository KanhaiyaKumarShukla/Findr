package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.exa.android.reflekt.ui.theme.DonutTheme

// ─── Colour mappers ───────────────────────────────────────────────────────────

/** Maps an ARGB Long from domain models to a Compose [Color]. */
fun Long.toColor(): Color = Color(this)

/** Maps colour key strings (used in categoryColorKey, accentColorKey) to Color. */
@Composable
fun colorFromKey(key: String): Color = when (key.lowercase()) {
    "blue"   -> DonutTheme.staticColorTokens.accentBlue
    "purple" -> DonutTheme.staticColorTokens.accentPurple
    "green"  -> DonutTheme.staticColorTokens.accentGreen
    "red"    -> DonutTheme.colorTokens.error
    "orange" -> DonutTheme.staticColorTokens.accentOrange
    "yellow" -> DonutTheme.staticColorTokens.accentYellow
    else     -> DonutTheme.staticColorTokens.accentBlue
}
