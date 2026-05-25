package com.fahim.chatroom.presentation.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.content.getSystemService
import com.fahim.chatroom.R

object NotificationChannels {
    const val CHAT = "chat_messages"
    const val EXTRA_ROOM_ID = "extra_room_id"
    const val EXTRA_MESSAGE_ID = "extra_message_id"

    fun ensureCreated(context: Context) {
        val nm = context.getSystemService<NotificationManager>() ?: return
        val channel = NotificationChannel(
            CHAT,
            context.getString(R.string.notification_channel_chat_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.notification_channel_chat_description)
            enableLights(true)
            enableVibration(true)
        }
        nm.createNotificationChannel(channel)
    }
}
