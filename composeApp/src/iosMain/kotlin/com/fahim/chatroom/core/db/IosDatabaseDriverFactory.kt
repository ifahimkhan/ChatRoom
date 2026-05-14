package com.fahim.chatroom.core.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class IosDatabaseDriverFactory : DatabaseDriverFactory {
    override fun create(): SqlDriver =
        NativeSqliteDriver(ChatDatabase.Schema, DATABASE_NAME)

    private companion object {
        const val DATABASE_NAME = "chatroom.db"
    }
}
