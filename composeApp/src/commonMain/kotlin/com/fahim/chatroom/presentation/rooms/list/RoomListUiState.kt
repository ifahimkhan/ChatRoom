package com.fahim.chatroom.presentation.rooms.list

import com.fahim.chatroom.domain.rooms.model.Room

data class RoomListUiState(
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val phase: Phase
        get() = when {
            isLoading && rooms.isEmpty() -> Phase.Loading
            errorMessage != null && rooms.isEmpty() -> Phase.Error
            rooms.isEmpty() -> Phase.Empty
            else -> Phase.Content
        }

    enum class Phase { Loading, Empty, Error, Content }
}