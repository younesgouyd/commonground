package com.commonground.client.multiplatform

import kotlin.time.Duration

data class Event(
    val id: Long,
    val title: String,
    val description: String,
    val location: String,
    val date: String,
    val isPrivate: Boolean,
    val duration: Duration,
    val isPaid: Boolean
)