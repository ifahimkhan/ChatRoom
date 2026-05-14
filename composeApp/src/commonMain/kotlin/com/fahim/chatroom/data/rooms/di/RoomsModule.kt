package com.fahim.chatroom.data.rooms.di

import com.fahim.chatroom.data.rooms.SupabaseRoomsRepository
import com.fahim.chatroom.domain.rooms.repository.RoomsRepository
import com.fahim.chatroom.domain.rooms.usecase.CreateRoomUseCase
import com.fahim.chatroom.domain.rooms.usecase.FindUserByEmailUseCase
import com.fahim.chatroom.domain.rooms.usecase.ObserveRoomsUseCase
import com.fahim.chatroom.domain.rooms.usecase.RefreshRoomsUseCase
import com.fahim.chatroom.presentation.rooms.create.CreateRoomViewModel
import com.fahim.chatroom.presentation.rooms.list.RoomListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val roomsModule: Module = module {
    single<RoomsRepository> { SupabaseRoomsRepository(get(), get(), get(), get()) }
    factory { ObserveRoomsUseCase(get()) }
    factory { RefreshRoomsUseCase(get()) }
    factory { CreateRoomUseCase(get()) }
    factory { FindUserByEmailUseCase(get()) }
    viewModel { RoomListViewModel(get(), get(), get()) }
    viewModel { CreateRoomViewModel(get(), get(), get()) }
}