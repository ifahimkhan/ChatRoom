package com.fahim.chatroom.data.notifications.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenDto(
    @SerialName("user_id") val userId: String,
    val token: String,
    val platform: String,
    @SerialName("last_seen_at") val lastSeenAt: String,
)
