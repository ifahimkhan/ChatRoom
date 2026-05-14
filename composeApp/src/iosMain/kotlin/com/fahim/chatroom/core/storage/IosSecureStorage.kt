package com.fahim.chatroom.core.storage

import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

/**
 * TODO(security): back this with Keychain Services before storing the auth session or other secrets.
 * NSUserDefaults is a Phase-1 placeholder.
 */
class IosSecureStorage : SecureStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override suspend fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    override suspend fun getString(key: String): String? = defaults.stringForKey(key)

    override suspend fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }

    override suspend fun clear() {
        NSBundle.mainBundle.bundleIdentifier?.let { defaults.removePersistentDomainForName(it) }
    }
}