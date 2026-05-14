package com.fahim.chatroom.core.di

import com.fahim.chatroom.core.db.DatabaseDriverFactory
import com.fahim.chatroom.core.db.IosDatabaseDriverFactory
import com.fahim.chatroom.core.storage.IosSecureStorage
import com.fahim.chatroom.core.storage.SecureStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SecureStorage> { IosSecureStorage() }
    single<DatabaseDriverFactory> { IosDatabaseDriverFactory() }
}
