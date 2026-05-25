package com.fahim.chatroom.presentation.common

import androidx.compose.runtime.Composable

/**
 * Multiplatform shim for system back handling. On Android this binds to the activity's
 * OnBackPressedDispatcher; on iOS it's a no-op (the OS provides edge-swipe navigation).
 */
@Composable
expect fun PlatformBackHandler(enabled: Boolean = true, onBack: () -> Unit)
