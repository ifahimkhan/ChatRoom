package com.fahim.chatroom.domain.auth.model

/** Minimal authenticated session view exposed to the presentation layer. */
data class AuthSession(
    val userId: String,
    val email: String,
)