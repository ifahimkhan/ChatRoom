package com.fahim.chatroom.data.auth

import com.fahim.chatroom.core.storage.SecureStorage
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.serialization.json.Json

/**
 * Persists the Supabase auth session through [SecureStorage] so users stay signed in across launches.
 *
 * NOTE: the platform [SecureStorage] impls are V1 placeholders (SharedPreferences / NSUserDefaults).
 * Harden them with EncryptedSharedPreferences and Keychain Services before shipping to production —
 * the wire format here is JSON containing access/refresh tokens.
 */
class SecureStorageSessionManager(
    private val storage: SecureStorage,
    private val json: Json = DefaultJson,
) : SessionManager {

    override suspend fun saveSession(session: UserSession) {
        storage.putString(KEY, json.encodeToString(UserSession.serializer(), session))
    }

    override suspend fun loadSession(): UserSession? {
        val raw = storage.getString(KEY) ?: return null
        return runCatching { json.decodeFromString(UserSession.serializer(), raw) }.getOrNull()
    }

    override suspend fun deleteSession() {
        storage.remove(KEY)
    }

    private companion object {
        const val KEY = "supabase.session.v1"
        val DefaultJson = Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
        }
    }
}
