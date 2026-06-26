package com.commonground.core.models

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Event(
    val id: String,
    val title: String,
    val description: String?,
    val locationName: String,
    val coordinates: Coordinates,
    val startDate: Instant,
    val endDate: Instant?,
    val isPrivate: Boolean,
    val isPrivatePlace: Boolean,
    val isPaid: Boolean,
    val image: ImageUrl? = null,
    val creator: User
) {
    val duration by lazy { endDate?.let { it - startDate } }
}