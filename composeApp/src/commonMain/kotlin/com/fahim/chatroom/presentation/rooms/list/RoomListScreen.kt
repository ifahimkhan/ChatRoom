package com.fahim.chatroom.presentation.rooms.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
 import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.presentation.common.NotificationPermissionStatus
import com.fahim.chatroom.presentation.common.rememberNotificationPermissionRequester
import com.fahim.chatroom.presentation.designsystem.components.AppScaffold
import com.fahim.chatroom.presentation.designsystem.components.EmptyView
import com.fahim.chatroom.presentation.designsystem.components.ErrorView
import com.fahim.chatroom.presentation.designsystem.components.LoadingView
import com.fahim.chatroom.presentation.designsystem.components.PlusIcon
import com.fahim.chatroom.presentation.designsystem.components.PrimaryPillButton
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import com.fahim.chatroom.presentation.designsystem.theme.rememberAuroraBrush
import com.fahim.chatroom.presentation.rooms.list.components.RoomRow
import kotlinx.datetime.Instant
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RoomListScreen(
    onCreateRoom: () -> Unit,
    onRoomClick: (Room) -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RoomListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    // Refresh whenever the screen resumes — covers app-foreground and back-navigation alike.
    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }
    RoomListContent(
        state = state,
        onCreateRoom = onCreateRoom,
        onRoomClick = onRoomClick,
        onOpenProfile = onOpenProfile,
        onRetry = viewModel::refresh,
        onRefresh = viewModel::refresh,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoomListContent(
    state: RoomListUiState,
    onCreateRoom: () -> Unit,
    onRoomClick: (Room) -> Unit,
    onOpenProfile: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val roomCount = state.rooms.size

    AppScaffold(
        title = "Rooms",
        subtitle = when (state.phase) {
            RoomListUiState.Phase.Content -> if (roomCount == 1) "1 private room" else "$roomCount private rooms"
            else -> "Your private spaces"
        },
        modifier = modifier,
        actions = { ProfileChip(onClick = onOpenProfile) },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                NotificationPermissionBanner()
                Box(modifier = Modifier.fillMaxSize()) {
                    PullToRefreshBox(
                        isRefreshing = state.isLoading && state.phase == RoomListUiState.Phase.Content,
                        onRefresh = onRefresh,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        RoomListBody(state, onCreateRoom, onRoomClick, onRetry)
                    }
                    if (state.phase == RoomListUiState.Phase.Content) {
                        AuroraFab(
                            onClick = onCreateRoom,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(Spacing.lg),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoomListBody(
    state: RoomListUiState,
    onCreateRoom: () -> Unit,
    onRoomClick: (Room) -> Unit,
    onRetry: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (state.phase) {
                RoomListUiState.Phase.Loading -> LoadingView()

                RoomListUiState.Phase.Empty -> EmptyView(
                    title = "No rooms yet",
                    message = "Create a private room to start chatting securely.",
                    action = { PrimaryPillButton(label = "Create room", onClick = onCreateRoom) },
                )

                RoomListUiState.Phase.Error -> ErrorView(
                    message = state.errorMessage ?: "Couldn't load your rooms.",
                    onRetry = onRetry,
                )

                RoomListUiState.Phase.Content -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = Spacing.sm,
                        bottom = Spacing.xxxl + Spacing.xl,
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                ) {
                    items(items = state.rooms, key = { it.id }) { room ->
                        RoomRow(room = room, onClick = { onRoomClick(room) })
                    }
                }
            }
    }
}

@Composable
private fun NotificationPermissionBanner() {
    val requester = rememberNotificationPermissionRequester()
    val status by requester.status
    var dismissed by rememberSaveable { mutableStateOf(false) }
    val visible = !dismissed &&
        (status == NotificationPermissionStatus.NotDetermined ||
            status == NotificationPermissionStatus.Denied)
    if (!visible) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
            .clip(RoundedCornerShape(Spacing.md))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Allow notifications so you don't miss new messages.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = { dismissed = true }) {
            Text("Not now", style = MaterialTheme.typography.labelSmall)
        }
        TextButton(onClick = { requester.request() }) {
            Text("Turn on", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun AuroraFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val brush = rememberAuroraBrush()
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(brush)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        PlusIcon(tint = MaterialTheme.colorScheme.onPrimary, size = 22.dp)
    }
}

@Composable
private fun ProfileChip(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Profile",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun previewRooms(): List<Room> {
    val base = Instant.parse("2026-05-12T10:00:00Z")
    return listOf(
        Room(id = "1", name = "Founders", createdBy = null, createdAt = base),
        Room(id = "2", name = "Design Studio", createdBy = null, createdAt = base),
        Room(id = "3", name = "iOS Crew", createdBy = null, createdAt = base),
        Room(id = "4", name = "Late Night Coding", createdBy = null, createdAt = base),
    )
}

@Preview
@Composable
private fun RoomListScreenContentPreview() {
    ChatTheme {
        RoomListContent(
            state = RoomListUiState(rooms = previewRooms()),
            onCreateRoom = {}, onRoomClick = {}, onOpenProfile = {}, onRetry = {}, onRefresh = {},
        )
    }
}

@Preview
@Composable
private fun RoomListScreenEmptyPreview() {
    ChatTheme {
        RoomListContent(
            state = RoomListUiState(rooms = emptyList(), isLoading = false),
            onCreateRoom = {}, onRoomClick = {}, onOpenProfile = {}, onRetry = {}, onRefresh = {},
        )
    }
}

@Preview
@Composable
private fun RoomListScreenLoadingPreview() {
    ChatTheme {
        RoomListContent(
            state = RoomListUiState(isLoading = true),
            onCreateRoom = {}, onRoomClick = {}, onOpenProfile = {}, onRetry = {}, onRefresh = {},
        )
    }
}

@Preview
@Composable
private fun RoomListScreenErrorPreview() {
    ChatTheme {
        RoomListContent(
            state = RoomListUiState(errorMessage = "Network unreachable. Try again."),
            onCreateRoom = {}, onRoomClick = {}, onOpenProfile = {}, onRetry = {}, onRefresh = {},
        )
    }
}
