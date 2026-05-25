package com.fahim.chatroom.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

enum class NotificationPermissionStatus { Granted, Denied, NotDetermined, NotRequired }

interface NotificationPermissionRequester {
    val status: State<NotificationPermissionStatus>
    fun request()
}

@Composable
expect fun rememberNotificationPermissionRequester(): NotificationPermissionRequester
