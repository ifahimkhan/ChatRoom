package com.fahim.chatroom

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform