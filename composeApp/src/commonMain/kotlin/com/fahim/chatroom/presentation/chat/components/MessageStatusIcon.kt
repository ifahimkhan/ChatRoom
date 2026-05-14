package com.fahim.chatroom.presentation.chat.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.fahim.chatroom.domain.chat.model.MessageStatus

@Composable
fun MessageStatusIcon(
    status: MessageStatus,
    onRetry: (() -> Unit)?,
) {
    when (status) {
        MessageStatus.Sending -> Text(
            text = "…",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        MessageStatus.Sent -> Text(
            text = "✓",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        MessageStatus.Failed -> TextButton(
            onClick = { onRetry?.invoke() },
            enabled = onRetry != null,
        ) {
            Text(
                text = "Failed · Retry",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
