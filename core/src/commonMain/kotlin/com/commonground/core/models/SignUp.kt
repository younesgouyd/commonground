package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
data class SignUp(
    val username: String,
    val password: String
)
