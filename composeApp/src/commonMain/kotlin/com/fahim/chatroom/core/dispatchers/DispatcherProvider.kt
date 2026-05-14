package com.fahim.chatroom.core.dispatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default

    // TODO(data-layer): back `io` with Dispatchers.IO via expect/actual once we offload real network/db work.
    override val io: CoroutineDispatcher = Dispatchers.Default
}