package com.fahim.chatroom.data.chat

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.fahim.chatroom.core.db.ChatDatabase
import com.fahim.chatroom.core.dispatchers.DispatcherProvider
import com.fahim.chatroom.core.error.AppError
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.core.error.appResultOf
import com.fahim.chatroom.core.logging.AppLogger
import com.fahim.chatroom.data.chat.dto.MessageDto
import com.fahim.chatroom.data.chat.dto.NewMessageDto
import com.fahim.chatroom.data.chat.mapper.toDomain
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import com.fahim.chatroom.domain.chat.model.Message
import com.fahim.chatroom.domain.chat.model.MessageStatus
import com.fahim.chatroom.domain.chat.repository.MessagesRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalUuidApi::class)
class SupabaseMessagesRepository(
    private val client: SupabaseClient,
    private val authRepo: AuthRepository,
    private val db: ChatDatabase,
    private val logger: AppLogger,
    dispatchers: DispatcherProvider,
) : MessagesRepository {

    private companion object {
        const val PAGE_SIZE = 50
        const val TAG = "ChatRepo"
    }

    private val io = dispatchers.io
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)
    private val flowCache = mutableMapOf<String, StateFlow<List<Message>>>()

    init {
        // Any message left in 'Sending' from a previous run was killed mid-send — surface it as
        // 'Failed' so the user can retry.
        scope.launch { withContext(io) { db.messagesQueries.markSendingAsFailed() } }

        // Wipe the cache only on real sign-outs, not on the initial pre-load `null` emission.
        scope.launch {
            var hasBeenAuthenticated = false
            authRepo.session.collect { s ->
                if (s != null) hasBeenAuthenticated = true
                else if (hasBeenAuthenticated) withContext(io) { db.messagesQueries.deleteAll() }
            }
        }
    }

    override fun messages(roomId: String): StateFlow<List<Message>> =
        flowCache.getOrPut(roomId) {
            db.messagesQueries.selectForRoom(roomId)
                .asFlow()
                .mapToList(io)
                .map { rows ->
                    logger.d(TAG, "DB flow emit: ${rows.size} rows for room=$roomId")
                    rows.mapNotNull { entity ->
                        runCatching { entity.toDomain() }
                            .onFailure { logger.e(TAG, "toDomain fail id=${entity.id}: ${it.message}") }
                            .getOrNull()
                    }
                }
                .stateIn(scope, SharingStarted.Eagerly, emptyList())
        }

    override suspend fun loadInitial(roomId: String): AppResult<Unit> = withContext(io) {
        logger.d(TAG, "loadInitial start roomId=$roomId")
        val result = appResultOf {
            val dtos = client.from("messages").select {
                filter { eq("room_id", roomId) }
                order(column = "created_at", order = Order.DESCENDING)
                limit(PAGE_SIZE.toLong())
            }.decodeList<MessageDto>()
            logger.d(TAG, "loadInitial fetched ${dtos.size} messages")
            db.transaction {
                dtos.forEach { upsertSent(it) }
            }
            val total = db.messagesQueries.selectForRoom(roomId).executeAsList().size
            logger.d(TAG, "loadInitial DB now has $total messages for room")
        }
        if (result is AppResult.Failure) logger.e(TAG, "loadInitial failed: ${result.error.message}", result.error)
        result
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun loadOlder(
        roomId: String,
        before: Instant,
        beforeId: String,
    ): AppResult<Boolean> = withContext(io) {
        appResultOf {
            val dtos = client.from("messages").select {
                filter {
                    eq("room_id", roomId)
                    lt("created_at", before.toString())
                }
                order(column = "created_at", order = Order.DESCENDING)
                limit(PAGE_SIZE.toLong())
            }.decodeList<MessageDto>()
            db.transaction {
                dtos.forEach { upsertSent(it) }
            }
            dtos.size == PAGE_SIZE
        }
    }

    override suspend fun send(roomId: String, content: String): AppResult<Unit> = withContext(io) {
        val uid = authRepo.session.value?.userId
            ?: return@withContext AppResult.Failure(AppError.Unauthorized)
        val localId = Uuid.random().toString()
        val now = Clock.System.now().toString()
        db.messagesQueries.upsert(
            id = localId,
            room_id = roomId,
            sender_id = uid,
            content = content,
            created_at = now,
            edited_at = null,
            deleted_at = null,
            status = MessageStatus.Sending.name,
        )
        insertAndReconcile(localId, content, roomId, uid)
    }

    override suspend fun retrySend(roomId: String, localMessageId: String): AppResult<Unit> = withContext(io) {
        val uid = authRepo.session.value?.userId
            ?: return@withContext AppResult.Failure(AppError.Unauthorized)
        val target = db.messagesQueries.selectForRoom(roomId).executeAsList()
            .firstOrNull { it.id == localMessageId && it.status != MessageStatus.Sent.name }
            ?: return@withContext AppResult.Success(Unit)
        db.messagesQueries.updateStatus(status = MessageStatus.Sending.name, id = localMessageId)
        insertAndReconcile(localMessageId, target.content, roomId, uid)
    }

    override suspend fun streamRoom(roomId: String) {
        logger.i(TAG, "streamRoom start roomId=$roomId")
        val channel = client.channel("room:$roomId") {
            isPrivate = true
        }
        val flow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "messages"
            filter("room_id", FilterOperator.EQ, roomId)
        }
        try {
            coroutineScope {
                val job = launch {
                    flow.collect {
                        logger.d(TAG, "realtime event: ${it::class.simpleName} on room=$roomId")
                        handleRealtime(roomId, it)
                    }
                }
                channel.subscribe(blockUntilSubscribed = true)
                logger.i(TAG, "streamRoom subscribed roomId=$roomId")
                job.join()
            }
        } catch (t: Throwable) {
            logger.e(TAG, "streamRoom error roomId=$roomId", t)
            throw t
        } finally {
            withContext(NonCancellable) {
                runCatching { channel.unsubscribe() }
            }
            logger.i(TAG, "streamRoom ended roomId=$roomId")
        }
    }

    private suspend fun handleRealtime(roomId: String, action: PostgresAction) {
        when (action) {
            is PostgresAction.Insert -> {
                val dto = runCatching { action.decodeRecord<MessageDto>() }.getOrNull() ?: return
                if (dto.roomId != roomId) return
                withContext(io) { upsertSent(dto) }
            }

            is PostgresAction.Update -> {
                val dto = runCatching { action.decodeRecord<MessageDto>() }.getOrNull() ?: return
                if (dto.roomId != roomId) return
                withContext(io) { upsertSent(dto) }
            }

            is PostgresAction.Delete -> {
                val record = runCatching { action.oldRecord }.getOrNull() ?: return
                if (record["room_id"]?.jsonPrimitive?.contentOrNull != roomId) return
                val id = record["id"]?.jsonPrimitive?.contentOrNull ?: return
                withContext(io) { db.messagesQueries.deleteById(id) }
            }

            else -> Unit
        }
    }

    private suspend fun insertAndReconcile(
        localId: String,
        content: String,
        roomId: String,
        uid: String,
    ): AppResult<Unit> {
        val result = appResultOf {
            val inserted = client.from("messages")
                .insert(NewMessageDto(id = localId, roomId = roomId, senderId = uid, content = content)) { select() }
                .decodeSingleOrNull<MessageDto>()
            if (inserted != null) {
                upsertSent(inserted)
            } else {
                db.messagesQueries.updateStatus(status = MessageStatus.Sent.name, id = localId)
            }
        }
        if (result is AppResult.Failure) {
            db.messagesQueries.updateStatus(status = MessageStatus.Failed.name, id = localId)
        }
        return result
    }

    /** Upsert a server-confirmed message; overwrites any optimistic row with the same id. */
    private fun upsertSent(dto: MessageDto) {
        db.messagesQueries.upsert(
            id = dto.id,
            room_id = dto.roomId,
            sender_id = dto.senderId,
            content = dto.content,
            created_at = dto.createdAt,
            edited_at = dto.editedAt,
            deleted_at = dto.deletedAt,
            status = MessageStatus.Sent.name,
        )
    }
}
