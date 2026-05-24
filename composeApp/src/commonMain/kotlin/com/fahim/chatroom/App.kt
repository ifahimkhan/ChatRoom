package com.fahim.chatroom

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.fahim.chatroom.core.navigation.DeepLinkBus
import com.fahim.chatroom.domain.auth.repository.AuthRepository
import com.fahim.chatroom.domain.auth.usecase.SignOutUseCase
import com.fahim.chatroom.domain.notifications.usecase.RegisterDeviceTokenUseCase
import com.fahim.chatroom.domain.rooms.model.Room
import com.fahim.chatroom.domain.rooms.repository.RoomsRepository
import com.fahim.chatroom.presentation.auth.AuthScreen
import com.fahim.chatroom.presentation.chat.ChatScreen
import com.fahim.chatroom.presentation.common.PlatformBackHandler
import com.fahim.chatroom.presentation.common.platformSwipeBack
import com.fahim.chatroom.presentation.designsystem.components.LoadingView
import com.fahim.chatroom.presentation.designsystem.theme.ChatTheme
import com.fahim.chatroom.presentation.profile.ProfileScreen
import com.fahim.chatroom.presentation.rooms.create.CreateRoomScreen
import com.fahim.chatroom.presentation.rooms.list.RoomListScreen
import kotlinx.coroutines.flow.first
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
            val roomsRepo: RoomsRepository = koinInject()
            val signOutUseCase: SignOutUseCase = koinInject()
            val registerDeviceToken: RegisterDeviceTokenUseCase = koinInject()
            val deepLinkBus: DeepLinkBus = koinInject()
            val session by authRepo.session.collectAsState()
            val isInitializing by authRepo.isInitializing.collectAsState()
            val scope = rememberCoroutineScope()

            val currentSession = session
            if (currentSession == null) {
                if (isInitializing) LoadingView() else AuthScreen()
                return@KoinContext
            }

            var route: AppRoute by remember { mutableStateOf(AppRoute.RoomList) }

            LaunchedEffect(currentSession.userId) { route = AppRoute.RoomList }

            LaunchedEffect(currentSession.userId) { registerDeviceToken() }

            LaunchedEffect(currentSession.userId) {
                deepLinkBus.pendingRoomId.collect { roomId ->
                    val cached = roomsRepo.rooms.value.firstOrNull { it.id == roomId }
                    val room = cached ?: run {
                        // Room not in local cache (cold start). Refresh and try again from the new emission.
                        roomsRepo.refresh()
                        roomsRepo.rooms.first { list -> list.any { it.id == roomId } || list.isNotEmpty() }
                            .firstOrNull { it.id == roomId }
                    }
                    if (room != null) route = AppRoute.Chat(room)
                }
            }

            PlatformBackHandler(enabled = route != AppRoute.RoomList) { route = AppRoute.RoomList }

            val backToRoot: () -> Unit = { route = AppRoute.RoomList }
            val swipeBack = Modifier.platformSwipeBack(
                enabled = route != AppRoute.RoomList,
                onBack = backToRoot,
            )

            when (val r = route) {
                AppRoute.RoomList -> RoomListScreen(
                    onCreateRoom = { route = AppRoute.CreateRoom },
                    onRoomClick = { room -> route = AppRoute.Chat(room) },
                    onOpenProfile = { route = AppRoute.Profile },
                )

                AppRoute.CreateRoom -> CreateRoomScreen(
                    onClose = backToRoot,
                    onCreated = { route = AppRoute.RoomList },
                    modifier = swipeBack,
                )

                AppRoute.Profile -> ProfileScreen(
                    onBack = backToRoot,
                    onSignOut = { scope.launch { signOutUseCase() } },
                    modifier = swipeBack,
                )

                is AppRoute.Chat -> ChatScreen(
                    room = r.room,
                    onBack = backToRoot,
                    modifier = swipeBack,
                )
            }
        }
    }
}
