package com.fahim.chatroom.domain.auth.usecase

import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.domain.auth.model.AuthSession
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

class SignInUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AppResult<Unit> =
        repo.signIn(email.trim(), password)
}

class SignUpUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AppResult<Unit> =
        repo.signUp(email.trim(), password)
}

class SignOutUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(): AppResult<Unit> = repo.signOut()
}

class ObserveSessionUseCase(private val repo: AuthRepository) {
    operator fun invoke(): StateFlow<AuthSession?> = repo.session
}