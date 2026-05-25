package com.fahim.chatroom.domain.notifications

import com.fahim.chatroom.domain.notifications.model.DeviceToken

object IOSPushTokenManager {
    var token: String? = null

    fun setDeviceToken(rawToken: String) {
        token = rawToken
    }
}

actual class PushTokenProvider {
    actual suspend fun currentToken(): DeviceToken? {
        return IOSPushTokenManager.token?.let {
            DeviceToken(it, DeviceToken.Platform.Ios)
        }
    }
}
