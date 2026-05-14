package com.fahim.chatroom.domain.rooms.model

import kotlinx.datetime.Instant

data class Room(
    val id: String,
    val name: String,
    val createdBy: String?,
    val createdAt: Instant,
)

data class UserLookup(
    val id: String,
    val displayName: String,
    val avatarUrl: String?,
)