package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.User
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class UserRepo(
    private val client: HttpClient
) {
    suspend fun getLoggedInUser(): User? {
        return client.get("me").body<User?>()
    }

    suspend fun getUser(id: String): User? {
        return client.get("user") {
            parameter("id", id)
        }.body<User?>()
    }

    suspend fun getUserFriends(id: String): List<User> {
        return emptyList()
    }

    suspend fun logout(refreshTokens: String) {
        client.post("me/logout") {
            setBody(refreshTokens)
        }
    }
}