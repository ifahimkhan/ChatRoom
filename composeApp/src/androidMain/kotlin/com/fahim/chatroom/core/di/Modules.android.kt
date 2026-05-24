package com.fahim.chatroom.core.di

import com.fahim.chatroom.core.db.AndroidDatabaseDriverFactory
import com.fahim.chatroom.core.db.DatabaseDriverFactory
import com.fahim.chatroom.core.storage.AndroidSecureStorage
import com.fahim.chatroom.core.storage.SecureStorage
import com.fahim.chatroom.domain.notifications.PushTokenProvider
import com.fahim.chatroom.presentation.notifications.AppForegroundState
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SecureStorage> { AndroidSecureStorage(androidContext()) }
    single<DatabaseDriverFactory> { AndroidDatabaseDriverFactory(androidContext()) }
    single { PushTokenProvider() }
    single { AppForegroundState() }
}
