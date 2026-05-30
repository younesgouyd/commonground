package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.User
import io.ktor.client.*
import io.ktor.client.request.*

class UserRepo(
    private val client: HttpClient
) {
    suspend fun getUserFriends(id: String): List<User> {
        return emptyList()
    }

    suspend fun logout(refreshTokens: String) {
        client.post("user/logout") {
            setBody(refreshTokens)
        }
    }
}