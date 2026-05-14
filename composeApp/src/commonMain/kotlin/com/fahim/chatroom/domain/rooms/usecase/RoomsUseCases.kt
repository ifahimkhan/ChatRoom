package com.fahim.chatroom.domain.rooms.usecase

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.domain.rooms.model.UserLookup
import com.fahim.chatroom.domain.rooms.repository.RoomsRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveRoomsUseCase(private val repo: RoomsRepository) {
    operator fun invoke(): StateFlow<List<Room>> = repo.rooms
}

class RefreshRoomsUseCase(private val repo: RoomsRepository) {
    suspend operator fun invoke(): AppResult<Unit> = repo.refresh()
}

class CreateRoomUseCase(private val repo: RoomsRepository) {
    suspend operator fun invoke(name: String, memberUserIds: List<String>): AppResult<Room> =
        repo.createRoom(name.trim(), memberUserIds)
}

class FindUserByEmailUseCase(private val repo: RoomsRepository) {
    suspend operator fun invoke(email: String): AppResult<UserLookup?> = repo.findUserByEmail(email.trim())
}