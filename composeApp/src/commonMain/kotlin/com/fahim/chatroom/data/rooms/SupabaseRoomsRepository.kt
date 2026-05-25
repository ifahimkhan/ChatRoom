package com.fahim.chatroom.data.rooms

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.fahim.chatroom.core.db.ChatDatabase
import com.fahim.chatroom.core.dispatchers.DispatcherProvider
import com.fahim.chatroom.core.error.AppError
import com.fahim.chatroom.core.error.AppResult
import com.fahim.chatroom.core.error.appResultOf
import com.fahim.chatroom.data.rooms.dto.NewRoomDto
import com.fahim.chatroom.data.rooms.dto.NewRoomMemberDto
import com.fahim.chatroom.data.rooms.dto.RoomDto
import com.fahim.chatroom.data.rooms.dto.UserLookupDto
import com.fahim.chatroom.data.rooms.mapper.toDomain
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.domain.rooms.model.UserLookup
import com.fahim.chatroom.domain.rooms.repository.RoomsRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseRoomsRepository(
    private val client: SupabaseClient,
    private val authRepo: AuthRepository,
    private val db: ChatDatabase,
    dispatchers: DispatcherProvider,
) : RoomsRepository {

    private val io = dispatchers.io
    private val scope = CoroutineScope(SupervisorJob() + dispatchers.default)

    override val rooms: StateFlow<List<Room>> = db.roomsQueries
        .selectAll()
        .asFlow()
        .mapToList(io)
        .map { rows -> rows.map { it.toDomain() } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        // Wipe local cache on real sign-outs, but not during the initial `null` emission that
        // appears before the persisted session has loaded.
        scope.launch {
            var hasBeenAuthenticated = false
            authRepo.session.collect { s ->
                if (s != null) hasBeenAuthenticated = true
                else if (hasBeenAuthenticated) withContext(io) { db.roomsQueries.deleteAll() }
            }
        }
    }

    override suspend fun refresh(): AppResult<Unit> = withContext(io) {
        appResultOf {
            val uid = authRepo.session.value?.userId ?: throw AppError.Unauthorized
            
            // 1. Get the list of room IDs where this user is a member
            val memberDtos = client.from("room_members")
                .select { filter { eq("user_id", uid) } }
                .decodeList<NewRoomMemberDto>()
            val roomIds = memberDtos.map { it.roomId }
            
            // 2. Fetch details for only those rooms
            val dtos = if (roomIds.isEmpty()) {
                emptyList()
            } else {
                client.from("rooms")
                    .select {
                        filter { isIn("id", roomIds) }
                        order(column = "updated_at", order = Order.DESCENDING)
                    }
                    .decodeList<RoomDto>()
            }
            
            // Upsert each fetched room, then prune locally cached rooms that no longer exist on the
            // server. This avoids wiping the cache when the fetch returns empty due to a transient
            // network/RLS hiccup.
            val serverIds = dtos.map { it.id }.toSet()
            db.transaction {
                dtos.forEach { dto ->
                    db.roomsQueries.upsert(
                        id = dto.id,
                        name = dto.name,
                        created_by = dto.createdBy,
                        created_at = dto.createdAt,
                        updated_at = dto.updatedAt,
                    )
                }
                val local = db.roomsQueries.selectAll().executeAsList()
                local.forEach { row -> if (row.id !in serverIds) db.roomsQueries.deleteById(row.id) }
            }
        }
    }

    override suspend fun createRoom(
        name: String,
        memberUserIds: List<String>,
    ): AppResult<Room> = withContext(io) {
        appResultOf {
            val uid = authRepo.session.value?.userId ?: throw AppError.Unauthorized

            val inserted = client.from("rooms")
                .insert(NewRoomDto(name = name, createdBy = uid)) { select() }
                .decodeSingle<RoomDto>()

            val memberRows = memberUserIds
                .filter { it != uid }                                   // creator added by trigger
                .distinct()
                .map { NewRoomMemberDto(roomId = inserted.id, userId = it) }
            if (memberRows.isNotEmpty()) {
                client.from("room_members").insert(memberRows)
            }

            db.roomsQueries.upsert(
                id = inserted.id,
                name = inserted.name,
                created_by = inserted.createdBy,
                created_at = inserted.createdAt,
                updated_at = inserted.updatedAt,
            )
            inserted.toDomain()
        }
    }

    override suspend fun findUserByEmail(email: String): AppResult<UserLookup?> = withContext(io) {
        appResultOf {
            client.postgrest
                .rpc("find_user_by_email", buildJsonObject { put("p_email", email) })
                .decodeList<UserLookupDto>()
                .firstOrNull()
                ?.toDomain()
        }
    }

    override suspend fun getRoomMembers(roomId: String): AppResult<List<UserLookup>> = withContext(io) {
        appResultOf {
            val memberDtos = client.from("room_members")
                .select { filter { eq("room_id", roomId) } }
                .decodeList<NewRoomMemberDto>()
            val userIds = memberDtos.map { it.userId }
            if (userIds.isEmpty()) {
                emptyList()
            } else {
                val profileDtos = client.from("profiles")
                    .select { filter { isIn("id", userIds) } }
                    .decodeList<UserLookupDto>()
                profileDtos.map { it.toDomain() }
            }
        }
    }

    override suspend fun deleteRoom(roomId: String): AppResult<Unit> = withContext(io) {
        appResultOf {
            client.from("rooms").delete {
                filter {
                    eq("id", roomId)
                }
            }
            db.roomsQueries.deleteById(roomId)
        }
    }

    override suspend fun removeRoomMember(roomId: String, userId: String): AppResult<Unit> = withContext(io) {
        appResultOf {
            client.from("room_members").delete {
                filter {
                    eq("room_id", roomId)
                    eq("user_id", userId)
                }
            }
            Unit
        }
    }

    override suspend fun addRoomMember(roomId: String, userId: String): AppResult<Unit> = withContext(io) {
        appResultOf {
            client.from("room_members").insert(
                NewRoomMemberDto(roomId = roomId, userId = userId)
            )
            Unit
        }
    }
}
