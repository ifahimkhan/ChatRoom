package com.fahim.chatroom.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

@Composable
fun DateSeparator(date: LocalDate, modifier: Modifier = Modifier) {
    val label = remember(date) { dateLabel(date) }
    val palette = ChatTheme.palette
    val chipShape = RoundedCornerShape(percent = 50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .clip(chipShape)
                .background(MaterialTheme.colorScheme.surfaceVariant, chipShape)
                .border(1.dp, palette.bubbleOtherOutline, chipShape)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs + Spacing.xxs),
        )
    }
}

private fun dateLabel(date: LocalDate): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val yesterday = today.minus(1, DateTimeUnit.DAY)
    return when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> date.toString()
    }
}
