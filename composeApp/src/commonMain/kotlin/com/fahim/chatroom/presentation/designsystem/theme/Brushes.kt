package com.fahim.chatroom.presentation.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun rememberAuroraBrush(): Brush {
    val p = ChatTheme.palette
    return remember(p.accentStart, p.accentMid, p.accentEnd) {
        Brush.linearGradient(
            colors = listOf(p.accentStart, p.accentMid, p.accentEnd),
            start = Offset(0f, 0f),
            end = Offset.Infinite,
        )
    }
}

@Composable
fun rememberAuroraBrushVertical(): Brush {
    val p = ChatTheme.palette
    return remember(p.accentStart, p.accentEnd) {
        Brush.verticalGradient(colors = listOf(p.accentStart, p.accentEnd))
    }
}

/** Soft top-of-screen glow used behind hero headers. */
@Composable
fun rememberHeroGlowBrush(): Brush {
    val p = ChatTheme.palette
    return remember(p.heroOverlay) {
        Brush.radialGradient(
            colors = listOf(p.heroOverlay, Color.Transparent),
            radius = 900f,
        )
    }
}
