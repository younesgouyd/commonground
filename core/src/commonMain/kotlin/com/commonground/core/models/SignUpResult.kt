package com.commonground.core.models

import kotlinx.serialization.Serializable

@Serializable
class SignUpResult(
    val errors: List<Error>,
    val token: TokenPair?
) {
    enum class Error {
        InvalidEmailAddress,
        InvalidUsername,
        InvalidPassword,
        UsernameTaken,
        EmailTaken
    }
}
