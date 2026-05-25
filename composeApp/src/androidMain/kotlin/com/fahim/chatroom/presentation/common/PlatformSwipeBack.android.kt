package com.fahim.chatroom.presentation.common

import androidx.compose.ui.Modifier

// Android's system edge-swipe is dispatched through OnBackPressedDispatcher and is
// already picked up by PlatformBackHandler — no Compose-level gesture needed.
actual fun Modifier.platformSwipeBack(enabled: Boolean, onBack: () -> Unit): Modifier = this
