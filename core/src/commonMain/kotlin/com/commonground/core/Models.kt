package com.commonground.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration.Companion.minutes

// TODO: remove unnecessary default values (null, emptyList()... etc). they were made for testing

typealias ImageUrl = String

@Serializable
@JvmInline
value class EventId(val value: String)

@Serializable
@JvmInline
value class UserId(val value: String)

@Serializable
data class Event(
    val id: EventId,
    val title: String,
    val description: String,
    val location: String,
    val date: String,
    val isPrivate: Boolean,
    val durationMinutes: Long,
    val isPaid: Boolean,
    val image: ImageUrl? = null,
    val creators: List<User> = emptyList() // TODO
) {
    @Transient
    val duration = durationMinutes.minutes
}

@Serializable
data class User(
    val id: UserId,
    val username: String,
    val displayName: String?,
    val bio: String? = null,
    val emailAddress: String? = null,
    val profilePic: ImageUrl? = null
)

@Serializable
data class UserEvents(
    val created: List<Event>,
    val going: List<Event>,
    val went: List<Event>
)
