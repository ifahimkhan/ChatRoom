package com.fahim.chatroom.domain.notifications.repository

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.notifications.model.DeviceToken

interface PushTokenRepository {
    suspend fun register(token: DeviceToken): AppResult<Unit>
    suspend fun unregister(token: DeviceToken): AppResult<Unit>
}
