package com.commonground.client.multiplatform.ui

import com.commonground.client.multiplatform.data.RepoStore
import kotlinx.datetime.*
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlin.time.Instant

val defaultLocalTimeFormat by lazy {
    LocalTime.Format {
        hour()
        char(':')
        minute()
    }
}

fun Instant.formatted(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val localDateTime = this.toLocalDateTime(timeZone)
    val eventCardFormat = LocalDateTime.Format {
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

fun LocalDate.formatted(): String {
    return this.format(
        LocalDate.Format {
            day()
            char(' ')
            monthName(MonthNames.ENGLISH_ABBREVIATED)
            char(' ')
            year()
        }
    )
}

fun LocalTime.formatted(): String {
    return this.format(defaultLocalTimeFormat)
}

fun String.toBackendUrl() = "http://${RepoStore.serverHost}:${RepoStore.SERVER_PORT}$this"