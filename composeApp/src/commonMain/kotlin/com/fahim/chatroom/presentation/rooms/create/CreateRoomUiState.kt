package com.fahim.chatroom.presentation.rooms.create

data class CreateRoomUiState(
    val name: String = "",
    val emailInput: String = "",
    val invitees: List<Invitee> = emptyList(),
    val isResolving: Boolean = false,
    val resolveError: String? = null,
    val isCreating: Boolean = false,
    val createError: String? = null,
) {
    val canAddInvitee: Boolean get() = emailInput.isNotBlank() && !isResolving && !isCreating
    val canCreate: Boolean get() = name.isNotBlank() && !isCreating

    data class Invitee(
        val userId: String,
        val email: String,
        val displayName: String,
    )
}