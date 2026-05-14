package com.fahim.chatroom.presentation.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val BrandLight = Color(0xFF3D5AFE)
private val BrandDark = Color(0xFF8C9EFF)

internal val LightColors = lightColorScheme(
    primary = BrandLight,
    secondary = Color(0xFF526070),
)

internal val DarkColors = darkColorScheme(
    primary = BrandDark,
    secondary = Color(0xFFBAC8D8),
)