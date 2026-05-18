package com.commonground.core.models

import kotlinx.serialization.Serializable

// TODO: implement pagination
@Serializable
data class UserEvents(
    val created: List<Event>,
    val going: List<Event>,
    val went: List<Event>
)