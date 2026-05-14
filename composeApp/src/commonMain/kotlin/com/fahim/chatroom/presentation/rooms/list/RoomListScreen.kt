package com.fahim.chatroom.presentation.rooms.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.presentation.designsystem.components.AppScaffold
import com.fahim.chatroom.presentation.designsystem.components.EmptyView
import com.fahim.chatroom.presentation.designsystem.components.ErrorView
import com.fahim.chatroom.presentation.designsystem.components.LoadingView
import com.fahim.chatroom.presentation.designsystem.theme.Spacing
import com.fahim.chatroom.presentation.rooms.list.components.RoomRow
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

    AppScaffold(
        title = "Rooms",
        modifier = modifier,
        actions = { TextButton(onClick = onOpenProfile) { Text("Profile") } },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state.phase) {
                RoomListUiState.Phase.Loading -> LoadingView()

                RoomListUiState.Phase.Empty -> EmptyView(
                    title = "No rooms yet",
                    message = "Create a private room to start chatting.",
                    action = { Button(onClick = onCreateRoom) { Text("Create room") } },
                )

                RoomListUiState.Phase.Error -> ErrorView(
                    message = state.errorMessage ?: "Couldn't load your rooms.",
                    onRetry = { viewModel.refresh() },
                )

                RoomListUiState.Phase.Content -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = state.rooms, key = { it.id }) { room ->
                        RoomRow(room = room, onClick = { onRoomClick(room) })
                    }
                }
            }

            if (state.phase == RoomListUiState.Phase.Content) {
                FloatingActionButton(
                    onClick = onCreateRoom,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(Spacing.lg),
                ) { Text("+") }
            }
        }
    }
}
