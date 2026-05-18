package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class EventCategory(
    val id: String,
    val name: String,
    val description: String? = null,
    val iconKey: String? = null
)