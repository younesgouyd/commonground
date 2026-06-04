package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class Events(
    val items: List<Event>,
    val next: Int?
)
