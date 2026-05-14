package com.fahim.chatroom.data.auth

import com.fahim.chatroom.core.dispatchers.DispatcherProvider
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.core.error.appResultOf
import com.fahim.chatroom.domain.auth.model.AuthSession
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SupabaseAuthRepository(
    client: SupabaseClient,
    dispatchers: DispatcherProvider,
) : AuthRepository {
    private val auth = client.auth
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)

    override val session: StateFlow<AuthSession?> = auth.sessionStatus
        .map { it.toAuthSession() }
        .stateIn(scope, SharingStarted.Eagerly, auth.sessionStatus.value.toAuthSession())

    override val isInitializing: StateFlow<Boolean> = auth.sessionStatus
        .map { it is SessionStatus.Initializing }
        .stateIn(scope, SharingStarted.Eagerly, auth.sessionStatus.value is SessionStatus.Initializing)

    override suspend fun signUp(email: String, password: String): AppResult<Unit> = appResultOf {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        Unit
    }

    override suspend fun signIn(email: String, password: String): AppResult<Unit> = appResultOf {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signOut(): AppResult<Unit> = appResultOf {
        auth.signOut()
    }

    private fun SessionStatus.toAuthSession(): AuthSession? =
        (this as? SessionStatus.Authenticated)?.session?.toDomain()

    private fun UserSession.toDomain(): AuthSession? {
        val u = user ?: return null
        return AuthSession(userId = u.id, email = u.email.orEmpty())
    }
}