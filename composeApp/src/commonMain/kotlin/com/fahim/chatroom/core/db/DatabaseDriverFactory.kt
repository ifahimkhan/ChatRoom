package com.fahim.chatroom.core.db

import app.cash.sqldelight.db.SqlDriver

/** Platform seam for creating a SQLDelight [SqlDriver] for [ChatDatabase]. */
interface DatabaseDriverFactory {
    fun create(): SqlDriver
}
