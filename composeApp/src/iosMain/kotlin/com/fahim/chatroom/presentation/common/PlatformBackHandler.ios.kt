package com.fahim.chatroom.presentation.common

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS uses an edge-swipe gesture for back navigation, handled by the platform.
}
