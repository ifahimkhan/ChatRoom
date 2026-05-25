package com.fahim.chatroom.core.notifications

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Tracks which room the user is currently viewing so FCM can suppress redundant notifications. */
class ActiveRoomTracker {
    private val _activeRoomId = MutableStateFlow<String?>(null)
    val activeRoomId: StateFlow<String?> = _activeRoomId.asStateFlow()

    fun enter(roomId: String) {
        _activeRoomId.value = roomId
    }

    fun leave(roomId: String) {
        _activeRoomId.update { current -> if (current == roomId) null else current }
    }
}
