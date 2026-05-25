package com.fahim.chatroom.data.notifications

import com.fahim.chatroom.core.dispatchers.DispatcherProvider
import com.fahim.chatroom.core.error.AppError
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.core.error.appResultOf
import com.fahim.chatroom.core.logging.AppLogger
import com.fahim.chatroom.data.notifications.dto.DeviceTokenDto
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import com.fahim.chatroom.domain.notifications.model.DeviceToken
import com.fahim.chatroom.domain.notifications.repository.PushTokenRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class SupabasePushTokenRepository(
    private val client: SupabaseClient,
    private val authRepo: AuthRepository,
    private val logger: AppLogger,
    dispatchers: DispatcherProvider,
) : PushTokenRepository {

    private companion object { const val TAG = "PushTokens" }

    private val io = dispatchers.io

    override suspend fun register(token: DeviceToken): AppResult<Unit> = withContext(io) {
        val uid = authRepo.session.value?.userId
            ?: return@withContext AppResult.Failure(AppError.Unauthorized)
        val dto = DeviceTokenDto(
            userId = uid,
            token = token.token,
            platform = token.platform.wire,
            lastSeenAt = Clock.System.now().toString(),
        )
        val result: AppResult<Unit> = appResultOf {
            client.from("device_tokens").upsert(dto) { onConflict = "token" }
            Unit
        }
        if (result is AppResult.Failure) {
            logger.w(TAG, "register failed token=${token.token.take(8)}…: ${result.error.message}")
        } else {
            logger.i(TAG, "register ok token=${token.token.take(8)}…")
        }
        result
    }

    override suspend fun unregister(token: DeviceToken): AppResult<Unit> = withContext(io) {
        val uid = authRepo.session.value?.userId
            ?: return@withContext AppResult.Failure(AppError.Unauthorized)
        val result: AppResult<Unit> = appResultOf {
            client.from("device_tokens").delete {
                filter {
                    eq("token", token.token)
                    eq("user_id", uid)
                }
            }
            Unit
        }
        if (result is AppResult.Failure) {
            logger.w(TAG, "unregister failed token=${token.token.take(8)}…: ${result.error.message}")
        } else {
            logger.i(TAG, "unregister ok token=${token.token.take(8)}…")
        }
        result
    }
}
