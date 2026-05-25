package com.fahim.chatroom.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.core.notifications.ActiveRoomTracker
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import com.fahim.chatroom.domain.chat.model.Message
import com.fahim.chatroom.domain.chat.repository.MessagesRepository
import com.fahim.chatroom.domain.rooms.model.UserLookup
import com.fahim.chatroom.domain.rooms.repository.RoomsRepository
import com.fahim.chatroom.presentation.chat.model.ChatListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ChatViewModel(
    private val roomId: String,
    private val messages: MessagesRepository,
    private val rooms: RoomsRepository,
    authRepo: AuthRepository,
    private val activeRoomTracker: ActiveRoomTracker,
) : ViewModel() {

    private val currentUserId: String? = authRepo.session.value?.userId
    private val zone = TimeZone.currentSystemDefault()

    private data class Flags(
        val isLoadingInitial: Boolean = true,
        val isLoadingOlder: Boolean = false,
        val hasOlder: Boolean = true,
        val errorMessage: String? = null,
        val input: String = "",
        val members: List<UserLookup> = emptyList(),
        val isLoadingMembers: Boolean = false,
        val showMembersDialog: Boolean = false,
        val membersError: String? = null,
        val isRoomDeleted: Boolean = false,
        val isDeletingRoom: Boolean = false,
        val deleteRoomError: String? = null,
        val showDeleteConfirmation: Boolean = false,
        val isAddingMember: Boolean = false,
        val addMemberError: String? = null,
    )

    private val flags = MutableStateFlow(Flags())

    val state: StateFlow<ChatUiState> = combine(
        messages.messages(roomId),
        rooms.rooms,
        flags,
    ) { msgs, allRooms, f ->
        val room = allRooms.find { it.id == roomId }
        val isOwner = room?.createdBy != null && room.createdBy == currentUserId

        ChatUiState(
            items = buildChatItems(msgs, currentUserId, zone),
            isLoadingInitial = f.isLoadingInitial,
            isLoadingOlder = f.isLoadingOlder,
            hasOlder = f.hasOlder,
            errorMessage = f.errorMessage,
            input = f.input,
            members = f.members,
            isLoadingMembers = f.isLoadingMembers,
            showMembersDialog = f.showMembersDialog,
            membersError = f.membersError,
            isOwner = isOwner,
            isRoomDeleted = f.isRoomDeleted,
            isDeletingRoom = f.isDeletingRoom,
            deleteRoomError = f.deleteRoomError,
            showDeleteConfirmation = f.showDeleteConfirmation,
            isAddingMember = f.isAddingMember,
            addMemberError = f.addMemberError,
            currentUserId = currentUserId,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ChatUiState())

    init {
        activeRoomTracker.enter(roomId)
        loadInitial()
        viewModelScope.launch { messages.streamRoom(roomId) }
    }

    override fun onCleared() {
        activeRoomTracker.leave(roomId)
        super.onCleared()
    }

    fun retryInitial() = loadInitial()

    private fun loadInitial() {
        flags.update { it.copy(isLoadingInitial = true, errorMessage = null) }
        viewModelScope.launch {
            when (val r = messages.loadInitial(roomId)) {
                is AppResult.Success -> flags.update { it.copy(isLoadingInitial = false) }
                is AppResult.Failure -> flags.update {
                    it.copy(isLoadingInitial = false, errorMessage = r.error.message ?: "Couldn't load messages")
                }
            }
        }
    }

    fun loadOlder() {
        val current = state.value
        if (current.isLoadingOlder || !current.hasOlder) return
        val oldest: Message = current.items
            .asSequence()
            .filterIsInstance<ChatListItem.MessageRow>()
            .map { it.message }
            .minByOrNull { it.createdAt }
            ?: return
        flags.update { it.copy(isLoadingOlder = true) }
        viewModelScope.launch {
            when (val r = messages.loadOlder(roomId, oldest.createdAt, oldest.id)) {
                is AppResult.Success -> flags.update { it.copy(isLoadingOlder = false, hasOlder = r.data) }
                is AppResult.Failure -> flags.update { it.copy(isLoadingOlder = false) }
            }
        }
    }

    fun onInputChange(value: String) {
        flags.update { it.copy(input = value) }
    }

    fun send() {
        val text = flags.value.input.trim()
        if (text.isEmpty()) return
        flags.update { it.copy(input = "") }
        viewModelScope.launch { messages.send(roomId, text) }
    }

    fun retryFailed(localId: String) {
        viewModelScope.launch { messages.retrySend(roomId, localId) }
    }

    fun showMembers() {
        flags.update { it.copy(showMembersDialog = true, isLoadingMembers = true, membersError = null) }
        viewModelScope.launch {
            when (val r = rooms.getRoomMembers(roomId)) {
                is AppResult.Success -> flags.update { it.copy(isLoadingMembers = false, members = r.data) }
                is AppResult.Failure -> flags.update {
                    it.copy(isLoadingMembers = false, membersError = r.error.message ?: "Couldn't load members")
                }
            }
        }
    }

    fun dismissMembers() {
        flags.update { it.copy(showMembersDialog = false) }
    }

    fun requestDeleteRoom() {
        flags.update { it.copy(showDeleteConfirmation = true) }
    }

    fun dismissDeleteConfirmation() {
        flags.update { it.copy(showDeleteConfirmation = false) }
    }

    fun confirmDeleteRoom() {
        val current = state.value
        if (!current.isOwner || current.isDeletingRoom) return
        flags.update { it.copy(isDeletingRoom = true, deleteRoomError = null) }
        viewModelScope.launch {
            when (val r = rooms.deleteRoom(roomId)) {
                is AppResult.Success -> {
                    flags.update { it.copy(isDeletingRoom = false, isRoomDeleted = true, showDeleteConfirmation = false) }
                }
                is AppResult.Failure -> {
                    flags.update {
                        it.copy(
                            isDeletingRoom = false,
                            deleteRoomError = r.error.message ?: "Couldn't delete room"
                        )
                    }
                }
            }
        }
    }

    fun addRoomMember(email: String) {
        val emailInput = email.trim()
        if (emailInput.isEmpty()) return
        flags.update { it.copy(isAddingMember = true, addMemberError = null) }
        viewModelScope.launch {
            when (val r = rooms.findUserByEmail(emailInput)) {
                is AppResult.Success -> {
                    val user = r.data
                    if (user == null) {
                        flags.update { it.copy(isAddingMember = false, addMemberError = "No user found with that email") }
                        return@launch
                    }
                    if (flags.value.members.any { it.id == user.id }) {
                        flags.update { it.copy(isAddingMember = false, addMemberError = "User is already a member") }
                        return@launch
                    }

                    when (val addRes = rooms.addRoomMember(roomId, user.id)) {
                        is AppResult.Success -> {
                            flags.update { it.copy(isAddingMember = false, members = it.members + user) }
                        }
                        is AppResult.Failure -> {
                            flags.update {
                                it.copy(
                                    isAddingMember = false,
                                    addMemberError = addRes.error.message ?: "Failed to add user to the room"
                                )
                            }
                        }
                    }
                }
                is AppResult.Failure -> {
                    flags.update {
                        it.copy(
                            isAddingMember = false,
                            addMemberError = r.error.message ?: "User lookup failed"
                        )
                    }
                }
            }
        }
    }

    fun removeRoomMember(userId: String) {
        viewModelScope.launch {
            when (val r = rooms.removeRoomMember(roomId, userId)) {
                is AppResult.Success -> {
                    flags.update { it.copy(members = it.members.filterNot { it.id == userId }) }
                }
                is AppResult.Failure -> {
                    flags.update {
                        it.copy(membersError = r.error.message ?: "Failed to remove member")
                    }
                }
            }
        }
    }



    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

/** Folds chronological messages into a list ready for a `reverseLayout = true` LazyColumn:
 *  newest first, with a [ChatListItem.DateHeader] preceding each day's first message (rendered as
 *  a separator above the day's messages). */
internal fun buildChatItems(
    messages: List<Message>,
    currentUserId: String?,
    zone: TimeZone,
): List<ChatListItem> {
    if (messages.isEmpty()) return emptyList()
    val chronological = ArrayList<ChatListItem>(messages.size + 8)
    var prevDate: LocalDate? = null
    for (msg in messages) {
        val date = msg.createdAt.toLocalDateTime(zone).date
        if (date != prevDate) {
            chronological += ChatListItem.DateHeader(date)
            prevDate = date
        }
        chronological += ChatListItem.MessageRow(message = msg, isOwn = msg.senderId == currentUserId)
    }
    return chronological.asReversed()
}
