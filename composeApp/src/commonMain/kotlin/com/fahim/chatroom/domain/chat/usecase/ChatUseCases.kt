package com.fahim.chatroom.domain.chat.usecase

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.chat.model.Message
import com.fahim.chatroom.domain.chat.repository.MessagesRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Instant

class ObserveMessagesUseCase(private val repo: MessagesRepository) {
    operator fun invoke(roomId: String): StateFlow<List<Message>> = repo.messages(roomId)
}

class LoadInitialMessagesUseCase(private val repo: MessagesRepository) {
    suspend operator fun invoke(roomId: String): AppResult<Unit> = repo.loadInitial(roomId)
}

class LoadOlderMessagesUseCase(private val repo: MessagesRepository) {
    suspend operator fun invoke(roomId: String, before: Instant, beforeId: String): AppResult<Boolean> =
        repo.loadOlder(roomId, before, beforeId)
}

class SendMessageUseCase(private val repo: MessagesRepository) {
    suspend operator fun invoke(roomId: String, content: String): AppResult<Unit> =
        repo.send(roomId, content.trim())
}

class RetryFailedMessageUseCase(private val repo: MessagesRepository) {
    suspend operator fun invoke(roomId: String, localMessageId: String): AppResult<Unit> =
        repo.retrySend(roomId, localMessageId)
}
