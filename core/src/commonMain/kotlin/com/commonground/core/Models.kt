package com.commonground.core

import kotlinx.serialization.Serializable
import kotlin.time.Duration

typealias ImageUrl = String

@Serializable
@JvmInline
value class EventId(val value: String)

@Serializable
@JvmInline
value class UserId(val value: String)

data class Event(
    val id: EventId,
    val title: String,
    val description: String,
    val location: String,
    val date: String,
    val isPrivate: Boolean,
    val duration: Duration,
    val isPaid: Boolean,
    val image: ImageUrl? = null
)

data class User(
    val id: UserId,
    val username: String,
    val displayName: String?,
    val bio: String? = null,
    val emailAddress: String? = null,
    val profilePic: ImageUrl? = null
)