package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class EventAttendees(
    val attendees: List<User> // TODO: implement pagination
)