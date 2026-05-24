package com.fahim.chatroom.presentation.rooms.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.presentation.common.formatters.formatDate
import com.fahim.chatroom.presentation.designsystem.components.ChevronRightIcon
import com.fahim.chatroom.presentation.designsystem.components.LockBadgeIcon
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.designsystem.theme.Spacing

@Composable
fun RoomRow(
    room: Room,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = ChatTheme.palette
    val cardShape = RoundedCornerShape(22.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.xs + Spacing.xxs)
            .clip(cardShape)
            .background(MaterialTheme.colorScheme.surfaceVariant, cardShape)
            .border(1.dp, palette.bubbleOtherOutline, cardShape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoomAvatar(name = room.name)
        Spacer(Modifier.width(Spacing.md))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = room.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(Spacing.sm))
                LockBadgeIcon(tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(Spacing.xxs))
            Text(
                text = "Private · ${room.createdAt.formatDate()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(Spacing.sm))
        ChevronRightIcon(tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RoomAvatar(name: String) {
    val palette = ChatTheme.palette
    val initials = remember(name) { initialsOf(name) }
    // Tilt the gradient slightly per-room so every avatar feels distinct.
    val seed = remember(name) { (name.hashCode() and 0x7fffffff) % 360 }
    val brush = remember(palette.accentStart, palette.accentEnd, seed) {
        val tint = (seed / 360f)
        Brush.linearGradient(
            colors = listOf(
                palette.accentStart.copy(alpha = 0.95f),
                palette.accentEnd.copy(alpha = 0.85f + 0.1f * tint),
            ),
        )
    }
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(brush),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleSmall,
            color = palette.bubbleOwnOnContent,
        )
    }
}

private fun initialsOf(name: String): String {
    val parts = name.trim().split(' ', '_', '-').filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "·"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}
