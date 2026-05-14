package com.fahim.chatroom.data.profile.di

import com.fahim.chatroom.data.profile.SupabaseProfileRepository
import com.fahim.chatroom.domain.profile.repository.ProfileRepository
import com.fahim.chatroom.domain.profile.usecase.ObserveProfileUseCase
import com.fahim.chatroom.domain.profile.usecase.RefreshProfileUseCase
import com.fahim.chatroom.domain.profile.usecase.UpdateDisplayNameUseCase
import com.fahim.chatroom.presentation.profile.ProfileViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule: Module = module {
    single<ProfileRepository> { SupabaseProfileRepository(get(), get(), get()) }
    factory { ObserveProfileUseCase(get()) }
    factory { RefreshProfileUseCase(get()) }
    factory { UpdateDisplayNameUseCase(get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
}
