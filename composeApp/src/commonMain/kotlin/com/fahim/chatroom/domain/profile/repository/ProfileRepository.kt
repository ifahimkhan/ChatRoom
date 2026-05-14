package com.fahim.chatroom.domain.profile.repository

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.profile.model.Profile
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    /** Current user's profile, or null when signed out / still loading. */
    val profile: StateFlow<Profile?>

    suspend fun refresh(): AppResult<Unit>
    suspend fun updateDisplayName(newName: String): AppResult<Unit>
}
