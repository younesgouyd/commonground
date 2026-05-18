package com.commonground.core.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration.Companion.minutes

@Serializable
data class Event(
    val id: String? = null,
    val title: String,
    val description: String?,
    val location: String,
    val date: String,
    val isPrivate: Boolean,
    val durationMinutes: Long,
    val isPaid: Boolean,
    val image: ImageUrl? = null,
    val creator: User
) {
    @Transient
    val duration = durationMinutes.minutes
}