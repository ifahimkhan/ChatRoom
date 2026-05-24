package com.fahim.chatroom.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.core.notifications.ActiveRoomTracker
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import com.fahim.chatroom.domain.chat.model.Message
import com.fahim.chatroom.domain.chat.repository.MessagesRepository
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
    )

    private val flags = MutableStateFlow(Flags())

    val state: StateFlow<ChatUiState> = combine(
        messages.messages(roomId),
        flags,
    ) { msgs, f ->
        ChatUiState(
            items = buildChatItems(msgs, currentUserId, zone),
            isLoadingInitial = f.isLoadingInitial,
            isLoadingOlder = f.isLoadingOlder,
            hasOlder = f.hasOlder,
            errorMessage = f.errorMessage,
            input = f.input,
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
