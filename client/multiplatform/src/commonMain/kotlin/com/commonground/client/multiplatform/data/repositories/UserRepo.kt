package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.User
import io.ktor.client.*

class UserRepo(
    private val client: HttpClient
) {
    suspend fun getUserFriends(id: String): List<User> {
        return emptyList()
    }
}