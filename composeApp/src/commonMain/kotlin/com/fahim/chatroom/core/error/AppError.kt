package com.fahim.chatroom.core.error

/** Domain-level error model. Repositories map transport/RLS failures into these. */
sealed class AppError(message: String? = null, cause: Throwable? = null) : Throwable(message, cause) {
    data object Network : AppError("Network unavailable")
    data object Unauthorized : AppError("Not authenticated")
    data object NotRoomMember : AppError("Not a member of this room")
    data class Backend(val description: String) : AppError(description)
    data class Unknown(val original: Throwable? = null) : AppError(original?.message ?: "Unknown error", original)
}