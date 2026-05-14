package com.fahim.chatroom.core.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseDriverFactory(private val context: Context) : DatabaseDriverFactory {
    override fun create(): SqlDriver =
        AndroidSqliteDriver(ChatDatabase.Schema, context.applicationContext, DATABASE_NAME)

    private companion object {
        const val DATABASE_NAME = "chatroom.db"
    }
}
