package com.fahim.chatroom.presentation.profile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val email: String = "",
    val displayName: String = "",
    val displayNameDraft: String = "",
    val isSaving: Boolean = false,
    val saveError: String? = null,
    val savedMessage: String? = null,
) {
    val canSave: Boolean
        get() = displayNameDraft.isNotBlank() &&
            displayNameDraft.trim() != displayName &&
            !isSaving
}
