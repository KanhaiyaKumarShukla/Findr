package com.exa.android.reflekt.feature.home.presentation.components

import androidx.compose.ui.graphics.Color

// ─── Colour mappers ───────────────────────────────────────────────────────────

/** Maps an ARGB Long from domain models to a Compose [Color]. */
fun Long.toColor(): Color = Color(this)

/** Maps colour key strings (used in categoryColorKey, accentColorKey) to Color. */
fun colorFromKey(key: String): Color = when (key) {
    "blue"   -> Color(0xFF13B6EC)
    "purple" -> Color(0xFFA855F7)
    "green"  -> Color(0xFF22C55E)
    "red"    -> Color(0xFFEF4444)
    "orange" -> Color(0xFFF97316)
    "yellow" -> Color(0xFFEAB308)
    else     -> Color(0xFF13B6EC)
}
