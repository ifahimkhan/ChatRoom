package com.fahim.chatroom.presentation.rooms.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.auth.usecase.ObserveSessionUseCase
import com.fahim.chatroom.domain.rooms.usecase.ObserveRoomsUseCase
import com.fahim.chatroom.domain.rooms.usecase.RefreshRoomsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RoomListViewModel(
    observeRooms: ObserveRoomsUseCase,
    private val refreshRooms: RefreshRoomsUseCase,
    observeSession: ObserveSessionUseCase,
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val state: StateFlow<RoomListUiState> = combine(
        observeRooms(),
        isLoading,
        errorMessage,
    ) { rooms, loading, error ->
        RoomListUiState(rooms = rooms, isLoading = loading, errorMessage = error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), RoomListUiState(isLoading = true))

    init {
        // Refresh on first appearance and whenever a session arrives (sign-out → sign-in).
        viewModelScope.launch {
            observeSession().collect { session -> if (session != null) refresh() }
        }
    }

    fun refresh() {
        if (isLoading.value) return
        isLoading.value = true
        errorMessage.value = null
        viewModelScope.launch {
            when (val result = refreshRooms()) {
                is AppResult.Success -> Unit
                is AppResult.Failure -> errorMessage.value = result.error.message ?: "Couldn't load rooms"
            }
            isLoading.value = false
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}