package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.models.UpdateProfileRequest
import com.commonground.core.models.User
import com.commonground.core.models.Users
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class UserRepo(
    private val client: HttpClient
) {
    suspend fun getLoggedInUser(): User? {
        return client.get("me").body<User?>()
    }

    suspend fun updateProfile(
        username: String,
        displayName: String?,
        bio: String?
    ) {
        client.patch("me") {
            setBody(
                UpdateProfileRequest(
                    username = username,
                    displayName = displayName,
                    bio = bio
                )
            )
        }
    }

    suspend fun updateProfilePic(image: ByteArray) {
        client.patch("me/profilePic") {
            setBody(
                MultiPartFormDataContent(
                    parts = formData {
                        append(
                            key = "file",
                            value = image,
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                            }
                        )
                    }
                )
            )
        }
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
        return client.get("users/$userId/followers") {
            parameter("pageNumber", pageNumber)
        }.body<Users>()
    }

    suspend fun getUserFollowees(userId: String, pageNumber: Int): Users {
        return client.get("users/$userId/followees") {
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
        return client.get("users/$id").body<User?>()
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