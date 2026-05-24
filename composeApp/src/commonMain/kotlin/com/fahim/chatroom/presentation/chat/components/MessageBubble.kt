package com.fahim.chatroom.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.fahim.chatroom.domain.chat.model.Message
import com.fahim.chatroom.domain.chat.model.MessageStatus
import com.fahim.chatroom.presentation.common.formatters.formatTime
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import com.fahim.chatroom.presentation.designsystem.theme.rememberAuroraBrush

private val BubbleHorizontalPadding = 14.dp
private val BubbleVerticalPadding = 10.dp

@Composable
fun MessageBubble(
    message: Message,
    isOwn: Boolean,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDeleted = message.deletedAt != null
    val palette = ChatTheme.palette
    val ownBrush = rememberAuroraBrush()

    val shape = if (isOwn) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 6.dp, bottomStart = 20.dp)
    } else {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 6.dp)
    }

    val contentColor = when {
        isDeleted -> MaterialTheme.colorScheme.onSurfaceVariant
        isOwn -> palette.bubbleOwnOnContent
        else -> MaterialTheme.colorScheme.onSurface
    }

    val bubbleModifier = Modifier
        .widthIn(max = 320.dp)
        .clip(shape)
        .let { base ->
            if (isOwn) {
                base.background(ownBrush)
            } else {
                base
                    .background(palette.bubbleOther)
                    .border(1.dp, palette.bubbleOtherOutline, shape)
            }
        }
        .padding(horizontal = BubbleHorizontalPadding, vertical = BubbleVerticalPadding)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xxs + Spacing.xxs),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start,
    ) {
        Column(
            horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 320.dp),
        ) {
            Text(
                text = if (isDeleted) "Message deleted" else message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontStyle = if (isDeleted) FontStyle.Italic else FontStyle.Normal,
                modifier = bubbleModifier,
            )
            Spacer(Modifier.height(Spacing.xxs + Spacing.xxs))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                modifier = Modifier.padding(horizontal = Spacing.xs),
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
