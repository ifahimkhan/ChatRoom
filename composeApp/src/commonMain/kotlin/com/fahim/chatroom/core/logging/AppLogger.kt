package com.fahim.chatroom.core.logging

interface AppLogger {
    fun d(tag: String, message: String)
    fun i(tag: String, message: String)
    fun w(tag: String, message: String, throwable: Throwable? = null)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

/**
 * Console logger that scrubs token-like substrings as a last line of defence.
 * Callers must still never pass plaintext message content, access/refresh tokens, or secrets.
 */
class ConsoleAppLogger : AppLogger {
    override fun d(tag: String, message: String) = out("D", tag, message, null)
    override fun i(tag: String, message: String) = out("I", tag, message, null)
    override fun w(tag: String, message: String, throwable: Throwable?) = out("W", tag, message, throwable)
    override fun e(tag: String, message: String, throwable: Throwable?) = out("E", tag, message, throwable)

    private fun out(level: String, tag: String, message: String, throwable: Throwable?) {
        println("[$level/$tag] ${Redaction.scrub(message)}")
        if (throwable != null) println(throwable.stackTraceToString())
    }
}

object Redaction {
    private val jwt = Regex("eyJ[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+")
    private val bearer = Regex("bearer\\s+\\S+", RegexOption.IGNORE_CASE)

    fun scrub(text: String): String = text
        .replace(jwt, "<redacted-jwt>")
        .replace(bearer, "Bearer <redacted>")
}