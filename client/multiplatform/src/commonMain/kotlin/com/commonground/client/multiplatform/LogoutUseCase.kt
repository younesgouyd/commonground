package com.commonground.client.multiplatform

import com.commonground.client.multiplatform.data.repositories.AuthRepo
import com.commonground.client.multiplatform.data.repositories.UserRepo

class LogoutUseCase(
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo
) {
    suspend fun execute() {
        val refreshTokens = authRepo.loadTokens()?.refreshToken
        if (refreshTokens != null) {
            userRepo.logout(refreshTokens)
            authRepo.clearTokens()
        }
    }
}