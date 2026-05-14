package com.fahim.chatroom.domain.chat.model

import kotlinx.datetime.Instant

data class Message(
    val id: String,
    val roomId: String,
    val senderId: String?,
    val content: String,
    val createdAt: Instant,
    val editedAt: Instant? = null,
    val deletedAt: Instant? = null,
    val status: MessageStatus = MessageStatus.Sent,
)

enum class MessageStatus { Sending, Sent, Failed }
