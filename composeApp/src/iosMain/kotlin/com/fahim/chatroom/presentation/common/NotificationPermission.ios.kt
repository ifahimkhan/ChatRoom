package com.fahim.chatroom.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UIKit.UIApplication

@Composable
actual fun rememberNotificationPermissionRequester(): NotificationPermissionRequester {
    val state = remember { mutableStateOf(NotificationPermissionStatus.NotDetermined) }

    remember {
        UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
            val status = when (settings?.authorizationStatus) {
                UNAuthorizationStatusAuthorized -> NotificationPermissionStatus.Granted
                UNAuthorizationStatusDenied -> NotificationPermissionStatus.Denied
                UNAuthorizationStatusNotDetermined -> NotificationPermissionStatus.NotDetermined
                else -> NotificationPermissionStatus.NotDetermined
            }
            platform.darwin.dispatch_async(platform.darwin.dispatch_get_main_queue()) {
                state.value = status
            }
        }
    }

    return remember {
        object : NotificationPermissionRequester {
            override val status: State<NotificationPermissionStatus> = state
            override fun request() {
                val center = UNUserNotificationCenter.currentNotificationCenter()
                val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
                center.requestAuthorizationWithOptions(options) { granted, error ->
                    val status = if (granted) NotificationPermissionStatus.Granted else NotificationPermissionStatus.Denied
                    platform.darwin.dispatch_async(platform.darwin.dispatch_get_main_queue()) {
                        state.value = status
                    }
                }
            }
        }
    }
}
