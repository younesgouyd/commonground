package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val username: String,
    val displayName: String?,
    val bio: String?
)
