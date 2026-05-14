package com.fahim.chatroom.presentation.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.presentation.chat.components.DateSeparator
import com.fahim.chatroom.presentation.chat.components.MessageBubble
import com.fahim.chatroom.presentation.chat.components.MessageInputBar
import com.fahim.chatroom.presentation.chat.model.ChatListItem
import com.fahim.chatroom.presentation.designsystem.components.AppScaffold
import com.fahim.chatroom.presentation.designsystem.components.EmptyView
import com.fahim.chatroom.presentation.designsystem.components.ErrorView
import com.fahim.chatroom.presentation.designsystem.components.LoadingView
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
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

    // Stick to the latest message (index 0 in reverseLayout) when a new one arrives and the
    // user is already near the bottom of the chat.
    LaunchedEffect(state.items.firstOrNull()?.key) {
        if (state.items.isNotEmpty() && listState.firstVisibleItemIndex <= STICK_TO_BOTTOM_THRESHOLD) {
            listState.animateScrollToItem(0)
        }
    }

    AppScaffold(
        title = room.name,
        modifier = modifier,
        onBack = onBack,
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when (state.phase) {
                    ChatUiState.Phase.Loading -> LoadingView()

                    ChatUiState.Phase.Error -> ErrorView(
                        message = state.errorMessage ?: "Couldn't load messages.",
                        onRetry = viewModel::retryInitial,
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
                                    onRetry = viewModel::retryFailed,
                                )
                            }
                        }
                        if (state.isLoadingOlder) {
                            item(key = "loading-older") {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                                    contentAlignment = Alignment.Center,
                                ) { CircularProgressIndicator() }
                            }
                        }
                    }
                }
            }

            if (state.phase == ChatUiState.Phase.Empty || state.phase == ChatUiState.Phase.Content) {
                MessageInputBar(
                    value = state.input,
                    onValueChange = viewModel::onInputChange,
                    onSend = viewModel::send,
                    canSend = state.canSend,
                )
            }
        }
    }
}

private const val PAGINATION_TRIGGER_OFFSET = 4
private const val STICK_TO_BOTTOM_THRESHOLD = 2
