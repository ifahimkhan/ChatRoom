package com.fahim.chatroom.presentation.notifications

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/** Process-wide foreground flag. Bound to [androidx.lifecycle.ProcessLifecycleOwner] from Application.onCreate. */
class AppForegroundState : DefaultLifecycleObserver {
    @Volatile
    var isForeground: Boolean = false
        private set

    override fun onStart(owner: LifecycleOwner) { isForeground = true }
    override fun onStop(owner: LifecycleOwner) { isForeground = false }
}
