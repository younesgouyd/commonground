package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val items: List<User>,
    val next: Int?,
    val total: Long?
)
