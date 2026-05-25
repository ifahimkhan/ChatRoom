package com.fahim.chatroom.domain.notifications

import com.fahim.chatroom.domain.notifications.model.DeviceToken
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class PushTokenProvider {
    actual suspend fun currentToken(): DeviceToken? = runCatching {
        val raw: String? = suspendCancellableCoroutine { cont ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token -> cont.resume(token) }
                .addOnFailureListener { cont.resume(null) }
        }
        raw?.let { DeviceToken(it, DeviceToken.Platform.Android) }
    }.getOrNull()
}
