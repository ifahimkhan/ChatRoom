package com.fahim.chatroom.presentation.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun ChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val palette = if (darkTheme) DarkChatPalette else LightChatPalette
    CompositionLocalProvider(LocalChatPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ChatTypography,
            shapes = ChatShapes,
            content = content,
        )
    }
}
