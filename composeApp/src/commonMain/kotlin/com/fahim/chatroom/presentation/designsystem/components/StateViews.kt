package com.fahim.chatroom.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import com.fahim.chatroom.presentation.designsystem.theme.rememberAuroraBrush

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 3.dp,
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
fun EmptyView(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    CenteredMessage(title = title, message = message, modifier = modifier, action = action)
}

@Composable
fun ErrorView(
    message: String,
    onRetry: (() -> Unit)?,
    modifier: Modifier = Modifier,
    title: String = "Something went wrong",
) {
    CenteredMessage(title = title, message = message, modifier = modifier) {
        if (onRetry != null) {
            PrimaryPillButton(label = "Retry", onClick = onRetry)
        }
    }
}

@Composable
private fun CenteredMessage(
    title: String,
    message: String,
    modifier: Modifier,
    action: (@Composable () -> Unit)?,
) {
    val palette = ChatTheme.palette
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, palette.bubbleOtherOutline, CircleShape),
        )
        Spacer(Modifier.height(Spacing.lg))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (action != null) {
            Spacer(Modifier.height(Spacing.lg))
            action()
        }
    }
}

/** Aurora-gradient pill, used as the primary call-to-action across state views. */
@Composable
fun PrimaryPillButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val brush = rememberAuroraBrush()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(brush)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.xl, vertical = Spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = ChatTheme.palette.bubbleOwnOnContent,
        )
    }
}
