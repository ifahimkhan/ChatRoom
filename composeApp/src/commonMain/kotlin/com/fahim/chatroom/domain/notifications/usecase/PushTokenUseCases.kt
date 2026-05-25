package com.fahim.chatroom.domain.notifications.usecase

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.notifications.PushTokenProvider
import com.fahim.chatroom.domain.notifications.model.DeviceToken
import com.fahim.chatroom.domain.notifications.repository.PushTokenRepository

class RegisterDeviceTokenUseCase(
    private val provider: PushTokenProvider,
    private val repo: PushTokenRepository,
) {
    suspend operator fun invoke(): AppResult<Unit> {
        val token = provider.currentToken() ?: return AppResult.Success(Unit)
        return repo.register(token)
    }
}

class UnregisterDeviceTokenUseCase(
    private val provider: PushTokenProvider,
    private val repo: PushTokenRepository,
) {
    suspend operator fun invoke(): AppResult<Unit> {
        val token = provider.currentToken() ?: return AppResult.Success(Unit)
        return repo.unregister(token)
    }
}

class SyncDeviceTokenUseCase(
    private val repo: PushTokenRepository,
) {
    suspend operator fun invoke(token: DeviceToken): AppResult<Unit> = repo.register(token)
}
