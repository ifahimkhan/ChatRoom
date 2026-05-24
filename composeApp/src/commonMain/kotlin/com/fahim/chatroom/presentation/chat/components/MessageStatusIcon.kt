package com.fahim.chatroom.presentation.chat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fahim.chatroom.domain.chat.model.MessageStatus
import com.fahim.chatroom.presentation.designsystem.components.AlertDotIcon
import com.fahim.chatroom.presentation.designsystem.components.CheckIcon
import com.fahim.chatroom.presentation.designsystem.theme.Spacing

@Composable
fun MessageStatusIcon(
    status: MessageStatus,
    onRetry: (() -> Unit)?,
) {
    when (status) {
        MessageStatus.Sending -> Text(
            text = "···",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.alpha(0.65f),
        )

        MessageStatus.Sent -> CheckIcon(
            modifier = Modifier.size(12.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        MessageStatus.Failed -> Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 50))
                .let { m -> if (onRetry != null) m.clickable(onClick = onRetry) else m }
                .padding(horizontal = Spacing.xs, vertical = 2.dp),
        ) {
            AlertDotIcon(tint = MaterialTheme.colorScheme.error)
            Text(
                text = "Tap to retry",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
