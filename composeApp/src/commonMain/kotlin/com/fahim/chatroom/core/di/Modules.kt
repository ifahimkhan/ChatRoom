package com.fahim.chatroom.core.di

import com.fahim.chatroom.core.config.SupabaseConfig
import com.fahim.chatroom.core.crypto.CryptoBox
import com.fahim.chatroom.core.crypto.NoopCryptoBox
import com.fahim.chatroom.core.db.ChatDatabase
import com.fahim.chatroom.core.db.DatabaseDriverFactory
import com.fahim.chatroom.core.dispatchers.DefaultDispatcherProvider
import com.fahim.chatroom.core.dispatchers.DispatcherProvider
import com.fahim.chatroom.core.logging.AppLogger
import com.fahim.chatroom.core.logging.ConsoleAppLogger
import com.fahim.chatroom.core.supabase.buildSupabaseClient
import com.fahim.chatroom.data.auth.SecureStorageSessionManager
import com.fahim.chatroom.data.auth.di.authModule
import com.fahim.chatroom.data.chat.di.chatModule
import com.fahim.chatroom.data.profile.di.profileModule
import com.fahim.chatroom.data.rooms.di.roomsModule
import io.github.jan.supabase.auth.SessionManager
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val coreModule: Module = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<AppLogger> { ConsoleAppLogger() }
    single<CryptoBox> { NoopCryptoBox() }
    single { SupabaseConfig.Default } // TODO: replace with real project config (see SupabaseConfig).
    single<SessionManager> { SecureStorageSessionManager(get()) }
    single { buildSupabaseClient(get(), get()) }
    single { ChatDatabase(get<DatabaseDriverFactory>().create()) }
}

/** Bindings supplied per platform (e.g. [com.fahim.chatroom.core.storage.SecureStorage]). */
expect val platformModule: Module

fun appModules(): List<Module> = listOf(coreModule, platformModule, authModule, roomsModule, chatModule, profileModule)

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication = startKoin {
    appDeclaration()
    modules(appModules())
}