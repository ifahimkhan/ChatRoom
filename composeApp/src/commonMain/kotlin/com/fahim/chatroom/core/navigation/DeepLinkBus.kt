package com.fahim.chatroom.core.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Carries room-id deep links from a notification tap into the Compose layer. */
class DeepLinkBus {
    private val _pendingRoomId = MutableSharedFlow<String>(replay = 1, extraBufferCapacity = 1)
    val pendingRoomId: SharedFlow<String> = _pendingRoomId.asSharedFlow()

    fun postRoomId(roomId: String) {
        _pendingRoomId.tryEmit(roomId)
    }
}
