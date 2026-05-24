package com.fahim.chatroom

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.fahim.chatroom.core.navigation.DeepLinkBus
import com.fahim.chatroom.presentation.notifications.NotificationChannels
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val deepLinkBus: DeepLinkBus by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
        setContent { App() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val roomId = intent?.getStringExtra(NotificationChannels.EXTRA_ROOM_ID) ?: return
        deepLinkBus.postRoomId(roomId)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
