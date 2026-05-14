package com.fahim.chatroom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.presentation.auth.AuthScreen
import com.fahim.chatroom.presentation.chat.ChatScreen
import com.fahim.chatroom.presentation.designsystem.components.LoadingView
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.profile.ProfileScreen
import com.fahim.chatroom.presentation.rooms.create.CreateRoomScreen
import com.fahim.chatroom.presentation.rooms.list.RoomListScreen
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

private sealed interface AppRoute {
    data object RoomList : AppRoute
    data object CreateRoom : AppRoute
    data object Profile : AppRoute
    data class Chat(val room: Room) : AppRoute
}

@Composable
@Preview
fun App() {
    ChatTheme {
        KoinContext {
            val authRepo: AuthRepository = koinInject()
            val session by authRepo.session.collectAsState()
            val isInitializing by authRepo.isInitializing.collectAsState()
            val scope = rememberCoroutineScope()

            val currentSession = session
            if (currentSession == null) {
                if (isInitializing) LoadingView() else AuthScreen()
                return@KoinContext
            }

            var route: AppRoute by remember { mutableStateOf(AppRoute.RoomList) }

            // Drop in-flight nav state on sign-out so the next sign-in starts at the list.
            LaunchedEffect(currentSession.userId) { route = AppRoute.RoomList }

            when (val r = route) {
                AppRoute.RoomList -> RoomListScreen(
                    onCreateRoom = { route = AppRoute.CreateRoom },
                    onRoomClick = { room -> route = AppRoute.Chat(room) },
                    onOpenProfile = { route = AppRoute.Profile },
                )

                AppRoute.CreateRoom -> CreateRoomScreen(
                    onClose = { route = AppRoute.RoomList },
                    onCreated = { route = AppRoute.RoomList },
                )

                AppRoute.Profile -> ProfileScreen(
                    onBack = { route = AppRoute.RoomList },
                    onSignOut = { scope.launch { authRepo.signOut() } },
                )

                is AppRoute.Chat -> ChatScreen(
                    room = r.room,
                    onBack = { route = AppRoute.RoomList },
                )
            }
        }
    }
}
