package com.commonground.core.models

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SaveEventRequest(
    val title: String,
    val description: String? = null,
    val locationName: String,
    val coordinates: Coordinates? = null,
    val startDate: Instant,
    val endDate: Instant?,
    val isPrivate: Boolean = false,
    val isPrivatePlace: Boolean = false,
    val isPaid: Boolean = false
)
