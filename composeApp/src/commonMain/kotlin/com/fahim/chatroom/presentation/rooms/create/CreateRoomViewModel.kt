package com.fahim.chatroom.presentation.rooms.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.rooms.usecase.CreateRoomUseCase
import com.fahim.chatroom.domain.rooms.usecase.FindUserByEmailUseCase
import com.fahim.chatroom.domain.rooms.usecase.RefreshRoomsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateRoomViewModel(
    private val createRoom: CreateRoomUseCase,
    private val findUser: FindUserByEmailUseCase,
    private val refreshRooms: RefreshRoomsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CreateRoomUiState())
    val state: StateFlow<CreateRoomUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun onNameChange(value: String) {
        _state.update { it.copy(name = value, createError = null) }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(emailInput = value, resolveError = null) }
    }

    fun removeInvitee(userId: String) {
        _state.update { it.copy(invitees = it.invitees.filterNot { i -> i.userId == userId }) }
    }

    fun addInvitee() {
        val current = _state.value
        if (!current.canAddInvitee) return
        val email = current.emailInput.trim()
        _state.update { it.copy(isResolving = true, resolveError = null) }
        viewModelScope.launch {
            when (val result = findUser(email)) {
                is AppResult.Success -> _state.update { s ->
                    val user = result.data
                    when {
                        user == null ->
                            s.copy(isResolving = false, resolveError = "No user with that email")
                        s.invitees.any { it.userId == user.id } ->
                            s.copy(isResolving = false, emailInput = "")
                        else ->
                            s.copy(
                                isResolving = false,
                                emailInput = "",
                                invitees = s.invitees + CreateRoomUiState.Invitee(
                                    userId = user.id,
                                    email = email,
                                    displayName = user.displayName,
                                ),
                            )
                    }
                }
                is AppResult.Failure -> _state.update {
                    it.copy(isResolving = false, resolveError = result.error.message ?: "Lookup failed")
                }
            }
        }
    }

    fun submit() {
        val current = _state.value
        if (!current.canCreate) return
        _state.update { it.copy(isCreating = true, createError = null) }
        viewModelScope.launch {
            when (val result = createRoom(current.name, current.invitees.map { it.userId })) {
                is AppResult.Success -> {
                    refreshRooms()
                    _events.tryEmit(Event.Created(result.data.id))
                }
                is AppResult.Failure -> _state.update {
                    it.copy(isCreating = false, createError = result.error.message ?: "Couldn't create room")
                }
            }
        }
    }

    sealed interface Event {
        data class Created(val roomId: String) : Event
    }
}