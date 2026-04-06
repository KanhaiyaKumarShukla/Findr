package com.exa.android.reflekt.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalDonutColorTokens = staticCompositionLocalOf { donutLightColorTokens() }
val LocalStaticDonutColorTokens = staticCompositionLocalOf { donutLightColorTokens() }

class DonutColorTokens(

    // ── Material3 semantic colors (primary palette) ──────────────────────────
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,

    // ── Backgrounds ──────────────────────────────────────────────────────────
    val background: Color,
    val onBackground: Color,

    // ── Surfaces ─────────────────────────────────────────────────────────────
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,

    // ── Borders / Outlines ───────────────────────────────────────────────────
    val outline: Color,
    val outlineVariant: Color,

    // ── Error ────────────────────────────────────────────────────────────────
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,

    // ── Cards ────────────────────────────────────────────────────────────────
    val cardBackground: Color,
    val cardBorder: Color,
    val cardText: Color,

    // ── Illustration ─────────────────────────────────────────────────────────
    val illustrationBackground: Color,

    // ── Badges / Avatars ─────────────────────────────────────────────────────
    val badgeBorder: Color,
    val avatarBorder: Color,

    // ── Pagination ───────────────────────────────────────────────────────────
    val paginationDotInactive: Color,

    // ── Bar Chart ────────────────────────────────────────────────────────────
    val barChartLow: Color,
    val barChartMid: Color,
    val barChartHigh: Color,
    val barChartLabel: Color,

    // ── Form Card ────────────────────────────────────────────────────────────
    val formCardBackground: Color,

    // ── Accent Colors (theme-invariant) ──────────────────────────────────────
    val accentBlue: Color,
    val accentIndigo: Color,
    val accentGreen: Color,
    val accentPurple: Color,
    val accentYellow: Color,
    val accentOrange: Color,
    val purpleGlow: Color,
    val googleBlue: Color,

    // ── Icon on colored backgrounds ──────────────────────────────────────────
    val iconOnAccent: Color,
)