package com.fahim.chatroom.domain.chat.repository

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.chat.model.Message
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

interface MessagesRepository {
    /** Per-room source of truth in chronological order (oldest .. newest). */
    fun messages(roomId: String): StateFlow<List<Message>>

    suspend fun loadInitial(roomId: String): AppResult<Unit>

    /** Returns true if the response was full (more pages may exist). */
    suspend fun loadOlder(roomId: String, before: Instant, beforeId: String): AppResult<Boolean>

    suspend fun send(roomId: String, content: String): AppResult<Unit>

    suspend fun retrySend(roomId: String, localMessageId: String): AppResult<Unit>

    /**
     * Subscribes to realtime INSERT/UPDATE/DELETE events for [roomId] over a private channel and
     * merges them into the per-room flow (dedupes against optimistic sends by message id).
     * Suspends until the caller cancels; safe to launch in [androidx.lifecycle.viewModelScope].
     */
    suspend fun streamRoom(roomId: String)
}
