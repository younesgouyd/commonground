package com.commonground.core.models

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ChatMessage(
    val id: String,
    val eventId: String,
    val sender: User,
    val content: String,
    val createdAt: Instant
)
