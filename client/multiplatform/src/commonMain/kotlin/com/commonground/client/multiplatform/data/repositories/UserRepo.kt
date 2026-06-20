package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.User
import com.commonground.core.models.Users
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class UserRepo(
    private val client: HttpClient
) {
    suspend fun getLoggedInUser(): User? {
        return client.get("me").body<User?>()
    }

    suspend fun getLoggedInUserFollowers(pageNumber: Int): Users {
        return client.get("me/followers") {
            parameter("pageNumber", pageNumber)
        }.body<Users>()
    }

    suspend fun getLoggedInUserFollowees(pageNumber: Int): Users {
        return client.get("me/followees") {
            parameter("pageNumber", pageNumber)
        }.body<Users>()
    }

    suspend fun getUserFollowers(userId: String, pageNumber: Int): Users {
        return client.get("user/$userId/followers") {
            parameter("pageNumber", pageNumber)
        }.body<Users>()
    }

    suspend fun getUserFollowees(userId: String, pageNumber: Int): Users {
        return client.get("user/$userId/followees") {
            parameter("pageNumber", pageNumber)
        }.body<Users>()
    }

    suspend fun followUser(id: String) {
        client.put("me/followees") {
            parameter("userId", id)
        }
    }

    suspend fun unfollowUser(id: String) {
        client.delete("me/followees") {
            parameter("userId", id)
        }
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