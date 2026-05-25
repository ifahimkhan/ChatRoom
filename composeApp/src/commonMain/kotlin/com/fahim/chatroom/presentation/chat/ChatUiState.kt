package com.fahim.chatroom.presentation.chat

import com.fahim.chatroom.domain.rooms.model.UserLookup
import com.fahim.chatroom.presentation.chat.model.ChatListItem

data class ChatUiState(
    val items: List<ChatListItem> = emptyList(),
    val isLoadingInitial: Boolean = true,
    val isLoadingOlder: Boolean = false,
    val hasOlder: Boolean = true,
    val errorMessage: String? = null,
    val input: String = "",
    val members: List<UserLookup> = emptyList(),
    val isLoadingMembers: Boolean = false,
    val showMembersDialog: Boolean = false,
    val membersError: String? = null,
    val isOwner: Boolean = false,
    val isRoomDeleted: Boolean = false,
    val isDeletingRoom: Boolean = false,
    val deleteRoomError: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val isAddingMember: Boolean = false,
    val addMemberError: String? = null,
    val currentUserId: String? = null,
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
