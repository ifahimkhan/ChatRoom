package com.fahim.chatroom.domain.profile.model

data class Profile(
    val userId: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String?,
)
