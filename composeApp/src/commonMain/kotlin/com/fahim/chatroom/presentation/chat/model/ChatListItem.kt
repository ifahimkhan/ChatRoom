package com.fahim.chatroom.presentation.chat.model

import com.fahim.chatroom.domain.chat.model.Message
import kotlinx.datetime.LocalDate

sealed interface ChatListItem {
    val key: String

    data class DateHeader(val date: LocalDate) : ChatListItem {
        override val key: String get() = "date-$date"
    }

    data class MessageRow(
        val message: Message,
        val isOwn: Boolean,
    ) : ChatListItem {
        override val key: String get() = "msg-${message.id}"
    }
}
