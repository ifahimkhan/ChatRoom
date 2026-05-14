package com.fahim.chatroom.domain.rooms.repository

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.domain.rooms.model.UserLookup
import kotlinx.coroutines.flow.StateFlow

interface RoomsRepository {
    val rooms: StateFlow<List<Room>>

    suspend fun refresh(): AppResult<Unit>
    suspend fun createRoom(name: String, memberUserIds: List<String>): AppResult<Room>
    suspend fun findUserByEmail(email: String): AppResult<UserLookup?>
}