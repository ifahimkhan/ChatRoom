package com.fahim.chatroom.domain.auth.repository

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.auth.model.AuthSession
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    /** null when signed out. Hot stream; safe to collect at app root for the auth gate. */
    val session: StateFlow<AuthSession?>

    /** True while the persisted session is being loaded from storage on cold start. */
    val isInitializing: StateFlow<Boolean>

    suspend fun signUp(email: String, password: String): AppResult<Unit>
    suspend fun signIn(email: String, password: String): AppResult<Unit>
    suspend fun signOut(): AppResult<Unit>
}