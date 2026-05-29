package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
sealed class LoginResult {
    @Serializable
    data class Success(val tokens: TokenPair): LoginResult()

    @Serializable
    data object InvalidCredentials : LoginResult()
}