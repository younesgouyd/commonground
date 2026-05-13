package com.commonground.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration.Companion.minutes

// TODO: remove unnecessary default values (null, emptyList()... etc). they were made for testing

typealias ImageUrl = String

@Serializable
data class Event(
    val id: String? = null,
    val title: String,
    val description: String,
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

@Serializable
data class User(
    val id: String? = null,
    val username: String,
    val displayName: String?,
    val bio: String? = null,
    val emailAddress: String? = null,
    val profilePic: ImageUrl? = null
)

@Serializable
data class EventCategory(
    val id: String,
    val name: String,
    val description: String? = null,
    val iconKey: String? = null
)

// TODO: implement pagination
@Serializable
data class UserEvents(
    val created: List<Event>,
    val going: List<Event>,
    val went: List<Event>
)

@Serializable
data class EventAttendees(
    val attendees: List<User> // TODO: implement pagination
)
