package com.fahim.chatroom.presentation.common

import androidx.compose.ui.Modifier

/**
 * Left-edge swipe-back gesture. On Android this is a no-op because the OS owns the
 * edge-swipe (it routes through OnBackPressedDispatcher / [PlatformBackHandler]).
 * On iOS this attaches a pointer-input detector that commits when the user drags
 * horizontally from the left edge past a threshold.
 */
expect fun Modifier.platformSwipeBack(enabled: Boolean = true, onBack: () -> Unit): Modifier
