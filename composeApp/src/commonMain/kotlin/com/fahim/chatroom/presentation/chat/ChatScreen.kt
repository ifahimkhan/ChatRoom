package com.fahim.chatroom.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fahim.chatroom.domain.chat.model.Message
import com.fahim.chatroom.domain.chat.model.MessageStatus
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.presentation.chat.components.DateSeparator
import com.fahim.chatroom.presentation.chat.components.MessageBubble
import com.fahim.chatroom.presentation.chat.components.MessageInputBar
import com.fahim.chatroom.presentation.chat.model.ChatListItem
import com.fahim.chatroom.presentation.designsystem.components.AppScaffold
import com.fahim.chatroom.presentation.designsystem.components.ChevronDownIcon
import com.fahim.chatroom.presentation.designsystem.components.EmptyView
import com.fahim.chatroom.presentation.designsystem.components.ErrorView
import com.fahim.chatroom.presentation.designsystem.components.LoadingView
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ChatScreen(
    room: Room,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = koinViewModel(
        key = room.id,
        parameters = { parametersOf(room.id) },
    ),
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()

    // Pagination: with reverseLayout=true the "top" of the visible chat is the highest index.
    // Lives in the wrapper so it can read the freshest VM state on each scroll tick.
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .collect { lastIdx ->
                val s = viewModel.state.value
                if (s.items.isNotEmpty() &&
                    lastIdx >= s.items.size - PAGINATION_TRIGGER_OFFSET &&
                    s.hasOlder &&
                    !s.isLoadingOlder
                ) {
                    viewModel.loadOlder()
                }
            }
    }

    ChatContent(
        title = room.name,
        state = state,
        listState = listState,
        onBack = onBack,
        onInputChange = viewModel::onInputChange,
        onSend = viewModel::send,
        onRetryFailed = viewModel::retryFailed,
        onRetryInitial = viewModel::retryInitial,
        modifier = modifier,
    )
}

@Composable
private fun ChatContent(
    title: String,
    state: ChatUiState,
    listState: LazyListState,
    onBack: () -> Unit,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onRetryFailed: (String) -> Unit,
    onRetryInitial: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    // Stick to the latest message when a new one arrives and the user is near the bottom.
    LaunchedEffect(state.items.firstOrNull()?.key) {
        if (state.items.isNotEmpty() && listState.firstVisibleItemIndex <= STICK_TO_BOTTOM_THRESHOLD) {
            listState.animateScrollToItem(0)
        }
    }

    val showScrollDown by remember(listState) {
        derivedStateOf { listState.firstVisibleItemIndex > SCROLL_DOWN_BUTTON_THRESHOLD }
    }

    AppScaffold(
        title = title,
        subtitle = "Private room",
        modifier = modifier,
        onBack = onBack,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (state.phase) {
                    ChatUiState.Phase.Loading -> LoadingView()

                    ChatUiState.Phase.Error -> ErrorView(
                        message = state.errorMessage ?: "Couldn't load messages.",
                        onRetry = onRetryInitial,
                    )

                    ChatUiState.Phase.Empty -> EmptyView(
                        title = "No messages yet",
                        message = "Say hello — your first message lands here.",
                    )

                    ChatUiState.Phase.Content -> LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        reverseLayout = true,
                    ) {
                        items(items = state.items, key = ChatListItem::key) { item ->
                            when (item) {
                                is ChatListItem.DateHeader -> DateSeparator(date = item.date)
                                is ChatListItem.MessageRow -> MessageBubble(
                                    message = item.message,
                                    isOwn = item.isOwn,
                                    onRetry = onRetryFailed,
                                )
                            }
                        }
                        if (state.isLoadingOlder) {
                            item(key = "loading-older") {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.primary,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                if (state.phase == ChatUiState.Phase.Content && showScrollDown) {
                    ScrollToBottomButton(
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = Spacing.lg, bottom = Spacing.md),
                    )
                }
            }

            if (state.phase == ChatUiState.Phase.Empty || state.phase == ChatUiState.Phase.Content) {
                MessageInputBar(
                    value = state.input,
                    onValueChange = onInputChange,
                    onSend = onSend,
                    canSend = state.canSend,
                )
            }
        }
    }
}

