package com.fahim.chatroom.presentation.chat

import com.fahim.chatroom.presentation.chat.model.ChatListItem

data class ChatUiState(
    val items: List<ChatListItem> = emptyList(),
    val isLoadingInitial: Boolean = true,
    val isLoadingOlder: Boolean = false,
    val hasOlder: Boolean = true,
    val errorMessage: String? = null,
    val input: String = "",
) {
    val canSend: Boolean get() = input.isNotBlank()

    val phase: Phase
        get() = when {
            isLoadingInitial && items.isEmpty() -> Phase.Loading
            errorMessage != null && items.isEmpty() -> Phase.Error
            items.isEmpty() -> Phase.Empty
            else -> Phase.Content
        }

    enum class Phase { Loading, Empty, Error, Content }
}
