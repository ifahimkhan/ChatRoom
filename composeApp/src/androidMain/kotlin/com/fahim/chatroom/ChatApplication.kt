package com.fahim.chatroom

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.fahim.chatroom.core.di.initKoin
import com.fahim.chatroom.presentation.notifications.AppForegroundState
import com.fahim.chatroom.presentation.notifications.NotificationChannels
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@ChatApplication)
        }
        NotificationChannels.ensureCreated(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(get<AppForegroundState>())
    }
}
