package com.fahim.chatroom.data.notifications.di

import com.fahim.chatroom.data.notifications.SupabasePushTokenRepository
import com.fahim.chatroom.domain.notifications.repository.PushTokenRepository
import com.fahim.chatroom.domain.notifications.usecase.RegisterDeviceTokenUseCase
import com.fahim.chatroom.domain.notifications.usecase.SyncDeviceTokenUseCase
import com.fahim.chatroom.domain.notifications.usecase.UnregisterDeviceTokenUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val notificationsModule: Module = module {
    single<PushTokenRepository> { SupabasePushTokenRepository(get(), get(), get(), get()) }
    factory { RegisterDeviceTokenUseCase(get(), get()) }
    factory { UnregisterDeviceTokenUseCase(get(), get()) }
    factory { SyncDeviceTokenUseCase(get()) }
}
