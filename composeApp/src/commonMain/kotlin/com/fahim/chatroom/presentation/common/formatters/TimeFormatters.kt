package com.fahim.chatroom.presentation.common.formatters

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Renders an [Instant] as a local ISO date (YYYY-MM-DD) — placeholder until a richer formatter lands. */
fun Instant.formatDate(zone: TimeZone = TimeZone.currentSystemDefault()): String =
    toLocalDateTime(zone).date.toString()

/** Renders an [Instant] as a 24-hour local time (HH:MM). */
fun Instant.formatTime(zone: TimeZone = TimeZone.currentSystemDefault()): String {
    val ldt = toLocalDateTime(zone)
    val hh = ldt.hour.toString().padStart(2, '0')
    val mm = ldt.minute.toString().padStart(2, '0')
    return "$hh:$mm"
}