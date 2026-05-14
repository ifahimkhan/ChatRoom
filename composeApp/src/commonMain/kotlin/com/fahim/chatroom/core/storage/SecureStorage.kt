package com.fahim.chatroom.core.storage

/**
 * Abstraction for sensitive local values (e.g. the auth session).
 * Platform implementations back this with an OS-secured store (Keystore / Keychain).
 */
interface SecureStorage {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun remove(key: String)
    suspend fun clear()
}