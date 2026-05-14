package com.fahim.chatroom.data.chat.mapper

import com.fahim.chatroom.core.db.MessageEntity
import com.fahim.chatroom.domain.chat.model.Message
import com.fahim.chatroom.domain.chat.model.MessageStatus
import kotlinx.datetime.Instant

fun MessageEntity.toDomain(): Message = Message(
    id = id,
    roomId = room_id,
    senderId = sender_id,
    content = content,
    createdAt = Instant.parse(created_at),
    editedAt = edited_at?.let(Instant::parse),
    deletedAt = deleted_at?.let(Instant::parse),
    status = runCatching { MessageStatus.valueOf(status) }.getOrDefault(MessageStatus.Sent),
)
