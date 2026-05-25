package com.fahim.chatroom.domain.notifications.model

data class DeviceToken(
    val token: String,
    val platform: Platform,
) {
    enum class Platform(val wire: String) {
        Android("android"),
        Ios("ios"),
        Web("web"),
    }
}
