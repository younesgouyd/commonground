package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.User
import com.commonground.core.UserId
import io.ktor.client.HttpClient

class UserRepo(
    private val client: HttpClient
) {
    suspend fun getUserFriends(id: UserId): List<User> {
        return emptyList()
    }
}