package com.fahim.chatroom.presentation.notifications

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.fahim.chatroom.MainActivity
import com.fahim.chatroom.R
import com.fahim.chatroom.core.logging.AppLogger
import com.fahim.chatroom.core.notifications.ActiveRoomTracker
import com.fahim.chatroom.domain.notifications.model.DeviceToken
import com.fahim.chatroom.domain.notifications.usecase.SyncDeviceTokenUseCase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {

    private companion object { const val TAG = "FcmService" }

    private val syncToken: SyncDeviceTokenUseCase by inject()
    private val activeRoom: ActiveRoomTracker by inject()
    private val appForeground: AppForegroundState by inject()
    private val logger: AppLogger by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        logger.i(TAG, "onNewToken token=${token.take(8)}…")
        scope.launch { syncToken(DeviceToken(token, DeviceToken.Platform.Android)) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val roomId = data["roomId"] ?: return
        val messageId = data["messageId"]
        logger.d(TAG, "onMessageReceived room=$roomId msg=$messageId")

        if (activeRoom.activeRoomId.value == roomId && appForeground.isForeground) {
            logger.d(TAG, "suppressed (foreground room)")
            return
        }

        val title = message.notification?.title ?: data["title"] ?: getString(R.string.app_name)
        val body = message.notification?.body ?: data["body"] ?: "Tap to view"
        show(roomId, messageId, title, body)
    }

    private fun show(roomId: String, messageId: String?, title: String, body: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationChannels.EXTRA_ROOM_ID, roomId)
            messageId?.let { putExtra(NotificationChannels.EXTRA_MESSAGE_ID, it) }
        }
        val pending = PendingIntent.getActivity(
            this,
            roomId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notif = NotificationCompat.Builder(this, NotificationChannels.CHAT)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(this, R.color.notification_accent))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setContentIntent(pending)
            .build()

        runCatching {
            NotificationManagerCompat.from(this).notify(roomId.hashCode(), notif)
        }.onFailure { logger.w(TAG, "notify denied (likely missing POST_NOTIFICATIONS)") }
    }
}
