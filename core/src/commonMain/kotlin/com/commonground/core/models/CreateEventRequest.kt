package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateEventRequest(
    val title: String,
    val description: String? = null,
    val locationName: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val date: String,
    val isPrivate: Boolean = false,
    val durationMinutes: Long,
    val isPaid: Boolean = false
)
