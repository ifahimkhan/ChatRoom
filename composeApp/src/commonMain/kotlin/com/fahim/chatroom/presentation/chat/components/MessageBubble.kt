package com.fahim.chatroom.presentation.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.fahim.chatroom.domain.chat.model.Message
import com.fahim.chatroom.domain.chat.model.MessageStatus
import com.fahim.chatroom.presentation.common.formatters.formatTime
import com.fahim.chatroom.presentation.designsystem.theme.Spacing

@Composable
fun MessageBubble(
    message: Message,
    isOwn: Boolean,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDeleted = message.deletedAt != null
    val containerColor = if (isOwn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isOwn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (isOwn) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 4.dp, bottomStart = 16.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp)
    }
    val rowArrangement = if (isOwn) Arrangement.End else Arrangement.Start

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        horizontalArrangement = rowArrangement,
    ) {
        Column(
            horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 320.dp),
        ) {
            Surface(color = containerColor, contentColor = contentColor, shape = shape) {
                Text(
                    text = if (isDeleted) "Message deleted" else message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = if (isDeleted) FontStyle.Italic else FontStyle.Normal,
                    modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                )
            }
            Spacer(Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Text(
                    text = message.createdAt.formatTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (isOwn) {
                    MessageStatusIcon(
                        status = message.status,
                        onRetry = if (message.status == MessageStatus.Failed) {
                            { onRetry(message.id) }
                        } else null,
                    )
                }
            }
        }
    }
}
