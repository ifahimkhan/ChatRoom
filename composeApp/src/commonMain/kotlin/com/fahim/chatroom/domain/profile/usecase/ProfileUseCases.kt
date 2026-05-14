package com.fahim.chatroom.domain.profile.usecase

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.profile.model.Profile
import com.fahim.chatroom.domain.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveProfileUseCase(private val repo: ProfileRepository) {
    operator fun invoke(): StateFlow<Profile?> = repo.profile
}

class RefreshProfileUseCase(private val repo: ProfileRepository) {
    suspend operator fun invoke(): AppResult<Unit> = repo.refresh()
}

class UpdateDisplayNameUseCase(private val repo: ProfileRepository) {
    suspend operator fun invoke(newName: String): AppResult<Unit> = repo.updateDisplayName(newName.trim())
}
