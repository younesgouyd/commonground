package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String? = null,
    val username: String,
    val displayName: String? = null,
    val bio: String? = null,
    val emailAddress: String? = null,
    val profilePic: ImageUrl? = null
)