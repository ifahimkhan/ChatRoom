package com.fahim.chatroom.data.chat.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val id: String,
    @SerialName("room_id") val roomId: String,
    @SerialName("sender_id") val senderId: String? = null,
    val content: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("edited_at") val editedAt: String? = null,
    @SerialName("deleted_at") val deletedAt: String? = null,
)

@Serializable
data class NewMessageDto(
    val id: String,                              // client-generated UUID for optimistic dedupe
    @SerialName("room_id") val roomId: String,
    @SerialName("sender_id") val senderId: String,
    val content: String,
)
