package com.fahim.chatroom.data.profile.mapper

import com.fahim.chatroom.data.profile.dto.ProfileDto
import com.fahim.chatroom.domain.profile.model.Profile

fun ProfileDto.toDomain(): Profile = Profile(
    userId = id,
    email = email.orEmpty(),
    displayName = displayName,
    avatarUrl = avatarUrl,
)
