package com.fahim.chatroom.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.profile.usecase.ObserveProfileUseCase
import com.fahim.chatroom.domain.profile.usecase.RefreshProfileUseCase
import com.fahim.chatroom.domain.profile.usecase.UpdateDisplayNameUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    observeProfile: ObserveProfileUseCase,
    private val refreshProfile: RefreshProfileUseCase,
    private val updateName: UpdateDisplayNameUseCase,
) : ViewModel() {

    private data class Flags(
        val draftOverride: String? = null,   // null => show the canonical name from the profile
        val isSaving: Boolean = false,
        val saveError: String? = null,
        val savedMessage: String? = null,
    )

    private val flags = MutableStateFlow(Flags())

    val state: StateFlow<ProfileUiState> = combine(
        observeProfile(),
        flags,
    ) { profile, f ->
        ProfileUiState(
            isLoading = profile == null,
            email = profile?.email.orEmpty(),
            displayName = profile?.displayName.orEmpty(),
            displayNameDraft = f.draftOverride ?: profile?.displayName.orEmpty(),
            isSaving = f.isSaving,
            saveError = f.saveError,
            savedMessage = f.savedMessage,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ProfileUiState())

    init {
        viewModelScope.launch { refreshProfile() }
    }

    fun onDisplayNameChange(value: String) {
        flags.update { it.copy(draftOverride = value, saveError = null, savedMessage = null) }
    }

    fun save() {
        val current = state.value
        if (!current.canSave) return
        val name = current.displayNameDraft.trim()
        flags.update { it.copy(isSaving = true, saveError = null, savedMessage = null) }
        viewModelScope.launch {
            when (val result = updateName(name)) {
                is AppResult.Success -> flags.value = Flags(savedMessage = "Saved")
                is AppResult.Failure -> flags.update {
                    it.copy(isSaving = false, saveError = result.error.message ?: "Couldn't save")
                }
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
