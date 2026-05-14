package com.fahim.chatroom.data.profile

import com.fahim.chatroom.core.dispatchers.DispatcherProvider
import com.fahim.chatroom.core.error.AppError
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.core.error.appResultOf
import com.fahim.chatroom.data.profile.dto.ProfileDto
import com.fahim.chatroom.data.profile.mapper.toDomain
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import com.fahim.chatroom.domain.profile.model.Profile
import com.fahim.chatroom.domain.profile.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SupabaseProfileRepository(
    private val client: SupabaseClient,
    private val authRepo: AuthRepository,
    dispatchers: DispatcherProvider,
) : ProfileRepository {

    private val io = dispatchers.io
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)

    private val _profile = MutableStateFlow<Profile?>(null)
    override val profile: StateFlow<Profile?> = _profile.asStateFlow()

    init {
        scope.launch {
            authRepo.session.collect { s ->
                if (s == null) {
                    _profile.value = null
                } else {
                    withContext(io) { fetch(s.userId) }
                }
            }
        }
    }

    override suspend fun refresh(): AppResult<Unit> = withContext(io) {
        appResultOf {
            val uid = authRepo.session.value?.userId ?: throw AppError.Unauthorized
            fetch(uid)
        }
    }

    override suspend fun updateDisplayName(newName: String): AppResult<Unit> = withContext(io) {
        appResultOf {
            val uid = authRepo.session.value?.userId ?: throw AppError.Unauthorized
            val updated = client.from("profiles").update({
                set("display_name", newName)
            }) {
                filter { eq("id", uid) }
                select()
            }.decodeSingle<ProfileDto>()
            _profile.value = updated.toDomain()
        }
    }

    private suspend fun fetch(uid: String) {
        val dto = client.from("profiles")
            .select { filter { eq("id", uid) } }
            .decodeSingleOrNull<ProfileDto>()
        _profile.value = dto?.toDomain()
    }
}
