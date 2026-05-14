package com.fahim.chatroom.data.chat.mapper

import com.fahim.chatroom.data.chat.dto.MessageDto
import com.fahim.chatroom.domain.chat.model.Message
import com.fahim.chatroom.domain.chat.model.MessageStatus
import kotlinx.datetime.Instant

fun MessageDto.toDomain(): Message = Message(
    id = id,
    roomId = roomId,
    senderId = senderId,
    content = content,
    createdAt = Instant.parse(createdAt),
    editedAt = editedAt?.let(Instant::parse),
    deletedAt = deletedAt?.let(Instant::parse),
    status = MessageStatus.Sent,
)
