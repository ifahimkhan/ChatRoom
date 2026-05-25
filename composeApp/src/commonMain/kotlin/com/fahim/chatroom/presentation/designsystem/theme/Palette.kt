package com.fahim.chatroom.presentation.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

/** Extra theme tokens that aren't part of Material 3's [androidx.compose.material3.ColorScheme]. */
@Immutable
data class ChatPalette(
    val accentStart: Color,
    val accentMid: Color,
    val accentEnd: Color,
    val accentGlow: Color,
    val surfaceRaised: Color,
    val bubbleOther: Color,
    val bubbleOtherOutline: Color,
    val bubbleOwnOnContent: Color,
    val heroOverlay: Color,
)

internal val DarkChatPalette = ChatPalette(
    accentStart = AuroraViolet,
    accentMid = AuroraIndigo,
    accentEnd = AuroraCyan,
    accentGlow = Color(0x668B6BFF),
    surfaceRaised = DarkSurfaceRaised,
    bubbleOther = Color(0xFF161C2D),
    bubbleOtherOutline = Color(0xFF222B44),
    bubbleOwnOnContent = Color(0xFFF7F4FF),
    heroOverlay = Color(0x3D8B6BFF),
)

internal val LightChatPalette = ChatPalette(
    accentStart = AuroraIndigo,
    accentMid = Color(0xFF6F5BFF),
    accentEnd = Color(0xFF1FB6E5),
    accentGlow = Color(0x554E5BFF),
    surfaceRaised = LightSurfaceRaised,
    bubbleOther = Color(0xFFFFFFFF),
    bubbleOtherOutline = Color(0xFFE2E6F2),
    bubbleOwnOnContent = Color.White,
    heroOverlay = Color(0x224E5BFF),
)

internal val LocalChatPalette = compositionLocalOf<ChatPalette> { DarkChatPalette }

object ChatTheme {
    val palette: ChatPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalChatPalette.current
}
