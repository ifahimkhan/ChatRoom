package com.fahim.chatroom

import com.fahim.chatroom.core.di.initKoin

/** Entry point for starting DI from the iOS app (called once before the first Compose controller). */
fun initKoinIos() {
    initKoin()
}