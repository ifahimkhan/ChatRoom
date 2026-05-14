package com.fahim.chatroom

import android.app.Application
import com.fahim.chatroom.core.di.initKoin
import org.koin.android.ext.koin.androidContext

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@ChatApplication)
        }
    }
}