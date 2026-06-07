package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val user: User,
    val events: UserEvents,
    val friendCount: Int,
    val eventCount: Int
)
