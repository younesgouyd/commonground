package com.commonground.core.models

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class CreateEventRequest(
    val title: String,
    val description: String? = null,
    val locationName: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val date: Instant,
    val isPrivate: Boolean = false,
    val durationMinutes: Long,
    val isPaid: Boolean = false
)
