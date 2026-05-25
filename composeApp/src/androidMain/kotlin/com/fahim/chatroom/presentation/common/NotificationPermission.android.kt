package com.fahim.chatroom.presentation.common

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberNotificationPermissionRequester(): NotificationPermissionRequester {
    val context = LocalContext.current
    val state: MutableState<NotificationPermissionStatus> = remember {
        mutableStateOf(initialStatus(context))
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        state.value = if (granted) NotificationPermissionStatus.Granted else NotificationPermissionStatus.Denied
    }
    return remember(state) {
        object : NotificationPermissionRequester {
            override val status: State<NotificationPermissionStatus> = state
            override fun request() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    state.value = NotificationPermissionStatus.NotRequired
                }
            }
        }
    }
}

private fun initialStatus(context: android.content.Context): NotificationPermissionStatus {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return NotificationPermissionStatus.NotRequired
    val granted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED
    return if (granted) NotificationPermissionStatus.Granted else NotificationPermissionStatus.NotDetermined
}
