package com.fahim.chatroom.data.rooms.mapper

import com.fahim.chatroom.core.db.RoomEntity
import com.fahim.chatroom.domain.rooms.model.Room
import kotlinx.datetime.Instant

fun RoomEntity.toDomain(): Room = Room(
    id = id,
    name = name,
    createdBy = created_by,
    createdAt = Instant.parse(created_at),
)
