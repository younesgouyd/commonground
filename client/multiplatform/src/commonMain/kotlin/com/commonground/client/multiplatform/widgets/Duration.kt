package com.commonground.client.multiplatform.widgets

import kotlin.time.Duration

fun Duration?.formatted(): String {
    if (this == null) {
        return "??:??"
    }
    return this.toComponents { hours: Long, minutes: Int, seconds: Int, _: Int ->
        StringBuilder()
            .append(hours.toString().padStart(2, '0'))
            .append(":")
            .append(minutes.toString().padStart(2, '0'))
            .append(":")
            .append(seconds.toString().padStart(2, '0'))
            .toString()
    }
}