package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
sealed class SignUpResult {
    @Serializable
    data class Success(val tokens: TokenPair) : SignUpResult()

    @Serializable
    data class Failure(val errors: List<Error>) : SignUpResult() {
        enum class Error {
            InvalidEmailAddress,
            InvalidUsername,
            InvalidPassword,
            UsernameTaken,
            EmailTaken
        }
    }
}
