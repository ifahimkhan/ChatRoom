package com.fahim.chatroom.data.chat.di

import com.fahim.chatroom.data.chat.SupabaseMessagesRepository
import com.fahim.chatroom.domain.chat.repository.MessagesRepository
import com.fahim.chatroom.domain.chat.usecase.LoadInitialMessagesUseCase
import com.fahim.chatroom.domain.chat.usecase.LoadOlderMessagesUseCase
import com.fahim.chatroom.domain.chat.usecase.ObserveMessagesUseCase
import com.fahim.chatroom.domain.chat.usecase.RetryFailedMessageUseCase
import com.fahim.chatroom.domain.chat.usecase.SendMessageUseCase
import com.fahim.chatroom.presentation.chat.ChatViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val chatModule: Module = module {
    single<MessagesRepository> { SupabaseMessagesRepository(get(), get(), get(), get(), get()) }
    factory { ObserveMessagesUseCase(get()) }
    factory { LoadInitialMessagesUseCase(get()) }
    factory { LoadOlderMessagesUseCase(get()) }
    factory { SendMessageUseCase(get()) }
    factory { RetryFailedMessageUseCase(get()) }
    viewModel { (roomId: String) -> ChatViewModel(roomId, get(), get(), get(), get()) }
}
