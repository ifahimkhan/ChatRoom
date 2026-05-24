package com.fahim.chatroom.data.auth.di

import com.fahim.chatroom.data.auth.SupabaseAuthRepository
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import com.fahim.chatroom.domain.auth.usecase.ObserveSessionUseCase
import com.fahim.chatroom.domain.auth.usecase.SignInUseCase
import com.fahim.chatroom.domain.auth.usecase.SignOutUseCase
import com.fahim.chatroom.domain.auth.usecase.SignUpUseCase
import com.fahim.chatroom.presentation.auth.AuthViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule: Module = module {
    single<AuthRepository> { SupabaseAuthRepository(get(), get()) }
    factory { SignInUseCase(get()) }
    factory { SignUpUseCase(get()) }
    factory { SignOutUseCase(get(), get()) }
    factory { ObserveSessionUseCase(get()) }
    viewModel { AuthViewModel(get(), get(), get()) }
}