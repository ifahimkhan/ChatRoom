package com.fahim.chatroom.presentation.chat.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.fahim.chatroom.presentation.designsystem.components.SendIcon
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import com.fahim.chatroom.presentation.designsystem.theme.rememberAuroraBrush

@Composable
fun MessageInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    canSend: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val palette = ChatTheme.palette
    val pillShape = RoundedCornerShape(percent = 50)
    val sendBrush = rememberAuroraBrush()
    val sendAlpha by animateFloatAsState(if (canSend && enabled) 1f else 0.45f, label = "sendAlpha")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            // Pill text field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(pillShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant, pillShape)
                    .border(1.dp, palette.bubbleOtherOutline, pillShape)
                    .heightIn(min = 48.dp)
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = "Message",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    maxLines = 6,
                    singleLine = false,
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight,
                    ),
                    cursorBrush = SolidColor(palette.accentEnd),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send,
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                    keyboardActions = KeyboardActions(onSend = { if (canSend) onSend() }),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            // Circular gradient send button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(sendBrush)
                    .alpha(sendAlpha)
                    .clickable(enabled = canSend && enabled, onClick = onSend),
                contentAlignment = Alignment.Center,
            ) {
                SendIcon(tint = Color.White, size = 18.dp)
            }
        }
    }
}