@Composable
private fun ScrollToBottomButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = ChatTheme.palette
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, palette.bubbleOtherOutline, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ChevronDownIcon(tint = MaterialTheme.colorScheme.onSurface)
    }
}

private const val PAGINATION_TRIGGER_OFFSET = 4
private const val STICK_TO_BOTTOM_THRESHOLD = 2
private const val SCROLL_DOWN_BUTTON_THRESHOLD = 3

// --- Previews -----------------------------------------------------------------------------

private fun previewMessages(): List<ChatListItem> {
    val roomId = "preview-room"
    val t = Instant.parse("2026-05-15T10:00:00Z")
    // reverseLayout=true → index 0 is the newest, drawn at the bottom of the screen.
    return listOf(
        ChatListItem.MessageRow(
            Message(id = "5", roomId = roomId, senderId = "me", content = "Pushing the final fix now.", createdAt = t, status = MessageStatus.Sending),
            isOwn = true,
        ),
        ChatListItem.MessageRow(
            Message(id = "4", roomId = roomId, senderId = "u1", content = "Perfect, go for it.", createdAt = t, status = MessageStatus.Sent),
            isOwn = false,
        ),
        ChatListItem.MessageRow(
            Message(id = "3", roomId = roomId, senderId = "me", content = "Almost — last commit failing in CI.", createdAt = t, status = MessageStatus.Failed),
            isOwn = true,
        ),
        ChatListItem.MessageRow(
            Message(id = "2", roomId = roomId, senderId = "u1", content = "Ready for the demo?", createdAt = t, status = MessageStatus.Sent),
            isOwn = false,
        ),
        ChatListItem.MessageRow(
            Message(id = "1", roomId = roomId, senderId = "me", content = "Hey 👋", createdAt = t, status = MessageStatus.Sent),
            isOwn = true,
        ),
        ChatListItem.DateHeader(LocalDate(2026, 5, 15)),
    )
}

@Preview
@Composable
private fun ChatScreenContentPreview() {
    ChatTheme {
        ChatContent(
            title = "Founders",
            state = ChatUiState(
                items = previewMessages(),
                isLoadingInitial = false,
                hasOlder = true,
                input = "",
            ),
            listState = rememberLazyListState(),
            onBack = {}, onInputChange = {}, onSend = {},
            onRetryFailed = {}, onRetryInitial = {},
        )
    }
}

@Preview
@Composable
private fun ChatScreenDraftingPreview() {
    ChatTheme {
        ChatContent(
            title = "Founders",
            state = ChatUiState(
                items = previewMessages(),
                isLoadingInitial = false,
                input = "Drafting a reply…",
            ),
            listState = rememberLazyListState(),
            onBack = {}, onInputChange = {}, onSend = {},
            onRetryFailed = {}, onRetryInitial = {},
        )
    }
}

@Preview
@Composable
private fun ChatScreenEmptyPreview() {
    ChatTheme {
        ChatContent(
            title = "Late Night Coding",
            state = ChatUiState(isLoadingInitial = false),
            listState = rememberLazyListState(),
            onBack = {}, onInputChange = {}, onSend = {},
            onRetryFailed = {}, onRetryInitial = {},
        )
    }
}

@Preview
@Composable
private fun ChatScreenLoadingPreview() {
    ChatTheme {
        ChatContent(
            title = "Founders",
            state = ChatUiState(isLoadingInitial = true),
            listState = rememberLazyListState(),
            onBack = {}, onInputChange = {}, onSend = {},
            onRetryFailed = {}, onRetryInitial = {},
        )
    }
}

@Preview
@Composable
private fun ChatScreenErrorPreview() {
    ChatTheme {
        ChatContent(
            title = "Founders",
            state = ChatUiState(isLoadingInitial = false, errorMessage = "Couldn't reach the chat server."),
            listState = rememberLazyListState(),
            onBack = {}, onInputChange = {}, onSend = {},
            onRetryFailed = {}, onRetryInitial = {},
        )
    }
}
