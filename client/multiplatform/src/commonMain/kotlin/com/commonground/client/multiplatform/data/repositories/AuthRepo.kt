package com.commonground.client.multiplatform.data.repositories

import com.commonground.core.User

interface AuthRepo {
    suspend fun login(email: String, password: String): User
    suspend fun signUp(email: String, username: String, password: String): User
    suspend fun logout()
}

