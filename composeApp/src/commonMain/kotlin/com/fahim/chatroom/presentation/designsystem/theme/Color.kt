package com.fahim.chatroom.presentation.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Aurora accent ramp — used for gradients, FAB, send button, own message bubble.
internal val AuroraViolet = Color(0xFF8B6BFF)
internal val AuroraIndigo = Color(0xFF4E5BFF)
internal val AuroraCyan = Color(0xFF38D4FF)
internal val AuroraMint = Color(0xFF6CF0C2)

// Midnight surface ramp (dark theme).
private val Midnight0 = Color(0xFF06080F)   // app background
private val Midnight1 = Color(0xFF0B0F1A)   // surface
private val Midnight2 = Color(0xFF131826)   // surfaceVariant / cards
private val Midnight3 = Color(0xFF1B2236)   // raised chips / input pill
private val MidnightOutline = Color(0xFF2A3450)
private val MidnightOutlineSoft = Color(0xFF1B2235)

private val InkPrimary = Color(0xFFEAEEFB)
private val InkSecondary = Color(0xFFA7B0C9)

// Paper surface ramp (light theme).
private val Paper0 = Color(0xFFF5F6FB)
private val Paper1 = Color(0xFFFFFFFF)
private val Paper2 = Color(0xFFEFF1F8)
private val Paper3 = Color(0xFFE4E8F3)
private val PaperOutline = Color(0xFFD6DBEC)
private val PaperOutlineSoft = Color(0xFFE6EAF4)

private val InkLight1 = Color(0xFF0D1226)
private val InkLight2 = Color(0xFF4A5072)

internal val DarkColors = darkColorScheme(
    primary = AuroraViolet,
    onPrimary = Color(0xFF0A0720),
    primaryContainer = Color(0xFF2A2358),
    onPrimaryContainer = Color(0xFFE2DDFF),
    secondary = AuroraCyan,
    onSecondary = Color(0xFF002633),
    secondaryContainer = Color(0xFF073A4D),
    onSecondaryContainer = Color(0xFFCBEEFF),
    tertiary = AuroraMint,
    onTertiary = Color(0xFF002819),
    background = Midnight0,
    onBackground = InkPrimary,
    surface = Midnight1,
    onSurface = InkPrimary,
    surfaceVariant = Midnight2,
    onSurfaceVariant = InkSecondary,
    surfaceTint = AuroraViolet,
    outline = MidnightOutline,
    outlineVariant = MidnightOutlineSoft,
    error = Color(0xFFFF6B7B),
    onError = Color(0xFF1F0006),
    errorContainer = Color(0xFF3E0915),
    onErrorContainer = Color(0xFFFFD9DF),
    scrim = Color(0xCC04060B),
)

internal val LightColors = lightColorScheme(
    primary = AuroraIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5E9FF),
    onPrimaryContainer = Color(0xFF1A1D5C),
    secondary = Color(0xFF1392B2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD2F0FB),
    onSecondaryContainer = Color(0xFF002633),
    tertiary = Color(0xFF1F8F73),
    background = Paper0,
    onBackground = InkLight1,
    surface = Paper1,
    onSurface = InkLight1,
    surfaceVariant = Paper2,
    onSurfaceVariant = InkLight2,
    surfaceTint = AuroraIndigo,
    outline = PaperOutline,
    outlineVariant = PaperOutlineSoft,
    error = Color(0xFFC2334A),
)

internal val DarkSurfaceRaised = Midnight3
internal val LightSurfaceRaised = Paper3
