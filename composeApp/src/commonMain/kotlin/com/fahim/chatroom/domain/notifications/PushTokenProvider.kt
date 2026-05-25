package com.fahim.chatroom.domain.notifications

import com.fahim.chatroom.domain.notifications.model.DeviceToken

expect class PushTokenProvider() {
    suspend fun currentToken(): DeviceToken?
}
