package com.fahim.chatroom.data.rooms.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    val id: String,
    val name: String,
    @SerialName("created_by") val createdBy: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
)

@Serializable
data class NewRoomDto(
    val name: String,
    @SerialName("created_by") val createdBy: String,
)

@Serializable
data class NewRoomMemberDto(
    @SerialName("room_id") val roomId: String,
    @SerialName("user_id") val userId: String,
)

@Serializable
data class UserLookupDto(
    val id: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)