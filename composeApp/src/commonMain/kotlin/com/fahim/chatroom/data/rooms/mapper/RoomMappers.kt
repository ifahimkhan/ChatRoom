package com.fahim.chatroom.data.rooms.mapper

import com.fahim.chatroom.data.rooms.dto.RoomDto
import com.fahim.chatroom.data.rooms.dto.UserLookupDto
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.domain.rooms.model.UserLookup
import kotlinx.datetime.Instant

fun RoomDto.toDomain(): Room = Room(
    id = id,
    name = name,
    createdBy = createdBy,
    createdAt = Instant.parse(createdAt),
)

fun UserLookupDto.toDomain(): UserLookup = UserLookup(
    id = id,
    displayName = displayName,
    avatarUrl = avatarUrl,
)