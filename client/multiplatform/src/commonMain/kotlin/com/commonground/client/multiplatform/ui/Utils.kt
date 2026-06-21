package com.commonground.client.multiplatform.ui

import com.commonground.client.multiplatform.data.RepoStore
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Instant.formatted(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val localDateTime = this.toLocalDateTime(timeZone)
    val eventCardFormat = kotlinx.datetime.LocalDateTime.Format {
        day()
        char(' ')
        monthName(MonthNames.ENGLISH_ABBREVIATED)
        char(' ')
        year()
        chars(", ")
        hour()
        char(':')
        minute()
    }
    return eventCardFormat.format(localDateTime)
}

fun String.toBackendUrl() = "http://${RepoStore.serverHost}:${RepoStore.SERVER_PORT}$this"